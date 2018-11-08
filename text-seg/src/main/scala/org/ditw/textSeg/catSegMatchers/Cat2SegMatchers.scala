package org.ditw.textSeg.catSegMatchers
import org.ditw.common.{InputHelpers, ResourceHelpers}
import org.ditw.matcher.{CompMatcherNs, MatcherMgr}
import org.ditw.matcher.TokenMatchers.ngramT
import org.ditw.textSeg.SegMatchers._
import org.ditw.textSeg.Settings
import org.ditw.textSeg.common.CatSegMatchers.{Category, SegMatcher4Cat, TSegMatchers4Cat}
import org.ditw.textSeg.common.Tags._
import org.ditw.textSeg.common.Vocabs
import org.ditw.textSeg.common.Vocabs._

object Cat2SegMatchers {
  import org.ditw.matcher.CompMatchers

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
  private[textSeg] val tagTmUnivOf = customTmTag("UnivOfVocab")
  private[textSeg] val tmUnivOf = ngramT(
    InputHelpers.splitVocabEntries(Vocabs.__univOfVocab),
    AllVocabDict,
    tagTmUnivOf
  )

  private val _canBeStart = true
  private[textSeg] val tagCmUnivOf = customCmTag("UnivOfVocab")
  private val cmUnivOf = CompMatcherNs.lngOfTags(
    IndexedSeq(TagGroup4Univ.keywordTag, TmOf, tagTmUnivOf),
    tagCmUnivOf
  )
  private[textSeg] val tagSegUnivOf = customCmTag("SegUnivOf")
  private[textSeg] val tagSegUnivOfVocab = customCmTag("SegUnivOfVocab")
  private[textSeg] val segUnivOf = segByPfxSfx(
    Set(tagCmUnivOf), _SegPfxs, _SegSfxs,
    _canBeStart,
    tagSegUnivOf
  )
  import CompMatchers._
  private[textSeg] val segUnivOfVocab =
    endWithTags(
      byTag(tagSegUnivOf),
      Set(tagTmUnivOf),
      tagSegUnivOfVocab
  )

  private[textSeg] val univOfVocabOverride = MatcherMgr.postProcOverride(
    Map(
      tagSegUnivOfVocab -> TagGroup4Univ.segTag
    )
  )

  val segMatchers = new SegMatcher4Cat(
    cat = Category.Univ,
    tagGroup = TagGroup4Univ,
    keywords = _UnivWords,
    gazWords = _UnivGazWords,
    stopKeywords = _UnivStopWords,
    segStopWordsLeft = _UnivSegStopWordsLeft,
    segStopWordsRight = _UnivSegStopWordsRight,
    _canBeStart,
    List(tmUnivOf),
    List(cmUnivOf, segUnivOf, segUnivOfVocab),
    List(univOfVocabOverride)
  )
}
