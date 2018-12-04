package org.ditw.demo1.matchers
import org.ditw.common.{Dict, InputHelpers, ResourceHelpers}
import org.ditw.demo1.extracts.Xtrs
import org.ditw.demo1.gndata.{GNSvc, TGNMap}
import org.ditw.extract.{TXtr, XtrMgr}
import org.ditw.matcher._

import scala.collection.mutable.ListBuffer

object MatcherGen extends Serializable {
  import InputHelpers._

  private[demo1] val _gnBlackList = ResourceHelpers.loadStrs("/gn_blacklist.txt").toSet

  private val extraVocabs = List(
    _gnBlackList
  )

  def loadDict(
    gnsvc: GNSvc
  ):Dict = {
    val adm0s = gnsvc._cntryMap.values
    val keys = adm0s.flatMap(_.admNameMap.values.flatMap(_.keySet))
    val adm0Names = adm0s.flatMap(_.self.get.queryNames)
    val words = splitVocabEntries(keys.toSet ++ adm0Names ++ extraVocabs.flatten)
      .map(_.toIndexedSeq)
    InputHelpers.loadDict(words)
  }

  import TagHelper._
  def gen(gnsvc:GNSvc, dict:Dict): (MatcherMgr, XtrMgr[Long]) = {
    val tmlst = ListBuffer[TTkMatcher]()
    val cmlst = ListBuffer[TCompMatcher]()
    val pproclst = ListBuffer[TPostProc]()

    val tmGNBlackList = TokenMatchers.ngramT(
      splitVocabEntries(_gnBlackList),
      dict,
      TmGNBlacklist
    )
    tmlst += tmGNBlackList

    val adm0s: Iterable[TGNMap] = gnsvc._cntryMap.values
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

    val xtrlst = ListBuffer[TXtr[Long]]()
    import collection.mutable
    val tmBlTargets = mutable.Set[String]()
    adm0s.foreach { adm0 =>
      val (tms, cms, xtrs, pproc) = Adm0Gen.genMatcherExtractors(gnsvc, adm0, dict)
      tmBlTargets ++= tms.flatMap(_.tag) // black list blocks all tms
      tmlst ++= tms
      cmlst ++= cms
      xtrlst ++= xtrs
      pproclst += pproc
    }

    val tmPProc = MatcherMgr.postProcBlocker(
      Map(
        TmGNBlacklist -> tmBlTargets.toSet
      )
    )

    //xtrlst += Xtrs.entXtr4TagPfx(_CityStatePfx)
    //xtrlst += Xtrs.entXtr4TagPfx(_CityCountryPfx)
    xtrlst += Xtrs.entXtrFirst4TagPfx(gnsvc, _CityAdmSeqPfx)

    new MatcherMgr(
      tmlst.toList,
      List(tmPProc),
      cmlst.toList,
      pproclst.toList
    ) -> XtrMgr.create(xtrlst.toList)

  }
}
