package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.util.ArrayList;
import java.util.List;

public class Element {


    private String customprocnameget;

    private String customprocnameset;

    private List<ElementField> fields = null;

    private List<ElementIndex> indexes = null;

    private String info;

    private String name;

    private String tableName;

    private int mode;

    private List<ElementRelation> relations = null;


    public Element()
    {
        this.fields = new ArrayList<>();
        this.indexes = new ArrayList<>();
    }


    public Element(FormElement form)
    {
        this.fields = new ArrayList<>();
        this.indexes = new ArrayList<>();
        this.relations = new ArrayList<>();

        this.name = form.getName();
        this.info = form.getInfo();
        this.indexes = form.getIndexes();
        this.relations = form.getRelations();
        this.customprocnameget = form.getCustomprocnameget();
        this.customprocnameset = form.getCustomprocnameset();

        for (ElementField f : form.getFields()){
            this.fields.add(f);
        }
    }


    public String getCustomprocnameget() {
        return customprocnameget;
    }


    public void setCustomprocnameget(String customprocnameget) {
        this.customprocnameget = customprocnameget;
    }


    public String getCustomprocnameset() {
        return customprocnameset;
    }


    public void setCustomprocnameset(String customprocnameset) {
        this.customprocnameset = customprocnameset;
    }


    public List<ElementField> getFields() {
        return fields;
    }


    public void setFields(List<ElementField> fields) {
        this.fields = fields;
    }


    public List<ElementIndex> getIndexes() {
        return indexes;
    }


    public void setIndexes(List<ElementIndex> indexes) {
        this.indexes = indexes;
    }


    public String getInfo() {
        return info;
    }


    public void setInfo(String info) {
        this.info = info;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getTableName() {
        if (tableName == null)
            return name;
        else
            return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ElementRelation> getRelations() {
        return relations;
    }


    public void setRelations(List<ElementRelation> relations) {
        this.relations = relations;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public List<ElementField> getListPk(){
        List<ElementField> list = new ArrayList<>();
        for (ElementField field: getFields()) {
            if(field.getIspk())
                list.add(field);
        }
        return list;
    }


}
