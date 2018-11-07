package org.ditw.textSeg
import org.ditw.common.TkRange
import org.ditw.matcher.CompMatchers.TDefRunAtLineFrom
import org.ditw.matcher.{MatchPool, TCompMatcher, TkMatch}

object SegMatchers {

  private val RangeBy2_1:(Int, Int) = (2, 1)
  private[textSeg] class SegBySfx(
    private val tagsContained:Set[String],
    private val sfxs:Set[String],
    private val canBeStart:Boolean = true,
    val tag:Option[String]
  ) extends TCompMatcher with TDefRunAtLineFrom {
    override def runAtLine(
      matchPool: MatchPool,
      lineIdx: Int): Set[TkMatch] = {
      val matches = matchPool.get(tagsContained)
        .filter(_.range.lineIdx == lineIdx)
      val segMatches = matches.map { m =>
        val lot = matchPool.input.linesOfTokens(m.range.lineIdx)
        var newRange = lot.rangeBy(m.range, sfxs)
        if (!canBeStart && m.range.start == newRange.start) {
          newRange = lot.rangeBySfxs(m.range, sfxs, RangeBy2_1)
        }
        TkMatch.oneChild(newRange, m, tag)
      }
      segMatches
    }

    override def getRefTags(): Set[String] = tagsContained
  }

  private[textSeg] class SegByPfxSfx(
    private val tagsContained:Set[String],
    private val pfxs:Set[String],
    private val sfxs:Set[String],
    private val canBeStart:Boolean = true,
    val tag:Option[String]
  ) extends TCompMatcher with TDefRunAtLineFrom {
    override def runAtLine(
                            matchPool: MatchPool,
                            lineIdx: Int): Set[TkMatch] = {
      val matches = matchPool.get(tagsContained)
        .filter(_.range.lineIdx == lineIdx)
      val segMatches = matches.map { m =>
        val lot = matchPool.input.linesOfTokens(m.range.lineIdx)
        var newRange = lot.rangeBy(m.range, sfxs)
        if (!canBeStart && m.range.start == newRange.start) {
          newRange = lot.rangeByPfxs(m.range, sfxs, RangeBy2_1)
        }
        TkMatch.oneChild(newRange, m, tag)
      }
      segMatches
    }

    override def getRefTags(): Set[String] = tagsContained
  }


  private[textSeg] class SegByTags(
    private val matcher:TCompMatcher,
    private val leftTags:Set[String],
    private val rightTags:Set[String],
    val tag:Option[String]
  ) extends TCompMatcher with TDefRunAtLineFrom {
    override def runAtLine(
      matchPool: MatchPool,
      lineIdx: Int
    ): Set[TkMatch] = {
      val candidates = matcher.runAtLine(matchPool, lineIdx)
      val leftRanges = matchPool.get(leftTags).filter(_.range.lineIdx == lineIdx)
        .map(_.range)
      val rightRanges = matchPool.get(rightTags).filter(_.range.lineIdx == lineIdx)
        .map(_.range)
      val c1 = candidates.map { c =>
        var maxLeft = c.range.start
        val firstChildStart = c.children.head.range.start // todo: what if no child?
        leftRanges.foreach { lr =>
          if (lr.overlap(c.range)) {
            if (lr.end > maxLeft && lr.end <= firstChildStart) {
              maxLeft = lr.end
            }
          }
        }
        val newStart = maxLeft

        val lastChildEnd = c.children.last.range.end
        var minRight = c.range.end
        rightRanges.foreach { rr =>
          if (rr.overlap(c.range)) {
            if (rr.start < minRight && rr.start >= lastChildEnd) {
              minRight = rr.start
            }
          }
        }
        val newEnd = minRight
        if (newStart != c.range.start || newEnd != c.range.end) {
          TkMatch.noChild(
            TkRange(matchPool.input, lineIdx, newStart, newEnd),
            tag
          )
        }
        else c
      }
      c1
    }

    private val refTags = leftTags ++ rightTags ++ matcher.tag
    override def getRefTags(): Set[String] = refTags
  }

  def segByPfxSfx(
    tagsContained:Set[String],
    sfxs:Set[String],
    canBeStart:Boolean,
    tag:String
  ):TCompMatcher = {
    new SegBySfx(tagsContained, sfxs, canBeStart, Option(tag))
  }

  def segByTags(
    matcher:TCompMatcher,
    leftTags:Set[String],
    rightTags:Set[String],
    tag:String
  ):TCompMatcher = {
    new SegByTags(matcher, leftTags, rightTags, Option(tag))
  }
}
