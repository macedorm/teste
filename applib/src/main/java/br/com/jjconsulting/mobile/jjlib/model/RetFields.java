package br.com.jjconsulting.mobile.jjlib.model;

import java.util.ArrayList;
import java.util.HashMap;


public class RetFields {


    public String tot;

    public ArrayList<HashMap<String, Object>> fields;

    public String getTot() {
        return tot;
    }

    public void setTot(String tot) {
        this.tot = tot;
    }

    public ArrayList<HashMap<String, Object>> getFields() {
        return fields;
    }

    public void setFields(ArrayList<HashMap<String, Object>> fields) {
        this.fields = fields;
    }
}
