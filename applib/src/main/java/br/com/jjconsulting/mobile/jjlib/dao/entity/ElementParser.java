package br.com.jjconsulting.mobile.jjlib.dao.entity;

import java.util.ArrayList;

public class ElementParser {

    private ArrayList<ElementInfo> elementInfo;

    public ArrayList<ElementInfo> getElementInfo() {
        return elementInfo;
    }

    public void setElementInfo(ArrayList<ElementInfo> elementInfo) {
        this.elementInfo = elementInfo;
    }

    public class ElementInfo{
        private Element table;
        private FormElement form1;

        public Element getTable() {
            return table;
        }

        public void setTable(Element table) {
            this.table = table;
        }

        public FormElement getForm() {
            return form1;
        }

        public void setForm(FormElement form) {
            this.form1 = form;
        }
    }
}
