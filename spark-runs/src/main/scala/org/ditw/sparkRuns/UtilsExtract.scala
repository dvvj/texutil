package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.GenUtils.printlnT0
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry._
import org.ditw.demo1.gndata.{GNCntry, GNSvc}
import org.ditw.demo1.gndata.SrcData.tabSplitter
import org.ditw.demo1.matchers.{MatcherGen, TagHelper}
import org.ditw.matcher.MatchPool
import org.ditw.tknr.TknrHelpers

import scala.collection.mutable.ListBuffer

object UtilsExtract extends App {
  val spark = SparkUtils.sparkContextLocal()
  val gnLines = spark.textFile(
    //"/media/sf_vmshare/fp2Affs_uniq"
    "/media/sf_vmshare/gns/all"
  ).map(tabSplitter.split)
    .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
  val ccs = Set(
    US, JP
    ,CA //, GB, AU //,FR,DE,ES,IT
  )
  val svc = GNSvc.load(gnLines, ccs)

  val dict = MatcherGen.loadDict(svc)

  val (mmgr, xtrMgr) = MatcherGen.gen(svc, dict)
  val brSvc = spark.broadcast(svc)
  val brMmgr = spark.broadcast(mmgr)
  val brXtrMgr = spark.broadcast(xtrMgr)
  val brDict = spark.broadcast(dict)
  val brTknr = spark.broadcast(TknrHelpers.TknrTextSeg)

  printlnT0("Running extraction ...")

  val xtrs = spark.textFile("/media/sf_vmshare/aff-w2v-dbg")
    .map { l =>
      val mp = MatchPool.fromStr(l, TknrHelpers.TknrTextSeg, brDict.value)
      brMmgr.value.run(mp)
      val t = TagHelper.cityCountryTag(GNCntry.US)
      val rng2Ents = brSvc.value.extrEnts(brXtrMgr.value, mp)
      l -> rng2Ents.map(identity)
    }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

  printlnT0("Saving results ...")

  val savePathEmpty = "/media/sf_vmshare/aff-w2v-e"
  SparkUtils.deleteLocal(savePathEmpty)
  xtrs.filter(_._2.isEmpty).keys.saveAsTextFile(savePathEmpty)

  val hasXtrs = xtrs.filter(_._2.nonEmpty)
  val savePath = "/media/sf_vmshare/aff-w2v-x"
  SparkUtils.deleteLocal(savePath)
  hasXtrs.map { p =>
    val (line, m) = p
    val trs = ListBuffer[String]()
    m.keySet.toList.sorted.map { range =>
      val ents = m(range)
      val trsEnts = ents.mkString("[", "],[", "]")
      trs += s"$range: $trsEnts"
    }
    val trStr = trs.mkString("; ")
    s"$line\n\t$trStr"
  }.saveAsTextFile(savePath)

  printlnT0("Done!")
  spark.stop()
}
