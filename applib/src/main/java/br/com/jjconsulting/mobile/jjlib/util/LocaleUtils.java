package br.com.jjconsulting.mobile.jjlib.util;

import android.content.Context;
import android.os.Build;

import java.util.Locale;

public class LocaleUtils {

    private LocaleUtils() {

    }

    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context
                    .getApplicationContext()
                    .getResources()
                    .getConfiguration()
                    .getLocales()
                    .get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public static Locale getPortugueseBrazilianLocale() {
        return new Locale("pt", "BR");
    }
}
