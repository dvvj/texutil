package org.ditw.demo1.extracts
import org.ditw.demo1.gndata.{GNEnt, GNSvc}
import org.ditw.extract.TXtr
import org.ditw.matcher.TkMatch

import scala.collection.mutable.ListBuffer

object Xtrs extends Serializable {

  import org.ditw.extract.TXtr._
  import org.ditw.demo1.matchers.TagHelper._

  private[demo1] def extractEntId(m: TkMatch)
  : List[Long] = {
    val allMatches = m.flatten
    allMatches.flatMap(_.getTags).filter { _.startsWith(GNIdTagPfx) }
      .map { gnidTag =>
        val gnid = gnidTag.substring(GNIdTagPfx.length).toLong
        gnid
      }.toList
  }

  private[demo1] def entXtr4Tag(tag2Match:String):TXtr[Long] = new TExactTag[Long](tag2Match) {
    override def _extract(m: TkMatch)
      : List[Long] = {
      extractEntId(m)
    }
  }

  private[demo1] def entXtr4TagPfx(tagPfx:String):TXtr[Long] = new TTagPfx[Long](tagPfx) {
    override def _extract(m: TkMatch)
    : List[Long] = {
      extractEntId(m.children.head)
    }
  }

  private[demo1] def entXtrFirst4TagPfx(gnsvc:GNSvc, tagPfx:String):TXtr[Long] = new TTagPfx[Long](tagPfx) {
    override def _extract(m: TkMatch)
    : List[Long] = {
      val admIds = extractEntId(m.children.last)
      if (admIds.size > 1)
        println("more than one adm1")
      val admEnt = gnsvc.entById(admIds.head).get

      var admEnts = List(admEnt)

      var idx = m.children.size-2
      while (idx >= 0) {
        val nextAdmEnts = ListBuffer[GNEnt]()
        val ids = extractEntId(m.children(idx))
        nextAdmEnts ++= ids.flatMap { id =>
          val e = gnsvc.entById(id)
          if (e.nonEmpty && admEnts.exists(ae => e.get.admc.startsWith(ae.admc)))
            Option(e.get)
          else None
        }
        admEnts = nextAdmEnts.toList
        idx -= 1
      }

      if(admEnts.isEmpty)
        println("ok")
      admEnts.map(_.gnid)
    }
  }

}
