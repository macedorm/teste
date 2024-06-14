package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class StatusPedido implements Serializable {

    /** INDEFINIDO. */ public static final int SEM_REGRA = 0;
    /** Em elaboração. */ public static final int NAO_ENVIADO = 1;
    /** Aguardando liberação. */ public static final int ENVIADO_ADM = 2;
    /** Enviado ao SAP. */ public static final int PEDIDO_GERADO = 3;
    /** Pedido reprovado. */ public static final int REPROVADO = 4;
    /** Pedido cancelado. */ public static final int CANCELADO = 5;
    /** Pedido enviado. */ public static final int EXPORTADO = 6;
    /** Aguardando aprovação. */ public static final int ENVIADO_APROVACAO = 7;
    /** Enviado CSP. */ public static final int ENVIADO_CSP = 8;
    /** Aprovado CSP  */ public static final int APROVADO_CSP = 9;

    private int codigo;
    private String nome;
    private String color;

    public StatusPedido() {

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public int hashCode() {
        int prime = 53;
        int hash = 3;
        return prime * hash + String.valueOf(codigo).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!obj.getClass().equals(getClass()))
            return false;

        StatusPedido other = (StatusPedido)obj;

        return codigo == other.codigo;
    }
}
