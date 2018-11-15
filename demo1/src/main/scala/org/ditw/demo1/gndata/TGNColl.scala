package org.ditw.demo1.gndata
import org.ditw.demo1.gndata.GNLevel.GNLevel

trait TGNColl extends Serializable {
  val level:GNLevel
  val self:GNEnt
  val subColls:IndexedSeq[TGNColl]
  protected[gndata] def childrenMap:Map[Long, GNEnt]
}
