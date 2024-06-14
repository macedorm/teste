package br.com.jjconsulting.mobile.dansales.model;


import java.io.Serializable;

public class CondicaoPerguntaPesquisa implements Serializable {

    private int id;
    private int numPergunta;
    private int operador;
    private int condicaoNumPergunta;

    private String condicaoValor1;
    private String condicaoValor2;

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

    public PesquisaConditionType getOperador() {
        return PesquisaConditionType.getEnumValue(operador);
    }

    public void setOperador(int operador) {
        this.operador = operador;
    }

    public int getCondicaoNumPergunta() {
        return condicaoNumPergunta;
    }

    public void setCondicaoNumPergunta(int condicaoNumPergunta) {
        this.condicaoNumPergunta = condicaoNumPergunta;
    }

    public String getCondicaoValor1() {
        return condicaoValor1;
    }

    public void setCondicaoValor1(String condicaoValor1) {
        this.condicaoValor1 = condicaoValor1;
    }

    public String getCondicaoValor2() {
        return condicaoValor2;
    }

    public void setCondicaoValor2(String condicaoValor2) {
        this.condicaoValor2 = condicaoValor2;
    }
}
