package org.ditw.nameUtils.nameParser.utils.inferers

import java.nio.charset.StandardCharsets

import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import LanguageInferers._
import org.apache.commons.io.IOUtils
import org.ditw.nameUtils.SimplifyCharset
import org.ditw.nameUtils.nameParser.ParserHelpers

import scala.io.Source

/**
  * Created by dev on 2017-08-31.
  */
object NameComponentInferers {
  private val NameCompRegex2Lang = regexMap(
    """[dD]os""" -> Hispanic,
    """[dD]e'""" -> Italian
  )

  import org.ditw.nameUtils.nameParser.langSpecData.SpanishLangData._

  //  private val NameCompSet2Lang = Map(
  //    Set("nguyen") -> Iterable(Vietnamese),
  //    HispanicNameParts -> Iterable(Hispanic)
  //  )

  private val LastNameCompSet2Lang = Map(
    Set("nguyen") -> Iterable(Vietnamese),
    HispanicNameParts -> Iterable(Hispanic)
  )

  def inferFromNameComponents(parts:IndexedSeq[String]):Iterable[LanguageEnum] =
    inferByRegex(NameCompRegex2Lang, parts)

  private[nameParser] def inferFromLastNameComponentsExactMatch(parts:IndexedSeq[String]):Iterable[LanguageEnum] =
    inferByMatch(LastNameCompSet2Lang, parts)

  def lastNameComponentInferer(
                                foreName:String,
                                foreNameParts:IndexedSeq[String],
                                foreNameComps:IndexedSeq[String],
                                lastName:String,
                                lastNameParts:IndexedSeq[String],
                                lastNameComps:IndexedSeq[String]
                              ):(Iterable[LanguageEnum], Iterable[LanguageEnum]) = {
    val allPossibleLastNameParts = (foreNameParts ++ lastNameComps).map(_.toLowerCase())
    if (allPossibleLastNameParts.nonEmpty) {
      val normedComps = allPossibleLastNameParts.slice(1, allPossibleLastNameParts.size)
        .flatMap(ParserHelpers.splitComponents)
        .map(SimplifyCharset.normalizeAndAsciify)

      val exactMatches = inferFromLastNameComponentsExactMatch(normedComps)
      if (exactMatches.nonEmpty) {
        confirmedInference(exactMatches)
      }
      else {
        val allComps = (foreNameComps ++ lastNameComps).map(_.toLowerCase())

        confirmedInference(
          inferByRegex(NameCompRegex2Lang, allComps)
        )
      }
    }
    else
      Iterable() -> Iterable()
  }
}