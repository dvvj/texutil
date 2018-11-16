package org.ditw.demo1.gndata
import org.ditw.demo1.gndata.GNLevel.GNLevel

case class GNEnt(
  gnid:Long,
  name:String,
  alias:Set[String],
  latitude:Double,
  longitude:Double,
  featureClz:String,
  featureCode:String,
  countryCode:String,
  admCodes:IndexedSeq[String],
  population:Long
) {
  val level:GNLevel = admCodes.size match {
    case 0 => GNLevel.ADM0
    case 1 => GNLevel.ADM1
    case 2 => GNLevel.ADM2
    case 3 => GNLevel.ADM3
    case 4 => GNLevel.ADM4
  }
}
