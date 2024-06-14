package br.com.jjconsulting.mobile.jjlib.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

public class DecimalTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;
    private String current;

    private int decimal;



    public DecimalTextWatcher(EditText editText, int decimal) {
        this.editTextWeakReference = new WeakReference<EditText>(editText);
        this.decimal = decimal;
    }

    public DecimalTextWatcher(EditText editText) {
        this.editTextWeakReference = new WeakReference<EditText>(editText);
        decimal = 2;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(current)) {
            EditText edit = editTextWeakReference.get();

            edit.removeTextChangedListener(this);
            try {
                String cleanString = s.toString();


                    if (count != 0) {
                        String substr = cleanString.substring(cleanString.length() - decimal);

                        if (substr.contains(".") || substr.contains(",")) {
                            cleanString += "0";
                        }
                    }


                cleanString = cleanString.replaceAll("[,.]", "");

                double parsed = Double.parseDouble(cleanString);

                String format = "0.";
                String div = "1";

                for(int ind = 0; ind < decimal; ind++){
                    format += "0";
                    div += "0";
                }

                DecimalFormat df = new DecimalFormat(format);
                String formatted = df.format((parsed / Integer.parseInt(div)));

                current = formatted;
                edit.setText(formatted);
                edit.setSelection(formatted.length());
            }catch (Exception ex){
                ex.printStackTrace();
            }

            edit.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


}