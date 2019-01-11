package org.ditw.nameUtils.nameParser

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-06.
  */
class CaseNormalizerTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
  import org.ditw.nameUtils.nameParser.utils.CaseNormalizer._

  val testData = Table(
    ("lang", "comp", "exp"),
    (Dutch, "VAN", "Van"),
    (Dutch, "IKSSEL", "Ikssel"),
    (Dutch, "IJSSEL", "IJssel"),
    (Chinese, "a", "A"),
    (Chinese, "ai", "Ai"),
    (Chinese, "AI", "Ai"),
    (Chinese, "E", "E"),
    (Chinese, "e", "E"),
    (Chinese, "Chen", "Chen"),
    (Chinese, "CHEN", "Chen")
  )

  @Test(groups = Array("UNIT_TEST"))
  def caseNormalizerTests() {
    forAll(testData) { (lang, comp, exp) =>
      val r = normalize(lang, comp)
      r shouldBe exp
    }
  }
}
