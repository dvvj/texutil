package org.ditw.demo1.matchers
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.common.TypeCommon.DictEntryKey
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.TokenMatchers._
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
      admTmTag(adm0.countryCode),
      addExtraAdmTag
    )

    val adm1NameIds = adm0.subAdms.map(adm0.admMap)
      .flatMap(c => c.self.map(cs => cs.queryNames.map(_ -> cs.gnid)).getOrElse(Set()))
    val adm1Names = adm1NameIds.map(_._1)
    val adm1Ids = adm1NameIds.map(_._2)

    // remove adm1 name->id pairs
    val adm2PlusMap = adm0.admNameMap.map { p =>
      val minusAdm1 = p._2.filter(p => !adm1Names.contains(p._1))
      val filtered = p._2.filter(p => adm1Names.contains(p._1))
        .flatMap { p =>
          val rmd = p._2.filter(!adm1Ids.contains(_))
          if (rmd.nonEmpty)
            Option(p._1 -> rmd)
          else None
        }
      p._1 -> (minusAdm1 ++ filtered)
    }

    val name2Adm1SubEnts = adm2PlusMap.map { p =>
      val adm1SubEntTag = adm1SubEntTmTag(p._1)
      ngramGNIds(p._2, dict, adm1SubEntTag)
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
    val ct = countryTag(adm0.countryCode)
    val cityMap = adm2PlusMap.flatMap(_._2.toIndexedSeq)
      .groupBy(_._1)
      .mapValues(_.flatMap(_._2).toIndexedSeq.distinct)
    val cityTag = countryOfCountryTag(adm0.countryCode)
    val tmCity = ngramGNIds(
      cityMap,
      dict,
      countryOfCountryTag(adm0.countryCode)
    )
    val cmCityCountry = CompMatcherNs.lngOfTags(
      IndexedSeq(cityTag, ct),
      cityCountryCmTag(adm0.countryCode)
    )

    (tmCity :: tms) -> (cmCityCountry :: cms)
  }


  private val addExtraAdmTag:TmMatchPProc[String] = (m, tag) => {
    val t = admDynTag(tag)
    m.addTag(t)
    m
  }


  private val GNIdTagTmpl = "GNId_%d"
  private val addGNIdTags:TmMatchPProc[IndexedSeq[Long]] = (m, gnids) => {
    val tags = gnids.map(GNIdTagTmpl.format(_))
    m.addTags(tags, false)
    m
  }

  private def ngramGNIds(
    ngrams:Map[String, IndexedSeq[Long]],
    dict: Dict,
    tag:String,
    pproc:TmMatchPProc[IndexedSeq[Long]] = addGNIdTags
    ):TTkMatcher = {
    ngramT(ngrams, dict, tag, pproc)
    //    val encm:Map[Array[DictEntryKey], String] =
    //      ngrams.map(p => InputHelpers.splitVocabEntry(p._1).map(checkValidAndEnc(dict, _)) -> p._2)
    //    new TmNGramD(encm, pproc, Option(tag))
  }
//  def genCms(adm0:TGNMap, dict:Dict):Iterable[TCompMatcher] = {
//
//
//  }
  //def genCms()
}
