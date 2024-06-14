package br.com.jjconsulting.mobile.dansales.model;

public enum RotaGuiadaTabType {
    CLIENTE_FRAGMENT(0),
    RESUMO_FRAGMENT(1);

    private final int value;

    RotaGuiadaTabType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RotaGuiadaTabType getRotaGuiadaTabType(int value) {
        switch (value) {
            case 0:
                return RotaGuiadaTabType.CLIENTE_FRAGMENT;
            case 1:
                return RotaGuiadaTabType.RESUMO_FRAGMENT;
            default:
                return RotaGuiadaTabType.CLIENTE_FRAGMENT;
        }
    }

    }

