package br.com.jjconsulting.mobile.dansales.model;

public enum TapConnectionType {
    TAP_LIST(1),
    TAP_ADD(2),
    TAP_DETAIL(3),
    TAP_ALTERAMC(4),
    TAP_SAVE(5),
    TAP_INCLUIR_ITEM(6),
    TAP_DELETAR_ITEM(7),
    TAP_DENVIA(8),
    TAP_DELETE(9),
    TAP_LOTE(10),
    TAP_MASTER_CONTRATO(11),
    TAP_REL_SALDO_MC(12),
    TAP_ADD_RAT(13),
    TAP_ADD_RASC(14);

    private final int value;

    TapConnectionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TapConnectionType getTapConnectionType(int value) {
        switch (value) {
            case 1:
                return TapConnectionType.TAP_LIST;
            case 2:
                return TapConnectionType.TAP_ADD;
            case 3:
                return TapConnectionType.TAP_DETAIL;
            case 4:
                return TapConnectionType.TAP_ALTERAMC;
            case 5:
                return TapConnectionType.TAP_SAVE;
            case 8:
                return TapConnectionType.TAP_DENVIA;
            case 7:
                return TapConnectionType.TAP_DELETAR_ITEM;
            case 9:
                return TapConnectionType.TAP_DELETE;
            case 10:
                return TapConnectionType.TAP_LOTE;
            case 11:
                return TapConnectionType.TAP_MASTER_CONTRATO;
            case 12:
                return TapConnectionType.TAP_REL_SALDO_MC;
            case 13:
                return TapConnectionType.TAP_ADD_RAT;
            case 14:
                return TapConnectionType.TAP_ADD_RASC;
            default:
                return TapConnectionType.TAP_LIST;
        }
    }
}
