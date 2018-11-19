package org.ditw.demo1.gndata

trait TGNMap extends TGNColl {
  def byId(gnid:Long):GNEnt
  def idsByName(name:String, adm:String):IndexedSeq[Long]
  def byName(name:String, adm:String):IndexedSeq[GNEnt]
  def admNameMap:Map[String, Map[String, IndexedSeq[Long]]]
  def admMap:Map[String, TGNColl]
}
