package org.ditw.nameUtils.nameParser

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-08.
  */
class TrialParsersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import TestHelpers._
  import ParserHelpers._
  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
  val testData = Table(
    ("name", "res"),
//    (
//      "Jiang-Ti, Kong",
//      Option(chineseResultWithAcaTitles(
//        "Kong",
//        IndexedSeq("Jiang", "Ti"),
//        IndexedSeq[String]()
//      ))
//    ),
    (
      "Jiang-Ti Kong, Lab Director",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("Lab Director")
      ))
    ),
    (
      "Jiang-Ti Kong, Lecturer",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("Lecturer")
      ))
    ),
    (
      "Jiang-Ti Kong, IVF Laboratory director",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("IVF Laboratory director")
      ))
    ),
    (
      "Jiang-Ti Kong, BSc; DC",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("BSc", "DC")
      ))
    ),
    (
      "Jiang-Ti Kong, Phd; Md",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("Phd", "Md")
      ))
    ),
    (
      "Jiang-Ti Kong, M.Ed.; Ph.D.",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("M.Ed.", "Ph.D.")
      ))
    ),
    (
      "Jiang-Ti Kong, D.D.S, M.Sc.Ph.D",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("D.D.S", "M.Sc.Ph.D")
      ))
    ),
    (
      "Yuan Yaozong, M.D.,MPH",
      Option(chineseResultWithAcaTitles(
        "Yuan",
        IndexedSeq("Yao", "zong"),
        IndexedSeq("M.D.", "MPH")
      ))
    ),
    (
      "Yuan Yaozong, MD, MS, CAT",
      Option(chineseResultWithAcaTitles(
        "Yuan",
        IndexedSeq("Yao", "zong"),
        IndexedSeq("MD", "MS", "CAT")
      ))
    ),
    (
      "Dominique JF van den Meiracker Jr., M.D.",
      Option(dutchResultWithAcTitlesAndSuffix(
        "van den Meiracker",
        IndexedSeq("Dominique"),
        Option("van den"),
        IndexedSeq("M.D."),
        "Jr.",
        Option(IndexedSeq("JF"))
      ))
    ),
    (
      "Dominique JF van den Meiracker Jr.",
      Option(dutchResultWithSuffix(
        "van den Meiracker",
        IndexedSeq("Dominique"),
        Option("van den"),
        "Jr.",
        Option(IndexedSeq("JF"))
      ))
    ),
    (
      "Dominique JF van den Meiracker III, M.D.",
      Option(dutchResultWithAcTitlesAndSuffix(
        "van den Meiracker",
        IndexedSeq("Dominique"),
        Option("van den"),
        IndexedSeq("M.D."),
        "III",
        Option(IndexedSeq("JF"))
      ))
    ),
    (
      "Prof. Yuan Yaozong",
      Option(chineseResultWithAcaTitles(
        "Yuan",
        IndexedSeq("Yao", "zong"),
        IndexedSeq("Prof.")
      ))
    ),
    (
      "Ding Ke-Feng",
      Option(chineseResult(
        "Ding",
        IndexedSeq("Ke", "Feng")
      ))
    ),
    (
      "A. H. van den Meiracker, MD, PhD",
      Option(dutchResultWithAcTitles(
        "van den Meiracker",
        IndexedSeq("A"),
        Option("van den"),
        IndexedSeq("MD", "PhD"),
        Option(IndexedSeq("H"))
      ))
    ),
    (
      "Dominique JF van den Meiracker, MD, Prof.",
      Option(dutchResultWithAcTitles(
        "van den Meiracker",
        IndexedSeq("Dominique"),
        Option("van den"),
        IndexedSeq("MD", "Prof."),
        Option(IndexedSeq("JF"))
      ))
    ),
    (
      "Jiang-Ti Kong Jr., D.D.S, M.Sc.Ph.D",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("D.D.S", "M.Sc.Ph.D")
      ))
    ),
    (
      "Jiang-Ti Kong, Ass prof",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("Ass prof")
      ))
    ),
    (
      "Jiang-Ti Kong",
      Option(chineseResult("Kong", IndexedSeq("Jiang", "Ti")))
    ),
    (
      "Assoc. Prof. Jiang-Ti Kong, MD, PhD",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("Assoc.", "Prof.", "MD", "PhD")
      ))
    ),
    (
      "Jiang-Ti Kong, MD,PhD, MPH",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("MD", "PhD", "MPH")
      ))
    ),
    (
      "Jiang-Ti Kong, phD, MD",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("phD", "MD")
      ))
    ),
    (
      "Jiang-Ti Kong, RN, BSN",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("RN", "BSN")
      ))
    ),
    (
      "Jiang-Ti Kong, DDS, MsC",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("DDS", "MsC")
      ))
    ),
    (
      "RUIHUA XU, Professor",
      Option(chineseResultWithAcaTitles(
        "XU",
        IndexedSeq("RUI", "HUA"),
        IndexedSeq("Professor")
      ))
    ),
    (
      "Dr. Jiang-Ti Kong MD",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("Dr.", "MD")
      ))
    ),
    (
      "Jiang-Ti Kong, Prof. Dr. med.",
      Option(chineseResultWithAcaTitles(
        "Kong",
        IndexedSeq("Jiang", "Ti"),
        IndexedSeq("Prof. Dr. med.")
      ))
    ),
    (
      "Chunxue Bai, M.D, Ph.D.",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("Ph.D.", "M.D")
      ))
    ),
    (
      "Chunxue Bai, MD,PhD stud.",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("PhD stud.", "MD")
      ))
    ),
    (
      "Chunxue Bai, Pharm D, PhD",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("Pharm D", "PhD")
      ))
    ),
    (
      "Chunxue Bai, Asst Prof",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("Asst Prof")
      ))
    ),
    (
      "Chunxue Bai, Prof.Dr.med.",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("Prof.Dr.med.")
      ))
    ),
    (
      "Chunxue Bai, MD-PhD",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("MD-PhD")
      ))
    ),
    (
      "Chunxue Bai, FRCS (ed)",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("FRCS")
      ))
    ),
    (
      "Chunxue Bai, MD & Ph.D",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("MD & Ph.D")
      ))
    ),
    (
      "Chunxue Bai, MD & Ph.D, Site 0064",
      Option(chineseResultWithAcaTitles(
        "Bai",
        IndexedSeq("Chun", "xue"),
        IndexedSeq("MD & Ph.D")
      ))
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def trialParserTests() {
    forAll(testData) { (name, res) =>
      val in = TrialParsers.trialNameInput(name)
      val r = parseTrialName(in)
      if (r.nonEmpty) {
        val result = r.get._2
        result.lang shouldBe res.get.lang
        result.lastNames shouldBe res.get.lastNames
        result.attrs shouldBe res.get.attrs
      }
      else {
        res.isEmpty shouldBe true
      }
    }
  }

  val checkTitlesInLastNamePartsTestData = Table(
    ("name", "remParts", "titles"),
    (
      "Vicki R Snodgrass-Miller, Ba",
      IndexedSeq("Vicki R Snodgrass-Miller", "Ba"),
      None
    ),
    (
      "Jason K Panchamia, DO",
      IndexedSeq("Jason K Panchamia"),
      Option(IndexedSeq("DO"))
    ),
    (
      "June Ohata, BS, BA",
      IndexedSeq("June Ohata"),
      Option(IndexedSeq("BS", "BA"))
    ),
    (
      "Wing Yee Lillian Choy, MBBS (HK)",
      IndexedSeq("Wing Yee Lillian Choy"),
      Option(IndexedSeq("MBBS", "(HK)"))
    ),
    (
      "Man Shun Law, FHKCA,FHKAM",
      IndexedSeq("Man Shun Law"),
      Option(IndexedSeq("FHKCA", "FHKAM"))
    ),
    (
      "Julia Hurrelbrink, BA, BSN, RN",
      IndexedSeq("Julia Hurrelbrink"),
      Option(IndexedSeq("BA", "BSN", "RN"))
    ),
    (
      "Hongming Pan, Doctor",
      IndexedSeq("Hongming Pan"),
      Option(IndexedSeq("Doctor"))
    ),
    (
      "Anthony Cardile, DO",
      IndexedSeq("Anthony Cardile"),
      Option(IndexedSeq("DO"))
    ),
    (
      "Juan Ma, physician",
      IndexedSeq("Juan Ma"),
      Option(IndexedSeq("physician"))
    )
  )

  import TrialParsers._

  @Test(groups = Array("UNIT_TEST"))
  def checkTitlesInLastNamePartsTests() {
    forAll (checkTitlesInLastNamePartsTestData) { (name, remParts, titles) =>
      val parts = splitTrialNameParts(name)
      val (rp, t) = checkTitlesInLastNameParts(parts)
      rp shouldBe remParts
      t shouldBe titles
    }
  }

  val curlyBracketPtnTestData = Table(
    ("in", "res"),
    ("a()d", "ad"),
    ("a(b)d", "ad"),
    ("a(bc)d (ef)g", "ad g"),
    ("a(bc)d", "ad")
  )

  @Test(groups = Array("UNIT_TEST"))
  def curlyBracketPtnTests() {
    forAll(curlyBracketPtnTestData) { (in, res) =>
      val r = CurlyBracketPtn.replaceAllIn(in, "")
      r shouldBe res
    }
  }
  val siteD4PtnTestData = Table(
    ("in", "res"),
    ("Daniel Keitz, site US07", "Daniel Keitz, "),
    ("Daniel Keitz, Site US07", "Daniel Keitz, "),
    ("Petr Nemec, Site 0020", "Petr Nemec, "),
    ("Jesus Lopez Garcia, Site 0064", "Jesus Lopez Garcia, ")
  )

  @Test(groups = Array("UNIT_TEST"))
  def siteD4PtnTests() {
    forAll(siteD4PtnTestData) { (in, res) =>
      val r = SiteD34Ptn.replaceAllIn(in, "")
      r shouldBe res
    }
  }

  val trialPersonHintTestData = Table(
    ("source", "hasHint"),
    (
      "Ahmad Halwani, Site 005", true
    ),
    (
      "Tibor Martyin, Site 0066", true
    ),
    (
      "Tibor Martyin, Site", false
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def trialPersonHintTests() {
    forAll(trialPersonHintTestData) { (source, hasHint) =>
      val r = containsTrialPersonHint(source)
      r shouldBe hasHint
    }
  }

}
