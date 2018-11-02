package org.ditw.matcher
import org.ditw.common.TkRange
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class CompMatcherNsTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  import CompMatchers._
  import CompMatcherNs._
  import TokenMatchers._
  import org.ditw.tknr.TestHelpers._

  private val tag12_23 = "tag12_23"
  private val matcher12_23 = seq(
    IndexedSeq(
      byTm(
        ngram(
          Set(Array("1"), Array("2")), dict)
      ),
      byTm(
        ngram(
          Set(Array("2"), Array("3")), dict)
      )
    ),
    tag12_23
  )
  private val seqTestData = Table(
    ( "cms", "inStr", "expResultMap" ),
    (
      List(matcher12_23),
      "2, 3 4\n2\" 1 3",
      Map(
        matcher12_23.tag.get -> Set(
          (0, 0, 2), (1, 1, 3)
        )
      )
    ),
    (
      List(matcher12_23),
      "1, 2, 3 4",
      Map(
        matcher12_23.tag.get -> Set(
          (0, 0, 2), (0, 1, 3)
        )
      )
    )
  )

  "seq matcher tests" should "pass" in {
    forAll(seqTestData) { (cms, inStr, expRes) =>
      val mmgr = new MatcherMgr(List(), cms)
      val matchPool:MatchPool = MatchPool.fromStr(inStr, testTokenizer, dict)
      mmgr.run(matchPool)
      val res = expRes.keySet
        .map(k => k -> matchPool.get(k))
        .toMap
      val res2Ranges = res.mapValues { ms =>
        ms.map(_.range)
      }
      val expMatcheRanges = expRes.mapValues { tp =>
        tp.map(p => TkRange(matchPool.input, p._1, p._2, p._3))
      }
      res2Ranges shouldBe expMatcheRanges
    }
  }

}
