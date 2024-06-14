package br.com.jjconsulting.mobile.dansales.model;

public enum PedidoViewType {
    PEDIDO(1),
    APROVACAO(2),
    LIBERACAO(3);

    private final int value;

    private PedidoViewType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
}
