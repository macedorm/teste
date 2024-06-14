package br.com.jjconsulting.mobile.dansales.model;

public enum RotaGuiadaTaskType {
    PEDIDO(1),
    PESQUISA(2);

    private final int value;

    RotaGuiadaTaskType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RotaGuiadaTaskType getRotaGuiadaTaskType(int value) {
        switch (value) {
            case 1:
                return RotaGuiadaTaskType.PEDIDO;
            case 2:
                return RotaGuiadaTaskType.PESQUISA;
             default:
                 return RotaGuiadaTaskType.PEDIDO;
        }
    }

}
