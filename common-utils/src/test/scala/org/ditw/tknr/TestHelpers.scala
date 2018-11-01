package org.ditw.tknr
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.tknr.TknrResults.TknrResult
import org.ditw.tknr.Tokenizers.TokenizerSettings

object TestHelpers {

  private val EmptyStr = ""
  private [tknr] def noPfxSfx(content:String) = IndexedSeq(content)
  private [tknr] def noPfx(content:String, sfx:String) = IndexedSeq(content, EmptyStr, sfx)
  private [tknr] def commaSfx(content:String) = noPfx(content, ",")
  private [tknr] def noSfx(content:String, pfx:String) = IndexedSeq(content, pfx)

  private [tknr] def tokenFrom(
    idx:Int,
    tokenContent:IndexedSeq[String]
  ):Token = {
    val content = tokenContent(0)
    val pfx = if (tokenContent.length > 1) tokenContent(1) else EmptyStr
    val sfx = if (tokenContent.length > 2) tokenContent(2) else EmptyStr
    new Token(
      idx=idx,
      content=content,
      pfx=pfx,
      sfx=sfx
    )
  }

  private [tknr] def tokensFrom(
    contents:IndexedSeq[IndexedSeq[String]]
  ):IndexedSeq[Token] = {
    contents.indices.map(idx => tokenFrom(idx, contents(idx)))
  }

  private [tknr] def origTokenStrsFrom(
    contents:IndexedSeq[IndexedSeq[String]]
  ):(String, IndexedSeq[String]) = {
    val origTokenStrs = contents.indices.map { idx =>
      val lineContents = contents(idx)
      val content = lineContents(0)
      val pfx = if (lineContents.length > 1) lineContents(1) else EmptyStr
      val sfx = if (lineContents.length > 2) lineContents(2) else EmptyStr
      s"$pfx$content$sfx"
    }
    val orig = origTokenStrs.mkString(" ")
    orig -> origTokenStrs
  }

  private [tknr] def loTFrom(
    contents:IndexedSeq[IndexedSeq[String]]
  ): SeqOfTokens = {
    val tokens = tokensFrom(contents)
    val (orig, origTokenStrs) = origTokenStrsFrom(contents)
    new SeqOfTokens(orig, origTokenStrs, tokens)
  }

  private [tknr] def resultFrom(
    orig:String,
    dict:Dict,
    sots:IndexedSeq[SeqOfTokens]
  ): TknrResult = {
    new TknrResult(orig, dict, sots)
  }

  private def tokenEqual(t1:Token, t2:Token):Boolean = {
    t1.content == t2.content &&
      t1.pfx == t2.pfx && t1.sfx == t2.sfx &&
      t1.idx == t2.idx
  }

  private def sotEqualTest(sot1:SeqOfTokens, sot2:SeqOfTokens):Boolean = {
    sot1.orig == sot2.orig &&
      sot1.origTokenStrs == sot2.origTokenStrs &&
      sot1.tokens.size == sot2.tokens.size &&
      sot1.tokens.indices.forall(idx => tokenEqual(sot1.tokens(idx), sot2.tokens(idx)))
  }

  private [tknr] def resEqual(tr1:TknrResult, tr2:TknrResult):Boolean = {
    tr1.linesOfTokens.size == tr2.linesOfTokens.size &&
    tr1.linesOfTokens.indices.forall(idx => sotEqualTest(tr1.linesOfTokens(idx), tr2.linesOfTokens(idx)))
  }


  val dict:Dict = InputHelpers.loadDict(
    "[\\s,]".r.pattern.split("Cardiovascular Research, Vrije University, Amsterdam")
      .filter(!_.isEmpty)
  )

  //private val trimByCommaColon = Trimmers.byChars(Set(',', ';'))
  private val trimByPuncts = Trimmers.byChars(
    ",;:\"()*†".toSet
  )
  private val settings = TokenizerSettings(
    "\\n+",
    "[\\s]+",
    List(),
    trimByPuncts
  )

  val testTokenizer = Tokenizers.load(settings)

}
