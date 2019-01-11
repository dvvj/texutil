package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameparser.ParsedPerson
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-10-23.
  */
class LangBasedHelpersTest extends TestNGSuite with Matchers with TableDrivenPropertyChecks {
  import org.ditw.nameUtils.nameParser.utils.LangBasedHelpers._
  val clusterBoostTestData = Table(
    ("origProb", "clusterName1", "clusterName2", "sig1", "sig2", "expB1", "expB2"),
    (0.1, "Wei", "Wei", "ZHO:W", "ZHO:W", Unboosted, Unboosted),
    (0.1, "Wei", "W", "ZHO:W", "ZHO:W", UnderBoosted, Unboosted),
    (0.1, "Wei hua", "Wei hua", "ZHO:W", "ZHO:W", DoubleBoosted, Unboosted),
    (0.1, "wei hua", "Wei hua", "ZHO:W", "ZHO:W", DoubleBoosted, Unboosted),
    (0.1, "wei hua", "Wei h", "ZHO:W", "ZHO:W", UnderBoosted, Unboosted),
    (0.4, "Wei", "Wei", "ZHO:W", "ZHO:W", Unboosted, Unboosted),
    (0.4, "Wei", "W", "ZHO:W", "ZHO:W", UnderBoosted, Unboosted),
    (0.4, "Wei hua", "Wei hua", "ZHO:W", "ZHO:W", DoubleBoosted, Unboosted),
    (0.4, "wei hua", "Wei hua", "ZHO:W", "ZHO:W", DoubleBoosted, Unboosted),
    (0.4, "wei hua", "Wei h", "ZHO:W", "ZHO:W", UnderBoosted, Unboosted),
    (0.7, "Wei", "Wei", "ZHO:W", "ZHO:W", Unboosted, Unboosted),
    (0.7, "Wei", "W", "ZHO:W", "ZHO:W", UnderBoosted, Unboosted),
    (0.7, "Wei hua", "Wei hua", "ZHO:W", "ZHO:W", DoubleBoosted, Unboosted),
    (0.7, "wei hua", "Wei hua", "ZHO:W", "ZHO:W", DoubleBoosted, Unboosted),
    (0.7, "wei hua", "Wei h", "ZHO:W", "ZHO:W", UnderBoosted, Unboosted)
  )
  val doubleTolerance = 1e-8

  @Test(groups = Array("UnitTest"))
  def clusterNameBoostTests() {
    forAll (clusterBoostTestData) { (origProb, cln1, cln2, sig1, sig2, expB1, expB2) =>
      val boost = clusterSimilarityBoost(origProb, cln1, cln2, sig1, sig2)
      val origBoost = clusterSimilarityBoostOrig(origProb, cln1, cln2, sig1, sig2)
      val expBoost = Math.log(origProb / (ProbOne - origProb) * expB1) * expB2

      Math.abs(boost - expBoost) shouldBe <(doubleTolerance)
    }
  }

  def parsedPerson(lastName:String, firstName:String, signature:String):ParsedPerson = {
    val pp = new ParsedPerson()
    pp.firstName = firstName
    pp.lastName = lastName
    pp.signature = signature
    pp
  }
  val genCoAuthorTestData = Table(
    ("parsedPerson", "coauthor"),
    (
      parsedPerson("Wu", "Xu xian", "ZHO:XX"),
      "wu|x|x:zho"
    ),
    (
      parsedPerson("Wu", "Xu", "ZHO:X"),
      "wu|x:zho"
    ),
    (
      parsedPerson("Hofman", "Albert", "A"),
      "hofman|a"
    )
  )

  @Test(groups = Array("UnitTest"))
  def genCoauthorTests() {
    forAll(genCoAuthorTestData) { (pp, coauthor) =>
      val ca = genCoAuthor(pp)
      ca shouldBe coauthor
    }
  }
}
