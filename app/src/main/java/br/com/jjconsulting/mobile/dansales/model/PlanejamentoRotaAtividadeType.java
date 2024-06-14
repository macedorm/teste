package br.com.jjconsulting.mobile.dansales.model;

import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public enum PlanejamentoRotaAtividadeType {

    VISITA(1),
    VISITA_PROMOTOR(2),
    PONTO_ENCONTRO(3),
    DIA_PLANEJAMENTO(4),
    REUNIAO_VENDAS(5),
    ROTINAS_ADMINISTRATIVAS(6);

    private final int value;

    private PlanejamentoRotaAtividadeType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PlanejamentoRotaAtividadeType getEnumValue(int value) {
        switch (value) {
            case 1:
                return PlanejamentoRotaAtividadeType.VISITA;
            case 2:
                return PlanejamentoRotaAtividadeType.VISITA_PROMOTOR;
            case 3:
                return PlanejamentoRotaAtividadeType.PONTO_ENCONTRO;
            case 4:
                return PlanejamentoRotaAtividadeType.DIA_PLANEJAMENTO;
            case 5:
                return PlanejamentoRotaAtividadeType.REUNIAO_VENDAS;
            case 6:
                return PlanejamentoRotaAtividadeType.ROTINAS_ADMINISTRATIVAS;
            default:
                return null;

        }
    }

    public static String getLabelEnum(int value, String cliente, boolean stringFull) {
        if(!stringFull){
            switch (value) {
                case 1:
                    return "Visita" + (TextUtils.isNullOrEmpty(cliente) ? "": "\n" + cliente);
                case 2:
                    return "Visita com promotor";
                case 3:
                    return "Ponto de encontro";
                case 4:
                    return "Dia de planejamento";
                case 5:
                    return "Reunião vendas";
                default:
                    return "Rotinas \nadmin";

            }
        } else {
            switch (value) {
                case 1:
                    return "Visita" + (TextUtils.isNullOrEmpty(cliente) ? "": "\n" + cliente);
                case 2:
                    return "Visita com promotor";
                case 3:
                    return "Ponto de encontro";
                case 4:
                    return "Dia de planejamento";
                case 5:
                    return "Reunião de vendas";
                default:
                    return "Rotinas administrativas";

            }
        }


    }
}
