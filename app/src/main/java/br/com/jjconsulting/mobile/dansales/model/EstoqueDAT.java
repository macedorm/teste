package br.com.jjconsulting.mobile.dansales.model;

public class EstoqueDAT {

    private int quantidadeDisponivel;
    private int quantidadeBatch;
    private String bloq;

    public EstoqueDAT() {

    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public int getQuantidadeBatch() {
        return quantidadeBatch;
    }

    public void setQuantidadeBatch(int quantidadeBatch) {
        this.quantidadeBatch = quantidadeBatch;
    }

    public String getBloq() {
        return bloq;
    }

    public void setBloq(String bloq) {
        this.bloq = bloq;
    }
}
