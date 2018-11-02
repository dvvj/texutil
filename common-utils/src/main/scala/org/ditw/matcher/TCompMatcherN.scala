package org.ditw.matcher

trait TCompMatcherN {
  self: TCompMatcher =>

  protected val subMatchers:Set[TCompMatcher]

  protected val refTags:Set[String] = subMatchers.flatMap(_.tag)
  override def getRefTags(): Set[String] = refTags

}
