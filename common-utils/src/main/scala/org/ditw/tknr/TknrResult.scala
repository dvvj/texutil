package org.ditw.tknr

/**
  * Created by dev on 2018-10-26.
  */
object TknrResults extends Serializable {

  private val EmptyTokens = IndexedSeq[Token]()

  private[tknr] class LineResult(
    val origTokenStrs: IndexedSeq[String],
    val origLine: String
  ) {
    private var _tokens: IndexedSeq[Token] = EmptyTokens
    private[tknr] def _setTokens(tokens: IndexedSeq[Token]): Unit = {
      _tokens = tokens
    }
    def tokens: IndexedSeq[Token] = _tokens
  }

  case class TknrResult(
    lineResults: IndexedSeq[LineResult]
  ) {}
}
