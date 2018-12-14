package org.ditw.sparkRuns
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.demo1.gndata.GNCntry.GNCntry
import org.ditw.demo1.gndata.{GNEnt, GNSvc}
import org.ditw.demo1.gndata.SrcData.tabSplitter
import org.ditw.demo1.matchers.MatcherGen
import org.ditw.extract.XtrMgr
import org.ditw.exutil1.poco.PocoUS
import org.ditw.matcher.{MatchPool, MatcherMgr}
import org.ditw.sparkRuns.UtilsEntCsv1.Pfx2Replace
import org.ditw.textSeg.catSegMatchers.Cat2SegMatchers
import org.ditw.textSeg.common.{AllCatMatchers, Vocabs}
import org.ditw.tknr.TknrHelpers
import org.ditw.tknr.Tokenizers.TTokenizer

object CommonUtils extends Serializable {

  private def genMMgr(gnsvc: GNSvc, dict: Dict, ccms:Set[GNCntry]):(MatcherMgr, XtrMgr[Long]) = {
    MatcherGen.gen(
      gnsvc, dict, ccms,
      Option(
        AllCatMatchers.segMatchersFrom(
          dict,
          Seq(Cat2SegMatchers.segMatchers(dict))
        )
      )
    )
  }

  case class GNMmgr(
    tknr:TTokenizer,
    svc:GNSvc,
    dict:Dict,
    mmgr:MatcherMgr,
    xtrMgr: XtrMgr[Long]
  )

  private[sparkRuns] def loadGNMmgr(
    ccs:Set[GNCntry],
    ccms:Set[GNCntry], // countries using
    spark:SparkContext,
    gnPath:String
  ):GNMmgr = {
    val gnLines = spark.textFile(gnPath)
      .map(tabSplitter.split)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
    val svc = GNSvc.loadNoPopuReq(gnLines, ccs)
    val dict = loadDict(svc)
    val (mmgr, xtrMgr) = genMMgr(svc, dict, ccms)
    val tknr = TknrHelpers.TknrTextSeg()
    GNMmgr(tknr, svc, dict, mmgr, xtrMgr)
  }


  private[sparkRuns] def runStr(str:String, tknr:TTokenizer, dict: Dict, mmgr: MatcherMgr, svc:GNSvc, xtrMgr: XtrMgr[Long]) = {
    val mp = MatchPool.fromStr(str, tknr, dict)
    mmgr.run(mp)
//    if (str == "SAN JUAN PR")
//      println("ok")
    val res = svc.extrEnts(xtrMgr, mp)
    res.filter(
      p => p._1.start == 0 && p._1.end == mp.input.linesOfTokens(0).length // should be the whole string
    )
  }

  private def loadDict(
                gnsvc: GNSvc
              ):Dict = {
    val words1 = MatcherGen.wordsFromGNSvc(gnsvc)
    val words2 = Vocabs.allWords
    InputHelpers.loadDict(words1++words2)
  }

  private val maxDiff = 0.5
  private[sparkRuns] def checkCoord(
                          lat1:Double,
                          long1:Double,
                          lat2:Double,
                          long2:Double
                        ):Boolean = {
    math.abs(lat1-lat2) < maxDiff && math.abs(long1-long2) < maxDiff
  }

  private[sparkRuns] def distByCoord(
                           lat1:Double,
                           long1:Double,
                           lat2:Double,
                           long2:Double
                         ):Double = {
    val latDiff = lat1-lat2
    val lonDiff = long1-long2
    latDiff*latDiff + lonDiff*lonDiff
  }

  private[sparkRuns] def csvRead(
    spSess: SparkSession,
    csvPath:String,
    cols:String*
  ):DataFrame = {
    val (first, theRest) = cols.head -> cols.tail
    val rows = spSess.read
      .format("csv")
      .option("header", "true")
      .load(csvPath)
    rows.select(first, theRest: _*)
  }

  def replPfx(in:String, pfxMap:Map[String, String]):Option[String] = {
    val pfx2Repl = pfxMap.keySet.filter(in.startsWith)
    if (pfx2Repl.nonEmpty) {
      if (pfx2Repl.size > 1) throw new IllegalArgumentException("todo")
      val pfx = pfx2Repl.head
      val replaced = pfxMap(pfx) + in.substring(pfx.length)
      Option(replaced)
    } else None
  }

  def findNearestAndCheck(ents:Iterable[GNEnt], coord:(Double, Double)):Option[GNEnt] = {
    val nearest = ents
      .minBy(ent => distByCoord(ent.latitude, ent.longitude, coord._1, coord._2))
    if (!checkCoord(nearest.latitude, nearest.longitude, coord._1, coord._2)) {
      val nearestCoord = (nearest.latitude, nearest.longitude)
      val diff = f"(${coord._1-nearestCoord._1}%.2f,${coord._2-nearestCoord._2}%.2f)"
      println(s"Too far ($diff): ${nearest.gnid} $nearestCoord vs. $coord)")
      None
    }
    else {
      Option(nearest)
    }

  }

  def writeJson[T](path:String, objs:Array[T], conv:Array[T] => String):Unit = {
    val out = new FileOutputStream(path)
    IOUtils.write(conv(objs), out, StandardCharsets.UTF_8)
    out.close()
  }
}
