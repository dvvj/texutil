package org.ditw.sparkRuns.csvXtr.isniPrePrc
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.storage.StorageLevel
import org.ditw.common.{ResourceHelpers, SparkUtils}
import org.ditw.sparkRuns.CommonUtils
import org.ditw.sparkRuns.CommonUtils.csvRead
import org.ditw.sparkRuns.csvXtr.IsniSchema

object UtilsIsniRel {
  import IsniSchema._

//  private val errataMap = Map(
//    "San Franciso" -> "San Francisco",
//    "San Fransisco" -> "San Francisco"
//  )

  private val pfxIgnoreSet = ResourceHelpers.loadStrs("/isni-rel/pfx_ignore.txt").toSet

  private def proc(
    spark:SparkContext,
    rdd:RDD[Row],
    filter:Row => Boolean,
    outPath:String
  ):Unit = {
    val _allNamesLower = rdd
      .filter(filter)
      .map { r =>
        val x = csvMeta.strVal(r, ColName)
        val name =
          if (x != null && !x.isEmpty) x.toLowerCase()
          else "________________________________NA"
        val isni = csvMeta.strVal(r, ColISNI)
        name -> isni
      }
      .groupByKey()
      .sortBy(p => p._1, false)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val allNamesNonUnique = _allNamesLower.filter(_._2.size != 1).collect().toList.mkString("\n\t", "\n\t", "")
    println(s"non unique names: $allNamesNonUnique")

    val allNamesLower = _allNamesLower.filter(_._2.size == 1)
      .mapValues(_.head)

    val brAllNames = spark.broadcast(allNamesLower.collect())
    val brPfxIgnoreSet = spark.broadcast(pfxIgnoreSet)
    val pfxIsniMap = allNamesLower.map { p =>
      val (n, isni) = p
      var found = false
      var idx = 0
      val allNames = brAllNames.value
      while (!found && idx < allNames.length) {
        val (curr, currIsni) = allNames(idx)
        if (!brPfxIgnoreSet.value.contains(curr) &&
          curr.length < n.length && n.startsWith(curr) &&
          (n(curr.length).isSpaceChar || n(curr.length) == '-')) {
          found = true
        }
        else idx += 1
      }
      if (found)
        p -> Option(allNames(idx))
      else
        p -> None

    }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val noPfx = pfxIsniMap
      .filter(_._2.isEmpty)
      .map(_._1)
      .sortBy(n => n)
      .collect()
    println(s"No Prefix #: ${noPfx.length}")
    CommonUtils.writeStr(
      "/media/sf_vmshare/isni_nopfx.txt",
      noPfx.map(_.toString())
    )

    val hasPfx = pfxIsniMap.filter(_._2.nonEmpty)
    val _pfxOutput = hasPfx.map { p =>
      p._2.get -> p._1
    }.groupByKey()
      .mapValues(strs => strs.toArray.sorted)
      .sortBy(_._1)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val childParents = _pfxOutput.map { p =>
      IsniRel(
        IsniRelUnit(p._1._2, p._1._1),
        p._2.map(
          pp => IsniRelUnit(pp._2, "") // IsniRelUnit(pp._2, pp._1)
        )
      )
    }.sortBy(_.parent.n).map(IsniRel.toJson).collect()
    CommonUtils.writeStr(
      "/media/sf_vmshare/isni_rel.json",
      childParents
    )

    val pfxOutput = _pfxOutput.map { p =>
      val (pfx, strs) = p
      strs.mkString(
        s"$pfx\n\t", "\n\t", ""
      )
    }.collect()
    println(s"W/ Prefix #: ${pfxOutput.length}")
    CommonUtils.writeStr(
      outPath,
      pfxOutput
    )
  }

  private val filter1:Row => Boolean = r => {
    csvMeta.strVal(r, ColCountryCode) == "US" &&
      csvMeta.strVal(r, ColName).toLowerCase().contains("university of california")
  }

  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()

    val rdd = csvRead(
      spSess,
      "/media/sf_vmshare/ringgold_isni.csv",
      csvMeta
    ).rdd.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    proc(
      spSess.sparkContext, rdd,
      filter1,
      "/media/sf_vmshare/us_univ.txt"
    )

    spSess.stop()
  }
}
