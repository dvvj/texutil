package org.ditw.exutil1
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.tknr.TknrHelpers.TknrTextSeg

object TestHelpers extends Serializable {
  private[exutil1] val testTokenizer = TknrTextSeg()
  private[exutil1] val testDict: Dict = InputHelpers.loadDict()
}
