package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class RotasFilter implements Serializable {

    private Integer status;
    private Date date;
    private List<Usuario> hierarquiaComercial;

    public RotasFilter() {
    }

    public RotasFilter(Integer status, Date date,  List<Usuario> hierarquiaComercial) {
        this.status = status;
        this.date = date;
        this.hierarquiaComercial = hierarquiaComercial;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Usuario> getHierarquiaComercial() {
        return hierarquiaComercial;
    }

    public void setHierarquiaComercial(List<Usuario> hierarquiaComercial) {
        this.hierarquiaComercial = hierarquiaComercial;
    }

    public boolean isEmpty() {
        return (status == null && date == null
                && (hierarquiaComercial == null || hierarquiaComercial.size() == 0));

    }
}