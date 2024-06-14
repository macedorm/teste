package br.com.jjconsulting.mobile.jjlib.model;

public enum DateCompare {
    EQUAL(0),
    BERFORE(-1),
    AFTER(1);

    private final int value;

    private DateCompare(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
