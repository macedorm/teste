package br.com.jjconsulting.mobile.dansales.model;


import java.io.Serializable;

public class PesquisaPerguntaOpcoes implements Serializable {

    private int numOpcao;
    private String valor;
    private String desc;

    public int getNumOpcao() {
        return numOpcao;
    }

    public void setNumOpcao(int numOpcao) {
        this.numOpcao = numOpcao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
