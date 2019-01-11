package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import org.ditw.nameUtils.nameParser.utils.inferers.LanguageInferers.EmptyLangs
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-31.
  */
class LangInferersIntegratedTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.inferers.LanguageInferers._
  val testData = Table(
    ("foreName", "lastName", "confirmedLangs", "possibleLangs"),
    (
      "Rodríguez-Hernández", "A",
      Iterable(Hispanic), EmptyLangs
    ),
    (
      "Bergasa Y Doroteo", "Lozano",
      Iterable(Hispanic), EmptyLangs
    ),
    (
      "Bergasa Y Doroteo", "Mark",
      EmptyLangs, EmptyLangs
    ),
    (
      "Phu Trong", "Nguyen",
      Iterable(Vietnamese), EmptyLangs
    ),
    (
      "Modesto", "Orozco",
      Iterable(Hispanic), EmptyLangs
    ),

    (
      "Bergasa y Doroteo", "Lozano Lozano",
      Iterable(Hispanic), EmptyLangs
    ),
    (
      "Maria", "García de Lorenzo Y Mateos",
      Iterable(Hispanic), EmptyLangs
    ),
    (
      "Maria", "García de Lorenzo y Mateos",
      Iterable(Hispanic), EmptyLangs
    ),
    (
      "Maria García de", "Lorenzo y Mateos",
      Iterable(Hispanic), EmptyLangs
    ),
    (
      "Maria García", "de Lorenzo y Mateos",
      Iterable(Hispanic), EmptyLangs
    ),


    (
      "Marijke Keus van de", "Poll",
      Iterable(Dutch), EmptyLangs
    ),
    (
      "Marijke Keus Van De", "Poll",
      Iterable(Dutch), EmptyLangs
    ),
    (
      "Marijke Keus van", " de Poll",
      Iterable(Dutch), EmptyLangs
    ),
    (
      "Marijke Keus van", "De Poll",
      Iterable(Dutch), EmptyLangs
    ),
    (
      "Marijke", "Keus van de Poll",
      Iterable(Dutch), EmptyLangs
    ),
    (
      "Marijke", "Keus Van de Poll",
      Iterable(Dutch), EmptyLangs
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def integratedLanguageInferenceTests() {
    forAll(testData) { (foreName, lastName, confirmedLangs, possibleLangs) =>
      val r = inferLanguages(foreName, lastName)
      r shouldBe (confirmedLangs, possibleLangs)
    }
  }

}
