package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapItemRules implements Serializable {
    private boolean arq;
    private boolean del;
    private boolean obs;
    private boolean vlrApur;
    private boolean vlrEst;

    public boolean isArq() {
        return arq;
    }

    public void setArq(boolean arq) {
        this.arq = arq;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public boolean isObs() {
        return obs;
    }

    public void setObs(boolean obs) {
        this.obs = obs;
    }

    public boolean isVlrApur() {
        return vlrApur;
    }

    public void setVlrApur(boolean vlrApur) {
        this.vlrApur = vlrApur;
    }

    public boolean isVlrEst() {
        return vlrEst;
    }

    public void setVlrEst(boolean vlrEst) {
        this.vlrEst = vlrEst;
    }
}
