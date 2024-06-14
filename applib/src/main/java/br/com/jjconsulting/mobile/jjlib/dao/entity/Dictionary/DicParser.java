package br.com.jjconsulting.mobile.jjlib.dao.entity.Dictionary;

import br.com.jjconsulting.mobile.jjlib.dao.entity.Element;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.JsonFormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.JsonFormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.UIOptions;

public class DicParser {

    private Element table;
    private JsonFormElement formJson;
    public UIOptions uiOptions;

    public FormElement getFormElement() {

        FormElement form = new FormElement(table);
        form.setTitle(formJson.title);
        form.setSubTitle(formJson.subTitle);

        for (JsonFormElementField item : formJson.formfields){
            FormElementField field = form.getFormField(item.getName());

            field.setComponent(item.getComponent());
            field.setVisibleExpression(item.getVisibleExpression());
            field.setEnableExpression(item.getEnableExpression());
            field.setTriggerExpression(item.getTriggerExpression());
            field.setOrder(item.getOrder());
            field.setLineGroup(item.getLineGroup());
            field.setHelpDescription(item.getHelpDescription());
            field.setDataItem(item.getDataItem());
            field.setExport(item.isExport());
            field.setAutoPostBack(item.isAutoPostBack());
            field.setNumberOfDecimalPlaces(item.getNumberOfDecimalPlaces());

            //Verificar
            //field.setActions(item.getActions());
        }

        return form;
    }

    public Element getTable() {
        return table;
    }

    public void setTable(Element table) {
        this.table = table;
    }

    public JsonFormElement getFormJson() {
        return formJson;
    }

    public void setFormJson(JsonFormElement formJson) {
        this.formJson = formJson;
    }

    public UIOptions getUIOptions() {
        return uiOptions;
    }

    public void setUIOptions(UIOptions UIOptions) {
        this.uiOptions = UIOptions;
    }

}


