package br.com.jjconsulting.mobile.dansales.util;

public enum TJustTask {
    PEDIDO(1),
    PESQUISA(2);

    private int intValue;

    TJustTask(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TJustTask fromInteger(int value){
        TJustTask justTask = TJustTask.PEDIDO;
        switch (value) {
            case 1:
                justTask = TJustTask.PEDIDO;
                break;
            case 2:
                justTask = TJustTask.PESQUISA;
                break;
        }

        return justTask;
    }
}
