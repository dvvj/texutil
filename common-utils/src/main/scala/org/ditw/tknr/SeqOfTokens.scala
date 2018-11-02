package org.ditw.tknr
import org.ditw.common.{Dict, TkRange}

import scala.collection.mutable.ArrayBuffer
import scala.collection.{IndexedSeqLike, mutable}

class SeqOfTokens(
  val orig:String,
  val origTokenStrs:IndexedSeq[String],
  _tokens: Seq[Token]
) extends IndexedSeq[Token] with IndexedSeqLike[Token, SeqOfTokens] {

  import SeqOfTokens._

  private[ditw] val tokens:IndexedSeq[Token] = {
    _tokens.foreach(_.setLoT(this))
    _tokens.toIndexedSeq
  }
  override def apply(idx:Int):Token = tokens(idx)

  override def length: Int = tokens.length

  override def newBuilder: mutable.Builder[Token, SeqOfTokens] =
    _newBuilder

  override def toString(): String = {
    s"[$orig] size=$length"
  }

  def rangeBy(
    range: TkRange,
    sfxs:Set[String]
  ):TkRange = {
    var start = range.start-1
    var found = false
    while (start >= 0 && !found) {
      val token = tokens(start)
      if (sfxs.contains(token.sfx) ||
        (token.content.isEmpty && sfxs.contains(token.str))
      ) {
        found = true
      }
      else
        start -= 1
    }
    start = if (found) start+1
      else 0

    var end = range.end-1
    found = false
    while (end < tokens.size && !found) {
      val token = tokens(end)
      if (sfxs.contains(token.sfx) ||
        (token.content.isEmpty && sfxs.contains(token.str))
      ) {
        found = true
      }
      else
        end += 1
    }
    end = if (found) {
        // if the (end) token is ',' self, use end instead
        if (tokens(end).content.isEmpty) end
        else end+1
      }
      else tokens.size
    TkRange(range.input, range.lineIdx, start, end)
  }
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

