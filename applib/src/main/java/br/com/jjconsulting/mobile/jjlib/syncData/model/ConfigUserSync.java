package br.com.jjconsulting.mobile.jjlib.syncData.model;

/**
 * Created by jjconsulting on 01/03/2018.
 */

import java.util.Hashtable;

public class ConfigUserSync {
    private String url;
    private String token;
    private String idUser;
    private String version;
    private String databaseName;
    private int databaseVersion;

    public ConfigUserSync(){

    }

    public ConfigUserSync(int databaseVersion, String databaseName){
        this.databaseName =  databaseName;
        this.databaseVersion = databaseVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }
}
