package org.ditw.demo1.matchers
import org.ditw.common.Dict
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.TokenMatchers.TmMatchPProc
import org.ditw.matcher.{CompMatcherNs, TCompMatcher, TTkMatcher, TokenMatchers}

object Adm0Gen extends Serializable {
  import TagHelper._

  private val EmptyPairs = Iterable[(String, String)]()
  def genMatchers(adm0:TGNMap, dict:Dict)
    :(List[TTkMatcher], List[TCompMatcher]) = {
    val name2Admc = adm0.admMap.flatMap { p =>
      val admCode = p._1
      if (p._2.self.nonEmpty) {
        p._2.self.get.queryNames.map(_ -> admCode)
      }
      else EmptyPairs
    }

    val tmAdm1s = TokenMatchers.ngramExtraTag(
      name2Admc,
      dict,
      admTmTag(adm0.countryCode.toString),
      addExtraAdmTag
    )

    val name2Adm1SubEnts = adm0.admNameMap.map { p =>
      val adm1SubEntTag = adm1SubEntTmTag(p._1)
      TokenMatchers.ngramGNIds(p._2, dict, adm1SubEntTag)
    }

    val tms = tmAdm1s :: name2Adm1SubEnts.toList

    val cms = adm0.admNameMap.keySet.map { admc =>
      val adm1Tag = admDynTag(admc)
      val subEntTag = adm1SubEntTmTag(admc)
      CompMatcherNs.lngOfTags(
        IndexedSeq(subEntTag, adm1Tag),
        adm1AndSubCmTag(admc)
      )
    }.toList
    tms -> cms
  }

  private val addExtraAdmTag:TmMatchPProc[String] = (m, tag) => {
    val t = admDynTag(tag)
    m.addTag(t)
    m
  }
//  def genCms(adm0:TGNMap, dict:Dict):Iterable[TCompMatcher] = {
//
//
//  }
  //def genCms()
}
