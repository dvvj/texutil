package org.ditw.nameUtils.nameParser.utils

/**
  * Created by dev on 2017-09-07.
  */
object SuffixData extends Serializable {
  private[nameParser] val SuffixLiterals = Set(
    "jr.",
    "sr.",
    "jr",
    "sr",
    "ii",
    "iii",
    "iv",
    "v",
    "vi",
    "vii",
    "viii",
    "ix",
    "x",
    "xi",
    "xii",
    "xiii",
    "xiv",
    "xv",
    "1st",
    "2nd",
    "3rd",
    "4th",
    "5th"
  )

  private[nameParser] val UnambiguousSuffixLiterals = Set(
    "jr.",
    "sr.",
    "jr",
    "sr",
    "junior",
    "senior",
    "iii"
  )

}
