package org.ditw.demo1.matchers
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.MatchPool
import org.ditw.tknr.TknrHelpers.{TokenSplitter_CommaColon, TokenSplitter_DashSlash}
import org.ditw.tknr.Tokenizers.TokenizerSettings
import org.ditw.tknr.{Tokenizers, Trimmers}

object TmTest1 extends App {
  def testTm(adm0:TGNMap,
             testStrs:Iterable[String]
            ):Unit = {
    val trimByPuncts = Trimmers.byChars(
      ",;:\"()*†#".toSet
    )
    val settings = TokenizerSettings(
      "\\n+",
      "[\\s]+",
      List(
        TokenSplitter_CommaColon, TokenSplitter_DashSlash
      ),
      trimByPuncts
    )

    val testTokenizer = Tokenizers.load(settings)

    val dict = MatcherGen.loadDict(Iterable(adm0))
    val tm = Adm0Gen.genTms(adm0, dict).head
    testStrs.foreach { str =>
      val res = tm.run(
        MatchPool.fromStr(str, testTokenizer, dict)
      )
      println(res)
    }

  }

  import org.ditw.demo1.TestData._
  import org.ditw.demo1.gndata.GNCntry._
  val admUS = testCountries(US)
  testTm(admUS, List(
    "massachusetts",
    "Worcester County",
    "City of Boston"
  ))
}
