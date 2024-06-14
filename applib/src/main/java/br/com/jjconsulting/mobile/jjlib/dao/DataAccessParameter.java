package br.com.jjconsulting.mobile.jjlib.dao;


import java.util.Date;

public class DataAccessParameter {
    public String name;
    public Object value;
    public DbType type;
    public int size;
    public ParameterDirection direction;

    public DataAccessParameter() { }

    public DataAccessParameter(String name, String value)
    {
        this.name = name;
        this.value = value;
        this.type = DbType.String;
        this.direction = ParameterDirection.Input;
    }

    public DataAccessParameter(String name, int value)
    {
        this.name = name;
        this.value = value;
        this.type = DbType.Int32;
        this.direction = ParameterDirection.Input;
    }

    public DataAccessParameter(String name, float value)
    {
        this.name = name;
        this.value = value;
        this.type = DbType.Double;
        this.direction = ParameterDirection.Input;
    }

    public DataAccessParameter(String name, Date value)
    {
        this.name = name;
        this.value = value;
        this.type = DbType.DateTime;
        this.direction = ParameterDirection.Input;
    }

    public DataAccessParameter(String name, Object value, DbType type)
    {
        this.name = name;
        this.value = value;
        this.type = type;
        this.direction = ParameterDirection.Input;
    }

    public DataAccessParameter(String name, Object value, DbType type, int size)
    {
        this.name = name;
        this.value = value;
        this.type = type;
        this.size = size;
        this.direction = ParameterDirection.Input;
    }

    public DataAccessParameter(String name, Object value, DbType type, int size, ParameterDirection direction)
    {
        this.name = name;
        this.value = value;
        this.type = type;
        this.size = size;
        this.direction = direction;
    }

    public DataAccessParameter(String name, Object value, DbType type, ParameterDirection direction)
    {
        this.name = name;
        this.value = value;
        this.type = type;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public DbType getType() {
        return type;
    }

    public void setType(DbType type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ParameterDirection getDirection() {
        return direction;
    }

    public void setDirection(ParameterDirection direction) {
        this.direction = direction;
    }
}

