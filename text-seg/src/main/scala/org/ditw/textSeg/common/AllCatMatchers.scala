package org.ditw.textSeg.common
import org.ditw.common.Dict
import org.ditw.matcher.{MatchPool, MatcherMgr}
import org.ditw.textSeg.Settings.TknrTextSeg
import org.ditw.textSeg.catSegMatchers.Cat1SegMatchers
import org.ditw.textSeg.common.CatSegMatchers.TSegMatchers4Cat
import org.ditw.tknr.Tokenizers.TTokenizer

object AllCatMatchers {

  def mmgrFrom(
    catSegMatchers:TSegMatchers4Cat*
  ):MatcherMgr = {
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

  def run(
    mmgr:MatcherMgr,
    inStr:String,
    tokenizer:TTokenizer = TknrTextSeg,
    dict: Dict = Vocabs._Dict
  ): MatchPool = {
    val matchPool = MatchPool.fromStr(inStr, tokenizer, dict)
    mmgr.run(matchPool)
    matchPool
  }

}
