package org.ditw.demo1.gndata

trait TGNMap extends TGNColl {
  def byId(gnid:Long):GNEnt
  def idsByName(name:String, adm:String):IndexedSeq[Long]
  def byName(name:String, adm:String):IndexedSeq[GNEnt]
}
