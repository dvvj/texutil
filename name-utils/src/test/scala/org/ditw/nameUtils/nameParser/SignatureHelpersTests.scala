package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult
import org.ditw.nameUtils.nameParser.SignatureHelpers.genSignature
import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-04.
  */
class SignatureHelpersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import TestHelpers._
  val chineseTestData = Table(
    ("result", "sig"),
    (
      chineseResult("Sun", IndexedSeq("Ai", "Jun")),
      "ZHO:AJ"
    ),
    (
      chineseResult("Xi", IndexedSeq("Jin", "ping")),
      "ZHO:JP"
    ),
    (
      chineseResult("Xi", IndexedSeq("jin", "ping")),
      "ZHO:JP"
    ),
    (
      chineseResult("Xi", IndexedSeq("jin")),
      "ZHO:J"
    ),
    (
      chineseResult("Xi", IndexedSeq()),
      "ZHO:"
    )
  )

  val dutchTestData = Table(
    ("result", "sig"),
    (
      dutchResult("Van Den Berg", IndexedSeq("Leonard"), Option("Van Den"), Option(IndexedSeq("H"))),
      "NLD:LH"
    ),
    (
      dutchResult("van der Meer", IndexedSeq("Jan"), Option("van der"), Option(IndexedSeq("M"))),
      "NLD:JM"
    ),
    (
      dutchResult("van der Meer", IndexedSeq("Jan"), Option("van der"), Option(IndexedSeq("de", "Tult"))),
      "NLD:JT"
    )
  )

  private def runTest(result:NormResult, sig:String):Unit = {
    val s = genSignature(result)
    s shouldBe sig
  }

  @Test(groups = Array("UNIT_TEST"))
  def chineseSignatureTests() {
    forAll(chineseTestData)(runTest)
  }

  @Test(groups = Array("UNIT_TEST"))
  def dutchSignatureTests() {
    forAll(dutchTestData)(runTest)
  }

}
