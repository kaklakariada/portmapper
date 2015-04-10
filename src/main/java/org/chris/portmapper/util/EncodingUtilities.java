/**
 *
 */
package org.chris.portmapper.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chris
 */
public class EncodingUtilities {

    private static Map<Character, String> knownEncodings;

    static {
        knownEncodings = new HashMap<>();
        knownEncodings.put('<', "&lt;");
        knownEncodings.put('>', "&gt;");
        knownEncodings.put('&', "&amp;");
    }

    /**
     * Replace all special characters with their html entities. This was found
     * at <a href=
     * "http://www.owasp.org/index.php/How_to_perform_HTML_entity_encoding_in_Java"
     * >http://www.owasp.org/index.php/
     * How_to_perform_HTML_entity_encoding_in_Java</a>
     * 
     * @param s
     *            the string in which to replace the special characters.
     * @return the result of the replacement.
     */
    public static String htmlEntityEncode(final String s) {
        final StringBuffer buf = new StringBuffer();

        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                buf.append(c);
            } else {
                if (knownEncodings.containsKey(c)) {
                    buf.append(knownEncodings.get(c));
                } else {
                    buf.append("&#" + (int) c + ";");
                }
            }
        }
        return buf.toString();
    }
}
