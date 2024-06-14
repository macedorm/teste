package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PesquisaAcesso implements Serializable {

    // tipo da Acesso
    public static final int FILTER_USER = 1;
    public static final int FILTER_FUNCAO = 2;
    public static final int FILTER_ORG = 3;
    public static final int FILTER_UF = 4;
    public static final int FILTER_REGIONAL = 5;
    public static final int FILTER_BANDEIRA = 6;
    public static final int FILTER_CIDADE = 7;
    public static final int FILTER_CLIENTE = 8;
    public static final int FILTER_CANAL = 9;

    private List<String> listUser;
    private List<String> listFuncao;
    private List<String> listOrg;
    private List<String> listUF;
    private List<String> listRegional;
    private List<String> listBandeira;
    private List<String> listCidade;
    private List<String> listCliente;
    private List<String> listCanal;

    private int pesquisaId;
    private int userId;

    public PesquisaAcesso() {

        listUser = new ArrayList<>();
        listFuncao = new ArrayList<>();
        listOrg = new ArrayList<>();
        listUF = new ArrayList<>();
        listRegional = new ArrayList<>();
        listBandeira = new ArrayList<>();
        listCidade = new ArrayList<>();
        listCliente = new ArrayList<>();
        listCanal = new ArrayList<>();

    }

    public boolean hasClienteFilter(){
        if (listOrg.size() > 0 ||
        listUF.size() > 0 ||
        listRegional.size() > 0 ||
        listBandeira.size() > 0 ||
        listCidade.size() > 0 ||
        listCliente.size() > 0 ||
        listCanal.size() > 0){
            return true;
        } else {
            return false;
        }
    }

    public void addFilter(int idFiltro, String value){
        switch (idFiltro){
            case FILTER_USER:
                listUser.add(value);
                break;
            case FILTER_FUNCAO:
                listFuncao.add(value);
                break;
            case FILTER_ORG:
                listOrg.add(value);
                break;
            case FILTER_UF:
                listUF.add(value);
                break;
            case FILTER_REGIONAL:
                listRegional.add(value);
                break;
            case FILTER_BANDEIRA:
                listBandeira.add(value);
                break;
            case FILTER_CIDADE:
                listCidade.add(value);
                break;
            case FILTER_CLIENTE:
                listCliente.add(value);
                break;
            case FILTER_CANAL:
                listCanal.add(value);
                break;
        }
    }

    public List<String> getListUser() {
        return listUser;
    }

    public void setListUser(List<String> listUser) {
        this.listUser = listUser;
    }

    public List<String> getListFuncao() {
        return listFuncao;
    }

    public void setListFuncao(List<String> listFuncao) {
        this.listFuncao = listFuncao;
    }

    public List<String> getListOrg() {
        return listOrg;
    }

    public void setListOrg(List<String> listOrg) {
        this.listOrg = listOrg;
    }

    public List<String> getListUF() {
        return listUF;
    }

    public void setListUF(List<String> listUF) {
        this.listUF = listUF;
    }

    public List<String> getListRegional() {
        return listRegional;
    }

    public void setListRegional(List<String> listRegional) {
        this.listRegional = listRegional;
    }

    public List<String> getListBandeira() {
        return listBandeira;
    }

    public void setListBandeira(List<String> listBandeira) {
        this.listBandeira = listBandeira;
    }

    public List<String> getListCidade() {
        return listCidade;
    }

    public void setListCidade(List<String> listCidade) {
        this.listCidade = listCidade;
    }

    public List<String> getListCliente() {
        return listCliente;
    }

    public void setListCliente(List<String> listCliente) {
        this.listCliente = listCliente;
    }

    public List<String> getListCanal() {
        return listCanal;
    }

    public void setListCanal(List<String> listCanal) {
        this.listCanal = listCanal;
    }

    public int getPesquisaId() {
        return pesquisaId;
    }

    public void setPesquisaId(int pesquisaId) {
        this.pesquisaId = pesquisaId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
