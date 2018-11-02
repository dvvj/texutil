package org.ditw.textSeg
import org.ditw.matcher.CompMatchers.TDefRunAtLineFrom
import org.ditw.matcher.{MatchPool, TCompMatcher, TkMatch}

object SegMatchers {

  private[textSeg] class SegByPSfx(
    private val tagsContained:Set[String],
    private val sfxs:Set[String],
    val tag:Option[String]
  ) extends TCompMatcher with TDefRunAtLineFrom {
    override def runAtLine(
      matchPool: MatchPool,
      lineIdx: Int): Set[TkMatch] = {
      val matches = matchPool.get(tagsContained)
      val segMatches = matches.map { m =>
        val lot = matchPool.input.linesOfTokens(m.range.lineIdx)
        val newRange = lot.rangeBy(m.range, sfxs)
        new TkMatch(newRange, IndexedSeq(m))
      }
      segMatches
    }

    override def getRefTags(): Set[String] = tagsContained
  }

  def segByPfxSfx(
    tagsContained:Set[String],
    sfxs:Set[String],
    tag:String
  ):TCompMatcher = {
    new SegByPSfx(tagsContained, sfxs, Option(tag))
  }
}
