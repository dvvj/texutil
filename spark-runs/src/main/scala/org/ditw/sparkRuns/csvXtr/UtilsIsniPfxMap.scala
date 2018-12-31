package org.ditw.sparkRuns.csvXtr
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.sparkRuns.CommonUtils
import org.ditw.sparkRuns.CommonUtils.csvRead

object UtilsIsniPfxMap {

  import IsniSchema._

  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()

    val allNamesLower = csvRead(
        spSess,
        "/media/sf_vmshare/ringgold_isni.csv",
        csvMeta
      ).rdd
      .map { r =>
        val x = csvMeta.strVal(r, ColName)
        if (x != null && !x.isEmpty) x.toLowerCase()
        else "________________________________NA"
      }
      .sortBy(x => x)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val brAllNames = spSess.sparkContext.broadcast(allNamesLower.collect())

    val pfxMap = allNamesLower.map { n =>
      var found = false
      var idx = 0
      val allNames = brAllNames.value
      while (!found && idx < allNames.length) {
        val curr = allNames(idx)
        if (curr.length < n.length && n.startsWith(curr)) {
          found = true
        }
        else idx += 1
      }
      if (found)
        n -> Option(allNames(idx))
      else
        n -> None
    }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val noPfx = pfxMap.filter(_._2.isEmpty).map(_._1).sortBy(n => n)
    CommonUtils.writeStr(
      "/media/sf_vmshare/isni_nopfx.txt",
      noPfx.collect()
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
    CommonUtils.writeStr(
      "/media/sf_vmshare/isni_pfx.txt",
      pfxOutput
    )

    spSess.stop()
  }

}
