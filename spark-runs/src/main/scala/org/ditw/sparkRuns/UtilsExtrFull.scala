package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.GenUtils.printlnT0
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry.{CA, JP, US}
import org.ditw.demo1.gndata.{GNCntry, GNSvc}
import org.ditw.demo1.gndata.SrcData.tabSplitter
import org.ditw.demo1.matchers.{MatcherGen, TagHelper}
import org.ditw.matcher.MatchPool
import org.ditw.sparkRuns.UtilsExtract._
import org.ditw.tknr.TknrHelpers

object UtilsExtrFull {
  def main(args:Array[String]):Unit = {
//    val spark = SparkUtils.sparkContextLocal()
//
//    val gnLines = spark.textFile(
//      //"/media/sf_vmshare/fp2Affs_uniq"
//      "/media/sf_vmshare/gns/all"
//    ).map(tabSplitter.split)
//      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
//    val ccs = Set(
//      US, JP
//      ,CA //, GB, AU //,FR,DE,ES,IT
//    )
//    val svc = GNSvc.load(gnLines, ccs)
//
//    val dict = MatcherGen.loadDict(svc)
//
//    val (mmgr, xtrMgr) = MatcherGen.gen(svc, dict)
//    val brSvc = spark.broadcast(svc)
//    val brMmgr = spark.broadcast(mmgr)
//    val brXtrMgr = spark.broadcast(xtrMgr)
//    val brDict = spark.broadcast(dict)
//    val brTknr = spark.broadcast(TknrHelpers.TknrTextSeg)
//
//    printlnT0("Running extraction ...")
//
//    val xtrs = spark.textFile("/media/sf_vmshare/pmjs/pmj9AuAff")
//      .map { l =>
//        val
//        val mp = MatchPool.fromStr(l, TknrHelpers.TknrTextSeg, brDict.value)
//        brMmgr.value.run(mp)
//        val t = TagHelper.cityCountryTag(GNCntry.US)
//        val rng2Ents = brSvc.value.extrEnts(brXtrMgr.value, mp)
//        l -> rng2Ents.map(identity)
//      }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)
//
//    spark.stop()
  }
}
