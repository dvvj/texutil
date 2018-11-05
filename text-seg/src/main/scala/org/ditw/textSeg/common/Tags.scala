package org.ditw.textSeg.common

object Tags extends Serializable {
  private val TmTagPfx = "__TagTm"
  private val SegTagPfx = "_TagSeg"
  private val SegLeftStopTagPfx = "_TagSegLStop"
  private val SegRightStopTagPfx = "_TagSegRStop"

  case class TagGroup(
    keywordTag:String,
    segTag:String,
    segLeftStopTag:String,
    segRightStopTag:String
  )

  private def tagGroup(groupTag:String):TagGroup = TagGroup(
    TmTagPfx + groupTag, // tm
    SegTagPfx + groupTag, // seg
    SegLeftStopTagPfx + groupTag, // seg stop left
    SegRightStopTagPfx + groupTag // seg stop right
  )

  private [textSeg] val Tag4Corp = "Corp"
  private [textSeg] val TagGroup4Corp:TagGroup = tagGroup(Tag4Corp)

  private [textSeg] val Tag4Univ = "Univ"
  private [textSeg] val TagGroup4Univ:TagGroup = tagGroup(Tag4Univ)
}
