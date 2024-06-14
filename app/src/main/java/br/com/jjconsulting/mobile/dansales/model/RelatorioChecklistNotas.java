package br.com.jjconsulting.mobile.dansales.model;


import java.io.Serializable;
import java.util.List;

public class RelatorioChecklistNotas implements Serializable {

    private String idPesquisa;
    private String nomePesquisa;
    private String codRegFunc;
    private String codigoCliente;
    private String nomeCliente;
    private String dataResposta;

    private String nota;
    private String espaco;


    public String getNomePesquisa() {
        return nomePesquisa;
    }

    public void setNomePesquisa(String nomePesquisa) {
        this.nomePesquisa = nomePesquisa;
    }

    public String getIdPesquisa() {
        return idPesquisa;
    }

    public void setIdPesquisa(String idPesquisa) {
        this.idPesquisa = idPesquisa;
    }

    public String getCodRegFunc() {
        return codRegFunc;
    }

    public void setCodRegFunc(String codRegFunc) {
        this.codRegFunc = codRegFunc;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(String dataResposta) {
        this.dataResposta = dataResposta;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getEspaco() {
        return espaco;
    }

    public void setEspaco(String espaco) {
        this.espaco = espaco;
    }
}
