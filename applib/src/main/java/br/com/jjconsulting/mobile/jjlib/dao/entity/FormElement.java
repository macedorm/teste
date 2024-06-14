package br.com.jjconsulting.mobile.jjlib.dao.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FormElement extends Element implements Serializable {

    private List<FormElementField> formfields;
    private String title;
    private String subTitle;


    public FormElement()
    {
        this.formfields = new ArrayList<>();
    }

    public FormElement(Element element)
    {
        this.formfields = new ArrayList<>();
        this.setName(element.getName());
        this.setTableName(element.getTableName());
        this.setInfo(element.getInfo());
        this.setIndexes(element.getIndexes());
        this.setMode(element.getMode());
        this.setRelations(element.getRelations());
        this.setCustomprocnameget(element.getCustomprocnameget());
        this.setCustomprocnameset(element.getCustomprocnameset());

        this.title = element.getName();
        this.subTitle = element.getInfo();
        for (ElementField f : element.getFields())
        {
            addField(f);
        }
    }


    public void addField(ElementField field)
    {
        this.formfields.add(new FormElementField(field));
        this.getFields().add(field);
    }

    public List<FormElementField> getFormfields() {
        return formfields;
    }

    public void setFormfields(List<FormElementField> formfields) {
        this.formfields = formfields;
    }

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

    public List<FormElementField> getFormFields() {
        return formfields;
    }

    public FormElementField getFormField(String name) {
        FormElementField field = null;
        for (FormElementField f : getFormFields()){
            if (f.getFieldname().equals(name)){
                field = f;
                break;
            }
        }

        return field;
    }

    public int getFormFieldIndex(String name) {
        FormElementField field = null;
        int index = -1;

        for(int ind = 0; ind < getFormFields().size(); ind++){
            if (getFormFields().get(ind).getFieldname().equals(name)){
                index = ind;
                break;
            }
        }

        return index;
    }



}
