package org.ditw.nameUtils.nameParser
import org.ditw.nameUtils.namerules.AuthorNameUtils
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2018-01-18.
  */
class SignatureMatchTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  private val testData = Table(
    ("sig1", "sig2", "matches4BigNs", "matches4SmallNs"),
    ( "r", "r", true, true),
    ( "r", "rd", true, true),
    ( "d", "rd", false, true),
    ( "rdj", "rd", true, true),
    ( "rdj", "r", true, true),
    ( "rdj", "rj", false, true),
    ( "rdj", "jd", false, true)
  )

  @Test(groups = Array("UNIT_TEST"))
  def testsForBigNamespace() {
    forAll(testData) { (sig1, sig2, matches4BigNs, matches4SmallNs) =>

      val r1 = AuthorNameUtils.signatureMatch(sig1, sig2, true)
      r1 shouldBe matches4BigNs
      val r2 = AuthorNameUtils.signatureMatch(sig1, sig2, false)
      r2 shouldBe matches4SmallNs

    }
  }

}
