package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

public class MessageAccess {

    @SerializedName("ID_MENSAGEM")
    private int idMessage;

    @SerializedName("ID_FUNCAO")
    private String idFuncao;

    @SerializedName("ID_FILTRO")
    private String idFiltro;

    @SerializedName("COD_REG_FUNC")
    private String codRecFunc;

    @SerializedName("DT_ULT_ALT")
    private String dtUltAlt;

    @SerializedName("DEL_FLAG")
    private String delFlag;

    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public String getIdFuncao() {
        return idFuncao;
    }

    public void setIdFuncao(String idFuncao) {
        this.idFuncao = idFuncao;
    }

    public String getIdFiltro() {
        return idFiltro;
    }

    public void setIdFiltro(String idFiltro) {
        this.idFiltro = idFiltro;
    }

    public String getCodRecFunc() {
        return codRecFunc;
    }

    public void setCodRecFunc(String codRecFunc) {
        this.codRecFunc = codRecFunc;
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
