package br.com.jjconsulting.mobile.dansales.model;

public enum RotaGuiadaStatusType {
    EM_ANDAMENTO(1),
    FORA_ROTA(2),
    FINALIZADO(3),
    NÃO_INICIADO(4),
    NAO_REALIZADO(5);

    private final int value;

    RotaGuiadaStatusType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RotaGuiadaStatusType getRotaGuiadaStatusType(int value) {
        switch (value) {
            case 1:
                return RotaGuiadaStatusType.EM_ANDAMENTO;
            case 2:
                return RotaGuiadaStatusType.FORA_ROTA;
            case 3:
                return RotaGuiadaStatusType.FINALIZADO;
            case 4:
                return RotaGuiadaStatusType.NÃO_INICIADO;
            case 5:
                return RotaGuiadaStatusType.NAO_REALIZADO;
             default:
                 return RotaGuiadaStatusType.EM_ANDAMENTO;

        }
    }


}
