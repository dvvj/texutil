package org.ditw.textSeg
import org.ditw.common.TkRange
import org.ditw.matcher.{MatchPool, MatcherMgr, TokenMatchers}
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class SegMatchersTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  import TokenMatchers._
  import TestHelpers._
  import Settings._
  import SegMatchers._
  private val tag2 = Option("tag2")
  private val tag3 = Option("tag3")
  private val tm2 = ngram(Set(Array("2")), _Dict, tag2)
  private val tm3 = ngram(Set(Array("3")), _Dict, tag3)
  private val punctSet = Set(",", ";")
  private val tagSegByPfxSfx = "tagSegByPfxSfx"
  private val tagByTagsMatcherLeftOnly = "tagByTagsMatcherLeftOnly"
  private val segByPfxSfxMatcher = segByPfxSfx(
    Set(tag2.get),
    punctSet,
    true,
    tagSegByPfxSfx
  )
  private val segByTagsMatcherLeftOnly = segByTags(
    segByPfxSfxMatcher,
    tag3.toSet,
    Set(),
    tagByTagsMatcherLeftOnly
  )
  private val mmgr = new MatcherMgr(
    List(tm2, tm3),
    List(segByPfxSfxMatcher, segByTagsMatcherLeftOnly),
    List()
  )

  private val segByPfxSfxTestData = Table(
    ("inStr", "expRes"),
    (
      "1, 3, 2 ,\n2 4, 1",
      Set(
        (0, 2, 3),
        (1, 0, 2)
      )
    ),
    (
      "1, 3, 2,\n2 4, 1",
      Set(
        (0, 2, 3),
        (1, 0, 2)
      )
    ),
    (
      "1, 3, 2\n2 4, 1",
      Set(
        (0, 2, 3),
        (1, 0, 2)
      )
    ),
    (
      "1, 3, 4\n2 4, 1",
      Set(
        (1, 0, 2)
      )
    ),
    (
      "1, 3, 4\n2 4",
      Set(
        (1, 0, 2)
      )
    ),
    (
      "1, 3, 4\n1, 2 4",
      Set(
        (1, 1, 3)
      )
    ),
    (
      "1, 2 3, 4\n1, 2 4",
      Set(
        (0, 1, 3),
        (1, 1, 3)
      )
    ),
    (
      ", 2 3 , 4",
      Set(
        (0, 1, 3)
      )
    ),
    (
      ", 2 3, 4",
      Set(
        (0, 1, 3)
      )
    ),
    (
      "1, 2 3  4",
      Set(
        (0, 1, 4)
      )
    ),
    (
      "1, 2, 3, 4",
      Set(
        (0, 1, 2)
      )
    ),
    (
      "2, 3, 4",
      Set(
        (0, 0, 1)
      )
    ),
    (
      " 2, 3, 4",
      Set(
        (0, 0, 1)
      )
    ),
    (
      "1, 2 3, 4",
      Set(
        (0, 1, 3)
      )
    )
  )

  "SegByPfxSfx Matcher tests" should "pass" in {
    forAll(segByPfxSfxTestData) { (inStr, expRes) =>
      val matchPool = MatchPool.fromStr(inStr, TknrTextSeg, _Dict)
      mmgr.run(matchPool)
      val expRanges = expRes.map { tp =>
        TkRange(matchPool.input, tp._1, tp._2, tp._3)
      }
      val resRanges = matchPool.get(tagSegByPfxSfx).map(_.range)
      resRanges shouldBe expRanges
    }
  }

  private val segByTagsTestData = Table(
    ("inStr", "segMatcher", "expRes"),
    (
      "1, 3 2 ,\n2 4, 1",
      segByTagsMatcherLeftOnly,
      Set(
        (0, 2, 3),
        (1, 0, 2)
      )
    ),
    (
      "1, 3, 2 ,\n2 4, 1",
      segByTagsMatcherLeftOnly,
      Set(
        (0, 2, 3),
        (1, 0, 2)
      )
    )
  )

  "SegByTags Matcher tests" should "pass" in {
    forAll(segByTagsTestData) { (inStr, segMatcher, expRes) =>
      val matchPool = MatchPool.fromStr(inStr, TknrTextSeg, _Dict)
      mmgr.run(matchPool)
      val expRanges = expRes.map { tp =>
        TkRange(matchPool.input, tp._1, tp._2, tp._3)
      }
      val resRanges = matchPool.get(segMatcher.tag.get).map(_.range)
      resRanges shouldBe expRanges
    }
  }

}
