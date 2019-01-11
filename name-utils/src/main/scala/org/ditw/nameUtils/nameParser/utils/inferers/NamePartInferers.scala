package org.ditw.nameUtils.nameParser.utils.inferers

import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import LanguageInferers._
/**
  * Created by dev on 2017-08-31.
  */
object NamePartInferers {
  val NamePart2Lang = regexMap(
    """e""" -> Hispanic,
    """y""" -> Hispanic
  )
  def namePartInferer(
    foreName:String,
    foreNameParts:IndexedSeq[String],
    foreNameComps:IndexedSeq[String],
    lastName:String,
    lastNameParts:IndexedSeq[String],
    lastNameComps:IndexedSeq[String]
  ):(Iterable[LanguageEnum], Iterable[LanguageEnum]) = {
    val allComps = foreNameParts ++ lastNameParts
    confirmedInference(
      inferByRegex(NamePart2Lang, allComps)
    )
  }

}
