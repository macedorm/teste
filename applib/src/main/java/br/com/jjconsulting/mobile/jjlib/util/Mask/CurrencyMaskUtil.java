package br.com.jjconsulting.mobile.jjlib.util.Mask;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public abstract class CurrencyMaskUtil {
    public static String unmask(String s) {
        return s.replaceAll("[.]", "").replaceAll("[-]", "")
                .replaceAll("[/]", "").replaceAll("[(]", "")
                .replaceAll("[)]", "");
    }


    public static TextWatcher monetario(EditText ediTxt) {
        return new TextWatcher() {
            // Mascara monetaria para o preço do produto
            private String current = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    ediTxt.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[ R$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted.replaceAll("[R$]", "");
                    ediTxt.setText(current);
                    ediTxt.setSelection(current.length());

                    ediTxt.addTextChangedListener(this);
                }
            }

        };
    }


    public static String convertSimpleText(String value){
        String currency = "";
        try {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            numberFormat.setMinimumFractionDigits(0);
            currency = numberFormat.format(Double.parseDouble(value));
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }



        return currency;
    }
}