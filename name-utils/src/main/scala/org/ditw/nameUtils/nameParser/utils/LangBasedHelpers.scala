package org.ditw.nameUtils.nameParser.utils

import org.ditw.nameUtils.nameParser.ParserHelpers
import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
import org.ditw.nameUtils.nameParser.utils.nameCompatibility.NameCompatibilityUtils
import org.ditw.nameUtils.nameParser.utils.nameCompatibility.NameCompatibilityUtils.{SpaceSplitterPtn, splitSignature}
import org.ditw.nameUtils.nameParser.utils.nameCompatibility.NameCompatibilityUtils
import org.ditw.nameUtils.nameparser.ParsedPerson

/**
  * Created by dev on 2017-10-23.
  */
object LangBasedHelpers extends Serializable {
  // ensure no infinity
  private[nameParser] val ProbOne = 1.0 + 1e-10
  private[nameParser] val DoubleBoosted = 2.0
  private[nameParser] val TenXBoosted = 10.0
  private[nameParser] val JapaneseFirstNameMatchBoosted = 8.0
  private[nameParser] val Unboosted = 1.0
  private[nameParser] val UnderBoosted = 0.1
  private[nameParser] val Boosted_Invalidated = 0.0
  private[nameParser] def nameSimilarityBoost(n1: String, n2: String, lengthThreshold: Int) = {
    if (n1 != null && n2 != null &&
      n1.length > lengthThreshold &&
      n2.length > lengthThreshold &&
      n1.equalsIgnoreCase(n2)
    ) TenXBoosted
    else Unboosted
  }

  def clusterSimilarityBoostOrig(origSimilarity: Double, clusterName1: String, clusterName2: String, sig1: String, sig2: String) = {
    val nameBoost = nameSimilarityBoost(clusterName1, clusterName2, 4)
    val sigBoost = nameSimilarityBoost(sig1, sig2, 1)
    val ms = Math.log(origSimilarity / (1.0 - origSimilarity) * nameBoost) * sigBoost
    ms
  }

  def clusterSimilarityBoostChinese(origSimilarity: Double, clusterName1: String, clusterName2: String, sig1: String, sig2: String)
  :Double = {
    if (!sig1.equalsIgnoreCase(sig2)) Boosted_Invalidated
    else {
      val nameBoost =
        if (clusterName1.equalsIgnoreCase(clusterName2)) {
          val chars = SpaceSplitterPtn.split(clusterName1)
          if (chars.length > 1) DoubleBoosted
          else Unboosted
        }
        else UnderBoosted
      val ms = Math.log(origSimilarity / (ProbOne - origSimilarity) * nameBoost)
      ms
    }
  }

  def clusterSimilarityBoostJapanese(origSimilarity: Double, clusterName1: String, clusterName2: String, sig1: String, sig2: String)
  :Double = {
    val shortSig = math.min(sig1.length, sig2.length)
    val sig1s = sig1.substring(0, shortSig)
    val sig2s = sig2.substring(0, shortSig)
    if (!sig1s.equalsIgnoreCase(sig2s)) Boosted_Invalidated
    else {
      val nameBoost =
        if (clusterName1.equalsIgnoreCase(clusterName2)) {
          if (clusterName1.length > 1) JapaneseFirstNameMatchBoosted
          else Unboosted
        }
        else UnderBoosted
      val ms = Math.log(origSimilarity / (ProbOne - origSimilarity) * nameBoost)
      ms
    }
  }

  type BoostFunc = (Double, String, String, String, String) => Double
  type CoAuthorGen = (ParsedPerson) => String

  private def coauthorGenOrig(pp:ParsedPerson):String = NameCompatibilityUtils.genNamespaceFromJ(pp) //NameCompatibilityUtils.cleanJ(pp.lastName.toLowerCase)
  private def coauthorGenChinese(pp:ParsedPerson):String = NameCompatibilityUtils.genNamespaceFromJ(pp)
  private def coauthorGenJapanese(pp:ParsedPerson):String = coauthorGenOrig(pp)

  class LangClusterHelpers(
                          val matchingScoreBoost:BoostFunc,
                          val coauthorGen:CoAuthorGen
                          )

  private val origClusterHelpers = new LangClusterHelpers(
    clusterSimilarityBoostOrig, coauthorGenOrig
  )

  private val Lang2ClusterSimilarityBoostFunc = Map[LanguageEnum, LangClusterHelpers](
    Chinese ->
      new LangClusterHelpers(clusterSimilarityBoostChinese, coauthorGenChinese),
    Japanese ->
      new LangClusterHelpers(clusterSimilarityBoostJapanese, coauthorGenJapanese)
  )

  private def getClusterHelpers(langCode:Option[String]):LangClusterHelpers = {
    if (langCode.nonEmpty) {
      val lang = NameLanguages.LangCodeReverseMap.get(langCode.get)
      if (lang.nonEmpty && Lang2ClusterSimilarityBoostFunc.contains(lang.get)) Lang2ClusterSimilarityBoostFunc(lang.get)
      else origClusterHelpers
    }
    else origClusterHelpers
  }

  def clusterSimilarityBoost(origSimilarity: Double,
                             clusterName1: String, clusterName2: String,
                             sig1: String, sig2: String):Double = {
    val (langCode1, s1) = splitSignature(sig1)
    val (langCode2, s2) = splitSignature(sig2)

    if (langCode1 != langCode2) 0.0
    else {
      val helpers:LangClusterHelpers = getClusterHelpers(langCode1)

      helpers.matchingScoreBoost(
        origSimilarity,
        clusterName1, clusterName2,
        s1, s2
      )
    }
  }

  def genCoAuthor(pp:ParsedPerson):String = {
    val (langCode, s) = splitSignature(pp.signature)
    val helpers:LangClusterHelpers = getClusterHelpers(langCode)
    helpers.coauthorGen(pp)
  }

}
