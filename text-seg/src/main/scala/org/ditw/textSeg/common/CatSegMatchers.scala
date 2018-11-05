package org.ditw.textSeg.common
import org.ditw.common.InputHelpers.splitVocabEntries
import org.ditw.matcher.TokenMatchers.ngramT
import org.ditw.matcher.{TCompMatcher, TTkMatcher}
import org.ditw.textSeg.SegMatchers._
import org.ditw.textSeg.common.Tags.TagGroup
import org.ditw.textSeg.common.Vocabs.{_Dict, _SegSfxs, _UnivWords}

import scala.collection.mutable.ListBuffer

object CatSegMatchers {

  object Category extends Enumeration {
    type Category = Value
    val Univ, Corp = Value
  }

  import Category._

  private [textSeg] trait TSegMatchers4Cat extends Serializable {
    def cat:Category
    protected def tms:List[TTkMatcher]
    protected def cms:List[TCompMatcher]
  }

  private val EmptyTags = Set[String]()
  private [textSeg] class SegMatcher4Cat(
    val cat:Category,
    keywords:Set[String],
    segStopWordsLeft:Set[String],
    segStopWordsRight:Set[String],
    tagGroup:TagGroup,
    canBeStart:Boolean
  ) extends TSegMatchers4Cat {

    protected val tms:List[TTkMatcher] = {
      val t = ListBuffer[TTkMatcher](
        ngramT(
          splitVocabEntries(keywords),
          _Dict,
          tagGroup.keywordTag
        )
      )
      if (segStopWordsLeft.nonEmpty) {
        t += ngramT(
          splitVocabEntries(segStopWordsLeft),
          _Dict,
          tagGroup.segLeftStopTag
        )
      }
      if (segStopWordsRight.nonEmpty) {
        t += ngramT(
          splitVocabEntries(segStopWordsRight),
          _Dict,
          tagGroup.segRightStopTag
        )
      }
      t.toList
    }

    protected val cms:List[TCompMatcher] = {
      var t = segByPfxSfx(
        Set(tagGroup.keywordTag), _SegSfxs,
        canBeStart,
        tagGroup.segTag
      )

      val leftStopTags =
        if (segStopWordsLeft.nonEmpty) Set(tagGroup.segLeftStopTag)
        else EmptyTags
      val rightStopTags =
        if (segStopWordsRight.nonEmpty) Set(tagGroup.segRightStopTag)
        else EmptyTags

      if (leftStopTags.nonEmpty || rightStopTags.nonEmpty) {
        t = segByTags(
          t, leftStopTags, rightStopTags, tagGroup.segTag
        )
      }
      List(
        t
      )
    }
  }
}
