package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult
import org.ditw.nameUtils.nameParser.ParserHelpers._
import org.ditw.nameUtils.nameParser.utils.multiLN.MLNParsers
import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-23.
  */
class MedlineParsersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import MedlineParsers._
  import TestHelpers._
  import org.ditw.nameUtils.nameParser.ParserHelpers.ResultAttrKeyEnum._

  val chineseNameTestData = Table(
    ("in", "result"),
    (
      genNormInput("SHU", "XIAO-OU"),
      chineseResult("SHU", IndexedSeq("XIAO", "OU"))
    ),
    (
      genNormInput("Sun", "Ai-Jun"),
      chineseResult("Sun", IndexedSeq("Ai", "Jun"))
    ),
    (
      genNormInput("Jian-Su", null),
      chineseResult("Su", IndexedSeq("Jian"))
    ),
    (
      genNormInput("Liu", "Jiao"),
      chineseResult("Liu", IndexedSeq("Jiao"))
    ),
    (
      genNormInput("Huang", "Jia"),
      chineseResult("Huang", IndexedSeq("Jia"))
    ),
    (
      genNormInput("Xi", "Jinping"),
      chineseResult("Xi", IndexedSeq("Jin", "ping"))
    ),
    (
      genNormInput("WANG", "D Y"),
      chineseResult("WANG",IndexedSeq("D", "Y"))
    ),
    (
      genNormInput("Wang", "Hong-Sheng"),
      chineseResult("Wang", IndexedSeq("Hong", "Sheng"))
    ),
    (
      genNormInput("Wang", "Qiangbin"),
      chineseResult("Wang",IndexedSeq("Qiang", "bin"))
    ),
    (
      genNormInput("Peng", "Huisheng"),
      chineseResult("Peng", IndexedSeq("Hui", "sheng"))
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def chineseMedlineNameParserTests() {
    forAll(chineseNameTestData) { (in, result) =>
      val r = MedlineParsers.tryParse(in)

      r.isEmpty shouldBe false

      r.get.lastNames shouldBe result.lastNames
      r.get.attrs shouldBe result.attrs
    }
  }

  object DutchTestData {
    import langSpecData.DutchLangData.PrefixUnits._
    val dutchNameTestData = Table(
      ("in", "result"),
      (
        genNormInput("van der Lingen", "Anne-Lotte C"),
        dutchResult(
          "van der Lingen", IndexedSeq("Anne", "Lotte"), Option("van der"),
          Option(IndexedSeq("C"))
        )
      ),
      (
        genNormInput("der Maaten", "Laurens van"),
        dutchResult("van der Maaten", IndexedSeq("Laurens"), Option("van der"))
      ),
      (
        genNormInput("Nest", "Johan van Der"),
        dutchResult("van Der Nest", IndexedSeq("Johan"), Option("van Der"))
      ),
      (
        genNormInput("de Man", "Laurens"),
        dutchResult("de Man", IndexedSeq("Laurens"), Option("de"))
      ),
      (
        genNormInput("Van De Velde", "Lee-ann"),
        dutchResult("Van De Velde", IndexedSeq("Lee", "ann"), Option("Van De"))
      ),
      (
        genNormInput("Van De Velde", "Lee-Ann"),
        dutchResult("Van De Velde", IndexedSeq("Lee", "Ann"), Option("Van De"))
      ),
      (
        genNormInput("Engen", "Ruben E van"),
        dutchResult("van Engen", IndexedSeq("Ruben"), Option(van), Option(IndexedSeq("E")))
      ),
      (
        genNormInput("Loeff", "Maarten F Schim van der"),
        dutchResult("van der Loeff", IndexedSeq("Maarten"), Option("van der"), Option(IndexedSeq("F", "Schim")))
      )

    )
  }

  object GermanTestData {
    import org.ditw.nameUtils.nameParser.langSpecData.GermanLangData.PrefixUnits._
    val germanNameTestData = Table(
      ("in", "result"),
      (
        genNormInput("Malsburg", "Christoph von der"),
        germanResult("von der Malsburg", IndexedSeq("Christoph"), Option("von der"))
      ),
      (
        genNormInput("von der Schmitt", "H"),
        germanResult("von der Schmitt", IndexedSeq("H"), Option("von der"))
      ),
      (
        genNormInput("Von der Haar", "Tobias"),
        germanResult("Von der Haar", IndexedSeq("Tobias"), Option("Von der"))
      ),
      (
        genNormInput("von zur Mühlen", "A"),
        germanResult("von zur Mühlen", IndexedSeq("A"), Option("von zur"))
      ),
      (
        genNormInput("zur Mühlen", "A Von"),
        germanResult("Von zur Mühlen", IndexedSeq("A"), Option("Von zur"))
      )

    )
  }

  object FrenchTestData {
    import langSpecData.FrenchLangData.PrefixUnits._
    val nameTestData = Table(
      ("in", "result"),
      (
        genNormInput("Barra", "Tiare de la"),
        frenchResult("de la Barra", IndexedSeq("Tiare"), Option("de la"))
      ),
      (
        genNormInput("de la Fuente", "Alex"),
        frenchResult("de la Fuente", IndexedSeq("Alex"), Option("de la"))
      ),
      (
        genNormInput("la Barra", "Paula de"),
        frenchResult("de la Barra", IndexedSeq("Paula"), Option("de la"))
      ),
      (
        genNormInput("de la Vega de León", "Antonio"),
        frenchResult("de la Vega de León", IndexedSeq("Antonio"), Option("de la"))
      )

    )
  }

  object SpanishTestData {
    import langSpecData.SpanishLangData.PrefixUnits._
  }

  private def nameParserTest(parser:TNameParser, in:NormInput, result:NormResult) = {
    val r = parser.parse(in)
    r.isEmpty shouldBe false
    r.get.lastNames shouldBe result.lastNames
    r.get.attrs shouldBe result.attrs
  }



  @Test(groups = Array("UNIT_TEST"))
  def dutchMedlineNameParserTests() {
    forAll(DutchTestData.dutchNameTestData)(nameParserTest(MedlineParser4Dutch, _, _))
  }

  @Test(groups = Array("UNIT_TEST"))
  def frenchMedlineNameParserTests() {
    forAll(FrenchTestData.nameTestData)(nameParserTest(MedlineParser4French, _, _))
  }

  @Test(groups = Array("UNIT_TEST"))
  def germanMedlineNameParserTests() {
    forAll(GermanTestData.germanNameTestData)(nameParserTest(MedlineParser4German, _, _))
  }

//  object GeneralTestData {
//    val testData = Table(
//      ("in", "result"),
//      (
//        genNormInput("van den Edwards", "Charles", Option("Sr")),
//        generalResult(IndexedSeq("van den Edwards"), IndexedSeq("Charles"), None, Option("Sr"))
//      )
//    )
//  }
//
//  "genearl medline name parser tests" should "pass" in {
//    forAll(GeneralTestData.testData) { (in, result) =>
//      val r = _runParserNorm(in, _SupportedLanguages, RegisteredParsers.regiMap)
//      r.isEmpty shouldBe false
//      r.get.lastNames shouldBe result.lastNames
//      r.get.firstNames shouldBe result.firstNames
//      r.get.attrs shouldBe result.attrs
//    }
//  }

}
