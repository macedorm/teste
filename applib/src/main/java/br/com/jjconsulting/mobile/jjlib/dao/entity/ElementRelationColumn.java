package br.com.jjconsulting.mobile.jjlib.dao.entity;

public class ElementRelationColumn {

    private String pkcolumn;

    private String fkcolumn;

    public ElementRelationColumn() { }

    public ElementRelationColumn(String pkcolumn, String fkcolumn) {
        this.pkcolumn = pkcolumn;
        this.fkcolumn = fkcolumn;
    }

    public String getPkcolumn() {
        return pkcolumn;
    }

    public void setPkcolumn(String pkcolumn) {
        this.pkcolumn = pkcolumn;
    }

    public String getFkcolumn() {
        return fkcolumn;
    }

    public void setFkcolumn(String fkcolumn) {
        this.fkcolumn = fkcolumn;
    }
}
