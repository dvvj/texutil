package org.ditw.tknr
import scala.collection.mutable.ArrayBuffer
import scala.collection.{IndexedSeqLike, mutable}

class SeqOfTokens(
  val orig:String,
  val origTokenStrs:IndexedSeq[String],
  tokens: Seq[Token]
) extends IndexedSeq[Token] with IndexedSeqLike[Token, SeqOfTokens] {

  import SeqOfTokens._

  private val _tokens = {
    tokens.foreach(_.setLoT(this))
    tokens.toIndexedSeq
  }
  override def apply(idx:Int):Token = _tokens(idx)

  override def length: Int = _tokens.length

  override def newBuilder: mutable.Builder[Token, SeqOfTokens] =
    _newBuilder
}

object SeqOfTokens {
  def fromTokens(tokens:Seq[Token]):SeqOfTokens = {
    val origTokenStrs = tokens.map { t =>
      s"${t.pfx}${t.content}${t.sfx}"
    }
    val orig = origTokenStrs.mkString(" ")
    val reIndexed = tokens.indices.map(idx => tokens(idx).reIndex(idx))
    new SeqOfTokens(orig, origTokenStrs.toIndexedSeq, reIndexed)
  }
  private def _newBuilder: mutable.Builder[Token, SeqOfTokens] =
    new ArrayBuffer[Token] mapResult fromTokens
}

