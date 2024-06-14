package br.com.jjconsulting.mobile.dansales.connectionController;

import android.content.Context;
import android.os.Build;

import com.google.gson.Gson;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.model.OrcInput;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class SyncPedidoConnection extends BaseConnection {

    private Context context;
    private boolean isQuestionOK;

    public SyncPedidoConnection(Context context, ConnectionListener listener,
                                boolean isQuestionOK) {
        this.context = context;
        this.isQuestionOK = isQuestionOK;
        this.listener = listener;

        userInfo.getUserInfo(context);
    }

    public void syncPedido(Pedido pedido, ArrayList<ItemPedido> itemPedido) {
        createConnection(context);
        Gson gson = new Gson();

        ArrayList<String> keyHeader = new ArrayList<>();
        ArrayList<String> infoHeader = new ArrayList<>();

        OrcInput orcInput = new OrcInput();
        orcInput.setQuestionOk(isQuestionOK);
        orcInput.setOrcCab(pedido);
        orcInput.setOrcItens(itemPedido);

        String json = gson.toJson(orcInput);

        if (userInfo != null) {
            Login login = userInfo.getUserInfo(context);
            if(login != null) {
                keyHeader.add("token");
                infoHeader.add(login.getToken());
            }
        }

        String URL = JJSDK.getHost(context) + Connection.API_ORCAMENTO;
        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTMED);
    }

}
