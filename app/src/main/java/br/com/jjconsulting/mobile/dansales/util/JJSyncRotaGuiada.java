package br.com.jjconsulting.mobile.dansales.util;

import android.app.Activity;
import android.app.ProgressDialog;


import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncRotaGuiadaConnection;
import br.com.jjconsulting.mobile.dansales.database.PlanejamentoRotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaActionDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaTarefaDao;
import br.com.jjconsulting.mobile.dansales.model.RotaAcao;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class JJSyncRotaGuiada {

    private OnFinish onFinish;

    public void syncRotaGuiada(Activity context, OnFinish onFinish){

        this.onFinish = onFinish;

        Date date = new Date();

        if (context == null) {
            return;
        }

        RotaGuiadaDao mRotaGuiadaDao = new RotaGuiadaDao(context);
        RotaGuiadaTarefaDao mRotaGuiadaTarefaDao = new RotaGuiadaTarefaDao(context);
        RotaGuiadaActionDao rotaGuiadaActionDao = new RotaGuiadaActionDao(context);
        PlanejamentoRotaGuiadaDao planejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(context);

        String unNeg = Current.getInstance(context).getUnidadeNegocio().getCodigo();
        ArrayList<Rotas> rotasSync = mRotaGuiadaDao.getRouteSync(unNeg);
        ArrayList<RotaTarefas> rotaTarefasSync = mRotaGuiadaTarefaDao.getTaskSync();
        ArrayList<RotaAcao> rotaAcaoSync = rotaGuiadaActionDao.getSyncAcao();
        ArrayList<HashMap<String, Object>> planejamentoSync = planejamentoRotaGuiadaDao.getSyncPlanejamentoRota();

        SyncRotaGuiadaConnection syncRotaGuiadaConnection = new SyncRotaGuiadaConnection(context, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                try {

                    LogUser.log("TB_ROTAGUIADA - onSucess: " + response);

                    Gson gson = new Gson();
                    ArrayList<ValidationLetter> validationLetters = gson.fromJson(response, new TypeToken<List<ValidationLetter>>(){}.getType());

                    if (validationLetters == null || validationLetters.size() > 0) {
                        if(validationLetters.get(0).getStatus() == Connection.CREATED || validationLetters.get(0).getStatus() == Connection.SUCCESS){
                            for (Rotas rota : rotasSync) {
                                mRotaGuiadaDao.updateSync(rota, date);
                            }
                        }
                    }
                }catch (Exception ex){
                    LogUser.log(ex.getMessage());
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                LogUser.log("TB_ROTAGUIADA - onError: " + response);
            }
        });

        SyncRotaGuiadaConnection syncRotaGuiadaTarefaConnection = new SyncRotaGuiadaConnection(context, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                try {
                    LogUser.log("TB_ROTAGUIADA_TAREFA - onSucess: " + response);

                    Gson gson = new Gson();
                    ArrayList<ValidationLetter> validationLetters = gson.fromJson(response, new TypeToken<List<ValidationLetter>>(){}.getType());
                    if (validationLetters == null || validationLetters.size() > 0) {
                        if (validationLetters.get(0).getStatus() == Connection.CREATED || validationLetters.get(0).getStatus() == Connection.SUCCESS) {
                            for (RotaTarefas tarefa : rotaTarefasSync) {
                                mRotaGuiadaTarefaDao.updateSync(tarefa, date);
                            }
                        }
                    }
                }catch (Exception ex){
                    LogUser.log(ex.getMessage());
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                LogUser.log("TB_ROTAGUIADA_TAREFA - onError: " +response);
            }
        });

        SyncRotaGuiadaConnection syncRotaGuiadaAcaoConnection = new SyncRotaGuiadaConnection(context, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                try {
                    LogUser.log("TB_ROTAGUIADA_ACAO - onSucess: " + response);

                    Gson gson = new Gson();
                    ArrayList<ValidationLetter> validationLetters = gson.fromJson(response, new TypeToken<List<ValidationLetter>>(){}.getType());

                    if (validationLetters == null || validationLetters.size() > 0) {
                        if (validationLetters.get(0).getStatus() == Connection.CREATED || validationLetters.get(0).getStatus() == Connection.SUCCESS) {
                            for (RotaAcao acao : rotaAcaoSync) {
                                rotaGuiadaActionDao.updateSync(acao, date);
                            }
                        }
                    }

                }catch (Exception ex){
                    LogUser.log(ex.getMessage());
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                LogUser.log("TB_ROTAGUIADA_ACAO - onError: " + response);
            }
        });

        SyncRotaGuiadaConnection syncPlanejamentoRotaConnection = new SyncRotaGuiadaConnection(context, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                try {
                    LogUser.log("TB_ROTAGUIADA_PLANEJAMENTO_ROTA - onSucess: " + response);

                    Gson gson = new Gson();
                    ArrayList<ValidationLetter> validationLetters = gson.fromJson(response, new TypeToken<List<ValidationLetter>>(){}.getType());

                    if (validationLetters == null || validationLetters.size() > 0) {
                        if (validationLetters.get(0).getStatus() == Connection.CREATED || validationLetters.get(0).getStatus() == Connection.SUCCESS) {
                            for (HashMap<String, Object> plan : planejamentoSync) {
                               planejamentoRotaGuiadaDao.updateSync(plan.get("PLR_ID").toString(), date);
                            }
                        }
                    }
                }catch (Exception ex){
                    LogUser.log(ex.getMessage());
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                LogUser.log("TB_ROTAGUIADA_PLANEJAMENTO_ROTA - onError: " + response);
            }
        });

        if(rotasSync.size() > 0){
            syncRotaGuiadaConnection.syncRotas(rotasSync);
        }

        if(rotaTarefasSync.size() > 0){
            syncRotaGuiadaTarefaConnection.syncTarefas(rotaTarefasSync);
        }

        if(rotaAcaoSync.size() > 0){
            syncRotaGuiadaAcaoConnection.syncAcoes(rotaAcaoSync);
        }

        if(planejamentoSync.size() > 0){
            syncPlanejamentoRotaConnection.syncPlanejamento(planejamentoSync);
        }

        setFinish();
    }

    private void setFinish(){
        if(onFinish != null){
            onFinish.onFinish();
        }
    }

    public interface OnFinish{
         void onFinish();
    }
}
