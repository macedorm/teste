package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.app.ProgressDialog;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncMessageConnection;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJSyncMessage {

    private ProgressDialog progressDialog;
    private OnFinish onFinish;

    public void syncMessage(Activity context, OnFinish onFinish){

        this.onFinish = onFinish;

        createDialogProgress(context);

        Date date = new Date();

        MessageDao messageDao = new MessageDao(context);
        ArrayList<Integer> mensagensLidasSync = messageDao.getSyncMensagensLidas();

        SyncMessageConnection syncMessageConnection = new SyncMessageConnection(context, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                try {
                    for (Integer message : mensagensLidasSync) {
                        messageDao.updateSync(String.valueOf(message), date);
                    }

                    setFinish();
                }catch (Exception ex){
                    LogUser.log(ex.toString());
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                setFinish();
            }
        });

        if(mensagensLidasSync.size() > 0){
            syncMessageConnection.syncMessage(mensagensLidasSync, date);
        } else {
            setFinish();
        }


    }

    private void setFinish(){
        if(onFinish != null){
            onFinish.onFinish();
        }
    }

    private void showProgressDialog(Activity context, boolean isShow) {
        try {

            if (!context.isFinishing()) {
                if (context.getWindow().getDecorView().isShown()) {
                    if (!isShow && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    } else {
                        progressDialog.show();
                    }
                }
            }
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

    }

    private void createDialogProgress(Activity context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getString(R.string.aguarde_sync_rg));
    }

    public interface OnFinish{
         void onFinish();
    }
}
