package org.ditw.nameUtils.nameParser

import ParserHelpers.{NormResult, combineAttrParts}
import ParserHelpers.ResultAttrKeyEnum._
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._

import scala.collection.mutable

/**
  * Created by dev on 2017-08-23.
  */
object TestHelpers {
  private[nameParser] def normResult(lastNames:IndexedSeq[String],
                                     firstNames:IndexedSeq[String],
                                     lang:LanguageEnum,
                                     preposition:Option[String] = None,
                                     extraNames: Option[IndexedSeq[String]] = None) = {
    import collection.mutable
    val attrs = mutable.Map(
      LastName -> lastNames,
      FirstName -> firstNames
    )
    if (preposition.nonEmpty) attrs += Preposition -> IndexedSeq(preposition.get)
    if (extraNames.nonEmpty) attrs += ExtraNames -> extraNames.get
    new NormResult(
      lastNames,
      attrs.toMap,
      Option(lang)
    )
  }

  private[nameParser] def dutchResult(lastName:String, firstNames:IndexedSeq[String],
                                      preposition:Option[String],
                                      extraNames: Option[IndexedSeq[String]] = None) = {
    normResult(IndexedSeq(lastName), firstNames, Dutch, preposition, extraNames)
  }
  private[nameParser] def dutchResultWithAcTitles(
    lastName:String,
    firstNames:IndexedSeq[String],
    preposition:Option[String],
    academicTitles:IndexedSeq[String],
    extraNames: Option[IndexedSeq[String]] = None
  ) = {
    normResult(IndexedSeq(lastName), firstNames, Dutch, preposition, extraNames).addAttrs(
      List(Out_AcademicTitles -> IndexedSeq(combineAttrParts(academicTitles)))
    )
  }
  private[nameParser] def dutchResultWithSuffix(
    lastName:String,
    firstNames:IndexedSeq[String],
    preposition:Option[String],
    suffix:String,
    extraNames: Option[IndexedSeq[String]] = None
  ) = {
    dutchResult(lastName, firstNames, preposition, extraNames).addAttrs(
      List(Suffix -> IndexedSeq(suffix))
    )
  }
  private[nameParser] def dutchResultWithAcTitlesAndSuffix(
                                                 lastName:String,
                                                 firstNames:IndexedSeq[String],
                                                 preposition:Option[String],
                                                 academicTitles:IndexedSeq[String],
                                                 suffix:String,
                                                 extraNames: Option[IndexedSeq[String]] = None
                                               ) = {
    dutchResultWithAcTitles(lastName, firstNames, preposition, academicTitles, extraNames).addAttrs(
      List(Suffix -> IndexedSeq(suffix))
    )
  }
  private[nameParser] def germanResult(lastName:String, firstNames:IndexedSeq[String],
                                       preposition:Option[String],
                                       extraNames: Option[IndexedSeq[String]] = None) = {
    normResult(IndexedSeq(lastName), firstNames, German, preposition, extraNames)
  }
  private[nameParser] def frenchResult(lastName:String, firstNames:IndexedSeq[String],
                                       preposition:Option[String],
                                       extraNames: Option[IndexedSeq[String]] = None) = {
    normResult(IndexedSeq(lastName), firstNames, French, preposition, extraNames)
  }
  private[nameParser] def chineseResult(lastName:String, firstNames:IndexedSeq[String]) = {
    normResult(IndexedSeq(lastName), firstNames, Chinese)
  }
  private[nameParser] def japaneseResult(lastNames:IndexedSeq[String], firstNames:IndexedSeq[String]) = {
    normResult(IndexedSeq(lastNames.mkString(" ")), firstNames, Japanese)
  }
  import ParserHelpers._
  private[nameParser] def chineseResultWithAcaTitles(
    lastName:String,
    firstNames:IndexedSeq[String],
    academicTitles:IndexedSeq[String]
  ) = {
    normResult(IndexedSeq(lastName), firstNames, Chinese).addAttrs(
      List(Out_AcademicTitles -> IndexedSeq(combineAttrParts(academicTitles)))
    )
  }
  private[nameParser] def hispanicResult(
    lastNames:IndexedSeq[String],
    firstNames:IndexedSeq[String],
    preposition:Option[String] = None,
    extraNames: Option[IndexedSeq[String]] = None
  ) = {
    normResult(lastNames, firstNames, Hispanic, preposition, extraNames)
  }

  private[nameParser] def generalResult(lastNames:IndexedSeq[String],
                                        firstNames:IndexedSeq[String],
                                        exNames:Option[IndexedSeq[String]],
                                        suffix:Option[String]
                                       ) = {
    val attrs = mutable.Map(
      LastName -> lastNames,
      FirstName -> firstNames
    )
    //if (preposition.nonEmpty) attrs += Preposition -> IndexedSeq(preposition.get)
    if (exNames.nonEmpty) attrs += ExtraNames -> exNames.get
    if (suffix.nonEmpty) attrs += Suffix -> IndexedSeq(suffix.get)
    new NormResult(
      lastNames,
      attrs.toMap,
      None
    )

  }

}
