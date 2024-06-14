package br.com.jjconsulting.mobile.dansales.model;

import java.util.Date;

public class RelatorioCarteiraPedido {
    private String codigo;
    private int codigoOrigem;
    private String nomeCliente;
    private String codigoCliente;
    private String codigoTipoVenda;
    private String codigoSap;
    private String valorTotal;
    private Date dataCadastro;
    private RelatorioCarteiraPedidoStatus status;
    private String peso;
    private String regional;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCodigoOrigem() {
        return codigoOrigem;
    }

    public void setCodigoOrigem(int codigoOrigem) {
        this.codigoOrigem = codigoOrigem;
    }

    public String getCliente() {
        return nomeCliente;
    }

    public void setCliente(String cliente) {
        this.nomeCliente = cliente;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getCodigoTipoVenda() {
        return codigoTipoVenda;
    }

    public void setCodigoTipoVenda(String codigoTipoVenda) {
        this.codigoTipoVenda = codigoTipoVenda;
    }

    public String getCodigoSap() {
        return codigoSap;
    }

    public void setCodigoSap(String codigoSap) {
        this.codigoSap = codigoSap;
    }

    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public RelatorioCarteiraPedidoStatus getStatus() {
        return status;
    }

    public void setStatus(RelatorioCarteiraPedidoStatus status) {
        this.status = status;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getValorTotal() {
        return valorTotal;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }
}
