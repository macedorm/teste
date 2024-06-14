package br.com.jjconsulting.mobile.jjlib.dao.entity;

/**
 * Created by jjconsulting on 01/03/2018.
 */

public class ElementFilter {

    private Boolean isrequired;
    private Integer type;


    public Boolean getIsrequired() {
        return isrequired;
    }

    public void setIsrequired(Boolean isrequired) {
        this.isrequired = isrequired;
    }

    public TFilter getType() {
        return TFilter.fromInteger(type);
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setType(TFilter type) {
        this.type = type.getValue();
    }

}
