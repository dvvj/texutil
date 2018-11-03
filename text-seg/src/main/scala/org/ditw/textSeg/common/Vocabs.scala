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
    "pharmaceuticals", "plc", "corp"
  )

  private val allVocabs = Seq(
    _CorpWords
  )

  import InputHelpers._
  private [textSeg] val _Dict:Dict = InputHelpers.loadDict(
    allVocabs.map(splitVocabToSet)
  )

}
