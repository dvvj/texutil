package org.ditw.demo1.matchers
import org.ditw.demo1.gndata.GNCntry.GNCntry
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.TokenMatchers.TmMatchPProc
import org.ditw.matcher._
import org.ditw.tknr.{TknrHelpers, Tokenizers, Trimmers}

import scala.collection.mutable.ListBuffer

object TmTest1 extends App {
  import org.ditw.demo1.TestData._
  import MatcherHelper._
  def testTm(
    testStrs:Iterable[String]
  ):Unit = {

    testStrs.foreach { str =>
      val mp = MatchPool.fromStr(str, TknrHelpers.TknrTextSeg, testDict)
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

  testTm(List(
    "Washington, USA.",
    "Worcester County, Massachusetts, USA.",
    "City of Boston, Massachusetts, USA.",
    "Boston, Massachusetts, USA.",
    "U.S. Secret Service, Forensic Services Division, Washington, DC, USA.",
    "massachusetts",
    "Worcester County",
    "City of Boston"
  ))
}
