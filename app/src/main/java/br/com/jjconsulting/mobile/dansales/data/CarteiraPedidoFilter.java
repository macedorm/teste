package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class CarteiraPedidoFilter implements Serializable {

    private Organizacao organizacao;
    private Bandeira bandeira;
    private StatusPedido status;
    private List<Usuario> hierarquiaComercial;
    private Date dateStart;
    private Date dateEnd;


    public CarteiraPedidoFilter() {
        this.hierarquiaComercial = new ArrayList<>();
    }

    public CarteiraPedidoFilter(Organizacao organizacao, Bandeira bandeira, StatusPedido status,
                                List<Usuario> hierarquiaComercial, Date dateStart, Date dateEnd) {
        this.organizacao = organizacao;
        this.bandeira = bandeira;
        this.status = status;
        this.hierarquiaComercial = hierarquiaComercial;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public Organizacao getOrganizacao() {
        return organizacao;
    }

    public Bandeira getBandeira() {
        return bandeira;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public List<Usuario> getHierarquiaComercial() {
        return hierarquiaComercial;
    }

    public boolean isEmpty() {
        return organizacao == null && bandeira == null && status == null && dateStart == null && dateEnd == null
                && (hierarquiaComercial == null || hierarquiaComercial.size() == 0);
    }
}