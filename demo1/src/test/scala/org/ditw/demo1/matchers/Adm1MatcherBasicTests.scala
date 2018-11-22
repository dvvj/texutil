package org.ditw.demo1.matchers
import org.ditw.demo1.TestData
import org.ditw.demo1.gndata.GNCntry
import org.ditw.matcher.MatchPool
import org.ditw.tknr.TknrHelpers
import org.ditw.tknr.TknrHelpers.rangeFromTp3
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class Adm1MatcherBasicTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private val testData = Table(
    ("inStr", "expTag", "expRange"),
    (
      "Washington, DC, USA.",
      TagHelper.cityCountryCmTag(GNCntry.US),
      Set(
        (0, 0, 3)
      )
    ),
    (
      "Worcester County, Massachusetts, USA.",
      TagHelper.adm1AndSubCmTag("US_MA"),
      Set(
        (0, 0, 3)
      )
    ),
    (
      "City of Boston, Massachusetts, USA.",
      TagHelper.adm1AndSubCmTag("US_MA"),
      Set(
        (0, 0, 4),
        (0, 2, 4)
      )
    ),
    (
      "Boston, Massachusetts, USA.",
      TagHelper.adm1AndSubCmTag("US_MA"),
      Set(
        (0, 0, 2)
      )
    )
  )

  "adm1 matchers" should "work" in {
    forAll(testData) { (inStr, expTag, expRanges) =>
      val mp = MatchPool.fromStr(
        inStr, TknrHelpers.TknrTextSeg, TestData.testDict
      )
      MatcherHelper.mmgr.run(mp)

      val ranges = mp.get(expTag).map(_.range)

      val exp = expRanges.map(rangeFromTp3(mp.input, _))

      ranges shouldBe exp
    }
  }
}
