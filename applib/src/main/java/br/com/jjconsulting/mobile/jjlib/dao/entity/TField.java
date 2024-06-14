package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.io.Serializable;

public enum TField  implements Serializable {

    DATE(1),
    DATETIME(2),
    FLOAT(3),
    INT(4),
    NTEXT(5),
    NVARCHAR(6),
    TEXT(7),
    VARCHAR(8);

    private int intValue;

    TField(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TField fromInteger(int x) {
        switch(x) {
            case 1:
                return DATE;
            case 2:
                return DATETIME;
            case 3:
                return FLOAT;
            case 4:
                return INT;
            case 5:
                return NTEXT;
            case 6:
                return NVARCHAR;
            case 7:
                return TEXT;
            case 8:
                return VARCHAR;
        }
        return null;
    }

}
