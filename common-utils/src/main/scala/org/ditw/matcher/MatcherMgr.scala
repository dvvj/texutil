package org.ditw.matcher
import scala.collection.mutable.ListBuffer

class MatcherMgr(
  val tms:List[TTkMatcher],
  val cms:List[TCompMatcher],
  val blockTagMap:Map[String, Set[String]]
) {
  private def checkTags:Unit = {
    val allTags = tms.map(_.tag) ++ cms.map(_.tag)
    if (!allTags.forall(_.nonEmpty))
      throw new IllegalArgumentException(
        "Empty Tag Matcher found!"
      )
    val dupTags:Iterable[String] = allTags.flatten.groupBy(t => t)
      .mapValues(_.size)
      .filter(_._2 > 1)
      .keys
      .toList.sorted
    if (dupTags.nonEmpty)
      throw new IllegalArgumentException(
        s"Duplicate tag(s) found: [${dupTags.mkString(",")}]"
      )
  }
  checkTags

  private val tag2CmMap:Map[String, TCompMatcher] = {
    cms.map(m => m.tag.get -> m).toMap
  }
  private[matcher] val cmDepMap:Map[String, Set[String]] = {
    val depPairs = cms.flatMap(cm => cm.getRefTags().map(_ -> cm.tag.get))
    depPairs.groupBy(_._1)
      .mapValues(_.map(_._2).toSet)
  }
  import MatcherMgr._
  def run(matchPool: MatchPool):Unit = {
    tms.foreach { tm =>
      val matches = tm.run(matchPool)
      if (matches.nonEmpty)
        matchPool.add(tm.tag.get, matches)
    }

    import collection.mutable

    var remMatchers = mutable.Set[TCompMatcher]()
    remMatchers ++= cms

    val matchCache = mutable.Map[TCompMatcher, Set[TkMatch]]()
    while (remMatchers.nonEmpty) {
      val headMatcher = remMatchers.head
      remMatchers.remove(headMatcher)

      val currMatches = headMatcher.run(matchPool)
      val hasUpdates = currMatches != matchCache.getOrElse(headMatcher, EmptyMatches)
      if (hasUpdates) {
        matchPool.add(headMatcher.tag.get, currMatches)
        val affectedCmTags = cmDepMap.getOrElse(headMatcher.tag.get, EmptyDepCmTags)
        remMatchers ++= affectedCmTags.map(tag2CmMap)
        matchCache.put(headMatcher, currMatches)
      }
    }

    postproc(matchPool)
  }

  private def postproc(matchPool: MatchPool):Unit = {
    blockTagMap.foreach { kv =>
      val (blockerTag, blockeeTags) = kv
      val blockerRanges = matchPool.get(blockerTag).map(_.range)

      val blockees = matchPool.get(blockeeTags)

      val toRemoveList = ListBuffer[TkMatch]()
      blockees.foreach { blockee =>
        if (blockerRanges.exists(_.overlap(blockee.range))) {
          toRemoveList += blockee
        }
      }
      val toRemoveMap = toRemoveList.flatMap { m =>
          m.getTags.map(_ -> m)
        }.groupBy(_._1)
        .mapValues(_.map(_._2))
      toRemoveMap.foreach { kv =>
        val (tag, matches) = kv
        val existing = matchPool.get(tag)
        matchPool.update(
          tag,
          existing -- matches
        )
      }
    }
  }
}

object MatcherMgr {
  private val EmptyMatches = Set[TkMatch]()
  private val EmptyDepCmTags = Set[String]()
}