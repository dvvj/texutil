package org.ditw.nameUtils.nameParser.utils.multiLN
import org.ditw.nameUtils.nameParser.MedlineParsers.TNameParser
import org.ditw.nameUtils.nameParser.ParserHelpers.ResultAttrKeyEnum._
import org.ditw.nameUtils.nameParser.ParserHelpers.{NormInput, NormResult, splitParts}
import org.ditw.nameUtils.SimplifyCharset
import org.ditw.nameUtils.nameParser.ParserHelpers

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2018-12-17.
  */
object MLNParsers extends Serializable {

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._

  private[nameParser] type LNSplitterResult = (Vector[String], Vector[String])
  private[nameParser] type MLNFullNameSplitter = (String) => LNSplitterResult

//  private def checkReligiousNames(lastNames:Array[String], relFirstNames:Set[Array[String]]):Option[Array[String]] = {
//    var found = false
//    val it = relFirstNames.iterator
//    var relFn = Array[String]()
//    while (!found && it.hasNext) {
//      relFn = it.next()
//      if (lastNames.length > relFn.length) {
//        found = relFn.indices.forall(idx => relFn(idx) == lastNames(idx))
//      }
//    }
//    if (found)
//      Option(relFn)
//    else None
//  }

  private[nameParser] trait TStrArrLookup extends Serializable {
    def find(in:Array[String]):Option[Array[String]]
  }

  private def seqArrSetLookup(strArrSet:Seq[Array[String]]):TStrArrLookup = new TStrArrLookup {
    private val _set = strArrSet
    override def find(in: Array[String]): Option[Array[String]] = {
      var found = false
      val it = _set.iterator
      var curr = Array[String]()
      while (!found && it.hasNext) {
        curr = it.next()
        if (in.length > curr.length) {
          found = curr.indices.forall(idx => curr(idx) == in(idx))
        }
      }
      if (found)
        Option(curr)
      else None
    }
  }

  private def parseDefImpl(
                            fullName:String,
                            lastNameSet:Set[String],
                            religiousNameLookup:Option[TStrArrLookup]
                            //religiousFirstNames:Set[Array[String]]
                          ):LNSplitterResult = {
    val parts = splitParts(fullName)
    if (parts.length > 1) {
      //val firstNames = ListBuffer[String](parts.head)
      var idx = 1
//      var isInit = true
      var isLastName = false
      while (!isLastName && idx < parts.length) {
//        val isInit = parts(idx).length == 1

        val normedCompss = SimplifyCharset.normalizeAndAsciify(parts(idx)).toLowerCase()
          .split("\\-")
        isLastName = normedCompss.exists(lastNameSet.contains)
        if (!isLastName)
          idx += 1
      }

      if (idx > parts.length-1) {
        //println(s"todo: no last name left, should probably flip: [$fullName]")
        idx = parts.length-1
      }

//      val lastPartInit = firstNames.last.length == 1
//      val checkLastName = lastNameSet.nonEmpty
//      if (!lastPartInit && checkLastName && idx != parts.length-1) {
//        // check if the next one is first name or last name
//        val part2Check = SimplifyCharset.normalizeAndAsciify(parts(idx))
//        if (!lastNameSet.contains(part2Check.toLowerCase())) {
//          firstNames += parts(idx)
//          idx += 1
//        }
//      }
      var lastNames = parts.slice(idx, parts.length)
      val firstNames = ListBuffer[String]()
      firstNames ++= parts.slice(0, idx)
      val normedLastNames = lastNames.map(SimplifyCharset.normalizeAndAsciify).map(_.toLowerCase())
      if (religiousNameLookup.nonEmpty) {
        //val lastnamesFlatten = lastNames.mkString(" ").toLowerCase()
        val relNameMatch = religiousNameLookup.get.find(normedLastNames.toArray)
        if (relNameMatch.nonEmpty) {
          val len = relNameMatch.get.length
          val relName = lastNames.slice(0, len).mkString(" ")
          if (firstNames.nonEmpty) {
            val mergedLastFN = s"${firstNames.last} $relName"
            firstNames.remove(firstNames.size-1)
            firstNames += mergedLastFN
          }
          else {
            //firstNames += relName // only rel name part?
            throw new RuntimeException("only religous name part found?")
          }

          lastNames = lastNames.slice(len, lastNames.length)
        }
      }
      firstNames.toVector -> lastNames.toVector
    }
    else {
      //throw new IllegalArgumentException(s"Only one name part found from [$fullName]")
      println(s"Only one name part found from [$fullName]")
      _EmptySplits
    }
  }
  private val _EmptySplits = Vector[String]() -> Vector[String]()

  private val DoNotCheckLastNames = Set[String]()
  private val DoNotCheckReligiousNames:Option[TStrArrLookup] = None

  private[nameParser] val parseFullName_Default:MLNFullNameSplitter = fullName => {
    parseDefImpl(fullName, DoNotCheckLastNames, DoNotCheckReligiousNames)
  }

  import org.ditw.nameUtils.nameParser.langSpecData.SpanishLangData._
  private val SpanishLastNameParts = HispanicNameParts ++ Set("de", "las", "del", "los", "la", "dela")
  private val spanishReligiousNameLookup = Option(seqArrSetLookup(SpanishReligiousNames))
  private[nameParser] val parseFullName_Hispanic:MLNFullNameSplitter = fullName => {
    parseDefImpl(fullName, SpanishLastNameParts, spanishReligiousNameLookup)
  }


  private[nameParser] val _RegisteredSplitters = Map[LanguageEnum, MLNFullNameSplitter](
    Hispanic -> parseFullName_Hispanic
  )

  private[nameParser] def parseFullName(lang:LanguageEnum, fullName:String):LNSplitterResult = {
    val parser = _RegisteredSplitters.getOrElse(lang, parseFullName_Default)
    parser(fullName)
  }

  import collection.mutable
  private[nameParser] def resultFrom(
    firstNames:IndexedSeq[String],
    lastNames:IndexedSeq[String],
    lang: LanguageEnum,
    extraNames: Option[IndexedSeq[String]]
  ): NormResult = {
    val m = mutable.Map[ResultAttrKeyEnum, IndexedSeq[String]]()
    m += FirstName -> firstNames
    m += LastName -> lastNames
    if (extraNames.nonEmpty)
      m += ExtraNames -> extraNames.get
    new NormResult(
      lastNames = lastNames,
      m.toMap,
      Option(lang)
    )
  }

  import ParserHelpers.InputKeyEnum._
  private def nameStr(input:NormInput):String = {
    var res = ""
    if (input.contains(Medline_ForeName))
      res += input(Medline_ForeName)
    if (input.contains(Medline_LastName))
      res += " " + input(Medline_LastName)
    res
  }

  import ParserHelpers._
  private[nameParser] trait TLFNParser extends TNameParser {
    val lfnSplitter:MLNFullNameSplitter
    val lastNameConn:Set[String]
    val lang:LanguageEnum
    val lastNameNobleTitleLookup:TStrArrLookup
    private def splitLastNames(lnParts:IndexedSeq[String]):IndexedSeq[String] = {
      lnParts
    }
    override def parse(tin: NormInput): Option[NormResult] = {
      val src = nameStr(tin)
      val (foreNames, lastNames) = lfnSplitter(src)

      if (lastNames.isEmpty || foreNames.isEmpty)
        None
      else {
        val connIdx = lastNames.indices.filter(idx => lastNameConn.contains(lastNames(idx)))
        val lastNameParts = ListBuffer[String]()
        if (connIdx.nonEmpty) {
          if (connIdx.size > 1)
            throw new IllegalArgumentException(s"todo: 2 or more connecting symbols [$src]?")
          else {
            val idx = connIdx.head
            val p1 = lastNames.slice(0, idx)
            val p2 = lastNames.slice(idx+1, lastNames.length)
            if (p1.nonEmpty)
              lastNameParts += p1.mkString(" ")
            if (p2.nonEmpty)
              lastNameParts += p2.mkString(" ")
          }
        }
        else {
          lastNameParts += lastNames.mkString(" ")
        }

        val (firstNames, exNames) =
          if (foreNames.size > 1) foreNames.slice(0, 1) -> Option(foreNames.slice(1, foreNames.size))
          else foreNames -> None
        Option(
          resultFrom(
            firstNames, splitLastNames(lastNameParts.toIndexedSeq), lang, exNames
          )
        )
      }
    }
  }

  private def LFNParser(
                         _lang: LanguageEnum,
                         _lastNameConn:Set[String],
                        _lastNameNobleTitleLookup:TStrArrLookup
                       ) = new TLFNParser {
    override val lang:LanguageEnum = _lang
    override val lastNameConn: Set[String] = _lastNameConn
    override val lfnSplitter: MLNFullNameSplitter = _RegisteredSplitters(lang)
    override val lastNameNobleTitleLookup: TStrArrLookup = _lastNameNobleTitleLookup
  }

  private val hispanicLastNameNobleTitles =
    seqArrSetLookup(
      Set(
        "de", "de la", "de las", "del", "de los"
      ).map(_.split("\\s+"))
      .toIndexedSeq
      .sortBy(_.length)(Ordering[Int].reverse)
    )
  private[nameParser] val hispanicParser = LFNParser(Hispanic, Set("y", "Y"), hispanicLastNameNobleTitles)
  private val registeredParsers = Map(
    Hispanic -> hispanicParser
  )

  def getParser(lang: LanguageEnum):TNameParser = registeredParsers(lang)
}
