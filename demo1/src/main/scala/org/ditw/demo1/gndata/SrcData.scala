package org.ditw.demo1.gndata
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.ditw.demo1.src.SrcDataUtils
import org.ditw.demo1.src.SrcDataUtils.GNsCols
import org.ditw.demo1.src.SrcDataUtils.GNsCols.GNsCols

object SrcData extends Serializable {

  val featureCodeIndex = 6
  private val colEnum2Idx = SrcDataUtils.GNsSlimColArrAltCount.indices.map { idx =>
    SrcDataUtils.GNsSlimColArrAltCount(idx) -> idx
  }.toMap
  def colVal(cols:Array[String], col: GNsCols):String = {
    cols(colEnum2Idx(col))
  }
  private val EmptyAdms = IndexedSeq[String]()

  def entFrom(cols:Array[String], countryCode:String, admCodes:IndexedSeq[String]):GNEnt = {
    GNEnt(
      colVal(cols, GNsCols.GID).toLong,
      colVal(cols, GNsCols.Name),
      Set(colVal(cols, GNsCols.AsciiName)), // todo
      colVal(cols, GNsCols.Latitude).toDouble,
      colVal(cols, GNsCols.Longitude).toDouble,
      colVal(cols, GNsCols.FeatureClass),
      colVal(cols, GNsCols.FeatureCode),
      countryCode,
      admCodes,
      colVal(cols, GNsCols.Population).toLong
    )
  }

  def loadAdm0(rdd:RDD[Array[String]]):Map[String, GNEnt] = {
    val adm0Ents = rdd.filter(cols => SrcDataUtils.isPcl(cols(featureCodeIndex)))
      .map { cols =>
        val countryCode = colVal(cols, GNsCols.CountryCode)
        val ent = entFrom(cols, countryCode, EmptyAdms)
        countryCode -> ent
      }.collectAsMap()

    adm0Ents.toMap
  }

}
