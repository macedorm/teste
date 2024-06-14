package br.com.jjconsulting.mobile.dansales.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class TapItem implements Serializable {

    private String obs;
    private TapTipoInvest tipo;
    private double vlApur;
    private TapProdCateg categProd;
    private boolean apFin;
    private int id;
    private int idTap;
    private boolean valorEstimado;
    private double vlEst;
    private TapAcao acao;
    private ArrayList<Anexo> anexos;
    private TapItemRules rules;
    private String urlAnexo;

    public void setTapTipoInvest(TapTipoInvest tipo) {
        this.tipo = tipo;
    }

    public TapTipoInvest getTapTipoInvest() {
        return tipo;
    }

    public void setVlApur(double vlApur) {
        this.vlApur = vlApur;
    }

    public double getVlApur() {
        return vlApur;
    }

    public void setTapProdCateg(TapProdCateg tapProdCateg) {
        this.categProd = tapProdCateg;
    }

    public TapProdCateg getTapProdCateg() {
        return categProd;
    }

    public void setApFin(boolean apFin) {
        this.apFin = apFin;
    }

    public boolean isApFin() {
        return apFin;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setValorEstimado(boolean valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public boolean isValorEstimado() {
        return valorEstimado;
    }

    public void setVlEst(double vlEst) {
        this.vlEst = vlEst;
    }

    public double getVlEst() {
        return vlEst;
    }

    public void setTapAcao(TapAcao tapAcao) {
        this.acao = tapAcao;
    }

    public TapAcao getTapAcao() {
        return acao;
    }

    public int getIdTap() {
        return idTap;
    }

    public void setIdTap(int idTap) {
        this.idTap = idTap;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public String getObs() {
        return obs;
    }

    public ArrayList<Anexo> getAnexos() {
        return anexos;
    }

    public void setAnexos(ArrayList<Anexo> anexos) {
        this.anexos = anexos;
    }

    public TapItemRules getTapItemRules() {
        return rules;
    }

    public void setTapItemRules(TapItemRules tapItemRules) {
        this.rules = tapItemRules;
    }

    public String getUrlAnexo() {
        return urlAnexo;
    }

    public void setUrlAnexo(String urlAnexo) {
        this.urlAnexo = urlAnexo;
    }
}