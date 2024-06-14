package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class RelatorioNotasItem implements Serializable {
    private String nome;
    private String id;
    private double qtd;
    private double icmsSt;
    private double vlrIcms;
    private double vlrIpi;
    private double vlrDesc;
    private double valorTotal;
    private double vlrUnitario;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getQtd() {
        return qtd;
    }

    public void setQtd(double qtd) {
        this.qtd = qtd;
    }

    public double getIcmsSt() {
        return icmsSt;
    }

    public void setIcmsSt(double icmsSt) {
        this.icmsSt = icmsSt;
    }

    public double getVlrIpi() {
        return vlrIpi;
    }

    public void setVlrIpi(double vlrIpi) {
        this.vlrIpi = vlrIpi;
    }

    public double getVlrDesc() {
        return vlrDesc;
    }

    public void setVlrDesc(double vlrDesc) {
        this.vlrDesc = vlrDesc;
    }

    public double getVlrIcms() {
        return vlrIcms;
    }

    public void setVlrIcms(double vlrIcms) {
        this.vlrIcms = vlrIcms;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getVlrUnitario() {
        return vlrUnitario;
    }

    public void setVlrUnitario(double vlrUnitario) {
        this.vlrUnitario = vlrUnitario;
    }
}
