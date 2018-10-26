package org.ditw.tknr

/**
  * Created by dev on 2018-10-26.
  */
import TknrResults._
case class Token(
  lineResult: LineResult,
  idx: Int,
  content: String,
  pfx: String,
  sfx: String
) {
  override def toString: String = {
    val tr = s"$pfx||$content||$sfx"
    val orig = lineResult.origTokenStrs(idx)
    s"$tr($orig)"
  }
}
