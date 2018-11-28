package org.ditw.demo1.extracts
import org.ditw.demo1.gndata.GNEnt
import org.ditw.extract.TXtr
import org.ditw.matcher.TkMatch

object Xtrs extends Serializable {

  import org.ditw.extract.TXtr._
  import org.ditw.demo1.matchers.TagHelper._

  private def extractEntId(m: TkMatch)
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

}
