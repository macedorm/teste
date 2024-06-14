package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

public class LayoutUserSync {

    private int lay_int_pesquisa_id;
    private int cod_reg_func;
    private String lay_txt_planogram_code;
    private String lay_txt_dtfreq;
    private String del_flag;
    private String dt_ult_alt;
    private String lay_txt_cliente;
    private String lay_int_cod_reg_func;

    public int getLay_int_pesquisa_id() {
        return lay_int_pesquisa_id;
    }

    public void setLay_int_pesquisa_id(int lay_int_pesquisa_id) {
        this.lay_int_pesquisa_id = lay_int_pesquisa_id;
    }

    public int getCod_reg_func() {
        return cod_reg_func;
    }

    public void setCod_reg_func(int cod_reg_func) {
        this.cod_reg_func = cod_reg_func;
    }

    public String getLay_txt_planogram_code() {
        return lay_txt_planogram_code;
    }

    public void setLay_txt_planogram_code(String lay_txt_planogram_code) {
        this.lay_txt_planogram_code = lay_txt_planogram_code;
    }

    public String getLay_txt_dtfreq() {
        return lay_txt_dtfreq;
    }

    public void setLay_txt_dtfreq(String lay_txt_dtfreq) {
        this.lay_txt_dtfreq = lay_txt_dtfreq;
    }

    public String getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(String del_flag) {
        this.del_flag = del_flag;
    }

    public String getDt_ult_alt() {
        return dt_ult_alt;
    }

    public void setDt_ult_alt(String dt_ult_alt) {
        this.dt_ult_alt = dt_ult_alt;
    }

    public String getLay_txt_cliente() {
        return lay_txt_cliente;
    }

    public void setLay_txt_cliente(String lay_txt_cliente) {
        this.lay_txt_cliente = lay_txt_cliente;
    }

    public String getLay_int_cod_reg_func() {
        return lay_int_cod_reg_func;
    }

    public void setLay_int_cod_reg_func(String lay_int_cod_reg_func) {
        this.lay_int_cod_reg_func = lay_int_cod_reg_func;
    }
}
