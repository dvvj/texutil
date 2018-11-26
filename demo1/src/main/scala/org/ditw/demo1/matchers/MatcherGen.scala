package org.ditw.demo1.matchers
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.{MatcherMgr, TCompMatcher, TTkMatcher, TokenMatchers}

import scala.collection.mutable.ListBuffer

object MatcherGen extends Serializable {
  import InputHelpers._

  def loadDict(adm0s: Iterable[TGNMap]):Dict = {
    val keys = adm0s.flatMap(_.admNameMap.values.flatMap(_.keySet))
    val adm0Names = adm0s.flatMap(_.self.get.queryNames)
    val words = splitVocabEntries(keys.toSet ++ adm0Names)
      .map(_.toIndexedSeq)
    InputHelpers.loadDict(words)
  }

  def gen(adm0s: Iterable[TGNMap], dict:Dict): MatcherMgr = {
    val tmlst = ListBuffer[TTkMatcher]()
    val cmlst = ListBuffer[TCompMatcher]()

    val adm0Name2Tag = adm0s.flatMap { adm0 =>
      val ent = adm0.self.get
      ent.queryNames.map(_ -> TagHelper.countryTag(adm0.countryCode))
    }.toMap
    val tmAdm0 = TokenMatchers.ngramExtraTag(
      adm0Name2Tag,
      dict,
      TagHelper.TmAdm0
    )
    tmlst += tmAdm0

    adm0s.foreach { adm0 =>
      val (tms, cms) = Adm0Gen.genMatchers(adm0, dict)
      tmlst ++= tms
      cmlst ++= cms
    }

    new MatcherMgr(
      tmlst.toList,
      cmlst.toList,
      List()
    )

  }
}
