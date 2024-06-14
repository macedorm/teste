package br.com.jjconsulting.mobile.dansales.model;

/**
 * Created by jjconsulting on 09/04/2018.
 */

public class UsuarioFuncao {

    private String codFuncao;
    private String flagPgv;
    private String nome;
    private double posicao;
    private String sigla;

    public String getCodFuncao() {
        return codFuncao;
    }

    public void setCodFuncao(String codFuncao) {
        this.codFuncao = codFuncao;
    }

    public String getFlagPgv() {
        return flagPgv;
    }

    public void setFlagPgv(String flagPgv) {
        this.flagPgv = flagPgv;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPosicao() {
        return posicao;
    }

    public void setPosicao(double posicao) {
        this.posicao = posicao;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
