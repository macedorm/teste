package br.com.jjconsulting.mobile.jjlib.util.Mask;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;

import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public abstract class NumberMaskUtil {
    private static int decimalSize;

    public static String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }


    public static String adjustDecimalSize(int decimal, String value){
        if(value.contains(".")){
            String splitValue[] = value.split("\\.");
            int result = decimal - splitValue[1].length();
            if(result == 0){
                return value;
            } else {
                String adjustValue =  value;
                for(int ind = result; ind > 0; ind--){
                    adjustValue += "0";
                }

                return adjustValue;
            }
        }else {
            return value;
        }


    }


    public static TextWatcher decimal(EditText ediTxt, int size) {
        decimalSize = 1;

        for (int ind = 0; ind < size; ind++) {
            decimalSize = decimalSize * 10;
        }

        return new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    ediTxt.removeTextChangedListener(this);

                    String cleanString = s.toString().replace(".", "");


                    if(cleanString.length() == 0){
                        current = "";
                    } else {
                        BigDecimal parsed = new BigDecimal(cleanString);
                        BigDecimal divisor = new BigDecimal(decimalSize);

                        parsed = parsed.divide(divisor, size, BigDecimal.ROUND_HALF_UP);

                        String formatted = String.valueOf(parsed);
                        current = formatted;

                    }

                    ediTxt.setText(current.replace(",", "."));
                    ediTxt.setSelection(current.length());

                    ediTxt.addTextChangedListener(this);
                }
            }

        };
    }

    public static String getNumberOfDecimalPlaces(String value, int decimalPlaces, boolean changeComma){

        if(TextUtils.isNullOrEmpty(value)){
            return value;
        } else {
            int ceilOrFloor = 1;
            double parsed = Double.parseDouble(value);


            /*double convertNumber = parsed;
            convertNumber *= (Math.pow(10, decimalPlaces));

            if (ceilOrFloor == 0) {
                convertNumber = Math.ceil(convertNumber);
            } else {
                convertNumber = Math.floor(convertNumber);
            }

            convertNumber /= (Math.pow(10, decimalPlaces));*/

            if(changeComma){
                return (value).replace(".", ",");
            } else {
                return value + "";
            }
        }
    }
}