package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

/**
 * Known as clsOrc, orc, oOrc, orcamento and etc.
 */
public class Pedido implements Serializable {

    public static final int TIPO_AGENDA_NAO_POSSUI = 0;
    public static final int TIPO_AGENDA_AGENDA = 1;
    public static final int TIPO_AGENDA_AGENDA_DISTRIBUIDOR = 2;

    public static final String UNIDADE_MEDIDA_UNIDADE = "PAC";
    public static final String UNIDADE_MEDIDA_CAIXA = "CPA";

    private String codigo;
    private String codigoEmpresa;
    private String codigoFilial;
    private String codigoVendedor;
    private String codigoSap;
    private String ordemCompra;
    private String codigoPedidoVenda;
    private String codigoGrupo;
    private String codigoCondPag;
    private String codigoPlanta;
    private double investimento;
    private Date dataCadastro;
    private Date dataImportacaoSAP; // ORC_DAT_IMPORTACAO_SAP
    private String observacao;
    private String observacaoNotaFiscal;
    private String unidadeMedida;
    private int tipoAgenda;
    private String codigoAgenda;
    private Date entregaEm;
    private Date dataEnvio;
    private String empenho;
    private String urgente;
    private String lojaEntrega;
    private Hashtable justificativas;

    private String codigoTipoVenda;
    private TipoVenda tipoVenda;

    private String codigoCliente;
    private Cliente cliente;

    private int codigoStatus;
    private StatusPedido status;

    private int codigoOrigem;
    private OrigemPedido origem;

    private int isPositivacao;

    /**
     * Código do sortimento (layout; planogram code).
     */
    private String codigoSortimento;

    /**
     * Valor total já calculado.<br>
     * Obs.: usar essa propriedade para casos em sque os itens estão indisponíveis.
     */
    private double valorTotal;


    public Pedido() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public void setCodigoEmpresa(String codigoEmpresa) {
        this.codigoEmpresa = codigoEmpresa;
    }

    public String getCodigoFilial() {
        return codigoFilial;
    }

    public void setCodigoFilial(String codigoFilial) {
        this.codigoFilial = codigoFilial;
    }

    public String getCodigoVendedor() {
        return codigoVendedor;
    }

    public void setCodigoVendedor(String codigoVendedor) {
        this.codigoVendedor = codigoVendedor;
    }

    public String getCodigoSap() {
        return codigoSap;
    }

    public void setCodigoSap(String codigoSap) {
        this.codigoSap = codigoSap;
    }

    public String getOrdemCompra() {
        return ordemCompra;
    }

    public void setOrdemCompra(String ordemCompra) {
        this.ordemCompra = ordemCompra;
    }

    public String getCodigoPedidoVenda() {
        return codigoPedidoVenda;
    }

    public void setCodigoPedidoVenda(String codigoPedidoVenda) {
        this.codigoPedidoVenda = codigoPedidoVenda;
    }

    public double getInvestimento() {
        return investimento;
    }

    public void setInvestimento(double investimento) {
        this.investimento = investimento;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getDataImportacaoSAP() {
        return dataImportacaoSAP;
    }

    public void setDataImportacaoSAP(Date dataImportacaoSAP) {
        this.dataImportacaoSAP = dataImportacaoSAP;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getObservacaoNotaFiscal() {
        return observacaoNotaFiscal;
    }

    public void setObservacaoNotaFiscal(String observacaoNotaFiscal) {
        this.observacaoNotaFiscal = observacaoNotaFiscal;
    }

    public String getCodigoGrupo() {
        return codigoGrupo;
    }

    public void setCodigoGrupo(String codigoGrupo) {
        this.codigoGrupo = codigoGrupo;
    }

    public String getCodigoCondPag() {
        return codigoCondPag;
    }

    public String getCodigoPlanta() {
        return codigoPlanta;
    }

    public void setCodigoPlanta(String codigoPlanta) {
        this.codigoPlanta = codigoPlanta;
    }

    public void setCodigoCondPag(String codigoCondPag) {
        this.codigoCondPag = codigoCondPag;
    }

    public int getTipoAgenda() {
        return tipoAgenda;
    }

    public void setTipoAgenda(int tipoAgenda) {
        this.tipoAgenda = tipoAgenda;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public String getCodigoAgenda() {
        return codigoAgenda;
    }

    public void setCodigoAgenda(String codigoAgenda) {
        this.codigoAgenda = codigoAgenda;
    }

    public String getCodigoTipoVenda() {
        return codigoTipoVenda;
    }

    public void setCodigoTipoVenda(String codigoTipoVenda) {
        this.codigoTipoVenda = codigoTipoVenda;
    }

    public TipoVenda getTipoVenda() {
        return tipoVenda;
    }

    public void setTipoVenda(TipoVenda tipoVenda) {
        this.tipoVenda = tipoVenda;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public int getCodigoStatus() {
        return codigoStatus;
    }

    public void setCodigoStatus(int codigoStatus) {
        this.codigoStatus = codigoStatus;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public int getCodigoOrigem() {
        return codigoOrigem;
    }

    public void setCodigoOrigem(int codigoOrigem) {
        this.codigoOrigem = codigoOrigem;
    }

    public OrigemPedido getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemPedido origem) {
        this.origem = origem;
    }

    public String getCodigoSortimento() {
        return codigoSortimento;
    }

    public void setCodigoSortimento(String codigoSortimento) {
        this.codigoSortimento = codigoSortimento;
    }

    public Date getEntregaEm() {
        return entregaEm;
    }

    public void setEntregaEm(Date entregaEm) {
        this.entregaEm = entregaEm;
    }

    public Date getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(Date dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getEmpenho() {
        return empenho;
    }

    public void setEmpenho(String empenho) {
        this.empenho = empenho;
    }

    public String getUrgente() {
        return urgente;
    }

    public void setUrgente(String urgente) {
        this.urgente = urgente;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCodigoUnidadeNegocio() {
        return getCodigoEmpresa() + getCodigoFilial();
    }

    public String getLojaEntrega() {
        return lojaEntrega;
    }

    public void setLojaEntrega(String lojaEntrega) {
        this.lojaEntrega = lojaEntrega;
    }

    public Hashtable getJustificativa() {
        return justificativas;
    }

    public void setJustificativa(Hashtable justificativa) {
        this.justificativas = justificativa;
    }

    public int getIsPositivacao() {
        return isPositivacao;
    }

    public void setIsPositivacao(int isPositivacao) {
        this.isPositivacao = isPositivacao;
    }
}
