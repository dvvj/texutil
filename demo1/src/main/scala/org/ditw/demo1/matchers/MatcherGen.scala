package org.ditw.demo1.matchers
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.demo1.gndata.TGNMap

object MatcherGen extends Serializable {
  import InputHelpers._

  def loadDict(adm0s: Iterable[TGNMap]):Dict = {
    val keys = adm0s.flatMap(_.admNameMap.flatMap(_._2.keySet))
    InputHelpers.loadDict(keys.toSet)
  }

  //def gen(adm0s: Iterable[TGNMap]):
}
