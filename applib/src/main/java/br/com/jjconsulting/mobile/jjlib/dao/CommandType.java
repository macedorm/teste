package br.com.jjconsulting.mobile.jjlib.dao;


public enum CommandType {

    Text(1),
    StoredProcedure(4),
    TableDirect(512);

    private int intValue;

    CommandType(int value) {
        intValue = value;
    }
}
