package org.ditw.demo1.matchers
import org.ditw.demo1.TestData
import org.ditw.demo1.TestData.testDict
import org.ditw.matcher.{MatcherMgr, TCompMatcher, TTkMatcher, TokenMatchers}

import scala.collection.mutable.ListBuffer

object MatcherHelper {
  val mmgr:MatcherMgr = {
    val adm0s = TestData.testCountries

    val tmlst = ListBuffer[TTkMatcher]()
    val cmlst = ListBuffer[TCompMatcher]()

    val adm0Name2Tag = adm0s.flatMap { adm0 =>
      val ent = adm0._2.self.get
      ent.queryNames.map(_ -> TagHelper.countryTag(adm0._2.countryCode))
    }
    val tmAdm0 = TokenMatchers.ngramExtraTag(
      adm0Name2Tag,
      testDict,
      TagHelper.TmAdm0
    )
    tmlst += tmAdm0

    adm0s.values.foreach { adm0 =>
      val (tms, cms) = Adm0Gen.genMatchers(adm0, testDict)
      tmlst ++= tms
      cmlst ++= cms
    }

    new MatcherMgr(
      tmlst.toList,
      cmlst.toList,
      List()
    )

  }
}
