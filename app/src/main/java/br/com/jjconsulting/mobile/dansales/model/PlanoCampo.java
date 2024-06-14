package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PlanoCampo implements Serializable {

    private String validade;
    private String visSeg;
    private String visTer;
    private String visQua;
    private String visQui;
    private String visSex;
    private String visSab;
    private String visDom;

    public String getValidade() {
        return validade;
    }

    public void setValidade(String validade) {
        this.validade = validade;
    }

    public String getVisSeg() {
        return visSeg;
    }

    public void setVisSeg(String visSeg) {
        this.visSeg = visSeg;
    }

    public String getVisTer() {
        return visTer;
    }

    public void setVisTer(String visTer) {
        this.visTer = visTer;
    }

    public String getVisQua() {
        return visQua;
    }

    public void setVisQua(String visQua) {
        this.visQua = visQua;
    }

    public String getVisQui() {
        return visQui;
    }

    public void setVisQui(String visQui) {
        this.visQui = visQui;
    }

    public String getVisSex() {
        return visSex;
    }

    public void setVisSex(String visSex) {
        this.visSex = visSex;
    }

    public String getVisSab() {
        return visSab;
    }

    public void setVisSab(String visSab) {
        this.visSab = visSab;
    }

    public String getVisDom() {
        return visDom;
    }

    public void setVisDom(String visDom) {
        this.visDom = visDom;
    }
}
