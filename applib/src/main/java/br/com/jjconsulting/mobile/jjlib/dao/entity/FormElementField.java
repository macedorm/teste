package br.com.jjconsulting.mobile.jjlib.dao.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class FormElementField  extends ElementField implements Serializable {

    @SerializedName("autopostback")
    private boolean autoPostBack;

    @SerializedName("component")
    private int component;
    @SerializedName("enableexpression")
    public String enableExpression;

    @SerializedName("export")
    private boolean export;

    @SerializedName("helpdescription")
    private String helpDescription;

    @SerializedName("linegroup")
    private int lineGroup;

    @SerializedName("numberdecimalplaces")
    private int numberOfDecimalPlaces;

    @SerializedName("order")
    private int order;

    @SerializedName("triggerexpression")
    private String triggerExpression;

    @SerializedName("visibleexpression")
    public String visibleExpression;

    @SerializedName("dataitem")
    private FormElementDataItem dataitem;

    @SerializedName("actions")
    private FormElementFieldActions actions;

    public FormElementField()
    {
        this.actions = new FormElementFieldActions();
        this.component = TFormComponent.TEXT.getValue();
        this.dataitem = new FormElementDataItem();
        this.export = true;
        this.visibleExpression = "val:1";
        this.enableExpression = "val:1";
    }


    public FormElementField(ElementField elementField)
    {
        this.setDataItem(elementField.getDataItem());
        this.setFieldname(elementField.getFieldname());
        this.setLabel(elementField.getLabel());
        this.setDatatype(elementField.getDatatype().getValue());
        this.setSize(elementField.getSize());
        this.setDefaultvalue(elementField.getDefaultvalue());
        this.setIsrequired(elementField.getIsrequired());
        this.setIspk(elementField.getIspk());
        this.setAutonum(elementField.getAutonum());
        this.setElementFilter(elementField.getElementFilter());
        this.setDatabehavior(elementField.getDatabehavior().ordinal());

        if (elementField.getDatatype() == TField.DATE ||
                elementField.getDatatype() == TField.DATETIME)
        {
            this.component = TFormComponent.DATE.getValue();
        }
        else if (elementField.getDatatype() == TField.INT)
        {
            this.component = TFormComponent.NUMBER.getValue();
        }
        else
        {
            if (elementField.getSize() > 290) {
                  this.component = TFormComponent.TEXTAREA.getValue();
            }else{
                this.component = TFormComponent.TEXT.getValue();
            }
        }

        this.visibleExpression = "val:1";
        this.enableExpression = "val:1";
        if (elementField.getIspk())
        {
            if (elementField.getAutonum())
            {
                this.enableExpression = "exp:{pagestate} = 'FILTER'";
                this.visibleExpression = "exp:{pagestate} <> 'INSERT'";
            }
            else
            {
                this.enableExpression = "exp:{pagestate} <> 'UPDATE'";
            }

        }

        this.setExport(true);
    }

    public boolean isAutoPostBack() {
        return autoPostBack;
    }

    public void setAutoPostBack(boolean autoPostBack) {
        this.autoPostBack = autoPostBack;
    }

    public int getComponent() {
        return component;
    }

    public void setComponent(int component) {
        this.component = component;
    }

    public String getEnableExpression() {
        return enableExpression;
    }

    public void setEnableExpression(String enableExpression) {
        this.enableExpression = enableExpression;
    }

    public boolean isExport() {
        return export;
    }

    public void setExport(boolean export) {
        this.export = export;
    }

    public String getHelpDescription() {
        return helpDescription;
    }

    public void setHelpDescription(String helpDescription) {
        this.helpDescription = helpDescription;
    }

    public int getLineGroup() {
        return lineGroup;
    }

    public void setLineGroup(int lineGroup) {
        this.lineGroup = lineGroup;
    }

    public int getNumberOfDecimalPlaces() {
        return numberOfDecimalPlaces;
    }

    public void setNumberOfDecimalPlaces(int numberOfDecimalPlaces) {
        this.numberOfDecimalPlaces = numberOfDecimalPlaces;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTriggerExpression() {
        return triggerExpression;
    }

    public void setTriggerExpression(String triggerExpression) {
        this.triggerExpression = triggerExpression;
    }

    public String getVisibleExpression() {
        return visibleExpression;
    }

    public void setVisibleExpression(String visibleExpression) {
        this.visibleExpression = visibleExpression;
    }

    public FormElementDataItem getDataItem() {
        return dataitem;
    }

    public void setDataItem(FormElementDataItem dataItem) {
        this.dataitem = dataItem;
    }

    public FormElementDataItem getDataitem() {
        return dataitem;
    }

    public void setDataitem(FormElementDataItem dataitem) {
        this.dataitem = dataitem;
    }

    public FormElementFieldActions getActions() {
        return actions;
    }

}
