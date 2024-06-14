package br.com.jjconsulting.mobile.dansales.util;

public enum TMessageType {
    MESSAGEM(1),
    ROTA_GUIADA(2),
    PUSH(3);

    private int intValue;

    TMessageType(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TMessageType fromInteger(int value){
        TMessageType messageType = TMessageType.MESSAGEM;
        switch (value) {
            case 1:
                messageType = TMessageType.MESSAGEM;
                break;
            case 2:
                messageType = TMessageType.ROTA_GUIADA;
                break;
            case 3  :
                messageType = TMessageType.PUSH;
                break;
        }

        return messageType;
    }
}
