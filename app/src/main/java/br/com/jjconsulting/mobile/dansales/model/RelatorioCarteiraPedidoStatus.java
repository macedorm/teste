package br.com.jjconsulting.mobile.dansales.model;

/**
 * Created by jjconsulting on 18/05/2018.
 */

public enum  RelatorioCarteiraPedidoStatus {

    FATURADO_TOTAL(1),
    CANCELADO(2),
    EM_ABERTO(3),
    FATURADO_PARCIAL(4),
    CORTE_TOTAL(5);

    private final int value;

    private RelatorioCarteiraPedidoStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;

    }

}
