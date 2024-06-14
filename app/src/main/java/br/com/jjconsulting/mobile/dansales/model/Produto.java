package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.util.Date;

public class Produto implements Serializable {

    public static final int SORTIMENTO_TIPO_OBRIGATORIO = 1;
    public static final int SORTIMENTO_TIPO_INOVACAO = 2;
    public static final int SORTIMENTO_TIPO_RECOMENDADO = 3;
    public static final int SORTIMENTO_TIPO_SUBSTITUTO = 4;


    private String codigo;
    private String codigoSimplificado;
    private String nome;
    private String familia;
    private int multiplo;
    private double lastro;
    private double pallet;
    private double peso;
    private double pesoLiquido;
    private Date dataVencimento;
    private Integer tipoSortimento;
    private String descricaoSortimento;
    private String codDUN14;
    private String codEAN13;

    private PrecoVenda precoVenda;
    private EstoqueDAT estoqueDAT;

    public Produto() {

    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoSimplificado() {
        return codigoSimplificado;
    }

    public void setCodigoSimplificado(String codigoSimplificado) {
        this.codigoSimplificado = codigoSimplificado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public int getMultiplo() {
        return multiplo;
    }

    public void setMultiplo(int multiplo) {
        this.multiplo = multiplo;
    }

    public double getLastro() {
        return lastro;
    }

    public void setLastro(double lastro) {
        this.lastro = lastro;
    }

    public double getPallet() {
        return pallet;
    }

    public void setPallet(double pallet) {
        this.pallet = pallet;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public double getPesoLiquido() {
        return pesoLiquido;
    }

    public void setPesoLiquido(double pesoLiquido) {
        this.pesoLiquido = pesoLiquido;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Integer getTipoSortimento() {
        return tipoSortimento;
    }

    public void setTipoSortimento(Integer tipoSortimento) {
        this.tipoSortimento = tipoSortimento;
    }

    public String getDescricaoSortimento() {
        return descricaoSortimento;
    }

    public void setDescricaoSortimento(String descricaoSortimento) {
        this.descricaoSortimento = descricaoSortimento;
    }

    public PrecoVenda getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(PrecoVenda precoVenda) {
        this.precoVenda = precoVenda;
    }

    public EstoqueDAT getEstoqueDAT() {
        return estoqueDAT;
    }

    public void setEstoqueDAT(EstoqueDAT estoqueDAT) {
        this.estoqueDAT = estoqueDAT;
    }

    public boolean isPrecoAvailable() {
        return precoVenda != null;
    }

    public boolean isEstoqueDATAvailable() {
        return estoqueDAT != null;
    }

    public String getCodDUN14() {
        return codDUN14;
    }

    public void setCodDUN14(String codDUN14) {
        this.codDUN14 = codDUN14;
    }

    public String getCodEAN13() {
        return codEAN13;
    }

    public void setCodEAN13(String codEAN13) {
        this.codEAN13 = codEAN13;
    }
}
