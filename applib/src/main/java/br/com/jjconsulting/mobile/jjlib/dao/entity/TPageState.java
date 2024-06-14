package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TPageState {
    LIST(1),
    VIEW(2),
    INSERT(3),
    UPDATE(4),
    FILTER(5),
    IMPORT(6);

    private int intValue;

    TPageState(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TPageState fromInteger(int x) {
        switch(x) {
            case 1:
                return LIST;
            case 2:
                return VIEW;
            case 3:
                return INSERT;
            case 4:
                return UPDATE;
            case 5:
                return FILTER;
            case 6:
                return IMPORT;
        }
        return null;
    }
}
