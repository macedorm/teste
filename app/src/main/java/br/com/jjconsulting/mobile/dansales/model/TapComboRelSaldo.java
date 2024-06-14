package br.com.jjconsulting.mobile.dansales.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class TapComboRelSaldo implements Serializable {

    private String cod;
    private String nome;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    @Override
    public int hashCode() {
        int prime = 51;
        int hash = 3;
        return prime * hash + (cod != null ? cod.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!obj.getClass().equals(getClass()))
            return false;

        TapComboRelSaldo other = (TapComboRelSaldo)obj;

        if (cod == null ? other.cod != null
                : !cod.equals(cod)) {
            return false;
        }

        return cod.equals(other.cod);
    }
}