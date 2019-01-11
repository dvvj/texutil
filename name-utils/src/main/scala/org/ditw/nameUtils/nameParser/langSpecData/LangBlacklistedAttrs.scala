package org.ditw.nameUtils.nameParser.langSpecData

/**
  * Created by dev on 2017-09-07.
  */
object LangBlacklistedAttrs extends Serializable {

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
  import org.ditw.nameUtils.nameParser.ParserHelpers.ResultAttrKeyEnum._
  private[nameParser] val _BlacklistMap = Map(
    Chinese -> Set(Suffix, Preposition)
  )
  private val EmptyAttrKeySet = Set[ResultAttrKeyEnum]()

  private def getLangBlacklistedAttrs(lang:Option[LanguageEnum]):Set[ResultAttrKeyEnum] =
    if (lang.nonEmpty) _BlacklistMap.getOrElse(lang.get, EmptyAttrKeySet)
    else EmptyAttrKeySet

  private[nameParser] def isBlockedIn(resultAttr:ResultAttrKeyEnum, lang:Option[LanguageEnum]) = {
    getLangBlacklistedAttrs(lang).contains(resultAttr)
  }

  private[nameParser] def filterOutAttrByLang[T](
    lang:Option[LanguageEnum],
    attrs:Iterable[T],
    getAttr:T => ResultAttrKeyEnum
  ):Iterable[T] = {
    val blackList = getLangBlacklistedAttrs(lang)
    if (blackList.nonEmpty) attrs.filter(t => !blackList.contains(getAttr(t)))
    else attrs
  }
}
