package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;


public class TapSend implements Serializable {
    private String resp;
    private String obs;
    private int idMotRep;
    private int nivel;
    private String tipo;

    public String getResp() {
        return resp;
    }

    public void setResp(String resp) {
        this.resp = resp;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public int getIdMotRep() {
        return idMotRep;
    }

    public void setIdMotRep(int idMotRep) {
        this.idMotRep = idMotRep;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
