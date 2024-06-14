package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapCabec implements Serializable {

    private int status;
    private String obs;
    private String codigo;
    private String dataInc;
    private int codUsuaEnv;
    private TapRegiao regiao;
    private String numAcordCli;
    private int posUsuaInc;
    private String anoMesAcao;
    private String dataPrevPagto;
    private TapMasterContrato masterContrato;
    private String cbu;
    private double fatLiq;
    private int id;
    private TapCliente cliente;
    private String contrDeal;
    private boolean isRascunho;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getObs() {
        return obs;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setDataInc(String dataInc) {
        this.dataInc = dataInc;
    }

    public String getDataInc() {
        return dataInc;
    }

    public void setCodUsuaEnv(int codUsuaEnv) {
        this.codUsuaEnv = codUsuaEnv;
    }

    public int getCodUsuaEnv() {
        return codUsuaEnv;
    }

    public void setTapRegiao(TapRegiao tapRegiao) {
        this.regiao = tapRegiao;
    }

    public TapRegiao getTapRegiao() {
        return regiao;
    }

    public void setNumAcordCli(String numAcordCli) {
        this.numAcordCli = numAcordCli;
    }

    public String getNumAcordCli() {
        return numAcordCli;
    }

    public void setPosUsuaInc(int posUsuaInc) {
        this.posUsuaInc = posUsuaInc;
    }

    public int getPosUsuaInc() {
        return posUsuaInc;
    }

    public void setAnoMesAcao(String anoMesAcao) {
        this.anoMesAcao = anoMesAcao;
    }

    public String getAnoMesAcao() {
        return anoMesAcao;
    }

    public void setDataPrevPagto(String dataPrevPagto) {
        this.dataPrevPagto = dataPrevPagto;
    }

    public String getDataPrevPagto() {
        return dataPrevPagto;
    }

    public void setTapMasterContrato(TapMasterContrato tapMasterContrato) {
        this.masterContrato = tapMasterContrato;
    }

    public TapMasterContrato getTapMasterContrato() {
        return masterContrato;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public String getCbu() {
        return cbu;
    }

    public void setFatLiq(double fatLiq) {
        this.fatLiq = fatLiq;
    }

    public double getFatLiq() {
        return fatLiq;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTapCliente(TapCliente tapCliente) {
        this.cliente = tapCliente;
    }

    public TapCliente getTapCliente() {
        return cliente;
    }

    public void setContrDeal(String contrDeal) {
        this.contrDeal = contrDeal;
    }

    public String getContrDeal() {
        return contrDeal;
    }

    public boolean isRascunho() {
        return isRascunho;
    }

    public void setRascunho(boolean rascunho) {
        isRascunho = rascunho;
    }
}