package org.ditw.nameUtils.nameparser;

import org.ditw.nameUtils.nameParser.ParserHelpers;


import java.io.*;
import java.util.*;

/**
 * Created by dele on 2017-03-28.
 */
public class NameParserHelpers implements Serializable {

  public static String parseOrcid(String raw) {
    String orcid = raw.replaceAll("-","");
    if (orcid.startsWith("http://orcid.org/")) {
      orcid = orcid.substring(17,orcid.length());
    }

    return orcid;
  }

  private static final List<NamesProcessed> EmptyProcessedList = new LinkedList<>();
  private static final NameParser nameParser = new NameParser();

  public static String buildSourceName(String firstName, String lastName, String suffix) {
    String fullName = null;
    if (firstName != null && !firstName.isEmpty())
      fullName = firstName + " " + lastName;
    else
      fullName = lastName;

    if (suffix != null && !suffix.isEmpty())
      fullName += " " + suffix;
    return fullName;
  }

  public static String normalize(String str) {
    return str.replace(".", "").replace(",", "").trim().toLowerCase();
  }

  /**
   * read a bunch of files return them in order: longest string first
   *
   * @param fileNames
   * @param minimumLength
   * @param excludes
   * @return
   */
  public static List<String> loadWordListFromResource(String[] fileNames, int minimumLength, Set<String> excludes) {

    List<String> wrkList = new ArrayList<>();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    for (String fileName : fileNames) {
      InputStream is = classLoader.getResourceAsStream(fileName);
      if (is != null) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
          while ((line = in.readLine()) != null) {
            String wrk = line.trim();
            if (wrk.length() >= minimumLength) {
              if (!excludes.contains(wrk)) {
                wrkList.add(normalize(wrk));
              }
            }
          }
          in.close();
          is.close();
        } catch (IOException e) {
          if (in != null) {
            try {
              in.close();
            } catch (IOException ioe) {
              // ok
            }
          }
          if (is != null) {
            try {
              is.close();
            } catch (IOException ioe) {
              // ok
            }
          }
        }
      }
    }
    String[] array = wrkList.toArray(new String[wrkList.size()]);
    Arrays.sort(array, new ComparatorStringLength());
    List<String> ret = Arrays.asList(array);
    return ret;
  }

  private static Set<String> _academicRanks() {

    //Todo: Maybe we should require the right case for some, since they may be names as well.

    Set<String> results = new HashSet<>();
    results.add("be");
    results.add("ma");
    results.add("MD".toLowerCase());
    results.add("PhD".toLowerCase());
    results.add("Dr".toLowerCase());
    results.add("Ph".toLowerCase());
    results.add("Site".toLowerCase());
    results.add("Prof".toLowerCase());
    results.add("Investigator".toLowerCase());
    results.add("ID".toLowerCase());
    results.add("MPH".toLowerCase());
    results.add("RN".toLowerCase());
    results.add("Professor".toLowerCase());
    results.add("MSc".toLowerCase());
    results.add("MS".toLowerCase());
    results.add("MBBS".toLowerCase());
    results.add("Trial".toLowerCase());
    results.add("Doctor".toLowerCase());
    results.add("med".toLowerCase());
    results.add("PHD".toLowerCase());
    results.add("BS".toLowerCase());
    results.add("UCB".toLowerCase());
    results.add("DMD".toLowerCase());
    results.add("DrMedDent".toLowerCase());
    results.add("BDS".toLowerCase());
    results.add("DO".toLowerCase());
    results.add("PA".toLowerCase());
    results.add("NP".toLowerCase());
    results.add("BA".toLowerCase());
    results.add("Pharm".toLowerCase());
    results.add("MBA".toLowerCase());
    results.add("FRCPCH".toLowerCase());
    results.add("Program".toLowerCase());
    results.add("Director".toLowerCase());
    results.add("MSC".toLowerCase());
    results.add("PT".toLowerCase());
    results.add("DPM".toLowerCase());
    results.add("Contact".toLowerCase());
    results.add("Person".toLowerCase());
    results.add("MSCI".toLowerCase());
    results.add("dr".toLowerCase());
    results.add("MRCP".toLowerCase());
    results.add("ChB".toLowerCase());
    results.add("RPh".toLowerCase());
    results.add("pharmd");
    results.add("master");
    results.add("ccrp");
    results.add("dc");
    results.add("ltd");
    results.add("frcs");
    results.add("bsn");
    results.add("lector");
    results.add("aggregate");
    results.add("mb");
    results.add("bmbs");
    results.add("global");
    results.add("bsc");
    results.add("ccrc");
    results.add("frcp");
    results.add("frcr");
    results.add("advisor");
    results.add("MBChB".toLowerCase());
    return results;
  }

  public final static List<String> Prefixes;
  public final static List<String> Suffixes;
  public final static List<String> Nobles;
  public final static Set<String> AcademicRanks;

  public final static HashSet<Character> VowelSet;

  public final static Set<String> Excludes;
  private final static Set<String> EmptyExcludes = new HashSet<>(); // todo: useless
  static {
    Prefixes = loadWordListFromResource(
      new String[] {"nameparser/prefixes.txt"}, 1, EmptyExcludes
    );
    Suffixes = loadWordListFromResource(
      new String[] {"nameparser/suffixes.txt"}, 1, EmptyExcludes
    );
    Nobles = loadWordListFromResource(
      new String[] {"nameparser/nobeltitles.txt"}, 1, EmptyExcludes
    );

    int size = Prefixes.size() + Suffixes.size() + Nobles.size();
    Excludes = new HashSet<>(size);
    Excludes.addAll(Prefixes);
    Excludes.addAll(Suffixes);
    Excludes.addAll(Nobles);

    VowelSet = new HashSet<>();
    VowelSet.addAll(Arrays.asList('a','e','i','o','u','y'));

    AcademicRanks = _academicRanks();
  }

  private static String _encPipe(String pstr) {
    return pstr.replace("|", "$");
  }

  public static String compact(NamesProcessed np) {
    return String.format("%s|p:%s|f:%s|m:%s|l:%s|sf:%s|n:%s|sg:%s\n",
      _encPipe(np.source), np.prefix,
      _encPipe(np.firstName), _encPipe(np.middleName), _encPipe(np.lastName),
      np.suffix, np.nobeltitles, //np.sortedNames,
      np.signature//, np.asciifiedLastname
    );
  }

  private static final Set<String> IgnorePrefix = null;

  private final static String SerMapKey_LastName = "ln";
  private final static String SerMapKey_ForeName = "fn";
  private final static String SerMapKey_Initials = "in";
  private final static String SerMapKey_Suffix = "su";
  private final static String SerMapKey_CollectiveName = "cn";

}
