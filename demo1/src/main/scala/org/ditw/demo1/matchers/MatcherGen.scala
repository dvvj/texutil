package org.ditw.demo1.matchers
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.demo1.gndata.TGNMap

object MatcherGen extends Serializable {
  import InputHelpers._

  def loadDict(adm0s: Iterable[TGNMap]):Dict = {
    val keys = adm0s.flatMap(_.admNameMap.values.flatMap(_.keySet))
    val adm0Names = adm0s.flatMap(_.self.get.queryNames)
    val words = splitVocabEntries(keys.toSet ++ adm0Names)
      .map(_.toIndexedSeq)
    InputHelpers.loadDict(words)
  }

  //def gen(adm0s: Iterable[TGNMap]):
}
