package org.ditw.demo1.gndata
import org.apache.spark.rdd.RDD
import org.ditw.common.TkRange
import org.ditw.demo1.gndata.GNCntry.{CA, GNCntry, US}
import org.ditw.demo1.gndata.SrcData.{loadAdm0, loadCountries}
import org.ditw.extract.XtrMgr
import org.ditw.matcher.MatchPool

class GNSvc private (private[demo1] val _cntryMap:Map[GNCntry, TGNMap]) extends Serializable {
  private val idMap:Map[Long, GNEnt] = {
    _cntryMap.flatMap(_._2.idMap)
  }

  def entById(gnid:Long):Option[GNEnt] = {
    idMap.get(gnid)
  }

  def extrEnts(xtrMgr:XtrMgr[Long], matchPool: MatchPool):Map[TkRange, List[GNEnt]] = {
    val xtrs = xtrMgr.run(matchPool)
    xtrs.mapValues(_.flatMap(entById))
  }
}

object GNSvc extends Serializable {

  def load(
    lines:RDD[Array[String]],
    countries:Set[GNCntry]
  ): GNSvc = {
    val spark = lines.sparkContext
    val adm0Ents = loadAdm0(lines)
    val brAdm0Ents = spark.broadcast(adm0Ents)

    val ccs = Set(
      US, CA
      //, "GB", "AU", "FR", "DE", "ES", "IT"
    )
    val adm0s = loadCountries(lines, ccs, brAdm0Ents)

    new GNSvc(adm0s)
  }
}