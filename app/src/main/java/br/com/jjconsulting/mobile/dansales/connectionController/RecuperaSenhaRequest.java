package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;

import java.util.ArrayList;
import java.util.Locale;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.SolicitacaoRecuperacaoSenha;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TResponseType;
import br.com.jjconsulting.mobile.jjlib.util.HardwareUtil;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class RecuperaSenhaRequest extends BaseConnection {

    private static final int RECOVER_PASSSWORD_TYPE_CONNECTION = 1;

    private Context context;
    private String endpoint;

    public RecuperaSenhaRequest(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        this.typeConnection = RECOVER_PASSSWORD_TYPE_CONNECTION;
        this.endpoint = JJSDK.getHost(context) + Connection.API_RECOVER_PASSWORD;
    }

    public void recuperaSenha(SolicitacaoRecuperacaoSenha solicitacaoRecuperacaoSenha) {
        createConnection(context);

        ArrayList<String> headerKeys = new ArrayList<>();
        ArrayList<String> headerValues = new ArrayList<>();

        ArrayList<String> bodyKeys = new ArrayList<>();
        ArrayList<String> bodyValues = new ArrayList<>();

        bodyKeys.add("user");
        bodyValues.add(solicitacaoRecuperacaoSenha.getUser());

        bodyKeys.add("appId");
        bodyValues.add(solicitacaoRecuperacaoSenha.getAppId());

        bodyKeys.add("appImei");
        bodyValues.add(solicitacaoRecuperacaoSenha.getDispositivoIMEI());

        connection.POST(endpoint, bodyValues, bodyKeys, headerValues, headerKeys, Connection.INITIALTIMEOUTMED);
    }
}
