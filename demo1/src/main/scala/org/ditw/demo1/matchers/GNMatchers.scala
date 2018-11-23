package org.ditw.demo1.matchers
import org.ditw.matcher.CompMatcherNs.lng
import org.ditw.matcher.CompMatchers.byTag
import org.ditw.matcher.TCompMatcher

object GNMatchers extends Serializable {

  import org.ditw.matcher.CompMatcherNs._
  def lngOfTagsSharedSfx(
    subMatcherTags:IndexedSeq[String],
    sharedSuffix:String,
    tag:String
  ):TCompMatcher = {
    val tagsWithSuffix = subMatcherTags.map(_ + sharedSuffix)
    lngOfTags(tagsWithSuffix, tag)
  }

}
