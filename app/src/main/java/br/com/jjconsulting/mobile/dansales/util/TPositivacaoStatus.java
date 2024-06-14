package br.com.jjconsulting.mobile.dansales.util;

public enum TPositivacaoStatus {
    SEMREGRA(0),
    POSITIVADO(1),
    NAO_POSITIVADO(2),
    SEM_CADASTRO(3);

    private int intValue;

    TPositivacaoStatus(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TPositivacaoStatus fromInteger(int value){
        TPositivacaoStatus positivacaoStatus = TPositivacaoStatus.SEMREGRA;
        switch (value) {
            case 0:
                positivacaoStatus = TPositivacaoStatus.SEMREGRA;
                break;
            case 1:
                positivacaoStatus = TPositivacaoStatus.POSITIVADO;
                break;
            case 2:
                positivacaoStatus = TPositivacaoStatus.NAO_POSITIVADO;
                break;
            case 3:
                positivacaoStatus = TPositivacaoStatus.SEM_CADASTRO;
                break;
        }

        return positivacaoStatus;
    }

}
