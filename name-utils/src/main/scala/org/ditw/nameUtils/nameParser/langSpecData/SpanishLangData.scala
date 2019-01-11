package org.ditw.nameUtils.nameParser.langSpecData

import java.nio.charset.StandardCharsets

import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.sortStringSeq
import org.ditw.nameUtils.nameParser.utils.inferers.NameComponentInferers.getClass
import org.apache.commons.io.IOUtils

/**
  * Created by dev on 2017-08-30.
  */
object SpanishLangData {
  // https://en.wikipedia.org/wiki/French_name
  // particles
  // de, du, de la, dele, del
  private[nameParser] object PrefixUnits extends Serializable {
    val de = "de"
    val del = "del"
    val la = "la"
  }
  import PrefixUnits._

  private[nameParser] val SpanishSortedPrefixes = sortStringSeq(
    Seq(
      IndexedSeq(de), // -> IndexedSeq(aan),
      IndexedSeq(del),
      IndexedSeq(de, la)
    )
  )

  private[nameParser] val SpanishConj = "y"

  import collection.JavaConverters._

  private def readResourceAsStrSet(path:String):Set[String] = {
    val rs = getClass.getResourceAsStream(path)
    val res = IOUtils.readLines(rs, StandardCharsets.UTF_8).asScala.toSet
    rs.close()
    res.map(_.toLowerCase())
  }
  private[nameParser] val HispanicNameParts =
    readResourceAsStrSet("/nameparser/hispanic/lastnames.txt")
  private[nameParser] val SpanishReligiousNames =
    readResourceAsStrSet("/nameparser/hispanic/religious_firstnames.txt")
    .map(_.split("\\s+")).toIndexedSeq.sortBy(_.length)(Ordering[Int].reverse)
}
