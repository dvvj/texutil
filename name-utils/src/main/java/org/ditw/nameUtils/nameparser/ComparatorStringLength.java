package org.ditw.nameUtils.nameparser;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by thomasfanto on 05/11/15.
 */
public class ComparatorStringLength implements Comparator<String>, Serializable {

    public int compare(String o1, String o2) {
        if (o1.length() > o2.length()) {
            return -1;
        } else if (o1.length() < o2.length()) {
            return 1;
        } else {
            return 0;
        }
    }

}

