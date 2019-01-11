package org.ditw.nameUtils.nameParser.langSpecData

import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.sortStringSeq

/**
  * Created by dev on 2017-08-30.
  */
object PortugueseLangData {
  // https://en.wikipedia.org/wiki/French_name
  // particles
  // de, du, de la, dele, del
  private[nameParser] object PrefixUnits extends Serializable {
    val da = "da"
    val das = "das"
    val de = "de"
    val _do = "do"
    val dos = "dos"
  }
  import PrefixUnits._

  private[nameParser] val PortugueseSortedPrefixes = sortStringSeq(
    Seq(
      IndexedSeq(da), // -> IndexedSeq(aan),
      IndexedSeq(das),
      IndexedSeq(de),
      IndexedSeq(_do),
      IndexedSeq(dos)
    )
  )

  private[nameParser] val PortugueseConj = "e"
}
