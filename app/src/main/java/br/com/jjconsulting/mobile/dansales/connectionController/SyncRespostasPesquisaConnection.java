package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.LayoutUserSync;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.dansales.model.PesquisaRespostaSync;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRegSync;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class SyncRespostasPesquisaConnection extends BaseConnection {

    private Context context;

    public SyncRespostasPesquisaConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        userInfo.getUserInfo(context);

    }

    public void syncRespostasPesquisa(ArrayList<PesquisaResposta> respostas) {
        createConnection(context);

        ArrayList< PesquisaRespostaSync > arrayRespostasSync = new ArrayList<>();

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        Gson gson = new Gson();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        String json = gson.toJson(respostas);

        String URL = JJSDK.getHost(context)+ CustomAPI.API_PESQUISA;

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTMED);
    }

    public void syncRespostasResumeCoaching(String idPesquisa, String promotor) {
        createConnection(context);
        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        String URL = JJSDK.getHost(context)+ CustomAPI.API_PESQUISA;
        URL += "?idPesquisa=" + idPesquisa + "&codPromotor=" +  promotor;
        connection.GET(URL, infoHeader, keyHeader);
    }
}
