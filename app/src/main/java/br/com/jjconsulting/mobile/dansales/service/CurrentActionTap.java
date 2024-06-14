package br.com.jjconsulting.mobile.dansales.service;

public class CurrentActionTap {

    private static CurrentActionTap current;

    private boolean isUpdateListTap;
    private boolean isUpdateListItem;

    public static CurrentActionTap getInstance() {
        if (current == null) {
            current = new CurrentActionTap();
        }
        return current;
    }

    private CurrentActionTap() {
    }

    public static CurrentActionTap getCurrent() {
        return current;
    }

    public boolean isUpdateListTap() {
        return isUpdateListTap;
    }

    public void setUpdateListTap(boolean updateListTap) {
        isUpdateListTap = updateListTap;
    }

    public boolean isUpdateListItem() {
        return isUpdateListItem;
    }

    public void setUpdateListItem(boolean updateListItem) {
        isUpdateListItem = updateListItem;
    }

    public void clear() {
        isUpdateListTap = false;
        isUpdateListItem = false;
    }
}
