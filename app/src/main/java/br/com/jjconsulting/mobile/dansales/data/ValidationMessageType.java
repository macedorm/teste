package br.com.jjconsulting.mobile.dansales.data;

public enum ValidationMessageType {
    ERROR(1),
    INFO(2),
    ALERT(3),
    SUCCESS(4);

    private final int value;

    private ValidationMessageType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;

    }
}
