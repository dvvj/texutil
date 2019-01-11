package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ParserHelpers.genNormInput
import org.ditw.nameUtils.nameParser.utils.NameLanguages
import org.ditw.nameUtils.nameParser.utils.NameLanguages.PrefixCheckResult
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import org.ditw.nameUtils.nameParser.utils.NameLanguages
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.Test
/**
  * Created by dev on 2017-08-24.
  */
class CheckPrefixTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.langSpecData.PfxParsers._
  import langSpecData.DutchLangData.PrefixUnits._
  val dutchTestData = Table(
    ("in", "result"),
    (
      genNormInput("Weijden", "Trudy van der"),
      PrefixCheckResult(Dutch, IndexedSeq(van, der))
    ),
    (
      genNormInput("der Kant", "Rik van"),
      PrefixCheckResult(Dutch, IndexedSeq(van, der))
    ),
    (
      genNormInput("van der Laan-Luijkx", "I T"),
      PrefixCheckResult(Dutch, IndexedSeq(van, der))
    ),
    (
      genNormInput("Laan-Luijkx", "I T"),
      None
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def checkDutchPrefixTests() {
    forAll(dutchTestData) { (in, result) =>

      val r = NameLanguages.checkPrefix(_DutchPrefixParser, in)
      if (r.nonEmpty) {
        r.get shouldBe result
      }
      else {
        result shouldBe None
      }
    }
  }

}
