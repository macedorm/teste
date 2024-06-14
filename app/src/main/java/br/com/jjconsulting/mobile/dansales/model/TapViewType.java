package br.com.jjconsulting.mobile.dansales.model;

public enum TapViewType {
    VISUALIZAR(1),
    EDITAR(2);

    private final int value;

    TapViewType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;

    }

}
