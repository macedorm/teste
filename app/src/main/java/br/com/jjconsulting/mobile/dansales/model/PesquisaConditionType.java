package br.com.jjconsulting.mobile.dansales.model;

public enum PesquisaConditionType {

    IGUAL(1),
    DIFERENTE(2),
    ENTRE(3),
    MAIOR(4),
    MAIOR_OU_IGUAL(5),
    MENOR(6),
    MENOR_OU_IGUAL(7),
    CONTEM(8),
    NAO_CONTEM(9);

    private final int value;

    private PesquisaConditionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PesquisaConditionType getEnumValue(int value) {
        switch (value) {
            case 1:
                return PesquisaConditionType.IGUAL;
            case 2:
                return PesquisaConditionType.DIFERENTE;
            case 3:
                return PesquisaConditionType.ENTRE;
            case 4:
                return PesquisaConditionType.MAIOR;
            case 5:
                return PesquisaConditionType.MAIOR_OU_IGUAL;
            case 6:
                return PesquisaConditionType.MENOR;
            case 7:
                return PesquisaConditionType.MENOR_OU_IGUAL;
            case 8:
                return PesquisaConditionType.CONTEM;
            case 9:
                return PesquisaConditionType.NAO_CONTEM;
            default:
                return null;

        }
    }
}
