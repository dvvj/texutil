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
  private val testStr4 =
    "\"Angelo Nocivelli\" Institute for Molecular Medicine"
  private val tokenContent4 = IndexedSeq(
    IndexedSeq(
      noSfx("Angelo", "\""),
      noPfx("Nocivelli", "\""),
      noPfxSfx("Institute"),
      noPfxSfx("for"),
      noPfxSfx("Molecular"),
      noPfxSfx("Medicine")
    )
  )

  private val testStr5 =
    "(Formerly) Department of Anthropology"
  private val tokenContent5 = IndexedSeq(
    IndexedSeq(
      IndexedSeq("Formerly", "(", ")"),
      noPfxSfx("Department"),
      noPfxSfx("of"),
      noPfxSfx("Anthropology")
    )
  )

  private val testStr6 =
    "*Auburn University (Professor Emeritus), Auburn, AL;"
  private val tokenContent6 = IndexedSeq(
    IndexedSeq(
      noSfx("Auburn", "*"),
      noPfxSfx("University"),
      noPfxSfx("Department"),
      noSfx("Professor", "("),
      noPfx("Emeritus", "),"),
      noPfx("Auburn", ","),
      noPfx("AL", ";")
    )
  )

  private val testData = Table(
    ("settings", "input", "expRes"),
    (
      testTokenizer,
      testStr6,
      resultFrom(
        testStr6,
        dict,
        tokenContent6.map(loTFrom)
      )
    ),
    (
      testTokenizer,
      testStr5,
      resultFrom(
        testStr5,
        dict,
        tokenContent5.map(loTFrom)
      )
    ),
    (
      testTokenizer,
      testStr4,
      resultFrom(
        testStr4,
        dict,
        tokenContent4.map(loTFrom)
      )
    ),
    (
      testTokenizer,
      testStr1,
      resultFrom(
        testStr1,
        dict,
        tokenContent1.map(loTFrom)
      )
    ),
    (
      testTokenizer,
      testStr2,
      resultFrom(
        testStr2,
        dict,
        tokenContent2.map(loTFrom)
      )
    ),
    (
      testTokenizer,
      testStr3,
      resultFrom(
        testStr3,
        dict,
        tokenContent3.map(loTFrom)
      )
    )
  )

  "tokenizer tests" should "pass" in {
    forAll(testData) { (tokenizer, input, expRes) =>
      val res = tokenizer.run(input, dict)

//      val expLrs = expLineTokens.map()
//      val expRes = TknrResult(expLineResults)
//      res.lineResults.size shouldBe expRes.lineResults.size
      //res.linesOfTokens.size shouldBe expRes.linesOfTokens.size
      resEqual(res, expRes) shouldBe true
    }
  }
}
