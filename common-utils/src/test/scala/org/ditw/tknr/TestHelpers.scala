package org.ditw.tknr
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.tknr.Tokenizers.TokenizerSettings

object TestHelpers {

  import TknrHelpers._

  val dict:Dict = InputHelpers.loadDict(
    "[\\s,]".r.pattern.split("Cardiovascular Research, Vrije University, Amsterdam")
      .filter(!_.isEmpty),
    "0123456789".map(_.toString)
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
