package org.ditw.sparkRuns.pmXtr
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.sparkRuns.pmXtr.PmXtrUtils.{processSingleSegs, segmentInput}

object XtrCntryTests extends App {

  import org.ditw.sparkRuns.TestHelpers._
  val spark = SparkUtils.sparkContextLocal()
  val gnmmgr = testGNMmgr(spark)
  val brGNMmgr = spark.broadcast(gnmmgr)

  val allSegs = segmentInput(spark, "file:///media/sf_vmshare/pmjs/dbg/")
    .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
  val singleSegs = allSegs.filter(_._3.length == 1)

  val multiSegs = allSegs.filter(_._3.length != 1)

  val brCcs = spark.broadcast(_ccs)

  val (foundRes, emptyRes) = processSingleSegs(singleSegs, brGNMmgr, brCcs)


  println("-------------- Found")
  foundRes.foreach(println)

  println("-------------- Empty")
  emptyRes.foreach(println)
  spark.stop()
}
