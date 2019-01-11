package org.ditw.nameUtils;



import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by hakanlofqvist on 22/09/15.
 */
public class JournalRatingService implements Serializable {

    private HashMap<String,String> issnToNlmIdMap = new HashMap<>();
    private HashMap<String,Map<String,Object>> nlmIdToJournalDataMap = new HashMap<>();
    private HashMap<String,Double> issnRatingMap = new HashMap<>();


    private Integer journalCounter;
    private Integer haveRank;
    private Integer haveRankButMissingJournalCount;

    private static Pattern CSV_PATTERN = Pattern.compile(":");
    private static Pattern TAB_PATTERN = Pattern.compile("\t");

    public JournalRatingService(Iterator<String> medlineJournalDataIterator, Iterator<String> journalRatingDataIterator) {
        loadJournals(medlineJournalDataIterator);
        loadJournalRatings(journalRatingDataIterator);
    }

    public Integer getJournalCounter() {
        return journalCounter;
    }

    public Integer getHaveRank() {
        return haveRank;
    }

    public Integer getHaveRankButMissingJournalCount() {
        return haveRankButMissingJournalCount;
    }

    private void loadJournals(Iterator<String> medlineJournalDataIterator) {
        System.out.println("Adding journals");

        boolean recordDone = false;
        journalCounter = 0;
        haveRank = 0;
        haveRankButMissingJournalCount = 0;

        Map<String, Object> journalProperties = new HashMap<>();
        while (medlineJournalDataIterator.hasNext()) {
            for(String nextLine: medlineJournalDataIterator.next().split("\n")){

                String[] parts = CSV_PATTERN.split(nextLine, 2);

                if (parts.length < 2) {
                    //Do nothing, this is the seperating line -----------------
                } else {
                    parts[1] = parts[1].trim();
                    switch (parts[0]) {
                        case "JrId":
                            break;
                        case "JournalTitle":
                            journalProperties.put("name", parts[1]);
                            break;
                        case "MedAbbr":
                            journalProperties.put("medAbbr", parts[1]);
                            break;
                        case "ISSN (Print)": {
                            if (parts[1].trim().length() > 4) {
                                journalProperties.put("issnPrint", parts[1].replace("-", "").trim());
                            }
                            break;
                        }
                        case "ISSN (Online)": {
                            if (parts[1].trim().length() > 4) {
                                journalProperties.put("issnOnline", parts[1].replace("-", "").trim());
                            }
                            break;
                        }
                        case "IsoAbbr":
                            journalProperties.put("isoAbbr", parts[1]);
                            break;
                        case "NlmId":
                            journalProperties.put("id", parts[1].trim());
                            recordDone = true;
                            break;
                    }
                    if (recordDone) {

                        String journalId = (String) journalProperties.get("id");
                        if (nlmIdToJournalDataMap.containsKey(journalId)) {
                            System.out.println("Duplicate journal id found for: " + journalId);
                        } else {
                            journalCounter++;
                            nlmIdToJournalDataMap.put(journalId, journalProperties);
                            if (journalProperties.containsKey("issnOnline")) {
                                issnToNlmIdMap.put((String) journalProperties.get("issnOnline"), journalId);
                            }
                            if (journalProperties.containsKey("issnPrint")) {
                                issnToNlmIdMap.put((String) journalProperties.get("issnPrint"), journalId);
                            }
                        }
                        journalProperties = new HashMap<>();
                        recordDone = false;
                    }
                }
            }
        }
        System.out.println(journalCounter + " nlm journals loaded");
        System.out.println(nlmIdToJournalDataMap.size());
    }

    private void loadJournalRatings(Iterator<String> journalRatingDataIterator) {
//        Integer haveRankButMissingJournalCount = 0;
//        Integer haveRank = 0;
        System.out.println("Loading journal ratings");
        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

        while (journalRatingDataIterator.hasNext()) {
            String[] parts = TAB_PATTERN.split(journalRatingDataIterator.next());
            String issnCol = parts[3];
            if (issnCol.startsWith("ISSN")) {
                String[] issns = issnCol.substring(5).split(", ");
                for (String issn: issns) {
                    if (issnToNlmIdMap.containsKey(issn)) {
                        String nlmId = issnToNlmIdMap.get(issn);
                        haveRank++;
                        if (nlmIdToJournalDataMap.containsKey(nlmId)) {
                            Map<String,Object> jornalData = nlmIdToJournalDataMap.get(nlmId);
                            Double journalRating = null;
                            try {
                                journalRating = format.parse(parts[4]).doubleValue();
                            } catch (ParseException e) {
                                System.out.println("Could not parse journal rating from row: " + parts);
                            }
                            jornalData.put("sjr", journalRating);
                            if (jornalData.containsKey("issnOnline")) {
                                issnRatingMap.put((String)jornalData.get("issnOnline"), journalRating);
                            }
                            if (jornalData.containsKey("issnPrint")) {
                                issnRatingMap.put((String)jornalData.get("issnPrint"), journalRating);
                            }
                        }
                        break;
                    } else {
                        haveRankButMissingJournalCount++;
                        //System.out.println("Rank|" + parts[0] + "|scimagojr issn not found|" + issn + "|sjr rating|"+ parts[4] +"|total docs|" + parts[6] + "|journal title|" + parts[1]);
                    }
                }
            }
        }

        System.out.println("Journals ratings added, " + haveRank + " journals have rank, " + (journalCounter -  haveRank)
                + " journals do not have rank, " + haveRankButMissingJournalCount + " rankings are missing journals count." );
    }


    public double getIssnRating(String issn) {
        return issnRatingMap.getOrDefault(issn, 0.0);
    }

    public double getJournalRating(String nlmJournalId) {
        double rating = 0.0;
        if (nlmIdToJournalDataMap.containsKey(nlmJournalId)) {
            Map<String,Object> journalProperties = nlmIdToJournalDataMap.get(nlmJournalId);
            if (journalProperties.containsKey("issnPrint")) {
                return getIssnRating((String) journalProperties.get("issnPrint"));
            }
            if (journalProperties.containsKey("issnOnline")) {
                return getIssnRating((String)journalProperties.get("issnOnline"));
            }
        }
        return rating;
    }

    public Boolean journalExists(String jurnalId) {
        return nlmIdToJournalDataMap.containsKey(jurnalId);
    }

    public Collection<Map<String,Object>> getJournalData() {
        return nlmIdToJournalDataMap.values();
    }
}
