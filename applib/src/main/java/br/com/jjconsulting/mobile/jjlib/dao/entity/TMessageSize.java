package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TMessageSize {
    SMALL(1),
    DEFAULT(2),
    LARGE(3);

    private int intValue;

    TMessageSize(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TMessageSize fromInteger(int x) {
        switch(x) {
            case 1:
                return SMALL;
            case 2:
                return DEFAULT;
            case 3:
                return LARGE;
        }
        return null;
    }
}
