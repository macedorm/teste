package br.com.jjconsulting.mobile.jjlib.dao.entity;

/**
 * Created by jjconsulting on 30/03/2018.
 */

import java.util.ArrayList;
import java.util.List;

public class ElementIndex {

    private List<String> columns;
    private boolean isUnique;
    private boolean isClustered;

    public ElementIndex()
    {
        this.columns = new ArrayList<>();
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public boolean isClustered() {
        return isClustered;
    }

    public void setClustered(boolean clustered) {
        isClustered = clustered;
    }

}
