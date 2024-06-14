package br.com.jjconsulting.mobile.jjlib.syncData.model;

/**
 * Created by jjconsulting on 01/03/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.dao.InfoField;
import br.com.jjconsulting.mobile.jjlib.dao.InfoTable;

public class DicSyncParam {

    private static final String COD_REG_FUNC = "COD_REG_FUNC";
    private static final String DT_ULT_ALT = "DT_ULT_ALT";

    private String name;

    private Hashtable<String, Object> filters;

    public DicSyncParam(){
        filters = new Hashtable();
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        filters.put(COD_REG_FUNC, userId);
    }

    public void setLastSync(String lastSync) {
        filters.put(DT_ULT_ALT, lastSync);
    }

    public String getLastSync(){
        if(filters.containsKey(DT_ULT_ALT)){
            return filters.get(DT_ULT_ALT).toString();
        }else {
            return null;
        }
    }

}
