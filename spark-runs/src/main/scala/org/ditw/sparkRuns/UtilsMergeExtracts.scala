package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.textSeg.output.SegGN

object UtilsMergeExtracts {

  def main(args:Array[String]):Unit = {
    val spark = SparkUtils.sparkContextLocal()
    val existing = spark.textFile("/media/sf_vmshare/pmjs/curated")
      .map(SegGN.fromJson)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val segCount = existing.count
    val segGnCount = existing.map(_.affGns.size).sum().toInt
    println(s"Existing: $segCount/$segGnCount")

    spark.stop()
  }
}
