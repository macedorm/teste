package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TipoVenda implements Serializable {

    public static final String VENDA = "VEN";
    public static final String UHT= "UHT";
    public static final String VPR = "VPR";
    public static final String DAT = "DAT";
    public static final String BON = "BON";
    public static final String VCO = "VCO";
    public static final String SUG = "SUG";
    public static final String REB = "REB";

    private String codigo;
    private String nome;
    private boolean edicaoPercentualHabilitada;
    private boolean edicaoObservacaoHabilitada;
    private boolean validacaoQtdMinimaHabilitada;
    private boolean permissaoUsoAnexoHabilitado;
    private BigDecimal valorMinimo;
    private BigDecimal valorMaximo;

    public TipoVenda() {

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isEdicaoPercentualHabilitada() {
        return edicaoPercentualHabilitada;
    }

    public void setEdicaoPercentualHabilitada(boolean edicaoPercentualHabilitada) {
        this.edicaoPercentualHabilitada = edicaoPercentualHabilitada;
    }

    public boolean isEdicaoObservacaoHabilitada() {
        return edicaoObservacaoHabilitada;
    }

    public void setEdicaoObservacaoHabilitada(boolean edicaoObservacaoHabilitada) {
        this.edicaoObservacaoHabilitada = edicaoObservacaoHabilitada;
    }

    public boolean isValidacaoQtdMinimaHabilitada() {
        return validacaoQtdMinimaHabilitada;
    }

    public void setValidacaoQtdMinimaHabilitada(boolean validacaoQtdMinimaHabilitada) {
        this.validacaoQtdMinimaHabilitada = validacaoQtdMinimaHabilitada;
    }

    public boolean isPermissaoUsoAnexoHabilitado() {
        return permissaoUsoAnexoHabilitado;
    }

    public void setPermissaoUsoAnexoHabilitado(boolean permissaoUsoAnexoHabilitado) {
        this.permissaoUsoAnexoHabilitado = permissaoUsoAnexoHabilitado;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }
}
