package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.CRReport;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class SyncCRReportConnection extends BaseConnection {

    private Context context;

    public static final int SYNC_REPORT = 1;

    public SyncCRReportConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;

        userInfo.getUserInfo(context);
    }

    public void syncReport(CRReport crReport) {
        createConnection(context);

        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).create();

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<CRReport> arrayCRReport = new ArrayList<>();

        Login login = userInfo.getUserInfo(context);

        if (login != null){
            keyHeader.add("token");
            infoHeader.add(login.getToken());
        }

        crReport.setStatus(0);
        arrayCRReport.add(crReport);
        String json = gson.toJson(arrayCRReport);

        String URL = JJSDK.getHost(context) + Connection.API_MASTER +
                context.getString(R.string.element_sort_report_erro) +
                "?replace=true";

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTSMALL);
    }

}
