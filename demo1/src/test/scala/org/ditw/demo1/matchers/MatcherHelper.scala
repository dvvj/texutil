package org.ditw.demo1.matchers
import org.ditw.demo1.TestData
import org.ditw.demo1.TestData.testDict
import org.ditw.extract.XtrMgr
import org.ditw.matcher.{MatcherMgr, TCompMatcher, TTkMatcher, TokenMatchers}

import scala.collection.mutable.ListBuffer

object MatcherHelper {
  val (mmgr:MatcherMgr, xtrMgr:XtrMgr[Long]) = {
    MatcherGen.gen(TestData.testGNSvc, testDict)
  }
}
