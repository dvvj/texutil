package org.ditw.nameUtils.nameParser
import org.ditw.nameUtils.nameParser.utils.PaymentDataHelpers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-19.
  */
class MergeNamePartsTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.PaymentDataHelpers._

  val dummyId = "dummy"
  private def outputWithDummyId(r:String) = s"$dummyId:$r"
  val traceTestData = Table(
    ("pair", "result"),
    (
      ("solorzano klapprott", "solorzano-klapprott"),
      outputWithDummyId("solorzano-klapprott")
    ),
    (
      ("solorzano klapprott", "solorzano"),
      outputWithDummyId("solorzano klapprott")
    ),
    (
      ("s klapprott", "solorzano k"),
      outputWithDummyId("solorzano klapprott")
    ),
    (
      ("s klapprott", "solorzano"),
      outputWithDummyId("solorzano klapprott")
    ),
    (
      ("klapprott", "solorzano"),
      outputWithDummyId("[klapprott||solorzano]")
    ),
    (
      ("solorzano-klapprott", "solorzano klapprott"),
      outputWithDummyId("solorzano-klapprott")
    ),
    (
      ("s klapprott", "solorzano-klapprott"),
      outputWithDummyId("solorzano-klapprott")
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def checkNamePartsTests() {
    forAll(traceTestData) { (pair, result) =>
      val r = traceCheckNameParts(dummyId, pair)
      r shouldBe result
    }
  }

  val mergeTestData = Table(
    ("pairs", "results"),
    (
      Seq(
        ("s klapprott", "solorzano-klapprott"),
        ("M Tommy", "Marie T")
      ),
      Seq(
        (
          IndexedSeq(
            "solorzano" -> None,
            "klapprott" -> None
          ),
          IndexedSeq("-")
        ),
        (
          IndexedSeq(
            "Marie" -> None,
            "Tommy" -> None
          ),
          IndexedSeq(" ")
        )
      )
    ),
    (
      Seq(
        ("s klapprott", "solorzano-klapprott"),
        ("M TOMMY", "Marie T")
      ),
      Seq(
        (
          IndexedSeq(
            "solorzano" -> None,
            "klapprott" -> None
          ),
          IndexedSeq("-")
        ),
        (
          IndexedSeq(
            "Marie" -> None,
            "TOMMY" -> None
          ),
          IndexedSeq(" ")
        )
      )
    ),
    (
      Seq(
        ("s klapprott", "solorzano-klapprott"),
        ("M TOMMY", "Marie Tommy")
      ),
      Seq(
        (
          IndexedSeq(
            "solorzano" -> None,
            "klapprott" -> None
          ),
          IndexedSeq("-")
        ),
        (
          IndexedSeq(
            "Marie" -> None,
            "Tommy" -> None
          ),
          IndexedSeq(" ")
        )
      )
    ),
    (
      Seq(
        ("s klapprott", "solorzano-klapprott"),
        ("M Tommy", "Marie TOMMY")
      ),
      Seq(
        (
          IndexedSeq(
            "solorzano" -> None,
            "klapprott" -> None
          ),
          IndexedSeq("-")
        ),
        (
          IndexedSeq(
            "Marie" -> None,
            "TOMMY" -> None
          ),
          IndexedSeq(" ")
        )
      )
    ),
    (
      Seq(
        ("s klapprott", "solorzano-klapprott"),
        ("M Tommy", "Marie Teddy")
      ),
      Seq(
        (
          IndexedSeq(
            "solorzano" -> None,
            "klapprott" -> None
          ),
          IndexedSeq("-")
        ),
        (
          IndexedSeq(
            "Marie" -> None,
            "Tommy" -> Option("Teddy")
          ),
          IndexedSeq(" ")
        )
      )
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def mergeNamePartsTests() {
    forAll(mergeTestData) { (pairs, results) =>
      val r = mergeNameParts(pairs)
      r shouldBe results
    }
  }

  val extractComponentSeparatorsTestData = Table(
    ("p1", "p2", "separators"),
    ("s", "solorzano", IndexedSeq[String]()),
    ("s klapprott", "solorzano-klapprott", IndexedSeq("-")),
    ("s", "solorzano-klapprott", IndexedSeq("-")),
    ("s klapprott ex", "solorzano-klapprott", IndexedSeq("-", " ")),
    ("s klapprott-ex ex2", "solorzano-klapprott", IndexedSeq("-", "-", " "))
  )

  import ParserHelpers._

  @Test(groups = Array("UNIT_TEST"))
  def extractComponentSeparatorsTests() {
    forAll(extractComponentSeparatorsTestData) { (p1, p2, separators) =>
      val comps1 = splitComponents(p1)
      val comps2 = splitComponents(p2)

      val r = extractComponentSeparators(p1, p2, comps1, comps2)
      r shouldBe separators
    }
  }
}
