package org.ditw.textSeg.common
import org.ditw.matcher.MatcherMgr
import org.ditw.textSeg.cat1.Cat1SegMatchers

object AllCatMatchers {

  private [textSeg] val matcherMgr = new MatcherMgr(
    Cat1SegMatchers.catTms(),
    Cat1SegMatchers.catCms()
  )

}
