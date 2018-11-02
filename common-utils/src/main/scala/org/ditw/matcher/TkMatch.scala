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

  override def hashCode(): Int = {
    (children.size << 16) + range.hashCode()
  }

  override def equals(obj: Any): Boolean = obj match {
    case m2:TkMatch => {
      range == m2.range && tags == m2.tags &&
        m2.children.size == children.size &&
        children.indices.forall(
          idx => m2.children(idx).range == children(idx).range
        ) // todo: check, not recursive now
    }
    case _ => false
  }
}

object TkMatch extends Serializable {

  val EmptyChildren:IndexedSeq[TkMatch] = IndexedSeq[TkMatch]()
  def fromChildren(children:IndexedSeq[TkMatch]):TkMatch = {
    if (children.isEmpty)
      throw new IllegalArgumentException("Empty Children")
    val lineIndices = children.map(_.range.lineIdx).distinct
    if (lineIndices.size > 1)
      throw new IllegalArgumentException(
        s"Children in multiple lines: ${lineIndices.mkString(",")}"
      )
    val firstChildRange = children.head.range
    val range = TkRange(
      firstChildRange.input,
      firstChildRange.lineIdx,
      firstChildRange.start,
      children.last.range.end
    )
    new TkMatch(range, children)
  }
}