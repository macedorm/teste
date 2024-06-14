package br.com.jjconsulting.mobile.dansales.connectionController;


import android.content.Context;
import android.util.Base64;

import java.util.ArrayList;
import java.util.Locale;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.HardwareUtil;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class    LoginConnection extends BaseConnection {

    public static final int LOGIN = 1;
    private Context context;

    public LoginConnection(Context context, BaseConnection.ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Login
     *
     * @param user
     * @param password
     */
    public void login(String user, String password, String appId) {
        typeConnection = LOGIN;

        createConnection(context);

        String URL = JJSDK.getHost(context) + Connection.API_LOGIN;

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        key.add("user");
        info.add(user);

        key.add("password");
        info.add(password);
        
        key.add("imei");
        info.add(HardwareUtil.getDeviceIMEI(context) + "");

        key.add("appId");
        info.add(appId + "");

        connection.POST(URL, info, key, infoHeader, keyHeader,  Connection.INITIALTIMEOUTSMALL);
    }


}
