package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.utils.NamePartHelpers._
import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.PrefixParser
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-23.
  */
class PrefixParserTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.langSpecData.PfxParsers._

  object DutchTestData {
    import langSpecData.DutchLangData.PrefixUnits._
    val compareEntryTestData = Table(
      ("seq2Compare", "entry", "result"),
      (
        IndexedSeq(van, den, "Berg"),
        IndexedSeq(van, den),
        List(Equal, GreaterThan)
      ),
      (
        IndexedSeq(van, "Berg"),
        IndexedSeq(van, den),
        List(LessThan, LessThan)
      ),
      (
        IndexedSeq(van, den, "Berg"),
        IndexedSeq(van),
        List(Equal, GreaterThan)
      ),
      (
        IndexedSeq(van, "e"),
        IndexedSeq(van, den),
        List(GreaterThan, GreaterThan)
      )
    )
    val findInLastNameTestData = Table(
      ("parser", "seq2Search", "result"),
      (
        _DutchPrefixParser,
        IndexedSeq("Van", den, "Berg"),
        Option(IndexedSeq("Van", den))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq(van, "Berg"),
        Option(IndexedSeq(van))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq(van, den, "Berg"),
        Option(IndexedSeq(van, den))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq("xx", "Berg"),
        None
      ),

      (
        _DutchPrefixParser,
        IndexedSeq(voor, in, appos_t, "Berg"),
        Option(IndexedSeq(voor, in, appos_t))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq(voor, "Berg"),
        Option(IndexedSeq(voor))
      ),

      (
        _DutchPrefixParser,
        IndexedSeq(appos_s, "Berg"),
        Option(IndexedSeq(appos_s))
      )
    )
    val findInForeNameTestData = Table(
      ("parser", "seq2Search", "result"),
      (
        //[Eva Van Den]
        _DutchPrefixParser,
        IndexedSeq("Eva", van, den),
        Option(IndexedSeq(van, den))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq("Martin", van, den),
        Option(IndexedSeq(van, den))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq("Marja", van, der),
        Option(IndexedSeq(van, der))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq("Jan", den),
        Option(IndexedSeq(den))
      ),
      (
        _DutchPrefixParser,
        IndexedSeq("Berg", "xx"),
        None
      )
    )

  }

  object FrenchTestData {
    import langSpecData.FrenchLangData.PrefixUnits._
    val compareEntryTestData = Table(
      ("seq2Compare", "entry", "result"),
      (
        IndexedSeq(de, "e"),
        IndexedSeq(de, la),
        List(LessThan, LessThan)
      ),
      (
        IndexedSeq(de, la, "Berg"),
        IndexedSeq(de, la),
        List(Equal, GreaterThan)
      ),
      (
        IndexedSeq(de, "Berg"),
        IndexedSeq(de, la),
        List(LessThan, LessThan)
      ),
      (
        IndexedSeq(de, "Berg"),
        IndexedSeq(de),
        List(Equal, GreaterThan)
      )
    )
    val findInLastNameTestData = Table(
      ("parser", "seq2Search", "result"),
      (
        _FrenchPrefixParser,
        IndexedSeq(de, "Berg"),
        Option(IndexedSeq(de))
      ),
      (
        _FrenchPrefixParser,
        IndexedSeq(de, la, "Berg"),
        Option(IndexedSeq(de, la))
      ),
      (
        _FrenchPrefixParser,
        IndexedSeq("xx", "Berg"),
        None
      ),
      (
        _FrenchPrefixParser,
        IndexedSeq(du, "Berg"),
        Option(IndexedSeq(du))
      ),
      (
        _FrenchPrefixParser,
        IndexedSeq(de, du, "Berg"),
        Option(IndexedSeq(de))
      )
    )
    val findInForeNameTestData = Table(
      ("parser", "seq2Search", "result"),
      (
        //[Eva Van Den]
        _FrenchPrefixParser,
        IndexedSeq("Eva", de, la),
        Option(IndexedSeq(de, la))
      ),
      (
        _FrenchPrefixParser,
        IndexedSeq("Marja", du),
        Option(IndexedSeq(du))
      ),
      (
        _FrenchPrefixParser,
        IndexedSeq("Jan", del),
        Option(IndexedSeq(del))
      ),
      (
        _FrenchPrefixParser,
        IndexedSeq("Berg", "xx"),
        None
      )
    )

  }

  private def compareEntryTestDataTest(seq2Compare:IndexedSeq[String],
                                       entry:IndexedSeq[String],
                                       results:List[Int]): Unit = {
    val c1 = compareEntry(seq2Compare, entry, true)
    c1 shouldBe results(0)
    val c2 = compareEntry(seq2Compare, entry, false)
    c2 shouldBe results(1)

  }

  @Test(groups = Array("UNIT_TEST"))
  def compareEntryTests() {
    forAll(FrenchTestData.compareEntryTestData)(compareEntryTestDataTest)
    forAll(DutchTestData.compareEntryTestData)(compareEntryTestDataTest)
  }

  private def findInLastNameTest(parser:PrefixParser, seq2Search:IndexedSeq[String], result:Option[IndexedSeq[String]]) = {
    val r = parser.findInLastName(seq2Search, true)
    r shouldBe result
  }
  @Test(groups = Array("UNIT_TEST"))
  def findInLastNameTests() {
    forAll(DutchTestData.findInLastNameTestData)(findInLastNameTest)
    forAll(FrenchTestData.findInLastNameTestData)(findInLastNameTest)
  }

  private def findInForeNameTest(parser:PrefixParser, seq2Search:IndexedSeq[String], result:Option[IndexedSeq[String]]) = {
    val r = parser.findInForeName(seq2Search)
    r shouldBe result
  }
  @Test(groups = Array("UNIT_TEST"))
  def findInForeNameTests() {
    forAll(DutchTestData.findInForeNameTestData)(findInForeNameTest)
    forAll(FrenchTestData.findInForeNameTestData)(findInForeNameTest)
  }

}
