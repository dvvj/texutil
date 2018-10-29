package org.ditw.tknr

import org.ditw.tknr.TknrResults.{TknrResult}
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
        IndexedSeq(
          "Cardiovascular",
          "Research",
          "Vrije",
          "University",
          "Amsterdam"
        )
      )
    ),
    (
      settings,
      testStr1,
      IndexedSeq(
        IndexedSeq(
          "Cardiovascular",
          "Research"
        ),
        IndexedSeq(
          "Vrije",
          "University",
          "Amsterdam"
        )
      )
    ),
    (
      settings,
      testStr2,
      IndexedSeq(
        IndexedSeq(
          "Cardiovascular",
          "Research"
        ),
        IndexedSeq(
          "Vrije",
          "University",
          "Amsterdam"
        )
      )
    )
  )

  "tokenizer tests" should "pass" in {
    forAll(testData) { (settings, input, expLineTokens) =>
      val tokenizer = Tokenizers.load(settings)
      //val res = tokenizer.run(input)

//      val expLrs = expLineTokens.map()
//      val expRes = TknrResult(expLineResults)
//      res.lineResults.size shouldBe expRes.lineResults.size
    //res shouldBe expResult
    }
  }
}
