package br.com.jjconsulting.mobile.jjlib.syncData.model;

import com.google.gson.annotations.SerializedName;

import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementInfo;


/**
 * Created by jjconsulting on 01/03/2018.
 */

public class SyncDictionaries {

    @SerializedName("Tot")
    private String total;

    @SerializedName("Dictionaries")
    private ElementInfo dictionaries[];


    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ElementInfo[] getDictionaries() {
        return dictionaries;
    }

    public void setDictionaries(ElementInfo[] dictionaries) {
        this.dictionaries = dictionaries;
    }
}
