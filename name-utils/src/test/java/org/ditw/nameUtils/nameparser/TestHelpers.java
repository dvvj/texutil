package org.ditw.nameUtils.nameparser;

import org.ditw.nameUtils.nameparser.v2.ParsedName;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.testng.Assert.assertEquals;

/**
 * Created by dele on 2017-03-23.
 */
public class TestHelpers {
  public static HashMap<String, String> hashMap(String[][] pairs) {
    HashMap<String, String> r = new LinkedHashMap<>(pairs.length);
    for (int i = 0; i < pairs.length; i++) {
      r.put(pairs[i][0], pairs[i][1]);
    }
    return r;
  }

  public static void assertEquality(NamesProcessed n1, NamesProcessed n2) {
    assertEquals(n1.source, n2.source);
    assertEquals(n1.prefix, n2.prefix);
    assertEquals(n1.firstName, n2.firstName);
    assertEquals(n1.middleName, n2.middleName);
    assertEquals(n1.lastName, n2.lastName);
    assertEquals(n1.suffix, n2.suffix);
    assertEquals(n1.nobeltitles, n2.nobeltitles);
    assertEquals(n1.initials, n2.initials);
    //assertEquals(n1.swapped, n2.swapped);
    //assertEquals(n1.sortedNames, n2.sortedNames);
    assertEquals(n1.signature, n2.signature);
    //assertEquals(n1.asciifiedLastname, n2.asciifiedLastname);
  }

  public final static NameParser _nameParser = new NameParser();

  public static String compactParsedName(ParsedName pn) {
    return String.format("%s|p:%s|f:%s|m:%s|l:%s|sf:%s|n:%s|sg:%s",
      pn.getSource(), pn.getPrefix(), pn.getFirstName(), pn.getMiddleName(), pn.getLastName(),
      pn.getSuffixes(), pn.getNobles(), //np.sortedNames,
      pn.getSignature() //, np.asciifiedLastname
    );
  }

}
