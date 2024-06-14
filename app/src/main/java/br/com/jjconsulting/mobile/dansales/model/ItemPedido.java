package br.com.jjconsulting.mobile.dansales.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;

public class ItemPedido implements Serializable, Comparator {

    private int id;
    private String codigoPedido;
    private int quantidade;
    private String observacao;
    private String lote;
    private String itSol;
    private String codSol;

    // valores e descontos
    private double precoVenda;
    private double valorTotal;
    private double valorDesconto;
    private double percentualDesconto;
    private double descontoCliente;
    private double descontoFlagship;
    private double descontoGerencial;
    private double descontoPromocional;
    private double descontoCondicaoPagamento;
    private double descontoEdv;
    private double descontoTatico;

    // bb?
    private double bbPrecoTabela;
    private double bbPrecoLoc;
    private double bbFator;

    // produto
    private String codigoProduto;
    private String codigoEmpresaProduto;
    private String codigoFilialProduto;
    private double precoTabelaProduto;
    private double pesoProduto;
    private double pesoLiquidoProduto;
    private int statusProduto;
    private Produto produto;
    private int qtdSug;
    private int qtdPedAnt;

    private int codSubstituto;

    private MultiValues multiValues;


    public ItemPedido() {
        qtdPedAnt = -1;
    }

    protected ItemPedido(Parcel in) {
        id = in.readInt();
        codigoPedido = in.readString();
        quantidade = in.readInt();
        observacao = in.readString();
        lote = in.readString();
        itSol = in.readString();
        codSol = in.readString();
        precoVenda = in.readDouble();
        valorTotal = in.readDouble();
        valorDesconto = in.readDouble();
        percentualDesconto = in.readDouble();
        descontoCliente = in.readDouble();
        descontoFlagship = in.readDouble();
        descontoGerencial = in.readDouble();
        descontoPromocional = in.readDouble();
        descontoCondicaoPagamento = in.readDouble();
        descontoEdv = in.readDouble();
        descontoTatico = in.readDouble();
        bbPrecoTabela = in.readDouble();
        bbPrecoLoc = in.readDouble();
        bbFator = in.readDouble();
        codigoProduto = in.readString();
        codigoEmpresaProduto = in.readString();
        codigoFilialProduto = in.readString();
        precoTabelaProduto = in.readDouble();
        pesoProduto = in.readDouble();
        pesoLiquidoProduto = in.readDouble();
        statusProduto = in.readInt();
        qtdSug = in.readInt();
        codSubstituto = in.readInt();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }

    public void setCodigoPedido(String codigoPedido) {
        this.codigoPedido = codigoPedido;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getItSol() {
        return itSol;
    }

    public void setItSol(String itSol) {
        this.itSol = itSol;
    }

    public String getCodSol() {
        return codSol;
    }

    public void setCodSol(String codSol) {
        this.codSol = codSol;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(double valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public double getDescontoCliente() {
        return descontoCliente;
    }

    public void setDescontoCliente(double descontoCliente) {
        this.descontoCliente = descontoCliente;
    }

    public double getDescontoFlagship() {
        return descontoFlagship;
    }

    public void setDescontoFlagship(double descontoFlagship) {
        this.descontoFlagship = descontoFlagship;
    }

    public double getDescontoGerencial() {
        return descontoGerencial;
    }

    public void setDescontoGerencial(double descontoGerencial) {
        this.descontoGerencial = descontoGerencial;
    }

    public double getDescontoPromocional() {
        return descontoPromocional;
    }

    public void setDescontoPromocional(double descontoPromocional) {
        this.descontoPromocional = descontoPromocional;
    }

    public double getDescontoCondicaoPagamento() {
        return descontoCondicaoPagamento;
    }

    public void setDescontoCondicaoPagamento(double descontoCondicaoPagamento) {
        this.descontoCondicaoPagamento = descontoCondicaoPagamento;
    }

    public double getDescontoEdv() {
        return descontoEdv;
    }

    public void setDescontoEdv(double descontoEdv) {
        this.descontoEdv = descontoEdv;
    }

    public double getDescontoTatico() {
        return descontoTatico;
    }

    public void setDescontoTatico(double descontoTatico) {
        this.descontoTatico = descontoTatico;
    }

    public double getBbPrecoTabela() {
        return bbPrecoTabela;
    }

    public void setBbPrecoTabela(double bbPrecoTabela) {
        this.bbPrecoTabela = bbPrecoTabela;
    }

    public double getBbPrecoLoc() {
        return bbPrecoLoc;
    }

    public void setBbPrecoLoc(double bbPrecoLoc) {
        this.bbPrecoLoc = bbPrecoLoc;
    }

    public double getBbFator() {
        return bbFator;
    }

    public void setBbFator(double bbFator) {
        this.bbFator = bbFator;
    }


    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getCodigoEmpresaProduto() {
        return codigoEmpresaProduto;
    }

    public void setCodigoEmpresaProduto(String codigoEmpresaProduto) {
        this.codigoEmpresaProduto = codigoEmpresaProduto;
    }

    public String getCodigoFilialProduto() {
        return codigoFilialProduto;
    }

    public void setCodigoFilialProduto(String codigoFilialProduto) {
        this.codigoFilialProduto = codigoFilialProduto;
    }

    public double getPrecoTabelaProduto() {
        return precoTabelaProduto;
    }

    public void setPrecoTabelaProduto(double precoTabelaProduto) {
        this.precoTabelaProduto = precoTabelaProduto;
    }

    public double getPesoProduto() {
        return pesoProduto;
    }

    public void setPesoProduto(double pesoProduto) {
        this.pesoProduto = pesoProduto;
    }

    public double getPesoLiquidoProduto() {
        return pesoLiquidoProduto;
    }

    public void setPesoLiquidoProduto(double pesoLiquidoProduto) {
        this.pesoLiquidoProduto = pesoLiquidoProduto;
    }

    public int getStatusProduto() {
        return statusProduto;
    }

    public void setStatusProduto(int statusProduto) {
        this.statusProduto = statusProduto;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQtdSug() {
        return qtdSug;
    }

    public void setQtdSug(int qtdSug) {
        this.qtdSug = qtdSug;
    }

    public int getCodSubstituto() {
        return codSubstituto;
    }

    public void setCodSubstituto(int codSubstituto) {
        this.codSubstituto = codSubstituto;
    }

    public int getQtdPedAnt() {
        return qtdPedAnt;
    }

    public void setQtdPedAnt(int qtdPedAnt) {
        this.qtdPedAnt = qtdPedAnt;
    }

    public MultiValues getMultiValues() {
        return multiValues;
    }

    public void setMultiValues(MultiValues multiValues) {
        this.multiValues = multiValues;
    }

    public int compare(Object o1, Object o2)
    {
        ItemPedido itemPedido1 = (ItemPedido)o1;
        ItemPedido itemPedido2 = (ItemPedido)o2;
        return itemPedido1.getProduto().getNome().compareToIgnoreCase(itemPedido2.getProduto().getNome());
    }


    public static Comparator<ItemPedido> statusOrder = new Comparator<ItemPedido>() {

        public int compare(ItemPedido s1, ItemPedido s2) {

            int status1 = (s1.getProduto().getTipoSortimento() == null ? -1 : s1.getProduto().getTipoSortimento());

            int status2 = (s2.getProduto().getTipoSortimento() == null ? -1 : s2.getProduto().getTipoSortimento());

            return status1 - status2;
        }};
}
