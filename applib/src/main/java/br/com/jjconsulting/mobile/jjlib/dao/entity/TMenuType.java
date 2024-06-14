package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TMenuType {
    MASTERDATA(1),
    URL(2),
    MODULE(3);

    private int intValue;

    TMenuType(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TMenuType fromInteger(int x) {
        switch(x) {
            case 1:
                return MASTERDATA;
            case 2:
                return URL;
            case 3:
                return MODULE;
        }
        return null;
    }
}
