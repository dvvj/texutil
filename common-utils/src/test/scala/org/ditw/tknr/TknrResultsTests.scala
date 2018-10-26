package org.ditw.tknr

import org.ditw.tknr.TknrResults.{LineResult, TknrResult}
import org.ditw.tknr.Tokenizers.TokenizerSettings
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2018-10-26.
  */
class TknrResultsTests
    extends FlatSpec
    with Matchers
    with TableDrivenPropertyChecks {
  private val trimByCommaColon = Trimmers.byChars(Set(',', ';'))

  private val settings = TokenizerSettings(
    "\\n+",
    "[\\s]+",
    List(),
    trimByCommaColon
  )

  private val testStr1 = "Cardiovascular Research, Vrije University, Amsterdam"
  private val testStr2 = "Cardiovascular Research,\nVrije University, Amsterdam"
  private val testStr3 =
    "Cardiovascular Research,\n Vrije University, Amsterdam"

  private val testData = Table(
    ("settings", "input", "expLineResults"),
    (
      settings,
      testStr3,
      IndexedSeq(
        new LineResult(
          IndexedSeq(
            "Cardiovascular",
            "Research",
            "Vrije",
            "University",
            "Amsterdam"
          ),
          testStr3
        )
      )
    ),
    (
      settings,
      testStr1,
      IndexedSeq(
        new LineResult(
          IndexedSeq(
            "Cardiovascular",
            "Research",
            "Vrije",
            "University",
            "Amsterdam"
          ),
          testStr1
        )
      )
    ),
    (
      settings,
      testStr2,
      IndexedSeq(
        new LineResult(
          IndexedSeq(
            "Cardiovascular",
            "Research",
            "Vrije",
            "University",
            "Amsterdam"
          ),
          testStr2
        )
      )
    )
  )

  "tokenizer tests" should "pass" in {
    forAll(testData) { (settings, input, expLineResults) =>
      val tokenizer = Tokenizers.load(settings)
      val res = tokenizer.run(input)
      val expRes = TknrResult(expLineResults)
      res.lineResults.size shouldBe expRes.lineResults.size
    //res shouldBe expResult
    }
  }
}
