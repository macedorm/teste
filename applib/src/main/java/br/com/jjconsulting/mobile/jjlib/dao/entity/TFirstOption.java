package br.com.jjconsulting.mobile.jjlib.dao.entity;


public enum TFirstOption {

    NONE(1),
    ALL(2),
    CHOOSE(3);

    private int intValue;

    TFirstOption(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TFirstOption fromInteger(int x) {
        switch(x) {
            case 1:
                return NONE;
            case 2:
                return ALL;
            case 3:
                return CHOOSE;
        }
        return null;
    }
}
