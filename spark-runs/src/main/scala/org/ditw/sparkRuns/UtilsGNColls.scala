package org.ditw.sparkRuns
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.ditw.demo1.gndata.GNLevel._
import org.ditw.demo1.gndata.{GNColls, GNEnt, TGNColl}
import org.ditw.demo1.src.SrcDataUtils
import org.ditw.demo1.src.SrcDataUtils.GNsCols
import org.ditw.demo1.src.SrcDataUtils.GNsCols._

import scala.collection.mutable.ListBuffer
import org.ditw.common.GenUtils._

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

    val ccList = List(
      "US"
      ,"CA", "GB", "AU", "FR", "DE", "ES", "IT"
    )
    val admCols = List(GNsCols.Adm1, GNsCols.Adm2, GNsCols.Adm3, GNsCols.Adm4)

    ccList.foreach { cc =>
      printlnT0(s"--- $cc")

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

      printlnT0(s"\tADM->Ent: ${gnsInCC.count}")

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

          val admM1 = admMinus1Code(p._1._1)
          val level =
            if (admEnt.nonEmpty) admEnt.get.level
            else p._1._2
          admM1 -> (level, p._1._1)
        }
        .groupByKey()
        .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

      val admGNs = gnsInCC.groupByKey()
        .map(p => p._1._1 -> (p._1._2 -> p._2))
        .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

      val withAdmc = t1.filter { p =>
        //        if (p._1.isEmpty)
        //          println("ok")
        p._1.nonEmpty
      }.map(p => p._1.get -> p._2)
        .persist(StorageLevel.MEMORY_AND_DISK_SER_2)


      val t3 = admGNs
        .filter { p =>
          p._1 != cc || p._2._1 != ADM0
        }
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

      val m2 = t3.collectAsMap().toMap

      assert (!m2.contains(cc))

      val t = withAdmc.filter(_._1 == cc).collect()
      val adm0GNs = admGNs.filter { p =>
        p._1 == cc && p._2._1 == ADM0
      }.collect()

      println(s"\tADM0: ${adm0GNs.length}")
      val gentOfAdm0 =
        if (adm0GNs.isEmpty) EmptyMap
        else {
          assert(adm0GNs.length == 1)
          adm0GNs(0)._2._2.map(ent => ent.gnid -> ent).toMap
        }

      assert (t.length == 1)
      val admEnt = adm0Ents.get(cc)
      val adm0 = GNColls.adm0(
        admEnt, // todo
        t(0)._2.map(_._2).toIndexedSeq,
        gentOfAdm0,
        m2
      )
        //m2 += cc -> adm0
      printlnT0(s"\tMap size: ${m2.size}")
      printlnT0(s"\tEnt in Map: " + m2.map(_._2.size).sum)

      val strs = testStrs.getOrElse(cc, List())
      strs.foreach { ts =>
        val parts = ts.split(",").map(_.trim).filter(_.nonEmpty)
        val res = adm0.byName(parts(0), cc + "_" + parts(1))
        printlnT0(res)
      }
    }

    spark.stop()
  }

  val testStrs = Map(
    "US" -> List[String](
      "Minneapolis, MN",
      "Research Triangle Park, NC"
    ),
    "CA" -> List[String](
      "Montréal, Québec"
    )
  )
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
