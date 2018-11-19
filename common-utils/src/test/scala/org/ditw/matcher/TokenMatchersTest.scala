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

  private val quotedNGramTag = "quotedNGramTag"
  private val quotedNGram = suffixedBy(
    pfxNGram,
    Set("\""),
    Option(quotedNGramTag)
  )

  private val ngramDTag = "ngramDTag"
  private val ngramD = ngramExtraTag(
    Map(
      "Cardiovascular Research" -> "T1",
      "Research Vrije" -> "T2",
      "Vrije" -> "T3"
    ),
    dict,
    (m, tag) => {
      m.addTag(tag)
      m
    },
    ngramDTag
  )

  private val noTag = Set[String]()

  private val testData = Table(
    ("ngram", "input", "expSet"),
    (
      ngramD,
      "Cardiovascular Research, Vrije University, Amsterdam",
      Set(
        (0, 0, 2) -> Set(ngramDTag, "T1"),
        (0, 1, 3) -> Set(ngramDTag, "T2"),
        (0, 2, 3) -> Set(ngramDTag, "T3")
      )
    ),
    (
      quotedNGram,
      "\"Cardiovascular Research\" department, X \"University",
      Set(
        (0, 0, 2) -> Set(quotedNGramTag)
      )
    ),
    (
      pfxNGram,
      "\"Cardiovascular Research\" department, X University",
      Set(
        (0, 0, 2) -> noTag
      )
    ),
    (
      pfxNGram,
      "\"Cardiovascular Research\" department, X \"University",
      Set(
        (0, 0, 2) -> noTag,
        (0, 4, 5) -> noTag
      )
    ),
    (
      regexD3Plus,
      "12 123 1234",
      Set(
        (0, 1, 2) -> noTag,
        (0, 2, 3) -> noTag
      )
    ),
    (
      ngram1,
      "Cardiovascular Research, Vrije University, Amsterdam",
      Set(
        (0, 0, 2) -> noTag,
        (0, 3, 4) -> noTag
      )
    ),
    (
      ngram1,
      "Cardiovascular Research,\n Vrije University, Amsterdam",
      Set(
        (0, 0, 2) -> noTag,
        (1, 1, 2) -> noTag
      )
    )
  )

  import org.ditw.tknr.TknrHelpers._

  "NGram matcher test" should "pass" in {
    forAll(testData) { (tm, inStr, expRangeTags) =>
      val matchPool = fromStr(inStr, testTokenizer, dict)
      val res0 = tm.run(matchPool)
      val res = res0.map(r => r.range -> r.getTags)
      //val expRanges = expRangeTags.map(_._1)
      val expRng2Tags = expRangeTags.map { p =>
        val rng = rangeFromTp3(matchPool.input, p._1)
        val tags = p._2
        rng -> tags
      }
      res shouldBe expRng2Tags
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
