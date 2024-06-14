package br.com.jjconsulting.mobile.dansales.model;


import java.io.Serializable;
import java.util.ArrayList;

public class PesquisaPergunta implements Serializable {

    private int idPequisa;
    private int id;
    private int numPergunta;
    private int ordem;
    private int tamMax;
    private int idDependeciaResposta;
    private int casasDecimais;

    private double valorMin;
    private double valorMax;

    private String titlePergunta;
    private String subTitlePergunta;
    private String descTitlePergunta;
    private String image;

    private String descPergunta;
    private String nameImage;

    private boolean isObrigatoria;
    private boolean isDisable;
    private boolean isOnlyCamera;

    private PesquisaPerguntaType tipo;
    private ArrayList<CondicaoPerguntaPesquisa> condicaoPerguntaPesquisa;
    private ArrayList<PesquisaPerguntaOpcoes> pesquisaPerguntaOpcoes;
    private ArrayList<PesquisaResposta> respostaPerguntaPesquisa;

    private ArrayList<PesquisaPergunta> pesquisaPerguntaParent;

    private ArrayList<ItensListSortimento> itensListSortimento;

    public int getCasasDecimais() {
        return casasDecimais;
    }

    public void setCasasDecimais(int casasDecimais) {
        this.casasDecimais = casasDecimais;
    }

    public int getIdPequisa() {
        return idPequisa;
    }

    public void setIdPequisa(int idPequisa) {
        this.idPequisa = idPequisa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumPergunta() {
        return numPergunta;
    }

    public void setNumPergunta(int numPergunta) {
        this.numPergunta = numPergunta;
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public int getTamMax() {
        return tamMax;
    }

    public void setTamMax(int tamMax) {
        this.tamMax = tamMax;
    }

    public double getValorMin() {
        return valorMin;
    }

    public int getIdDependeciaResposta() {
        return idDependeciaResposta;
    }

    public void setIdDependeciaResposta(int idDependeciaResposta) {
        this.idDependeciaResposta = idDependeciaResposta;
    }

    public void setValorMin(double valorMin) {
        this.valorMin = valorMin;
    }

    public double getValorMax() {
        return valorMax;
    }

    public void setValorMax(double valorMax) {
        this.valorMax = valorMax;
    }

    public String getDescPergunta() {
        return descPergunta;
    }

    public void setDescPergunta(String descPergunta) {
        this.descPergunta = descPergunta;
    }

    public String getNameImage() {
        return nameImage;
    }

    public void setNameImage(String nameImage) {
        this.nameImage = nameImage;
    }

    public boolean isObrigatoria() {
        return isObrigatoria;
    }

    public void setObrigatoria(boolean obrigatoria) {
        isObrigatoria = obrigatoria;
    }

    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
    }

    public boolean isOnlyCamera() {
        return isOnlyCamera;
    }

    public void setOnlyCamera(boolean onlyCamera) {
        isOnlyCamera = onlyCamera;
    }

    public PesquisaPerguntaType getTipo() {
        return tipo;
    }

    public void setTipo(PesquisaPerguntaType tipo) {
        this.tipo = tipo;
    }

    public ArrayList<CondicaoPerguntaPesquisa> getCondicaoPerguntaPesquisa() {
        return condicaoPerguntaPesquisa;
    }

    public void setCondicaoPerguntaPesquisa(ArrayList<CondicaoPerguntaPesquisa> condicaoPerguntaPesquisa) {
        this.condicaoPerguntaPesquisa = condicaoPerguntaPesquisa;
    }

    public ArrayList<PesquisaPerguntaOpcoes> getPesquisaPerguntaOpcoes() {
        return pesquisaPerguntaOpcoes;
    }

    public void setPesquisaPerguntaOpcoes(ArrayList<PesquisaPerguntaOpcoes> pesquisaPerguntaOpcoes) {
        this.pesquisaPerguntaOpcoes = pesquisaPerguntaOpcoes;
    }

    public ArrayList<PesquisaResposta> getRespostaPesquisa() {
        return respostaPerguntaPesquisa;
    }

    public void setRespostaPesquisa(ArrayList<PesquisaResposta> respostaPerguntaPesquisa) {
        this.respostaPerguntaPesquisa = respostaPerguntaPesquisa;
    }

    public ArrayList<PesquisaPergunta> getPesquisaPerguntaParent() {
        return pesquisaPerguntaParent;
    }

    public void setPesquisaPerguntaParent(ArrayList<PesquisaPergunta> pesquisaPerguntaParent) {
        this.pesquisaPerguntaParent = pesquisaPerguntaParent;
    }

    public String getTitlePergunta() {
        return titlePergunta;
    }

    public void setTitlePergunta(String titlePergunta) {
        this.titlePergunta = titlePergunta;
    }

    public String getSubTitlePergunta() {
        return subTitlePergunta;
    }

    public void setSubTitlePergunta(String subTitlePergunta) {
        this.subTitlePergunta = subTitlePergunta;
    }

    public String getDescTitlePergunta() {
        return descTitlePergunta;
    }

    public void setDescTitlePergunta(String descTitlePergunta) {
        this.descTitlePergunta = descTitlePergunta;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<ItensListSortimento> getItensListSortimento() {
        return itensListSortimento;
    }

    public void setItensListSortimento(ArrayList<ItensListSortimento> itensListSortimento) {
        this.itensListSortimento = itensListSortimento;
    }
}


