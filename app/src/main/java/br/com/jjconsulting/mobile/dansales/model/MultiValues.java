package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class MultiValues implements Serializable {

    private int codTable;
    private int valCod;
    private String desc;

    public int getCodTable() {
        return codTable;
    }

    public void setCodTable(int codTable) {
        this.codTable = codTable;
    }

    public int getValCod() {
        return valCod;
    }

    public void setValCod(int valCod) {
        this.valCod = valCod;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
