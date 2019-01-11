package org.ditw.nameUtils.nameParser

import ParserHelpers.splitParts
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by dev on 2018-12-17.
  */
class MLNParsersTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
  private val testData = Table(
    ("lang", "fullName", "firstNames", "lastNames"),
//    (
//      Spanish,
//      "maria del pilar estevez diz",
//      splitParts("francina"),
//      splitParts("gonzalez de los santos")
//    ),
//    (
//      Spanish,
//      "alvaro amo vazquez de la torre",
//      splitParts("francina"),
//      splitParts("gonzalez de los santos")
//    ),
//    (
//      Spanish,
//      "francina gonzalez de los santos",
//      splitParts("francina"),
//      splitParts("gonzalez de los santos")
//    ),
//    (
//      Spanish,
//      "María de los Ángeles Alvariño González",
//      splitParts("María de los Ángeles"),
//      splitParts("Alvariño González")
//    ),
//    (
//      Spanish,
//      "Maria de Las Mercedes Romero",
//      splitParts("Maria de Las Mercedes"),
//      splitParts("Romero")
//    ),
    (
      Hispanic,
      "Digna R Velez Edwards",
      splitParts("Digna R"),
      splitParts("Velez Edwards")
    ),
    (
      Hispanic,
      "Digna R X Velez Edwards",
      splitParts("Digna R X"),
      splitParts("Velez Edwards")
    ),
    (
      Hispanic,
      "victor zuniga dourado",
      Vector("victor"),
      splitParts("zuniga dourado")
    ),
    (
      Hispanic,
      "ricardo diaz de leon-medina",
      Vector("ricardo"),
      splitParts("diaz de leon-medina")
    )
  )

  import org.ditw.nameUtils.nameParser.utils.multiLN.MLNParsers._
  "MLN parser tests" should "pass" in {
    forAll(testData) { (lang, fullName, firstNames, lastNames) =>
      val r = parseFullName(lang, fullName)

      r shouldBe (firstNames, lastNames)
    }
  }

//  private val exTestData = Table(
//    ("lang", "fullName"),
//    (
//      Hispanic,
//      "Digna R"
//    ),
//    (
//      Hispanic,
//      "Digna R X"
//    ),
//    (
//      Hispanic,
//      "victor"
//    )
//  )
//
//  "MLN parser Exception tests" should "pass" in {
//    forAll(exTestData) { (lang, fullName) =>
//      intercept[IllegalArgumentException] {
//        parseFullName(lang, fullName)
//      }
//    }
//  }
}
