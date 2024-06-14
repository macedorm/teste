package br.com.jjconsulting.mobile.dansales.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class TapList  implements Serializable {

    private int id;
    private String codigo;
    private String dataInc;
    private int status;
    private int posUsuaInc;
    private String anoMesAcao;
    private String cliCod;
    private String cliNome;
    private String statusNome;
    private String masterContrato;
    private Double percFatLiq;
    private Double vlEst;
    private Double vlApur;
    private boolean isAprov;

    private boolean isCheckdLote;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDataInc() {
        return dataInc;
    }

    public void setDataInc(String dataInc) {
        this.dataInc = dataInc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPosUsuaInc() {
        return posUsuaInc;
    }

    public void setPosUsuaInc(int posUsuaInc) {
        this.posUsuaInc = posUsuaInc;
    }

    public String getAnoMesAcao() {
        return anoMesAcao;
    }

    public void setAnoMesAcao(String anoMesAcao) {
        this.anoMesAcao = anoMesAcao;
    }

    public String getCliCod() {
        return cliCod;
    }

    public void setCliCod(String cliCod) {
        this.cliCod = cliCod;
    }

    public String getCliNome() {
        return cliNome;
    }

    public void setCliNome(String cliNome) {
        this.cliNome = cliNome;
    }

    public String getStatusNome() {
        return statusNome;
    }

    public void setStatusNome(String statusNome) {
        this.statusNome = statusNome;
    }

    public String getMasterContrato() {
        return masterContrato;
    }

    public void setMasterContrato(String masterContrato) {
        this.masterContrato = masterContrato;
    }

    public Double getPercFatLiq() {
        return percFatLiq;
    }

    public void setPercFatLiq(Double percFatLiq) {
        this.percFatLiq = percFatLiq;
    }

    public Double getVlEst() {
        return vlEst;
    }

    public void setVlEst(Double vlEst) {
        this.vlEst = vlEst;
    }

    public Double getVlApur() {
        return vlApur;
    }

    public void setVlApur(Double vlApur) {
        this.vlApur = vlApur;
    }

    public boolean isCheckdLote() {
        return isCheckdLote;
    }

    public void setCheckdLote(boolean checkdLote) {
        isCheckdLote = checkdLote;
    }

    public boolean isAprov() {
        return isAprov;
    }

    public void setAprov(boolean aprov) {
        isAprov = aprov;
    }
}
