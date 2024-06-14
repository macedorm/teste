package br.com.jjconsulting.mobile.jjlib.syncData.connection;


import android.content.Context;
import android.util.Log;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TResponseType;
import br.com.jjconsulting.mobile.jjlib.syncData.model.SyncInfo;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class SyncDataConnection {

    public static final int SYNCMASTERDATA = 1;
    public static final int SYNCAVAILABLEUPDATE = 2;
    public static final int SYNCDATA = 3;

    private ConnectionListener listener;
    public Connection connection;
    private Context context;

    public int typeConnection;
    private String url;
    private String token;
    private String version;


    public SyncDataConnection(Context context, ConnectionListener listener, String url, String token, String version) {
        this.context = context;
        this.listener = listener;
        this.url = url;
        this.token = token;
        this.version = version;

    }

    public void syncRequest(int typeRequest) {
        createSyncRequest(typeRequest, "", null, "");
    }

    public void syncRequest(int typeRequest, String json) {
        createSyncRequest(typeRequest, json, null, "");
    }

    public void syncRequest(int typeRequest, String json, SyncInfo.SyncInfoElement syncInfo, String currentPage) {
        createSyncRequest(typeRequest, json, syncInfo, currentPage);
    }

    public void createSyncRequest(int typeRequest, String json, SyncInfo.SyncInfoElement syncInfo, String currentPage) {
        int timeout = Connection.INITIALTIMEOUTLARGE;

        createConnection(context);

        String URL = "";

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> header = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();


        keyHeader.add("Token");
        header.add(token);

        switch (typeRequest) {
            case SYNCMASTERDATA:
                URL = url + Connection.API_DICITIONARIES;
                typeConnection = SYNCMASTERDATA;
                connection.setMaxNumRetries(-1);

                keyHeader.add("Accept-Encoding");
                header.add("gzip");

                connection.GET(URL, info, key, header, keyHeader, timeout);

                break;
            case SYNCAVAILABLEUPDATE:
                URL = url + Connection.API_DICITIONARIES_COUNT;
                typeConnection = SYNCAVAILABLEUPDATE;
                timeout = Connection.COUNT_TIMEOUT_VALUE;

                keyHeader.add("Accept-Encoding");
                header.add("gzip");

                keyHeader.add("Accept");
                header.add("application/json");

                connection.setMaxNumRetries(-1);
                connection.POST(URL, header, keyHeader, json, timeout);

                break;
            case SYNCDATA:
                typeConnection = SYNCDATA;

                keyHeader.add("Accept-Encoding");
                header.add("gzip");

                keyHeader.add("Accept");
                header.add("text/csv");

                URL = url +  Connection.API_MASTER;
                URL += syncInfo.getName() + "?pag=" + currentPage + "&regporpag=" + Config.PAGE_SYNC;

                if (!TextUtils.isNullOrEmpty(syncInfo.getLastSync())) {
                    try {
                        URL  += "&dt_ult_alt=" + FormatUtils.toTextToCompareDateTInSQlite(FormatUtils.toDate(syncInfo.getLastSync()));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                Log.i("RD", "RD - " + syncInfo.getName());
                connection.setMaxNumRetries(-1);
                connection.GET(URL, new ArrayList<>(), new ArrayList<>(), header, keyHeader, timeout, TResponseType.INPUTSTREAM);

                break;
        }
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
                LogUser.log(Config.TAG, "onResponse: " + response);
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

