package br.com.jjconsulting.mobile.jjlib.dao.entity;

import com.google.gson.annotations.SerializedName;

public class JsonFormElementField {

    @SerializedName("name")
    private String name;

    @SerializedName("autopostback")
    private boolean autoPostBack;

    @SerializedName("component")
    private int component;

    @SerializedName("enableexpression")
    private String enableExpression;

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
    private String visibleExpression;

    @SerializedName("dataitem")
    private FormElementDataItem dataitem;


    public JsonFormElementField()
    {
        this.component = TFormComponent.TEXT.getValue();
        this.dataitem = new FormElementDataItem();
        this.export = true;
        this.visibleExpression = "val:1";
        this.enableExpression = "val:1";
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
        this.dataitem = dataitem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
