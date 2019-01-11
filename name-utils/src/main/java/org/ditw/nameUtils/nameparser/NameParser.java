package org.ditw.nameUtils.nameparser;


import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ditw.nameUtils.nameparser.NameParserHelpers.*;

/**
 * Created by thomasfanto on 03/11/15.
 */
public class NameParser implements Serializable {

    private static String original = "(?<=[., ])";
    String test = "(?<=[|., ])";

    private static final Pattern originalSpitPattern = Pattern.compile(original);

    public NameParser() {
    }

    /**
     * common control format
     *
     * @param str
     * @return
     */

    private static String  normalizeAndSort(String str){

        String ret = "";

        String[] splited = originalSpitPattern.split(str);

        List<String> parts = new ArrayList<>();
        parts.addAll(Arrays.asList(splited));

        // remove empty parts
        for (int x = splited.length - 1; x >= 0; x--) {
            if (splited[x].trim().isEmpty()) {
                parts.remove(x);
            }
        }

        Set<String> onlyOne = new HashSet<>();
        for(String part : parts) {
            onlyOne.add(normalize(part));
        }

        String[] array = onlyOne.toArray(new String[onlyOne.size()]);
        Arrays.sort(array);
        List<String> ar = Arrays.asList(array);
        for(String s : ar){
            ret += s + " ";
        }
        return ret;
    }

    private static String  signature(String fromNames, String initials){

        StringBuilder resultBuf = new StringBuilder(2);
        originalSpitPattern.splitAsStream(fromNames)
                //.peek(part-> System.out.println("peek1: " + part))
                .flatMap(part -> Stream.of(part.split("-")))
                //.peek(part-> System.out.println("peek2: " + part))
                .filter(part -> !part.trim().isEmpty())
                .map(part -> part.charAt(0))
                .filter(s -> !String.valueOf(s).trim().isEmpty())
                .forEach(firstCharacter -> resultBuf.append(firstCharacter));
        boolean haveSkpped = false;
        for (int i = 0; i<initials.length(); i++) {
            if (resultBuf.toString().contains(String.valueOf(initials.charAt(i))) && haveSkpped == false) {
                haveSkpped = true;
            } else {
                resultBuf.append(initials.charAt(i));
            }
        }

        /*String ret = "";

        // first create initials from names
        String[] splitedFromNames = fromNames.split(original);
        List<String> partsFromNames = new ArrayList<>();
        partsFromNames.addAll(Arrays.asList(splitedFromNames));
        // remove empty parts
        for (int x = splitedFromNames.length - 1; x >= 0; x--) {
            if (splitedFromNames[x].trim().isEmpty()) {
                partsFromNames.remove(x);
            }
        }
        StringBuilder resultBuf = new StringBuilder(2);
        for(String part : partsFromNames) {
            resultBuf.append(String.valueOf(part.charAt(0)));
        }

        // then process and normalize initiials from the input initials
        if(initials != null){
            // String wrk = initials.toLowerCase();
            String wrk = initials;
            wrk = wrk.trim();
            String[] splitedFromInitials = wrk.split(original);
            List<String> partsFromInitials = new ArrayList<>();
            partsFromInitials.addAll(Arrays.asList(splitedFromInitials));
            for (int x = splitedFromInitials.length - 1; x >= 0; x--) {
                if (splitedFromInitials[x].trim().isEmpty()) {
                    partsFromInitials.remove(x);
                }
            }

            for(String part : partsFromInitials) {
                resultBuf.append(String.valueOf(part.charAt(0)));
            }

        }*/

        return resultBuf.toString();

    }


    /**
     * main parse function
     *
     * @param input
     * @return
     */
    public static NamesProcessed parseName(String input) {

        if (input.endsWith(",") || input.endsWith(".")) {
            input = input.substring(0, input.length()-1);
        }

        StringBuilder nameWoAcademicRanks = new StringBuilder();
        String[] parts = originalSpitPattern.split(input);

        for (int i = 0; i<parts.length; i++) {
            if (i<2) {
                nameWoAcademicRanks.append(parts[i]);
            } else {
                if (!AcademicRanks.contains(parts[i])) {
                    nameWoAcademicRanks.append(parts[i]);
                }
            }
        }
        NamesProcessed name1 = parseNameStep1(nameWoAcademicRanks.toString());
        NamesProcessed name2 = parseNameStep2(name1);
        return name2;
    }

    public final static List<String> CommonAcademicRankTrash =
        Arrays.asList(
            "M.D. Anderson Cancer".toLowerCase(),
            "Medical Director".toLowerCase(),
            "Bayer Study Director".toLowerCase(),
            "Global Clinical Registry".toLowerCase(),
            "For more information".toLowerCase(),
            "Study Coordinator".toLowerCase(),
            "Novartis Pharmaceuticals".toLowerCase(),
            "Janssen Research".toLowerCase(),
            "Call Center".toLowerCase(),
            "Research Coordinator".toLowerCase(),
            "Medical Director Clinical Science".toLowerCase(),
            "Pfizer".toLowerCase(),
            "UCB Pharma".toLowerCase(),
            "AstraZeneca".toLowerCase()
        );

    public static boolean containsAcademicTitleTrash(String sourceSegment) {
        String normalized = normalize(sourceSegment);

        for (String trash : CommonAcademicRankTrash) {
            if (normalized.contains(trash)) return true;
        }

        return false;
    }

    public boolean isNameAndAcademicRank(String sourceSegment) {
        if (sourceSegment == null) return false;
        if (containsAcademicTitleTrash(sourceSegment)) return false;

        String normalized = normalize(sourceSegment);

        String[] parts = originalSpitPattern.split(normalized);

        Boolean foundOnePartThatIsLongAndNotAcademicRank = false;
        Boolean foundOnePartThatIsAcademicRank = false;

        for (String part : parts) {
            if (part.length()>1) {
                if (AcademicRanks.contains(part.trim())) {
                    foundOnePartThatIsAcademicRank = true;
                } else if (!foundOnePartThatIsLongAndNotAcademicRank) {
                    if (part.length() > 4) {
                        foundOnePartThatIsLongAndNotAcademicRank = true;
                    }
                }
            }
        }
        return foundOnePartThatIsLongAndNotAcademicRank && foundOnePartThatIsAcademicRank;
    }

    public static Optional<NamesProcessed> parseTrialName(String input) {

        // should NOT call clean() method here: it removes '-' in name parts.
        //String cleanInput = clean(normalize(input));
        String cleanInput = normalize(input);
        String name = originalSpitPattern.splitAsStream(cleanInput)
                .filter(part -> !AcademicRanks.contains(part.trim()))
                .collect(Collectors.joining(" "));

        if (name.length() > 0) {
            NamesProcessed np = parseName(name);
            return Optional.of(np);
        }

        return Optional.empty();
    }

    //******************
    //***
    //*******************


    private static String clean(String s) {
        if (s != null) {
            return asciify(s.trim().toLowerCase());
        } else {
            return "";
        }
    }

    public static String asciify(String s) {
        char[] c = s.toCharArray();
        StringBuffer b = new StringBuffer();
        for (char element : c) {
            b.append(translate(element));
        }
        return b.toString();
    }

    /**
     * Translate the given unicode char in the closest ASCII representation
     * NOTE: this function deals only with latin-1 supplement and latin-1 extended code charts
     */
    private static char translate(char c) {
        switch(c) {
            case '\u00C0':
            case '\u00C1':
            case '\u00C2':
            case '\u00C3':
            case '\u00C4':
            case '\u00C5':
            case '\u00E0':
            case '\u00E1':
            case '\u00E2':
            case '\u00E3':
            case '\u00E4':
            case '\u00E5':
            case '\u0100':
            case '\u0101':
            case '\u0102':
            case '\u0103':
            case '\u0104':
            case '\u0105':
            case 'ǎ':
            case 'ӓ':
                return 'a';
            case '\u00C7':
            case '\u00E7':
            case '\u0106':
            case '\u0107':
            case '\u0108':
            case '\u0109':
            case '\u010A':
            case '\u010B':
            case '\u010C':
            case '\u010D':
                return 'c';
            case '\u00D0':
            case '\u00F0':
            case '\u010E':
            case '\u010F':
            case '\u0110':
            case '\u0111':
                return 'd';
            case '\u00C8':
            case '\u00C9':
            case '\u00CA':
            case '\u00CB':
            case '\u00E8':
            case '\u00E9':
            case '\u00EA':
            case '\u00EB':
            case '\u0112':
            case '\u0113':
            case '\u0114':
            case '\u0115':
            case '\u0116':
            case '\u0117':
            case '\u0118':
            case '\u0119':
            case '\u011A':
            case '\u011B':
            case 'ẻ':
            case 'ȩ':
                return 'e';
            case '\u011C':
            case '\u011D':
            case '\u011E':
            case '\u011F':
            case '\u0120':
            case '\u0121':
            case '\u0122':
            case '\u0123':
            case 'ǧ':
                return 'g';
            case '\u0124':
            case '\u0125':
            case '\u0126':
            case '\u0127':
                return 'h';
            case '\u00CC':
            case '\u00CD':
            case '\u00CE':
            case '\u00CF':
            case '\u00EC':
            case '\u00ED':
            case '\u00EE':
            case '\u00EF':
            case '\u0128':
            case '\u0129':
            case '\u012A':
            case '\u012B':
            case '\u012C':
            case '\u012D':
            case '\u012E':
            case '\u012F':
            case '\u0130':
            case '\u0131':
            case 'ι':
            case 'ί':
            case 'ї':
                return 'i';
            case '\u0134':
            case '\u0135':
                return 'j';
            case '\u0136':
            case '\u0137':
            case '\u0138':
                return 'k';
            case '\u0139':
            case '\u013A':
            case '\u013B':
            case '\u013C':
            case '\u013D':
            case '\u013E':
            case '\u013F':
            case '\u0140':
            case '\u0141':
            case '\u0142':
            case 'ɬ':
                return 'l';
            case '\u00D1':
            case '\u00F1':
            case '\u0143':
            case '\u0144':
            case '\u0145':
            case '\u0146':
            case '\u0147':
            case '\u0148':
            case '\u0149':
            case '\u014A':
            case '\u014B':
                return 'n';
            case '\u00D2':
            case '\u00D3':
            case '\u00D4':
            case '\u00D5':
            case '\u00D6':
            case '\u00D8':
            case '\u00F2':
            case '\u00F3':
            case '\u00F4':
            case '\u00F5':
            case '\u00F6':
            case '\u00F8':
            case '\u014C':
            case '\u014D':
            case '\u014E':
            case '\u014F':
            case '\u0150':
            case '\u0151':
            case 'ӧ':
            case 'ό':
                return 'o';
            case '\u0154':
            case '\u0155':
            case '\u0156':
            case '\u0157':
            case '\u0158':
            case '\u0159':
                return 'r';
            case '\u015A':
            case '\u015B':
            case '\u015C':
            case '\u015D':
            case '\u015E':
            case '\u015F':
            case '\u0160':
            case '\u0161':
            case '\u017F':
            case 'ș':
            case 'ṣ':
                return 's';
            case '\u0162':
            case '\u0163':
            case '\u0164':
            case '\u0165':
            case '\u0166':
            case '\u0167':
            case 'ț':
                return 't';
            case '\u00D9':
            case '\u00DA':
            case '\u00DB':
            case '\u00DC':
            case '\u00F9':
            case '\u00FA':
            case '\u00FB':
            case '\u00FC':
            case '\u0168':
            case '\u0169':
            case '\u016A':
            case '\u016B':
            case '\u016C':
            case '\u016D':
            case '\u016E':
            case '\u016F':
            case '\u0170':
            case '\u0171':
            case '\u0172':
            case '\u0173':
            case 'ϋ':
                return 'u';
            case '\u0174':
            case '\u0175':
                return 'w';
            case '\u00DD':
            case '\u00FD':
            case '\u00FF':
            case '\u0176':
            case '\u0177':
            case '\u0178':
                return 'y';
            case '\u0179':
            case '\u017A':
            case '\u017B':
            case '\u017C':
            case '\u017D':
            case '\u017E':
                return 'z';
            case 'ʾ':
            case 'ʼ':
                return '\'';
            case '-':
                return ' ';
        }
        return c;
    }


    //******************
    //***
    //*******************




    /** fix all names ony once and in alfabetic order
     *  fix all initials once and in alfabetic order
     *
     * @param name
     * @return
     */
    private static NamesProcessed parseNameStep2(NamesProcessed name) {


        if (name.firstName.length() == 1 && name.initials.length()>0) {
            //remove the first name character from initials
            StringBuilder validInitals = new StringBuilder();
            boolean firstRemoved = false;
            for (int i=0; i<name.initials.length(); i++) {
                if (firstRemoved) {
                    validInitals.append(name.initials.charAt(i));
                } else {
                    if (name.initials.charAt(i) == name.firstName.charAt(0)) {
                        firstRemoved = true;
                    } else {
                        validInitals.append(name.initials.charAt(i));
                    }
                }

            }
            name.initials=validInitals.toString();
        }
        String allNamesStr = name.firstName + " " + name.middleName;
        String signature = signature(allNamesStr,name.initials);

        String sortedNames = normalizeAndSort(allNamesStr);
        //String sortedSignature = normalizeAndSort(signature);
        //name.signature = (sortedSignature == null ? "" :  sortedSignature.trim());
        StringBuilder signatureSorterd = new StringBuilder(signature.length());
        Arrays.asList(signature.toCharArray()).stream().sorted().forEach(c -> signatureSorterd.append(c));
        name.signature = signatureSorterd.toString();
        //name.sortedNames = sortedNames;
        //name.asciifiedLastname = clean(name.lastName);

        return fix2(name);

    }


    /** if firstname is blank try to get first letter in originalstring and put in firstnameField
     *
     * @param res
     * @param originalString
     * @return
     */
    private static NamesProcessed fix1(NamesProcessed res, String originalString){
        if(originalString == null) return res;
        if(originalString.length() < 1) return res;
        if(res.firstName.length() < 1){
            res.firstName = originalString.substring(0, 1);
        }
        return res;
    }

    /** break at first - in firstname
     *
     * @param np
     * @return
     */
    private static NamesProcessed fix2(NamesProcessed np){
        if(np.firstName.length() > 4) {
            if (np.nobeltitles != null && np.nobeltitles.length()>0) {
                np.lastName = np.nobeltitles + " " + np.lastName;
            }
            return np;
        }

        np.firstName = np.firstName.replace(" ","");
        int pos = np.firstName.indexOf("-");
        if(pos >= 0){
            np.firstName = np.firstName.substring(pos);
        }
        if (np.nobeltitles != null && np.nobeltitles.length()>0) {
            np.lastName = np.nobeltitles + " " + np.lastName;
        }
        return np;
    }

    /**
     *
     * @param str
     * @return
     */
    private static final String[] forbidden = {"-","+","/",};
    private static String cleanItFromCrap(String str){

        if(str.length() > 7) return str;
        for(String dontWant : forbidden){
            str = str.replace(dontWant, "");
        }
        return str;
    }

    /**
     *
     * @param parts
     * @return
     */
    private static String[] cleanDirtyParts(String[] parts){

        if(parts == null) return null;
        if(parts.length < 1) return parts;

        int n = parts.length;
        for(int pointer = 0 ; pointer < n ; pointer++){
            parts[pointer] = cleanItFromCrap(parts[pointer]);
        }
        return parts;
    }


    /** main parser step
     *
     * @param input
     * @return
     */
    private static NamesProcessed parseNameStep1(String input) {

        NamesProcessed res = new NamesProcessed();
        res.source = input;
        String s = input;

        //*******************************************
        //*   get nobel titles
        //*   sorted on stringlength
        //*   start to check with longest
        //*   cut it out , put in result
        //*   remaining goes to normal parsing
        //*******************************************

        boolean foundNobel = false;
        for (String str : Nobles) {
            int startpos = s.indexOf(" " + str + " ");
            if (startpos >= 0) {
                int endpos = startpos + (str.length());
                int n = s.length();

                String part1 = "";
                String part2 = "";
                String part3 = "";
                part1 = s.substring(0, startpos);
                if (endpos <= n) {
                    part2 = s.substring(startpos + 1, endpos + 1);
                    part3 = s.substring(endpos + 1);
                    s = part1.trim() + " " + part3.trim();
                }
                foundNobel = true;
                res.nobeltitles = part2.trim();
                break;
            }
        }

        //*******************************************
        /* Special case lastNames */
        //*******************************************
        boolean lastNameFound = false;
//        for (String str : suspectedlastnames) {
//            int startpos = s.indexOf(str);
//            if (startpos >= 0) {
//                int endpos = startpos + (str.length());
//                int n = s.length();
//
//                String part1 = "";
//                String part2 = "";
//                String part3 = "";
//                part1 = s.substring(0, startpos);
//                if (endpos <= n) {
//                    part2 = s.substring(startpos, endpos);
//                    part3 = s.substring(endpos);
//                    s = part1.trim() + " " + part3.trim();
//                }
//                res.lastName = part2.trim();
//                lastNameFound = true;
//                break;
//            }
//        }


        //*********************************************************************************
        // Split on period, commas or spaces, but don't remove from results.
        //  String[] splited = s.split("(?<=[., ])");
        //*********************************************************************************
        String[] splited = originalSpitPattern.split(s);
        String[] splited2= cleanDirtyParts(splited);
        List<String> parts = new ArrayList<>();
        parts.addAll(Arrays.asList(splited));

        // remove empty parts
        for (int x = splited.length - 1; x >= 0; x--) {
            if (splited[x].trim().isEmpty()) {
                parts.remove(x);
            }
        }


        //*********************************************************************************
        // prefixes
        //*********************************************************************************
        if (parts.size() > 0) {
            int foundPrefixIndex = -1;
            int n = parts.size();
            for (int i = 0; i < n; i++) {
                String normalizedPart = normalize(parts.get(i));
                if (Prefixes.contains(normalizedPart)) {
                    foundPrefixIndex = i;
                }
            }
            if (foundPrefixIndex >= 0) {
                res.prefix = parts.get(foundPrefixIndex).trim();
                parts.remove(foundPrefixIndex);
            }
        }


        //*********************************************************************************
        // suffixes
        //*********************************************************************************
        if (parts.size() > 0) {
            int lastPartIndex = parts.size()-1;
                String normalizedPart = normalize(parts.get(lastPartIndex));
                if (Suffixes.contains(normalizedPart)) {
                    res.suffix = parts.get(lastPartIndex).trim();
                    parts.remove(lastPartIndex);
                }
        }


        //*********************************************************************************
        // initials :  parts that are short and can not be name
        //*********************************************************************************
        if (parts.size() > 0) {
            List<String> notInitialParts = new ArrayList<>();
            String initials = "";
            int n = parts.size();
            for (String str : parts) {
                //if (str.trim().length() == 1) {
                //    initials += str.trim();
                //}
                if (str.trim().length() < 3) {
                    if (!canBeMiddleName(str.trim())) {
                        initials += str.trim();
                    } else {
                        notInitialParts.add(str);
                    }
                } else {
                    notInitialParts.add(str);
                }
            }
            res.initials = initials;
            parts.clear();
            parts.addAll(notInitialParts);
        }

        if (parts.size() == 0) {
            res = fix1(res,input);
            return res;

        } else if (parts.size() == 1) {

            //if (res.prefix.equals(""))
            //    res.firstName = parts.get(0).replace(",", "").trim();
            //else
            if (!lastNameFound) res.lastName = parts.get(0).replace(",", "").trim();
            res = fix1(res, input);
            return res;
        } else {

            boolean aStrEndsWithComma = parts.get(0).endsWith(",");
            String aStr = parts.get(0).replace(",", "").trim();

            if (aStrEndsWithComma) {

                if (!lastNameFound) res.lastName = parts.get(0).replace(",", "").trim();
                for (int x = 1; x < parts.size(); x++) {
                    res.firstName += parts.get(x).replace(",", "").trim() + " ";
                }
                res.firstName = res.firstName.trim();
                res = fix1(res, input);
            } else {

                res.firstName = aStr;
                res = fix1(res, input);

                int last = parts.size() - 1;

                // If first part ends with a comma, assume format:
                // Last, First [...First...]

                if (!lastNameFound) {
                    res.lastName = parts.get(last).replace(",", "").trim();
                } else {
                    // Otherwise assume format:
                    // First [...Middle...] Last
                    res.firstName = parts.get(0).replace(",", "").trim();
                }


                List<String> suspectedMiddleNames = new ArrayList<>();

                for (int x = 1; x < parts.size() - 1; x++) {
                    suspectedMiddleNames.add(parts.get(x).replace(",", "").trim());
                }

                if (suspectedMiddleNames.size()>0) {
                    res.middleName = suspectedMiddleNames.stream()
                            .filter(suspectedName -> canBeMiddleName(suspectedName))
                            .collect(Collectors.joining(" "));
                }




            }

        }
        return res;
    }

    private static boolean canBeMiddleName(String suspectedName) {
        if (suspectedName.length()>1) {
            return containsVowel(suspectedName.toLowerCase());
        }
        return false;
    }

    private static boolean containsVowel(String suspectedName) {
        char[] c = suspectedName.toCharArray();
        for (char element : c) {
            if (VowelSet.contains(translate(element))) {
                return true;
            }
        }
        return false;
    }


    public void go(String[] args) {

        List<String> data1 = Arrays.asList(
                "Eric van Cutsem",
                "Eric  Cutsem",
                "e  Cutsem",
                "e  van Cutsem",
                "Cutsem",
                "Carlo la Vecchia",
                "Carlo de la Vecchia",
                "Carlo  Vecchia",
                "C la Vecchia",
                "C  Vecchia",
                "president   Vecchia jr",
                "president  c la Vecchia",
                "J Petter Gustavsson",
                "president Werner von dem Braun",
                "von president jr. Braun Werner",
                "jr Werner Sture  Braun von mayor",
                "von president jr. J R B A Braun Sture Werner",
                "b a   y y y y sturesson",
                "b-a  xxx sturesson",
                "von president jr. Praun Sture Wörner"
        );

        List<String> data2 = Arrays.asList(
                "J Petter Gustavsson"
        );


        List<String> data3 = Arrays.asList(

                "Jsmiljka bbbbb svensson-suić"
        );
        List<String> data4 = Arrays.asList(

                "b a   y y y y sturesson",
                "b-a  xxx sturesson",
                "bert-ove  xxx sturesson",
                "j -p  meyer",
                "kengo imamura"

                );

        //  data1.stream().forEach(x -> System.out.println(parseName(x).toString()));
        //  data2.stream().forEach(x -> System.out.println(parseName(x).toString()));
        data1.stream().forEach(x -> System.out.println(parseName(x).toString()));

    }



    public static void main(String[] args) {

        new NameParser().go(args);

    }



}
