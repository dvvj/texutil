package org.ditw.nameUtils.nameparser.v2;

import java.util.Map;

/**
 * Created by dele on 2017-03-28.
 */
class GrantNameParser extends NameParserBase {

  private GrantNameParser() { }
  public ParsedName tryParse(Map<String,String> inMap) {
    ParsedName pn = new ParsedName(inMap);
    // todo
    return pn;
  }

  protected final static NameParserBase inst = new GrantNameParser();
}
