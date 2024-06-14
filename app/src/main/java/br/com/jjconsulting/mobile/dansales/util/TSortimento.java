package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;

import br.com.jjconsulting.mobile.dansales.R;

public enum TSortimento {
    OBRIGATORIO(1),
    INOVACAO(2),
    RECOMENDADO(3),
    SUBSTITUTO(4);

    private int intValue;

    TSortimento(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TSortimento fromInteger(int value){
        TSortimento sortimentoStatus = TSortimento.OBRIGATORIO;
        switch (value) {
            case 1:
                sortimentoStatus = TSortimento.OBRIGATORIO;
                break;
            case 2:
                sortimentoStatus = TSortimento.INOVACAO;
                break;
            case 3:
                sortimentoStatus = TSortimento.RECOMENDADO;
                break;
            case 4:
                sortimentoStatus = TSortimento.SUBSTITUTO;
                break;
        }

        return sortimentoStatus;
    }

    public static String getDescriptionSortimento(Context context, int value){

        if(value == -1){
            return "";
        } else {
            TSortimento sortimentoStatus = TSortimento.fromInteger(value);

            switch (sortimentoStatus) {
                case OBRIGATORIO:
                    return  context.getString(R.string.resumo_sortimento_obrigatorio);
                case INOVACAO:
                    return  " " + context.getString(R.string.resumo_sortimento_inovacao) + " ";
                case RECOMENDADO:
                    return  context.getString(R.string.resumo_sortimento_recomendado);
                case SUBSTITUTO:
                    return  context.getString(R.string.resumo_sortimento_substituto);
                default:

            }

            return "";
        }

    }


    public static int getColorSortimento(Context context, int value){
        if(value == -1){
            return  context.getResources().getColor(R.color.crSortimentoObrigatorio);
        } else {
            TSortimento sortimentoStatus = TSortimento.fromInteger(value);

            switch (sortimentoStatus) {
                case OBRIGATORIO:
                    return  context.getResources().getColor(R.color.crSortimentoObrigatorio);
                case INOVACAO:
                    return  context.getResources().getColor(R.color.crSortimentoInovacao);
                case RECOMENDADO:
                    return  context.getResources().getColor(R.color.crSortimentoRecomendado);
                case SUBSTITUTO:
                    return  context.getResources().getColor(R.color.crSortimentoSubstituto);
                default:
                    return  context.getResources().getColor(R.color.crSortimentoObrigatorio);
            }
        }
    }
}
