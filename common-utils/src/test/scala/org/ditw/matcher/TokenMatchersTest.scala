package org.ditw.matcher
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import TokenMatchers._
import org.ditw.tknr.TestHelpers._
import MatchPool._
import org.ditw.common.TkRange
class TokenMatchersTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {
  private val ngram1 = ngramSplit(
    Set(
      "Cardiovascular Research",
      "University"
    ),
    dict
  )
  private val testData = Table(
    ("ngram", "input", "expSet"),
    (
      ngram1,
      "Cardiovascular Research, Vrije University, Amsterdam",
      Set(
        (0, 0, 2),
        (0, 3, 4)
      )
    ),
    (
      ngram1,
      "Cardiovascular Research,\n Vrije University, Amsterdam",
      Set(
        (0, 0, 2),
        (1, 1, 2)
      )
    )
  )

  "NGram matcher test" should "pass" in {
    forAll(testData) { (ngram, inStr, expRanges) =>
      val matchPool = fromStr(inStr, testTokenizer, dict)
      val res = ngram.run(matchPool).map(_.range)
      val expRngs = expRanges.map(tp => TkRange(matchPool.input, tp._1, tp._2, tp._3))
      res shouldBe expRngs
    }
  }
}
