package br.com.jjconsulting.mobile.jjlib.dao.entity;

/**
 * Created by jjconsulting on 01/03/2018.
 */

import java.util.HashMap;
import java.util.Map;

import br.com.jjconsulting.mobile.jjlib.dao.TypeDbInfo;

public class ElementField {

    private Boolean autonum;

    private Integer databehavior;

    private Integer datatype;

    private String defaultvalue;

    private Integer fieldid;

    private String fieldname;

    private ElementFilter filter;

    private Boolean ispk;

    private Boolean isrequired;

    private String label;

    private Integer size;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private FormElementDataItem dataItem;

    public Boolean getAutonum() {
        return autonum;
    }

    public void setAutonum(Boolean autonum) {
        this.autonum = autonum;
    }

    public TBehavior getDatabehavior() {
        return TBehavior.values()[databehavior - 1];
    }

    public void setDatabehavior(Integer databehavior) {
        this.databehavior = databehavior;
    }

    public TField getDatatype() {
        return TField.fromInteger(datatype);
    }


    public TypeDbInfo getTypeDbInfo(){
        switch (datatype){
            case 4: //INT
                return  TypeDbInfo.INTEGER;
            case 3: //FLOAT
                return  TypeDbInfo.NUMERIC;
            default:
                return  TypeDbInfo.TEXT;
        }
    }

    public void setDatatype(Integer datatype) {
        this.datatype = datatype;
    }

    public void setDatatype(TField datatype) {
        this.datatype = datatype.getValue();
    }

    public String getDefaultvalue() {
        return defaultvalue;
    }


    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }


    public Integer getFieldid() {
        return fieldid;
    }


    public void setFieldid(Integer fieldid) {
        this.fieldid = fieldid;
    }


    public String getFieldname() {
        return fieldname;
    }


    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }


    public ElementFilter getElementFilter() {
        return filter;
    }


    public void setElementFilter(ElementFilter elementFilter) {
        this.filter = elementFilter;
    }


    public Boolean getIspk() {
        return ispk;
    }


    public void setIspk(Boolean ispk) {
        this.ispk = ispk;
    }


    public Boolean getIsrequired() {
        return isrequired;
    }


    public void setIsrequired(Boolean isrequired) {
        this.isrequired = isrequired;
    }


    public String getLabel() {
        return label;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }


    public FormElementDataItem getDataItem() {
        return dataItem;
    }

    public void setDataItem(FormElementDataItem dataItem) {
        this.dataItem = dataItem;
    }
}