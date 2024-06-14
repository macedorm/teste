package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.util.ArrayList;
import java.util.List;

public class JsonFormElement extends Element {

    public String title;
    public String subTitle;

    public List<JsonFormElementField> formfields = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<JsonFormElementField> getFormfields() {
        return formfields;
    }

   // private UIOptions options;


    public void setFormfields(List<JsonFormElementField> formfields) {
        this.formfields = formfields;
    }
/*
    public UIOptions getOptions() {
        return options;
    }

    public void setOptions(UIOptions options) {
        this.options = options;
    }*/


}
