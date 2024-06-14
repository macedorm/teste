package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class PrecoVenda implements Serializable {

    public static final int BLOQ_PRODUTO_DISPONIVEL = 0;

    private double preco;
    private double descontoMaximo;
    private int bloq;

    public PrecoVenda() { }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getDescontoMaximo() {
        return descontoMaximo;
    }

    public void setDescontoMaximo(double descontoMaximo) {
        this.descontoMaximo = descontoMaximo;
    }

    public int getBloq() {
        return bloq;
    }

    public void setBloq(int bloq) {
        this.bloq = bloq;
    }
}
