package org.ditw.common

case class TkRange(
  input:Input,
  lineIdx:Int,
  start:Int,
  end:Int
) {
  override def hashCode(): Int = {
    (lineIdx << 24) + (start << 16) + (end << 8) + input.hashCode()
  }

  override def equals(obj: Any): Boolean = obj match {
    case r:TkRange => {
      r.lineIdx == lineIdx && r.start == start && r.end == end
    }
    case _ => false
  }

  def origStr:String = {
    val sot = input.tknrResult.linesOfTokens(lineIdx)
    sot.origTokenStrs.slice(start, end).mkString(" ")
  }

  def str:String = {
    val sot = input.tknrResult.linesOfTokens(lineIdx)
    sot.slice(start, end).map(_.content).mkString(" ")
  }
}
