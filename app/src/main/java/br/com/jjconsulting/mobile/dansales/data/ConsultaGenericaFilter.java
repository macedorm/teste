package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class ConsultaGenericaFilter implements Serializable {

    private List<Usuario> hierarquiaComercial;
    private int status;
    private int tipoCadastro;
    private String dateStart;
    private String dateEnd;



    public ConsultaGenericaFilter() {
        this.hierarquiaComercial = new ArrayList<>();
    }

    public ConsultaGenericaFilter(int status, int tipoCadastro,
             String dateStart,  String dateEnd, List<Usuario> hierarquiaComercial) {
        this.hierarquiaComercial = hierarquiaComercial;
        this.status = status;
        this.tipoCadastro = tipoCadastro;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }


    public List<Usuario> getHierarquiaComercial() {
        return hierarquiaComercial;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(int tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getDateStart() {
        return dateStart;
    }

    public void setDateStart(String dateStart) {
        this.dateStart = dateStart;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public boolean isEmpty() {
        return  status == 0 && tipoCadastro == 0 && dateStart == null && dateEnd == null
                && (hierarquiaComercial == null || hierarquiaComercial.size() == 0);
    }

}