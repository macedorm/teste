package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class AndroidUICompoentUtils {

    private AndroidUICompoentUtils() { }

    public static void showKeyboard(Context context) throws Exception {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager == null)
            throw new Exception("Service INPUT_METHOD_SERVICE not available.");

        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void hideKeyboard(Context context) throws Exception {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager == null)
            throw new Exception("Service INPUT_METHOD_SERVICE not available.");

        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
