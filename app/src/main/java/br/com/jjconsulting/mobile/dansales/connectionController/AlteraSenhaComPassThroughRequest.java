package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.SolicitacaoAlteracaoSenha;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class AlteraSenhaComPassThroughRequest extends BaseConnection {

    private static final int CHANGE_PASSSWORD_TYPE_CONNECTION = 1;

    private Context context;
    private String endpoint;

    public AlteraSenhaComPassThroughRequest(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        this.typeConnection = CHANGE_PASSSWORD_TYPE_CONNECTION;
        this.endpoint = JJSDK.getHost(context) + Connection.API_CHANGE_PASSWORD_PASS;
    }

    public void alteraSenhaComPassThrough(SolicitacaoAlteracaoSenha solicitacaoAlteracaoSenha) {
        createConnection(context);

        ArrayList<String> headerKeys = new ArrayList<>();
        ArrayList<String> headerValues = new ArrayList<>();

        ArrayList<String> bodyKeys = new ArrayList<>();
        ArrayList<String> bodyValues = new ArrayList<>();

        bodyKeys.add("user");
        bodyValues.add(solicitacaoAlteracaoSenha.getUser());

        bodyKeys.add("appId");
        bodyValues.add(solicitacaoAlteracaoSenha.getAppId());

        bodyKeys.add("appImei");
        bodyValues.add(solicitacaoAlteracaoSenha.getAppImei());

        bodyKeys.add("pwdNew");
        bodyValues.add(solicitacaoAlteracaoSenha.getPwdNew());

        bodyKeys.add("pwdConfirm");
        bodyValues.add(solicitacaoAlteracaoSenha.getPwdConfirm());

        connection.POST(endpoint, bodyValues, bodyKeys, headerValues, headerKeys, Connection.INITIALTIMEOUTMED);
    }
}
