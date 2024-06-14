package br.com.jjconsulting.mobile.dansales.connectionController;


import android.content.Context;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.MessageSend;
import br.com.jjconsulting.mobile.dansales.model.UserChat;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ChatConnection extends BaseConnection {

    private Context context;

    public ChatConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        userInfo.getUserInfo(context);
    }

    public void sendMessage(String id, String message) {

        createConnection(context);

        String URL = ""; //BuildConfig.URL_ISSAC;

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        key.add("version");
        info.add(BuildConfig.VERSION_NAME);

        keyHeader.add("Content-Type");
        infoHeader.add("application/json");

        Usuario usuario = Current.getInstance(context).getUsuario();
        MessageSend messageSend = new MessageSend(id, message, usuario.getCodigo(),  usuario.getEmail());

        String json =  gson.toJson(messageSend);

        LogUser.log(Config.TAG, "Message Send: " + json);

        connection.setIgnoreHeaders(true);
        connection.POST(URL, infoHeader, keyHeader, json , Connection.INITIALTIMEOUTLARGE);
    }

}
