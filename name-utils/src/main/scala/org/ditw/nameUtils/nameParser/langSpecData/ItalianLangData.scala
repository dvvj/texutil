package org.ditw.nameUtils.nameParser.langSpecData

import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.sortStringSeq

/**
  * Created by dev on 2017-08-30.
  */
object ItalianLangData {
  private[nameParser] object PrefixUnits extends Serializable {
    val de = "de"
    val di = "di"
    val de_appos = "de'"
  }
  import PrefixUnits._

  private[nameParser] val ItalianSortedPrefixes = sortStringSeq(
    Seq(
      IndexedSeq(de), // -> IndexedSeq(aan),
      IndexedSeq(di),
      IndexedSeq(de_appos)
    )
  )
}
