package org.ditw.tknr

/**
  * Created by dev on 2018-10-26.
  */

class Token(
  private var idx: Int,
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

  def reIndex(idx:Int):Token = new Token(
    idx, content, pfx, sfx
  )
}
