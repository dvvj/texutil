package org.ditw.exutil1.extract
import org.ditw.common.ResourceHelpers
import org.ditw.extract.TXtr
import org.ditw.extract.TXtr.XtrPfx
import org.ditw.exutil1.poco.{PoPfxGB, PocoData, PocoTags}
import org.ditw.matcher.TkMatch

object PocoXtrs extends Serializable {

  private val pocogbPfxs = ResourceHelpers.load("/pocopfx/gb.json", PoPfxGB.fromJson)
  private val pocogbPfx2GNid = pocogbPfxs.flatMap { pocogbPfx =>
    pocogbPfx.gnid2Pfxs.flatMap { p =>
      val id = p._1
      val pfxs = p._2.toSet
      val t = pfxs.map(pfx => pfx.replaceAllLiterally(" ", ""))
      t.map(_ -> id)
    }
  }.toMap


  private[exutil1] def pocoXtr4TagPfx(
    pfx2IdMap:Map[String, Long],
    tagPfx:String
  ):TXtr[Long] = new XtrPfx[Long](tagPfx) {
    private val pfxRange:Range = {
      val t = pfx2IdMap.keySet.map(_.length)
      t.min to t.max
    }
    override def _extract(m: TkMatch)
    : List[Long] = {
      // todo: generalize to other countries
      val all = m.range.str.replace(" ", "")
      val maxLen = math.min(all.length, pfxRange.end)
      var found = false
      var currLen = maxLen
      var res:List[Long] = Nil
      while (!found && currLen >= pfxRange.start) {
        val currPfx = all.substring(0, currLen)
        if (pfx2IdMap.contains(currPfx)) {
          found = true
          res = List(pfx2IdMap(currPfx))
        }
        currLen -= 1
      }
      res
    }
  }

   val gbPocoPfxXtr:TXtr[Long] = pocoXtr4TagPfx(
     pocogbPfx2GNid, PocoTags.pocoTag(PocoData.CC_GB)
   )

}
