package org.ditw.textSeg.common
import org.ditw.common.{Dict, InputHelpers}

object Vocabs extends Serializable {

  private[textSeg] val _SegSfxs = Set(
    ",", ";"
  )

  private[textSeg] val _CorpWords = Set(
    "llc", "inc", "ltd",
    "pte ltd", "pty ltd", "pvt ltd", "co ltd",
    "ggmbh", "gmbh", "kgaa",
    "pharmaceuticals", "plc", "corp",
    "srl",
    "s/a"
  )

  private[textSeg] val _UnivWords = Set(
    "university", "univ", "hochschule", "università",
    "universidade",
    "universitário",
    "universitario",
    "université",
    "universitá",
    "universitaria"
  )

  private[textSeg] val _UnivStopWords = Set(
    "and university center"
  )

  private[textSeg] val __UnivSegStopWordsCommon = Set(
    "school",
    "medical school",
    "medical college",
    "graduate school",
    "superior school",
    "department",
    "and",
    "faculty"
  )

  private[textSeg] val _UnivSegStopWordsLeftExtra = Set(
    "of", "at"
  )
  private[textSeg] val _UnivSegStopWordsLeft = __UnivSegStopWordsCommon ++ _UnivSegStopWordsLeftExtra
  private[textSeg] val _UnivSegStopWordsRight = __UnivSegStopWordsCommon

  private val allVocabs = Seq(
    _CorpWords,
    _UnivWords,
    __UnivSegStopWordsCommon,
    _UnivSegStopWordsLeftExtra
  )

  import InputHelpers._
  private [textSeg] val _Dict:Dict = InputHelpers.loadDict(
    allVocabs.map(splitVocabToSet)
  )
}
