package org.ditw.textSeg
import org.ditw.matcher.{MatchPool, MatcherMgr}
import org.ditw.textSeg.Settings.{TknrTextSeg, _Dict}
import org.ditw.textSeg.common.Vocabs
import org.ditw.tknr.TknrHelpers.{loTFrom, resultFrom}
import org.ditw.tknr.{TknrHelpers, TknrResult}
import org.scalatest.Matchers

object TestHelpers extends Matchers {

  private [textSeg] def testDataTuple(
    testStr:String,
    testContent:IndexedSeq[IndexedSeq[String]]
  ):(String, TknrResult) = {
    testStr ->
      resultFrom(
        testStr,
        _Dict,
        IndexedSeq(loTFrom(testContent))
      )
  }

  def runAndVerifyRanges(
    mmgr:MatcherMgr,
    inStr:String, tag:String, expRanges:Set[(Int, Int, Int)]
  ):Unit = {
    val matchPool = MatchPool.fromStr(inStr, TknrTextSeg, Vocabs._Dict)
    mmgr.run(matchPool)
    val res = matchPool.get(tag)
    val resRanges = res.map(_.range)
    val expRngs = expRanges.map(tp => TknrHelpers.rangeFromTp3(matchPool.input, tp))
    resRanges shouldBe expRngs
  }
}
