package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TapDetail implements Serializable {

    private String tapCod;

    private ArrayList<WebUser> listUser;

    private List<TapItem> itens;
    private List<TapStep> listStep;
    private List<TapMaterInfo> listMaterInfo;
    private List<TapComboMesAcao> listAnoMes;
    private List<TapComboRegiao> listRegiao;

    private TapCabec cabec;

    private Object comboList;

    private String nomeConsSaldo;
    private String obs;
    private String descErr;
    private String codErr;

    private boolean isEdit;
    private boolean isCancel;
    private boolean isAprov;
    private boolean isBloq;
    private boolean isDes;

    public String getTapCod() {
        return tapCod;
    }

    public void setTapCod(String tapCod) {
        this.tapCod = tapCod;
    }

    public String getCodErr() {
        return codErr;
    }

    public void setItens(List<TapItem> itens) {
        this.itens = itens;
    }

    public List<TapItem> getItens() {
        return itens;
    }

    public List<TapStep> getListStep() {
        return listStep;
    }

    public String getDescErr() {
        return descErr;
    }

    public List<TapMaterInfo> getListMaterInfo() {
        return listMaterInfo;
    }

    public TapCabec getCabec() {
        return cabec;
    }

    public List<TapComboMesAcao> getListComboAnoMes() {
        return listAnoMes;
    }

    public void setListComboAnoMes(List<TapComboMesAcao> listAnoMes) {
        this.listAnoMes = listAnoMes;
    }

    public List<TapComboRegiao> getListComboRegiao() {
        return listRegiao;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public boolean isAprov() {
        return isAprov;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public boolean isBloq() {
        return isBloq;
    }

    public boolean isDes() {
        return isDes;
    }

    public void setDes(boolean des) {
        isDes = des;
    }

    public ArrayList<WebUser> getListUser() {
        return listUser;
    }

    public String getNomeConsSaldo() {
        return nomeConsSaldo;
    }

}