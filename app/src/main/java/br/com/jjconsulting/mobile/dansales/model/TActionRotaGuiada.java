package br.com.jjconsulting.mobile.dansales.model;

import android.content.Context;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public enum TActionRotaGuiada implements Serializable {
    CHECKIN(1),
    CHECKOUT(2),
    PAUSE(3),
    RESUME(4),
    CHECKOUT_MANUAL(5);

    private int intValue;

    TActionRotaGuiada(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TActionRotaGuiada fromInteger(int x) {
        switch(x) {
            case 1:
                return CHECKIN;
            case 2:
                return CHECKOUT;
            case 3:
                return PAUSE;
            case 4:
                return RESUME;
            case 5:
                return CHECKOUT_MANUAL;
            default:
                return CHECKIN;
        }
    }

}
