package org.ditw.nameUtils.nameParser

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-08.
  */
class PaymentParsersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import TestHelpers._
  import ParserHelpers._
  val testData = Table(
    ("in", "res"),
    (
      IndexedSeq("JIA",	"Y",	"Wen", "Jr"),
      Option(chineseResult(
        "Wen",
        IndexedSeq("JIA", "Y")
      ))
    ),
    (
      IndexedSeq("",	"",	"", ""),
      None
    ),
    (
      IndexedSeq("Peter",	"",	"", ""),
      None
    ),
    (
      IndexedSeq("Peter",	"Leonid",	"", ""),
      None
    ),
    (
      IndexedSeq("Peter",	"Leonid",	"", "Jr"),
      None
    ),
    (
      IndexedSeq("JIA",	"Y",	"Wen", ""),
      Option(chineseResult(
        "Wen",
        IndexedSeq("JIA", "Y")
      ))
    ),
    (
      IndexedSeq("Peter",	"",	"Van De Althausen", ""),
      Option(dutchResult(
        "Van De Althausen",
        IndexedSeq("Peter"),
        Option("Van De"),
        None
      ))
    ),
    (
      IndexedSeq("Peter",	"Leonid",	"Van De Althausen", "MD"),
      Option(dutchResult(
        "Van De Althausen",
        IndexedSeq("Peter"),
        Option("Van De"),
        Option(IndexedSeq("Leonid"))
      ))
    ),
    (
      IndexedSeq("Peter",	"Leonid",	"Van De Althausen", "Jr"),
      Option(dutchResultWithSuffix(
        "Van De Althausen",
        IndexedSeq("Peter"),
        Option("Van De"),
        "Jr",
        Option(IndexedSeq("Leonid"))
      ))
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def parserTests() {
    forAll(testData) { (in, res) =>
      val fn = in(0)
      val mn = in(1)
      val ln = in(2)
      val su = in(3)
      val r = parsePaymentName(fn, mn, ln, su)
      if (res.isEmpty) r.isEmpty shouldBe true
      else {
        val t = r.get._2
        t.lastNames shouldBe res.get.lastNames
        t.lang shouldBe res.get.lang
        t.attrs shouldBe res.get.attrs
      }
    }
  }

  val parserJTestData = Table(
    ("in", "result"),
    (
      Array("DEBORAH","T.","GLASSMAN", ""),
      Array("deborah", "t", "glassman", "dt")
    )
  )

//  @Test(groups = Array("TO_ADD"))
//  def parserJTests() {
//    forAll(parserJTestData) { (in, result) =>
//      val r = parsePaymentNameJ(in)
//      Array(r.firstName, r.middleName, r.lastName, r.signature) shouldBe result
//    }
//  }

  val EmptyNameParts = Array("", "", "", "")
  val testData4PhysicianProfileNames = Table(
    ("nameParts", "altNameParts", "resultLastName", "resultFirstName"),
    (
      Array("JULIA", "B.", "BARBAGALLO", ""),
      EmptyNameParts,
      "barbagallo",
      "julia"
    ),
    (
      Array("ANTHONY", "H", "WEISS", ""),
      Array("ANTHONY", "A", "WEISS", ""),
      "weiss",
      "anthony"
    ),
    (
      Array("CHONG XIAN", "", "PAN", ""),
      Array("CHONG-XIAN", "", "PAN", ""),
      "PAN",
      "CHONG XIAN"
    ),
    (
      Array("ANIRUDH", "", "MASAND-RAI", ""),
      EmptyNameParts,
      "masand-rai",
      "anirudh"
    ),
    (
      Array("BERNARDO", "", "STEIN", ""),
      Array("BERNARDO", "", "STEIN ROSEN", ""),
      "rosen",
      "bernardo"
    ),
    (
      Array("ELEFTHERIA", "", "MARATOS", ""),
      Array("ELEFTHERIA", "", "MARATOS-FLIER", ""),
      "maratos-flier",
      "eleftheria"
    )
//    (
//      Array("DANIEL", "V", "DO", ""),
//      Array("DANIEL", "", "DO", ""),
//      "do",
//      "daniel"
//    )
//    (
//      Array("CYNTHIA", "", "MA", ""),
//      Array("CYNTHIA", "XIUGUANG", "MA", ""),
//      "ma",
//      "cynthia"
//    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def physicianProfileNamesParsingTests() {
    forAll(testData4PhysicianProfileNames) { (np, anp, rln, rfn) =>
      val r = ParserHelpers.parsePaymentNamesInPhysicianProfileJ(np, anp)
      r.lastName shouldBe rln
      r.firstName shouldBe rfn
    }
  }
}
