package br.com.jjconsulting.mobile.dansales.model;

import java.util.Date;

public class PedidoBonificado {
    private String codigo;
    private Date entregueEm;
    private String codigoCondPag;
    private String unidadeMedida;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getEntregueEm() {
        return entregueEm;
    }

    public void setEntregueEm(Date entregueEm) {
        this.entregueEm = entregueEm;
    }

    public String getCodigoCondPag() {
        return codigoCondPag;
    }

    public void setCodigoCondPag(String codigoCondPag) {
        this.codigoCondPag = codigoCondPag;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }
}
