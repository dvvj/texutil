package org.ditw.sparkRuns
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.ditw.demo1.gndata.GNLevel._
import org.ditw.demo1.gndata.{GNColls, GNEnt, TGNColl}
import org.ditw.demo1.src.SrcDataUtils
import org.ditw.demo1.src.SrcDataUtils.GNsCols
import org.ditw.demo1.src.SrcDataUtils.GNsCols._

import scala.collection.mutable.ListBuffer

object UtilsGNColls {

  private val tabSplitter = "\\t".r
  val featureClassIndex = 5
  val featureCodeIndex = 6
  val countryCodeIndex = 7

  private val admIndexMap = Map(
    Adm1 -> 9,
    Adm2 -> 10,
    Adm3 -> 11,
    Adm4 -> 12
  )
  private val indexAdmMap = admIndexMap.map(p => p._2 -> p._1)

  private val emptyCol2Level:Map[GNsCols, GNLevel] = Map(
    Adm1 -> ADM0,
    Adm2 -> ADM1,
    Adm3 -> ADM2,
    Adm4 -> ADM3
  )

//  private val admLevel2Code = List(ADM1, ADM2, ADM3, ADM4).map(a => a -> a.toString).toMap

  private val EmptyMap = Map[Long, GNEnt]()
  private val EmptySubAdms = IndexedSeq[String]()
  private val EmptyAdms = IndexedSeq[String]()

  private def admCode(gncols:Array[String]):(String, GNLevel) = {

    if (gncols(featureCodeIndex) == "PCLI")
      println("error")
    var res = gncols(countryCodeIndex)
    var empty = false
    var level:GNLevel = ADM4
    val colIdxSorted = admIndexMap.values.toIndexedSeq.sorted
    val it = colIdxSorted.iterator
    while (it.hasNext && !empty) {
      val idx = it.next()
      if (gncols(idx).nonEmpty) {
        if (empty)
          throw new IllegalArgumentException("Already empty?!")
        res += s"_${gncols(idx)}"
      }
      else {
        empty = true
        level = emptyCol2Level(indexAdmMap(idx))
      }
    }
//    if (level.isEmpty)
//      println("ok")
    res -> level
  }

  private val colEnum2Idx = SrcDataUtils.GNsSlimColArrAltCount.indices.map { idx =>
    SrcDataUtils.GNsSlimColArrAltCount(idx) -> idx
  }.toMap
  def colVal(cols:Array[String], col: GNsCols):String = {
    cols(colEnum2Idx(col))
  }

  private def admMinus1Code(admCode:String):Option[String] = {
    val lastIdx = admCode.lastIndexOf("_")
    if (lastIdx > 0) {
      Option(admCode.substring(0, lastIdx))
    }
    else None
  }


  def main(args:Array[String]):Unit = {
    val spark = SparkUtils.sparkContextLocal()

    val gnLines = spark.textFile(
      //"/media/sf_vmshare/fp2Affs_uniq"
      "/media/sf_vmshare/gns/all"
    ).map(tabSplitter.split)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val adm0Ents = gnLines.filter(cols => SrcDataUtils.isPcl(cols(featureCodeIndex)))
      .map { cols =>
        val countryCode = colVal(cols, GNsCols.CountryCode)
        val ent = GNEnt(
          colVal(cols, GNsCols.GID).toLong,
          colVal(cols, GNsCols.Name),
          Set(colVal(cols, GNsCols.AsciiName)), // todo
          colVal(cols, GNsCols.Latitude).toDouble,
          colVal(cols, GNsCols.Longitude).toDouble,
          colVal(cols, GNsCols.FeatureClass),
          colVal(cols, GNsCols.FeatureCode),
          countryCode,
          EmptyAdms,
          colVal(cols, GNsCols.Population).toLong
        )
        countryCode -> ent
      }.collectAsMap()
    val brAdm0Ents = spark.broadcast(adm0Ents)

    val cc = "ES"

    val admCols = List(GNsCols.Adm1, GNsCols.Adm2, GNsCols.Adm3, GNsCols.Adm4)



    val gnsInCC:RDD[((String,GNLevel), GNEnt)] = gnLines
      .filter { cols =>
        cols(countryCodeIndex) == cc && !SrcDataUtils.isPcl(cols(featureCodeIndex))
      }
      .map { cols =>
        val adms = ListBuffer[String]()
        admCols.foreach { col =>
          if (colVal(cols, col).nonEmpty) {
            adms += colVal(cols, col)
          }
        }
        val ent = GNEnt(
          colVal(cols, GNsCols.GID).toLong,
          colVal(cols, GNsCols.Name),
          Set(colVal(cols, GNsCols.AsciiName)), // todo
          colVal(cols, GNsCols.Latitude).toDouble,
          colVal(cols, GNsCols.Longitude).toDouble,
          colVal(cols, GNsCols.FeatureClass),
          colVal(cols, GNsCols.FeatureCode),
          colVal(cols, GNsCols.CountryCode),
          adms.toIndexedSeq,
          colVal(cols, GNsCols.Population).toLong
        )

        val admc = admCode(cols)
        admc -> ent
      }
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    println(gnsInCC.count())


    val t1:RDD[(Option[String], Iterable[(GNLevel, String)])] = gnsInCC
      .groupByKey()
      .map { p =>
        var admEnt:Option[GNEnt] = None
        val admCode = p._1._2.toString
        val ents = p._2
        ents.foreach { ent =>
          if (ent.featureCode == admCode) {
            if (admEnt.nonEmpty)
              throw new IllegalArgumentException("dup adm entity?")
            admEnt = Option(ent)
          }
        }

//        if (admEnt.nonEmpty && admEnt.get.level == ADM1)
//          println("ok")

        val admM1 = admMinus1Code(p._1._1)
        val level =
          if (admEnt.nonEmpty) admEnt.get.level
          else p._1._2
        admM1 -> (level, p._1._1)
//        GNColls.admx(
//          level,
//          admEnt,
//          ents
//        )
//        if (admEnt.isEmpty) {
//          throw new IllegalArgumentException("no adm entity?")
//        }
      }
      .groupByKey()
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val admGNs = gnsInCC.groupByKey()
      .map(p => p._1._1 -> (p._1._2 -> p._2))
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val noAdmc = t1.filter(_._1.isEmpty).collect()
    println(noAdmc.length)
    val withAdmc = t1.filter { p =>
//        if (p._1.isEmpty)
//          println("ok")
        p._1.nonEmpty
      }.map(p => p._1.get -> p._2)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

//    val t2 = withAdmc.leftOuterJoin(admGNs)
//      .map { p =>
//        //
//        val (sub, tp) = p._2
//        if (tp.nonEmpty) {
//          val (level, curr) = tp.get
//          val (currAdm, ppls) = splitAdmAndPpl(curr, level)
//
//          val coll:TGNColl = GNColls.admx(
//            level,
//            currAdm,
//            sub.map(_._2).toIndexedSeq,
//            ppls.map(ppl => ppl.gnid -> ppl).toMap
//          )
//
//          p._1 -> coll
////          it.map { tp =>
////            val
////          }
////          val coll = GNColls.admx(level, ent, )
//        }
//        else {
//          val adm0:TGNColl = GNColls.adm0(
//            None,
//            sub.map(_._2).toIndexedSeq,
//            EmptyMap
//          )
//          cc -> adm0
//        }
//      }
//      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
//
//    val m = t2.collectAsMap()
//
//    println(m.size)
//    println("m.size: " + m.map(_._2.size).sum)

    val t3 = admGNs
      .leftOuterJoin(withAdmc)
      //.filter(_._2._2.isEmpty)
      .mapValues { p =>
        val (level, curr) = p._1
        val (currAdm, ppls) = splitAdmAndPpl(curr, level)
        val subAdms =
          if (p._2.isEmpty) EmptySubAdms
          else p._2.get.map(_._2).toIndexedSeq

        val admEnt =
          if (level == ADM0 && currAdm.isEmpty) {
            brAdm0Ents.value.get(cc)
          }
          else if (level == ADM0 && currAdm.nonEmpty)
            currAdm
          else
            currAdm
        val coll = GNColls.admx(
          level,
          admEnt,
          subAdms,
          ppls.map(ppl => ppl.gnid -> ppl).toMap
        )
//        if (level == ADM0)
//          println("ok")
        coll
      }

    var m2 = t3.collectAsMap()

    if (!m2.contains(cc)) {
      val t = withAdmc.filter(_._1 == cc).collect()
      if (t.length == 1) {
        val admEnt = adm0Ents.get(cc)
        val adm0 = GNColls.adm0(
          admEnt, // todo
          t(0)._2.map(_._2).toIndexedSeq,
          EmptyMap
        )
        m2 += cc -> adm0
      }
      else {
        throw new IllegalArgumentException("More than one country code entries?!")
      }
    }

    println(m2.size)
    println(s"m2.size: " + m2.map(_._2.size).sum)

    //    val tPath = "/media/sf_vmshare/ttt"
//    SparkUtils.deleteLocal(tPath)
//    t2.sortBy(_._1).saveAsTextFile(tPath)

//    val featureCodeIndex = 6
//    val adm1s = gnsInCC.filter(_(featureCodeIndex) == "ADM1").collect()
//    val adm2s = gnsInCC.filter(_(featureCodeIndex) == "ADM2").collect()
//    val adm3s = gnsInCC.filter(_(featureCodeIndex) == "ADM3").collect()
//    val adm4s = gnsInCC.filter(_(featureCodeIndex) == "ADM4").collect()
//    val adm5s = gnsInCC.filter(_(featureCodeIndex) == "ADM5").collect()
//    val admds = gnsInCC.filter(_(featureCodeIndex) == "ADMD").collect()
//    val prshs = gnsInCC.filter(_(featureCodeIndex) == "PRSH").collect()
//    println(s"${adm1s.length} ${adm2s.length} ${adm3s.length} ${adm4s.length} ${adm5s.length} ${admds.length} ${prshs.length}")

    spark.stop()
  }

  //private val EmptyPpls = IndexedSeq[GNEnt]()
  private def splitAdmAndPpl(in:Iterable[GNEnt], level:GNLevel)
    :(Option[GNEnt], Iterable[GNEnt]) = {
    var currAdm:Option[GNEnt] = None
    val ppls = in.filter { c =>
      if (c.featureCode == level.toString) {
        currAdm = Option(c)
        false
      }
      else true
    }
    currAdm -> ppls
  }
}
