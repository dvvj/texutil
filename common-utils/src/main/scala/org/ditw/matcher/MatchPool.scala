package org.ditw.matcher
import org.ditw.common.{Dict}
import org.ditw.tknr.TknrResults.TknrResult
import org.ditw.tknr.Tokenizers.TTokenizer

class MatchPool(
  val input: TknrResult
) extends Serializable {
  import collection.mutable
  private var _map = mutable.Map[String, Set[TkMatch]]()

}

object MatchPool extends Serializable {
  def fromStr(str:String, tokenizer: TTokenizer, dict:Dict):MatchPool = {
    val tr = tokenizer.run(str, dict)
    new MatchPool(tr)
  }
}
