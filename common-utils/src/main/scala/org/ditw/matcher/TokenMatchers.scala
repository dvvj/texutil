package org.ditw.matcher
import org.ditw.common.{Dict, PrefixTree, TkRange}
import org.ditw.tknr.Tokenizers.TTokenizer

object TokenMatchers extends Serializable {

  import collection.JavaConverters._
  import collection.mutable
  import java.lang.{Integer => JavaInt}
  private[matcher] class TmNGram(
    private val ngrams:List[Array[JavaInt]],
    val tag:Option[String]
  ) extends TTkMatcher {
    private val _pfxTree:PrefixTree[JavaInt] = {
      //val jl:JavaList[Array[Int]] = new JavaArrayList(ngrams.asJava)
      PrefixTree.createPrefixTree(ngrams.asJava)
    }

    def runAtLineFrom(matchPool: MatchPool, lineIdx:Int, start:Int):Set[TkMatch] = {
      val encLine = matchPool.input.tknrResult.encoded(lineIdx)
      val lens = _pfxTree.allPrefixes(encLine, start).asScala
      val matches = lens.map { len =>
        val range = TkRange(matchPool.input, lineIdx, start, start+len)
        val m = new TkMatch(range)
        if (tag.nonEmpty) {
          m.addTag(tag.get)
        }
        m
      }

      matches.toSet
    }
  }

  def ngram(
    ngrams:Set[Array[String]],
    dict: Dict,
    tag:Option[String] = None
  ):TTkMatcher = {
    val encNGrams = ngrams
      .map { arr =>
        arr.map { t =>
          val lower = t.toLowerCase()
          if (dict.contains(lower))
            dict.enc(t.toLowerCase())
          else
            throw new IllegalArgumentException(
              s"Token [$t] not found in Dictionary"
            )
        }
      }
      .toList
    new TmNGram(encNGrams, tag)
  }

  private val SpaceSep = "\\h+".r
  def splitBySpace2NonEmpty(str:String):Array[String] = {
    SpaceSep.split(str).filter(!_.isEmpty)
  }
  def ngramSplit(
    spaceSepNGrams:Set[String],
    dict: Dict,
    tag:Option[String] = None
  ):TTkMatcher = {
    ngram(spaceSepNGrams.map(splitBySpace2NonEmpty), dict, tag)
  }

  private val EmptyMatches = Set[TkMatch]()

  private [matcher] class TmRegex(
    private val _regex:String,
    val tag:Option[String]
  ) extends TTkMatcher {
    private val regex = _regex.r
    def runAtLineFrom(matchPool: MatchPool, lineIdx:Int, start:Int):Set[TkMatch] = {
      val token = matchPool.input.tknrResult.linesOfTokens(lineIdx)(start)
      if (regex.pattern.matcher(token.content).matches()) {
        val range = TkRange(matchPool.input, lineIdx, start, start+1)
        Set(new TkMatch(range))
      }
      else
        EmptyMatches
    }
  }

  def regex(
    _regex:String,
    tag:Option[String] = None
  ):TTkMatcher = {
    new TmRegex(_regex, tag)
  }

}
