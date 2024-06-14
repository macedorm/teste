package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public enum TPerfilAgendaAprov implements Serializable {

    APROVA_AMBOS(1),
    APROVA_SOMENTE_COM_AGENDA(2),
    APROVA_SOMENTE_SEM_AGENDA(3);

    private int intValue;

    TPerfilAgendaAprov(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TPerfilAgendaAprov fromInteger(int x) {
        switch(x) {
            case 1:
                return APROVA_AMBOS;
            case 2:
                return APROVA_SOMENTE_COM_AGENDA;
            case 3:
                return APROVA_SOMENTE_SEM_AGENDA;
        }
        return null;
    }

}
