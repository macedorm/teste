package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

public class RetProcess {
    @SerializedName("CodErr")
    private int codErr;

    @SerializedName("DescErr")
    private String descErr;

    @SerializedName("CodAux")
    private String codAux;

    @SerializedName("TextAux")
    private String textAux;


    public int getCodErr() {
        return codErr;
    }

    public void setCodErr(int codErr) {
        this.codErr = codErr;
    }

    public String getDescErr() {
        return descErr;
    }

    public void setDescErr(String descErr) {
        this.descErr = descErr;
    }

    public String getCodAux() {
        return codAux;
    }

    public void setCodAux(String codAux) {
        this.codAux = codAux;
    }

    public String getTextAux() {
        return textAux;
    }

    public void setTextAux(String textAux) {
        this.textAux = textAux;
    }
}
