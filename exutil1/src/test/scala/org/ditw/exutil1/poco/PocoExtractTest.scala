package org.ditw.exutil1.poco
import org.ditw.extract.XtrMgr
import org.ditw.exutil1.TestHelpers.{testDict, testTokenizer}
import org.ditw.exutil1.extract.PocoXtrs
import org.ditw.exutil1.poco.PocoData.cc2Poco
import org.ditw.matcher.{MatchPool, MatcherMgr, TCompMatcher}
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.TableDrivenPropertyChecks

class PocoExtractTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {
  private val testData = Table(
    ("inStr", "expRes"),
    (
      "Bath BA2 7AY, UK.",
      List(2656173)
    ),
    (
      "Sheffield, S10 1RX, UK",
      List(3333193)
    ),
    (
      "Sheffield, S3 7HF, UK.",
      List(2638077)
    ),
    (
      "Norwich, NR4 7UA, UK.",
      List(2641181)
    ),
    (
      "Cardiff University, Cardiff, CF10 3NB, UK.",
      List(2653822)
    ),
    (
      "Granta Park, Cambridge CB21 6GH, UK.",
      List(2653941)
    ),
    (
      "Lowestoft NR33 0HT, UK.",
      List(2643490)
    )
  )

  private def mmgrFrom(cm: TCompMatcher) = new MatcherMgr(
    List(), List(), List(cm), List()
  )
  private val xtrMgr = XtrMgr.create(List(PocoXtrs.gbPocoPfxXtr))

  "Poco extract tests" should "pass" in {
    forAll (testData) { (inStr, expRes) =>
      val cm = cc2Poco(PocoData.CC_GB).genMatcher
      val mmgr = mmgrFrom(cm)
      val mp = MatchPool.fromStr(inStr, testTokenizer, testDict)
      mmgr.run(mp)
      val ids = xtrMgr.run(mp)
      ids.values.flatten.toList shouldBe expRes
    }
  }
}
