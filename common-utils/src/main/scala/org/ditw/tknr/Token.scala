package org.ditw.tknr

/**
  * Created by dev on 2018-10-26.
  */

class Token(
  private[tknr] val idx: Int,
  val content: String,
  val pfx: String,
  val sfx: String
) {
  private var _sot: SeqOfTokens = null
  private[tknr] def setLoT(lot: SeqOfTokens):Unit = {
    _sot = lot
  }

  override def toString: String = {
    val tr = s"$pfx||$content||$sfx"
    val orig = _sot.origTokenStrs(idx)
    s"$tr($orig)"
  }

  def reIndex(newIdx:Int):Token = new Token(
    newIdx, content, pfx, sfx
  )
}
