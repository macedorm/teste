package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public enum TMultiValuesType implements Serializable {
    RG_JUST_NAO_INICIADO(1),
    RG_TASK_JUST(2),
    RG_ITEM_PEDIDO_JUST(3),
    RG_JUST_INCOMPLETO(5),
    CG_TIPO_CADASTRO(6),
    CG_STATUS(7);

    private int intValue;

    TMultiValuesType(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TMultiValuesType fromInteger(int x) {
        switch(x) {
            case 1:
                return RG_JUST_INCOMPLETO;
            case 2:
                return RG_TASK_JUST;
            case 3:
                return RG_ITEM_PEDIDO_JUST;
            case 5:
                return RG_JUST_NAO_INICIADO;
            case 6:
                return CG_TIPO_CADASTRO;
            case 7:
                return CG_STATUS;
        }

        return null;
    }

}
