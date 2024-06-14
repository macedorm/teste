package br.com.jjconsulting.mobile.jjlib.dao.entity;

import com.google.gson.annotations.SerializedName;

public class FormActionField {

    @SerializedName("InternalField")
    public String internalField;

    @SerializedName("RedirectField")
    public String redirectField;

    public String getInternalField() {
        return internalField;
    }

    public void setInternalField(String internalField) {
        this.internalField = internalField;
    }

    public String getRedirectField() {
        return redirectField;
    }

    public void setRedirectField(String redirectField) {
        this.redirectField = redirectField;
    }
}
