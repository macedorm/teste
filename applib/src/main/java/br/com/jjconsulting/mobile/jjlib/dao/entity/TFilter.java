package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TFilter {
    NONE(1),
    EQUALS(2),
    CONTAIN(3),
    RANGE(4);

    private int intValue;

    TFilter(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TFilter fromInteger(int x) {
        switch(x) {
            case 1:
                return NONE;
            case 2:
                return EQUALS;
            case 3:
                return CONTAIN;
            case 4:
                return RANGE;
        }
        return null;
    }

}
