package org.ditw.demo1.gndata
import org.apache.spark.rdd.RDD
import org.ditw.demo1.gndata.GNCntry.{CA, GNCntry, US}
import org.ditw.demo1.gndata.SrcData.{loadAdm0, loadCountries}

class GNSvc private (private[demo1] val _cntryMap:Map[GNCntry, TGNMap]) {
  def entById(gnid:Long, country:GNCntry):Option[GNEnt] = {
    val gnmap = _cntryMap(country)
    gnmap.byId(gnid)
  }
}

object GNSvc {

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