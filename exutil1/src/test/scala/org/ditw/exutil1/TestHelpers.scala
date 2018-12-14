package org.ditw.exutil1
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.extract.XtrMgr
import org.ditw.exutil1.naen.NaEnData
import org.ditw.matcher.MatcherMgr
import org.ditw.tknr.TknrHelpers.TknrTextSeg

object TestHelpers extends Serializable {
  private[exutil1] val testTokenizer = TknrTextSeg()
  private[exutil1] val testDict: Dict = InputHelpers.loadDict(
    NaEnData.allVocs
  )

  val mmgr:MatcherMgr = {
    new MatcherMgr(NaEnData.tmsNaEn(testDict),
      List(),
      List(),
      List()
    )
  }
}
