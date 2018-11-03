package org.ditw.textSeg.cat1
import org.ditw.matcher.{TCompMatcher, TTkMatcher}

object Cat1SegMatchers {

  import org.ditw.matcher.TokenMatchers._
  import org.ditw.common.InputHelpers._
  import org.ditw.textSeg.SegMatchers._
  import org.ditw.textSeg.common.Vocabs._
  import org.ditw.textSeg.common.Tags._
  private val tmCorp = ngramT(
    splitVocabEntries(_CorpWords),
    _Dict,
    TagTmCorp
  )
  private val segCorp = segByPfxSfx(
    Set(TagTmCorp), _SegSfxs,
    false,
    TagSegCorp
  )

  def catTms():List[TTkMatcher] = List(tmCorp)
  def catCms():List[TCompMatcher] = List(segCorp)
}
