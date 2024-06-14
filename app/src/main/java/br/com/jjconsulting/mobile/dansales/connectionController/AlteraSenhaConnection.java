package br.com.jjconsulting.mobile.dansales.connectionController;


import android.content.Context;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class AlteraSenhaConnection extends BaseConnection {

    public static final int LOGIN = 1;
    private Context context;

    public AlteraSenhaConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
    }


    /**
     * Altear a senha do usuário
     *
     * @param password
     * @param newPassword
     */
    public void alteraSenha(String password, String newPassword, String confirmPassword, String user) {

        typeConnection = LOGIN;

        createConnection(context);

        String URL = JJSDK.getHost(context) + Connection.API_CHANGE_PASSWORD;

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        key.add("pwdCurrent");
        info.add(password);

        key.add("pwdNew");
        info.add(newPassword);

        key.add("pwdConfirm");
        info.add(confirmPassword);

        key.add("user");
        info.add(user);

        connection.POST(URL, info, key, infoHeader, keyHeader, Connection.INITIALTIMEOUTSMALL);
    }

}
