package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TResponseType {
    TEXT(0),
    INPUTSTREAM(1);

    private int intValue;

    TResponseType(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TResponseType fromInteger(int x) {
        switch(x) {
            case 0:
                return TEXT;
            case 1:
                return INPUTSTREAM;
        }
        return null;
    }
}
