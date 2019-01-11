package org.ditw.nameUtils.nameParser.utils.inferers

import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import LanguageInferers._

/**
  * Created by dev on 2017-08-31.
  */
object FullNameInferers extends Serializable {

  val FullName2Lang = regexMap(
    """\s[vV]an\s+[dD](e|en|er)\s""" -> Dutch
  )

  def inferFromFullName(fullName:String):Iterable[LanguageEnum] = {
    inferFromNameStringByRegex(FullName2Lang, fullName)
  }

  def fullNameInferer(
    foreName:String,
    foreNameParts:IndexedSeq[String],
    foreNameComps:IndexedSeq[String],
    lastName:String,
    lastNameParts:IndexedSeq[String],
    lastNameComps:IndexedSeq[String]
  ):(Iterable[LanguageEnum], Iterable[LanguageEnum]) = {
    confirmedInference(
      inferFromNameStringByRegex(FullName2Lang, s"$foreName $lastName")
    )
  }
}
