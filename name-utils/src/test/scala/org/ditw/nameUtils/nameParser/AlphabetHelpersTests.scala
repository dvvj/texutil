package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.utils.AlphabetHelpers
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import org.ditw.nameUtils.nameParser.utils.AlphabetHelpers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-25.
  */
class AlphabetHelpersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  val testData = Table(
    ("in", "result"),
    (
      IndexedSeq("abc-ă", "def"),
      Iterable[(LanguageEnum,Int)](
        Romanian -> 1
      )
    ),
    (
      IndexedSeq("abc-č", "ćdef"),
      Iterable[(LanguageEnum,Int)](
        Croation -> 2
      )
    ),
    (
      IndexedSeq("abc", "def"),
      Iterable[(LanguageEnum,Int)]()
    ),
    (
      IndexedSeq("ăbc", "def"),
      Iterable[(LanguageEnum,Int)](
        Romanian -> 1
      )
    ),
    (
      IndexedSeq("ăbć", "def"),
      Iterable[(LanguageEnum,Int)]()
    ),
    (
      IndexedSeq("ąbć", "def"),
      Iterable[(LanguageEnum,Int)](
        Polish -> 2
      )
    ),
    (
      IndexedSeq("ąbć", "dęf"),
      Iterable[(LanguageEnum,Int)](
        Polish -> 3
      )
    ),
    (
      IndexedSeq("ąbc", "dęf"),
      Iterable[(LanguageEnum,Int)](
        Lithuanian -> 2,
        Polish -> 2
      )
    ),
    (
      IndexedSeq("Ąbc", "Dęf"),
      Iterable[(LanguageEnum,Int)](
        Lithuanian -> 2,
        Polish -> 2
      )
    ),
    (
      IndexedSeq("Ąb C.", "dęf"),
      Iterable[(LanguageEnum,Int)](
        Lithuanian -> 2,
        Polish -> 2
      )
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def langByAlphabetTests() {
    forAll(testData) { (in, result) =>
      val langs = AlphabetHelpers.langByAlphabets(in).toSet
      langs shouldBe result.toSet
    }
  }
}
