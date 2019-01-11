package org.ditw.nameUtils.nameparser;


import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by hakanlofqvist on 07/12/15.
 */
@Test(groups = { "UnitTest" } )
public class NameParserTest {


    @Test
    public void testIsNameAndAcademicRank() throws Exception {

        NameParser parser = new NameParser();

        assert(!parser.isNameAndAcademicRank("Medical Director"));
        assert(!parser.isNameAndAcademicRank("AstraZeneca Clinical Study Information Center"));


        assert(parser.isNameAndAcademicRank("Jean-François Boivin, MD, FRCPC"));
        assert(parser.isNameAndAcademicRank("Ann B Cranney, MD"));
        assert(parser.isNameAndAcademicRank("B. Timothy Walsh, MD"));
        assert(parser.isNameAndAcademicRank("Anne-Mette Hvas, MD, PhD"));
        assert parser.isNameAndAcademicRank("Paul Erne, MD");
        //
        assert(!parser.isNameAndAcademicRank("Allison Rubino"));
        assert(!parser.isNameAndAcademicRank("E-mail: ClinicalTrials@ ImClone.com"));
        assert(!parser.isNameAndAcademicRank("Novum Pharmaceutical Research Services"));
        assert(!parser.isNameAndAcademicRank("Chief Medical Officer"));
        assert(!parser.isNameAndAcademicRank("Contacts Novartis"));
        assert(!parser.isNameAndAcademicRank(null));


    }

//    @Test
//    public void testSomeName() throws Exception {
//        NameParser parser = new NameParser();
//        System.out.println(parser.parseName("augusto juorio v"));
//        System.out.println(parser.parseName("loyd v allen jr phd"));
//
//        System.out.println(parser.parseName("thomas k kupiec phd"));  //Todo (1): Find out a safe way to remove academic ranks
//        System.out.println(parser.parseTrialName("Robert Grauss, MD, PhD, MSc")); //Todo: Maybe we dont need a seperate method for clincal trials if (1) is fixed
//        System.out.println(parser.parseTrialName("Enno van der Velde, MSc, PhD ")); //Todo: Multiple novel titles
//        System.out.println(parser.parseTrialName("xie yanyun, master"));
//        System.out.println(parser.parseTrialName("md"));
//    }

    private <T> void junitStyleAssertEquals(String msg, T v1, T v2) {
        Assert.assertEquals(v1, v2, msg);
    }

    @Test
    public void testParseName() throws Exception {

        NameParser parser = new NameParser();

//        System.out.println(parser.parseName("C. Lynn Anderson"));
//
//        System.out.println(parser.parseName("kar-ming fung"));

        junitStyleAssertEquals("Last name should match", "kupiec", parser.parseName("thomas k kupiec 3rd phd").lastName);
        junitStyleAssertEquals("Last name should match", "kupiec", parser.parseName("thomas k kupiec phd").lastName);
        junitStyleAssertEquals("Last name should match", "kupiec", parser.parseName("t kupiec phd").lastName);

        junitStyleAssertEquals("Last name should match", "hamilton", parser.parseName("howard hamilton 3rd").lastName);
        junitStyleAssertEquals("Signature should be km","km", parser.parseName("kar-ming fung").signature);
        junitStyleAssertEquals("Signature should be km","km", parser.parseName("kar ming fung").signature);
        junitStyleAssertEquals("Last name should contain nobel title", "la vecchia", parser.parseName("carlo la vecchia").lastName);
        junitStyleAssertEquals("Last name should contain nobel title", "la vecchia", parser.parseName("c la vecchia").lastName);
        junitStyleAssertEquals("Signature should match", "a", parser.parseName("alexei a stuchebrukhov").signature);
        junitStyleAssertEquals("Signature should match", "c", parser.parseName("c mauritsson").signature);
        junitStyleAssertEquals("Middle should match", "pa", parser.parseName("ian pa tomlinson").middleName);
        junitStyleAssertEquals("Middle name should be empty", "", parser.parseName("ian pm tomlinson").middleName);
        junitStyleAssertEquals("Signature should match", "ff",parser.parseName("f ff fransson").signature);
        junitStyleAssertEquals("Signature should match", "ff",parser.parseName("f f f fransson").signature);
        junitStyleAssertEquals("Middle should match", "pa", parser.parseName("ian pa tomlinson").middleName);
        junitStyleAssertEquals("Middle name should be empty", "", parser.parseName("ian pm tomlinson").middleName);
        junitStyleAssertEquals("Signature should match","ipm", parser.parseName("ian pm tomlinson").signature);
        junitStyleAssertEquals("Signature should match","ipm", parser.parseName("ian p m tomlinson").signature);
        junitStyleAssertEquals("Signature should match","qpm", parser.parseName("q p m tomlinson").signature);
        junitStyleAssertEquals("Middle name should be empty", "", parser.parseName("ian p m tomlinson").middleName);
        junitStyleAssertEquals("Middle name should be empty", "", parser.parseName("i p m tomlinson").middleName);
        junitStyleAssertEquals("Should not have suffix", "", parser.parseName("d v trichopoulos").suffix);
        junitStyleAssertEquals("Should have suffix", "Jr", parser.parseName("Thomas More Jr").suffix);
        junitStyleAssertEquals("Should have suffix", "V", parser.parseName("Eric von Pommen V").suffix);


//        System.out.println(parser.parseName("f f fransson"));
        for (int i=0; i<500; i ++) {
            //System.out.println(parser.parseName(Character.toChars(i)[0]+ " f MARK"));
            junitStyleAssertEquals("Should have matching last name", "mark", parser.parseName(Character.toChars(i)[0]+ " f mark").lastName);
        }


        junitStyleAssertEquals("Initials should match","QQ", parser.parseName("Q Victor Q MARK").initials);

        junitStyleAssertEquals("Signature should match", "f",parser.parseName("fredrik f fransson").signature);
        junitStyleAssertEquals("Signature should match", "f",parser.parseName("f fredrik fransson").signature);
        junitStyleAssertEquals("Signature should match", "ff",parser.parseName("franz fredrik fransson").signature);

        junitStyleAssertEquals("Nobel title not equal", "de", parser.parseName("ulf de faire").nobeltitles);
        junitStyleAssertEquals("Nobel title not equal", "dé", parser.parseName("ulf dé faire").nobeltitles);
        junitStyleAssertEquals("Middle name should be empty", "",parser.parseName("ulf de faire").middleName);
        junitStyleAssertEquals("Middle name should be empty", "",parser.parseName("ulf dé faire").middleName);
        junitStyleAssertEquals("First name should be Petter","Petter", parser.parseName("J Petter Gustavsson").firstName);
        junitStyleAssertEquals("First name should match","Eric", parser.parseName("Eric van Cutsem").firstName);
        junitStyleAssertEquals("Nobeltitle should match","van", parser.parseName("Eric van Cutsem").nobeltitles);
        junitStyleAssertEquals("Last name should match","van Cutsem", parser.parseName(" Eric  van  Cutsem ").lastName);
        // The test cases below is not valid any longer since nobel titles are now part of the last name.
        // assertEquals("Last name should match",parser.parseName("Carlo la Vecchia").lastName, parser.parseName("Carlo de la Vecchia").lastName);
        // assertEquals("Last name should match","Braun", parser.parseName("president Werner von dem Braun").lastName);

//        System.out.println(parser.parseName("Eric Van Cutsem"));
//
//        System.out.println(parser.parseName("Q Victor Q MARK"));
//        System.out.println(parser.parseName("Victor Q MARK"));
//        System.out.println(parser.parseName("Kennedy, J.F."));

        junitStyleAssertEquals("Initial should be E","E", parser.parseName("CRONAN, JOHN E.").initials);
        junitStyleAssertEquals("First name should be John","JOHN", parser.parseName("CRONAN, JOHN E.").firstName);

    }


}