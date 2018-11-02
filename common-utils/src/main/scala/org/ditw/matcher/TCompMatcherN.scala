package org.ditw.matcher

trait TCompMatcherN extends TCompMatcher {
  protected val subMatchers:Iterable[TCompMatcher]

  protected val refTags:Set[String] = subMatchers.flatMap(_.tag).toSet
  override def getRefTags: Set[String] = refTags

}
