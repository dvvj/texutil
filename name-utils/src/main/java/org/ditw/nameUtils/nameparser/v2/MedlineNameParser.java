package org.ditw.nameUtils.nameparser.v2;

import java.util.Map;

import static org.ditw.nameUtils.nameparser.v2.NameParserUtils.*;

/**
 * Created by dele on 2017-03-27.
 */
public class MedlineNameParser extends NameParserBase {

  private MedlineNameParser() { }

  public static final String LastName = "LastName";
  public static final String ForeName = "ForeName";
  public static final String Initials = "Initials";
  public static final String Suffix = "Suffix";
  public static final String CollectiveName = "CollectiveName";

  protected static String getOldInitials(String foreName) {
    return getInitials(foreName, 1);
  }
  public ParsedName tryParse(Map<String,String> inMap) {
    if (!inMap.containsKey(LastName)) {
      if (!inMap.containsKey(CollectiveName))
        throw new IllegalArgumentException("LastName/CollectiveName missing, inMap: " + inMap.toString());
      else {
        System.out.println("CollectiveName: " + inMap.get(CollectiveName));
        return null;
      }
    }
    ParsedName pn = new ParsedName(inMap);

    String lastName = inMap.get(LastName);
    pn.setLastName(lastName);
    pn.setLastName4Namespace(lastName);

    parseLastNamePart(lastName, pn);
    String foreName = parseOptional(inMap, ForeName);
    pn.setFirstName(get1stUnit(foreName));
    pn.setMiddleName(getMidName(foreName));
    //pn.setInitials();
    String suffix = parseOptional(inMap, Suffix);
    if (!suffix.isEmpty()) {
      pn.updateSuffix(suffix);
    }
    pn.setSignature(getInitials(foreName, 0));
    /*
    NamesProcessed r = new NamesProcessed(inMap);
    String lastName = inMap.get(LastName);
    r.lastName = lastName;
    r.lastNameWoNobles = parseNamePart(lastName, r);
    String foreName = parseOptional(inMap, ForeName);
    r.firstName = get1stUnitOptional(inMap, ForeName);
    r.initials = getOldInitials(foreName);
    r.suffix = parseOptional(inMap, Suffix);
    r.source = NameParserHelpers.buildSourceName(foreName, lastName, r.suffix);
    r.signature = getInitials(foreName, 0);
    r.asciifiedLastname = r.lastName;
    r.sortedNames = r.firstName + " ";
    */
    return pn;
  }

  protected final static NameParserBase inst = new MedlineNameParser();
}
