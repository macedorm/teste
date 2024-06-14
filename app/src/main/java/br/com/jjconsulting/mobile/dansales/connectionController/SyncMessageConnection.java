package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.model.MessageLida;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class SyncMessageConnection extends BaseConnection {

    private Context context;

    public SyncMessageConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;

        userInfo.getUserInfo(context);
    }

    public void syncMessage(ArrayList<Integer> listMessage, Date date) {
        createConnection(context);

        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).create();

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        String data = FormatUtils.toTextToCompareDateInSQlite(date);
        String codRegFunc = "";

        ArrayList arrayMenssageLida = new ArrayList();

        if (userInfo != null) {
            Login login = userInfo.getUserInfo(context);
            if(login != null) {
                keyHeader.add("token");
                infoHeader.add(login.getToken());
                codRegFunc = login.getUserId();
            }
        }

        for(Integer id: listMessage){
            MessageLida messageLida = new MessageLida();
            messageLida.setId_mensagem(String.valueOf(id));
            messageLida.setCod_reg_func(codRegFunc);
            messageLida.setDt_ult_alt(data);

            arrayMenssageLida.add(messageLida);
        }

        String json = gson.toJson(arrayMenssageLida);
        String URL = JJSDK.getHost(context) + Connection.API_MASTER  + context.getString(R.string.element_messagem_liga);
        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTSMALL);

        LogUser.log(Config.TAG, "Mensagens enviadas" + gson.toJson(listMessage));

    }

}
