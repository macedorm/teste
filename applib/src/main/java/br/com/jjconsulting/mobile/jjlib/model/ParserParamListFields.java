package br.com.jjconsulting.mobile.jjlib.model;

import java.io.Serializable;
import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TField;

public class ParserParamListFields implements Serializable {
    public String ElementName;
    public Hashtable Filters;
    public String OrderBy;
    public int RegPerPage;
    public int CurrentPage;
    public int TotReg;

    public String getElementName() {
        return ElementName;
    }

    public void setElementName(String elementName) {
        ElementName = elementName;
    }

    public Hashtable getFilters() {
        return Filters;
    }

    public void setFilters(Hashtable filters) {
        Filters = filters;
    }

    public String getOrderBy() {
        return OrderBy;
    }

    public void setOrderBy(String orderBy) {
        OrderBy = orderBy;
    }

    public int getRegPerPage() {
        return RegPerPage;
    }

    public void setRegPerPage(int regPerPage) {
        RegPerPage = regPerPage;
    }

    public int getCurrentPage() {
        return CurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        CurrentPage = currentPage;
    }

    public int getTotReg() {
        return TotReg;
    }

    public void setTotReg(int totReg) {
        TotReg = totReg;
    }
}
