package org.ditw.nameUtils.nameParser

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-11.
  */
class NameCompatibilityCheckersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.nameCompatibility.NameCompatibilityUtils._
  val chineseNameCompatibleTestData = Table(
    ("n1", "n2", "isCompatible"),
    (
      Array("ZHO:XQ", "Xiang qian"),
      Array("ZHO:XQ", "Xiang R"),
      false
    ),
    (
      Array("ZHO:XQ", "Xiang qian"),
      Array("ZHO:XQ", "Xiang Q"),
      true
    ),
    (
      Array("ZHO:XQ", "Xiang qian"),
      Array("ZHO:XQ", "Xiang qian"),
      true
    ),
    (
      Array("ZHO:XQ", "X Qian"),
      Array("ZHO:XQ", "Xiang q"),
      true
    ),
    (
      Array("ZHO:XQ", "Xiang", "qian"),
      Array("ZHO:XQ", "Xiang", "qian"),
      true
    ),
    (
      Array("ZHO:XQ", "Xiang", "qian"),
      Array("ZHO:XQ", "Xiang", "Q"),
      true
    ),
    (
      Array("ZHO:XQ", "Xiang", "qian"),
      Array("ZHO:XQ", "X", "Q"),
      true
    ),
    (
      Array("ZHO:XQ", "xiang", "qian"),
      Array("ZHO:XQ", "X", "Q"),
      true
    ),
    (
      Array("ZHO:XQ", "Xian qian"),
      Array("ZHO:XQ", "Xiang qian"),
      false
    ),
    (
      Array("ZHO:XQ", "Xian", "qian"),
      Array("ZHO:XQ", "Xiang", "Q"),
      false
    ),
    (
      Array("XQ", "Xiang", "qian"),
      Array("ZHO:XQ", "X", "Q"),
      false
    ),
    (
      Array("XQ", "Xiang", "qian"),
      Array("ZHO:XQ", "X", "Q"),
      false
    ),
    (
      Array("ZHO:DH", "Dong", "hao"),
      Array("ZHO:D", "D"),
      false
    )
  )

  @Test(groups = Array("UnitTest"))
  def checkChineseNameCompatibleTests() {
    forAll (chineseNameCompatibleTestData)(doCheck)
  }

  val dutchNameCompatibleTestData = Table(
    ("n1", "n2", "isCompatible"),
    (
      Array("NLD:A", "André"),
      Array("NLD:Ä", "Ä"),
      true
    ),
    (
      Array("NLD:A", "André"),
      Array("NLD:Ä", "Ändre"),
      true
    ),
    (
      Array("NLD:A", "André"),
      Array("NLD:A", "Andre"),
      true
    ),
    (
      Array("NLD:A", "André"),
      Array("NLD:A", "Ändre"),
      true
    ),
    (
      Array("NLD:ALC", "A L"),
      Array("NLD:A", "Anne"),
      true
    ),
    (
      //IndexedSeq("NLD:ALC", "Anne-Lotte"), note: '-' should be handled by the parser
      Array("NLD:ALC", "Anne Lotte"),
      Array("NLD:AL", "Anne Lotte"),
      true
    ),
    (
      Array("AL", "Anne Lotte"),
      Array("NLD:AL", "Anne Lotte"),
      false
    ),
    (
      Array("NLD:ALC", "A L"),
      Array("NLD:AL", "Anne Lotte"),
      true
    ),
    (
      Array("NLD:ALC", "A L"),
      Array("NLD:AL", "anne Lotte"),
      true
    ),
    (
      Array("NLD:ALC", "Ann L"),
      Array("NLD:AL", "Anne Lotte"),
      false
    ),
    (
      Array("NLD:ALC", "A Lotte"),
      Array("NLD:AL", "Anne Lotte"),
      true
    )
  )

  private def doCheck(n1:Array[String], n2:Array[String], isCompatible:Boolean):Unit = {
    val c = checkNameCompatibleJ(n1, n2, false)
    c shouldBe isCompatible
  }

  @Test(groups = Array("UnitTest"))
  def checkNameCompatibleGeneralTests() {
    forAll (dutchNameCompatibleTestData)(doCheck)
  }

  val generalNameCompatibleTestData = Table(
    ("n1", "n2", "isCompatible"),
    (
      Array("ad", "AARON"),
      Array("a", "a"),
      true
    )
  )

  @Test(groups = Array("UnitTest"))
  def generalNameCompatibleTests() {
    forAll (generalNameCompatibleTestData)(doCheck)
  }

  val chineseNamespaceGenTestData = Table(
    ("nameParts", "namespace"),
    (
      Array("Xi", "Jin", "ping"),
      Option("xi|j|p:zho")
    ),
    (
      Array("Xi", "J", "P"),
      Option("xi|j|p:zho")
    ),
    (
      Array("Xi", "tian"),
      Option("xi|t:zho")
    ),
    (
      Array("Xi", "T"),
      Option("xi|t:zho")
    ),
    (
      Array("Xi"),
      None
    )
  )

  import org.ditw.nameUtils.nameParser.utils.nameCompatibility.NameComponentComparors._

  @Test(groups = Array("UnitTest"))
  def genChineseNamespaceTests() {
    forAll (chineseNamespaceGenTestData) { (nameParts, namespace) =>
      val ns = chineseNamespaceGenerator(nameParts)
      ns shouldBe namespace
    }
  }

  val japaneseNamespaceGenTestData = Table(
    ("nameParts", "namespace"),
    (
      Array("Saitō", "Susumu"),
      Option("saito|susumu:jpn")
    ),
    (
      Array("Saito", "Susumu"),
      Option("saito|susumu:jpn")
    ),
    (
      Array("Kobayashi", "Susumu"),
      Option("kobayashi|susumu:jpn")
    ),
    (
      Array("Takahashi", "Ken", "Ichi"),
      Option("takahashi|ken|ichi:jpn")
    )
  )

  @Test(groups = Array("UnitTest"))
  def genJapaneseNamespaceTests() {
    forAll (japaneseNamespaceGenTestData) { (nameParts, namespace) =>
      val ns = japaneseNamespaceGenerator(nameParts)
      ns shouldBe namespace
    }
  }

  val generalNamespaceGenTestData = Table(
    ("nameParts", "namespace"),
    (
      Array("van den Berg", "Anne"),
      Option("van den berg|a")
    ),
    (
      Array("van den Berg", "Anne", "Bengt"),
      Option("van den berg|a")
    ),
    (
      Array("van den Berg", "änne"),
      Option("van den berg|a")
    ),
    (
      Array("van den Berg", "Änne"),
      Option("van den berg|a")
    ),
    (
      Array("van den Berg"),
      None
    )
  )

  @Test(groups = Array("UnitTest"))
  def generalNamespaceGenTests() {
    forAll (generalNamespaceGenTestData) { (nameParts, namespace) =>
      val ns = generalNamespaceGenerator(nameParts)
      ns shouldBe namespace
    }
  }

  val clusterNameCompatibilityTestData = Table(
    ("n1", "n2", "isCompatible"),
    (
      Array("NLD:A", "André"),
      Array("NLD:Ä", "Ä"),
      true
    ),
    (
      Array("NLD:A", "André"),
      Array("NLD:Ä", "Ändre"),
      true
    ),
    (
      Array("NLD:A", "André"),
      Array("NLD:A", "Andre"),
      true
    ),
    (
      Array("NLD:A", "André"),
      Array("NLD:A", "Ändre"),
      true
    ),
    (
      Array("NLD:A", "André"),
      Array("NLD:A", "Ändreas"),
      false
    ),
    (
      Array("a", "adnan"),
      Array("ah", "adnan"),
      true
    ),
    (
      Array("ah", "anthony"),
      Array("as", "anthony"),
      false
    ),
    (
      Array("ZHO:DW", "DA WEI"),
      Array("ZHO:DW", "Da wei"),
      true
    ),
    (
      Array("ad", "AARON"),
      Array("ac", "a"),
      false
    ),
    (
      Array("ZHO:XQ", "Xiang", "qian"),
      Array("ZHO:XQ", "X", "Q"),
      true
    ),
    (
      Array("ZHO:XQ", "xiang", "qian"),
      Array("ZHO:XQ", "X", "Q"),
      true
    ),
    (
      Array("ZHO:XQ", "Xian qian"),
      Array("ZHO:XQ", "Xiang qian"),
      false
    ),
    (
      Array("ad", "AARON"),
      Array("a", "a"),
      true
    )

  )

  private def doClusterCheck(n1:Array[String], n2:Array[String], isCompatible:Boolean):Unit = {
    val c = checkClusterNameCompatibleJ(n1, n2, false, false)
    c shouldBe isCompatible
  }

  @Test(groups = Array("UnitTest"))
  def clusterNameCompatibilityTests() {
    forAll (clusterNameCompatibilityTestData)(doClusterCheck)
  }



}
