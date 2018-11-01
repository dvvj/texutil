package org.ditw.matcher
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import TokenMatchers._
import org.ditw.tknr.TestHelpers._
import MatchPool._
class TokenMatchersTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {
  private val testData = Table(
    ("ngram", "input", "expSet"),
    (
      ngramSplit(
        Set(
          "Cardiovascular Research",
          "University"
        ),
        dict
      ),
      "Cardiovascular Research, Vrije University, Amsterdam",
      Set(
        (0, 0, 2),
        (0, 3, 4)
      )
    )
  )

  "NGram matcher test" should "pass" in {
    forAll(testData) { (ngram, inStr, expRanges) =>
      val matchPool = fromStr(inStr, testTokenizer, dict)
      val res = ngram.run(matchPool)
      res.size shouldBe expRanges.size
    }
  }
}
