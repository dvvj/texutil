package org.ditw.nameUtils.nameParser.utils

import org.ditw.nameUtils.nameParser.langSpecData.jpn.HanaAndLastName
import org.ditw.nameUtils.nameParser.langSpecData.{ChineseLangData, PfxParsers, PortugueseLangData, SpanishLangData}
import org.ditw.nameUtils.nameParser.utils.NameLanguages._tryGetLanguage
import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.PrefixParser
import org.ditw.nameUtils.nameParser.utils.inferers.LanguageInferers
import org.ditw.nameUtils.nameParser.langSpecData.jpn.HanaAndLastName
import org.ditw.nameUtils.nameParser.langSpecData.{ChineseLangData, PfxParsers}
import org.ditw.nameUtils.nameParser.utils.inferers.LanguageInferers

/**
  * Created by dev on 2017-08-23.
  */
object NameLanguages extends Serializable {
  object LanguageEnum extends Enumeration {
    type LanguageEnum = Value
    val Hispanic, Chinese, Dutch, Japanese, French, Polish,
      Czech, Sami, Icelandic,
      Romanian, German, Turkish,
      Faroese, Hungarian, Irish,
      //Portuguese, Spanish,
      Croation,
      Estonian, Latvian, Lithuanian,
      Albanian, Italian, Danish,
      Norwegian, Finnish, Swedish,
      Vietnamese,
      Undetermined = Value
  }
  import LanguageEnum._
  import org.ditw.nameUtils.nameParser.MedlineParsers._

  import org.ditw.nameUtils.nameParser.ParserHelpers._
  import org.ditw.nameUtils.nameParser.ParserHelpers.Consts._

  private[nameParser] val LangCodeMap = Map(
    Chinese -> "ZHO",
    Dutch -> "NLD",
    Japanese -> "JPN"
  )
  def getLangCode(lang:LanguageEnum):Option[String] = LangCodeMap.get(lang)
  private[nameParser] val LangCodeReverseMap = LangCodeMap.map(p => p._2 -> p._1)

  private[nameParser] val EmptyLangs = Iterable[LanguageEnum]()

  private def isChineseAuthorName(namePart:String, checkSplit:Boolean, ignoreInitials:Boolean):Boolean = {
    import Pinyin.isPinyin
    val nameComps = splitTrim(NamePartCompSplitPtn, namePart)
    val toCheck = if (ignoreInitials) nameComps.filter(_.length > 1) else nameComps
    toCheck.forall(c => isPinyin(c, checkSplit))
  }

  private def checkChineseNameComps(nameParts:String*):Int = {
    val comps = nameParts.flatMap(splitComponents)
    comps.map { comp =>
      if (comp.length == 1 || Pinyin.isPinyin(comp)) 1 // 1 comp
      else {
        val sp = Pinyin.trySplit(comp)
        if (sp.nonEmpty) 2
        else throw new IllegalArgumentException(s"[$nameParts] not a valid Chinese pinyin")
      }
    }.sum
  }

  private def verifyChineseLastName(lastName:String, foreName:String):Boolean = {
    val comps = splitComponents(lastName)
    if (comps.length == 1 && !Pinyin.isPinyin(comps.head)) {
      // check first name
      val fnComps = splitComponents(foreName)
      // all the first name components are initials cannot find a valid family name
      if (fnComps.forall(_.length == 1)) false
      else if (fnComps.exists(c => c.length > 1 && !Pinyin.isPinyin(c))) false
      else true
    }
    else {
      val lnComps = splitComponents(lastName)
      if (lnComps.length < 1) false
      else {
        val lnComp = lnComps.last
        val fnComps = lnComps.slice(0, lnComps.size-1)
        // all the first name components are initials cannot find a valid family name
        if (!Pinyin.isPinyin(lnComp) && fnComps.forall(_.length == 1)) false
        else true
      }
    }
  }

  private[nameParser]
  case class PrefixCheckResult(lang:LanguageEnum, prefix:IndexedSeq[String])

  private[nameParser] def checkPrefix(pfxParser:PrefixParser, in:NormInput):Option[PrefixCheckResult] = {
    val lastName = inputLastName(in)
    val foreName = inputForeName(in)

    val lnParts = splitTrim(NamePartCompSplitPtn, lastName)
    val lnResult = pfxParser.findInLastName(lnParts)

    val fnParts = splitTrim(NamePartCompSplitPtn, foreName)
    val fnResult = pfxParser.findInForeName(fnParts)

    val parts = IndexedSeq(fnResult, lnResult).flatten
    if (parts.nonEmpty)
      Option(PrefixCheckResult(pfxParser.lang, parts.flatten))
    else None
  }


//  def checkLastName(comps:IndexedSeq[String]):Iterable[LanguageEnum] =
//    LastNameComp2Lang.filterKeys(comps.contains).flatMap(_._2)

  private def getLanguageByNameParts(lastName:String, foreName:String)
    :(Iterable[LanguageEnum], Iterable[LanguageEnum]) = {

    // check special components in last name
//    val lastNameComps = splitComponents(lastName)
//    val lastNameCheckResult = checkLastName(lastNameComps)
//    if (lastNameCheckResult.nonEmpty)
//      (lastNameCheckResult, EmptyLangs)
    val inf = LanguageInferers.inferLanguages(foreName, lastName)
    if (inf._1.nonEmpty) (inf._1, EmptyLangs)
    else {
      val in = genNormInput(lastName, foreName)
      val pfxCheckResults = PfxParsers.all.flatMap(checkPrefix(_, in))
      if (pfxCheckResults.nonEmpty) {
        val langsByPrefixes = pfxCheckResults.toList.sortBy(_.prefix.length)(Ordering[Int].reverse)
        val atLeastLen2 = langsByPrefixes.filter(_.prefix.length >= 2)
        if (atLeastLen2.nonEmpty) (atLeastLen2.map(_.lang), EmptyLangs)
        else (EmptyLangs, langsByPrefixes.map(_.lang))
      }
      else (EmptyLangs, EmptyLangs)
    }

  }
  def tryGetLanguage(in:NormInput):Iterable[LanguageEnum] = {

    val lastName = inputLastName(in)
    val foreName = inputForeName(in)
    val suffix = inputSuffix(in)
    _tryGetLanguage(lastName, foreName, suffix)
  }

  def _tryGetLanguage(lastName:String, foreName:String, suffix:Option[String]):Iterable[LanguageEnum] = {
    if (
      !ChineseLangData.isLastNameBlacklisted(lastName) &&
      !ChineseLangData.isBlacklisted(lastName) &&
      !ChineseLangData.isBlacklisted(foreName) &&
      isChineseAuthorName(lastName, true, true) && // todo: check double family-names
      isChineseAuthorName(foreName, true, true) &&
      verifyChineseLastName(lastName, foreName) &&
      checkChineseNameComps(lastName, foreName) <= ChineseLangData.MaxChineseNameComps
      //suffix.isEmpty
    ) {
      Iterable(Chinese)
    }
    else if (HanaAndLastName.isJapaneseName(lastName, foreName)) {
      Iterable(Japanese)
    }
    else {
      val (confirmedLangs, possibleLangs) = getLanguageByNameParts(lastName, foreName)

      if (confirmedLangs.nonEmpty) confirmedLangs
      else {
        val alphabetLangInfo = AlphabetHelpers.langByAlphabets(Seq(lastName, foreName))

        if (alphabetLangInfo.nonEmpty) {
          // debug trace:
          if (alphabetLangInfo.size == 1 || alphabetLangInfo.head._2 > 1) {
            // either this is the only possible language or we have more than 1 non-standard letters
            //println(s"[$lastName][$foreName]: $alphabetLangInfo")
            alphabetLangInfo.map(_._1)
          }
          else Iterable(Undetermined)
        }
        else Iterable(Undetermined)
      }


    }
  }


}
