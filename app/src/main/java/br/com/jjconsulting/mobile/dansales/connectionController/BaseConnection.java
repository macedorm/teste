package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;
import android.util.Log;

import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;


public class BaseConnection {

    public BaseConnection.ConnectionListener listener;
    public Connection connection;
    public UserInfo userInfo;
    public int typeConnection;
    public Gson gson;

    private boolean ignoreHeaders;

    public BaseConnection() {
        userInfo = new UserInfo();
        gson = new Gson();
    }

    /**
     * Create Connection
     */
    public void createConnection(Context context) {


        if (connection == null) {
            connection = new Connection(context);
        }

        connection.setConnectionListener(new Connection.ConnectionListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onResponse(String response) {
                listener.onSucess(response, typeConnection, null, null);
            }

            @Override
            public void onResponse(InputStreamReader reader) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;

                    ArrayList<String[]> linesValues = new ArrayList<>();

                    while ((line = bufferedReader.readLine()) != null) {
                        //Ignora linhas em branco
                        if (line.trim().length() == 0)
                           continue;

                        String[] values = line.split("\\|", -1);

                        if(values == null || values.length == 0)
                            continue;

                        linesValues.add(values);

                    }

                    reader.close();
                    bufferedReader.close();
                    listener.onSucess("", typeConnection, reader, linesValues);

                }catch (Exception ex){
                    int errorCode = Connection.NO_CONNECTION;
                    ServerError serverError = new ServerError();
                    listener.onError(serverError, errorCode, typeConnection, null);
                }
            }


            @Override
            public void onError(int code, VolleyError volleyError, String response) {
                LogUser.log("connection: " + response);
                listener.onError(volleyError, code, typeConnection, response);
            }
        });
    }


    public interface ConnectionListener {
        void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> list);
        void onError(VolleyError volleyError, int code, int typeConnection, String response);
    }
}
