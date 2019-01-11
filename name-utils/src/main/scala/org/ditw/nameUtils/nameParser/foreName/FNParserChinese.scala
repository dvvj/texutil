package org.ditw.nameUtils.nameParser.foreName

import org.ditw.nameUtils.nameParser.ForeNameParsers.Consts._
import org.ditw.nameUtils.nameParser.ForeNameParsers._
import org.ditw.nameUtils.nameParser.utils.Pinyin
import org.ditw.nameUtils.nameParser.ForeNameParsers.TForeNameParser
import org.ditw.nameUtils.nameParser.utils.Pinyin

/**
  * Created by dev on 2017-08-24.
  */
private[nameParser] object FNParserChinese
  extends TForeNameParser {

  import org.ditw.nameUtils.nameParser.ParserHelpers._

  //override def parsePossibleLastNameParts(parts: IndexedSeq[String]): NormParseResult = None

  override def parseNames(parts: IndexedSeq[String]): (IndexedSeq[String], IndexedSeq[String]) = {
    val comps = parts.flatMap(splitComponents)
    val firstNames =
      if (comps.length > 1) comps
      else if (comps.length == 1) {
        val n = comps.head //.toLowerCase
        if (Pinyin.isPinyin(n)) IndexedSeq(n)
        else {
          val split = Pinyin.trySplit(n)
          if (split.nonEmpty) {
            val p = split.get
            IndexedSeq(p._1, p._2)
          }
          else IndexedSeq(n)
        }
      }
      else { // parts.length == 0
        EmptyStrSeq
      }

    (firstNames, EmptyStrSeq)
  }
}