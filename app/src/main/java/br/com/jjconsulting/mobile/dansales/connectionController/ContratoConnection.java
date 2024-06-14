package br.com.jjconsulting.mobile.dansales.connectionController;


import android.content.Context;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.jjlib.Connection;

public class ContratoConnection extends BaseConnection {

    private Context context;

    public ContratoConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        userInfo.getUserInfo(context);
    }

    public void getList() {

        createConnection(context);

        String URL = BuildConfig.URL_API + CustomAPI.API_PESQUISA;

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        key.add("version");
        info.add(BuildConfig.VERSION_NAME);

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.POST(URL, info, key, infoHeader, keyHeader,  Connection.INITIALTIMEOUTLARGE);
    }

}
