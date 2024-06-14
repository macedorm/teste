package br.com.jjconsulting.mobile.dansales.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class LayoutFilter implements Serializable {

    private String codigo;
    private String filter;
    private String arrayFilter[];

    private Date dateStart;

    private boolean isGroup;


    public LayoutFilter(){

    }

    public LayoutFilter(String filter, Date dateStart) {
        this.filter = filter;
        this.dateStart = dateStart;
    }

    public String getFilter() {
        return filter;
    }


    public boolean isEmpty() {
        return filter == null && dateStart == null;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String[] getArrayFilter() {
        return arrayFilter;
    }

    public void setArrayFilter(String[] arrayFilter) {
        this.arrayFilter = arrayFilter;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }


}