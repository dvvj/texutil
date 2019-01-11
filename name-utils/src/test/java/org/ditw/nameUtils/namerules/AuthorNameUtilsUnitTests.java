package org.ditw.nameUtils.namerules;


import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by dele on 2017-04-03.
 */
@Test(groups = { "UnitTest" } )
public class AuthorNameUtilsUnitTests {
  @DataProvider(name = "namespaceTestData")
  public static Object[][] namespaceTestData() {
    return new Object[][] {
      { new String[] {"B Essen", "Gustavsson"}, "gustavsson|b" },
      { new String[] {"Ågren", "Östling"}, "ostling|a" },
      { new String[] {" ", "b"}, AuthorNameUtils.InvalidNameSpace },
      { new String[] {null,"Östling"}, AuthorNameUtils.InvalidNameSpace },
      { new String[] {"Fredrik","Bäckhed"}, "backhed|f" },
    };
  }

  @Test(dataProvider = "namespaceTestData")
  public void testNamespace(String[] flnames, String expected) {
    String ns = AuthorNameUtils.getNamespace(flnames[0], flnames[1]);
    assertEquals(ns, expected);
  }
}
