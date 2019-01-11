package org.ditw.nameUtils;

import java.text.Normalizer;

/**
 * Created by joe on 6/21/17.
 */
public class SimplifyCharset {
    public static String normalizeAndAsciify(String str) {
        String normalize = Normalizer.normalize(str, Normalizer.Form.NFD);
        //todo: replace with Pattern
        String asciiReplaced = normalize.replaceAll("[^\\p{ASCII}]", "");
        return asciiReplaced;
    }
}
