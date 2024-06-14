package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class TapRelSaldo implements Serializable {

    private String tapCod;

    private String masterContrato;
    private String codMaster;
    private String codTap;
    private String data;
    private String codCliente;
    private String nomeCliente;
    private String nomeStatus;
    private double valorTap;
    private double valorMaster;
    private double saldo;

    public String getTapCod() {
        return tapCod;
    }

    public void setTapCod(String tapCod) {
        this.tapCod = tapCod;
    }

    public String getMasterContrato() {
        return masterContrato;
    }

    public void setMasterContrato(String masterContrato) {
        this.masterContrato = masterContrato;
    }

    public String getCodMaster() {
        return codMaster;
    }

    public void setCodMaster(String codMaster) {
        this.codMaster = codMaster;
    }

    public String getCodTap() {
        return codTap;
    }

    public void setCodTap(String codTap) {
        this.codTap = codTap;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getNomeStatus() {
        return nomeStatus;
    }

    public void setNomeStatus(String nomeStatus) {
        this.nomeStatus = nomeStatus;
    }

    public double getValorTap() {
        return valorTap;
    }

    public void setValorTap(double valorTap) {
        this.valorTap = valorTap;
    }

    public double getValorMaster() {
        return valorMaster;
    }

    public void setValorMaster(double valorMaster) {
        this.valorMaster = valorMaster;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}