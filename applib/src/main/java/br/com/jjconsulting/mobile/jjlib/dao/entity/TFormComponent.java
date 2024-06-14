package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TFormComponent {
    TEXT(1),
    TEXTAREA(2),
    HOUR(3),
    DATE(4),
    DATETIME(5),
    PASSWORD(6),
    EMAIL(7),
    NUMBER(8),
    COMBOBOX(9),
    SEARCH(10), // nao implementado
    RADIOBUTTON(11), // nao implementado Combo sendo usado no lugar
    CHECKBOX(12),
    CNPJ(13),
    CPF(14),
    CNPJ_CPF(15),
    CURRENCY(16),
    TEL(17),
    CEP(18),
    QRCODE(19),
    LOCATION(20),
    PHOTO(21),
    UPLOAD(21);


    private int intValue;

    TFormComponent(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TFormComponent fromInteger(int x) {
        switch(x) {
            case 1:
                return TEXT;
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
            case 9:
                return COMBOBOX;
            case 10:
                return SEARCH;
            case 11:
                return RADIOBUTTON;
            case 12:
                return CHECKBOX;
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
            case 19:
                return QRCODE;
            case 20:
                return LOCATION;
            case 21:
                return PHOTO;
        }
        return null;
    }

}
