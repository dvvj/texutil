package org.ditw.nameUtils.nameParser

import org.ditw.nameUtils.nameparser.NamesProcessed
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.testng.TestNGSuite
import org.scalatest.{FlatSpec, Matchers}
import org.testng.annotations.Test

/**
  * Created by dev on 2017-09-06.
  */
class GrantParsersTests extends TestNGSuite with Matchers with TableDrivenPropertyChecks {

  import TestHelpers._
  import ParserHelpers._
  import ResultAttrKeyEnum._
  import GrantParsers._
  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._

  private def dutchResultWithContact(lastName:String, firstNames:IndexedSeq[String],
                                     preposition:Option[String],
                                     extraNames: Option[IndexedSeq[String]] = None) =
    dutchResult(lastName, firstNames, preposition, extraNames).addAttrs(
      Iterable(Out_Grant_Role -> IndexedSeq(ContactRole))
    )
  private def dutchResultWithAcTitlesAndContact(lastName:String, firstNames:IndexedSeq[String],
                                     preposition:Option[String], acTitles:IndexedSeq[String],
                                     extraNames: Option[IndexedSeq[String]] = None) =
    dutchResultWithAcTitles(lastName, firstNames, preposition, acTitles, extraNames).addAttrs(
      Iterable(Out_Grant_Role -> IndexedSeq(ContactRole))
    )
  private def dutchResultWithContactWithSuffix(lastName:String, firstNames:IndexedSeq[String],
                                     preposition:Option[String],
                                     suffix:String,
                                     extraNames: Option[IndexedSeq[String]] = None) =
    dutchResult(lastName, firstNames, preposition, extraNames).addAttrs(
      Iterable(
        Out_Grant_Role -> IndexedSeq(ContactRole),
        Suffix -> IndexedSeq(suffix)
      )
    )
  private def chineseResultWithContact(lastName:String, firstNames:IndexedSeq[String]) = {
    normResult(IndexedSeq(lastName), firstNames, Chinese).addAttrs(
      Iterable(Out_Grant_Role -> IndexedSeq(ContactRole))
    )
  }

  val testData = Table(
    ("name", "resLang", "res"),
//    (
//      "SHAO, XUESI M",
//      Option(Chinese),
//      Option(chineseResult(
//        "SHAO",
//        IndexedSeq("XUE", "SI") //todo: fix me
//      ))
//    ),
    (
      "VAN DEN BERG, JACOB J (contact)",
      Option(Dutch),
      Option(dutchResultWithContact(
        "VAN DEN BERG",
        IndexedSeq("JACOB"),
        Option("VAN DEN"),
        Option(IndexedSeq("J"))
      ))
    ),
    (
      "VAN DEN BERG, JR., JACOB J (contact)",
      Option(Dutch),
      Option(dutchResultWithContactWithSuffix(
        "VAN DEN BERG",
        IndexedSeq("JACOB"),
        Option("VAN DEN"),
        "JR.",
        Option(IndexedSeq("J"))
      ))
    ),
    (
      "VAN DEN BERG, PH.D. JACOB J (contact)",
      Option(Dutch),
      Option(dutchResultWithAcTitlesAndContact(
        "VAN DEN BERG",
        IndexedSeq("JACOB"),
        Option("VAN DEN"),
        IndexedSeq("PH.D."),
        Option(IndexedSeq("J"))
      ))
    ),
    (
      "VAN DEN BERG, PH.D., CRA, JACOB J (contact)",
      Option(Dutch),
      Option(dutchResultWithAcTitlesAndContact(
        "VAN DEN BERG",
        IndexedSeq("JACOB"),
        Option("VAN DEN"),
        IndexedSeq("PH.D.", "CRA"),
        Option(IndexedSeq("J"))
      ))
    ),
    (
      "VAN DEN BERG, JACOB J",
      Option(Dutch),
      Option(dutchResult(
        "VAN DEN BERG",
        IndexedSeq("JACOB"),
        Option("VAN DEN"),
        Option(IndexedSeq("J"))
      ))
    ),
    (
      "VAN DEN BERG, JACOB J.",
      Option(Dutch),
      Option(dutchResult(
        "VAN DEN BERG",
        IndexedSeq("JACOB"),
        Option("VAN DEN"),
        Option(IndexedSeq("J"))
      ))
    ),
    (
      "VAN DEN BERG, JACOB J., MD, MPH",
      Option(Dutch),
      Option(dutchResultWithAcTitles(
        "VAN DEN BERG",
        IndexedSeq("JACOB"),
        Option("VAN DEN"),
        IndexedSeq("MD", "MPH"),
        Option(IndexedSeq("J"))
      ))
    ),
    (
      "TAN, JUN",
      Option(Chinese),
      Option(chineseResult(
        "TAN",
        IndexedSeq("JUN")
      ))
    ),
    (
      "WANG, JI MING",
      Option(Chinese),
      Option(chineseResult(
        "WANG",
        IndexedSeq("JI", "MING")
      ))
    ),
    (
      "WANG, JI MING (contact)",
      Option(Chinese),
      Option(chineseResultWithContact(
        "WANG",
        IndexedSeq("JI", "MING")
      ))
    ),
    (
      "JI MING WANG",
      Option(Chinese),
      Option(chineseResult(
        "WANG",
        IndexedSeq("JI", "MING")
      ))
    ),
    (
      "XIAODONG WANG",
      Option(Chinese),
      Option(chineseResult(
        "WANG",
        IndexedSeq("XIAO", "DONG")
      ))
    ),
    (
      "XiaoDong Wang",
      Option(Chinese),
      Option(chineseResult(
        "Wang",
        IndexedSeq("Xiao", "Dong")
      ))
    ),
    (
      ",",
      None,
      None
    )
//    (
//      "DIRK, VAN DEN BOOM",
//      Option(Dutch),
//      Option(dutchResult(
//        "VAN DEN BOOM",
//        IndexedSeq("DIRK"),
//        Option("VAN DEN")
//      ))
//    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def grantParserTests() {
    forAll(testData) { (name, resLang, res) =>
      val in = GrantParsers.grantNameInput(name)

      val r = parseGrantName(in)
      if (r.nonEmpty) {
        val result = r.get._2
        result.lang shouldBe resLang
        result.lastNames shouldBe res.get.lastNames
        result.attrs shouldBe res.get.attrs
      }
      else {
        res.isEmpty shouldBe true
      }
    }
  }

  private def genNp(ln:String, fn:String, mn:String, sf:String, src:String, sg:String):NamesProcessed = {
    val np = new NamesProcessed
    np.lastName = ln
    np.firstName = fn
    np.middleName = mn
    np.suffix = sf
    np.source = src
    np.signature = sg
    np
  }

  private val parseGrantNameJTestData = Table(
    ("name", "res"),
    (
      ",",
      null
    ),
//    (
//      "UNKNOWN,",
//      null
//    ),
    (
      "LAPU-BULA,",
      genNp(
        "lapu-bula",
        "l", //todo: first name should be empty
        "",
        "",
        "LAPU-BULA,",
        "l"
      )
    ),
    (
      "HARRIS RAYMOND C,",
      genNp(
        "harris",
        "raymond", //todo: first name should be empty
        "",
        "",
        "HARRIS RAYMOND C,",
        "rc"
      )
    ),
    (
      "FALCK JOHN RUSSELL,",
      genNp(
        "russell",
        "falck", //todo: first name should be empty
        "john",
        "",
        "FALCK JOHN RUSSELL,",
        "fj"
      )
    ),
    (
      "PELICCI,",
      genNp(
        "pelicci",
        "p", //todo: first name should be empty
        "",
        "",
        "PELICCI,",
        "p"
      )
    ),
    (
      "CASEY/GUNNAR,",
      genNp(
        "gunnar",
        "casey",
        "",
        "",
        "CASEY/GUNNAR,",
        "c"
      )
    ),
    (
      "MUSSELMAN, ROD PHILLIP",
      genNp(
        "musselman",
        "rod",
        "phillip",
        "",
        "MUSSELMAN, ROD PHILLIP",
        "rp"
      )
    ),
    (
      "GRODZICKER, TERRI I.",
      genNp(
        "grodzicker",
        "terri",
        "",
        "",
        "GRODZICKER, TERRI I.",
        "ti"
      )
    ),
    (
      "WILLIAMS-BLANGERO, SARAH A.",
      genNp(
        "williams-blangero",
        "sarah",
        "",
        "",
        "WILLIAMS-BLANGERO, SARAH A.",
        "sa"
      )
    ),
    (
      "PAGE, JR., C. DAVID",
      genNp(
        "page",
        "david",
        "",
        "jr",
        "PAGE, JR., C. DAVID",
        "dc"
      )
    )
    //    (
    //      "SHAO, XUESI M",
    //      Option(Chinese),
    //      Option(chineseResult(
    //        "SHAO",
    //        IndexedSeq("XUE", "SI") //todo: fix me
    //      ))
    //    ),

  )

  private def compareNamesProcessed(n1:NamesProcessed, n2:NamesProcessed):Boolean = {
    (n1 == null && n2 == null) || (
      n1.firstName == n2.firstName &&
        n1.lastName == n2.lastName &&
        n1.middleName == n2.middleName &&
        n1.nobeltitles == n2.nobeltitles &&
        n1.signature == n2.signature &&
        n1.source == n2.source &&
        n1.suffix == n2.suffix
    )
  }

  @Test(groups = Array("UNIT_TEST"))
  def parseGrantNameJTest() {
    forAll(parseGrantNameJTestData) { (name, res) =>
      val parsedName = parseGrantNameJ(name)

      compareNamesProcessed(parsedName, res) shouldBe true
    }
  }

  val splitNameTestData = Table(
    ("source", "names"),
    (
      "TAN, JUN ;",
      Array("TAN, JUN")
    ),
    (
      ",  ;",
      Array(",")
    ),
    (
      "HOPFER, CHRISTIAN J (contact); SAKAI, JOSEPH T;",
      Array("HOPFER, CHRISTIAN J (contact)", "SAKAI, JOSEPH T")
    )
  )

  @Test(groups = Array("UNIT_TEST"))
  def splitNameTests() {
    forAll(splitNameTestData) { (source, names) =>
      val r = splitNamesJ(source)
      r shouldBe names
    }
  }

  import MultiPartNameUtils._
  val removeCITestData = Table(
    ("in", "toFind", "found", "out"),
    //( "TORTU, STEPHANIE DO NOT USE A MIDDLE NAME", NoMiddleNameStr, true, "TORTU, STEPHANIE" ),
    ( "DICKSON-GOMEZ, JULIA B", ContactStr, false, "DICKSON-GOMEZ, JULIA B" ),
    ( "DICKSON-GOMEZ, JULIA B (Contact)", ContactStr, true, "DICKSON-GOMEZ, JULIA B" ),
    ( "DICKSON-GOMEZ, JULIA B (contact)", ContactStr, true, "DICKSON-GOMEZ, JULIA B" ),
    ( "DICKSON-GOMEZ, JULIA B (CONTACT)", ContactStr, true, "DICKSON-GOMEZ, JULIA B" ),
    ( "DICKSON-GOMEZ, JULIA B(contact)", ContactStr, true, "DICKSON-GOMEZ, JULIA B" ),
    ( "(contact)DICKSON-GOMEZ, JULIA B", ContactStr, true, "DICKSON-GOMEZ, JULIA B" ),
    ( "(contact) DICKSON-GOMEZ, JULIA B", ContactStr, true, "DICKSON-GOMEZ, JULIA B" ),
    ( "(contact) ", ContactStr, true, "" )
  )

  @Test(groups = Array("UNIT_TEST"))
  def removeCITests() {
    forAll(removeCITestData) { (in, toFind, found, out) =>
      val (f, r) = removeCI(in, toFind)
      f shouldBe found
      r shouldBe out
    }
  }

  val removeNoiseCITestData = Table(
    ("in", "toFind", "out"),
    ( "TORTU, STEPHANIE DO NOT USE A MIDDLE NAME", NoiseInfo, "TORTU, STEPHANIE" ),
    ( "N/A", NoiseInfo, "" )
  )

  @Test(groups = Array("UNIT_TEST"))
  def removeNoiseCITestDataTests() {
    forAll(removeNoiseCITestData) { (in, noises, out) =>
      val r = removeNoiseCI(in, noises)
      r shouldBe out
    }
  }


  import InputKeyEnum._
  import ParserHelpers.Consts._
  val nonNamePartTestData = Table(
    ("in", "nameParts", "nonNameAttrs"),
    (
      "MCNAMARA MPH, ERICA L",
      IndexedSeq("MCNAMARA", "ERICA L"),
      Map(
        MultiPart_AcademicTitles -> "MPH"
      )
    ),
    (
      "MOCK, DR. MOCK",
      IndexedSeq("MOCK", "MOCK"),
      Map(
        MultiPart_AcademicTitles -> "DR."
      )
    ),
    (
      "MASTER, VIRAJ",
      IndexedSeq("VIRAJ"),
      Map(
        MultiPart_AcademicTitles -> "MASTER"
      )
    ),
    (
      "KIM, DO-HYUNG",
      IndexedSeq("KIM", "DO-HYUNG"),
      EmptyInputAttrMap
    ),
    (
      "DO, AN",
      IndexedSeq("DO", "AN"),
      EmptyInputAttrMap
    ),
    (
      "BA, YONG",
      IndexedSeq("BA", "YONG"),
      EmptyInputAttrMap
    ),
    (
      "NURO PHD, KATHRYN",
      IndexedSeq("NURO", "KATHRYN"),
      Map(
        MultiPart_AcademicTitles -> "PHD"
      )

    ),
    (
      "CASTRO PH.D, ALLEN",
      IndexedSeq("CASTRO", "ALLEN"),
      Map(
        MultiPart_AcademicTitles -> "PH.D"
      )

    ),
    (
      "TENG, BA-BIE",
      IndexedSeq("TENG", "BA-BIE"),
      EmptyInputAttrMap
    ),
    (
      "EICHENBAUM, PH.D., HOWARD B.",
      IndexedSeq("EICHENBAUM", "HOWARD B."),
      Map(
        MultiPart_AcademicTitles -> "PH.D."
      )
    ),
    (
      "ROBERTS, II, L JACKSON",
      IndexedSeq("ROBERTS", "L JACKSON"),
      Map(
        MultiPart_SuffixPart -> "II"
      )
    ),
    (
      "LAURENCIN, CATO T., MD, PHD",
      IndexedSeq("LAURENCIN", "CATO T."),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("MD", "PHD")
      )
    ),
    (
      "AGRON, MA.RD, PEGGY",
      IndexedSeq("AGRON", "PEGGY"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("MA.RD")
      )
    ),
    (
      "JOHNSON, M.D., CAROLINE C",
      IndexedSeq("JOHNSON", "CAROLINE C"),
      Map(
        MultiPart_AcademicTitles -> "M.D."
      )
    ),
    (
      "SHARIFF-MARCO, PH.D., SALMA",
      IndexedSeq("SHARIFF-MARCO", "SALMA"),
      Map(
        MultiPart_AcademicTitles -> "PH.D."
      )
    ),
    (
      "TOMPKINS, BRADLEY, J",
      IndexedSeq("TOMPKINS", "BRADLEY J"),
      EmptyInputAttrMap
    ),
    (
      "ADAMS, JR, EDWARD",
      IndexedSeq("ADAMS", "EDWARD"),
      Map(
        MultiPart_SuffixPart -> "JR"
      )
    ),
    (
      "QUE, JR, LAWRENCE",
      IndexedSeq("QUE", "LAWRENCE"),
      Map(
        MultiPart_SuffixPart -> "JR"
      )
    ),
    (
      "XI, PH.D., WEN-TIEN",
      IndexedSeq("XI", "WEN-TIEN"),
      Map(
        MultiPart_AcademicTitles -> "PH.D."
      )
    ),
    (
      "SUN, XI",
      IndexedSeq("SUN", "XI"),
      EmptyInputAttrMap
    ),
//    (
//      "SUN, XI, TIAN",
//      IndexedSeq("SUN", "XI TIAN"),
//      EmptyInputAttrMap
//    ),
    (
      "CHEN, PH.D., WEN-TIEN",
      IndexedSeq("CHEN", "WEN-TIEN"),
      Map(
        MultiPart_AcademicTitles -> "PH.D."
      )
    ),
    (
      "HSU, JOHN MBA, MD, MS",
      IndexedSeq("HSU", "JOHN"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("MBA", "MD", "MS")
      )
    ),
    (
      "GALEA, SANDRO MD, MPH, DRPH",
      IndexedSeq("GALEA", "SANDRO"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("MD", "MPH", "DRPH")
      )
    ),
    (
      "WILBUR, JOELLEN, PHD, APN, FAAN",
      IndexedSeq("WILBUR", "JOELLEN"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("PHD", "APN", "FAAN")
      )
    ),
    (
      "MCCORMICK, PH.D., D.A.B.T., DAVID",
      IndexedSeq("MCCORMICK", "DAVID"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("PH.D.", "D.A.B.T.")
      )
    ),
    (
      "MCCORMICK, PH.D. DABT, DAVID",
      IndexedSeq("MCCORMICK", "DAVID"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("PH.D.", "DABT")
      )
    ),
    (
      "HOLLADAY, DR. E. BLAIR",
      IndexedSeq("HOLLADAY", "E. BLAIR"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("DR.")
      )
    ),
    (
      "WORTHAM, JAMES T., JR.",
      IndexedSeq("WORTHAM", "JAMES T."),
      Map(
        MultiPart_SuffixPart -> "JR."
      )
    ),
    (
      "GRAY, SHELLEY I, PHD, CCC-SLP",
      IndexedSeq("GRAY", "SHELLEY I"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("PHD", "CCC-SLP")
      )
    ),
    (
      "OJAKAA, MD, DR. DAVID IJAKAA",
      IndexedSeq("OJAKAA", "DAVID IJAKAA"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("MD", "DR.")
      )
    ),
    (
      "DEXTER PH.D, LCSW, VICTORIA",
      IndexedSeq("DEXTER", "VICTORIA"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("PH.D", "LCSW")
      )
    ),
    (
      "KUO, GRACE M., PHMD, PHD",
      IndexedSeq("KUO", "GRACE M."),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("PHMD", "PHD")
      )
    ),
    (
      "SWARR, DANIEL, T (MD) TODD",
      IndexedSeq("SWARR", "DANIEL T TODD"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("(MD)")
      )
    ),
    (
      "DE LA TORRE, RN, SUSANA",
      IndexedSeq("DE LA TORRE", "SUSANA"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("RN")
      )
    ),
    (
      "YONG, PH.D., LIU",
      IndexedSeq("YONG", "LIU"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("PH.D.")
      )
    ),
    (
      "UHL, CRA, KARI T.",
      IndexedSeq("UHL", "KARI T."),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("CRA")
      )
    ),
    (
      "WILSON, MED, MPA, JANET S",
      IndexedSeq("WILSON", "JANET S"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("MED", "MPA")
      )
    ),
    (
      "RINALDI, M.ED., JULIE ERWIN",
      IndexedSeq("RINALDI", "JULIE ERWIN"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("M.ED.")
      )
    ),
    (
      "ADAMS, LINDA T., DR.PH, R.N., F.A.A.N.",
      IndexedSeq("ADAMS", "LINDA T."),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("DR.PH", "R.N.", "F.A.A.N.")
      )
    ),
    (
      "FRYE ED.D., GPC, GARY L",
      IndexedSeq("FRYE", "GPC GARY L"),
      Map(
        MultiPart_AcademicTitles -> combineAttrParts("ED.D.")
      )
    )
  )

  // todo: other strange input:
  //   PRONK, VP PRECLINICAL DEV, GIJSBERTUS J.
  //   PALMER, CARDEN, NORMAN, PAM


  @Test(groups = Array("UNIT_TEST"))
  def parseNonNamePartsTests() {
    forAll (nonNamePartTestData) { (name, outNameParts, nonNameAttrs) =>
      val nameParts = splitGrantNameParts(name)

      val (outParts, attrMap) = preprocessNameParts(nameParts)
      outParts shouldBe outNameParts
      attrMap shouldBe nonNameAttrs
    }
  }

  val splitFirstLastNamesTestData = Table(
    ("part", "lastName", "foreName"),
    ("LAPU-BULA", "LAPU-BULA", None),
    ("TOURASSI", "TOURASSI", None),
    ("HARRIS RAYMOND C", "HARRIS", Option("RAYMOND C")),
    ("MICHAEL FRANK", "FRANK", Option("MICHAEL"))
  )

  @Test(groups = Array("UNIT_TEST"))
  def splitFirstLastNamesTests() {
    forAll (splitFirstLastNamesTestData) { (part, lastName, foreName) =>
      val (ln, fn) = splitFirstLastNames4Grant(part)
      ln shouldBe lastName
      fn shouldBe foreName
    }
  }
}
