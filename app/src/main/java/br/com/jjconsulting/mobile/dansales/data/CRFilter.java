package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.Date;

public class CRFilter implements Serializable {

    private String codigo;
    private String regiao;
    private String regioes[];

    private Date dateStart;


    public CRFilter(){

    }

    public CRFilter(String regiao, Date dateStart) {
        this.regiao = regiao;
        this.dateStart = dateStart;
    }

    public String getRegiao() {
        return regiao;
    }


    public boolean isEmpty() {
        return regiao == null && dateStart == null;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setRegiao(String regiao) {
        this.regiao = regiao;
    }

    public String[] getRegioes() {
        return regioes;
    }

    public void setRegioes(String[] regioes) {
        this.regioes = regioes;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }


}