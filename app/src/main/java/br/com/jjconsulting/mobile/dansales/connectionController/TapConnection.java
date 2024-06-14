package br.com.jjconsulting.mobile.dansales.connectionController;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.TapListFilterActivity;
import br.com.jjconsulting.mobile.dansales.data.TapFilter;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.model.TapCabec;
import br.com.jjconsulting.mobile.dansales.model.TapConnectionType;
import br.com.jjconsulting.mobile.dansales.model.TapInsert;
import br.com.jjconsulting.mobile.dansales.model.TapItem;
import br.com.jjconsulting.mobile.dansales.model.TapSend;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapConnection extends BaseConnection {

    private Context context;

    public TapConnection(Context context, ConnectionListener listener) {
        this.context = context;
        this.listener = listener;
        userInfo.getUserInfo(context);
    }

    public void getListETap(String unidNeg, String mNome, TapActionType tapActionType, TapFilter mTapFilter) {

        typeConnection = TapConnectionType.TAP_LIST.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP;

        ArrayList<String> info = new ArrayList<>();
        ArrayList<String> key = new ArrayList<>();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        String param = "?codUnNeg=" + unidNeg;
        param += "&userAnFin=true";

        if (mNome != null) {
            param += "&filter=" + mNome;
        }

        param += "&status=";

        if (mTapFilter == null || mTapFilter.getStatus() == null) {
            param += String.valueOf(0);
        } else {
            param += String.valueOf(mTapFilter.getStatus().getCodigo());
        }

        param += "&dataDe=";
        if (mTapFilter == null || mTapFilter.getDateStart() == null) {
            param += FormatUtils.toTextToCompareshortDateInSQlite(FormatUtils.getDateTimeNow(0, TapListFilterActivity.DIF_DATA_DEFAULT, 0));
        } else {
            param += FormatUtils.toTextToCompareshortDateInSQlite(mTapFilter.getDateStart());
        }
        param += "&dataAte=";
        if (mTapFilter == null || mTapFilter.getDateEnd() == null) {
            param += FormatUtils.toTextToCompareshortDateInSQlite(new Date());
        } else {
            param += FormatUtils.toTextToCompareshortDateInSQlite(mTapFilter.getDateEnd());
        }

        param += "&minhasAprov=";

        if (mTapFilter == null){
            param += "false";
        } else {
            param += mTapFilter.isPendingApproval();
        }

        param += "&tipoLista=" + TapActionType.getTapActionTypeParam(tapActionType);

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.GET(URL + param, infoHeader, keyHeader);
    }

    public void getETapDetail(String mCodTap, TapActionType tapActionType) {
        typeConnection = TapConnectionType.TAP_DETAIL.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        String param = "/" + mCodTap;
        param += "?tipoLista=" + TapActionType.getTapActionTypeParam(tapActionType);

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.GET(URL + param, infoHeader, keyHeader);
    }

    public void addETapDetail(String codUser, String mEmpresa, String mFilial, String mCodCli, boolean isRascunho) {
        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP;

        TapInsert tapInsert =  new TapInsert();
        tapInsert.setCodUsuario(codUser);
        tapInsert.setCodEmpresa(mEmpresa);
        tapInsert.setCodfilial(mFilial);
        tapInsert.setCodCliente(mCodCli);
        tapInsert.setRacunho(isRascunho);

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        String json = gson.toJson(tapInsert);

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);
    }

    public void deleteETap(int tapId) {
        typeConnection = TapConnectionType.TAP_DELETE.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP + "/" + tapId;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.DELETE(URL, infoHeader, keyHeader, Connection.INITIALTIMEOUTMED);
    }

    public void changeETapMasterContrato(String undNeg, String mContrato150MCDC, int idTap) {
        typeConnection = TapConnectionType.TAP_ALTERAMC.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP + "/" + idTap + "/metadatas";

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        String parms = "?codUnNeg=" + undNeg;
        parms += "&contrato150MCDC=" + mContrato150MCDC;

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.GET(URL + parms, infoHeader, keyHeader);
    }

    public void getRelSaldoMC(String codUnd, String mDataDe, String mDataAte, String mCodMC) {
        typeConnection = TapConnectionType.TAP_REL_SALDO_MC.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAPSALDO;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        String param = "?codUnNeg=" + codUnd;
        param += "&codMasterContrato=" + mCodMC;
        param += "&dataDe=" + mDataDe;
        param += "&dataAte=" + mDataAte;

        //http://189.57.10.251:88/api/tapsaldo?codUnNeg=2400&codMasterContrato=-1&dataDe=32%2F11%2F2020&dataAte=32%2F11%2F2020
        //http://189.57.10.251:88/api/tap/tapsaldo?codUnNeg=2400&codMasterContrato=-1&dataDe=01%2F11%2F2020&dataAte=30%2F11%2F2020
        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.GET(URL + param, infoHeader, keyHeader);
    }

    public void saveETap(TapCabec tapCabec) {
        typeConnection = TapConnectionType.TAP_SAVE.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.PUT(URL, infoHeader, keyHeader, gson.toJson(tapCabec), Connection.INITIALTIMEOUTLARGE);
    }

    public void sendETap(TapCabec tapCabec, String tipo, String nivel, String resp, String obs, int idMotivo) {
        typeConnection = TapConnectionType.TAP_DENVIA.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP;

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        TapSend tapSend = new TapSend();
        tapSend.setResp(String.valueOf(resp));
        tapSend.setObs(obs);
        tapSend.setIdMotRep(idMotivo);
        if(!TextUtils.isNullOrEmpty(nivel)){
            tapSend.setNivel(Integer.parseInt(nivel));
        }
        tapSend.setTipo(tipo);

        String json = gson.toJson(tapSend);

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        String param =  "/" + tapCabec.getId() + "/enviar";

        connection.POST(URL + param, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);
    }

    public void getMC(String unidNeg) {
        typeConnection = TapConnectionType.TAP_MASTER_CONTRATO.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAPSALDO + "/" + "mastercontrado";

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        String param =  "?codUnNeg=" + unidNeg;

        connection.GET(URL + param, infoHeader, keyHeader);
    }

    public void insertItemETap(TapItem tapItem, TapCabec tapCabec) {
        typeConnection = TapConnectionType.TAP_INCLUIR_ITEM.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP + "/" + tapCabec.getId() + "/item";

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();


        String json = gson.toJson(tapItem);

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);
    }

    public void deleteItemETap(TapItem tapItem, TapCabec tapCabec) {
        typeConnection = TapConnectionType.TAP_DELETAR_ITEM.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context)+ CustomAPI.API_TAP + "/";
        URL += tapCabec.getId() + "/item/" + tapItem.getId();

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        connection.DELETE(URL, infoHeader, keyHeader, Connection.INITIALTIMEOUTLARGE);
    }



    public void sendLoteETap(ArrayList<Integer> codList) {
        typeConnection = TapConnectionType.TAP_LOTE.getValue();

        createConnection(context);

        String URL = JJSDK.getHost(context) + CustomAPI.API_TAP + "/aprovar";

        ArrayList<String> infoHeader = new ArrayList<>();
        ArrayList<String> keyHeader = new ArrayList<>();

        keyHeader.add("token");
        infoHeader.add(userInfo.getUserInfo(context).getToken());

        String json = gson.toJson(codList);

        connection.POST(URL, infoHeader, keyHeader, json, Connection.INITIALTIMEOUTLARGE);
    }

}
