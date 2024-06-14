package br.com.jjconsulting.mobile.jjlib.dao;


public enum ParameterDirection {

    Input(1),
    Output(2),
    InputOutput(3),
    ReturnValue(6);

    private int intValue;

    ParameterDirection(int value) {
        intValue = value;
    }
}
