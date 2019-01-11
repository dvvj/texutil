package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.utils.NamePartHelpers._
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-23.
  */
class SortStringSeqTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  val testData = Table(
    ("in", "sorted"),
    (
      Iterable(
        IndexedSeq("ab"),
        IndexedSeq("a"),
        IndexedSeq("b")
      ),
      IndexedSeq(
        IndexedSeq("b"),
        IndexedSeq("ab"),
        IndexedSeq("a")
      )
    ),
    (
      Iterable(
        IndexedSeq("a"),
        IndexedSeq("ab"),
        IndexedSeq("ab c"),
        IndexedSeq("b c")
      ),
      IndexedSeq(
        IndexedSeq("b c"),
        IndexedSeq("ab c"),
        IndexedSeq("ab"),
        IndexedSeq("a")
      )
    ),
    (
      Iterable(
        IndexedSeq("a"),
        IndexedSeq("ab"),
        IndexedSeq("ab c"),
        IndexedSeq("ab d"),
        IndexedSeq("b c")
      ),
      IndexedSeq(
        IndexedSeq("b c"),
        IndexedSeq("ab d"),
        IndexedSeq("ab c"),
        IndexedSeq("ab"),
        IndexedSeq("a")
      )
    )

  )

  @Test(groups = Array("UNIT_TEST"))
  def sortStringSeqTests() {
    forAll(testData) { (in, sorted) =>
      val r = sortStringSeq(in)
      r shouldBe sorted
    }

    // display dutch prefixes
    println(langSpecData.DutchLangData.DutchSortedPrefixes.mkString("\n"))
  }

}
