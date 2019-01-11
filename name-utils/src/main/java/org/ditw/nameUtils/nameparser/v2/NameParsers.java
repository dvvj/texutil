package org.ditw.nameUtils.nameparser.v2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dele on 2017-03-28.
 */
public class NameParsers {
  private final static String MedLine = "MedLine";
  private final static String Grant = "Grant";
  private final static String Payment = "Payment";
  private final static String Trial = "Trial";
  private static Map<String, NameParserBase> createParserMap() {
    Map<String, NameParserBase> m = new HashMap<>();
    m.put(MedLine, MedlineNameParser.inst);
    m.put(Grant, GrantNameParser.inst);
    m.put(Payment, PaymentNameParser.inst);
    m.put(Trial, TrialNameParser.inst);
    return m;
  }
  private static final Map<String, NameParserBase> parserMap = createParserMap();

  public static NameParserBase medLineParser = parserMap.get(MedLine);
}
