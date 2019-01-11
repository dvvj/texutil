package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.langSpecData.PortugueseLangData
import org.ditw.nameUtils.nameParser.utils.inferers.LanguageInferers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-31.
  */
class LangInferersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
  import org.ditw.nameUtils.nameParser.utils.inferers.LanguageInferers._
  val lastNameInfererTestData = Table(
    ("in", "langs"),
    (
      "Mello e Souza",
      Iterable(Hispanic)
    ),
    (
      "Mello E Souza",
      Iterable(Hispanic)
    ),
    (
      "García de Lorenzo y Mateos",
      Iterable(Hispanic)
    ),
    (
      "García de Lorenzo Y Mateos",
      Iterable(Hispanic)
    ),
    (
      "García-y Otero",
      EmptyLangs
    ),
    (
      "García-Y Otero",
      EmptyLangs
    )
  )

  import org.ditw.nameUtils.nameParser.utils.inferers.LastNameInferers._
  import ParserHelpers._

  @Test(groups = Array("UnitTest"))
  def lastNameInfererTests() {
    forAll(lastNameInfererTestData) { (in, langs) =>
      val comps = splitParts(in) // ignore cases like "García-y Otero" for now (from Mexico instead)
      val inferredLangs = inferFromLastNameParts(comps)
      inferredLangs shouldBe langs
    }
  }

  val nameComponentInfererTestData = Table(
    ("foreName", "lastName", "langs"),
    (
      "Mariana dos Santos", "Marcon",
      Iterable(Hispanic)
    ),
    (
      "Mariana Dos Santos", "Marcon",
      Iterable(Hispanic)
    ),
    (
      "Marcon", "Mariana dos Santos",
      Iterable(Hispanic)
    ),
    (
      "Marcon", "Mariana Dos Santos",
      Iterable(Hispanic)
    ),
//    (
//      "Pasca di Magliano", "Laitano",
//      Iterable(Italian)
//    ),
//    (
//      "Pasca Di Magliano", "Laitano",
//      Iterable(Italian)
//    ),
//    (
//      "Laitano", "Pasca di Magliano",
//      Iterable(Italian)
//    ),
//    (
//      "Laitano", "Pasca Di Magliano",
//      Iterable(Italian)
//    ),
    (
      "E", "Coccanari de' Fornari",
      Iterable(Italian)
    ),
    (
      "E", "Sassoli De' Bianchi",
      Iterable(Italian)
    )
  )

  import org.ditw.nameUtils.nameParser.utils.inferers.NameComponentInferers._

  @Test(groups = Array("UnitTest"))
  def nameComponentInfererTests() {
    forAll(nameComponentInfererTestData) { (foreName, lastName, langs) =>
      val comps = splitComponents(foreName) ++ splitComponents(lastName)
      val inferredLangs = inferFromNameComponents(comps)
      inferredLangs shouldBe langs
    }
  }

  val fullNameInfererTestData = Table(
    ("foreName", "lastName", "langs"),
    (
      "Elisabeth E", "Fransen van de Putte",
      Iterable(Dutch)
    ),
    (
      "Elisabeth E", "Fransen Van De Putte",
      Iterable(Dutch)
    ),
    (
      "Elisabeth E", "Fransen Van Putte",
      EmptyLangs
    ),
    (
      "Elisabeth E", "Fransen Van Dette",
      EmptyLangs
    ),
    (
      "Elisabeth E", "Svan de Putte",
      EmptyLangs
    )
  )

  import org.ditw.nameUtils.nameParser.utils.inferers.FullNameInferers._

  @Test(groups = Array("UnitTest"))
  def fullNameInfererTests() {
    forAll(fullNameInfererTestData) { (foreName, lastName, langs) =>
      //val comps = splitComponents(foreName) ++ splitComponents(lastName)
      val inferredLangs = inferFromFullName(s"$foreName $lastName")
      inferredLangs shouldBe langs
    }
  }

}
