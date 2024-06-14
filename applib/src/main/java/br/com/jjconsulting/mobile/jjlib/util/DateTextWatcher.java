package br.com.jjconsulting.mobile.jjlib.util;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class DateTextWatcher implements TextWatcher {

    private static final String mask = "##/##/####";

    private final WeakReference<EditText> editTextWeakReference;

    private boolean isUpdating;
    private String old = "";

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher insert(final EditText editText) {
        return new TextWatcher() {


            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }


    public DateTextWatcher(EditText editText) {
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        this.editTextWeakReference = new WeakReference<EditText>(editText);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        EditText edit = editTextWeakReference.get();
        String str = DateTextWatcher.unmask(s.toString());
        boolean applymaks = true;

        if (str.length() > 8) {
            applymaks = false;
        }

        String mascara = "";
        if (isUpdating) {
            old = str;
            isUpdating = false;
            return;
        }
        int i = 0;
        if (applymaks) {
            for (char m : mask.toCharArray()) {

                if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                    mascara += m;
                    continue;
                }


                try {
                    mascara += str.charAt(i);
                } catch (Exception e) {
                    break;
                }
                i++;
            }
        } else {
            mascara = str;
            if (str.length() > 10) {
                mascara = str.substring(0, 10);
            }
        }
        isUpdating = true;
        edit.setText(mascara);
        edit.setSelection(mascara.length());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}