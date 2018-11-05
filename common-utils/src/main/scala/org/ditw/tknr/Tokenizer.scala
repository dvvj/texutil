package org.ditw.tknr

import org.ditw.common.Dict
import org.ditw.tknr.Trimmers.TTrimmer

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

/**
  * Created by dev on 2018-10-26.
  */
object Tokenizers extends Serializable {
  trait TTokenizer extends Serializable {
    def run(input: String, dict:Dict): TknrResult
  }

  trait TTokenSplitter extends Serializable {
    def split(input: String): IndexedSeq[String]
  }

  val EmptyTokenStrs = IndexedSeq[String]()
  case class TokenSplitterCond(
    _condRegex: String,
    _tokenSplitter: String,
    splitterStrsToKeep:Set[String]
  ) extends TTokenSplitter {
    private val condRegex: Regex = _condRegex.r
    private val tokenSplitter: Regex = _tokenSplitter.r
    def split(input: String): IndexedSeq[String] = {
      if (canSplit(input)) {
        val parts = tokenSplitter.split(input)
        val res = ListBuffer[String]()
        var start = 0
        parts.foreach { p =>
          val s = input.indexOf(p, start)
          if (s > start) {
            val pfx = input.substring(start, s).trim
            if (!pfx.isEmpty && splitterStrsToKeep.contains(pfx.toLowerCase()))
              res += pfx
          }
          res += p
          start = s + p.length
        }
        val rem = input.substring(start)
        if (splitterStrsToKeep.contains(rem.toLowerCase()))
          res += rem
        res.toIndexedSeq
      } else
        throw new RuntimeException("Error!")
    }

    def canSplit(input: String): Boolean =
      condRegex.pattern.matcher(input).matches()
  }

  case class RegexTokenSplitter(_tokenSplitter: String) extends TTokenSplitter {
    private val splitter = _tokenSplitter.r
    override def split(input: String): IndexedSeq[String] = {
      splitter.split(input)
    }
  }

  case class TokenizerSettings(
    _lineSplitter: String,
    _tokenSplitter: String,
    tokenSplitterCond: List[TokenSplitterCond],
    _tokenTrimmer: TTrimmer
  ) {
    private[Tokenizers] val lineSplitter: Regex = _lineSplitter.r
    private[Tokenizers] val tokenSplitter: TTokenSplitter =
      RegexTokenSplitter(_tokenSplitter)
  }

  private[Tokenizers] class Tokenizer(private val _settings: TokenizerSettings)
    extends TTokenizer {
    override def run(input: String, dict:Dict): TknrResult = {
      val linesOfTokens = _settings.lineSplitter.split(input).map { l =>
        val line = l.trim
        val tokens = _settings.tokenSplitter.split(line).flatMap { t =>
          var processed = false;
          val it = _settings.tokenSplitterCond.iterator
          var res = EmptyTokenStrs
          while (!processed && it.hasNext) {
            val condSplitter = it.next()
            if (condSplitter.canSplit(t)) {
              res = condSplitter.split(t)
              processed = true
            }
          }
          if (!processed)
            res = IndexedSeq(t)
          res.filter(!_.isEmpty)
        }
        val resTokens = tokens.indices.map { idx =>
          val trimRes = _settings._tokenTrimmer.run(tokens(idx))
          new Token(
//            lineResult,
            idx,
            trimRes.result,
            trimRes.leftTrimmed,
            trimRes.rightTrimmed)
        }
        val lineOfTokens = new SeqOfTokens(line, tokens, resTokens)
        //lineResult._setTokens(resTokens)
        lineOfTokens
      }

      new TknrResult(input, dict, linesOfTokens)
    }
  }

  def load(settings: TokenizerSettings): TTokenizer = new Tokenizer(settings)
}
