package br.com.jjconsulting.mobile.dansales.model;


import java.io.Serializable;

public class Familia implements Serializable {

    private String codigo;
    private String nome;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
        return prime * hash + (codigo != null ? codigo.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!obj.getClass().equals(getClass()))
            return false;

        Familia other = (Familia)obj;

        if (codigo == null ? other.codigo != null
                : !codigo.equals(codigo)) {
            return false;
        }

        return codigo.equals(other.codigo);
    }
}
