package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapStep implements Serializable {
    private String user;
    private String action;
    private String dateCad;
    private String obs;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDateCad() {
        return dateCad;
    }

    public void setDateCad(String dateCad) {
        this.dateCad = dateCad;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }
}
