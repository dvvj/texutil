package org.ditw.matcher

trait TTkMatcher extends Serializable {
  val tag:Option[String]
  def run(matchPool: MatchPool):Set[TkMatch]
}
