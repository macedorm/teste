package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TMessageIcon {
    NONE(1),
    INFO(2),
    WARNING(3),
    ERROR(4),
    QUESTION(5);

    private int intValue;

    TMessageIcon(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TMessageIcon fromInteger(int x) {
        switch(x) {
            case 1:
                return NONE;
            case 2:
                return INFO;
            case 3:
                return WARNING;
            case 4:
                return ERROR;
            case 5:
                return QUESTION;
        }
        return null;
    }
}
