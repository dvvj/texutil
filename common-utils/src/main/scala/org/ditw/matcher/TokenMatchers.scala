package org.ditw.matcher
import org.ditw.common.{Dict, PrefixTree, TkRange}
import org.ditw.tknr.Tokenizers.TTokenizer

object TokenMatchers extends Serializable {

  import collection.JavaConverters._
  import collection.mutable
  import org.ditw.common.TypeCommon._

  // ---------- N-Gram
  private[matcher] class TmNGram(
    private val ngrams:List[Array[DictEntryKey]],
    val tag:Option[String]
  ) extends TTkMatcher {
    private val _pfxTree:PrefixTree[DictEntryKey] = {
      //val jl:JavaList[Array[Int]] = new JavaArrayList(ngrams.asJava)
      PrefixTree.createPrefixTree(ngrams.asJava)
    }

    def runAtLineFrom(matchPool: MatchPool, lineIdx:Int, start:Int):Set[TkMatch] = {
      val encLine = matchPool.input.encoded(lineIdx)
      val lens = _pfxTree.allPrefixes(encLine, start).asScala
      val matches = lens.map { len =>
        val range = TkRange(matchPool.input, lineIdx, start, start+len)
        val m = TkMatch.noChild(range, tag)
        m
      }

      matches.toSet
    }
  }

  def ngramT(
    ngrams:Set[Array[String]],
    dict: Dict,
    tag:String
  ):TTkMatcher = {
    ngram(ngrams, dict, Option(tag))
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

  // ---------- Regex
  private [matcher] class TmRegex(
    private val _regex:String,
    val tag:Option[String]
  ) extends TTkMatcher {
    private val regex = _regex.r
    def runAtLineFrom(matchPool: MatchPool, lineIdx:Int, start:Int):Set[TkMatch] = {
      val token = matchPool.input.linesOfTokens(lineIdx)(start)
      if (regex.pattern.matcher(token.content).matches()) {
        val range = TkRange(matchPool.input, lineIdx, start, start+1)
        Set(TkMatch.noChild(range, tag))
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

  private val _EmailPtn = "(?i)(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
  def emailMatcher(tag:String):TTkMatcher = regex(_EmailPtn, Option(tag))

  // ---------- Prefixed-By
  private [matcher] trait SuffixedOrPrefixedBy extends TTkMatcher {
    protected val _tkMatcher: TTkMatcher
    protected val _preSuffixSet:Set[String]
    protected val _isPrefix:Boolean
    override def runAtLineFrom(
      matchPool: MatchPool,
      lineIdx: Int,
      start: Int): Set[TkMatch] = {
      val matches = _tkMatcher.runAtLineFrom(matchPool, lineIdx, start)
      val lot = matchPool.input.linesOfTokens(lineIdx)
      matches.filter { m =>
        if (_isPrefix) {
          val pfx = lot.tokens(m.range.start).pfx
          _preSuffixSet.contains(pfx)
        }
        else {
          val sfx = lot.tokens(m.range.end-1).sfx
          _preSuffixSet.contains(sfx)
        }
      }
    }
  }

  private def _prefixSuffixedBy(
                                 tkMatcher: TTkMatcher,
                                 isPrefix: Boolean,
                                 preSuffixSet:Set[String],
                                 t:Option[String] = None
                               ):TTkMatcher = {
    new SuffixedOrPrefixedBy {
      override protected val _isPrefix: Boolean = isPrefix
      override protected val _preSuffixSet: Set[String] = preSuffixSet
      override protected val _tkMatcher: TTkMatcher = tkMatcher
      override val tag: Option[String] = t
    }
  }

  def prefixedBy(
                  tkMatcher: TTkMatcher,
                  prefixSet:Set[String],
                  t:Option[String] = None
                ):TTkMatcher = {
    _prefixSuffixedBy(tkMatcher, true, prefixSet, t)
  }

  def suffixedBy(
                  tkMatcher: TTkMatcher,
                  suffixSet:Set[String],
                  t:Option[String] = None
                ):TTkMatcher = {
    _prefixSuffixedBy(tkMatcher, false, suffixSet, t)
  }

}
