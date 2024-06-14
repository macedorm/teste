package br.com.jjconsulting.mobile.jjlib.connection;

import android.content.Context;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class SyncFieldsConnection {

    public ConnectionListener listener;
    public Connection connection;

    private Context context;

    private String host;
    private String token;

    public int typeConnection;

    private Gson gson;

    public SyncFieldsConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        this.host = JJSDK.getHost(context);
        this.token = JJSDK.getToken(context);
        this.gson = new Gson();
    }

    public void deleteField(String elementName, HashMap valuesHashMap, FormElement formElement){
        createConnection(context);

        String URL = host + Connection.API_MASTER;
        URL += elementName;

        String pk = "";

        for (ElementField item : formElement.getFields()) {
            if (item.getIspk()) {
                if(pk.length() == 0){
                    pk = valuesHashMap.get(item.getFieldname()).toString();
                } else {
                    pk += "," + valuesHashMap.get(item.getFieldname()).toString();

                }
            }
        }

        URL += "/" + pk;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("Token");
        infoHeader.add(token);

        keyHeader.add("Accept");
        infoHeader.add("application/json");

        connection.DELETE(URL, infoHeader, keyHeader, Connection.INITIALTIMEOUTLARGE);

    }

    public void insertField(String elementName, Hashtable values){
        createConnection(context);

        String URL = host +  Connection.API_MASTER + elementName + "?replace=true";

        ArrayList<Hashtable> arrayList = new ArrayList<>();
        arrayList.add(values);

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        Gson gson = new Gson();
        String json = gson.toJson(arrayList);

        keyHeader.add("Token");
        infoHeader.add(token);

        keyHeader.add("Accept");
        infoHeader.add("application/json");

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);

    }

    public void updateField(String elementName, Hashtable values){
        createConnection(context);

        String URL = host +  Connection.API_MASTER + elementName;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        Gson gson = new Gson();
        String json = gson.toJson(values);

        keyHeader.add("Token");
        infoHeader.add(token);

        connection.PUT(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);

    }

    public void insertFieldBatch(String elementName, String json){
        createConnection(context);

        String URL = host +  Connection.API_MASTER + elementName + "/batch?replace=true";

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();


        keyHeader.add("Token");
        infoHeader.add(token);

        keyHeader.add("Accept");
        infoHeader.add("application/json");

        keyHeader.add("replace");
        infoHeader.add("true");

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);

    }

    public void getField(Hashtable filters, FormElement formElement){
        createConnection(context);

        String URL = host +  Connection.API_MASTER + formElement.getName();

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        String pk = "";

        for (Object keyHash : filters.keySet()) {
            if(pk.length() == 0){
                pk = filters.get(keyHash).toString();
            } else {
                pk += "," + filters.get(keyHash).toString();

            }
        }

        URL += "/" + pk;

        keyHeader.add("Token");
        infoHeader.add(token);

        connection.GET(URL, info, key, infoHeader, keyHeader, Connection.INITIALTIMEOUTLARGE);

    }

    public void syncField(String elementName, Hashtable filter, int page, int size, int total) {

        createConnection(context);

        String URL;
        String raw = "";

        URL = host + Connection.API_MASTER;
        URL += elementName;

        if(filter != null){
            URL += "/filter";
            raw = gson.toJson(filter);

            LogUser.log("Filtro", raw);
        }

        URL += "?pag=" + page + "&regporpag=" + size + "&tot=" + total;

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("Token");
        infoHeader.add(token);

        keyHeader.add("Accept");
        infoHeader.add("application/json");

        if(filter == null) {
            connection.GET(URL, info, key, infoHeader, keyHeader, raw, Connection.INITIALTIMEOUTLARGE);
        } else {
            connection.POST(URL, infoHeader, keyHeader, raw, Connection.INITIALTIMEOUTLARGE);
        }
    }

    public void triggerForm(String elementName, String objname, Hashtable values, TPageState pageState){
        createConnection(context);

        String json = gson.toJson(values);

        String URL = host +  Connection.API_MASTER + elementName +"/trigger?pageState=" + pageState.getValue();

        if(!TextUtils.isNullOrEmpty(objname)){
            URL += "&objname=" + objname;
        }

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("Token");
        infoHeader.add(token);

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);

    }

    /**
     * Create Connection
     */
    public void createConnection(Context context) {


        if (connection == null) {
            connection = new Connection(context);
        }


        connection.setConnectionListener(new Connection.ConnectionListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onResponse(String response) {
                //LogUser.log(Config.TAG, "onResponse: " + response);
                listener.onSucess(response, typeConnection, null);
            }

            @Override
            public void onResponse(InputStreamReader reader) {
                listener.onSucess("", typeConnection, reader);
            }

            @Override
            public void onError(int code, VolleyError volleyError, String response) {
                listener.onError(code, volleyError, typeConnection, response);

            }
        });
    }


    public interface ConnectionListener {
        void onSucess(String response, int typeConnection, InputStreamReader reader);
        void onError(int code, VolleyError volleyError, int typeConnection, String response);
    }
}


