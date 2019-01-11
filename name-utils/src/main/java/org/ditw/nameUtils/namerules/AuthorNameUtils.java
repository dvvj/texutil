package org.ditw.nameUtils.namerules;


import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by hakanlofqvist on 12/02/15.
 */

public class AuthorNameUtils implements Serializable {
    private static Pattern splitterPattern = Pattern.compile("\\W+");
    public AuthorNameUtils() {}

//    @Deprecated
//    public boolean isNameCompatible(HadoopAuthorDTO leftAuthor, HadoopAuthorDTO rightAuthor) {
//        String leftFirstName = leftAuthor.firstName;
//        String rightFirstName = rightAuthor.firstName;
//        return isNameCompatible(leftFirstName, rightFirstName);
//    }

    public boolean isNameCompatible(String leftFirstName, String rightFirstName) {
        if (leftFirstName.length()<2 || rightFirstName.length()<2) {
            return true;
        }
        return firstNamesMatch(leftFirstName, rightFirstName);
    }

    public boolean isClusterNameCompatible(
        String leftFirstName,
        String leftSignature,
        String rightFirstName,
        String rightSignature
    ) {

        if (leftFirstName.length()<2 || rightFirstName.length()<2) {
            return signatureMatch(leftSignature,rightSignature);
        }

        if (firstNamesMatch(leftFirstName, rightFirstName)) {
            return signatureMatch(leftSignature,rightSignature);
        } else {
            return false;
        }
    }

    private boolean firstNamesMatch(String leftFirstName, String rightFirstName) {
        if (leftFirstName.equals(rightFirstName)) {
            return true;
        }
        String left = leftFirstName.replaceFirst("-| ","");
        String right = rightFirstName.replaceFirst("-| ","");
        return left.equals(right);
    }

    public static boolean signatureMatch(
        String leftSignature,
        String rightSignature) {

        return signatureMatch(
            leftSignature,
            rightSignature,
            false // be default, do not require same order, applies to small namespaces
        );
    }
    public static boolean signatureMatch(
        String leftSignature,
        String rightSignature,
        Boolean matchOrder
    ) {
        if (leftSignature.length() == 0 || rightSignature.length() == 0) {
            return true;
        }
        if (leftSignature.length()<2 || rightSignature.length()< 2) {
            if (!matchOrder) {
                for (char c : leftSignature.toCharArray()) {
                    if (rightSignature.contains(String.valueOf(c))) {
                        return true;
                    }
                }
                return false;
            }
            else {
                return leftSignature.charAt(0) == rightSignature.charAt(0);
            }
        } else if (leftSignature.length() == rightSignature.length()) {
            return leftSignature.equals(rightSignature);

        } else if (leftSignature.length()>2 || rightSignature.length()>2) {

            if (!matchOrder) {
                int matching = 0;
                for (char c : leftSignature.toCharArray()) {
                    if (rightSignature.contains(String.valueOf(c))) {
                        matching++;
                    }
                }
                return matching>1;
            }
            else {
                int minLen = Math.min(leftSignature.length(), rightSignature.length());
                for (int i = 0; i < minLen; i++) {
                    if (leftSignature.charAt(i) != rightSignature.charAt(i))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean haveSameEmail(Set<String> leftEmailSet, Set<String> rightEmailSet) {
        if (leftEmailSet.size() < 1) {
            if (rightEmailSet.size() < 1) {
                return false;
            }
        } else {
            if (rightEmailSet.size() > 0) {
                return leftEmailSet.stream().map(leftEmail-> rightEmailSet.contains(leftEmail)).filter(condition-> condition == true).findFirst().isPresent();
            }
        }
        return false;
    }

    public final static String InvalidNameSpace = "NOT_VALID";
    public static String _getNamespace(String firstTrimmed, String lastName) {
        StringBuffer sb = new StringBuffer();
        sb.append(asciify(lastName.trim().toLowerCase()));

        if (sb.toString().isEmpty()) {
            return InvalidNameSpace;
        }

        if (firstTrimmed != null && firstTrimmed.length()>0) {
            sb.append("|");
            sb.append(asciify(firstTrimmed.toLowerCase()));
        } else {
            return InvalidNameSpace;
        }

        return sb.toString();
    }

    public static String getNamespace(String firstName, String lastName) {

        if (firstName != null) {
            String firstTrimmed = firstName.trim();
            if (firstTrimmed.length() > 0)
                return _getNamespace(firstTrimmed.substring(0,1), lastName);
            else
                return InvalidNameSpace;
        }
        else
            return InvalidNameSpace;
    }

    public static String getNamespace_FullFirstName(String firstName, String lastName) {
        if (firstName != null) {
            return _getNamespace(firstName.trim(), lastName);
        }
        else
            return InvalidNameSpace;
    }


    public List<String> tokenize(String firstName, String initials, String lastName) {
        List<String> results = new ArrayList<>();
        results.addAll(Arrays.asList(splitterPattern.split(asciify(lastName.trim().toLowerCase()))));
        results.addAll(getInitials(firstName, initials, lastName));
        return results;
    }

    public List<String> getInitials(String firstName, String initials, String lastName) {
        Set<String> results = new HashSet<>();
        if (firstName != null) {
            for (String part : splitterPattern.split(asciify(firstName.trim().toLowerCase()))) {
                if (part != null && part.length()>0) {
                    results.add(part.substring(0, 1));
                }
            }
        }
        if (initials != null) {
            for (Character initial : asciify(initials.trim().toLowerCase()).toCharArray()) {
                results.add(initial.toString());
            }
        }
        if (splitterPattern.split(asciify(lastName.trim().toLowerCase())).length>1) {
            results.add(asciify(lastName.trim().substring(0,1).toLowerCase()));
        }
        return results.stream().collect(Collectors.toList());
    }

    public static String clean(String s) {
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
    public static char translate(char c) {
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
                return 'e';
            case '\u011C':
            case '\u011D':
            case '\u011E':
            case '\u011F':
            case '\u0120':
            case '\u0121':
            case '\u0122':
            case '\u0123':
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
                return 's';
            case '\u0162':
            case '\u0163':
            case '\u0164':
            case '\u0165':
            case '\u0166':
            case '\u0167':
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
            case '-':
                return ' ';
        }
        return c;
    }
}
