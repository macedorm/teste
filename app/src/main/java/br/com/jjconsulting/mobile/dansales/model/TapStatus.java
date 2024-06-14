package br.com.jjconsulting.mobile.dansales.model;

import java.io.Serializable;

public class TapStatus implements Serializable {

    /** Em digitação. */ public static final int EM_DIGITACAO = 1;
    /** Aguardando Aprovador. */ public static final int AGUARDANDO_APROVADOR = 2;
    /** Aprovacao TapCliente. */ public static final int APROVACAO_CLIENTE = 3;
    /** Aprovado. */ public static final int APROVADO = 4;
    /** Reprovado. */ public static final int REPROVADO = 5;
    /** Aguardando Comprovacao. */ public static final int AGUARDANDO_COMPROVACAO = 6;
    /** Validacao Comprovacao. */ public static final int VALIDACAO_COMPROVACAO = 7;
    /** Aguardando Programacao Pgto. */ public static final int AGUARDANDO_PROGRAMACAO_PGTO = 8;
    /** Pago ou Pgto Programado. */ public static final int PAGO_PGTO_PROGRAMADO = 9;
    /** Cancelado. */ public static final int CANCELADO = 10;
    /** Bloqueado Financas  */ public static final int BLOQUEADO_FINANCAS = 11;
    /** Reprovado TapCliente  */ public static final int REPROVADO_CLIENTE = 12;

    private int codigo;
    private String nome;

    public TapStatus() {

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

        TapStatus other = (TapStatus)obj;

        return codigo == other.codigo;
    }
}
