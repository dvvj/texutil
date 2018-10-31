package org.ditw.common

object InputHelpers extends Serializable {

  def loadDict0(words:Iterable[Iterable[String]]):Dict = {
    val wordSeq = words.flatMap(s => s.map(_.toLowerCase()))
      .toSet
      .toIndexedSeq
    val m = wordSeq.sorted.indices
      .map(idx => wordSeq(idx) -> idx)
      .toMap
    new Dict(m)
  }

  def loadDict(words:Iterable[String]*):Dict = {
    loadDict0(words)
  }

}
