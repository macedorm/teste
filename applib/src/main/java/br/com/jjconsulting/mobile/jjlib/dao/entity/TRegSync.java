package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.io.Serializable;

public enum TRegSync implements Serializable {

    INSERT(1),
    EDIT(2),
    SEND(3);

    private int intValue;

    TRegSync(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TRegSync fromInteger(int x) {
        switch(x) {
            case 1:
                return INSERT;
            case 2:
                return EDIT;
            case 3:
                return SEND;
        }
        return null;
    }

}
