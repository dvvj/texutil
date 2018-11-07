package org.ditw.textSeg.catSegMatchers
import org.ditw.textSeg.common.CatSegMatchers.{Category, SegMatcher4Cat, TSegMatchers4Cat}
import org.ditw.textSeg.common.Tags._
import org.ditw.textSeg.common.Vocabs._

object Cat2SegMatchers {

//  private val tmUniv = ngramT(
//    splitVocabEntries(_UnivWords),
//    _Dict,
//    TagGroup4Univ.segTag
//  )
//
//  private val segUniv = segByPfxSfx(
//    Set(TagTmUniv), _SegSfxs,
//    true,
//    TagGroup4Univ
//  )
//
//  private[textSeg] val segMatchers = new TSegMatchers4Cat {
//    override def cat: Category = Category.Univ
//    override def tms: List[TTkMatcher] = List(tmUniv)
//    override def cms: List[TCompMatcher] = List(segUniv)
//  }
  val segMatchers = new SegMatcher4Cat(
    cat = Category.Univ,
    tagGroup = TagGroup4Univ,
    keywords = _UnivWords,
    gazWords = _UnivGazWords,
    stopKeywords = _UnivStopWords,
    segStopWordsLeft = _UnivSegStopWordsLeft,
    segStopWordsRight = _UnivSegStopWordsRight,
  true
  )
}
