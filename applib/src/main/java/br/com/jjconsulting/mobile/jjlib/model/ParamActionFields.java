package br.com.jjconsulting.mobile.jjlib.model;

import com.google.gson.annotations.SerializedName;

import java.util.Hashtable;

public class ParamActionFields {

    @SerializedName("ElementName")
    private String elementName;
    @SerializedName("Values")
    private Hashtable values;
    @SerializedName("Action")
    public String action;

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public Hashtable getValues() {
        return values;
    }

    public void setValues(Hashtable values) {
        this.values = values;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
