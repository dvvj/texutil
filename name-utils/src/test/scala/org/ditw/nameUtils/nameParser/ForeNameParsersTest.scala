package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ForeNameParsers._
import org.ditw.nameUtils.nameParser.ParserHelpers.{NormParseResult, combineNameComponents}
import org.ditw.nameUtils.nameParser.ParserHelpers.ResultAttrKeyEnum._
import org.ditw.nameUtils.nameParser.foreName.FNParserChinese
import org.ditw.nameUtils.nameParser.foreName.FNParserChinese
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

import scala.collection.mutable

/**
  * Created by dev on 2017-08-24.
  */
class ForeNameParsersTest extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import ForeNameParsers.Consts._
  import langSpecData.DutchLangData.PrefixUnits._
  val chineseForeNameParserTestData = Table(
    ("in", "result"),
    (
      "Hua",
      foreNameParseResult(
        "Hua",
        firstNames = IndexedSeq("Hua"),
        exNames = EmptyStrSeq
      )
    ),
    (
      "Sai'e",
      foreNameParseResult(
        "Sai'e",
        firstNames = IndexedSeq("Sai", "e"),
        exNames = EmptyStrSeq
      )
    ),
    (
      "Xiao-Hu",
      foreNameParseResult(
        "Xiao-Hu",
        firstNames = IndexedSeq("Xiao", "Hu"),
        exNames = EmptyStrSeq
      )
    ),
    (
      "Xiaohu",
      foreNameParseResult(
        "Xiaohu",
        firstNames = IndexedSeq("Xiao", "hu"),
        exNames = EmptyStrSeq
      )
    ),
    (
      "Xiao Hu",
      foreNameParseResult(
        "Xiao Hu",
        firstNames = IndexedSeq("Xiao", "Hu"),
        exNames = EmptyStrSeq
      )
    ),
    (
      "X H",
      foreNameParseResult(
        "X H",
        firstNames = IndexedSeq("X", "H"),
        exNames = EmptyStrSeq
      )
    ),
    (
      "X-H",
      foreNameParseResult(
        "X-H",
        firstNames = IndexedSeq("X", "H"),
        exNames = EmptyStrSeq
      )
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def chineseForeNameParserTests() {
    forAll(chineseForeNameParserTestData) { (in, result) =>
      val r = FNParserChinese.parse(in)
      r shouldBe result
    }
  }

  private def dutchForeNameParseResult(
                                     source:String,
                                     firstNames:IndexedSeq[String],
                                     exNames:IndexedSeq[String],
                                     lastNameParts:Option[IndexedSeq[String]],
                                     lastNamePreps:Option[IndexedSeq[String]] = None
                                   ):NormParseResult = {
    val r = mutable.Map(
      _ForeName_Source -> IndexedSeq(source),
      FirstName -> firstNames
    )
    if (exNames.nonEmpty)
      r += ExtraNames -> exNames
    //    if (lastNamePrefixes.nonEmpty)
    //      r += _ForeName_LastNamePreposition -> lastNamePrefixes.get
    if (lastNameParts.nonEmpty) {
      r += _ForeName_LastNameParts -> lastNameParts.get
      r += _ForeName_LastNamePreposition ->
        (
          if (lastNamePreps.isEmpty) lastNameParts.get
          else lastNamePreps.get
        )
    }

    r.toMap
  }

  val dutchForeNameParserTestData = Table(
    ("in", "result"),
    (
      "Trudy van der",
      dutchForeNameParseResult(
        "Trudy van der",
        firstNames = IndexedSeq("Trudy"),
        exNames = IndexedSeq(van, der),
        lastNameParts = Option(IndexedSeq("van", "der"))
      )
    ),
    (
      "Marcel G A van der",
      dutchForeNameParseResult(
        "Marcel G A van der",
        firstNames = IndexedSeq("Marcel"),
        exNames = IndexedSeq("G", "A", van, der),
        lastNameParts = Option(IndexedSeq("van", "der"))
      )
    ),
    (
      "Jack van",
      dutchForeNameParseResult(
        "Jack van",
        firstNames = IndexedSeq("Jack"),
        exNames = IndexedSeq(van),
        lastNameParts = Option(IndexedSeq("van"))
      )
    ),
    (
      "Bas-jan M",
      dutchForeNameParseResult(
        "Bas-jan M",
        firstNames = IndexedSeq("Bas", "jan"),
        exNames = IndexedSeq("M"),
        lastNameParts = None
      )
    ),
    (
      "Mary-Lou",
      dutchForeNameParseResult(
        "Mary-Lou",
        firstNames = IndexedSeq("Mary", "Lou"),
        exNames = EmptyStrSeq,
        lastNameParts = None
      )
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def dutchForeNameParserTests() {
    forAll(dutchForeNameParserTestData) { (in, result) =>
      val r = FNParserDutch.parse(in)

      r shouldBe result
    }
  }


}
