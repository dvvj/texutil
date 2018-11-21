package org.ditw.demo1.matchers
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.{MatchPool, MatcherMgr}
import org.ditw.tknr.{TknrHelpers, Tokenizers, Trimmers}

object TmTest1 extends App {
  def testTm(adm0:TGNMap,
             testStrs:Iterable[String]
            ):Unit = {


    val dict = MatcherGen.loadDict(Iterable(adm0))

    val (tms, cms) = Adm0Gen.genMatchers(adm0, dict)
    val mmgr = new MatcherMgr(
      tms,
      cms,
      List()
    )

    testStrs.foreach { str =>
      val mp = MatchPool.fromStr(str, TknrHelpers.TknrTextSeg, dict)
      mmgr.run(mp)
      val tag = TagHelper.adm1AndSubCmTag("US_MA")
      val t = mp.get(tag)
      println(t.size)
    }

  }

  import org.ditw.demo1.TestData._
  import org.ditw.demo1.gndata.GNCntry._
  val admUS = testCountries(US)
  testTm(admUS, List(
    "Worcester County, Massachusetts, USA.",
    "City of Boston, Massachusetts, USA.",
    "Boston, Massachusetts, USA.",
    "massachusetts",
    "Worcester County",
    "City of Boston"
  ))
}
