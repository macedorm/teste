package br.com.jjconsulting.mobile.dansales.model;

import java.util.Date;

public class QuantidadeSugerida {

    private int quantidade;
    private String sku;
    private boolean isItemNovo;


    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean isItemNovo() {
        return isItemNovo;
    }

    public void setItemNovo(boolean itemNovo) {
        isItemNovo = itemNovo;
    }
}
