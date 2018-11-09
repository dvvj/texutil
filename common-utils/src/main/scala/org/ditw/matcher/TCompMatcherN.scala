package org.ditw.matcher

trait TCompMatcherN extends TCompMatcher {
  protected val subMatchers:Iterable[TCompMatcher]

  protected val refTags:Set[String] = {
    subMatchers.flatMap(sm => if (sm.tag.nonEmpty) sm.tag else sm.getRefTags())
      .toSet
  }
  override def getRefTags: Set[String] = refTags

}
