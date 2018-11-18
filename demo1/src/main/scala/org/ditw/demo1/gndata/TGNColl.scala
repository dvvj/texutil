package org.ditw.demo1.gndata
import org.ditw.demo1.gndata.GNLevel.GNLevel

trait TGNColl extends Serializable {
  val level:GNLevel
  val self:Option[GNEnt]
  val subAdms:IndexedSeq[String]
  val gents:Map[Long, GNEnt]

  override def toString: String = {
    val selfName = if (self.nonEmpty) self.get.name else "_NA_"

    s"$level: $selfName(${subAdms.size},${gents.size})"
  }
  //protected[gndata] def childrenMap:Map[Long, GNEnt]
}
