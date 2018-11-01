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

  private val regexD3Plus = TokenMatchers.regex(
    "\\d{3,}"
  )
  private val pfxNGram = prefixedBy(
    ngram1,
    Set("\"")
  )
  private val quotedNGram = suffixedBy(
    pfxNGram,
    Set("\"")
  )
  private val testData = Table(
    ("ngram", "input", "expSet"),
    (
      quotedNGram,
      "\"Cardiovascular Research\" department, X \"University",
      Set(
        (0, 0, 2)
      )
    ),
    (
      pfxNGram,
      "\"Cardiovascular Research\" department, X University",
      Set(
        (0, 0, 2)
      )
    ),
    (
      pfxNGram,
      "\"Cardiovascular Research\" department, X \"University",
      Set(
        (0, 0, 2),
        (0, 4, 5)
      )
    ),
    (
      regexD3Plus,
      "12 123 1234",
      Set(
        (0, 1, 2),
        (0, 2, 3)
      )
    ),
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
    forAll(testData) { (tm, inStr, expRanges) =>
      val matchPool = fromStr(inStr, testTokenizer, dict)
      val res = tm.run(matchPool).map(_.range)
      val expRngs = expRanges.map(tp => TkRange(matchPool.input, tp._1, tp._2, tp._3))
      res shouldBe expRngs
    }
  }

  "Unknown token in ngram" should "throw Exception" in {
    val caught = intercept[IllegalArgumentException] {
      val ngramException = ngramSplit(
        Set(
          "Cardiovascular Research",
          "University11"
        ),
        dict
      )
    }
    caught.getMessage shouldBe "Token [University11] not found in Dictionary"
  }
}
