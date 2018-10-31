package org.ditw.tknr

import org.ditw.common.{Dict, InputHelpers}
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

  import TestHelpers._
  private val testStr1 = "Cardiovascular Research, Vrije University, Amsterdam"
  private val tokenContent1 = IndexedSeq(
    IndexedSeq(
      noPfxSfx("Cardiovascular"),
      commaSfx("Research"),
      noPfxSfx("Vrije"),
      commaSfx("University"),
      noPfxSfx("Amsterdam")
    )

  )
  private val testStr2 = "Cardiovascular Research,\nVrije University, Amsterdam"
  private val tokenContent2 = IndexedSeq(
    IndexedSeq(
      noPfxSfx("Cardiovascular"),
      commaSfx("Research")
    ),
    IndexedSeq(
      noPfxSfx("Vrije"),
      commaSfx("University"),
      noPfxSfx("Amsterdam")
    )
  )
  private val testStr3 =
    "Cardiovascular Research,\n Vrije University, Amsterdam"
  private val tokenContent3 = IndexedSeq(
    IndexedSeq(
      noPfxSfx("Cardiovascular"),
      commaSfx("Research")
    ),
    IndexedSeq(
      noPfxSfx("Vrije"),
      commaSfx("University"),
      noPfxSfx("Amsterdam")
    )
  )

  private val testData = Table(
    ("settings", "input", "expRes"),
    (
      settings,
      testStr1,
      resultFrom(
        testStr1,
        dict,
        tokenContent1.map(loTFrom)
      )
    ),
    (
      settings,
      testStr2,
      resultFrom(
        testStr2,
        dict,
        tokenContent2.map(loTFrom)
      )
    ),
    (
      settings,
      testStr3,
      resultFrom(
        testStr3,
        dict,
        tokenContent3.map(loTFrom)
      )
    )
  )

  "tokenizer tests" should "pass" in {
    forAll(testData) { (settings, input, expRes) =>
      val tokenizer = Tokenizers.load(settings)
      val res = tokenizer.run(input, dict)

//      val expLrs = expLineTokens.map()
//      val expRes = TknrResult(expLineResults)
//      res.lineResults.size shouldBe expRes.lineResults.size
      //res.linesOfTokens.size shouldBe expRes.linesOfTokens.size
      resEqual(res, expRes) shouldBe true
    }
  }
}
