package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class PlanejamentoRotaFilter implements Serializable {

    private List<Usuario> hierarquiaComercial;

    public PlanejamentoRotaFilter() {
        this.hierarquiaComercial = new ArrayList<>();
    }

    public PlanejamentoRotaFilter(List<Usuario> hierarquiaComercial) {
        this.hierarquiaComercial = hierarquiaComercial;
    }


    public List<Usuario> getHierarquiaComercial() {
        return hierarquiaComercial;
    }


    public boolean isEmpty() {
        return  (hierarquiaComercial == null || hierarquiaComercial.size() == 0);
    }

}