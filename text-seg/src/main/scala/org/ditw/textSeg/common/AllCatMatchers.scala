package org.ditw.textSeg.common
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.matcher._
import org.ditw.textSeg.catSegMatchers.Cat1SegMatchers
import org.ditw.textSeg.common.CatSegMatchers.TSegMatchers4Cat
import org.ditw.tknr.Tokenizers.TTokenizer

object AllCatMatchers {

  import org.ditw.matcher.TokenMatchers._
  import Tags._
  import org.ditw.tknr.TknrHelpers._

  import AssiMatchers._
  import Vocabs._
  def mmgrFrom(
    catSegMatchers:TSegMatchers4Cat*
  ):MatcherMgr = {
    val postprocs = catSegMatchers.map(_.postproc)
    new MatcherMgr(
      _ExtraTms ++ catSegMatchers.flatMap(_.tms),
      List(),
      _ExtraCms ++ catSegMatchers.flatMap(_.cms),
      postprocs
    )
  }

  def run(
    mmgr:MatcherMgr,
    inStr:String,
    tokenizer:TTokenizer = TknrTextSeg,
    dict: Dict = Vocabs.AllVocabDict
  ): MatchPool = {
    val matchPool = MatchPool.fromStr(inStr, tokenizer, dict)
    mmgr.run(matchPool)
    matchPool
  }

}
