package org.ditw.nameUtils.nameParser.utils.trace
import org.ditw.nameUtils.nameparser.NamesProcessed

/**
  * Created by dev on 2017-09-06.
  */
object TraceHelpers extends Serializable {
  private def diffParsedNameField(heading:String, v1:String, v2:String, ignoreCase:Boolean = true):String = {

    if (v1 == null && v2 == null) ""
    else if (v1 == null || v2 == null) s"[[$heading:$v1/$v2]]"
    else if (v1.isEmpty && v2.isEmpty) ""
    else {
      val eq = if (ignoreCase) v1.equalsIgnoreCase(v2) else v1.equals(v2)
      if (!eq) s"[[$heading:$v1/$v2]]"
      else s"=$heading:$v1="
    }
  }

  def diffParsedName(np1: NamesProcessed, np2: NamesProcessed):String = {
    if (np1 == null || np2 == null) s"At least one null result: $np1, $np2"
    else {
      val diffStrs = List(
        s"${diffParsedNameField("SRC", np1.source, np2.source)}",
        s"${diffParsedNameField("LN", np1.lastName, np2.lastName)}",
        s"${diffParsedNameField("FN", np1.firstName, np2.firstName)}",
        s"${diffParsedNameField("NT", np1.nobeltitles, np2.nobeltitles)}",
        s"${diffParsedNameField("SG", np1.signature, np2.signature)}"
      )
      diffStrs.mkString
    }
  }


}
