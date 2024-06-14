package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class Bandeira implements Serializable {

    private String codigoCliente;
    private String codigoBandeira;
    private String nomeBandeira;

    public Bandeira() {

    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNomeBandeira() {
        return nomeBandeira;
    }

    public void setNomeBandeira(String nomeBandeira) {
        this.nomeBandeira = nomeBandeira;
    }

    public String getCodigoBandeira() {
        return codigoBandeira;
    }

    public void setCodigoBandeira(String codigoBandeira) {
        this.codigoBandeira = codigoBandeira;
    }

    @Override
    public int hashCode() {
        int prime = 53;
        int hash = 3;
        return prime * hash + (codigoBandeira != null ? codigoBandeira.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!obj.getClass().equals(getClass()))
            return false;

        Bandeira other = (Bandeira)obj;

        if (codigoBandeira == null ? other.codigoBandeira != null
                : !codigoBandeira.equals(codigoBandeira)) {
            return false;
        }

        return codigoBandeira.equals(other.codigoBandeira);
    }
}
