package org.ditw.nameUtils.nameParser.utils

import NameLanguages.LanguageEnum._

/**
  * Created by dev on 2017-08-25.
  */
object AlphabetHelpers {

  import collection.mutable
  private def createMapping(
    lower2Upper:Seq[(Char, List[LanguageEnum])],
    noConversion:Seq[(Char, List[LanguageEnum])]
  ):Map[Char, List[LanguageEnum]] = {
    val m = mutable.Map[Char, List[LanguageEnum]]()

    lower2Upper.foreach { p =>
      m += p
      m += p._1.toUpper -> p._2
    }
    noConversion.foreach { p =>
      m += p
    }

    m.toMap
  }

  val Char2LangMap = createMapping(
    Seq(
      'ż' -> List(Polish),
      'ź' -> List(Polish),
      'ž' -> List(Polish),

      'ů' -> List(Czech),
      'ū' -> List(Latvian, Lithuanian),
      'ű' -> List(Hungarian),
      'ü' -> List(German, Hungarian, Turkish),
      'û' -> List(French, Turkish),
      'ù' -> List(Italian),
      'ú' -> List(Czech, Faroese, Icelandic, Irish),

      'ý' -> List(Czech, Faroese, Icelandic),

      'ť' -> List(Czech),
      'ŧ' -> List(Sami),
      'þ' -> List(Icelandic),
      'ţ' -> List(Romanian),

      'ß' -> List(German),
      'ş' -> List(Romanian, Turkish),
      'š' -> List(Czech, Croation, Estonian, Latvian, Lithuanian, Sami),
      'ś' -> List(Polish),

      'ř' -> List(Czech),

      'œ' -> List(French),
      'ø' -> List(Danish, Faroese, Norwegian),
      'ő' -> List(Hungarian),
      'ö' -> List(Estonian, Finnish, German, Hungarian, Icelandic, Swedish, Turkish),
      'õ' -> List(Estonian, Hispanic),
      'ô' -> List(French, Hispanic),
      'ò' -> List(Italian),
      'ó' -> List(Czech, Faroese, Hungarian, Icelandic, Irish, Polish),

      'ŋ' -> List(Sami),
      'ņ' -> List(Latvian),
      'ň' -> List(Czech),
      'ñ' -> List(Hispanic),
      'ń' -> List(Polish),

      'ļ' -> List(Latvian),
      'ł' -> List(Polish),

      'ķ' -> List(Latvian),

      'į' -> List(Lithuanian),
      'ī' -> List(Latvian),
      'ï' -> List(Dutch, French),
      'î' -> List(French, Turkish),
      'ì' -> List(Italian),
      'í' -> List(Czech, Faroese, Hungarian, Irish, Icelandic),

      'ğ' -> List(Turkish),
      'ģ' -> List(Latvian),

      'ę' -> List(Lithuanian, Polish),
      'ė' -> List(Lithuanian),
      'ē' -> List(Latvian),
      'ě' -> List(Czech),
      'ë' -> List(Albanian, Dutch, French),
      'ê' -> List(French, Hispanic),
      'è' -> List(French),
      'é' -> List(Czech, French, Hungarian, Icelandic, Irish),

      'ð' -> List(Faroese, Icelandic),
      'đ' -> List(Croation, Sami),
      'ď' -> List(Czech),

      'ç' -> List(Albanian, French, Hispanic, Turkish),
      'č' -> List(Croation, Czech, Latvian, Lithuanian, Sami),
      'ć' -> List(Croation, Polish),

      'æ' -> List(Danish, Faroese, Norwegian),
      'ą' -> List(Lithuanian, Polish),
      'å' -> List(Danish, Norwegian, Swedish),
      'ā' -> List(Latvian),
      'ă' -> List(Romanian),
      'ä' -> List(Estonian, Finnish, German, Swedish),
      'ã' -> List(Hispanic),
      'â' -> List(French, Romanian, Turkish),
      'à' -> List(French, Italian),
      'á' -> List(Czech, Faroese, Hungarian, Icelandic, Irish, Hispanic, Sami, Hispanic)
    ),
    Seq(
      'ı' -> List(Turkish),
      'İ' -> List(Turkish)
    )
  )

  val Char2LangAntiMap = createMapping(
    Seq(
      'j' -> List(Italian),
      'k' -> List(Italian, Hispanic),
      'q' -> List(Croation, Faroese, Latvian, Lithuanian, Polish, Sami, Turkish),
      'v' -> List(Polish),
      'w' -> List(Albanian, Croation, Faroese, Italian, Latvian, Lithuanian, Hispanic, Sami, Turkish),
      'x' -> List(Croation, Faroese, Italian, Latvian, Lithuanian, Polish, Sami, Turkish),
      'y' -> List(Italian, Latvian, Hispanic, Sami),
      'z' -> List(Faroese)
    ),
    Seq()
  )

  def langByAlphabets(nameParts:Seq[String]):Iterable[(LanguageEnum,Int)] = {
    val nonNullParts = nameParts.filter(p => p != null && !p.isEmpty)
    val negMatches:Set[LanguageEnum] =
      nonNullParts.flatMap(_.flatMap(Char2LangAntiMap.get).flatten).toSet
    val allLetters:Seq[Char] = nonNullParts.flatten.filter(_.isLetter)
    val nonStandardLetterCount = allLetters.count { l =>
      val ll = l.toLower
      ll < 'a' || ll > 'z'
    }
    val allLang:Seq[LanguageEnum] = allLetters.flatMap(Char2LangMap.get).flatten
    allLang.filter(x => !negMatches.contains(x)).groupBy(l => l).map(p => p._1 -> p._2.size)
      .filter(_._2 == nonStandardLetterCount) // all non-standard letters should be from the lang
      .toList.sortBy(_._2)(Ordering[Int].reverse)
  }
}
