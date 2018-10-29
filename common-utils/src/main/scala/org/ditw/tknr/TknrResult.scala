package org.ditw.tknr

/**
  * Created by dev on 2018-10-26.
  */
object TknrResults extends Serializable {

  private val EmptyTokens = IndexedSeq[Token]()

//  private[tknr] class LineResult(
//    val origTokenStrs: IndexedSeq[String],
//    val origLine: String,
//    val tokens: IndexedSeq[Token]
//  ) {
//    tokens.foreach(_.setLineResult(this))
//  }
//
  case class TknrResult(
    lineResults: IndexedSeq[SeqOfTokens]
  ) {}
}
