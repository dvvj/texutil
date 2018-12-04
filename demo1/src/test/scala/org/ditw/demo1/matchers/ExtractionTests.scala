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
      "Cambridge, MA 02138, USA.",
      Set(4931972L)
    ),
    (
      "Boston, MA, USA.",
      Set(4930956L)
    ),
    (
      "Boston, Massachusetts, USA.",
      Set(4930956L)
    ),
    (
      "Iizuka Fukuoka 820-8505 Japan",
      Set(1861835L)
    ),
    (
      "Bunkyo-Ku, Tokyo 113-0033, Japan",
      Set(7475133L)
    ),
    (
      "Bunkyō, Tokyo 113-0033, Japan",
      Set(7475133L)
    ),
    (
      "Minato-ku, Tokyo",
      Set(1857091L)
    ),
    (
      "Shinjuku, Tokyo",
      Set(1852083L)
    ),
    (
      "Shinjuku, Tokyo, Japan.",
      Set(1852083L)
    ),
    (
      "Shinjuku-ku, Tokyo 160-8582, Japan.",
      Set(1852083L)
    ),
    (
      "Yufu-City, Oita 879-5593, Japan",
      Set(8739980L)
    ),
    (
      "Yufu, Oita 879-5593, Japan",
      Set(11612342L)
    ),
    (
      "Oita, Japan",
      Set(1854487L)
    ),
    (
      "Onga, Fukuoka Japan.",
      Set(7407422L)
    ),
    (
      "Chuo-ku, Fukuoka 810-0051, Japan.",
      Set(7407400L)
    ),
    (
      "Kanda Chiyodaku Japan",
      Set(11749713L)
    ),
    (
      "Miyata Eye Hospital Miyakonojo-shi Miyazaki-ken Japan",
      Set(1856774L)
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
