package org.ditw.demo1.gndata

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

}
