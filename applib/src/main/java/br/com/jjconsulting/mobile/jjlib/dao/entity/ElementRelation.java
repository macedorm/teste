package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.util.ArrayList;
import java.util.List;

public class ElementRelation {

    public int viewType;

    private String childElement;

    private String title;

    private List<ElementRelationColumn> columns;

    private Boolean updateOnCascade;

    private Boolean deleteOnCascade;

    public ElementRelation()
    {
        this.columns = new ArrayList<>();
    }

    public TRelationView getViewType() {
        return TRelationView.fromInteger(viewType);
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getChildElement() {
        return childElement;
    }

    public void setChildElement(String childElement) {
        this.childElement = childElement;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ElementRelationColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ElementRelationColumn> columns) {
        this.columns = columns;
    }

    public Boolean getUpdateOnCascade() {
        return updateOnCascade;
    }

    public void setUpdateOnCascade(Boolean updateOnCascade) {
        this.updateOnCascade = updateOnCascade;
    }

    public Boolean getDeleteOnCascade() {
        return deleteOnCascade;
    }

    public void setDeleteOnCascade(Boolean deleteOnCascade) {
        this.deleteOnCascade = deleteOnCascade;
    }
}
