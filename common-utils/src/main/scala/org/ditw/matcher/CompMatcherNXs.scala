package org.ditw.matcher
import org.ditw.common.TkRange
import org.ditw.matcher.CompMatcherNs.TCompMatcherSeq

object CompMatcherNXs extends Serializable {
  import CompMatchers._

  private val EmptyMatches = Set[TkMatch]()

  private[matcher] class CmLookAround (
    private val sfx:Set[String],
    private val sfxCounts:(Int, Int),
    override protected val subMatchers:Iterable[TCompMatcher],
    val tag:Option[String]
  ) extends TCompMatcher with TCompMatcherN with TDefRunAtLineFrom {
//    protected val subMatchers:Iterable[TCompMatcher] = Iterable(mCenter, m2Lookfor)
    private val mCenter:TCompMatcher = subMatchers.head
    private val m2Lookfor:TCompMatcher = subMatchers.tail.head

    override def runAtLine(
      matchPool: MatchPool,
      lineIdx: Int): Set[TkMatch] = {
      val ctMatches = mCenter.runAtLine(matchPool, lineIdx)
      val lookforMatches = m2Lookfor.runAtLine(matchPool, lineIdx)
      ctMatches.flatMap { m =>
        val rangeBySfx = matchPool.input.linesOfTokens(lineIdx).rangeBySfxs(m.range, sfx, sfxCounts)
        lookforMatches.filter { lm => rangeBySfx.covers(lm.range) && !lm.range.overlap(m.range) }
          .map { lm =>
            val mseq = if (m.range.start < lm.range.start) IndexedSeq(m, lm)
            else IndexedSeq(lm, m)
            TkMatch.fromChildren(mseq)
          }
      }
    }
  }

  def sfxLookAround(
    sfx:Set[String],
    sfxCounts:(Int, Int),
    mCenter:TCompMatcher,
    m2Lookfor:TCompMatcher,
    tag:String
  ):TCompMatcher = {
    new CmLookAround(sfx, sfxCounts, List(mCenter, m2Lookfor), Option(tag))
  }


  def sfxLookAroundByTag(
    sfx:Set[String],
    sfxCounts:(Int, Int),
    mCenterTag:String,
    m2LookforTag:String,
    tag:String
  ):TCompMatcher = {
    sfxLookAround(sfx,
      sfxCounts,
      CompMatchers.byTag(mCenterTag),
      CompMatchers.byTag(m2LookforTag),
      tag
    )
  }
}
