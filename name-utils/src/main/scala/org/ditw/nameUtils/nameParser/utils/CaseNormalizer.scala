package org.ditw.nameUtils.nameParser.utils

import NameLanguages.LanguageEnum._

/**
  * Created by dev on 2017-09-06.
  */
object CaseNormalizer extends Serializable {

  private[nameParser] def generalNormalizer(comp:String):String = {
    if (comp.length > 1 && comp.forall(_.isUpper)) comp(0) + comp.substring(1).toLowerCase()
    else comp
  }

  private[nameParser] def dutchNormalizer(comp:String):String = {
    if (comp.length > 1 && comp.forall(_.isUpper)) {
      if (comp.startsWith("IJ")) "IJ" + comp.substring(2).toLowerCase()
      else comp(0) + comp.substring(1).toLowerCase()
    }
    else comp
  }

  private[nameParser] def chineseNormalizer(comp:String):String = {
    if (comp.length > 0) comp(0).toUpper + comp.substring(1).toLowerCase()
    else comp
  }

  type CaseNormFunc = String => String
  private[nameParser] val langNormalizerMap = Map[LanguageEnum, CaseNormFunc](
    Dutch -> dutchNormalizer,
    Chinese -> chineseNormalizer
  )

  def normalize(lang: LanguageEnum, nameComp:String):String = {
    if (langNormalizerMap.contains(lang)) langNormalizerMap(lang)(nameComp)
    else generalNormalizer(nameComp)
  }
}
