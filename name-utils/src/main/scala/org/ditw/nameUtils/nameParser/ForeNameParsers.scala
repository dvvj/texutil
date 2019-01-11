package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameParser.ParserHelpers.Consts._
import org.ditw.nameUtils.nameParser.ParserHelpers.splitTrim
import org.ditw.nameUtils.nameParser.langSpecData.PfxParsers._
import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.PrefixParser
import org.ditw.nameUtils.nameParser.utils.NamePartHelpers

import scala.collection.mutable

/**
  * Created by dev on 2017-08-24.
  */
object ForeNameParsers {
  object Consts extends Serializable {
    val EmptyStrSeq = IndexedSeq[String]()
  }

  import Consts._

  import MedlineParsers._
  import ParserHelpers.ResultAttrKeyEnum._
  import ParserHelpers._

//  case class ForeNameParseResult(
//                                firstNames:IndexedSeq[String],
//                                exNames:IndexedSeq[String],
//                                lastNamePrefixes:Option[String]
//                                ) {
//    // todo: determine middle-name and initials later (depending on if lastNamePrefixes present)
//    //def possibleInitials():
//  }

  def foreNameParseResult(
    source:String,
    firstNames:IndexedSeq[String],
    exNames:IndexedSeq[String]
  ):NormParseResult = {
    val r = mutable.Map(
      _ForeName_Source -> IndexedSeq(source),
      FirstName -> firstNames
    )
    if (exNames.nonEmpty)
      r += ExtraNames -> exNames
//    if (lastNamePrefixes.nonEmpty)
//      r += _ForeName_LastNamePreposition -> lastNamePrefixes.get
    r.toMap
  }

  trait TForeNameParser extends Serializable {
    protected def parsePossibleLastNameParts(parts:IndexedSeq[String]):NormParseResult = EmptyParseResult

    protected def parseNames(parts:IndexedSeq[String]):(IndexedSeq[String], IndexedSeq[String]) = {
      if (parts.nonEmpty) {
        val p0 = parts.head
        val firstNames = splitComponents(p0)
        val exNames = parts.tail
        (firstNames, exNames)
      }
      else (EmptyStrSeq, EmptyStrSeq)
    }

    def parse(in:String):NormParseResult = {
      val parts = splitParts(in)

      val lnParts = parsePossibleLastNameParts(parts)
      val remParts = parts //.slice(0, parts.size-prefixes.size) keep all parts for further parsing

      val (firstNames, exNames) = parseNames(remParts)
      lnParts ++ foreNameParseResult(
        source = in,
        firstNames = firstNames,
        exNames = exNames
      )

    }

    def postProcess(
                     res:NormParseResult,
                     existingRes:mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]]
                   ):mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]] = existingRes
  }

  trait TPrefixFNParser {
    this: TForeNameParser =>
    val prefixParser:PrefixParser

    override protected def parsePossibleLastNameParts(parts:IndexedSeq[String]):NormParseResult = {
      if (parts.size > 1) {
        var prefFound:Option[IndexedSeq[String]] = None
        var end = parts.size+1
        while (prefFound.isEmpty && end > 1) {
          end = end-1
          val remParts = parts.slice(0, end)
          prefFound = prefixParser.findInForeName(remParts)
        }
        if (prefFound.nonEmpty) {
          val preposition = prefFound.get
          val len = parts.size - end + prefFound.get.size
          val lastNameParts = parts.slice(parts.size-len, parts.size)
          Map(
            _ForeName_LastNameParts -> lastNameParts,
            _ForeName_LastNamePreposition -> preposition
          )
        }
        else EmptyParseResult
      }
      else EmptyParseResult
    }

    override def postProcess(
                              thisRes:NormParseResult,
                              existingRes:mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]]
                            ):mutable.Map[ResultAttrKeyEnum,IndexedSeq[String]] = {
      val lnPfx =
        if (existingRes.contains(Preposition))
          Option(existingRes(Preposition).head)
        else
          None
      var fnPfx:Option[String] = None
      if (thisRes.contains(_ForeName_LastNameParts)) {
        val lastNamePartsInForeName = thisRes(_ForeName_LastNameParts)
        // adjust last name / extra names part
        val fixedFirstLastName = combineNameComponents(lastNamePartsInForeName ++ IndexedSeq(existingRes(LastName).head))
        existingRes.put(LastName, IndexedSeq(fixedFirstLastName) ++ existingRes(LastName).tail)

        //        if (!attrs.contains(ExtraNames))
        //          println(s"$attrs")
        val extraNames = thisRes(ExtraNames)
        val extraNamesMinusPfx = extraNames.slice(0, extraNames.size - lastNamePartsInForeName.size)
        if (extraNamesMinusPfx.nonEmpty)
          existingRes.put(ExtraNames, extraNamesMinusPfx)
        else existingRes -= ExtraNames

        if (thisRes.contains(_ForeName_LastNamePreposition)) {
          fnPfx = Option(combineNameComponents(thisRes(_ForeName_LastNamePreposition)))
//          if (lastNamePrep.size == lastNamePartsInForeName.size) {
//            fnPfx = Option(combineNameComponents(lastNamePrep))
//          }
        }
      }

      if (fnPfx.nonEmpty) {
        val preposition = List(fnPfx, lnPfx).flatten
        if (preposition.nonEmpty)
          existingRes += Preposition -> IndexedSeq(combineNameComponents(preposition))
      }

      existingRes
    }
  }

  private[nameParser] class PrefixFNParser(val prefixParser:NamePartHelpers.PrefixParser)
    extends TForeNameParser with TPrefixFNParser {
  }

  private[nameParser] val FNParserDutch = new PrefixFNParser(_DutchPrefixParser)
  private[nameParser] val FNParserFrench = new PrefixFNParser(_FrenchPrefixParser)
  //private[nameParser] val FNParserPortuguese = new PrefixFNParser(_PortuguesePrefixParser)
  //private[nameParser] val FNParserSpanish = new PrefixFNParser(_SpanishPrefixParser)
  private[nameParser] val FNParserItalian = new PrefixFNParser(_ItalianPrefixParser)
  private[nameParser] val FNParserGerman = new PrefixFNParser(_GermanPrefixParser)

  private[nameParser] val GeneralForeNameParser = new TForeNameParser { }


  private[nameParser] def initialsFromFirstNames(firstNames:IndexedSeq[String]):IndexedSeq[String] =
    firstNames.map(_.substring(0, 1))
}
