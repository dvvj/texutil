package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.demo1.gndata.GNEnt
import org.ditw.demo1.src.SrcDataUtils
import org.ditw.demo1.src.SrcDataUtils.GNsCols
import org.ditw.demo1.src.SrcDataUtils.GNsCols.GNsCols

import scala.collection.mutable.ListBuffer

object UtilsGNColls {

  private val tabSplitter = "\\t".r
  val featureCodeIndex = 6
  val countryCodeIndex = 7

  def admCode(gncols:Array[String]):String = {
    val admIndices = 9 to 12
    var res = gncols(countryCodeIndex)
    admIndices.foreach { idx =>
      if (gncols(idx).nonEmpty)
        res += s"_${gncols(idx)}"
    }
    res
  }

  private val colEnum2Idx = SrcDataUtils.GNsSlimColArrAltCount.indices.map { idx =>
    SrcDataUtils.GNsSlimColArrAltCount(idx) -> idx
  }.toMap
  def colVal(cols:Array[String], col: GNsCols):String = {
    cols(colEnum2Idx(col))
  }

  def main(args:Array[String]):Unit = {
    val spark = SparkUtils.sparkContextLocal()

    val gnLines = spark.textFile(
      //"/media/sf_vmshare/fp2Affs_uniq"
      "/media/sf_vmshare/gns/all"
    )

    val cc = "US"

    val admCols = List(GNsCols.Adm1, GNsCols.Adm2, GNsCols.Adm3, GNsCols.Adm4)

    val gnsInCC = gnLines.map { l =>
        tabSplitter.split(l)
      }
      .filter(_(countryCodeIndex) == cc)
      .map(cols => admCode(cols) -> cols)
      .groupByKey()
      .map { p =>
        var admEnt:Option[GNEnt] = None
        p._2.foreach { cols =>
          val adms = ListBuffer[String]()
          admCols.foreach { col =>
            if (colVal(cols, col).nonEmpty)
              adms += colVal(cols, col)
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
          val fc = cols(featureCodeIndex)
          if (fc.startsWith("ADM") && fc != "ADMD") {
            if (admEnt.nonEmpty)
              throw new IllegalArgumentException("dup adm entity?")
            admEnt = Option(ent)
          }

        }
//        if (admEnt.isEmpty) {
//          throw new IllegalArgumentException("no adm entity?")
//        }
      }
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    println(gnsInCC.count())

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
}
