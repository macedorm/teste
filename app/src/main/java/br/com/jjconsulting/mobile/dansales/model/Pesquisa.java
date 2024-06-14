package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class Pesquisa implements Serializable {

    // tipo da pesquisa
    public static final int PESQUISA = 1;
    public static final int ATIVIDADE = 2;
    public static final int CHECKLIST = 3;
    public static final int CHECKLIST_RG = 4;
    public static final int COACHING = 5;

    // status da pesquisa
    public static final int EM_ABERTO = 1;
    public static final int PUBLICADO = 2;
    public static final int INATIVO = 3;

    // status da resposta
    public static final int OBRIGATORIAS_NAO_RESPONDIDAS = 1;
    public static final int PARCIL_RESPONDIDAS = 2;
    public static final int OBRIGATORIAS_RESPONDIDAS = 3;

    private String nome;
    private String codigoUnidadeNegocio;
    private Layout layout;

    private int codigo;
    private int status;
    private int statusResposta;
    private int edit;
    private int qtdCli;
    private int freq;
    private int tipo;

    private Date DataInicio;
    private Date DataFim;
    private Date currentDate;

    private boolean selecionaCliente;
    private boolean isResposta;
    private boolean atividadeObrigatoria;
    private boolean isVisualizaAtividade;



    private ArrayList<MultiValues> pesquisaJustificativa;

    public Pesquisa() { }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatusResposta() {
        return statusResposta;
    }

    public void setStatusResposta(int statusResposta) {
        this.statusResposta = statusResposta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoUnidadeNegocio() {
        return codigoUnidadeNegocio;
    }

    public void setCodigoUnidadeNegocio(String codigoUnidadeNegocio) {
        this.codigoUnidadeNegocio = codigoUnidadeNegocio;
    }

    public Date getDataInicio() {
        return DataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        DataInicio = dataInicio;
    }

    public Date getDataFim() {
        return DataFim;
    }

    public void setDataFim(Date dataFim) {
        DataFim = dataFim;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isSelecionaCliente() {
        return selecionaCliente;
    }

    public void setSelecionaCliente(boolean selecionaCliente) {
        this.selecionaCliente = selecionaCliente;
    }

    public boolean isResposta() {
        return isResposta;
    }

    public void setResposta(boolean resposta) {
        isResposta = resposta;
    }

    public TPesquisaEdit getEdit() {
        return TPesquisaEdit.fromInteger(edit);
    }

    public void setEdit(int edit) {
        this.edit = edit;
    }

    public int getQtdCli() {
        return qtdCli;
    }

    public void setQtdCli(int qtdCli) {
        this.qtdCli = qtdCli;
    }

    public TFreq getFreq() {
        return  TFreq.fromInteger(freq);
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isAtividadeObrigatoria() {
        return atividadeObrigatoria;
    }


    public void setAtividadeObrigatoria(boolean atividadeObrigatoria) {
        this.atividadeObrigatoria = atividadeObrigatoria;
    }

    public boolean isVisualizaAtividade() {
        return isVisualizaAtividade;
    }

    public void setVisualizaAtividade(boolean visualizaAtividade) {
        isVisualizaAtividade = visualizaAtividade;
    }

    public ArrayList<MultiValues> getPesquisaJustificativa() {
        return pesquisaJustificativa;
    }

    public void setPesquisaJustificativa(ArrayList<MultiValues> pesquisaJustificativa) {
        this.pesquisaJustificativa = pesquisaJustificativa;
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }
}
