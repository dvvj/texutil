package org.ditw.demo1.gndata
import org.ditw.demo1.gndata.GNLevel.GNLevel

case class GNEnt(
  gnid:Long,
  name:String,
  private[gndata] var _alias:Set[String],
  latitude:Double,
  longitude:Double,
  featureClz:String,
  featureCode:String,
  countryCode:String,
  admCodes:IndexedSeq[String],
  population:Long
) {
  private var _queryNames:Set[String] = _alias + name
  def queryNames:Set[String] = _queryNames
  def addAliases(aliases:Iterable[String]):Unit = {
    _alias ++= aliases
    _queryNames ++= aliases
  }
  val level:GNLevel = admCodes.size match {
    case 0 => GNLevel.ADM0
    case 1 => GNLevel.ADM1
    case 2 => GNLevel.ADM2
    case 3 => GNLevel.ADM3
    case 4 => GNLevel.ADM4
  }
}
