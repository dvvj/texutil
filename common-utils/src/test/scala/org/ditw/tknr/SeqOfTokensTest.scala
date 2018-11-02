package org.ditw.tknr
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class SeqOfTokensTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  import TestHelpers._
  import TknrHelpers._

  private val testTokens1 = IndexedSeq(
    noPfxSfx("Cardiovascular"),
    noPfx("Research", ","),
    noPfxSfx("Vrije"),
    noPfx("University", ","),
    noPfxSfx("Amsterdam")
  )
  private val constructorTestData = Table(
    ("tokenContents"),
    (
      testTokens1
    )
  )

  "constructor" should "work" in {
    forAll(constructorTestData) { (tokenContents) =>
      val lot = loTFrom(tokenContents)
      lot.length shouldBe tokenContents.length
    }
  }

  private val filterTestData = Table(
    ("tokenContents", "filteredCount"),
    (
      testTokens1, 2
    )
  )

  "filter" should "work" in {
    forAll(filterTestData) { (tokenContents, filteredCount) =>
      val lot = loTFrom(tokenContents)
      val lot2 = lot.filter(!_.sfx.isEmpty)
      lot2.length shouldBe filteredCount
      lot2.isInstanceOf[SeqOfTokens] shouldBe true
    }
  }

}
