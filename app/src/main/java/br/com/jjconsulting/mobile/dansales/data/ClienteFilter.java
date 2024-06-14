package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class ClienteFilter implements Serializable {

    private Organizacao organizacao;
    private Bandeira bandeira;
    private Integer status;
    private Integer planoCampo;
    private List<Usuario> hierarquiaComercial;

    public ClienteFilter() {
        this.hierarquiaComercial = new ArrayList<>();
    }

    public ClienteFilter(Organizacao organizacao, Bandeira bandeira, Integer status, Integer planoCampo,
                         List<Usuario> hierarquiaComercial) {
        this.organizacao = organizacao;
        this.bandeira = bandeira;
        this.status = status;
        this.planoCampo = planoCampo;
        this.hierarquiaComercial = hierarquiaComercial;
    }

    public Organizacao getOrganizacao() {
        return organizacao;
    }

    public Bandeira getBandeira() {
        return bandeira;
    }


    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getPlanoCampo() {
        return planoCampo;
    }

    public void setPlanoCampo(Integer planoCampo) {
        this.planoCampo = planoCampo;
    }

    public List<Usuario> getHierarquiaComercial() {
        return hierarquiaComercial;
    }

    public void setHierarquiaComercial(List<Usuario> hierarquiaComercial) {
        this.hierarquiaComercial = hierarquiaComercial;
    }

    public boolean isEmpty() {
        return organizacao == null && bandeira == null && status == null && planoCampo == null
                && (hierarquiaComercial == null || hierarquiaComercial.size() == 0);
    }
}