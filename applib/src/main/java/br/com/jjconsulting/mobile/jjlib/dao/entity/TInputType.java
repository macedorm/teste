package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TInputType {
    TEXTBOX(1),
    TEXTAREA(2),
    HOUR(3),
    DATE(4),
    DATETIME(5),
    PASSWORD(6),
    EMAIL(7),
    NUMBER(8),
    CNPJ(13),
    CPF(14),
    CNPJ_CPF(15),
    CURRENCY(16),
    TEL(17),
    CEP(18);

    private int intValue;

    TInputType(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TInputType fromInteger(int x) {
        switch(x) {
            case 1:
                return TEXTBOX;
            case 2:
                return TEXTAREA;
            case 3:
                return HOUR;
            case 4:
                return DATE;
            case 5:
                return DATETIME;
            case 6:
                return PASSWORD;
            case 7:
                return EMAIL;
            case 8:
                return NUMBER;
            case 13:
                return CNPJ;
            case 14:
                return CPF;
            case 15:
                return CNPJ_CPF;
            case 16:
                return CURRENCY;
            case 17:
                return TEL;
            case 18:
                return CEP;
        }
        return null;
    }

}
