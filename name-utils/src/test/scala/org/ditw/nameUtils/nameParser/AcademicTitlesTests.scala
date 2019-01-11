package org.ditw.nameUtils.nameParser

import MultiPartNameUtils.checkWholePartTitle
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.Matchers
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-08.
  */

class AcademicTitlesTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.AcademicTitles._

  private val UnitTestGroup = "UnitTest"
  @Test(groups = Array("UnitTest") )
  def testGenerateAbbrs() {
    forAll(testData) { (abbrs, results) =>
      val titles = _TitlesFromAbbrs(abbrs)
      titles shouldBe results
    }
    println("Done testing testGenerateAbbrs")
  }

  private val testData = Table(
    ("abbrs", "results"),
    (
      Seq("ph", "d"),
      Set("phd", "ph.d", "ph.d.", "phd.")
    ),
    (
      Seq("m", "d"),
      Set("md", "m.d", "m.d.", "md.")
    )
  )


  private val abbrTitleTestData = Table(
    ("abbr", "res"),
    (
      "DDS", Set("dds", "dds.", "d.d.s", "d.d.s.")
    )
  )

  @Test(groups = Array("UnitTest") )
  def abbrTitleTests() {
    println("========== Starting abbrTitleTests ...")
    abbrTitleTestData.forEvery { (abbr, res) =>
      val r = AbbrTitle(abbr)
      r shouldBe res
    }
  }

  private val decorateTitlesTestData = Table(
    ("title"),
    ("medical doctor"),
    ("ass prof.")
  )

  @Test(groups = Array("UnitTest") )
  def decoratedTitlesTests() {
    forAll(decorateTitlesTestData) { t =>
      checkWholePartTitle(t) shouldBe true
    }
  }

  private val containsTitlesTestData = Table(
    ("source", "contains"),
    ("Alaa MA Karim El-din, ass lect", true),
    ("Hu Weixin, Master", true),
    ("Hu Weixin, Doctor", true),
    ("Centocor Research & Development, Inc., PA, USA Clinical Trial", false),
    ("Barbra L Fogarty, BA", true),
    ("Joanna Ye, BA", true),
    ("Joanna Ye, MA", true),
    ("JOSEP ORDI-ROS, M", false),
    ("Centocor Research & Development, Inc., PA, USA Clinical Trial", false),
    ("Geneviève J Depresseux, Trial Coord", true),
    ("Betsy Roth-Wojcicki, MS", true),
    ("Betsy Roth-Wojcicki, CPNP", true)
  )

  @Test(groups = Array("UnitTest") )
  def containsTitlesTests() {
    println("========== Starting containsTitlesTests ...")
    forAll(containsTitlesTestData) { (source, contains) =>
      titlesInTrialNames(source) shouldBe contains
    }
  }
}
