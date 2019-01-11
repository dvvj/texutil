package org.ditw.nameUtils.nameparser.v2;

import java.util.Map;

/**
 * Created by dele on 2017-03-28.
 */
class PaymentNameParser extends NameParserBase {

  private PaymentNameParser() { }

  private static final String LastName = "Physician_Last_Name";
  private static final String FirstName = "Physician_First_Name";
  private static final String MiddleName = "Physician_Middle_Name";

  public ParsedName tryParse(Map<String,String> inMap) {
    if (!inMap.containsKey(LastName)) throw new IllegalArgumentException("LastName missing, inMap: " + inMap.toString());
    ParsedName pn = new ParsedName(inMap);
    /*
    r.lastName = inMap.get(LastName);
    r.firstName = parseOptional(inMap, FirstName);
    r.middleName = parseOptional(inMap, MiddleName);
    */
    return pn;
  }

  protected final static NameParserBase inst = new PaymentNameParser();
}
