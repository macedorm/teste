package br.com.jjconsulting.mobile.jjlib.dao.entity;

public enum TPesquisaEdit {
    VIEW_ONLY_ANSWER (0),
    VIEW_AND_EDIT_ANSWER(1),
    NO_VIEW_AND_EDIT_ANSWER(2);

    private int intValue;

    TPesquisaEdit(int value) {
        intValue = value;
    }

    public int getValue() {
        return intValue;
    }

    public static TPesquisaEdit fromInteger(int x) {
        switch(x) {
            case 0:
                return VIEW_ONLY_ANSWER;
            case 1:
                return VIEW_AND_EDIT_ANSWER;
            case 2:
                return NO_VIEW_AND_EDIT_ANSWER;
        }
        return null;
    }
}
