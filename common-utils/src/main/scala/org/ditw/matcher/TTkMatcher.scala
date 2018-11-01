package org.ditw.matcher
import scala.collection.mutable

trait TTkMatcher extends Serializable {
  val tag:Option[String]
  def run(matchPool: MatchPool)
  : Set[TkMatch] = {
    val res = mutable.Set[TkMatch]()

    matchPool.input.tknrResult.linesOfTokens.indices.foreach { lineIdx =>
      res ++= runAtLine(matchPool, lineIdx)
    }

    res.toSet
  }

  def runAtLine(matchPool: MatchPool, lineIdx:Int):Set[TkMatch] = {
    val sot = matchPool.input.tknrResult.linesOfTokens(lineIdx)
    val res = mutable.Set[TkMatch]()
    sot.indices.foreach { idx =>
      res ++= runAtLineFrom(matchPool, lineIdx, idx)
    }
    res.toSet
  }

  def runAtLineFrom(matchPool: MatchPool, lineIdx:Int, start:Int):Set[TkMatch]
}
