package org.ditw.nameUtils.nameParser.langSpecData

import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.sortStringSeq

/**
  * Created by dev on 2017-09-01.
  */
object GermanLangData {
  private[nameParser] object PrefixUnits extends Serializable {
    val von = "von"
    val zu = "zu"
    val und = "und"
    val vom = "vom"
    val zur = "zur"
    val zum = "zum"
  }
  import PrefixUnits._

  private[nameParser] val GermanSortedPrefixes = sortStringSeq(
    Seq(
      IndexedSeq(von), // -> IndexedSeq(aan),
      IndexedSeq(zu),
      IndexedSeq(vom),
      IndexedSeq(zur),
      IndexedSeq(zum),
      IndexedSeq(von, "der"),
      IndexedSeq(von, zur),
      IndexedSeq(von, und, zu)
    )
  )

}
