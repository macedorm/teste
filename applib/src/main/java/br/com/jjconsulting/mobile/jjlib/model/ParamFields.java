package br.com.jjconsulting.mobile.jjlib.model;

import java.io.Serializable;
import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TField;

public class ParamFields implements Serializable {

    public String elementName;
    public Hashtable filtes;

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public Hashtable getFiltes() {
        return filtes;
    }

    public void setFiltes(Hashtable filtes) {
        this.filtes = filtes;
    }
}
