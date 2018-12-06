package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.GenUtils.printlnT0
import org.ditw.common.{Dict, InputHelpers, SparkUtils, TkRange}
import org.ditw.demo1.gndata.GNCntry.{CA, JP, US}
import org.ditw.demo1.gndata.{GNCntry, GNEnt, GNSvc}
import org.ditw.demo1.gndata.SrcData.tabSplitter
import org.ditw.demo1.matchers.{MatcherGen, TagHelper}
import org.ditw.extract.XtrMgr
import org.ditw.matcher.{MatchPool, MatcherMgr}
import org.ditw.pmxml.model.AAAuAff
import org.ditw.sparkRuns.UtilsExtract.{xtrs, _}
import org.ditw.textSeg.catSegMatchers.Cat2SegMatchers
import org.ditw.textSeg.common.Tags.TagGroup4Univ
import org.ditw.textSeg.common.{AllCatMatchers, Vocabs}
import org.ditw.tknr.TknrHelpers

import scala.collection.mutable.ListBuffer

object UtilsExtrFull {

  private def genMMgr(gnsvc: GNSvc, dict: Dict):(MatcherMgr, XtrMgr[Long]) = {
    MatcherGen.gen(
      gnsvc, dict,
      Option(
        AllCatMatchers.segMatchersFrom(
          dict,
          Seq(Cat2SegMatchers.segMatchers(dict))
        )
      )
    )
  }

  def loadDict(
                gnsvc: GNSvc
              ):Dict = {
    val words1 = MatcherGen.wordsFromGNSvc(gnsvc)
    val words2 = Vocabs.allWords
    InputHelpers.loadDict(words1++words2)
  }

  def main(args:Array[String]):Unit = {
    val spark = SparkUtils.sparkContextLocal()

    val gnLines = spark.textFile(
      //"/media/sf_vmshare/fp2Affs_uniq"
      "/media/sf_vmshare/gns/all"
    ).map(tabSplitter.split)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
    val ccs = Set(
      US, JP
      //,CA , GB, AU //,FR,DE,ES,IT
    )
    val svc = GNSvc.load(gnLines, ccs)

    val dict = loadDict(svc)

    val (mmgr, xtrMgr) = genMMgr(svc, dict)
    val brSvc = spark.broadcast(svc)
    val brMmgr = spark.broadcast(mmgr)
    val brXtrMgr = spark.broadcast(xtrMgr)
    val brDict = spark.broadcast(dict)
    val brTknr = spark.broadcast(TknrHelpers.TknrTextSeg)

    printlnT0("Running extraction ...")

    val xtrs = spark.textFile("/media/sf_vmshare/pmjs/pmj9AuAff")
      .flatMap { l =>
        val firstComma = l.indexOf(",")
        val pmid = l.substring(1, firstComma).toLong
        val j = l.substring(firstComma+1, l.length-1)
        val auaff = AAAuAff.fromJson(j)
        val affMap = auaff.affs.map(aff => aff.localId -> aff.aff.aff).toMap
        affMap.mapValues { aff =>
          val mp = MatchPool.fromStr(aff, TknrHelpers.TknrTextSeg, brDict.value)
          brMmgr.value.run(mp)
          val univRngs = mp.get(TagGroup4Univ.segTag).map(_.range)
          val rng2Ents = brSvc.value.extrEnts(brXtrMgr.value, mp)
          val univs =
            if (univRngs.size == 1 && rng2Ents.size == 1) { // name fix, todo: better structure
              val univRng = univRngs.head
              val gnEntRng = rng2Ents.head._1
              if (univRng.overlap(gnEntRng) && gnEntRng.start > univRng.start) {
                val newRng = univRng.copy(end = gnEntRng.start)
                Set(newRng.str)
              }
              else {
                univRngs.map(_.str)
              }
            }
            else univRngs.map(_.str)
          (aff, rng2Ents.map(identity), univs)
        }.toIndexedSeq.sortBy(_._1).map(p => (pmid, p._1, p._2))
      }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    printlnT0("Saving results ...")

    val savePathEmpty = "/media/sf_vmshare/pmjs/9-e"
    SparkUtils.deleteLocal(savePathEmpty)
    xtrs.filter(xtr => xtr._3._2.isEmpty || xtr._3._3.isEmpty).saveAsTextFile(savePathEmpty)

    val hasXtrs1 = xtrs.filter(xtr => xtr._3._2.size == 1 && xtr._3._3.size == 1)
    val savePath1 = "/media/sf_vmshare/pmjs/9-x-s"
    SparkUtils.deleteLocal(savePath1)
    hasXtrs1.map { p =>
      val (pmid, localId, pp) = p
      trace(pmid, localId, pp)
    }.saveAsTextFile(savePath1)

    val savePath1a = "/media/sf_vmshare/pmjs/9-x-agg"
    SparkUtils.deleteLocal(savePath1a)
    hasXtrs1.map { p =>
        val (pmid, localId, pp) = p
        val univ = p._3._3.head
        val gnEnt = p._3._2.values.head.head
//        if (univ.endsWith(gnEnt.name))
//          univ = univ.substring(0, univ.length-gnEnt.name.length).trim // name fix, todo: better structure
        (
          univ,
          (gnEnt.gnid, gnEnt.toString, pmid, localId)
        )
      }
      .groupBy(_._1)
      .mapValues{ p =>
        p.map(_._2).groupBy(_._1)
          .mapValues { pp =>
            val gnstr = pp.head._2
            val fps = pp.toList.sortBy(_._3).map(tp => s"${tp._3}-${tp._4}")
            s"$gnstr #${fps.size}: ${fps.mkString(",")}"
          }.toList.sortBy(_._2)
          .map(_._2)
          .mkString("\t", "\n\t", "")
      }
      .sortBy(_._1)
      .map { p =>
        s"${p._1}\n${p._2}"
      }
      .saveAsTextFile(savePath1a)

    val hasXtrs = xtrs.filter(xtr => xtr._3._2.size > 1 && xtr._3._3.nonEmpty || xtr._3._2.nonEmpty && xtr._3._3.size > 1)
    val savePath = "/media/sf_vmshare/pmjs/9-x-m"
    SparkUtils.deleteLocal(savePath)
    hasXtrs.map { p =>
      val (pmid, localId, pp) = p
      trace(pmid, localId, pp)
    }.saveAsTextFile(savePath)

    spark.stop()
  }

  def trace(pmid:Long, localId:Int,
            pp:(String, Map[TkRange, List[GNEnt]], Set[String])):String = {
    val trs = ListBuffer[String]()
    pp._2.keySet.toList.sorted.map { range =>
      val ents = pp._2(range)
      val trsEnts = ents.mkString("[", "],[", "]")
      trs += s"$range: $trsEnts"
    }
    val univStrs = pp._3.toList.sorted.mkString("---{", "},{", "}")
    trs += univStrs
    val trStr = trs.mkString("; ")
    s"$pmid-$localId: [${pp._1}]\n\t$trStr"
  }
}
