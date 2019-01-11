package org.ditw.nameUtils.nameParser.langSpecData.jpn

import org.scalatest.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.testng.annotations.Test


/**
  * Created by dev on 2018-02-13.
  */
class HanaAndLastNameTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import HanaAndLastName._
  @Test(groups = Array("UnitTest") )
  def lastNameSetTest(): Unit = {
    isNameFound("abe") shouldBe true
    isNameFound("abee") shouldBe false
    isNameFound("saitō") shouldBe true
    isNameFound("saito") shouldBe true
    isNameFound("du") shouldBe false
  }

  @Test(groups = Array("UnitTest") )
  def kanaSetTest(): Unit = {
    isKanaFound("abe") shouldBe false
    isKanaFound("ty") shouldBe false
    isKanaFound("a") shouldBe true
    isKanaFound("be") shouldBe true
    isKanaFound("tō") shouldBe true
    isKanaFound("to") shouldBe true

  }

  val checkKanasTestData = Table(
    ("n", "res"),
    ("anna", true),
    ("johann", false),
    ("tapan", true), // not japanese name though
    ("ke", true),
    ("ge", true),
    ("kyo", true),
    ("kyoto", true),
    ("kyotoyamakaze", true),
    ("kydoto", false),
    ("susan", false),
    ("teresa", false),
    ("damien", false),
    ("ky", false)
  )

  @Test(groups = Array("UnitTest") )
  def checkKanasTest(): Unit = {
    forAll(checkKanasTestData) { (n, res) =>
      checkKanas(n) shouldBe res
    }
  }

  // todo (distinguish from korean names):
  //   (an,a akihiro choa hiromi hyo-jin jaibin jin-kyu kie kihyon kin-nan kumiko masae sehee tatsuichi toshu woojin yasuyoshi yasuyosi yoshimori)

  val isJapaneseNameTestData = Table(
    ("ln", "fn", "res"),
    ("earashi", "j", true),
    ("Boachie-Adjei", "Oheneba", false),
    ("earashi", "joshua", false),
    ("du", "mitsuharu", false),
    ("Earashi", "mitsuharu", true)
  )

  @Test(groups = Array("UnitTest") )
  def isJapaneseNameTest():Unit = {
    forAll(isJapaneseNameTestData) { (ln, fn, res) =>
      isJapaneseName(ln, fn) shouldBe res
    }
  }
}
