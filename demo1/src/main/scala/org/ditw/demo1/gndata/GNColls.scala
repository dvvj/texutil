package org.ditw.demo1.gndata
import org.ditw.demo1.gndata.GNLevel.GNLevel

object GNColls extends Serializable {
  private class GNColl(
    val level:GNLevel,
    val self:Option[GNEnt],
    val subAdms:IndexedSeq[String],
    val gents:Map[Long, GNEnt]
  ) extends TGNColl {
//    protected[gndata] def childrenMap:Map[Long, GNEnt] = {
//      val all = subColls.flatMap { sc =>
//        sc.childrenMap.toIndexedSeq
//      } ++ self.map(e => e.gnid -> e)
//      val grouped = all.groupBy(_._1).mapValues(_.map(_._2))
//      assert(grouped.forall(_._2.size == 1))
//      grouped.mapValues(_.head)
//    }
  }

  private class GNCollChildMap(
    _level:GNLevel,
    _self:Option[GNEnt],
    _subAdms:IndexedSeq[String],
    _gents:Map[Long, GNEnt]
  ) extends GNColl(_level, _self, _subAdms, _gents) with TGNMap {
//    private val map = childrenMap
    def byId(gnid:Long):GNEnt = _gents(gnid)
  }

  def adm0(
    ent:Option[GNEnt],
    subAdms:IndexedSeq[String],
    gents:Map[Long, GNEnt]
  ):TGNColl = {
    new GNCollChildMap(GNLevel.ADM0, ent, subAdms, gents)
  }

  def admx(
    level:GNLevel,
    ent:Option[GNEnt],
    subAdms:IndexedSeq[String],
    gents:Map[Long, GNEnt]
  ):TGNColl = {
    new GNColl(level, ent, subAdms, gents)
  }
}

