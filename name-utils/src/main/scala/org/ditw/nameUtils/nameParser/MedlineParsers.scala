package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ForeNameParsers._
import org.ditw.nameUtils.nameParser.LastNameParsers._
import org.ditw.nameUtils.nameParser.ParserHelpers.ResultAttrKeyEnum._
import org.ditw.nameUtils.nameParser.ParserHelpers._
import org.ditw.nameUtils.nameParser.foreName.{FNParserChinese, FNParserJapanese}
import org.ditw.nameUtils.nameParser.langSpecData.LangBlacklistedAttrs
import org.ditw.nameUtils.nameParser.utils.multiLN.MLNParsers
import org.ditw.nameUtils.nameParser.utils.{Pinyin, SuffixData}
import org.ditw.nameUtils.nameParser.LastNameParsers.{ChineseLastNameParser, JapaneseLastNameParser, TLastNameParser}
import org.ditw.nameUtils.nameParser.ParserHelpers.NormResult
import org.ditw.nameUtils.nameParser.foreName.{FNParserChinese, FNParserJapanese}
import org.ditw.nameUtils.nameParser.langSpecData.LangBlacklistedAttrs
import org.ditw.nameUtils.nameParser.utils.{Pinyin, SuffixData}
import org.ditw.nameUtils.nameParser.utils.multiLN.MLNParsers

/**
  * Created by dev on 2017-08-17.
  */
object MedlineParsers {
  object Consts extends Serializable {
    val EmptyStrSeq = IndexedSeq[String]()
    val EmptyStr = ""
  }
  import Consts._
  import org.ditw.nameUtils.nameParser.ParserHelpers.InputKeyEnum._

  import scala.collection.mutable

  private[nameParser] def filterParseResultByKeys(keys:Iterable[ResultAttrKeyEnum], result:NormParseResult):NormParseResult = {
    val keySet = keys.toSet
    result.filterKeys(keySet)
  }

  private[nameParser] def inputLastName(in:NormInput) = in(Medline_LastName)
  private[nameParser] def inputForeName(in:NormInput) = in.getOrElse(Medline_ForeName, EmptyStr)
  private[nameParser] def inputSuffix(in:NormInput):Option[String] = in.get(Medline_Suffix)

  trait TNameParser extends Serializable {
    //protected def convert(tin:TInput):NormInput
    def parse(tin:NormInput):Option[NormResult]
  }

  import org.ditw.nameUtils.nameParser.utils.NameLanguages._
  import LanguageEnum._

  private[nameParser] object RegisteredParsers extends Serializable {
    private[nameParser] val regiMap:Map[LanguageEnum, TNameParser] = Map(
      Chinese -> MedlineParser4ChineseAuthors,
      Dutch -> MedlineParser4Dutch,
      Japanese -> MedlineParser4Japanese,
      Hispanic -> MLNParsers.getParser(Hispanic),
      French -> MedlineParser4French,
      Italian -> MedlineParser4Italian,
      Undetermined -> MedlineParserGeneral
    )
  }

  // interface to java calls

  def tryParse(in:NormInput):Option[NormResult] = {
    val lang = tryGetLanguage(in)
    // suppose the first one is the most likely
    val regParser = RegisteredParsers.regiMap.getOrElse(lang.head, MedlineParserGeneral)
    regParser.parse(in)
  }


  abstract class MedlineParser extends TNameParser {
    protected val lang:Option[LanguageEnum]
    protected val lastNameParser:TLastNameParser
    protected val foreNameParser:TForeNameParser
    protected def switchForeLastName(input: NormInput):Boolean = false
    override def parse(input: NormInput): Option[NormResult] = {
      try {
        val in =
          if (!switchForeLastName(input)) input
          else {
            val ln = inputLastName(input)
            val fn = inputForeName(input)
            val attr2Copy = input.keySet -- Iterable(Medline_ForeName, Medline_LastName)
            input.filterKeys(attr2Copy.contains) ++ List(
              Medline_ForeName -> ln,
              Medline_LastName -> fn
            )
          }
        val lastNameLit = inputLastName(in)

        val lnResult = lastNameParser.parse(lastNameLit)
        val lnAttrs = filterParseResultByKeys(
          Set(LastName, Suffix),
          lnResult
        )
        val attrs = mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]]()
        attrs ++= lnAttrs
        if (lnResult.contains(_LastName_Preposition))
          attrs += Preposition -> lnResult(_LastName_Preposition)

        // deal with names found in LastName part, which perhaps belong to FirstName
        val foreNamesFromLN = lnResult.get(_LastName_PossibleForeNames)
        val foreNamesFromInput = inputForeName(in)
        val foreNameLit =
          if (foreNamesFromLN.nonEmpty) s"$foreNamesFromInput ${combineNameComponents(foreNamesFromLN.get)}"
          else foreNamesFromInput

        val fnResult = foreNameParser.parse(foreNameLit)
        //if (fnResult.firstNames.nonEmpty) attrs += FirstName -> fnResult.firstNames
        val fnAttrs = filterParseResultByKeys(
          Set(FirstName, ExtraNames), // todo more name parts like middle-name etc.
          fnResult
        )
        attrs ++= fnAttrs
        // Suffix
        if (in.contains(Medline_Suffix) &&
          !attrs.contains(Suffix) &&
          !LangBlacklistedAttrs.isBlockedIn(Suffix, lang) // check if blocked in languages
        ) {
          val suffix = in(Medline_Suffix).trim
          if (SuffixData.SuffixLiterals.contains(suffix.toLowerCase))
            attrs += Suffix -> IndexedSeq(suffix)
          else {
            println(s"Unknown suffix [$suffix] ignored")
          }
        }

        val resAttrs = foreNameParser.postProcess(fnResult, attrs)

        Option(
          new NormResult(
            lastNames = resAttrs(LastName),
            attrs = resAttrs.toMap,
            lang
          )
        )
      }
      catch {
        case t:Throwable => {
          println(s"Exception when processing input: $input")
          //t.printStackTrace()
          //throw t
          None
        }
      }
    }
  }
  private[nameParser] class MedlineParser4Lang(
    language:LanguageEnum,
    override val lastNameParser:TLastNameParser,
    override val foreNameParser:TForeNameParser
  ) extends MedlineParser {
    override val lang:Option[LanguageEnum] = if (language != Undetermined) Option(language) else None
  }


  private[nameParser] val MedlineParser4ChineseAuthors =
    new MedlineParser4Lang(Chinese, ChineseLastNameParser, FNParserChinese) {
      override protected def switchForeLastName(input: NormInput):Boolean = {
        val fn = inputForeName(input)
        // no switch if no fore name
        if (fn.isEmpty) false
        else {
          val ln = inputLastName(input)
          val comps = splitComponents(ln)
          if (comps.length > 1) true
          else if (comps.length == 1) {
            if (Pinyin.isPinyin(comps.head)) false
            else true
          }
          else {
            throw new IllegalArgumentException(s"last name has no components [$ln]")
          }
        }
      }
    }

  import ForeNameParsers._
  private[nameParser] val MedlineParserGeneral =
    new MedlineParser4Lang(Undetermined, GeneralLastNameParser, GeneralForeNameParser)

  private[nameParser] val MedlineParser4Dutch =
    new MedlineParser4Lang(Dutch, LNParserDutch, FNParserDutch)

  private[nameParser] val MedlineParser4French =
    new MedlineParser4Lang(French, LNParserFrench, FNParserFrench)

//  private[nameParser] val MedlineParser4Portuguese =
//    new MedlineParser4Lang(Portuguese, LNParserPortuguese, FNParserPortuguese)

//  private[nameParser] val MedlineParser4Spanish =
//    new MedlineParser4Lang(Spanish, LNParserSpanish, FNParserSpanish)

  private[nameParser] val MedlineParser4Italian =
    new MedlineParser4Lang(Italian, LNParserItalian, FNParserItalian)

  private[nameParser] val MedlineParser4German =
    new MedlineParser4Lang(German, LNParserGerman, FNParserGerman)

  private[nameParser] val MedlineParser4Japanese =
    new MedlineParser4Lang(Japanese, JapaneseLastNameParser, FNParserJapanese)


}
