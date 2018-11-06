package org.ditw.textSeg.common
import org.ditw.matcher.MatcherMgr
import org.ditw.textSeg.catSegMatchers.Cat1SegMatchers
import org.ditw.textSeg.common.CatSegMatchers.TSegMatchers4Cat

object AllCatMatchers {

  private [textSeg] def mmgrFrom(
    catSegMatchers:TSegMatchers4Cat*
  ) = {
    val blockerMap = catSegMatchers.map(m => m.tagGroup.stopWordsTag -> Set(m.tagGroup.segTag)).toMap
    val gazOverrideMap = catSegMatchers.map(m => m.tagGroup.gazTag -> m.tagGroup.segTag).toMap
    new MatcherMgr(
      catSegMatchers.flatMap(_.tms).toList,
      catSegMatchers.flatMap(_.cms).toList,
      List(
        MatcherMgr.postProcBlocker(blockerMap),
        MatcherMgr.postProcOverride(gazOverrideMap)
      )
    )
  }


}
