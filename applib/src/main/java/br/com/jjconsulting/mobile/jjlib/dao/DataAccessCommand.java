package br.com.jjconsulting.mobile.jjlib.dao;


public class DataAccessCommand {
    public CommandType cmdType;
    public String sql;
    public String parameters[];

    public DataAccessCommand()
    {
        cmdType = CommandType.Text;
    }

    public DataAccessCommand(String sql)
    {
        this.sql = sql;
        this.cmdType = CommandType.Text;
    }

    public DataAccessCommand(String sql, String parms[])
    {
        this.sql = sql;
        this.parameters = parms;
        this.cmdType = CommandType.Text;
    }


    public DataAccessCommand(String sql, String parms[], CommandType type)
    {
        this.sql = sql;
        this.parameters = parms;
        this.cmdType = type;
    }

    public CommandType getCmdType() {
        return cmdType;
    }

    public void setCmdType(CommandType cmdType) {
        this.cmdType = cmdType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String[] parameters) {
        this.parameters = parameters;
    }
}
