package br.com.jjconsulting.mobile.dansales.model;

public enum TapActionType {
    TAP_LIST(1),
    TAP_ANALISE_FINANCEIRA(2),
    TAP_CONSULTA(3);

    private final int value;

    TapActionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TapActionType getTapActionType(int value) {
        switch (value) {
            case 1:
                return TapActionType.TAP_LIST;
            case 2:
                return TapActionType.TAP_ANALISE_FINANCEIRA;
            case 3:
                return TapActionType.TAP_CONSULTA;
            default:
                return TapActionType.TAP_LIST;
        }
    }

    public static String getTapActionTypeParam(TapActionType tapActionType) {
        switch (tapActionType) {
            case TAP_LIST:
                return "padrao";
            case TAP_ANALISE_FINANCEIRA:
                return "financas";
            case TAP_CONSULTA:
                return "consulta";
            default:
                return "padrao";
        }
    }
}
