package org.ditw.nameUtils.nameParser

import java.util.regex.Pattern

import ParserHelpers.InputKeyEnum._
import ParserHelpers.{combineAttrParts, combineNameComponents, splitTrim}
import org.ditw.nameUtils.nameParser.utils.AcademicTitles._
import org.ditw.nameUtils.nameParser.utils.SuffixData._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-09-08.
  */
object MultiPartNameUtils extends Serializable {
  private[nameParser] val SpaceSplitter = Pattern.compile("""[\s/&]""")

  private val EmptyStr = ""

  private def findSuffix(nameParts:IndexedSeq[String], suffixSet:Set[String])
    :(IndexedSeq[String], Option[String]) = {
    val possibleSuffixes = ListBuffer[String]()
    val remComps = nameParts.flatMap { p =>
      if (suffixSet.contains(p.toLowerCase)) {
        possibleSuffixes += p
        None
      }
      else Option(p)
    }
    val suffix =
      if (possibleSuffixes.nonEmpty) {
        if (possibleSuffixes.size == 1) Option(possibleSuffixes.head)
        else Option(combineAttrParts(possibleSuffixes))
      }
      else None
    remComps -> suffix
  }

  private val PartCompSplitter = Pattern.compile("""[\s\.\-/&]""")
  private[nameParser] def checkWholePartTitle(part:String):Boolean = {
    val compStr = splitTrim(PartCompSplitter, part).mkString(" ")
    TitleSetFromParts.contains(compStr)
  }

  private[nameParser] def preprocessNameParts(nameParts:IndexedSeq[String])
  :(IndexedSeq[String], Map[InputKeyEnum,String]) = {
    val titles = ListBuffer[String]()

    var remNameParts = nameParts.flatMap { p =>
      val tr = p.trim
      if (checkWholePartTitle(tr.toLowerCase)) {
        titles += tr
        None
      }
      else Option(tr)
    }

    val possibleSuffixes = ListBuffer[String]()
    remNameParts = remNameParts.flatMap { p =>
      val spacedParts = splitTrim(SpaceSplitter, p)
      val remParts = ListBuffer[String]()
      spacedParts.foreach { sp =>
        if (TitleSetFromComponents.contains(sp.toLowerCase)) titles += sp
        else if (UnambiguousSuffixLiterals.contains(sp.toLowerCase)) possibleSuffixes += sp
        else remParts += sp
      }

      if (remParts.nonEmpty) Option(combineNameComponents(remParts))
      else None
    }
    val attrMap = mutable.Map[InputKeyEnum,String]()
    if (titles.nonEmpty) attrMap += MultiPart_AcademicTitles -> combineAttrParts(titles)
    if (possibleSuffixes.nonEmpty) attrMap += MultiPart_SuffixPart -> combineAttrParts(possibleSuffixes)

//    val (rem, suffix) = findSuffix(remNameParts, UnambiguousSuffixLiterals)
//    if (suffix.nonEmpty) attrMap += MultiPart_SuffixPart -> suffix.get
//    remNameParts = rem

    // if there are still more parts, try to find parts containing only suffixes
    if (remNameParts.size > 2) {
      val (rem, suffix) = findSuffix(remNameParts, SuffixLiterals)
      if (suffix.nonEmpty) attrMap += MultiPart_SuffixPart -> suffix.get
      remNameParts = rem
    }

    // make sure we have at most 2 name parts
    if (remNameParts.size > 2) {
      remNameParts = IndexedSeq(
        remNameParts.head,
        combineNameComponents(remNameParts.tail)
      )
    }

    remNameParts -> attrMap.toMap
  }

  private[nameParser] def removeCI(str:String, toRemove:String):(Boolean, String) = {
    val lower = str.toLowerCase

    val pieces = ListBuffer[String]()

    var idx = lower.indexOf(toRemove)
    var start = 0
    var foundMatch = false
    while (idx >= 0) {
      foundMatch = true
      pieces += str.substring(start, idx)
      start = idx+toRemove.length
      idx = lower.indexOf(toRemove, start)
    }

    pieces += str.substring(start, str.length)
    (foundMatch, pieces.mkString.trim)
  }

  private[nameParser] def removeNoiseCI(s:String, noises:Iterable[String]):String = {
    var t1 = s
    noises.foreach { noise =>
      val t = removeCI(t1, noise)
      t1 = t._2
    }
    t1
  }

}
