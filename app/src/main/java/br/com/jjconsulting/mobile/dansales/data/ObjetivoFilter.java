package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Familia;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class ObjetivoFilter implements Serializable {

    private Organizacao organizacao;
    private Bandeira bandeira;
    private Familia familia;
    private List<Usuario> hierarquiaComercial;
    private Cliente cliente;
    private TObjetivoUn objetivoUn;

    public ObjetivoFilter() {
        this.hierarquiaComercial = new ArrayList<>();
    }

    public ObjetivoFilter(Cliente cliente, Organizacao organizacao, Bandeira bandeira, Familia familia,
                          List<Usuario> hierarquiaComercial, TObjetivoUn objetivoUn) {
        this.organizacao = organizacao;
        this.bandeira = bandeira;
        this.familia = familia;
        this.hierarquiaComercial = hierarquiaComercial;
        this.cliente = cliente;
        this.objetivoUn = objetivoUn;
    }

    public Organizacao getOrganizacao() {
        return organizacao;
    }

    public Bandeira getBandeira() {
        return bandeira;
    }

    public Familia getFamilia() {
        return familia;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Usuario> getHierarquiaComercial() {
        return hierarquiaComercial;
    }

    public boolean isEmpty() {
        return organizacao == null && bandeira == null && familia == null && cliente == null && objetivoUn == null
                && (hierarquiaComercial == null || hierarquiaComercial.size() == 0);
    }

    public TObjetivoUn getObjetivoUn() {
        return objetivoUn;
    }

    public void setObjetivoUn(TObjetivoUn objetivoUn) {
        this.objetivoUn = objetivoUn;
    }

    public enum TObjetivoUn {
        CAIXA,
        FAT,
        TON,
    }


}