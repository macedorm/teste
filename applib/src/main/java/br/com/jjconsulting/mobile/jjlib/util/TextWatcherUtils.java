package br.com.jjconsulting.mobile.jjlib.util;

import android.text.Editable;
import android.text.TextWatcher;

public class TextWatcherUtils {

    /**
     * Build a TextWatcher implementing only the afterTextChanged method.
     */
    public static TextWatcher buildWithAfterTextChanged(
            AfterTextChangedListener listener) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                listener.afterTextChanged(editable);
            }
        };
    }

    public interface AfterTextChangedListener {

        void afterTextChanged(Editable editable);
    }
}
