package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TFormState {
    LIST(1),
    PAINEL(2),
    APROV(3);

    private int intValue;

    TFormState(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TFormState fromInteger(int x) {
        switch(x) {
            case 1:
                return LIST;
            case 2:
                return PAINEL;
            case 3:
                return APROV;
        }
        return null;
    }

}
