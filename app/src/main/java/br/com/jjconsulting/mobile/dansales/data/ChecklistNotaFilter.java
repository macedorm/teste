package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Familia;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class ChecklistNotaFilter implements Serializable {

    private List<Usuario> hierarquiaComercial;
    private Cliente cliente;

    public ChecklistNotaFilter() {
        this.hierarquiaComercial = new ArrayList<>();
    }

    public ChecklistNotaFilter(Cliente cliente,
                               List<Usuario> hierarquiaComercial) {
        this.hierarquiaComercial = hierarquiaComercial;
        this.cliente = cliente;
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
        return  cliente == null
                && (hierarquiaComercial == null || hierarquiaComercial.size() == 0);
    }

}