package org.ditw.nameUtils.nameParser

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-17.
  */
class LastNameParsersTest extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import LastNameParsers._
  import LastNameParsers.Consts._
  private def noPrefixNorSuffixParseResult(in:String) = {
    val comps = ParserHelpers.splitComponents(in)
    lastNameParseResult(in, comps, IndexedSeq(comps.indices), NoPrefix, None)
  }
  private def chinseNameParseResult(in:String) =
    noPrefixNorSuffixParseResult(in)

  val chineseLastNameParserSuffixTestData = Table(
    ("in", "exp"),
    (
      "Xi",
      chinseNameParseResult("Xi")
    ),
    (
      "Ouyang",
      chinseNameParseResult("Ouyang")
    ),
    (
      "Ximen",
      chinseNameParseResult("Ximen")
    )
//    (
//      "Men Xi",
//      chinseNameParseResult("Men Xi")
//    )

  )

  val chineseLastNameParserNobleTitlesTestData = Table(
    ("in", "exp"),
    (
      "Di",
      chinseNameParseResult("Di")
    ),
    (
      "Xi",
      chinseNameParseResult("Xi")
    )

  )

  @Test(groups = Array("UnitTest") )
  def chineseLastNameParserSuffixTests() {
    forAll(chineseLastNameParserSuffixTestData) { (in, exp) =>
      val r = ChineseLastNameParser.parse(in)
      r shouldBe exp
    }
  }

  @Test(groups = Array("UnitTest") )
  def chineseLastNameParserNobleTitleTests() {
    forAll(chineseLastNameParserNobleTitlesTestData) { (in, exp) =>
      val r = ChineseLastNameParser.parse(in)
      r shouldBe exp
    }
  }

  val generalLastNameParserSuffixTestData = Table(
    ("in", "exp"),
    (
      "Xi",
      noPrefixNorSuffixParseResult("Xi")
    ),
    (
      "Xi Men",
      noPrefixNorSuffixParseResult("Xi Men")
    ),
    ( // Xi should be treated as suffix
      "Men Xi",
      lastNameParseResult("Men Xi", IndexedSeq("Men", "Xi"), IndexedSeq(0 to 0), NoPrefix, Option("Xi"))
    )

  )

  val generalLastNameParserNobleTitleTestData = Table(
    ("in", "exp"),
    (
      "Di",
      noPrefixNorSuffixParseResult("Di")
    ),
    (
      "Di Arthur Xi",
      lastNameParseResult("Di Arthur Xi", IndexedSeq("Di", "Arthur", "Xi"), IndexedSeq(0 to 1), 1, Option("Xi"))
    ),
    (
      "Xi Da",
      noPrefixNorSuffixParseResult("Xi Da")
    ),
    (
      "Da Xi", // tricky case, either da is not a noble title or xi is not a suffix
      lastNameParseResult("Da Xi", IndexedSeq("Da", "Xi"), IndexedSeq(0 to 0), 1, Option("Xi"))
    ),
    (
      "van den Berg", // tricky case, either da is not a noble title or xi is not a suffix
      lastNameParseResult("van den Berg", IndexedSeq("van", "den", "Berg"), IndexedSeq(0 to 2), 2, None)
    )

  )

  @Test(groups = Array("UnitTest") )
  def generalLastNameParserSuffixTests() {
    forAll(generalLastNameParserSuffixTestData) { (in, exp) =>
      val r = GeneralLastNameParser.parse(in)
      r shouldBe exp
    }
  }

  @Test(groups = Array("UnitTest") )
  def generalLastNameParserNobleTitleTests() {
    forAll(generalLastNameParserNobleTitleTestData) { (in, exp) =>
      val r = GeneralLastNameParser.parse(in)
      r shouldBe exp
    }
  }

//  val portugueseLastNameParserTestData = Table(
//    ("in", "exp"),
//    (
//      "da Cunha e Silva",
//      lastNameParseResult("da Cunha e Silva",
//        IndexedSeq("da", "Cunha", "e", "Silva"),
//        IndexedSeq(0 to 1, 3 to 3),
//        1, None)
//    ),
//    (
//      "da Cunha E Silva",
//      lastNameParseResult("da Cunha E Silva",
//        IndexedSeq("da", "Cunha", "E", "Silva"),
//        IndexedSeq(0 to 1, 3 to 3),
//        1, None)
//    ),
//    (
//      "da Cunha e Silva e Brito",
//      lastNameParseResult("da Cunha e Silva e Brito",
//        IndexedSeq("da", "Cunha", "e", "Silva", "e", "Brito"),
//        IndexedSeq(0 to 1, 3 to 3, 5 to 5),
//        1, None)
//    ),
//    (
//      "da Cunha e Silva e Castro Brito",
//      lastNameParseResult("da Cunha e Silva e Castro Brito",
//        IndexedSeq("da", "Cunha", "e", "Silva", "e", "Castro", "Brito"),
//        IndexedSeq(0 to 1, 3 to 3, 5 to 6),
//        1, None)
//    ),
//    (
//      "da Cunha e",
//      lastNameParseResult("da Cunha e",
//        IndexedSeq("da", "Cunha", "e"),
//        IndexedSeq(0 to 1),
//        1, None)
//    ),
//    (
//      "e Silva",
//      lastNameParseResult("e Silva",
//        IndexedSeq("e", "Silva"),
//        IndexedSeq(1 to 1),
//        0, None)
//    )
//
//  )
//
//  @Test(groups = Array(CstTest.UnitTestGroup))
//  def portugueseLastNameParserTests() {
//    forAll(portugueseLastNameParserTestData) { (in, exp) =>
//      val r = LNParserPortuguese.parse(in)
//      r shouldBe exp
//    }
//  }

//  val spanishLastNameParserTestData = Table(
//    ("in", "exp"),
//    (
//      "López de Silanes de Miguel",
//      lastNameParseResult("López de Silanes de Miguel",
//        IndexedSeq("López", "de", "Silanes", "de", "Miguel"),
//        IndexedSeq(0 to 4),
//        0, None)
//    ),
//    (
//      "Martinez de la Peña y Valenzuela",
//      lastNameParseResult("Martinez de la Peña y Valenzuela",
//        IndexedSeq("Martinez", "de", "la", "Peña", "y", "Valenzuela"),
//        IndexedSeq(0 to 3, 5 to 5),
//        0, None)
//    )
//
//  )
//
//  @Test(groups = Array(CstTest.UnitTestGroup) )
//  def spanishLastNameParserTests() {
//    forAll(spanishLastNameParserTestData) { (in, exp) =>
//      val r = LNParserSpanish.parse(in)
//      r shouldBe exp
//    }
//  }

  val japaneseLastNameParserTestData = Table(
    ("in", "exp"),
    (
      "Fujioka-Kobayashi",
      lastNameParseResult("Fujioka-Kobayashi",
        IndexedSeq("Fujioka", "Kobayashi"),
        IndexedSeq(0 to 1),
        0, None)
    ),
    (
      "Kobayashi",
      lastNameParseResult("Kobayashi",
        IndexedSeq("Kobayashi"),
        IndexedSeq(0 to 0),
        0, None)
    )

  )

  @Test(groups = Array("UnitTest") )
  def japaneseLastNameParserTests() {
    forAll(japaneseLastNameParserTestData) { (in, exp) =>
      val r = JapaneseLastNameParser.parse(in)
      r shouldBe exp
    }
  }
}
