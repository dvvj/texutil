package org.ditw.nameUtils.nameParser

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-08-23.
  */
class GetLanguageTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import org.ditw.nameUtils.nameParser.utils.NameLanguages._
  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._
  import MedlineParsers.Consts._
  import ParserHelpers._

  val testData = Table(
    ("in", "lang"),
    (
      genNormInput("Dai", "Zunyan"),
      Option(Chinese)
    ),
    (
      genNormInput("Takumi-Kobayashi", "Asuka"),
      Option(Japanese)
    ),
    (
      genNormInput("Kobayashi", "S"),
      Option(Japanese)
    ),
    (
      genNormInput("Kobayashi", "Victor"),
      None
    ),
    (
      genNormInput("Kobayashi", "Susumu S"),
      Option(Japanese)
    ),
    (
      genNormInput("Takahashi", "Ken-Ichi"),
      Option(Japanese)
    ),
    (
      genNormInput("Kobayashi", "Kazuyoshi"),
      Option(Japanese)
    ),
    (
      genNormInput("Nishida", "Yoshihiro"),
      Option(Japanese)
    ),
    (
      genNormInput("Zheng", "Wanting"),
      Option(Chinese)
    ),
    (
      genNormInput("Sean", "Hong"),
      None
    ),
    (
      genNormInput("Lee", "Hong"),
      None
    ),
    (
      genNormInput("Hong", "Lee"),
      None
    ),
    (
      genNormInput("Duncan", "Ken"),
      None
    ),
    (
      genNormInput("C", "Badiu"),
      None
    ),
    (
      genNormInput("Badiu", "C"),
      None
    ),
    (
      genNormInput("N Aida", null),
      None
    ),
    (
      genNormInput("Eniu", "A"),
      None
    ),
    (
      genNormInput("Eniu", "A B"),
      None
    ),
    (
      genNormInput("Eniu", "A-B"),
      None
    ),
    (
      genNormInput("Eniu", "Zhang"),
      Option(Chinese)
    ),
    (
      genNormInput("Banning", "A"),
      None
    ),
    (
      genNormInput("Kamen", "Diane L"),
      None
    ),
    (
      genNormInput("Otero y Garzon", "Santiago"),
      Option(Hispanic)
    ),
    (
      genNormInput("Martinez de la Peña y Valenzuela", "Santiago"),
      Option(Hispanic)
    ),
    (
      genNormInput("Otero e Garzon", "Santiago"),
      Option(Hispanic)
    ),
    (
      genNormInput("Xiuying", "Liu"),
      Option(Chinese)
    ),
    (
      genNormInput("Xu", "Aie"),
      Option(Chinese)
    ),
    (
      genNormInput("Jiang", "Huidi"),
      Option(Chinese)
    ),
    (
      genNormInput("Xi", "Jinping"),
      Option(Chinese)
    ),
    (
      genNormInput("Xi", "J P"),
      Option(Chinese)
    ),
    (
      genNormInput("Xi", "Jin Ping"),
      Option(Chinese)
    ),
    (
      genNormInput("Xi", "Jin-Ping"),
      Option(Chinese)
    )
  )

  @Test(groups = Array("UnitTest") )
  def getLanguageTests() {
    forAll(testData) { (in, lang) =>
      val l = tryGetLanguage(in)
      if (lang.nonEmpty) l.head shouldBe lang.get
      else {
        val notDetected = l.isEmpty || l.forall(_ == Undetermined)
        notDetected shouldBe true
      }
    }
  }
}
