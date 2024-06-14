package br.com.jjconsulting.mobile.dansales.model;

public class TapInsert {
    private String codUsuario;
    private String codEmpresa;
    private String codfilial;
    private String codCliente;
    private boolean isRacunho;

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String getCodfilial() {
        return codfilial;
    }

    public void setCodfilial(String codfilial) {
        this.codfilial = codfilial;
    }

    public String getCodCliente() {
        return codCliente;
    }

    public void setCodCliente(String codCliente) {
        this.codCliente = codCliente;
    }

    public boolean isRacunho() {
        return isRacunho;
    }

    public void setRacunho(boolean racunho) {
        isRacunho = racunho;
    }
}
