package org.ditw.matcher
import org.ditw.common.{Dict}
import org.ditw.tknr.TknrResult
import org.ditw.tknr.Tokenizers.TTokenizer

class MatchPool(
  val input: TknrResult
) extends Serializable {
  import MatchPool._
  import collection.mutable
  private var _map = mutable.Map[String, Set[TkMatch]]()
  def get(tag:String):Set[TkMatch] = _map.getOrElse(tag, EmptyMatches)
  def get(tags:Set[String]):Set[TkMatch] =
    tags.flatMap(_map.getOrElse(_, EmptyMatches))
  def add(tag:String, matches:Set[TkMatch]):Unit = {
    val existing = get(tag)
    _map.put(tag, existing ++ matches)
  }
}

object MatchPool extends Serializable {
  private val EmptyMatches = Set[TkMatch]()
  def fromStr(str:String, tokenizer: TTokenizer, dict:Dict):MatchPool = {
    val tr = tokenizer.run(str, dict)
    new MatchPool(tr)
  }
}
