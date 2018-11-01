package org.ditw.matcher
import org.ditw.common.{Dict, Input}
import org.ditw.tknr.Tokenizers.TTokenizer

class MatchPool(
  val input:Input
) extends Serializable {
  import collection.mutable
  private var _map = mutable.Map[String, Set[TkMatch]]()

}

object MatchPool extends Serializable {
  def fromStr(str:String, tokenizer: TTokenizer, dict:Dict):MatchPool = {
    val tr = tokenizer.run(str, dict)
    val input = new Input(tr)
    new MatchPool(input)
  }
}
