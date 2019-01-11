package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.MedlineParsers._
import org.ditw.nameUtils.nameParser.ParserHelpers.ResultAttrKeyEnum._
import org.ditw.nameUtils.nameParser.langSpecData.PfxParsers._
import org.ditw.nameUtils.nameParser.langSpecData.{ItalianLangData, PortugueseLangData, SpanishLangData}
import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.PrefixParser
import org.ditw.nameUtils.nameParser.utils.NamePartHelpers

import scala.collection.mutable

/**
  * Created by dev on 2017-08-17.
  */
object LastNameParsers {

  object Consts extends Serializable {
    val EmptyStrSeq = IndexedSeq[String]()
    val NoPrefix = 0
    val NobelTitles = Set(
      "von",
      "van",
      "de",
      "dé",
      "du",
      "la",
      "z",
      "af",
      "av",
      "den",
      "op",
      "der",
      "van't",
      "le",
      "des",
      "am",
      "aus",
      "vom",
      "dem",
      "zum",
      "zur",
      "ten",
      "zu",
      "da",
      "de",
      "obla",
      "li",
      "dos",
      "el",
      "las",
      "di",
      "das",
      "del"
    )

//    val ResultAttrKey_Source = "src"
//    val ResultAttrKey_Components = "comp"
//    val ResultAttrKey_LastNames = "ln"
//    val ResultAttrKey_Preposition = "pfx"
//    val ResultAttrKey_Suffix = "suf"
  }
  import Consts._
  import ParserHelpers._
  import ParserHelpers.Consts._


  // helper function
//  private[nameParser] def toPair(nameParts:String*):(String,IndexedSeq[String]) =
//    nameParts.mkString(" ") -> nameParts.toIndexedSeq

//  case class LastNameParseResult(source:String,
//                                 nameComponents:IndexedSeq[String],
//                                 lastNameRanges:IndexedSeq[Range],
//                                 prefixEndIndex:Int,
//                                 suffix:Option[String]) {
//    def hasPrefix:Boolean = prefixEndIndex != NoPrefix
//    def getLastNames:IndexedSeq[String] = lastNameRanges.map(compose)
//    private def compose(r:Range):String = combineNameComponents(r.map(nameComponents))
//    def getPrefix:Option[String] =
//      if (prefixEndIndex != NoPrefix)
//        Option(compose(0 until prefixEndIndex))
//      else None
//  }
  def lastNameParseResult(
    source:String,
    nameComponents:IndexedSeq[String],
    lastNameRanges:IndexedSeq[Range],
    prefixEndIndex:Int,
    suffix:Option[String]):NormParseResult = {
    val lastNames = lastNameRanges.map(r => composeName(nameComponents, r))
    val r = mutable.Map(
      LastName -> lastNames,
      _LastName_Source -> IndexedSeq(source)
    )

    if (suffix.nonEmpty)
      r += Suffix -> suffix.toIndexedSeq

    if (prefixEndIndex != NoPrefix)
      r += _LastName_Preposition -> IndexedSeq(composeName(nameComponents, 0 until prefixEndIndex))

    r.toMap
  }

  import org.ditw.nameUtils.nameParser.utils.SuffixData._
  private[nameParser] def parseSuffixGeneral(nameComps:IndexedSeq[String]): Option[String] = {
    if (nameComps.size > 1) {
      val lastPart = nameComps.last
      if (SuffixLiterals.contains(lastPart.toLowerCase)) Option(lastPart)
      else None
    }
    else None
  }

  private[nameParser] def parsePrefixesGeneral(nameComps:IndexedSeq[String]): Int = {
    if (nameComps.size > 1) {
      var found = true
      var idx = -1
      while (idx < nameComps.length - 1 && found) {
        idx = idx + 1
        val p = nameComps(idx).toLowerCase()
        if (!NobelTitles.contains(p)) found = false
      }
      if (idx > 0) idx
      else 0
    }
    else 0
  }

  private[nameParser] def parseLastNameGeneral(nameComps:IndexedSeq[String]):IndexedSeq[Range] = {
    IndexedSeq(nameComps.indices)
  }

  trait TLastNameParser extends Serializable {
    protected def parseSuffix(nameComps:IndexedSeq[String]):Option[String] =
      parseSuffixGeneral(nameComps)
    // only try to find the nobel titles at the beginning
    protected def parsePrefixes(nameComps:IndexedSeq[String]):Int =
      parsePrefixesGeneral(nameComps)
    protected def parseLastNames(nameComps:IndexedSeq[String]):IndexedSeq[Range] =
      parseLastNameGeneral(nameComps)
    def parse(in:String):NormParseResult = {
      val nameComps = splitComponents(in)
      //val parts = splitTrim(NamePartCompSplitPtn, in)
      if (nameComps.size <= 0)
        throw new IllegalArgumentException(s"Empty input: [$nameComps]")

      val suffix = parseSuffix(nameComps)
      val comps2Parse = if (suffix.isEmpty) nameComps else nameComps.slice(0, nameComps.length-1)
      val lastNames = parseLastNames(comps2Parse)
      val pfxEndIdx = parsePrefixes(nameComps)
      val res = lastNameParseResult(in, nameComps, lastNames, pfxEndIdx, suffix)
      postProcess(res)
    }

    protected def postProcess(res:NormParseResult):NormParseResult = res
  }

  trait TPrefixLNParser {
    this: TLastNameParser =>
    val prefixParser:PrefixParser

    override protected def parsePrefixes(parts: IndexedSeq[String]): Int = {
      val prefix = prefixParser.findInLastName(parts)
      if (prefix.nonEmpty) prefix.get.size else NoPrefix
      //prefix.getOrElse(Consts.EmptyStrSeq)
    }
  }

  trait TMultiLastNameLNParser {
    this: TLastNameParser =>
    val lastNameConj:String

    override protected def parseLastNames(nameComps:IndexedSeq[String]):IndexedSeq[Range] = {
      val conjIndices = nameComps.indices.filter(idx => nameComps(idx).toLowerCase() == lastNameConj)
      if (conjIndices.nonEmpty) {
        val range0 = (0, conjIndices(0))
        val range_1 = (conjIndices.last+1, nameComps.size)
        val ranges =  range0 :: (1 until conjIndices.size).map( idx => conjIndices(idx-1)+1->conjIndices(idx) ).toList ::: List(range_1)
        ranges.map(r => r._1 until r._2).filter(_.nonEmpty).toIndexedSeq
      }
      else {
        parseLastNameGeneral(nameComps)
      }
//      val prefix = prefixParser.findInLastName(parts)
//      if (prefix.nonEmpty) prefix.get.size else NoPrefix
      //prefix.getOrElse(Consts.EmptyStrSeq)
    }
  }

  trait TLastNameWithPrepositionFixer {
    this: TPrefixLNParser with TLastNameParser =>

    override protected def postProcess(res:NormParseResult):NormParseResult = {
      if (res.contains(LastName)) {
        val lastName1 = res(LastName).head
        val ln1Comps = splitComponents(lastName1)
        val existing = prefixParser.findInLastName(ln1Comps)
        if (existing.nonEmpty) res // do nothing
        else {
          var prepFound:Option[IndexedSeq[String]] = None
          var start = 0
          while (prepFound.isEmpty && start < ln1Comps.size-2) {
            start = start+1
            val remComps = ln1Comps.slice(start, ln1Comps.size)
            prepFound = prefixParser.findInLastName(remComps)
          }
          if (prepFound.nonEmpty) {
            val newLastName1 = combineNameComponents(ln1Comps.slice(start, ln1Comps.size))
            val newLastNames = IndexedSeq(newLastName1) ++ res(LastName).tail
            val cp = copyParseResult(res, res.keySet - LastName)
            cp += LastName -> newLastNames
            cp += _LastName_Preposition -> IndexedSeq(combineNameComponents(prepFound.get))
            cp += _LastName_PossibleForeNames -> ln1Comps.slice(0, start)
            cp.toMap
          }
          else res
        }
      }
      else res
    }
  }


  private[nameParser] object ChineseLastNameParser extends TLastNameParser {
    override protected def parseSuffix(nameComps:IndexedSeq[String]): Option[String] = None
    override protected def parsePrefixes(nameComps:IndexedSeq[String]): Int = NoPrefix

    override protected def postProcess(res:NormParseResult):NormParseResult = {
      if (res.contains(LastName)) {
        val lastNames = res(LastName).flatMap(splitComponents)
        if (lastNames.size <= 1) res
        else {
          // todo: possible double family names e.g. ouyang, murong, etc.
          // modify the last name part
          val cp = copyParseResult(res, res.keySet - LastName)
          cp += LastName -> IndexedSeq(lastNames.last)
          cp += _LastName_PossibleForeNames -> lastNames.slice(0, lastNames.size-1)
          //println(s"Fixed chinese last names [${combineNameComponents(lastNames)}] -> [${lastNames.last}]")
          cp.toMap
        }
      }
      else res
    }
  }

  private[nameParser] object JapaneseLastNameParser extends TLastNameParser {
    override protected def parseSuffix(nameComps:IndexedSeq[String]): Option[String] = None
    override protected def parsePrefixes(nameComps:IndexedSeq[String]): Int = NoPrefix

    override protected def postProcess(res:NormParseResult):NormParseResult = res
  }



  private[nameParser] class PrefixLNParser(val prefixParser:PrefixParser)
    extends TLastNameParser with TPrefixLNParser {
  }

  private[nameParser] val LNParserDutch = new PrefixLNParser(_DutchPrefixParser) with TLastNameWithPrepositionFixer
  private[nameParser] val LNParserFrench = new PrefixLNParser(_FrenchPrefixParser)
  private[nameParser] val LNParserItalian = new PrefixLNParser(_ItalianPrefixParser)
  private[nameParser] val LNParserGerman = new PrefixLNParser(_GermanPrefixParser)

  private[nameParser] class MultiLastNamePrefixLNParser(
    prefixParser:PrefixParser,
    val lastNameConj:String
  ) extends PrefixLNParser(prefixParser) with TMultiLastNameLNParser {
  }

//  private[nameParser] val LNParserPortuguese =
//    new MultiLastNamePrefixLNParser(_PortuguesePrefixParser, PortugueseLangData.PortugueseConj)

//  private[nameParser] val LNParserSpanish =
//    new MultiLastNamePrefixLNParser(_SpanishPrefixParser, SpanishLangData.SpanishConj)

  private[nameParser] val GeneralLastNameParser = new TLastNameParser { }

}
