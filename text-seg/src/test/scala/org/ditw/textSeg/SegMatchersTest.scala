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
  private val tm = ngram(Set(Array("2")), _Dict, tag2)
  private val punctSet = Set(",", ";")
  private val tagSeg = "tagSeg"
  private val segMatcher = segByPfxSfx(
    Set(tag2.get),
    punctSet,
    tagSeg
  )
  private val mmgr = new MatcherMgr(
    List(tm),
    List(segMatcher)
  )

  private val segMatcherTestData = Table(
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

  "SegMatcher tests" should "pass" in {
    forAll(segMatcherTestData) { (inStr, expRes) =>
      val matchPool = MatchPool.fromStr(inStr, TknrTextSeg, _Dict)
      mmgr.run(matchPool)
      val expRanges = expRes.map { tp =>
        TkRange(matchPool.input, tp._1, tp._2, tp._3)
      }
      val resRanges = matchPool.get(tagSeg).map(_.range)
      resRanges shouldBe expRanges
    }
  }

}
