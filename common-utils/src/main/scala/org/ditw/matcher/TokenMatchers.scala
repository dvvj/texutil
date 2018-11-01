package org.ditw.matcher
import org.ditw.common.{Dict, PrefixTree, TkRange}
import org.ditw.tknr.Tokenizers.TTokenizer

object TokenMatchers extends Serializable {

  import collection.JavaConverters._
  import collection.mutable
  import java.lang.{Integer => JavaInt}
  private[matcher] class TmNGram(
    val tag:Option[String],
    private val ngrams:List[Array[JavaInt]]
  ) extends TTkMatcher {
    private val _pfxTree:PrefixTree[JavaInt] = {
      //val jl:JavaList[Array[Int]] = new JavaArrayList(ngrams.asJava)
      PrefixTree.createPrefixTree(ngrams.asJava)
    }

    override def run(matchPool: MatchPool)
      : Set[TkMatch] = {
      val res = mutable.Set[TkMatch]()

      matchPool.input.tknrResult.linesOfTokens.indices.foreach { lineIdx =>
        val sot = matchPool.input.tknrResult.linesOfTokens(lineIdx)
        sot.indices.foreach { idx =>
          res ++= runAt(matchPool, lineIdx, idx)
        }
      }

      res.toSet
    }

    def runAt(matchPool: MatchPool, lineIdx:Int, start:Int):Set[TkMatch] = {
      val encLine = matchPool.input.tknrResult.encoded(lineIdx)
      val lens = _pfxTree.allPrefixes(encLine, start).asScala
      val matches = lens.map { len =>
        val range = new TkRange(matchPool.input, lineIdx, start, start+len)
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
      .map(arr => arr.map(t => dict.enc(t.toLowerCase())))
      .toList
    new TmNGram(tag, encNGrams)
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

}
