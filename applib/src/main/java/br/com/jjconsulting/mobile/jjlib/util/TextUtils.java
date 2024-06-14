package br.com.jjconsulting.mobile.jjlib.util;

import java.io.File;

public class TextUtils {

    private TextUtils() {

    }

    public static String firstLetterUpperCase(String text) {
        if (text != null && text.length() > 0) {
            StringBuilder sb = new StringBuilder(text.toLowerCase());
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        } else {
            return text;
        }
    }


    public static String trimLeadingZeros(String value) {
        return value.replaceFirst("^0+(?!$)", "");
    }

    /**
     * Make a new String, using the origin parameter as base, filling it (to the left)
     * with the characterToFill until it get the desired length.
     *
     * @param origin          String used as base
     * @param characterToFill char used to fill the (new) String
     * @param untilLengthIs   target length
     */
    public static String fillLeftWithUntil(String origin, char characterToFill,
                                           int untilLengthIs) {
        int requiredLoops = untilLengthIs - origin.length();
        StringBuilder result = new StringBuilder(origin);
        for (int i = 0; i < requiredLoops; i++) {
            result.insert(0, characterToFill);
        }
        return result.toString();
    }

    /**
     * Remove all (character) occurrencies starting from the left until it's a different character.
     */
    public static String removeAllLeftOccurrencies(String origin, char character) {
        if(origin != null) {

            char[] originArray = origin.toCharArray();
            for (int i = 0; i < origin.length(); i++) {
                if (originArray[i] != character) {
                    return origin.substring(i);
                }
            }
            return origin;
        } else {
            return "";
        }
    }

    /**
     * String empty or null = true
     *
     * @param value
     * @return
     */
    public static boolean isNullOrEmpty(String value) {
        if (value == null || android.text.TextUtils.isEmpty(value)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getExtensionFile(String path){
        String extension = "";
        try{

            String extensionSplit[] = path.split("\\.");

            if(extensionSplit != null && extensionSplit.length > 0) {
                extension = extensionSplit[extensionSplit.length - 1];
                extension = "." + extension;
            }

        } catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        return extension;
    }
}
