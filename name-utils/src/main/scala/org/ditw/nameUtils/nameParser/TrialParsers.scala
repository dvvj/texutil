package org.ditw.nameUtils.nameParser

import java.util.regex.Pattern

import ParserHelpers.InputKeyEnum.MultiPart_AcademicTitles

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-09-08.
  */
object TrialParsers {
  import ParserHelpers._
  import MultiPartNameUtils._

  private val EmptyStr = ""
  private val TrialNamePartSplitter = Pattern.compile(",")
  private[nameParser] def splitTrialNameParts(name:String) = splitTrim(TrialNamePartSplitter, name)

  private def splitFirstLastNames4Trial(part:String):(String, Option[String]) = {
    val comps = splitTrim(SpaceSplitter, part)
    if (comps.isEmpty) (EmptyStr -> None)
    else if (comps.size == 1) (comps.head -> None)
    else {
//      val lastPart = comps.last.replace(".", "")
//      if (lastPart.length <= 1) // todo: consider suffix || SuffixLiterals.contains(lastPart.toLowerCase))
//        comps.head -> Option(combineNameComponents(comps.tail))
//      else
      comps.last -> Option(combineNameComponents(comps.slice(0, comps.size-1)))
    }
  }

  import org.ditw.nameUtils.nameParser.utils.AcademicTitles._
  import collection.mutable
  private[nameParser] def checkTitlesInLastNameParts(parts:IndexedSeq[String]):(IndexedSeq[String], Option[IndexedSeq[String]]) = {
    // note: only check if there are at least 2 parts
    if (parts.size <= 1) parts -> None
    else {
      val titlesFound = ListBuffer[String]()
      val lastPart = parts.last.trim
      var remParts = parts
      // , doctor/master/bachelor/...
      if (TitlesInTheLastTrialNameParts.contains(lastPart.toLowerCase)) {
        titlesFound += lastPart
        remParts = parts.slice(0, parts.size-1)
      }
      if (remParts.size <= 1) remParts -> Option(titlesFound.toIndexedSeq)
      else {
        val lastComps = parts.slice(1, parts.size).flatMap(splitTrim(SpaceSplitter, _))
        if (lastComps.forall(_.forall(c => !c.isLetter || c.isUpper))) {
          titlesFound ++= lastComps
          IndexedSeq(remParts.head) -> Option(titlesFound.toIndexedSeq)
        }
        else parts -> None
      }
    }
  }

  private val BlockWords = Set(
    "ltd",
    "ltd.",
    "inc."
  )

  private def checkBlocked(nameParts:IndexedSeq[String]):Boolean = {
    val comps = nameParts.flatMap(splitTrim(SpaceSplitter, _))
    comps.exists(c => BlockWords.contains(c.toLowerCase))
  }

  private[nameParser] val CurlyBracketPtn = """\([^\)]*\)""".r
  private[nameParser] val SiteD34Ptn = """[S|s]ite\s+[\w\d]{3,4}""".r
  private val RegexToRemove = Set(
    CurlyBracketPtn,
    SiteD34Ptn
  )


  def containsTrialPersonHint(trialNameSource:String):Boolean = {
    if (trialNameSource == null) false
    else {
      // "site 1234" at the end
      val parts = TrialParsers.splitTrialNameParts(trialNameSource) //.map(_.toLowerCase)
      if (parts.length >= 2) {
        val lastPart = parts.last.toLowerCase()
        SiteD34Ptn.pattern.matcher(lastPart).matches()
      }
      else false
    }
  }

  private val TokensToBeReplacedBySpace = List(";")

  private[nameParser] def trialNameInput(name:String): Option[NormInput] = {
    var fixedName = name
    RegexToRemove.foreach { r =>
      fixedName = r.replaceAllIn(fixedName, "")
    }
    TokensToBeReplacedBySpace.foreach { t =>
      fixedName = fixedName.replace(t, " ")
    }
    val nameParts = splitTrialNameParts(fixedName)

    if (nameParts.nonEmpty) {
      if (checkBlocked(nameParts)) None
      else {
        val (remParts, attrs) = preprocessNameParts(nameParts)
        if (remParts.size <= 0) None
        else {
          var attrs2 = attrs
          val (lastNamePart, foreNamePart) =
            if (remParts.size == 1)
              splitFirstLastNames4Trial(remParts.head)
            else {
              val (rem2, t) = checkTitlesInLastNameParts(remParts)
              if (t.nonEmpty) {
                attrs2 += MultiPart_AcademicTitles ->
                  (if (attrs2.contains(MultiPart_AcademicTitles)) combineAttrParts(attrs2(MultiPart_AcademicTitles) :: t.get.toList)
                  else combineAttrParts(t.get))
              }

              if (rem2.size == 1)
                splitFirstLastNames4Trial(rem2.head)
              else {
                //println(s"part count != 1: ${rem2.mkString("|")}($name)")
                rem2(0) -> Option(rem2(1))
              }
            }
          Option(attrs2 ++ getNormInputTrial(name, lastNamePart, foreNamePart.getOrElse(EmptyStr)))
        }
      }
    }
    else None
  }

  import InputKeyEnum._
  private def getNormInputTrial(source:String, lastName:String, foreName:String):NormInput = {
//    val f = removeCI(foreName, ContactStr)
//    val foundInForeName = f._1
//    var foreNameFixed = f._2
//    val l = removeCI(lastName, ContactStr)
//    val foundInLastName = l._1
//    var lastNameFixed = l._2
//
//    foreNameFixed = removeNoiseCI(foreNameFixed, NoiseInfo)
//    lastNameFixed = removeNoiseCI(lastNameFixed, NoiseInfo)

    val in = genNormInput(lastName, foreName) +
      (SourceString -> source)
    in
  }
  private[nameParser] def trialNormInput2NameStr(in:NormInput):String = {
    val fn = MedlineParsers.inputForeName(in)
    val ln = MedlineParsers.inputLastName(in)
    val r = s"$fn $ln".trim
    r
  }
}
