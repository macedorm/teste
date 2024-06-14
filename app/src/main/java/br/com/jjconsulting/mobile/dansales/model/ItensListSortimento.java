package br.com.jjconsulting.mobile.dansales.model;

public class ItensListSortimento {

    private String SKU;
    private String SUBSTITUTE;
    private String name;
    private String url;
    private String peso;
    private String variante;
    private String descVariante;

    private int status;
    private int marca;
    private int cod;

    private String precoUser;

    private float precoEDV;
    private float precoPROMO;

    private boolean selected;

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getSUBSTITUTE() {
        return SUBSTITUTE;
    }

    public void setSUBSTITUTE(String SUBSTITUTE) {
        this.SUBSTITUTE = SUBSTITUTE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPeso() {
        return peso;
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMarca() {
        return marca;
    }

    public void setMarca(int marca) {
        this.marca = marca;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public String getPrecoUser() {
        return precoUser;
    }

    public void setPrecoUser(String precoUser) {
        this.precoUser = precoUser;
    }

    public float getPrecoEDV() {
        return precoEDV;
    }

    public void setPrecoEDV(float precoEDV) {
        this.precoEDV = precoEDV;
    }

    public float getPrecoPROMO() {
        return precoPROMO;
    }

    public void setPrecoPROMO(float precoPROMO) {
        this.precoPROMO = precoPROMO;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getVariante() {
        return variante;
    }

    public void setVariante(String variante) {
        this.variante = variante;
    }

    public String getDescVariante() {
        return descVariante;
    }

    public void setDescVariante(String descVariante) {
        this.descVariante = descVariante;
    }
}
