package br.com.jjconsulting.mobile.jjlib.dao.entity;

/**
 * Created by jjconsulting on 01/03/2018.
 */

public enum TBehavior {
    REAL(1),
    VIRTUAL(2),
    VIEWONLY(3);


    private int intValue;

    TBehavior(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TBehavior fromInteger(int x) {
        switch(x) {
            case 1:
                return REAL;
            case 2:
                return VIRTUAL;
            case 3:
                return VIEWONLY;

        }
        return null;
    }
}
