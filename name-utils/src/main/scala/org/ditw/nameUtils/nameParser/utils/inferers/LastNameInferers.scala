package org.ditw.nameUtils.nameParser.utils.inferers

import java.util.regex.Pattern

import org.ditw.nameUtils.nameParser.langSpecData.SpanishLangData
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._

/**
  * Created by dev on 2017-08-31.
  */
private[nameParser] object LastNameInferers extends Serializable {
  import LanguageInferers._

  val LastNameComp2Lang = regexMap(
    """[yY]""" -> Hispanic,
    """[eE]""" -> Hispanic
  )


  def inferFromLastNameParts(parts:IndexedSeq[String]):Iterable[LanguageEnum] =
    inferByRegex(LastNameComp2Lang, parts)

  def lastNamePartInferer(
    foreName:String,
    foreNameParts:IndexedSeq[String],
    foreNameComps:IndexedSeq[String],
    lastName:String,
    lastNameParts:IndexedSeq[String],
    lastNameComps:IndexedSeq[String]
  ):(Iterable[LanguageEnum], Iterable[LanguageEnum]) = {
    confirmedInference(
      inferByRegex(LastNameComp2Lang, lastNameParts)
    )
  }
}
