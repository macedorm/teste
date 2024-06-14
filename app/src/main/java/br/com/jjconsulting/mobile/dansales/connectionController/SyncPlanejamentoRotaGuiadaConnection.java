package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class SyncPlanejamentoRotaGuiadaConnection extends BaseConnection {

    private Context context;

    public SyncPlanejamentoRotaGuiadaConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;

        userInfo.getUserInfo(context);
    }

    public void getResumeStore(String promotor, String cliente, String unNeg) {
        createConnection(context);

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        Login login = userInfo.getUserInfo(context);
        if (login != null){
            keyHeader.add("token");
            infoHeader.add(login.getToken());
        }

        String URL = JJSDK.getHost(context) + CustomAPI.API_RESUME_STORE;

        URL += "?codPromotor=" + promotor + "&codCliente=" + cliente + "&codUnidNegoc=" + unNeg;

        connection.GET(URL, infoHeader, keyHeader);
    }


    public void getResumeAll(String promotor, String codPesquisa, String codUnidNegoc) {
        createConnection(context);

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        Login login = userInfo.getUserInfo(context);
        if (login != null){
            keyHeader.add("token");
            infoHeader.add(login.getToken());
        }

        String URL = JJSDK.getHost(context) + CustomAPI.API_RESUME_ALL;

        URL += "?codPromotor=" + promotor + "&codPesquisa=" + codPesquisa + "&codUnidNegoc=" + codUnidNegoc ;

        connection.GET(URL, infoHeader, keyHeader);
    }
}
