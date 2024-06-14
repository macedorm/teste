package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.LayoutUserSync;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class SyncRespostasPesquisaLayoutConnection extends BaseConnection {

    private Context context;

    public SyncRespostasPesquisaLayoutConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void syncRespostasPesquisaLayout(LayoutUserSync layoutUserSync) {
        createConnection(context);

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        ArrayList<LayoutUserSync> arrayLayoutUserSync = new ArrayList<>();
        arrayLayoutUserSync.add(layoutUserSync);

        Gson gson = new Gson();
        String json = gson.toJson(arrayLayoutUserSync);

        connection.POST(JJSDK.getHost(context) + Connection.API_MASTER  +
                        context.getString(R.string.element_pesquisa_layout) +
                        "?replace=true",
         infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);

    }
}
