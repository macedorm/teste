package br.com.jjconsulting.mobile.dansales.data;

import com.google.gson.annotations.SerializedName;

public class SyncPesquisaLayout {
    @SerializedName("CodAux")
    private String codAux;
    @SerializedName("CodErr")
    private String codErr;
    @SerializedName("DescErr")
    private String descErr;
    @SerializedName("TextAux")
    private String TextAux;

    public String getCodAux() {
        return codAux;
    }

    public void setCodAux(String codAux) {
        this.codAux = codAux;
    }

    public String getCodErr() {
        return codErr;
    }

    public void setCodErr(String codErr) {
        this.codErr = codErr;
    }

    public String getDescErr() {
        return descErr;
    }

    public void setDescErr(String descErr) {
        this.descErr = descErr;
    }

    public String getTextAux() {
        return TextAux;
    }

    public void setTextAux(String textAux) {
        TextAux = textAux;
    }
}
