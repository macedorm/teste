package br.com.jjconsulting.mobile.jjlib.util;


import android.content.Context;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.jjlib.masterdata.JJURL;

/**
 * Holds and manages the references of the logged user (usuário) and unidade de negócio.
 */
public class JJSDK {

    private static final String  KEY_USER = "key_user";
    private static final String  KEY_USER_KEY = "key_user_key";
    private static final String  KEY_COD_USER = "key_cod_user";
    private static final String  KEY_USER_TOKEN = "key_user_token";
    private static final String  KEY_HOST = "key_host";
    private static final String  KEY_DB_NAME = "key_db_name";
    private static final String  KEY_DB_VERSION = "key_db_version";

    public static void initializeAuthConnection(Context context, String[] hostCurrent, String userCurrent, String userKeyCurrent, String databaseName, int databaseVersion) {
        Gson gson = new Gson();

        JJSDK.saveInfo(context, KEY_USER, userCurrent);
        JJSDK.saveInfo(context, KEY_USER_KEY, userKeyCurrent);
        JJSDK.saveInfo(context, KEY_DB_NAME, databaseName);
        JJSDK.saveInfo(context, KEY_DB_VERSION, String.valueOf(databaseVersion));
        JJSDK.saveInfo(context, KEY_HOST, gson.toJson(hostCurrent));

    }

    public static boolean isInitialize(){
       return true;
    }

    private JJSDK() {

    }

    /**
     * Use it to (when) logout.
     */
    public static void clear(Context context) {
        SavePref savePref = new SavePref();
        savePref.deleteSharedPreferences(KEY_USER_TOKEN, context.getPackageName(), context);
        savePref.deleteSharedPreferences(KEY_COD_USER, context.getPackageName(), context);
    }

    public static String getHost(Context context) {
       JJURL jjurl = new JJURL();
       Gson gson = new Gson();
       String[] host = gson.fromJson(loadInfo(context, KEY_HOST), String[].class);
       return jjurl.getURL(context, host);
    }

    public static String[] getNameHost(Context context) {
        JJURL jjurl = new JJURL();
        Gson gson = new Gson();
        String[] host = gson.fromJson(loadInfo(context, KEY_HOST), String[].class);
        return jjurl.getNameURLArray(context, host);
    }

    public static void setIndexHost(Context context, int index){
        JJURL jjurl = new JJURL();
        jjurl.setIndex(context, index);
    }

    public static int getIndexHost(Context context){
        JJURL jjurl = new JJURL();
        return jjurl.getIndex(context);
    }

    public static String getCodUser(Context context) {
        return loadInfo(context, KEY_COD_USER);
    }

    public static void setCodUser(Context context, String codUser) {
        saveInfo(context, KEY_COD_USER, codUser);
    }

    public static String getToken(Context context) {
        return loadInfo(context, KEY_USER_TOKEN);
    }

    public static void setToken(Context context, String token) {
        saveInfo(context, KEY_USER_TOKEN, token);
    }

    public static String getUser(Context context) {
        return loadInfo(context, KEY_USER);
    }

    public static void setUser(Context context, String user) {
        saveInfo(context, KEY_USER, user);
    }

    public static String getUserKey(Context context) {
        return loadInfo(context, KEY_USER_KEY);
    }

    public static void setUserKey(Context context, String userKey) {
        saveInfo(context, KEY_USER_KEY, userKey);
    }

    public static String getDbName(Context context) {
        return loadInfo(context, KEY_DB_NAME);
    }

    public static void setDbName(Context context, int version) {
        saveInfo(context, KEY_DB_NAME, String.valueOf(version));
    }

    public static int getDbVersion(Context context) {
        return Integer.parseInt(loadInfo(context, KEY_DB_VERSION));
    }

    public static void setDbVersion(Context context, int version) {
        saveInfo(context, KEY_DB_VERSION, String.valueOf(version));
    }

    private static void saveInfo(Context context, String key, String value){
        SavePref savePref = new SavePref();
        savePref.saveSharedPreferences(key, context.getPackageName(), value, context);
    }

    private static String loadInfo(Context context, String key){
        SavePref savePref = new SavePref();
        String value = savePref.getPref(key, context.getPackageName(), context);
        return value;
    }

}
