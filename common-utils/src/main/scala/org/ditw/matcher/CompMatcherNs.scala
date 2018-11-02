package org.ditw.matcher

object CompMatcherNs {
  // ------------ OR
  private[matcher] class CmOr(
    protected val subMatchers:Set[TCompMatcher],
    val tag:Option[String]
  ) extends TCompMatcher with TCompMatcherN {
    override def runAtLineFrom(
      matchPool: MatchPool,
      lineIdx: Int,
      start: Int): Set[TkMatch] = {
      subMatchers.flatMap(_.runAtLineFrom(matchPool, lineIdx, start))
    }
  }

  def or(subMatchers:Set[TCompMatcher], tag:String):TCompMatcher = {
    new CmOr(subMatchers, Option(tag))
  }

  // ------------ Seq
  private[matcher] abstract class CmSeq
}
