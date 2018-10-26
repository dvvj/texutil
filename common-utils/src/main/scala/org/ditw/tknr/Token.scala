package org.ditw.tknr

/**
  * Created by dev on 2018-10-26.
  */
import TknrResults._
case class Token(
  lineResult: LineResult,
  idx: Int,
  pfx: String,
  sfx: String
) {}
