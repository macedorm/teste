package br.com.jjconsulting.mobile.jjlib.syncData.model;

/**
 * Created by jjconsulting on 01/03/2018.
 */

import com.google.gson.annotations.SerializedName;

import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.dao.InfoField;
import br.com.jjconsulting.mobile.jjlib.dao.InfoTable;

@InfoTable(tableName="TB_MASTERDATA_SYNC")
public class MasterDataSync {

    @InfoField(fieldName="NAME", isNull=true, isPK=true)
    private String name;

    @InfoField(fieldName="USERID", isNull=false, isPK=true)
    private String userId;

    @SerializedName("LastSync")
    @InfoField(fieldName="LAST_SYNC")
    private String lastSync;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }

}
