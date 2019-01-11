package org.ditw.nameUtils.nameParser.utils

import NameLanguages.LanguageEnum.LanguageEnum

/**
  * Created by dev on 2017-08-23.
  */
object NamePartHelpers {
  private[nameParser] def toPairSeq(namePart1:String, namePart2:IndexedSeq[String]):IndexedSeq[IndexedSeq[String]] =
    namePart2.map(np2 => IndexedSeq(namePart1, np2))
  private[nameParser] def sortStringSeq(seqs:Iterable[IndexedSeq[String]]):IndexedSeq[IndexedSeq[String]] = {
    seqs.toIndexedSeq.sortBy(_.mkString)(Ordering[String].reverse)
  }

  private[nameParser] val GreaterThan = 1
  private[nameParser] val LessThan = -1
  //private def isLessThan(r:Int) = r < 0
  private[nameParser] def isGreaterThan(r:Int) = r > 0

  private[nameParser] val Equal = 0

  private[nameParser] def compareEntry(
                                        strSeq2Compare:IndexedSeq[String],
                                        entry:IndexedSeq[String],
                                        matchPrefix:Boolean
                                      ):Int = {
    var idx = 0
    var partResult = Equal
    while (partResult == Equal && idx < strSeq2Compare.size && idx < entry.size) {
      partResult = strSeq2Compare(idx).compareTo(entry(idx))
      if (partResult > 0) partResult = GreaterThan
      else if (partResult < 0) partResult = LessThan
      idx = idx+1
    }
    if (partResult != Equal) partResult
    else if (idx >= entry.size) {
      // strSeq2Compare is longer
      if (matchPrefix) Equal // prefix equal
      else if (idx < strSeq2Compare.size) GreaterThan
      else Equal
    }
    else LessThan  // entry is longer
  }

  private def findMatch(
    strSeq2Search:IndexedSeq[String],
    reverseSortedDict:IndexedSeq[IndexedSeq[String]],
    matchPrefix:Boolean = true
  ):Option[IndexedSeq[String]] = {
    //      var idx = 0
    //      var found = false
    //      var eidx1 = 0
    //      var eidx2 = _dict.size-1
    //      var eidx = (eidx1+eidx2) / 2
    //      while (!found && eidx1 < eidx2) {
    //        eidx = (eidx1+eidx2) / 2
    //        val cmp = compareEntry(strSeq2Search, _dict(eidx), matchPrefix)
    //        if (cmp == Equal) found = true
    //        else if (isGreaterThan(cmp)) {
    //          eidx2 = eidx-1
    //        }
    //        else eidx1 = eidx+1
    //      }
    //      if (found) Option(_dict(eidx))
    //      else None

    var found = false
    var idx = 0
    val strSeq2SearchLower = strSeq2Search.map(_.toLowerCase)
    while (!found && idx < reverseSortedDict.size) {
      val cmp = compareEntry(strSeq2SearchLower, reverseSortedDict(idx), matchPrefix)
      if (cmp == Equal) found = true
      else idx = idx+1
    }
    if (found) Option(strSeq2Search.slice(0, reverseSortedDict(idx).size)) else None
  }


  private[nameParser]
  class PrefixParser(
    val lang:LanguageEnum,
    private val _dict:IndexedSeq[IndexedSeq[String]]
  ) extends Serializable {
    private val _reverseDict = sortStringSeq(_dict.map(_.reverse))
    def findInLastName(
      strSeq2Search:IndexedSeq[String],
      matchPrefix:Boolean = true
    ):Option[IndexedSeq[String]] = findMatch(strSeq2Search, _dict, matchPrefix)

    def findInForeName(
      strSeq2Search:IndexedSeq[String]
    ):Option[IndexedSeq[String]] = {
      val r = findMatch(strSeq2Search.reverse, _reverseDict, true)
      r.map(_.reverse)
    }

  }

  private[nameParser]
  class MultiNamePrefixParser(
                      val lang:LanguageEnum,
                      private val _dict:IndexedSeq[IndexedSeq[String]],
                      private val _conj:String
                    ) extends Serializable {
    private val _reverseDict = sortStringSeq(_dict.map(_.reverse))
    def findInLastName(
                        strSeq2Search:IndexedSeq[String],
                        matchPrefix:Boolean = true
                      ):Option[IndexedSeq[String]] = findMatch(strSeq2Search, _dict, matchPrefix)

    def findInForeName(
                        strSeq2Search:IndexedSeq[String]
                      ):Option[IndexedSeq[String]] = {
      val r = findMatch(strSeq2Search.reverse, _reverseDict, true)
      r.map(_.reverse)
    }

  }


}
