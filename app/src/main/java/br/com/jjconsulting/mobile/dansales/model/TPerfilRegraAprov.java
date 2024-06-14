package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public enum TPerfilRegraAprov implements Serializable {

    //Aprova todos
    APROVA_AMBOS(1),

    //Aprova somente pedidos com desconto abaixo do permitido
    APROVA_DESCONTO(2),

    //Aprova somente pedidos com quebra
    APROVA_QUEBRA(3);

    private int intValue;

    TPerfilRegraAprov(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TPerfilRegraAprov fromInteger(int x) {
        switch(x) {
            case 1:
                return APROVA_AMBOS;
            case 2:
                return APROVA_DESCONTO;
            case 3:
                return APROVA_QUEBRA;
        }
        return null;
    }

}
