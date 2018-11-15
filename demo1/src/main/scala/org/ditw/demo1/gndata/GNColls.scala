package org.ditw.demo1.gndata
import org.ditw.demo1.gndata.GNLevel.GNLevel

object GNColls extends Serializable {
  private class GNColl(
    val level:GNLevel,
    val self:GNEnt,
    val subColls:IndexedSeq[TGNColl]
  ) extends TGNColl {
    protected[gndata] def childrenMap:Map[Long, GNEnt] = {
      val all = (self.gnid -> self) +: subColls.flatMap { sc =>
        sc.childrenMap.toIndexedSeq
      }
      val grouped = all.groupBy(_._1).mapValues(_.map(_._2))
      assert(grouped.forall(_._2.size == 1))
      grouped.mapValues(_.head)
    }
  }

  private class GNCollChildMap(
    _level:GNLevel,
    _self:GNEnt,
    _subColls:IndexedSeq[TGNColl]
  ) extends GNColl(_level, _self, _subColls) with TGNMap {
    private val map = childrenMap
    def byId(gnid:Long):GNEnt = map(gnid)
  }

  def adm0(
    ent:GNEnt,
    subColls:IndexedSeq[TGNColl]
  ):TGNMap = {
    new GNCollChildMap(GNLevel.ADM0, ent, subColls)
  }

  def admx(
    level:GNLevel,
    ent:GNEnt,
    subColls:IndexedSeq[TGNColl]
  ):TGNColl = {
    new GNColl(level, ent, subColls)
  }
}

