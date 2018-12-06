package org.ditw.common

import TypeCommon._
import org.ditw.tknr.TknrHelpers
object InputHelpers extends Serializable {

  def loadDict0(words:Iterable[Iterable[String]]):Dict = {
    val wordSeq = words.flatMap(s => s.map(_.toLowerCase()))
      .toSet
      .toIndexedSeq
    val m:Map[String, DictEntryKey] = wordSeq.sorted.indices
      .map(idx => wordSeq(idx) -> idx.asInstanceOf[DictEntryKey])
      .toMap
    new Dict(m)
  }

  def loadDict(words:Iterable[String]*):Dict = {
    loadDict0(words)
  }

  def loadDict(words:Iterable[Iterable[String]]):Dict = {
    loadDict0(words)
  }

  private val SpaceSplitter = "\\s+".r
  def splitVocabToSet(phrases:Iterable[String]):Set[String] = {
    phrases.flatMap(splitVocabEntry).map(_.toLowerCase()).toSet
  }

  private val punctCharSet = TknrHelpers.PunctChars.toSet
  def splitVocabEntries(phrases:Set[String]):Set[Array[String]] = {
    phrases.map(splitVocabEntry)
  }

  def splitVocabEntry(phrase:String):Array[String] = {
    val tokenSeq = SpaceSplitter.split(phrase)
    tokenSeq.flatMap { token =>
      if (punctCharSet.contains(token.last)) {
        if (token.length > 1)
          Option(token.substring(0, token.length - 1)) // todo
        else
          None
      } else
        Option(token)
    }
  }

}
