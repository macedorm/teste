package br.com.jjconsulting.mobile.dansales.service;

public class CurrentActionPedido {

    private static CurrentActionPedido current;

    private boolean isUpdateListPedido;
    private boolean isUpdateListItem;

    public static CurrentActionPedido getInstance() {
        if (current == null) {
            current = new CurrentActionPedido();
        }
        return current;
    }

    private CurrentActionPedido() { }

    public static CurrentActionPedido getCurrent() {
        return current;
    }

    public static void setCurrent(CurrentActionPedido current) {
        CurrentActionPedido.current = current;
    }

    public boolean isUpdateListPedido() {
        return isUpdateListPedido;
    }

    public void setUpdateListPedido(boolean updateListPedido) {
        isUpdateListPedido = updateListPedido;
    }

    public boolean isUpdateListItem() {
        return isUpdateListItem;
    }

    public void setUpdateListItem(boolean updateListItem) {
        isUpdateListItem = updateListItem;
    }

    public void clear() {
        isUpdateListPedido = false;
        isUpdateListItem = false;
    }
}
