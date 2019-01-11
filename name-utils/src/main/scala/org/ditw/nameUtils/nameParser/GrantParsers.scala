package org.ditw.nameUtils.nameParser

import java.util.regex.Pattern

import org.ditw.nameUtils.nameParser.ParserHelpers._
import org.ditw.nameUtils.nameParser.utils.AcademicTitles

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-09-06.
  */
object GrantParsers extends Serializable {

  // general format:  name1[ (contact)]; name2 ...
  // where name's format: last_name, fore names
  //  e.g. GEFENAS, EUGENIJUS ; PHILPOTT, SEAN M. (contact); STROSBERG, MARTIN ;
  private val GrantNameSplitter = Pattern.compile(";")
  private val GrantNamePartSplitter = Pattern.compile(",")
  private[nameParser] def splitGrantNameParts(name:String) = splitTrim(GrantNamePartSplitter, name)
  private val EmptyStr = ""
  private[nameParser] val ContactStr = "(contact)"
  private[nameParser] val NoiseInfo = List(
    "do not use a middle name",
    "n/a"
  )

  private[nameParser] val ContactRole = "CT"

  import MultiPartNameUtils._
  import org.ditw.nameUtils.nameParser.ParserHelpers.InputKeyEnum._

  // used for only one name part cases
  private[nameParser] def splitFirstLastNames4Grant(part:String):(String, Option[String]) = {
    val comps = splitTrim(SpaceSplitter, part)
    if (comps.isEmpty) (EmptyStr -> None)
    else if (comps.size == 1) (comps.head -> None)
    else {
      // if there's clear indication that last name comes first, reverse
      val lastPart = comps.last.replace(".", "")
      if (lastPart.length <= 1) // todo: consider suffix || SuffixLiterals.contains(lastPart.toLowerCase))
        comps.head -> Option(combineNameComponents(comps.tail))
      else
        comps.last -> Option(combineNameComponents(comps.slice(0, comps.size-1)))
    }
  }

  private def getNormInputGrant(source:String, lastName:String, foreName:String):NormInput = {
    val f = removeCI(foreName, ContactStr)
    val foundInForeName = f._1
    var foreNameFixed = f._2
    val l = removeCI(lastName, ContactStr)
    val foundInLastName = l._1
    var lastNameFixed = l._2

    foreNameFixed = removeNoiseCI(foreNameFixed, NoiseInfo)
    lastNameFixed = removeNoiseCI(lastNameFixed, NoiseInfo)

    val in = genNormInput(lastNameFixed, foreNameFixed) +
      (SourceString -> source)
    if (foundInForeName || foundInLastName)
      in + (In_Grant_Role -> ContactRole)
    else
      in
  }

  private def grantSuffix(in:NormInput):String = in.get(MultiPart_SuffixPart).getOrElse(EmptyStr)
  private[nameParser] def grantNormInput2NameStr(in:NormInput):String = {
    val fn = MedlineParsers.inputForeName(in)
    val ln = MedlineParsers.inputLastName(in)
    val sf = grantSuffix(in)

    //    val r =
    //      if (fn.nonEmpty) s"$ln, $fn".trim
    //      else ln.trim
    //    r

    val r = List(ln, sf, fn).filter(_.nonEmpty).mkString(", ")
    r.trim
  }

  import AcademicTitles._
  import ParserHelpers.Consts._
  import collection.mutable
  import org.ditw.nameUtils.nameParser.utils.SuffixData._

  def splitNamesJ(source:String):Array[String] = splitTrim(GrantNameSplitter, source).toArray
  private[nameParser] def grantNameInput(name:String): Option[NormInput] = {
    val nameParts = splitGrantNameParts(name)

    if (nameParts.nonEmpty) {
      val (remParts, attrs) = preprocessNameParts(nameParts)
      if (remParts.length > 2) {
        //throw new IllegalArgumentException(s"too many name parts (2 at most): [$name]")
        println(s"too many name parts (2 at most): [$name], skipped")
        None
      }
      else {
        if (remParts.size <= 0) None
        else {
          val (lastNamePart, foreNamePart) =
            if (remParts.size == 1)
              splitFirstLastNames4Grant(remParts.head)
            else
              remParts(0) -> Option(remParts(1))
          Option(attrs ++ getNormInputGrant(name, lastNamePart, foreNamePart.getOrElse(EmptyStr)))
        }
      }
    }
    else None
  }

  private[nameParser] def traceNameInput1(name:String): Option[String] = {
    val nameParts = splitGrantNameParts(name)

    if (nameParts.nonEmpty) {
      val (remParts, attrs) = preprocessNameParts(nameParts)
      if (remParts.length > 2) {
        //throw new IllegalArgumentException(s"too many name parts (2 at most): [$name]")
        Option(s"too many name parts (2 at most): [$name], skipped")
      }
      else {
        if (remParts.length <= 1) {
          val (ln, fn) = if (remParts.nonEmpty) splitFirstLastNames4Grant(remParts.head) else EmptyStr -> None
          if (fn.isEmpty) Option(s"too few parts: [$name]")
          else None
        }
        else {
          val lastNamePart = remParts(0)
          val comps = remParts.flatMap(ParserHelpers.splitComponents)
          val compMatches = comps.filter(c => AcademicTitles.TitleSetFromComponents.contains(c.toLowerCase))
          if (compMatches.nonEmpty) {
            Option(s"T[$name](${compMatches.mkString(",")})")
          }
          else None
        }
      }
    }
    else None

  }


}
