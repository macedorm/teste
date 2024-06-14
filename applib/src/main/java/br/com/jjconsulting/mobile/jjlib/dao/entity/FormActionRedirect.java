package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.util.ArrayList;
import java.util.List;

public class FormActionRedirect {

    private String elementNameRedirect;

    private List<FormActionRelationField> entityReferences;

    private TRelationView viewType;

    public FormActionRedirect()
    {
        this.entityReferences = new ArrayList<>();
    }

    public String getElementNameRedirect() {
        return elementNameRedirect;
    }

    public void setElementNameRedirect(String elementNameRedirect) {
        this.elementNameRedirect = elementNameRedirect;
    }

    public List<FormActionRelationField> getRelationFields() {
        return entityReferences;
    }

    public void setRelationFields(List<FormActionRelationField> entityReferences) {
        this.entityReferences = entityReferences;
    }

    public TRelationView getViewType() {
        return viewType;
    }

    public void setViewType(TRelationView viewType) {
        this.viewType = viewType;
    }
}
