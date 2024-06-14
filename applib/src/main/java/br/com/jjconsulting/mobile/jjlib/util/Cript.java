package br.com.jjconsulting.mobile.jjlib.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Cript {

    public static String getEncryptReportPortal(String msg) {
        String ret = "";
        String password = "Secret";
        int pos = 0;
        char c;
        int ascii;
        int key;
        int newAscii;

        while (pos < msg.length()) {
            c = msg.charAt(pos);
            ascii = (int) c;
            key = (int) password.charAt(((pos + 1) % password.length() + 1) - 1);
            newAscii = ascii + key;

            if (newAscii > 255)
                newAscii = newAscii - 255;

            String tmp = "0" + Integer.toHexString(newAscii);
            ret = ret + tmp.substring(tmp.length() - 2);
            pos++;
        }

        return ret.toUpperCase();
    }

    /**
     * Converte uma string para hash (32 character hexadecimal string)
     *
     * @param input Valor a ser convertido
     * @return Valor em hash
     * @author Lucio Pelinson
     * @since 2015-05-28
     */
    public static String getMd5Hash(String input) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
