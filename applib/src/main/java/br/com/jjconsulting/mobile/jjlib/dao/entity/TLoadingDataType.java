package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TLoadingDataType {
    ONLINE(1),
    OFFLINE(0);

    private int intValue;

    TLoadingDataType(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TLoadingDataType fromInteger(int x) {
        switch(x) {
            case 1:
                return ONLINE;
            case 0:
                return OFFLINE;
        }
        return null;
    }
}
