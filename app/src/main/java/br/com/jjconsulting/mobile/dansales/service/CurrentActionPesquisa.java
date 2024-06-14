package br.com.jjconsulting.mobile.dansales.service;

public class CurrentActionPesquisa {

    private static CurrentActionPesquisa current;

    private boolean isUpdateListPesquisa;

    public static CurrentActionPesquisa getInstance() {
        if (current == null) {
            current = new CurrentActionPesquisa();
        }
        return current;
    }

    private CurrentActionPesquisa() {
    }

    public static CurrentActionPesquisa getCurrent() {
        return current;
    }

    public static void setCurrent(CurrentActionPesquisa current) {
        CurrentActionPesquisa.current = current;
    }

    public boolean isUpdateListPesquisa() {
        return isUpdateListPesquisa;
    }

    public void setUpdateListPesquisa(boolean updateListPesquisa) {
        this.isUpdateListPesquisa = updateListPesquisa;
    }
}
