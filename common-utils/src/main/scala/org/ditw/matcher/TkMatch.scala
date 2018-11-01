package org.ditw.matcher
import org.ditw.common.TkRange

class TkMatch(
  val range: TkRange,
  val children: IndexedSeq[TkMatch] = TkMatch.EmptyChildren
) extends Serializable {
  import collection.mutable
  private val tags = mutable.Set[String]()

  def addTag(ts:String):Unit = tags += ts
  def addTags(ts:Iterable[String]):Unit = tags ++= ts
}

object TkMatch extends Serializable {
  val EmptyChildren:IndexedSeq[TkMatch] = IndexedSeq[TkMatch]()
}