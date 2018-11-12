package org.ditw.textSeg.common
import org.ditw.common.InputHelpers
import org.ditw.common.InputHelpers.splitVocabEntries
import org.ditw.matcher.TokenMatchers.ngramT
import org.ditw.matcher.{CompMatcherNs, TCompMatcher, TokenMatchers}

object AssiMatchers extends Serializable {
  import Vocabs._
  import Tags._

  private val _TmDept = TmTagPfx + "Dept"
  private val _TmDeptType = TmTagPfx + "DeptType"

  private val _ExtraTmData = List(
    TmOf -> Set("of"),
    _TmDept -> _DeptWords,
    _TmDeptType -> _DeptTypes
  )

  private[textSeg] val _ExtraTms = _ExtraTmData.map(
    tmd => ngramT(splitVocabEntries(tmd._2), Vocabs.AllVocabDict, tmd._1)
  )

  private[textSeg] val _CmDeptOfTag = CmTagPfx + "DeptOf"
  private val _CmDeptOf = CompMatcherNs.lngOfTags(
    IndexedSeq(_TmDept, TmOf, _TmDeptType),
    _CmDeptOfTag
  )
  private[textSeg] val _ExtraCms:List[TCompMatcher] = List(_CmDeptOf)


}
