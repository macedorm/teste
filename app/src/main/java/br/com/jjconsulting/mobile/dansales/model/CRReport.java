package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

public class CRReport {

    private String cod_cliente;
    private String cod_reg_func;
    private String sales_organization;
    private String layout_old;
    private String layout_new;
    private String base64_arquivo;
    private String nome_arquivo;
    private String dt_inc;
    private String dt_ult_alt;

    private int status;

    public String getCod_cliente() {
        return cod_cliente;
    }

    public void setCod_cliente(String cod_cliente) {
        this.cod_cliente = cod_cliente;
    }

    public String getCod_reg_func() {
        return cod_reg_func;
    }

    public void setCod_reg_func(String cod_reg_func) {
        this.cod_reg_func = cod_reg_func;
    }

    public String getSales_organization() {
        return sales_organization;
    }

    public void setSales_organization(String sales_organization) {
        this.sales_organization = sales_organization;
    }

    public String getLayout_old() {
        return layout_old;
    }

    public void setLayout_old(String layout_old) {
        this.layout_old = layout_old;
    }

    public String getLayout_new() {
        return layout_new;
    }

    public void setLayout_new(String layout_new) {
        this.layout_new = layout_new;
    }

    public String getBase64_arquivo() {
        return base64_arquivo;
    }

    public void setBase64_arquivo(String base64_arquivo) {
        this.base64_arquivo = base64_arquivo;
    }

    public String getNome_arquivo() {
        return nome_arquivo;
    }

    public void setNome_arquivo(String nome_arquivo) {
        this.nome_arquivo = nome_arquivo;
    }

    public String getDt_inc() {
        return dt_inc;
    }

    public void setDt_inc(String dt_inc) {
        this.dt_inc = dt_inc;
    }

    public String getDt_ult_alt() {
        return dt_ult_alt;
    }

    public void setDt_ult_alt(String dt_ult_alt) {
        this.dt_ult_alt = dt_ult_alt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
