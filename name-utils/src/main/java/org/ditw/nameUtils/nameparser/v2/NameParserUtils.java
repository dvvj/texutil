package org.ditw.nameUtils.nameparser.v2;

import org.ditw.nameUtils.nameparser.NameParserHelpers;
import org.ditw.nameUtils.nameparser.NamesProcessed;
import org.ditw.nameUtils.namerules.AuthorNameUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by dele on 2017-03-30.
 */
public class NameParserUtils {
  public final static String EmptyName = "";
  protected final static String emptyNameIfNull(String s) {
    if (s == null) return EmptyName;
    else return s;
  }

  protected final static List<String> EmptyNobles = new LinkedList<>();


  public static String parseOptional(Map<String,String> inMap, String inField) {
    if (inMap.containsKey(inField)) {
      return inMap.get(inField);
    }
    else {
      return EmptyName;
    }
  }

  protected static List<String> findFromList(List<String> list2Check, String[] parts, List<String> foundList) {
    List<String> rem = new LinkedList<>();
    for (int i = 0; i < parts.length; i ++) {
      if (list2Check.contains(parts[i].toLowerCase())) {
        foundList.add(parts[i]);
      }
      else {
        rem.add(parts[i]);
      }
    }
    return rem;
  }

  protected static String combineStrings(List<String> parts) {
    StringBuilder b = new StringBuilder();
    for (String p : parts) {
      if (b.length() > 0) b.append(' ');
      b.append(p);
    }
    return b.toString();
  }

  public static void parseLastNamePart(String part, ParsedName parsed) {
    String[] subParts = splitParts(part);
    if (subParts.length > 1) {
      String lastPart = subParts[subParts.length-1].toLowerCase();
      String[] remainingParts;
      if (NameParserHelpers.Suffixes.contains(lastPart)) {
        parsed.updateSuffix(lastPart);
        remainingParts = Arrays.copyOfRange(subParts, 0, subParts.length-1);
        String rem = combineStrings(Arrays.asList(remainingParts));
        parsed.setLastName4Namespace(rem);
      }
      else {
        remainingParts = subParts;
      }
      if (remainingParts.length > 1) {
        List<String> nobles = new LinkedList<>();
        List<String> rem = findFromList(NameParserHelpers.Nobles, remainingParts, nobles);
        if (!nobles.isEmpty()) {
          parsed.updateNobles(nobles);
          // nobles are kept while building namespaces
          //parsed.setLastName4Namespace(rem);
        }
      }
      //List<String> suffixes = new LinkedList<>();
      //rem = findFromList(Suffixes, subParts, suffixes);
    }
    /*
    else if (subParts.length == 1 && subParts[0].length() > 0) {
      return subParts[0];
    }
    else {
      return EmptyName;
    }
    */
  }


  private static final Pattern PartsPtn = Pattern.compile("[\\s\\t.,\\-]+");
  public static String[] splitParts(String n) {
    return PartsPtn.split(n);
  }
  private static final Pattern UnitsPtn = Pattern.compile("[\\s\\t.,]+");
  public static String[] splitUnits(String n) {
    return UnitsPtn.split(n);
  }
  protected static Pattern _exInitialPtn = Pattern.compile("[A-Z][a-z]");

  protected static String getInitials(String instr, int startIndex) {
    String[] parts = splitParts(instr);
    if (parts.length > startIndex) {
      String r = "";
      for (int i = startIndex; i < parts.length; i++) {
        if (_exInitialPtn.matcher(parts[i]).matches()) {
          r += parts[i];
        }
        else {
          if (parts[i].length() > 0)
            r += parts[i].charAt(0);
        }
      }
      return r;
    }
    return EmptyName;
  }

  protected static String get1stUnit(String inName) {
    String[] units = splitUnits(inName);
    if (units.length > 0) return units[0];
    else return EmptyName;
  }

  protected static String getMidName(String inName) {

    String[] parts = splitUnits(inName);
    if (parts.length > 1) {
      List<String> midParts = Arrays.asList(parts).subList(1, parts.length);
      return concatStrings(midParts);
      //return parts[0];
    }
    else return EmptyName;

  }

  private static String caseChange(String input, boolean toLower) {
    if (toLower) return input.toLowerCase();
    else return input;
  }


  public static String getSourceFromMedlineInOldFormat(ParsedName pn) {
    String foreName = NameParserUtils.parseOptional(pn.sourceMap, MedlineNameParser.ForeName);
    return NameParserHelpers.buildSourceName(
      foreName, pn.getLastName(), pn.getSuffixes()
    );
  }

  public static String getMiddleNameFromMedlineInOldFormat(ParsedName pn) {
    return pn.getMiddleName();
    //return (pn.getMiddleName().length() < 3) ? EmptyName : pn.getMiddleName();
  }

  public static String getUpperCaseSignature(String s) {
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      if (Character.isUpperCase(s.charAt(i))) b.append(s.charAt(i));
    }
    return b.toString();
  }

  public static NamesProcessed parsedName2NamesProcessed(ParsedName pn, boolean toLower) {
    NamesProcessed r = new NamesProcessed();

    r.lastName = caseChange(pn.getLastName4Namespace(), toLower);
    r.firstName = caseChange(pn.getFirstName(), toLower);
    r.middleName = caseChange(getMiddleNameFromMedlineInOldFormat(pn), toLower);
    r.suffix = caseChange(pn.getSuffixes(), toLower);
    r.nobeltitles = caseChange(pn.getNobles(), toLower);
    // todo: only applies to medline data now
    r.source = caseChange(getSourceFromMedlineInOldFormat(pn), toLower);

    String uppercaseSignature = getUpperCaseSignature(pn.getSignature());
    r.signature = caseChange(uppercaseSignature, toLower);

    return r;
  }

  public static String concatStrings(Iterable<String> strs, String delim) {
    StringBuilder builder = new StringBuilder();
    for(String s : strs) {
      if (builder.length() > 0) builder.append(delim);
      builder.append(s);
    }
    return builder.toString();
  }

  public static String concatStrings(Iterable<String> strs) {
    return concatStrings(strs, " ");
  }


  public static String getNs(NamesProcessed np) {
    String fn = AuthorNameUtils.clean(np.firstName);
    String ln = AuthorNameUtils.clean(np.lastName);

    return AuthorNameUtils.getNamespace(fn, ln);
  }

  // this is used mostly for tests
  private static final Pattern _ptn = Pattern.compile("\\|");
  private static final Pattern _partsPtn = Pattern.compile("\\:");
  private static String getValuePart(String kv) {
    String[] p = _partsPtn.split(kv);
    if (p.length < 2) return EmptyName;
    else return p[1];
  }
  public static NamesProcessed parse(String compactInput) {
    NamesProcessed r = new NamesProcessed();
    String[] parts = _ptn.split(compactInput);
    try {
      int idx = 0;
      r.source = parts[idx++];
      r.prefix = getValuePart(parts[idx++]);
      r.firstName = getValuePart(parts[idx++]);
      r.middleName = getValuePart(parts[idx++]);
      r.lastName = getValuePart(parts[idx++]);
      r.suffix = getValuePart(parts[idx++]);
      r.nobeltitles = getValuePart(parts[idx++]);
      //r.initials = parts[idx++];
      //r.swapped = parts[idx++];
      //r.sortedNames = parts[idx++];
      r.signature = getValuePart(parts[idx++]);
      /*
      if (parts.length > idx) {
        r.asciifiedLastname = parts[idx++];
      }
      else {
        r.asciifiedLastname = "";
      }
      */
    }
    catch (Exception ex) {
      throw new IllegalArgumentException(
        String.format("Failed to parse [%s], exception: %s", compactInput, ex.getMessage())
      );
    }
    return r;
  }


}
