package br.com.jjconsulting.mobile.dansales.connectionController;


import android.content.Context;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.model.OrcStatusInput;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class AprovacaoLiberacaoConnection extends BaseConnection {

    private Context context;

    public AprovacaoLiberacaoConnection(Context context, BaseConnection.ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        userInfo.getUserInfo(context);
    }

    /**
     * Aprova/ReprovaPedido
     *
     * @param pedidoViewType
     * @param aprovaReprova  (0 - Reprova e 1 - Aprova)
     * @param codigoPedido
     * @param nota
     * @param data           (Somente para TipoVenda = UHT, nos demais casos será vaizo)
     */
    public void enviaAprovaReprova(PedidoViewType pedidoViewType, int aprovaReprova, String codigoPedido, String nota, String data) {

        createConnection(context);

        String type = "";

        if(aprovaReprova == 0){
            type = "reprovar";
        } else{
            switch (pedidoViewType){
                case APROVACAO:
                    type = "aprovar";
                    break;
                case LIBERACAO:
                    type = "liberar";
                    break;
            }
        }

        String URL = JJSDK.getHost(context) + Connection.API_ORCAMENTO + "/" + codigoPedido +  "/" + type;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        OrcStatusInput orcStatusInput = new OrcStatusInput();
        orcStatusInput.setObs(nota);

        if (!data.isEmpty()) {
            try {
                orcStatusInput.setDataAgendamento(FormatUtils.toConvertDate(data, "dd/MM/yyyy", "yyyy-MM-dd"));
            } catch (Exception ex) {
                orcStatusInput.setDataAgendamento(data);
            }
        } else {
            orcStatusInput.setDataAgendamento(data);
        }

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        String json = gson.toJson(orcStatusInput);

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTMED);
    }

}
