package org.ditw.nameUtils.nameParser.utils.inferers

import java.util.regex.Pattern

import org.ditw.nameUtils.nameParser.langSpecData.SpanishLangData
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum.{LanguageEnum}

/**
  * Created by dev on 2017-08-31.
  */
private[nameParser] object LanguageInferers extends Serializable {
  type Name2LangInferer =
    ( String, IndexedSeq[String], IndexedSeq[String], // fore name, parts, components
      String, IndexedSeq[String], IndexedSeq[String]  // last name, parts, components
      ) => (Iterable[LanguageEnum], Iterable[LanguageEnum])

  val EmptyLangs = Iterable[LanguageEnum]()
  val NotInferred = (EmptyLangs, EmptyLangs)

  import LastNameInferers._
  import NameComponentInferers._
  import FullNameInferers._
  import NamePartInferers._

  // priority based on list order
  private[inferers] val PrioritizedInferers = List(
    List[Name2LangInferer](
      lastNamePartInferer, namePartInferer, lastNameComponentInferer, fullNameInferer
    )
  )

  import org.ditw.nameUtils.nameParser.ParserHelpers._
  private[nameParser] def inferLanguages(foreName:String, lastName:String)
    :(Iterable[LanguageEnum], Iterable[LanguageEnum]) = {
    var done = false
    var result = NotInferred
    val foreNameParts = splitParts(foreName)
    val foreNameComps = splitComponents(foreName)
    val lastNameParts = splitParts(lastName)
    val lastNameComps = splitComponents(lastName)
    val itInfererList = PrioritizedInferers.iterator
    while (!done && itInfererList.hasNext) {
      val infererList = itInfererList.next()
      val itInferer = infererList.iterator
      while (!done && itInferer.hasNext) {
        val inferer = itInferer.next()
        val (confirmed, possible) = inferer(
          foreName, foreNameParts, foreNameComps, lastName, lastNameParts, lastNameComps
        )
        if (confirmed.nonEmpty) {
          done = true
          result = (confirmed, EmptyLangs)
        }
        else if (possible.nonEmpty) {
          result = (EmptyLangs, result._2++possible) // todo: find which one is more 'possible'
        }
      }
    }
    result
  }

  private[inferers] def regexMap(pairs:(String, LanguageEnum)*):Map[Pattern, Iterable[LanguageEnum]] = {
    pairs.map { p =>
      val ptn = Pattern.compile(p._1)
      val lang = Iterable(p._2)
      ptn -> lang
    }.toMap
  }

  private[inferers] def inferByRegex(
                    regexesMap:Map[Pattern, Iterable[LanguageEnum]],
                    nameUnits:IndexedSeq[String]
                  ):Iterable[LanguageEnum] = {
    regexesMap.filterKeys { k =>
      nameUnits.exists{ c =>
        k.matcher(c).matches()
      }
    }.flatMap(_._2)
  }

  private[inferers] def inferByMatch(
                                      strSetMap:Map[Set[String], Iterable[LanguageEnum]],
                                      nameUnits:IndexedSeq[String]
                                    ):Iterable[LanguageEnum] = {
    strSetMap.filterKeys { k =>
      nameUnits.exists(k.contains)
    }.flatMap(_._2)
  }


  private[inferers] def inferFromNameStringByRegex(
                    regexesMap:Map[Pattern, Iterable[LanguageEnum]],
                    nameStr:String
                  ):Iterable[LanguageEnum] = {
    regexesMap.filterKeys { k =>
      k.matcher(nameStr).find()
    }.flatMap(_._2)
  }

  private[inferers] def confirmedInference(inferred:Iterable[LanguageEnum]):(Iterable[LanguageEnum], Iterable[LanguageEnum]) = {
    if (inferred.nonEmpty) (inferred, EmptyLangs)
    else NotInferred
  }
}
