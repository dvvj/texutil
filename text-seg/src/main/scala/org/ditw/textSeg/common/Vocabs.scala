package org.ditw.textSeg.common
import org.ditw.common.{Dict, InputHelpers, ResourceHelpers}
import org.ditw.textSeg.common.CatSegMatchers.Category

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

  import Category._

  private val _catToResPath = Map(
    Category.Corp -> "cat1",
    Category.Univ -> "cat2"
  )

  private[textSeg] def loadStopWords(cat: Category):Set[String] = {
    val resPath = _catToResPath(cat)
    ResourceHelpers.loadStrs(s"/$resPath/stopwords.txt").toSet
  }

  private[textSeg] def loadGazWords(cat: Category):Set[String] = {
    val resPath = _catToResPath(cat)
    ResourceHelpers.loadStrs(s"/$resPath/gaz.txt").toSet
  }

  private[textSeg] val _UnivStopWords = loadStopWords(Univ)
  private[textSeg] val _UnivGazWords = loadGazWords(Univ)

  private[textSeg] val __UnivSegStopWordsCommon = Set(
    "school",
    "college",
    "sw medical center",
    "southwestern medical center",
    "medical center",
    "medical campus",
    "biomedical campus",
    "medical school",
    "medical college",
    "graduate school",
    "superior school",
    "department",
    "clinical centre",
    "and",
    "&",
    "faculty",
    "-"
  )

  private[textSeg] val _UnivSegStopWordsLeftExtra = Set(
    "of", "at"
  )
  private[textSeg] val _UnivSegStopWordsLeft = __UnivSegStopWordsCommon ++ _UnivSegStopWordsLeftExtra
  private[textSeg] val _UnivSegStopWordsRight = __UnivSegStopWordsCommon

  private val allVocabs = Seq(
    _CorpWords,
    _UnivWords,
    _UnivGazWords,
    _UnivStopWords,
    __UnivSegStopWordsCommon,
    _UnivSegStopWordsLeftExtra
  )

  import InputHelpers._
  private [textSeg] val _Dict:Dict = InputHelpers.loadDict(
    allVocabs.map(splitVocabToSet)
  )
}
