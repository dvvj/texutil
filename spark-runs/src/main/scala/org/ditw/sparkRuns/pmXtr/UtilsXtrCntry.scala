package org.ditw.sparkRuns.pmXtr
import org.apache.spark.storage.StorageLevel
import org.ditw.common.GenUtils.printlnT0
import org.ditw.common.{ResourceHelpers, SparkUtils, TkRange}
import org.ditw.demo1.gndata.GNCntry.{PR, US}
import org.ditw.demo1.gndata.GNEnt
import org.ditw.demo1.gndata.SrcData.tabSplitter
import org.ditw.exutil1.naen.NaEnData.NaEnCat._
import org.ditw.exutil1.naen.TagHelper
import org.ditw.exutil1.naen.TagHelper.builtinTag
import org.ditw.matcher.MatchPool
import org.ditw.pmxml.model.AAAuAff
import org.ditw.sparkRuns.CommonUtils
import org.ditw.sparkRuns.CommonUtils.loadGNMmgr
import org.ditw.sparkRuns.csvXtr.{EntXtrUtils, IsniEnAlias}
import org.ditw.textSeg.common.Tags.TagGroup4Univ
import org.ditw.textSeg.output.{AffGN, SegGN}

import scala.collection.mutable.ListBuffer

object UtilsXtrCntry {


  def main(args:Array[String]):Unit = {
    val runLocally = if (args.length > 0) args(0).toBoolean else true
    val inputPath =
      if (args.length > 1) args(1)
      else "file:///media/sf_vmshare/pmjs/pmj9AuAff/"  //"file:///media/sf_vmshare/pmjs/testAuAff/"  //
    val inputGNPath = if (args.length > 2) args(2) else "file:///media/sf_vmshare/gns/all"
    val outputPathJson = if (args.length > 3) args(3) else "file:///media/sf_vmshare/pmjs/9-x-json"
    val outputPathTrace = if (args.length > 4) args(4) else "file:///media/sf_vmshare/pmjs/9-x-agg"
    val parts = if (args.length > 5) args(5).toInt else 4

    val spark =
      if (runLocally) {
        SparkUtils.sparkContextLocal()
      }
      else {
        SparkUtils.sparkContext(false, "Full Extraction", parts)
      }


    val gnLines = spark.textFile(
        //"/media/sf_vmshare/fp2Affs_uniq"
        inputGNPath
      ).map(tabSplitter.split)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val ccs = Set(US, PR)
    val brCcs = spark.broadcast(ccs)


    val isniEnts = EntXtrUtils.loadIsniNaEns("/media/sf_vmshare/isni.json")

    import CommonUtils._
    import org.ditw.exutil1.naen.NaEnData._
    val gnmmgr = loadGNMmgr(
      ccs, Set(PR), spark,
      "file:///media/sf_vmshare/gns/all"
      ,Map(
//        builtinTag(US_UNIV.toString) -> UsUnivColls,
//        builtinTag(US_HOSP.toString) -> UsHosps,
        builtinTag(ISNI.toString) -> isniEnts
      )
    )
    val brGNMmgr = spark.broadcast(gnmmgr)
    //    val (mmgr, xtrMgr) = genMMgr(svc, dict)
    //    val brSvc = spark.broadcast(svc)
    //    val brMmgr = spark.broadcast(mmgr)
    //    val brXtrMgr = spark.broadcast(xtrMgr)
    //    val brDict = spark.broadcast(dict)
    //    val brTknr = spark.broadcast(TknrHelpers.TknrTextSeg())

    printlnT0("Running extraction ...")

    import PmXtrUtils._
    val allSegs = segmentInput(spark, inputPath)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
    val singleSegs = allSegs.filter(_._3.length == 1)

    val multiSegs = allSegs.filter(_._3.length != 1)

    val (foundRes, emptyRes) = processSingleSegs(singleSegs, brGNMmgr, brCcs)

    printlnT0("Saving results ...")
    val outJsonPath = "file:///media/sf_vmshare/pmjs/ssr"
    SparkUtils.del(spark, outJsonPath)
    foundRes
      .coalesce(1)
      .sortBy(r => r.pmid -> r.localId)
      .map(singleRes2Json)
      .saveAsTextFile(outJsonPath)

    val outEmptyPath = "file:///media/sf_vmshare/pmjs/sse"
    SparkUtils.del(spark, outEmptyPath)
    emptyRes
      .coalesce(1)
      .sortBy(s => s)
      .saveAsTextFile(outEmptyPath)

    spark.stop()
  }

}
