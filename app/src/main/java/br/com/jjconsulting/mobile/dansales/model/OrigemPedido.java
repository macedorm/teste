package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class OrigemPedido implements Serializable {

    public static final int WEB = 0;
    public static final int MOBILE = 1;
    public static final int CAC = 2;
    public static final int EDI = 3;
    public static final int TEVEC = 4;
    public static final int SUPORTE = 5;

    private int codigo;
    private String nome;

    public OrigemPedido() {

    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
