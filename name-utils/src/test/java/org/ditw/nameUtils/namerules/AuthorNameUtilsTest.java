package org.ditw.nameUtils.namerules;

import org.ditw.nameUtils.nameparser.NameParser;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by hakanlofqvist on 20/04/15.
 */
@Test(groups = { "UnitTest" } )
public class AuthorNameUtilsTest {
    AuthorNameUtils authorNameUtils = new AuthorNameUtils();


    @Test
    public void testIsNameCompatible() throws Exception {
        NameParser nameParser = new NameParser();

        //Todo handle last name with "-"
        //System.out.println(nameParser.parseName("Praveen Ramakrishnan-Geethakumari"));
        //System.out.println(nameParser.parseTrialName("Chung-Hua Hsu, PHD"));


        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("d", "dlh", "donna","dd"));

        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("karl","","karl",""));
        Assert.assertTrue(authorNameUtils.isNameCompatible(nameParser.parseName("kar-ming chen").firstName,nameParser.parseName("karming chen").firstName));
        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("kar-ming","km","kar ming","km"));
        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("kar-ming","km","karming","k"));
        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("Zheng-De","zd","Zheng De","zd"));
        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("j","jj","j","jf"));
        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("den","daj","den","djv"));
        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("kenneth", "kc", "k","ke"));

        Assert.assertTrue(authorNameUtils.isNameCompatible(nameParser.parseName("a hakan lofqvist").firstName, nameParser.parseName("hakan a lofqvist").firstName));
        Assert.assertTrue(authorNameUtils.isNameCompatible(nameParser.parseName("a f hakan lofqvist").firstName, nameParser.parseName("hakan af lofqvist").firstName));
        Assert.assertTrue(authorNameUtils.isNameCompatible(nameParser.parseName("hakan von lofqvist").firstName, nameParser.parseName("hakan a lofqvist").firstName));
        Assert.assertTrue(authorNameUtils.isNameCompatible(nameParser.parseName("a hakan lofqvist jr").firstName, nameParser.parseName("hakan a lofqvist").firstName));

        Assert.assertTrue(authorNameUtils.isNameCompatible("hakan","hakan"));
        Assert.assertTrue(authorNameUtils.isNameCompatible("","hakan"));
        Assert.assertTrue(authorNameUtils.isNameCompatible("hakan", ""));

        Assert.assertFalse(authorNameUtils.isNameCompatible("christopher", "christoph"));
        Assert.assertTrue(authorNameUtils.isNameCompatible("", ""));

        Assert.assertFalse(authorNameUtils.isNameCompatible("harry", "henry"));


        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("phillip", "bp", "phillip", "bp"));
        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("do","d","doh","d"));
        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("Carlo","C","Carlo","C"));
        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("christopher","ch", "christoph","ch"));

        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("robert","rd", "robert","rm"));

        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("phillip", "p", "phillip", "py"));
        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("phillip", "pz", "phillip", "py"));

        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("r", "rs", "robert", "rs"));
        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("e", "dej", "eric", "edj"));
        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("e", "dej", "eric", "de"));

        Assert.assertFalse(authorNameUtils.isClusterNameCompatible("kenneth", "kc", "kenneth","ke"));
        Assert.assertTrue(authorNameUtils.isClusterNameCompatible("kenneth", "kc", "k","k"));


    }

    @Test
    public void testGetNamespace() throws Exception {
        String namespace = authorNameUtils.getNamespace("B Essen", "Gustavsson");
        System.out.println(namespace);

        namespace = authorNameUtils.getNamespace("Ågren","Östling");
        System.out.println(namespace);

        namespace = authorNameUtils.getNamespace(null,"Östling");
        System.out.println(namespace);

        namespace = authorNameUtils.getNamespace(" ", "b");
        System.out.println(namespace);

        namespace = authorNameUtils.getNamespace("a", "Östling");
        System.out.println(namespace);

        namespace = authorNameUtils.getNamespace("Fredrik","Bäckhed");
        System.out.println(namespace);

    }

    @Test
    public void testTokenize() throws Exception {
        List<String> tokens = authorNameUtils.tokenize("  B   Ågren  ", "BÅ","Smith");
        System.out.println(tokens);

        // Todo: Sometimes part of the last name appears as first name. Currently not handled.
        // "firstName":"B Essen","lastName":"Gustavsson"            => b,e,gustavsson
        // "firstName":"Birgitta","lastName":"Essén-Gustavsson"     => b,essen-gustavsson
        // "firstName":"B","lastName":"Essén-Gustavsson"            => b,essen-gusvasson

        tokens = authorNameUtils.tokenize("Birgitta", "B","Essén-Gustavsson");
        System.out.println(tokens);

        tokens = authorNameUtils.tokenize("B Essen", "BE","Gustavsson");
        System.out.println(tokens);

        tokens = authorNameUtils.tokenize(null, null, "Smith");
        System.out.println(tokens);

        tokens = authorNameUtils.tokenize("S", null, "Smith");
        System.out.println(tokens);

        tokens = authorNameUtils.tokenize(null, "S", "Smith");
        System.out.println(tokens);

        tokens = authorNameUtils.tokenize("Pier Giorgio", "PG", "Righetti");
        System.out.println(tokens);

        tokens = authorNameUtils.tokenize("P G", "PG", "Righetti");
        System.out.println(tokens);




    }

    @Test
    public void testGetInitials() throws Exception {
        List<String> tokens = authorNameUtils.getInitials("Birgitta", "B","Essén-Gustavsson");
        System.out.println(tokens);

        tokens = authorNameUtils.getInitials("B Essen", "BE","Gustavsson");
        System.out.println(tokens);


        tokens = authorNameUtils.getInitials("Pier Giorgio", null, "-");
        System.out.println(tokens);

        tokens = authorNameUtils.getInitials("  ", null, "-");
        System.out.println(tokens);

        tokens = authorNameUtils.getInitials("zulfiqar ahmed", null, "-");
        System.out.println(tokens);
    }

    @Test
    public void testHaveSameEmail() throws Exception {
        String testDate = "2015-10-10";
        Assert.assertEquals("2015",testDate.substring(0,4), "Should match");

        Assert.assertFalse(authorNameUtils.haveSameEmail(
                Arrays.asList("test@test.com").stream().collect(Collectors.toSet()),
                Arrays.asList("testB@test.com").stream().collect(Collectors.toSet())
        ));

        Assert.assertTrue(authorNameUtils.haveSameEmail(
                Arrays.asList("test@test.com","testB@test.com").stream().collect(Collectors.toSet()),
                Arrays.asList("testB@test.com").stream().collect(Collectors.toSet())
        ));


        Long c = 999_999L;

        System.out.println(c % 1_000_000);

    }
}