package br.com.jjconsulting.mobile.jjlib.syncData.model;

/**
 * Created by jjconsulting on 01/03/2018.
 */

import br.com.jjconsulting.mobile.jjlib.dao.InfoField;
import br.com.jjconsulting.mobile.jjlib.dao.InfoTable;
import br.com.jjconsulting.mobile.jjlib.dao.TypeDbInfo;

@InfoTable(tableName="TB_MASTERDATA")
public class MasterData {

    @InfoField(fieldName="ELEMENTNAME", isPK=true)
    private String elementName;

    @InfoField(fieldName="NAME", isNull=false)
    private String name;


    @InfoField(fieldName="JSON")
    private String json;

    @InfoField(fieldName="JSONFORM")
    private String jsonForm;

    @InfoField(fieldName="JSONUIOPTION")
    private String jsonUIOptions;

    @InfoField(fieldName="ORDER_SYNC", type= TypeDbInfo.INTEGER)
    private int orderSync;

    @InfoField(fieldName="MODE", type= TypeDbInfo.INTEGER)
    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Integer getOrder() {
        return orderSync;
    }

    public void setOrder(Integer order) {
        this.orderSync = order;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJsonForm() {
        return jsonForm;
    }

    public void setJsonForm(String jsonForm) {
        this.jsonForm = jsonForm;
    }

    public String getJsonUIOptions() {
        return jsonUIOptions;
    }

    public void setJsonUIOptions(String jsonUIOptions) {
        this.jsonUIOptions = jsonUIOptions;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }
}
