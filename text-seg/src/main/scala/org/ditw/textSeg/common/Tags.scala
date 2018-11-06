package org.ditw.textSeg.common

object Tags extends Serializable {
  private val TmTagPfx = "__TagTm"
  private val GazTagPfx = "__TagGaz"
  private val TmStopTagPfx = "__TagStopTm"
  private val SegTagPfx = "_TagSeg"
  private val SegLeftStopTagPfx = "_TagSegLStop"
  private val SegRightStopTagPfx = "_TagSegRStop"

  case class TagGroup(
    keywordTag:String,
    gazTag:String,
    segTag:String,
    stopWordsTag:String,
    segLeftStopTag:String,
    segRightStopTag:String
  )

  private def tagGroup(groupTag:String):TagGroup = TagGroup(
    TmTagPfx + groupTag, // tm
    GazTagPfx + groupTag,
    SegTagPfx + groupTag, // seg
    TmStopTagPfx + groupTag, // stop words tag
    SegLeftStopTagPfx + groupTag, // seg stop left
    SegRightStopTagPfx + groupTag // seg stop right
  )

  private val Tag4Corp = "Corp"
  val TagGroup4Corp:TagGroup = tagGroup(Tag4Corp)

  private val Tag4Univ = "Univ"
  val TagGroup4Univ:TagGroup = tagGroup(Tag4Univ)
}
