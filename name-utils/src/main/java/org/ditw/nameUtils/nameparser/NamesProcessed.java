package org.ditw.nameUtils.nameparser;

import java.io.Serializable;

public class NamesProcessed implements Serializable {

    public String source = "";
    public String prefix = "";
    public String firstName = "";
    public String middleName = "";
    public String lastName = "";
    //    public String lastNameWoNobles = "";
    public String suffix = "";
    public String nobeltitles = "";
    public String initials = "";
    //    public String swapped = "0";
//    public String sortedNames = "";
    public String signature = "";
//    public String asciifiedLastname = "";


    public static final String COLLECTION_NAME = "NamesProcessed";
    public static final String KEYFIELD = "name";
    public static final String ID = "_id";


    public NamesProcessed() {

    }

}
