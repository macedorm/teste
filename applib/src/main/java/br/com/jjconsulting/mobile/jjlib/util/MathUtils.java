package br.com.jjconsulting.mobile.jjlib.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class MathUtils {

    private MathUtils() {

    }

    /**
     * Parse a string representation of a number to BigDecimal using the default locale.
     * @throws ParseException
     */
    public static BigDecimal toBigDecimal(String value) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getInstance();
        Number number = numberFormat.parse(value);
        return new BigDecimal(number.doubleValue());
    }

    /**
     * Parse a string representation of a number to BigDecimal using the default locale.<br>
     * Warning: it does not throw any exception, just null in case of error.
     */
    public static BigDecimal toBigDecimalOrDefault(String value) {
        try {
            NumberFormat numberFormat = NumberFormat.getInstance();
            Number number = numberFormat.parse(value);
            return new BigDecimal(number.doubleValue());
        } catch (ParseException parseEx) {
            parseEx.printStackTrace();
            return null;
        }
    }

    /**
     * Parse a string representation of a number to BigDecimal using the PT-BR locale.
     * @throws ParseException
     */
    public static BigDecimal toBigDecimalUsingLocalePTBR(String value) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getInstance(
                LocaleUtils.getPortugueseBrazilianLocale());
        Number number = numberFormat.parse(value);
        return new BigDecimal(number.doubleValue());
    }

    /**
     * Parse a string representation of a number to BigDecimal using the PT-BR locale.<br>
     * Warning: it does not throw any exception, just null in case of error.
     */
    public static BigDecimal toBigDecimalOrDefaultUsingLocalePTBR(String value) {
        try {
            NumberFormat numberFormat = NumberFormat.getInstance(
                    LocaleUtils.getPortugueseBrazilianLocale());
            Number number = numberFormat.parse(value);
            return new BigDecimal(number.doubleValue());
        } catch (ParseException parseEx) {
            parseEx.printStackTrace();
            return null;
        }
    }

    /**
     * Parse a string representation of a number to double using the PT-BR locale.
     * @throws ParseException
     */
    public static double toDoubleUsingLocalePTBR(String value) throws ParseException {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(
                LocaleUtils.getPortugueseBrazilianLocale());
        Number number = numberFormat.parse(value);
        return number.doubleValue();
    }

    /**
     * Parse a string representation of a number to double using the PT-BR locale.<br>
     * Warning: it does not throw any exception, just zero (0) in case of error.
     */
    public static double toDoubleOrDefaultUsingLocalePTBR(String value) {
        try {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(
                    LocaleUtils.getPortugueseBrazilianLocale());
            Number number = numberFormat.parse(value);
            return number.doubleValue();
        } catch (ParseException parseEx) {
            parseEx.printStackTrace();
            return 0;
        }
    }

    /**
     * Parse a string representation of a number to double using the given locale.<br>
     * Warning: it does not throw any exception, just zero (0) in case of error.
     */
    public static double toDoubleOrDefault(String value, Locale locale) {
        try {
            NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
            Number number = numberFormat.parse(value);
            return number.doubleValue();
        } catch (ParseException parseEx) {
            parseEx.printStackTrace();
            return 0;
        }
    }
}
