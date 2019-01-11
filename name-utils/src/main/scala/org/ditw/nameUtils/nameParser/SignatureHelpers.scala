package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult
import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult

/**
  * Created by dev on 2017-09-04.
  */
object SignatureHelpers extends Serializable {

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._

  private type SignatureParser = NormResult => String

  private def parseMultiFirstNamePartsSignature(nr:NormResult):String = {
    nr.firstNames.map(_.head.toUpper).mkString
  }

  private val _SignatureParserMap = Map[LanguageEnum, SignatureParser](
    Chinese -> parseMultiFirstNamePartsSignature,
    Japanese -> parseMultiFirstNamePartsSignature
  )

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LangCodeMap

  private def generalGenerator(nr:NormResult):String = {
    (nr.firstNames ++ nr.extraNames)
      .map(_.head)
      .filter(_.isUpper)
      .mkString
  }

  def genSignature(result:NormResult):String = {
    val langCode =
      if (result.lang.nonEmpty) LangCodeMap.get(result.lang.get) else None

    val p2 =
      if (result.lang.nonEmpty) {
        val lang = result.lang.get
        if (_SignatureParserMap.contains(lang)) {
          _SignatureParserMap(lang)(result)
        }
        else generalGenerator(result)
      }
      else generalGenerator(result)

    if (langCode.nonEmpty) s"${langCode.get}:$p2"
    else p2
  }
}
