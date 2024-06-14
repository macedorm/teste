package br.com.jjconsulting.mobile.dansales.model;


import java.io.Serializable;
import java.util.List;

public class RelatorioNotas implements Serializable {

    private String nome;
    private String codigoCliente;
    private String data;
    private String sap;
    private String numero;
    private double valor;
    private String cnpj;
    private String serie;
    private String numeroPedido;
    private String condicaoPagamento;
    private String codigoTipoVenda;
    private double icmsTotal;
    private List<RelatorioNotasItem> sku;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSap() {
        return sap;
    }

    public void setSap(String sap) {
        this.sap = sap;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getCondicaoPagamento() {
        return condicaoPagamento;
    }

    public void setCondicaoPagamento(String condicaoPagamento) {
        this.condicaoPagamento = condicaoPagamento;
    }

    public String getCodigoTipoVenda() {
        return codigoTipoVenda;
    }

    public void setCodigoTipoVenda(String codigoTipoVenda) {
        this.codigoTipoVenda = codigoTipoVenda;
    }

    public double getIcmsTotal() {
        return icmsTotal;
    }

    public void setIcmsTotal(double icmsTotal) {
        this.icmsTotal = icmsTotal;
    }

    public List<RelatorioNotasItem> getSku() {
        return sku;
    }

    public void setSku(List<RelatorioNotasItem> sku) {
        this.sku = sku;
    }
}
