package org.ditw.demo1.matchers
import org.ditw.demo1.TestData.{testDict, testGNSvc}
import org.ditw.demo1.matchers.MatcherHelper.{mmgr, xtrMgr}
import org.ditw.matcher.MatchPool
import org.ditw.tknr.TknrHelpers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class ExtractionTests extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private val testData = Table(
    ("inStr", "expGNIds"),
    (
      "Miyata Eye Hospital Miyakonojo-shi Miyazaki-ken Japan",
      Set(1856774L)
    ),
    (
      "Oita, Japan",
      Set(1854487L)
    ),
    (
      "Yufu-City, Oita 879-5593, Japan",
      Set(8739980L)
    ),
    (
      "Yufu-shi, Oita 879-5593, Japan",
      Set(8739980L)
    ),
    (
      "Beppu, Oita, 874-0838, Japan",
      Set(1864750L)
    ),
    (
      "Imizu City, Toyama-ken 937-8585, Japan",
      Set(6822125L)
    ),
    (
      "Imizu City, Toyama 937-8585, Japan",
      Set(6822125L)
    ),
    (
      "Yufu, Oita 879-5593, Japan",
      Set(11612342L)
    ),
    (
      "University of Southern California, Los Angeles, California, USA",
      Set(5368361L)
    ),
    (
      "Komatsu Japan",
      Set(1858909L, 1858910L, 1858911L)  // before disambiguation
    )
  )

  "extraction tests" should "pass" in {
    forAll(testData) { (inStr, expGNIds) =>
      val mp = MatchPool.fromStr(inStr, TknrHelpers.TknrTextSeg, testDict)
      mmgr.run(mp)
      val rng2Ents = testGNSvc.extrEnts(xtrMgr, mp)
      val res = rng2Ents.flatMap(_._2.map(_.gnid)).toSet
      res shouldBe expGNIds

    }
  }

}
