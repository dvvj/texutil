package org.ditw.nameUtils.nameParser.langSpecData

import DutchLangData.DutchSortedPrefixes
import FrenchLangData.FrenchSortedPrefixes
import PortugueseLangData.PortugueseSortedPrefixes
import SpanishLangData.SpanishSortedPrefixes
import ItalianLangData.ItalianSortedPrefixes
import GermanLangData.GermanSortedPrefixes
import org.ditw.nameUtils.nameParser.utils.NamePartHelpers.PrefixParser

/**
  * Created by dev on 2017-08-24.
  */
object PfxParsers {

  //def parseInLastName()
  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
  private[nameParser] val _DutchPrefixParser = new PrefixParser(Dutch, DutchSortedPrefixes)
  private[nameParser] val _FrenchPrefixParser = new PrefixParser(French, FrenchSortedPrefixes)
  //private[nameParser] val _PortuguesePrefixParser = new PrefixParser(Portuguese, PortugueseSortedPrefixes)
  //private[nameParser] val _SpanishPrefixParser = new PrefixParser(Spanish, SpanishSortedPrefixes)
  private[nameParser] val _ItalianPrefixParser = new PrefixParser(Italian, ItalianSortedPrefixes)
  private[nameParser] val _GermanPrefixParser = new PrefixParser(German, GermanSortedPrefixes)

  val all = Iterable(_DutchPrefixParser, _FrenchPrefixParser)
}
