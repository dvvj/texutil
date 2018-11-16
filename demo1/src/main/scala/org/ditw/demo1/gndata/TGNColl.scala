package org.ditw.demo1.gndata
import org.ditw.demo1.gndata.GNLevel.GNLevel

trait TGNColl extends Serializable {
  val level:GNLevel
  val self:Option[GNEnt]
  val subAdms:IndexedSeq[String]
  val gents:Map[Long, GNEnt]
  //protected[gndata] def childrenMap:Map[Long, GNEnt]
}
