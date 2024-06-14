package br.com.jjconsulting.mobile.jjlib.dao.entity;


public class ElementInfo{
    private Element table;
    private JsonFormElement form;
    private UIOptions uioptions;


    public Element getTable() {
        return table;
    }

    public void setTable(Element table) {
        this.table = table;
    }

    public JsonFormElement getForm() {
        return form;
    }

    public void setForm(JsonFormElement form) {
        this.form = form;
    }

    public UIOptions getUioptions() {
        return uioptions;
    }

    public void setUioptions(UIOptions uioptions) {
        this.uioptions = uioptions;
    }
}
