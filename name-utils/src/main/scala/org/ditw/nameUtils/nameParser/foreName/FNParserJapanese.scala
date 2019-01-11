package org.ditw.nameUtils.nameParser.foreName

import org.ditw.nameUtils.nameParser.ForeNameParsers.Consts.EmptyStrSeq
import org.ditw.nameUtils.nameParser.ForeNameParsers.TForeNameParser
import org.ditw.nameUtils.nameParser.ForeNameParsers.TForeNameParser

/**
  * Created by dev on 2018-02-13.
  */
private[nameParser] object FNParserJapanese extends TForeNameParser {

  import org.ditw.nameUtils.nameParser.ParserHelpers._
  override def parseNames(parts: IndexedSeq[String]): (IndexedSeq[String], IndexedSeq[String]) = {
    //throw new IllegalArgumentException("not implemented")
    val parsed =
      if (parts.length <= 0) EmptyStrSeq
      else splitComponents(parts(0))
    parsed -> EmptyStrSeq
  }

}
