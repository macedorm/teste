package br.com.jjconsulting.mobile.jjlib.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DataTable implements Serializable {

    ArrayList<DataItem> dataItens;

    public ArrayList<DataItem> getDataItens() {
        return dataItens;
    }

    public void setDataItens(ArrayList<DataItem> dataItens) {
        this.dataItens = dataItens;
    }

    public void addNew(DataItem item){
        if(dataItens == null){
            dataItens = new ArrayList<>();
        }
        dataItens.add(item);
    }
}
