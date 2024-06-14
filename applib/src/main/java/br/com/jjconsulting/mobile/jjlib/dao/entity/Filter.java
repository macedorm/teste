package br.com.jjconsulting.mobile.jjlib.dao.entity;

/**
 * Created by jjconsulting on 01/03/2018.
 */

import java.util.HashMap;
import java.util.Map;

public class Filter {

    private Boolean isrequired;
    private Integer type;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Boolean getIsrequired() {
        return isrequired;
    }

    public void setIsrequired(Boolean isrequired) {
        this.isrequired = isrequired;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
