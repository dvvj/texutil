package org.ditw.nameUtils.nameParser.hispanic

import org.ditw.nameUtils.nameParser.utils.nameCompatibility.NameComponentComparors.hispanicNamespaceGenerator
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

class NamespaceGenTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private val nsgTestData = Table(
    ("nameParts", "namespace"),
    (
      Array("Fernández-Peñas", "Pablo"),
      Option("fernandez/penas|p:hisp")
    ),
    (
      Array("Fernández Peñas", "Pablo"),
      Option("fernandez/penas|p:hisp")
    ),
    (
      Array("Salinas-de-León", "Pelayo"),
      Option("salinas/leon|p:hisp")
    ),
    (
      Array("Salinas de-León", "Pelayo"),
      Option("salinas/leon|p:hisp")
    ),
    (
      Array("Flores Vogel", "Fernanda", "Silveira"),
      Option("flores/vogel|f:hisp")
    ),
    (
      Array("Flores Vogel", "Fernanda"),
      Option("flores/vogel|f:hisp")
    ),
    (
      Array("Álvares da Silva-Conforti", "Adriana", "Madeira"),
      Option("alvares/silva/conforti|a:hisp")
    ),
    (
      Array("De Las Heras", "Marcelo"),
      Option("heras|m:hisp")
    ),
    (
      Array("De Campos", "Reinaldo"),
      Option("campos|r:hisp")
    ),
    (
      Array("Campos", "Reinaldo"),
      Option("campos|r:hisp")
    ),
    (
      Array("Campos", "Reinaldo"),
      Option("campos|r:hisp")
    )
  )

  @Test(groups = Array("UnitTest"))
  def genSpanishNamespaceTests() {
    forAll (nsgTestData) { (nameParts, namespace) =>
      val ns = hispanicNamespaceGenerator(nameParts)
      ns shouldBe namespace
    }
  }

}
