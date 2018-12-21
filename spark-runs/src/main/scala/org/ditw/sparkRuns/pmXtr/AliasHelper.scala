package org.ditw.sparkRuns.pmXtr
import scala.util.matching.Regex

object AliasHelper extends Serializable {

  trait TMatchRepl extends Serializable {
    def matchRepl(in:String):String
  }

  private val spaceSplitter = "\\s+".r
  private class MR1(val centerStr:String, val replStr:String, preTokens:Range, postTokens:Range) extends TMatchRepl {
    override def matchRepl(in:String):String = {
      val normed = spaceSplitter.split(in.toLowerCase()).filter(_.nonEmpty)
      var rem:String = ""
      val start = preTokens.find { i =>
        rem = normed.slice(i, normed.length).mkString(" ")
        rem.startsWith(centerStr)
      }
      if (start.nonEmpty) {
        val post = rem.substring(centerStr.length).trim
        val remTokens = spaceSplitter.split(post).filter(_.nonEmpty).length
        if (postTokens.contains(remTokens)) {
          normed.mkString(" ").replace(centerStr, replStr)
        }
        else in
      }
      else in
    }
  }

  private[sparkRuns] val MR_xUnivSchoolOfY:TMatchRepl =
    new MR1("university school of", "school of", 1 to 2, 1 to 1)

}
