package org.ditw.nameUtils.nameparser.v2;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by dele on 2017-03-27.
 */
public abstract class NameParserBase implements Serializable {
  public abstract ParsedName tryParse(Map<String,String> inMap);

  protected static String parseLastName(String in) {
    return in;
  }

}
