package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.model.RotaAcao;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class SyncRotaGuiadaConnection extends BaseConnection {

    private Context context;



    public SyncRotaGuiadaConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;

        userInfo.getUserInfo(context);
    }

    public void syncRotas(ArrayList<Rotas> rotasList) {
        sendRotas(rotasList, null, null, null);
    }

    public void syncTarefas(ArrayList<RotaTarefas> listRotaTarefas) {
        sendRotas(null, listRotaTarefas, null, null);
    }

    public void syncAcoes( ArrayList<RotaAcao> listRotaAcao) {
        sendRotas(null, null, listRotaAcao, null);
    }

    public void syncPlanejamento( ArrayList<HashMap<String, Object>> listPlanejamento) {
        sendRotas(null, null, null, listPlanejamento);
    }
    private void sendRotas(ArrayList<Rotas> rotasList, ArrayList<RotaTarefas> listRotaTarefas, ArrayList<RotaAcao> listRotaAcao, ArrayList<HashMap<String,Object>> listPlanejamento) {
        createConnection(context);

        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).create();

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        String json = "";
        String table = "";

        if(rotasList != null){
            json = gson.toJson(rotasList);
            table = context.getString(R.string.element_rota_guiada);
        }else if(listRotaTarefas != null){
            json = gson.toJson(listRotaTarefas);
            table = context.getString(R.string.element_rota_guiada_tarefa);
        } else if(listRotaAcao != null){
            json = gson.toJson(listRotaAcao);
            table = context.getString(R.string.element_rota_guiada_acao);
        } else if(listPlanejamento != null) {
            json = gson.toJson(listPlanejamento);
            table = context.getString(R.string.element_planejamento_rota);
        }

        Login login = userInfo.getUserInfo(context);
        if (login != null){
            keyHeader.add("token");
            infoHeader.add(login.getToken());
        }

        LogUser.log("TB_ROTAGUIADA_PLANEJAMENTO_ROTA", json);

        String URL = JJSDK.getHost(context) + Connection.API_MASTER + table + "?replace=true";

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTSMALL);
    }

}
