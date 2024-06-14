package br.com.jjconsulting.mobile.dansales.model;

import java.util.Date;

public class BatchDAT {

    private String lote;
    private Date data;
    private double percentualDesconto;
    private int quantidadeDisponivel;

    private PrecoVenda precoVenda;

    public BatchDAT() {

    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }

    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }

    public PrecoVenda getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(PrecoVenda precoVenda) {
        this.precoVenda = precoVenda;
    }

    public double getPrecoComDescontoAplicado() {
        if (precoVenda == null) {
            return 0;
        }
        return precoVenda.getPreco() - (precoVenda.getPreco() * (percentualDesconto / 100));
    }

    @Override
    public int hashCode() {
        int prime = 51;
        int hash = 3;
        return prime * hash + (lote != null ? lote.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!obj.getClass().equals(getClass()))
            return false;

        BatchDAT other = (BatchDAT) obj;

        if (lote == null ? other.lote != null
                : !lote.equals(lote)) {
            return false;
        }

        return lote.equals(other.lote);
    }
}
