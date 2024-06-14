package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TRelationView {
    LIST(1),
    VIEW(2),
    UPDATE(4);

    private int intValue;

    TRelationView(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TRelationView fromInteger(int x) {
        switch(x) {
            case 1:
                return LIST;
            case 2:
                return VIEW;
            case 4:
                return UPDATE;
        }
        return null;
    }

}
