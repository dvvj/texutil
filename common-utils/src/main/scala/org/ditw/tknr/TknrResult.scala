package org.ditw.tknr
import org.ditw.common.Dict

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
  class TknrResult(
    val orig:String,
    val dict:Dict,
    val linesOfTokens: IndexedSeq[SeqOfTokens]
  ) {
    val encoded:IndexedSeq[IndexedSeq[Int]] = {
      linesOfTokens.map(_._tokens.map(t => dict.enc(t.content)))
    }
  }
}
