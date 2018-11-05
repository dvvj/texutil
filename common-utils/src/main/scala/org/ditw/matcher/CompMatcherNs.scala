package org.ditw.matcher
import scala.collection.mutable.ListBuffer

object CompMatcherNs {
  import CompMatchers._
  // ------------ OR
  private[matcher] class CmOr(
    protected val subMatchers:Set[TCompMatcher],
    val tag:Option[String]
  ) extends TCompMatcher with TCompMatcherN {
    override def runAtLineFrom(
      matchPool: MatchPool,
      lineIdx: Int,
      start: Int): Set[TkMatch] = {
      subMatchers.flatMap(_.runAtLineFrom(matchPool, lineIdx, start))
    }
  }

  def or(subMatchers:Set[TCompMatcher], tag:String):TCompMatcher = {
    new CmOr(subMatchers, Option(tag))
  }
  private def _seqMatches(
    curr:List[IndexedSeq[TkMatch]],
    subMatchesSeq:IndexedSeq[Set[TkMatch]],
    start:Int
  ):List[IndexedSeq[TkMatch]] = {
    if (start == subMatchesSeq.size)
      curr
    else {
      val nextSubMatches = subMatchesSeq(start)
      val next = ListBuffer[IndexedSeq[TkMatch]]()
      if (curr.isEmpty) {
        next ++= nextSubMatches.map(sm => IndexedSeq(sm))
      }
      else {
        nextSubMatches.foreach { nextSubMatch =>
          curr.foreach { subMatchesSoFar =>
            val lastMatch = subMatchesSoFar.last
            if (nextSubMatch.range.start >= lastMatch.range.end) {
              next += (subMatchesSoFar :+ nextSubMatch)
            }
          }
        }
      }
      _seqMatches(next.toList, subMatchesSeq, start+1)
    }
  }

  private def seqMatches(
    subMatchesSeq:IndexedSeq[Set[TkMatch]]
  ):List[IndexedSeq[TkMatch]] = {
    _seqMatches(List(), subMatchesSeq, 0)
  }

  // ------------ Seq
  trait TCompMatcherSeq extends TCompMatcherN with TDefRunAtLineFrom {
    protected val subMatchers:IndexedSeq[TCompMatcher]

    private def matchCandidates(
      matchPool: MatchPool,
      subMatchesSeq:IndexedSeq[Set[TkMatch]]
    ):Set[TkMatch] = {
      val lineIdx2Matches = matchPool.input.linesOfTokens.indices.map { idx =>
        val matchesSeqInLineIdx = subMatchesSeq.map(_.filter(_.range.lineIdx == idx))
        idx -> matchesSeqInLineIdx
      }
      val matchesInLines = lineIdx2Matches.map(p => seqMatches(p._2))
      val res = matchesInLines.flatMap(l => l.map(ch => TkMatch.fromChildren(ch)))
      res.toSet
    }

    protected def filterCandidates(candidates:Set[TkMatch]):Set[TkMatch]

    override def run(matchPool: MatchPool)
      : Set[TkMatch] = {
      val subMatchesSeq:IndexedSeq[Set[TkMatch]] = subMatchers.map(_.run(matchPool))
      val candidates = matchCandidates(matchPool, subMatchesSeq)
      filterCandidates(candidates)
    }
  }

  private[matcher] class CmLNGram(
    protected val subMatchers:IndexedSeq[TCompMatcher],
    val tag:Option[String]
  ) extends TCompMatcherSeq {
    override protected def filterCandidates(candidates: Set[TkMatch])
      : Set[TkMatch] = {
      candidates.filter { c =>
        val nextToEachOther = (0 to c.children.size-2).forall { idx =>
          val curr = c.children(idx)
          val next = c.children(idx+1)
          curr.range.end == next.range.start
        }
        nextToEachOther
      }
    }
  }

  def seq(
    subMatchers:IndexedSeq[TCompMatcher],
    tag:String
  ):TCompMatcher = {
    new CmLNGram(subMatchers, Option(tag))
  }
}
