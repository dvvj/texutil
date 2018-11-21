package org.ditw.demo1.matchers
import org.ditw.demo1.gndata.GNCntry.GNCntry
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.TokenMatchers.TmMatchPProc
import org.ditw.matcher._
import org.ditw.tknr.{TknrHelpers, Tokenizers, Trimmers}

import scala.collection.mutable.ListBuffer

object TmTest1 extends App {
  def testTm(adm0s:Map[GNCntry, TGNMap],
             testStrs:Iterable[String]
            ):Unit = {

    val dict = MatcherGen.loadDict(adm0s.values)

    val tmlst = ListBuffer[TTkMatcher]()
    val cmlst = ListBuffer[TCompMatcher]()
    adm0s.values.foreach { adm0 =>
      val (tms, cms) = Adm0Gen.genMatchers(adm0, dict)
      tmlst ++= tms
      cmlst ++= cms
    }

    val adm0Name2Tag = adm0s.flatMap { adm0 =>
      val ent = adm0._2.self.get
      ent.queryNames.map(_ -> TagHelper.adm0DynTag(adm0._2.countryCode.toString))
    }
    val tmAdm0 = TokenMatchers.ngramExtraTag(
      adm0Name2Tag,
      dict,
      TagHelper.TmAdm0
    )
    tmlst += tmAdm0

    val mmgr = new MatcherMgr(
      tmlst.toList,
      cmlst.toList,
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

//  private val addExtraAdm0Tag:TmMatchPProc[String] = (m, tag) => {
//    m.addTag(TagHelper.adm0DynTag(tag))
//    m
//  }

  import org.ditw.demo1.TestData._
  import org.ditw.demo1.gndata.GNCntry._

  testTm(testCountries, List(
    "Worcester County, Massachusetts, USA.",
    "City of Boston, Massachusetts, USA.",
    "Boston, Massachusetts, USA.",
    "massachusetts",
    "Worcester County",
    "City of Boston"
  ))
}
