package org.ditw.nameUtils.nameParser.hispanic

import org.ditw.nameUtils.nameParser.MedlineParsers.TNameParser
import org.ditw.nameUtils.nameParser.ParserHelpers.{NormInput, NormResult, genNormInput}
import org.ditw.nameUtils.nameParser.TestHelpers.hispanicResult
import org.ditw.nameUtils.nameParser.utils.multiLN.MLNParsers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

class ParsingTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  val nameTestData = Table(
    ("in", "result"),
    (
      genNormInput("DE LA PENA", "CRISTINA ISABEL"),
      hispanicResult(
        IndexedSeq("DE LA PENA"), IndexedSeq("CRISTINA"), None, Option(IndexedSeq("ISABEL")))
    ),
    (
      genNormInput("DE LA CRUZ", "WILFRED PERUCHO"),
      hispanicResult(
        IndexedSeq("DE LA CRUZ"), IndexedSeq("WILFRED"), None, Option(IndexedSeq("PERUCHO")))
    ),
    (
      genNormInput("DELA CRUZ", "WILFRED PERUCHO"),
      hispanicResult(
        IndexedSeq("DELA CRUZ"), IndexedSeq("WILFRED"), None, Option(IndexedSeq("PERUCHO")))
    ),
    (
      genNormInput("RODRIGUEZ JUARBE", "MARY ANN ANN"),
      hispanicResult(
        IndexedSeq("RODRIGUEZ JUARBE"), IndexedSeq("MARY"), None, Option(IndexedSeq("ANN", "ANN")))
    ),
    (
      genNormInput("De-León", "Pelayo Salinas"),
      hispanicResult(
        IndexedSeq("Salinas De-León"), IndexedSeq("Pelayo"), None)
    ),
    (
      genNormInput("Salinas-de-León", "Pelayo"),
      hispanicResult(
        IndexedSeq("Salinas-de-León"), IndexedSeq("Pelayo"), None)
    ),
    (
      genNormInput("Vogel", "Fernanda Flores"),
      hispanicResult(
        IndexedSeq("Flores Vogel"), IndexedSeq("Fernanda"), None)
    ),
    (
      genNormInput("Vogel", "Fernanda Silveira Flores"),
      hispanicResult(
        IndexedSeq("Flores Vogel"), IndexedSeq("Fernanda"), None, Option(IndexedSeq("Silveira")))
    ),
    (
      genNormInput("da Silva-Conforti", "Adriana Madeira Álvares"),
      hispanicResult(
        IndexedSeq("Álvares da Silva-Conforti"), IndexedSeq("Adriana"), None, Option(IndexedSeq("Madeira")))
    ),
    (
      genNormInput("De la Rosa", "Javier Rivera"),
      hispanicResult(
        IndexedSeq("Rivera De la Rosa"), IndexedSeq("Javier"), None)
    ),
    (
      genNormInput("Rodríguez Celin", "María de los Ángeles"),
      hispanicResult(
        IndexedSeq("Rodríguez Celin"), IndexedSeq("María de los Ángeles"), None)
    ),
    (
      genNormInput("Rodríguez Celin", "María de Las Mercedes"),
      hispanicResult(
        IndexedSeq("Rodríguez Celin"), IndexedSeq("María de Las Mercedes"), None)
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
      genNormInput("De Las Heras", "Marcelo"),
      hispanicResult(
        IndexedSeq("De Las Heras"), IndexedSeq("Marcelo"), None)
    ),
    (
      genNormInput("De Las Heras", "M"),
      hispanicResult(
        IndexedSeq("De Las Heras"), IndexedSeq("M"), None)
    ),
    (
      genNormInput("Ugrinowitsch", "Alessandra Aguilar Coca"),
      hispanicResult(
        IndexedSeq("Aguilar Coca Ugrinowitsch"), IndexedSeq("Alessandra"), None)
    ),
    (
      genNormInput("Coca-Ugrinowitsch", "Alessandra Aguilar"),
      hispanicResult(
        IndexedSeq("Aguilar Coca-Ugrinowitsch"), IndexedSeq("Alessandra"), None)
    ),
    (
      genNormInput("Herrera", "Ana Abarca"),
      hispanicResult(
        IndexedSeq("Abarca Herrera"), IndexedSeq("Ana"), None)
    ),
    (
      genNormInput("Alejandro Vasquez", "A"),
      hispanicResult(
        IndexedSeq("Alejandro Vasquez"), IndexedSeq("A"), None)
    )
  )
  private def nameParserTest(parser:TNameParser, in:NormInput, result:NormResult) = {
    val r = parser.parse(in)
    r.isEmpty shouldBe false
    r.get.lastNames shouldBe result.lastNames
    r.get.attrs shouldBe result.attrs
  }

  @Test(groups = Array("UNIT_TEST"))
  def spanishMedlineNameParserTests() {
    forAll(nameTestData)(nameParserTest(MLNParsers.hispanicParser, _, _))
  }
}
