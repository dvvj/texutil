package org.ditw.nameUtils.nameParser.langSpecData

import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.sortStringSeq
/**
  * Created by dev on 2017-08-25.
  */
object FrenchLangData {

  // https://en.wikipedia.org/wiki/French_name
  // particles
  // de, du, de la, dele, del
  private[nameParser] object PrefixUnits {
    val de = "de"
    val du = "du"
    val dele = "dele"
    val del = "del"
    val la = "la"
  }
  import PrefixUnits._

  private[nameParser] val FrenchSortedPrefixes = sortStringSeq(
    Seq(
      IndexedSeq(de), // -> IndexedSeq(aan),
      IndexedSeq(du),
      IndexedSeq(dele),
      IndexedSeq(del),
      IndexedSeq(la),
      IndexedSeq(de, la)
    )
  )


}
