package org.ditw.textSeg.catSegMatchers
import org.ditw.matcher.MatchPool
import org.ditw.textSeg.Settings.TknrTextSeg
import org.ditw.textSeg.TestHelpers
import org.ditw.textSeg.common.AllCatMatchers.mmgrFrom
import org.ditw.textSeg.common.Tags._
import org.ditw.textSeg.common.Vocabs
import org.ditw.tknr.TknrHelpers
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class Cat2SegMatchersTest extends FlatSpec with Matchers with TableDrivenPropertyChecks {

  private def testDataEntr(
    inStr:String,
    ranges:(Int, Int, Int)*
  ):(String, String, Set[(Int, Int, Int)]) = {
    (
      inStr, TagGroup4Univ.segTag, ranges.toSet
    )
  }

  private val univSegtestData = Table(
    ("inStr", "tag", "expRanges"),
    testDataEntr(
      "The Graduate School and University Center of The City University of New York",
      (0, 8, 14)
    ),
    testDataEntr(
      "1 Department of Urology, Weill Medical College of Cornell University",
      (0, 8, 10)
    ),
    testDataEntr(
      "University of Thessalia, Medical School",
      (0, 0, 3)
    ),
    testDataEntr(
      "Drexel University School of Public Health, Dept. of Community Health & Prevention",
      (0, 0, 2)
    ),
    testDataEntr(
      "The University of Texas Graduate School of Biomedical Sciences at Houston",
      (0, 0, 4)
    ),
    testDataEntr(
      "Matsumoto Dental University Graduate School of Oral Medicine",
      (0, 0, 3)
    ),
    testDataEntr(
      "University of Rochester School of Medicine and Dentistry",
      (0, 0, 3)
    ),
    testDataEntr(
      "Yale University School of Medicine",
      (0, 0, 2)
    ),
    testDataEntr(
      "Department of Surgery, University of Vermont Medical College",
      (0, 3, 6)
    ),
    testDataEntr(
      "University of Heidelberg Medical School",
      (0, 0, 3)
    ),
    testDataEntr(
      "Robert Gordon University, School of Nursing and Midwifery",
      (0, 0, 3)
    ),
    testDataEntr(
      "University of Gdańsk and Medical University of Gdańsk",
      (0, 0, 3),
      (0, 4, 8)
    ),
    testDataEntr(
      "University of Thessalia, Medical School",
      (0, 0, 3)
    ),
    testDataEntr(
      "University of Arkansas for Medical Sciences",
      (0, 0, 6)
    ),
    testDataEntr(
      "Drexel University School of Public Health, Dept. of Community Health & Prevention",
      (0, 0, 2)
    ),
    testDataEntr(
      "wyss institute for biologically inspired engineering at harvard university",
      (0, 7, 9)
    ),
    testDataEntr(
      "harvard university faculty of arts and sciences center for systems biology",
      (0, 0, 2)
    ),
    testDataEntr(
      "Swiss Federal Institute of Technology and University of Zurich",
      (0, 6, 9)
    ),
    testDataEntr(
      "Georgia Institute of Technology and Emory University School of Medicine Atlanta",
      (0, 5, 7)
    )
  )

  private val mmgr = mmgrFrom(
    Cat1SegMatchers.segMatchers,
    Cat2SegMatchers.segMatchers
  )
  "Cat2 seg matchers test" should "pass" in {
    forAll(univSegtestData) { (inStr, tag, expRanges) =>
      TestHelpers.runAndVerifyRanges(
        mmgr, inStr, tag, expRanges
      )
    }
  }
}
