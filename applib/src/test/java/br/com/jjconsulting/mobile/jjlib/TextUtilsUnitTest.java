package br.com.jjconsulting.mobile.jjlib;

import org.junit.Test;

import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

import static org.junit.Assert.assertEquals;

public class TextUtilsUnitTest {

    @Test
    public void fillLeftWithUntil_isCorrect1() throws Exception {
        String origin = "102030";
        char characterToFill = '0';
        int untilLengthIs = 18;
        String result = TextUtils.fillLeftWithUntil(origin, characterToFill, untilLengthIs);

        String correctValue = "000000000000102030";
        assertEquals(null, correctValue, result);
    }

    @Test
    public void fillLeftWithUntil_isCorrect2() throws Exception {
        String origin = "102030";
        char characterToFill = '0';
        int untilLengthIs = 5;
        String result = TextUtils.fillLeftWithUntil(origin, characterToFill, untilLengthIs);

        String correctValue = "102030";
        assertEquals(null, correctValue, result);
    }

    @Test
    public void removeAllLeftOccurrencies_isCorrect1() throws Exception {
        String origin = "000000000000102030";
        char character = '0';
        String result = TextUtils.removeAllLeftOccurrencies(origin, character);

        String correctValue = "102030";
        assertEquals(null, correctValue, result);
    }

    @Test
    public void removeAllLeftOccurrencies_isCorrect2() throws Exception {
        String origin = "102030";
        char character = '0';
        String result = TextUtils.removeAllLeftOccurrencies(origin, character);

        String correctValue = "102030";
        assertEquals(null, correctValue, result);
    }
}