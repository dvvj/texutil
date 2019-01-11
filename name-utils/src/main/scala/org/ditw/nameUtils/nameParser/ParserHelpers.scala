package org.ditw.nameUtils.nameParser

import java.util.regex.Pattern

import org.ditw.nameUtils.nameparser.{NameParser, NameParserHelpers, NamesProcessed}
import org.ditw.nameUtils.nameParser.MedlineParsers.{MedlineParser, RegisteredParsers, TNameParser}
import org.ditw.nameUtils.nameParser.PaymentParsers.getNormInputPayment
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum.{LanguageEnum, _}
import org.ditw.nameUtils.nameParser.utils.NameLanguages.tryGetLanguage
import org.ditw.nameUtils.nameParser.utils.{NameLanguages, SuffixData}
import org.ditw.nameUtils.nameParser.langSpecData.LangBlacklistedAttrs
import org.ditw.nameUtils.nameParser.utils.{NameLanguages, SuffixData}
import org.ditw.nameUtils.nameparser.{NameParser, NameParserHelpers}
import org.ditw.nameUtils.namerules.AuthorNameUtils

import scala.collection.mutable

/**
  * Created by dev on 2017-08-17.
  */
object ParserHelpers {
  //val useNewParser:Boolean = true
  private[nameParser] val _oldParser = new AuthorNameUtils()


  object ResultAttrKeyEnum extends Enumeration {
    type ResultAttrKeyEnum = Value
    val Language, FirstName, ExtraNames, LastName, Preposition, Prefix, Suffix, Signature,
    Out_Grant_Role,
    Out_AcademicTitles,
    // intermedia results from last name part
    _LastName_Source, _LastName_Components, _LastName_Preposition, _LastName_PossibleForeNames,
    // intermedia results from fore name part
    _ForeName_Source, _ForeName_ExNames, _ForeName_LastNameParts, _ForeName_LastNamePreposition
    = Value
  }

  import ResultAttrKeyEnum._

  object InputKeyEnum extends Enumeration {
    type InputKeyEnum = Value

    val SourceString, // the source string created from various inputs, such as Medline, grant, etc.
    MultiPart_SuffixPart, MultiPart_AcademicTitles,
    In_Grant_Role,
    Medline_LastName, Medline_ForeName, Medline_Suffix, Medline_Initials = Value
    //todo: Grant_LastName, Grant_ForeName
  }
  import InputKeyEnum._


  object Consts extends Serializable {
    val NamePartSplitPtn = Pattern.compile("[\\s\\t.,]+")
    val NamePartCompSplitPtn = Pattern.compile("[\\s\\t.,\\-]+")
    val SpaceSplitPtn = Pattern.compile("\\s+")
    val NameComponentConnector = " "
    val InputAttrConnector = "||"

    val EmptyInputAttrMap = Map[InputKeyEnum,String]()
    val EmptyParseResult = Map[ResultAttrKeyEnum,IndexedSeq[String]]()
    val EmptyStr = ""

    val EmptyStrSeq = IndexedSeq[String]()
  }

  import Consts._

  def splitTrim(splitPtn:Pattern, in:String):IndexedSeq[String] =
    if (in == null || in.isEmpty) EmptyStrSeq
    else splitPtn.split(in).map(_.trim).filter(!_.isEmpty).toIndexedSeq
  def splitComponents(in:String):IndexedSeq[String] = splitTrim(NamePartCompSplitPtn, in)
  def splitParts(in:String):IndexedSeq[String] = splitTrim(NamePartSplitPtn, in)
  def splitBySpace(in:String):IndexedSeq[String] = splitTrim(SpaceSplitPtn, in)

  def combineNameComponents(comps:String*):String = comps.mkString(NameComponentConnector)
  def combineNameComponents(comps:Iterable[String]):String = comps.mkString(NameComponentConnector)
  def combineNameComponents(comps:IndexedSeq[String], seps:IndexedSeq[String]):String = {
    if (comps.isEmpty && seps.isEmpty) EmptyStr
    else {
      if (comps.size != seps.size+1)
        throw new IllegalArgumentException(s"Component/separator count don't match (${comps.size}/${seps.size})")
      comps.head + (seps zip comps.tail).map(p => p._1+p._2).mkString
    }
  }
  def combineAttrParts(comps:String*) = comps.mkString(InputAttrConnector)
  def combineAttrParts(comps:Iterable[String]) = comps.mkString(InputAttrConnector)
  def composeName(nameComponents:IndexedSeq[String], r:Range):String = combineNameComponents(r.map(nameComponents))

  type NormInput = Map[InputKeyEnum,String]
  def inputSource(input:NormInput):String = input(SourceString)
  private[nameParser] def genNormInput(
                                        lastName:String,
                                        foreName:String,
                                        suffix:Option[String] = None
                                      ):NormInput = {
    val source = NameParserHelpers.buildSourceName(foreName, lastName, suffix.orNull)
    val r = mutable.Map(
      SourceString -> source,
      Medline_LastName -> lastName
    )
    if (foreName != null && !foreName.isEmpty) r += Medline_ForeName -> foreName

    if (suffix.nonEmpty) r += Medline_Suffix -> suffix.get
    r.toMap
  }

  private[nameParser] class NormResult(val lastNames:IndexedSeq[String],
                                       val attrs:Map[ResultAttrKeyEnum,IndexedSeq[String]],
                                       val lang:Option[LanguageEnum])
    extends Serializable {
    def firstNames:IndexedSeq[String] = attrs.getOrElse(FirstName, EmptyStrSeq)
    def extraNames:IndexedSeq[String] = attrs.getOrElse(ExtraNames, EmptyStrSeq)
    def lastNamePrepositions:IndexedSeq[String] = attrs.getOrElse(Preposition, EmptyStrSeq)
    def suffix:String = if (attrs.contains(Suffix)) attrs(Suffix).head else EmptyStr
    def addAttrs(newAttrs:Iterable[(ResultAttrKeyEnum, IndexedSeq[String])]):NormResult =
      new NormResult(lastNames, attrs ++ newAttrs, lang)
  }
  def normResult1(lastName:String, attrs:Map[ResultAttrKeyEnum,IndexedSeq[String]], lang:Option[LanguageEnum]):NormResult =
    new NormResult(IndexedSeq(lastName), attrs, lang)


  type NormParseResult = Map[ResultAttrKeyEnum,IndexedSeq[String]]
  private[nameParser] def copyParseResult(source:NormParseResult, keys:Iterable[ResultAttrKeyEnum])
    :mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]] = {
    val cp = mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]]()
    keys.foreach(k => cp += k -> source(k))
    cp
  }

  private val NameNotParsed:NamesProcessed = null
  def nameNotParsed(np:NamesProcessed):Boolean = np == NameNotParsed
  def normResult2ParseResult(source:String, r:NormResult):NamesProcessed = {
    if (r.lastNames.isEmpty)
      NameNotParsed
    else {
      val np = new NamesProcessed
      np.source = source
      np.firstName = combineNameComponents(r.firstNames)
      np.middleName = combineNameComponents(r.extraNames)
      np.lastName = combineNameComponents(r.lastNames)
      np.suffix = r.suffix
      np.nobeltitles = combineNameComponents(r.lastNamePrepositions)
      np.signature = SignatureHelpers.genSignature(r)
      np
    }
  }

  private[nameParser] val _SupportedLanguages = Set[LanguageEnum](
    Chinese, Dutch, Japanese, Hispanic
  )

  def getSupportedLanguageCodes():Array[String] = _SupportedLanguages.map(NameLanguages.LangCodeMap).toArray

  private[nameParser] def _runParserNorm(
    in:NormInput,
    langSet:Set[LanguageEnum],
    parserMap:Map[LanguageEnum, TNameParser]
  ):Option[NormResult] = {
    val langs = tryGetLanguage(in)

    // only process those with definite language
    if (langs.isEmpty) None
    else if (langs.size > 1) None
    else {
      val lang = langs.head
      //val langSet:Set[] = languageSet.asScala.toSet
      if (_SupportedLanguages.contains(lang)) {
        // do parsing

        val r:Option[NormResult] = parserMap(lang).parse(in)
        if (lang == Hispanic) {

          val forename = in(Medline_ForeName)
          val lastname = in(Medline_LastName)
          if (r.nonEmpty) {
            val resFirstNames = r.get.attrs(FirstName).mkString(" ")
            val resLastNames = r.get.attrs(LastName).mkString(" ")
            val resMiddleNames = r.get.extraNames.mkString(" ")
            println(s"[$forename|$lastname] => [$resFirstNames|$resMiddleNames|$resLastNames]")
          }
          else {
            println("empty results")
          }
        }

        r
//        if (r.nonEmpty) normResult2ParseResult(inputSource(in), r.get)
//        else None
      }
      else None
    }
  }

  def parseMedlineNameJ(
    foreName:String,
    lastName:String,
    suffix:String
    // languageSet:java.util.Set[LanguageEnum]
  ): NamesProcessed = {
    val nameParts = IndexedSeq(foreName, lastName, suffix)

    tryNewParserOtherwiseRevertToOldOne_MultipleNamePart(
      nameParts,
      names => {
        val (foreName, lastName, suffix) = (nameParts(0), nameParts(1), nameParts(2))

        val foreNameTrimmed = if (foreName != null) foreName.trim else null
        val lastNameTrimmed = if (lastName != null) lastName.trim else null
        val suffixTrimmed = if (suffix != null && !suffix.trim.isEmpty) Option(suffix.trim) else None
        val in = genNormInput(
          lastNameTrimmed, foreNameTrimmed,
          suffixTrimmed
        )

//        if (lastNameTrimmed != null && lastNameTrimmed.toLowerCase == "takahashi")
//          println("found!")

        val r = _runParserNorm(in, _SupportedLanguages, RegisteredParsers.regiMap)
        if (r.nonEmpty) normResult2ParseResult(inputSource(in), r.get)
        else NameNotParsed
      },
      names => {
        val (firstName, lastName, suffix) = (nameParts(0), nameParts(1), nameParts(2))
        // reverting to old parser
        val fullName = NameParserHelpers.buildSourceName(
          if (firstName != null) firstName.toLowerCase() else null,
          lastName.toLowerCase(),
          if (suffix != null) suffix.toLowerCase() else null
        )
        NameParser.parseName(fullName)
      }
    )
  }

  private[nameParser] def copyFromInput(in:NormInput, res:NormResult,
                                        keys:Iterable[(InputKeyEnum, ResultAttrKeyEnum)]
                                       ):NormResult = {
    val attrs = mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]]()
    keys.foreach { kp =>
      val inKey = kp._1
      val resKey = kp._2
      val v = in.get(inKey)
      if (v.nonEmpty) attrs += resKey -> IndexedSeq(v.get)
    }
    res.addAttrs(attrs)
  }

  private val GrantInOutAttrMap = Map[InputKeyEnum, ResultAttrKeyEnum](
    In_Grant_Role -> Out_Grant_Role,
    MultiPart_AcademicTitles -> Out_AcademicTitles,
    MultiPart_SuffixPart -> Suffix
  )

  private[nameParser] def parseGrantName(in:Option[NormInput]): Option[(NormInput, NormResult)] = {

    if (in.nonEmpty) {
      val r = _runParserNorm(in.get, _SupportedLanguages, RegisteredParsers.regiMap)
      r.map { res =>
        //val blacklistedAttrs = LangBlacklistedAttrs.getLangBlacklistedAttrs(res.lang)
        val filteredList = LangBlacklistedAttrs.filterOutAttrByLang(
          res.lang,
          GrantInOutAttrMap,
          (p:(InputKeyEnum, ResultAttrKeyEnum)) => p._2
        )

        val at = copyFromInput(
          in.get, res,
          filteredList
        )
        in.get -> at
      }
    }
    else None
  }

  private def convertResult(r:Option[(NormInput, NormResult)]):NamesProcessed = {
    if (r.nonEmpty) normResult2ParseResult(inputSource(r.get._1), r.get._2)
    else NameNotParsed
  }

  private def fixOldGrantNameParser(source:String):NamesProcessed = {
    val dotsRemoved = source.replaceAll("\\.","")
    val parts = dotsRemoved.split(",").map(_.trim).filter(_.nonEmpty)
    if (parts.nonEmpty) {
      val trans =
        if (parts.length >= 2) {
          val (lastNamePart, firstNameParts) =
            if (SuffixData.UnambiguousSuffixLiterals.contains(parts(1).toLowerCase)) {
              (parts(0) + " " + parts(1), parts.slice(2, parts.length))
            }
            else (parts(0), parts.slice(1, parts.length))
          s"${firstNameParts.mkString(" ")} $lastNamePart"
        }
        else parts.mkString(" ")
      NameParser.parseName(trans)
    }
    else NameNotParsed
  }

  def parseGrantNameJ(name:String): NamesProcessed = {
    val in = GrantParsers.grantNameInput(name)

    tryNewParserOtherwiseRevertToOldOne_SingleNamePart(
      name,
      n => convertResult(parseGrantName(in)),
      n => {
        val n2 =
          if (in.nonEmpty) {
            GrantParsers.grantNormInput2NameStr(in.get)
          }
          else name
        // note: this is copied from old code

        //NameParser.parseName(n2.toLowerCase().replaceAll("\\.",","))
        val np = fixOldGrantNameParser(n2.toLowerCase())
        if (!nameNotParsed(np)) np.source = name
        np
      }
    )
  }

  private val TrialInOutAttrMap = Map[InputKeyEnum, ResultAttrKeyEnum](
    MultiPart_AcademicTitles -> Out_AcademicTitles,
    MultiPart_SuffixPart -> Suffix
  )

  private[nameParser] def parseTrialName(in:Option[NormInput]): Option[(NormInput, NormResult)] = {

    if (in.nonEmpty) {
      val r = _runParserNorm(in.get, _SupportedLanguages, RegisteredParsers.regiMap)
      r.map { res =>
        //val blacklistedAttrs = LangBlacklistedAttrs.getLangBlacklistedAttrs(res.lang)
        val filteredList = LangBlacklistedAttrs.filterOutAttrByLang(
          res.lang,
          TrialInOutAttrMap,
          (p:(InputKeyEnum, ResultAttrKeyEnum)) => p._2
        )

        val at = copyFromInput(
          in.get, res,
          filteredList
        )

        in.get -> at
      }
    }
    else None
  }

  private[nameParser] def tryNewParserOtherwiseRevertToOldOne_SingleNamePart(
    name:String,
    newParserProc: String => NamesProcessed,
    oldParserProc: String => NamesProcessed
  ):NamesProcessed = {
    var r:NamesProcessed = newParserProc(name)
    if (!nameNotParsed(r)) r
    else {
      oldParserProc(name)
    }
  }

  private[nameParser] def tryNewParserOtherwiseRevertToOldOne_MultipleNamePart(
    names:IndexedSeq[String],
    newParserProc: IndexedSeq[String] => NamesProcessed,
    oldParserProc: IndexedSeq[String] => NamesProcessed
  ):NamesProcessed = {
    var r:NamesProcessed = newParserProc(names)
    if (!nameNotParsed(r)) r
    else {
      oldParserProc(names)
    }
  }


  def parseTrialNameJ(name:String): NamesProcessed = {
    val in = TrialParsers.trialNameInput(name)

    tryNewParserOtherwiseRevertToOldOne_SingleNamePart(
      name,
      n => convertResult(parseTrialName(in)),
      n => {
        val n2 = if (in.nonEmpty) TrialParsers.trialNormInput2NameStr(in.get) else name
        val op = NameParser.parseTrialName(n2)
        if (op.isPresent) op.get()
        else NameNotParsed
      }
    )
//    var r:NamesProcessed = null
//    if (useNewParser) {
//      val pr = parseTrialName(name)
//      r = convertResult(pr)
//    }
//    if (!nameNotParsed(r)) r
//    else {
//
//    }
  }


  private[nameParser] def parsePaymentName(
    firstName:String,
    middleName:String,
    lastName:String,
    suffix:String
  ):Option[(NormInput, NormResult)] = {
    val in = getNormInputPayment(firstName, middleName, lastName, suffix)

    if (in.nonEmpty) {
      val result = _runParserNorm(in.get, _SupportedLanguages, RegisteredParsers.regiMap)
      result.map(in.get -> _)
    }
    else None
  }

  import org.ditw.nameUtils.nameParser.utils.PaymentDataHelpers._
  def parsePaymentNamesInPhysicianProfileJ(nameParts:Array[String], altNameParts:Array[String]): NamesProcessed = {
    val mergedNames = mergeNamePartsJ(nameParts, altNameParts)
    if (!namePartsNotMerged(mergedNames)) parsePaymentNameJ(mergedNames)
    else parsePaymentNameJ(nameParts) // if name not compatible, ignore the alt name. todo: use this as a hint to merge experts
  }

  def parsePaymentNameJ(nameParts:Array[String]): NamesProcessed = {
    //nameParts: Array(firstName, middleName, lastName, suffix)
    tryNewParserOtherwiseRevertToOldOne_MultipleNamePart(
      nameParts,
      names => {
        val r = parsePaymentName(names(0), names(1), names(2), names(3))

        if (r.nonEmpty) normResult2ParseResult(inputSource(r.get._1), r.get._2)
        else NameNotParsed
      },
      names => {
        val firstMiddleLastName = combineNameComponents(
          nameParts.slice(0, nameParts.length-1)
            .filter(n => n != null && !n.isEmpty)
        )
        NameParser.parseName(firstMiddleLastName.toLowerCase)
      }
    )
  }

  def traceGrantNameJ(name:String): String = {
    val r = GrantParsers.traceNameInput1(name)

    r.orNull
  }


  //def splitBy(in:String, ptn: Pattern) = ptn.split(in)
//  def parseLastName(in:String):(List[String], List[String], String) = {
//    // check suffix
//    val parts = NamePartSplitPtn.split(in)
//
//
//  }
//  public static void parseLastNamePart(String part, ParsedName parsed) {
//    String[] subParts = splitParts(part);
//    if (subParts.length > 1) {
//      String lastPart = subParts[subParts.length-1].toLowerCase();
//      String[] remainingParts;
//      if (Suffixes.contains(lastPart)) {
//        parsed.updateSuffix(lastPart);
//        remainingParts = Arrays.copyOfRange(subParts, 0, subParts.length-1);
//        String rem = combineStrings(Arrays.asList(remainingParts));
//        parsed.setLastName4Namespace(rem);
//      }
//      else {
//        remainingParts = subParts;
//      }
//      if (remainingParts.length > 1) {
//        List<String> nobles = new LinkedList<>();
//        List<String> rem = findFromList(Nobles, remainingParts, nobles);
//        if (!nobles.isEmpty()) {
//          parsed.updateNobles(nobles);
//
//        }
//      }
//
//    }
//
//  }

}
