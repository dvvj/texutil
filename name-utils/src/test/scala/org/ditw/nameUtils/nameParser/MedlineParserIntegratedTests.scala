package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ParserHelpers.ResultAttrKeyEnum._
import org.ditw.nameUtils.nameParser.MedlineParsers._
import org.ditw.nameUtils.nameParser.ParserHelpers._
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-31.
  */
class MedlineParserIntegratedTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import TestHelpers._

  val testData = Table(
    ("input", "result"),
    // Spanish
    (
      genNormInput("Rodríguez Hernández", "María de los Ángeles"),
      hispanicResult(
        IndexedSeq("Rodríguez Hernández"), IndexedSeq("María de los Ángeles"), None)
    ),
    (
      genNormInput("Rodríguez-Hernández", "Angeles"),
      hispanicResult(
        IndexedSeq("Rodríguez-Hernández"), IndexedSeq("Angeles"), None)
    ),
    (
      genNormInput("Fernández-Peñas", "Pablo F"),
      hispanicResult(
        IndexedSeq("Fernández-Peñas"), IndexedSeq("Pablo"), None, Option(IndexedSeq("F")))
    ),
    (
      genNormInput("Fernández Peñas", "Pablo"),
      hispanicResult(
        IndexedSeq("Fernández Peñas"), IndexedSeq("Pablo"), None)
    ),
    (
      genNormInput("Fernández-Peñas", "Pablo"),
      hispanicResult(
        IndexedSeq("Fernández-Peñas"), IndexedSeq("Pablo"), None)
    ),
    (
      genNormInput("Otero Y Garzon", "E"),
      hispanicResult(IndexedSeq("Otero", "Garzon"), IndexedSeq("E"))
    ),
    (
      genNormInput("Fernández y mas", "Sarah"),
      hispanicResult(IndexedSeq("Fernández", "mas"), IndexedSeq("Sarah"))
    ),
    (
      genNormInput("Hidalgo y Teran Elizondo", "Roberto"),
      hispanicResult(IndexedSeq("Hidalgo", "Teran Elizondo"), IndexedSeq("Roberto"))
    ),

    // Chinese
//    (
//      genNormInput("Fan", "Xi de la"),
//      new NormResult(
//        IndexedSeq("de la Fan"),
//        Map(
//          LastName -> IndexedSeq("de la Fan"),
//          FirstName -> IndexedSeq("Xi")
//        )
//      )
//    ),
    (
      genNormInput("Fujioka-Kobayashi", "Ken-Ichi"),
      japaneseResult(IndexedSeq("Fujioka", "Kobayashi"), IndexedSeq("Ken", "Ichi"))
    ),
    (
      genNormInput("Kobayashi", "Susumu S"),
      japaneseResult(IndexedSeq("Kobayashi"), IndexedSeq("Susumu"))
    ),
    (
      genNormInput("Takahashi", "Ken-Ichi"),
      japaneseResult(IndexedSeq("Takahashi"), IndexedSeq("Ken", "Ichi"))
    ),
    (
      genNormInput("Fujioka-Kobayashi", "Masako"),
      japaneseResult(IndexedSeq("Fujioka", "Kobayashi"), IndexedSeq("Masako"))
    ),
    (
      genNormInput("Kobayashi", "Yoshio"),
      japaneseResult(IndexedSeq("Kobayashi"), IndexedSeq("Yoshio"))
    ),
    (
      genNormInput("Ouyang", "Fan"),
      chineseResult("Ouyang", IndexedSeq("Fan"))
    ),
    (
      genNormInput("Xide", "Fan"),
      chineseResult("Fan", IndexedSeq("Xi", "de"))
    ),
    (
      genNormInput("Fan", "Xi de"),
      chineseResult("Fan", IndexedSeq("Xi", "de"))
    ),
    (
      genNormInput("Fan", "Xide"),
      chineseResult("Fan", IndexedSeq("Xi", "de"))
    ),
    (
      genNormInput("Fan", "Xi-de"),
      chineseResult("Fan", IndexedSeq("Xi", "de"))
    ),
    (
      genNormInput("Fan", "Xi-De"),
      chineseResult("Fan", IndexedSeq("Xi", "De"))
    ),

    // Dutch
    // todo: noble titles in forename indicate last name
    (
      genNormInput("Júnior", "Hélio van der Linden"),
      dutchResult("van der Linden Júnior", IndexedSeq("Hélio"), Option("van der"))
    ),
    (
      genNormInput("den Akker", "Marjan van"),
      dutchResult("van den Akker", IndexedSeq("Marjan"), Option("van den"))
    ),
    (
      genNormInput("van de Woestijne", "Pieter"),
      dutchResult("van de Woestijne", IndexedSeq("Pieter"), Option("van de"))
    ),
    (
      genNormInput("van de Kerkhof", "Peter C M"),
      dutchResult("van de Kerkhof", IndexedSeq("Peter"), Option("van de"), Option(IndexedSeq("C", "M")))
    ),
    (
      genNormInput("Fransen van de Putte", "Elisabeth E"),
      dutchResult("van de Putte", IndexedSeq("Elisabeth"), Option("van de"), Option(IndexedSeq("E", "Fransen")))
    )
  )

  @Test(groups = Array("UnitTest") )
  def integratedMedlineParserTests() {
    forAll(testData) { (in:NormInput, result:NormResult) =>
      val r = MedlineParsers.tryParse(in)
      r.isEmpty shouldBe false
      r.get.lastNames shouldBe result.lastNames
      r.get.attrs shouldBe result.attrs
    }
  }

}
