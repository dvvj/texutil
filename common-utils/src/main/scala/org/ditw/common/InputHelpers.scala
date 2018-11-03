package org.ditw.common

import TypeCommon._
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
    phrases.flatMap(SpaceSplitter.split).map(_.toLowerCase()).toSet
  }
  def splitVocabEntries(phrases:Set[String]):Set[Array[String]] = {
    phrases.map(SpaceSplitter.split)
  }

}
