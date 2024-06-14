package br.com.jjconsulting.mobile.jjlib.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;
    private String current;

    public MoneyTextWatcher(EditText editText) {
        this.editTextWeakReference = new WeakReference<EditText>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(current)) {
            EditText edit = editTextWeakReference.get();

            edit.removeTextChangedListener(this);

            String cleanString = s.toString();

            try {
                if (count != 0) {
                    String substr = cleanString.substring(cleanString.length() - 2);

                    if (substr.contains(".") || substr.contains(",")) {
                        cleanString += "0";
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            cleanString = cleanString.replaceAll("[,.R$]", "");

            double parsed = 0;

            try{
                parsed = Double.parseDouble(cleanString);
            }catch (Exception ex){
                LogUser.log(ex.toString());
            }

            DecimalFormat df = new DecimalFormat("0.00");
            String formatted = df.format((parsed / 100));

            current = formatted;
            edit.setText("R$" + formatted);
            edit.setSelection(formatted.length() + 2);

            edit.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


}