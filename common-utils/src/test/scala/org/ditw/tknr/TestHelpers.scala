package org.ditw.tknr

object TestHelpers {

  private val EmptyStr = ""
  private [tknr] def noPfxSfx(content:String) = IndexedSeq(content)
  private [tknr] def noPfx(content:String, sfx:String) = IndexedSeq(content, EmptyStr, sfx)
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
}
