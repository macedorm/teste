package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RotaAcao implements Serializable {
	@SerializedName("RGA_DAT_DIAPLANO")
    private String diaPlano;
    @SerializedName("RGA_TXT_CODCLI")
    private String codCliente;
    @SerializedName("RGA_TXT_UNIDNEGOC")
    private String unidadeNegocio;
    @SerializedName("RGA_INT_TIPO")
    private int tipo;
    @SerializedName("RGA_DAT_ACAO")
    private String dataAction;
    @SerializedName("RGA_FLO_LATITUDE")
    private double latitude;
    @SerializedName("RGA_FLO_LONGITUDE")
    private double longitude;
    @SerializedName("RGA_TXT_DENTRO_RAIO")
    private String raio;
    @SerializedName("COD_REG_FUNC")
    private String codRegFunc;
    @SerializedName("DT_ULT_ALT")
    private String dtUltAlt;
    @SerializedName("DEL_FLAG")
    private String delFlag;

    public String getDiaPlano() {
        return diaPlano;
    }

    public void setDiaPlano(String diaPlano) {
        this.diaPlano = diaPlano;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getUnidadeNegocio() {
        return unidadeNegocio;
    }

    public void setUnidadeNegocio(String unidadeNegocio) {
        this.unidadeNegocio = unidadeNegocio;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getDataAction() {
        return dataAction;
    }

    public void setDataAction(String dataAction) {
        this.dataAction = dataAction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getRaio() {
        return raio;
    }

    public void setRaio(String raio) {
        this.raio = raio;
    }

    public String getCodRegFunc() {
        return codRegFunc;
    }

    public void setCodRegFunc(String codRegFunc) {
        this.codRegFunc = codRegFunc;
    }

    public String getDtUltAlt() {
        return dtUltAlt;
    }

    public void setDtUltAlt(String dtUltAlt) {
        this.dtUltAlt = dtUltAlt;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}