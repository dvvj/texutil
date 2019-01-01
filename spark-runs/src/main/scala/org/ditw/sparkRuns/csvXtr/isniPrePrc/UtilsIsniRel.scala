package org.ditw.sparkRuns.csvXtr.isniPrePrc
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.sparkRuns.CommonUtils
import org.ditw.sparkRuns.CommonUtils.csvRead
import org.ditw.sparkRuns.csvXtr.IsniSchema

object UtilsIsniRel {
  import IsniSchema._

  private val errataMap = Map(
    "San Franciso" -> "San Francisco",
    "San Fransisco" -> "San Francisco"
  )

  private def proc(
    spark:SparkContext,
    rdd:RDD[Row],
    filter:Row => Boolean,
    outPath:String
  ):Unit = {
    val allNamesLower = rdd
      .filter(filter)
      .map { r =>
        val x = csvMeta.strVal(r, ColName, errataMap)
        if (x != null && !x.isEmpty) x.toLowerCase()
        else "________________________________NA"
      }
      .sortBy(x => x, false)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
    val brAllNames = spark.broadcast(allNamesLower.collect())

    val pfxMap = allNamesLower.map { n =>
      var found = false
      var idx = 0
      val allNames = brAllNames.value
      while (!found && idx < allNames.length) {
        val curr = allNames(idx)
        if (curr.length < n.length && n.startsWith(curr) &&
          (n(curr.length).isSpaceChar || n(curr.length) == '-')) {
          found = true
        }
        else idx += 1
      }
      if (found)
        n -> Option(allNames(idx))
      else
        n -> None
    }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val noPfx = pfxMap
      .filter(_._2.isEmpty)
      .map(_._1)
      .sortBy(n => n)
      .collect()
    println(s"No Prefix #: ${noPfx.length}")
    CommonUtils.writeStr(
      "/media/sf_vmshare/isni_nopfx.txt",
      noPfx
    )

    val hasPfx = pfxMap.filter(_._2.nonEmpty)
    val pfxOutput = hasPfx.map { p =>
      p._2.get -> p._1
    }.groupByKey()
      .mapValues(strs => strs.toArray.sorted)
      .sortBy(_._1)
      .map { p =>
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
      csvMeta.strVal(r, ColName, errataMap).toLowerCase().contains("university of california")
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
