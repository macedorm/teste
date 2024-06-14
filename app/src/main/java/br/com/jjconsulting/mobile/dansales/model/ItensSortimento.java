package br.com.jjconsulting.mobile.dansales.model;

import java.util.Date;

public class ItensSortimento {

    private String SKU;
    private String SUBSTITUTE;

    private float precoPromo;
    private float precoEDV;

    private String name;
    private boolean selected;

    public ItensSortimento() {

    }

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public float getPrecoPromo() {
        return precoPromo;
    }

    public void setPrecoPromo(float precoPromo) {
        this.precoPromo = precoPromo;
    }

    public float getPrecoEDV() {
        return precoEDV;
    }

    public void setPrecoEDV(float precoEDV) {
        this.precoEDV = precoEDV;
    }
}
