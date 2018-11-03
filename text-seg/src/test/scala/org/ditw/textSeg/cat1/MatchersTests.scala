package org.ditw.textSeg.cat1
import org.ditw.common.TkRange
import org.ditw.matcher.MatchPool
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class MatchersTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {
  import org.ditw.textSeg.common.AllCatMatchers._
  import org.ditw.textSeg.common.Tags._
  import org.ditw.textSeg.Settings._
  import org.ditw.textSeg.common.Vocabs
  private val corpTestData = Table(
    ("inStr", "tag", "expRanges"),
//    (
//      "Integrated Laboratory Systems, Inc.,",
//      TagSegCorp,
//      Set(
//        (0, 0, 4)
//      )
//    ),
    (
      "Integrated Laboratory Systems Inc.,",
      TagSegCorp,
      Set(
        (0, 0, 4)
      )
    )
  )
  "Corp SegMatcher tests" should "pass" in {
    forAll(corpTestData) { (inStr, tag, expRanges) =>
      val matchPool = MatchPool.fromStr(inStr, TknrTextSeg, Vocabs._Dict)
      matcherMgr.run(matchPool)
      val res = matchPool.get(tag)
      val resRanges = res.map(_.range)
      val expRngs = expRanges.map(tp => TkRange(matchPool.input, tp._1, tp._2, tp._3))
      resRanges shouldBe expRngs
    }
  }
}
