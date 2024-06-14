package br.com.jjconsulting.mobile.jjlib.syncData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnUpdateChangeScreen;
import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.CurrentProcess;
import br.com.jjconsulting.mobile.jjlib.dao.DataAccess;
import br.com.jjconsulting.mobile.jjlib.dao.DictionaryDao;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Element;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementInfo;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementDataItem;
import br.com.jjconsulting.mobile.jjlib.dao.entity.JsonFormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TBehavior;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TLoadingDataType;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRegSync;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.syncData.connection.SyncDataConnection;
import br.com.jjconsulting.mobile.jjlib.syncData.model.ConfigUserSync;
import br.com.jjconsulting.mobile.jjlib.syncData.model.DicSyncParam;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterData;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterDataSync;
import br.com.jjconsulting.mobile.jjlib.syncData.model.SyncInfo;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.SavePref;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class SyncDataManager {

    public static String SAVE_DICTIONARY_KEY = "save_dictionary";
    public static String SAVE_INFO_SYNC_KEY = "save_info_sync";

    private static final int COUNT_TIMEOUT_MAX = 5;
    private static boolean isProgress;

    private String SAVE_DICTIONARY;
    private String SAVE_INFO_SYNC;

    private OnUpdateChangeScreen onUpdateChangeScreen;

    private Gson gson;
    private List<DicSyncParam> listDate;
    private SyncInfo syncInfo;
    private SyncDataConnection syncDataConnection;
    private CurrentProcess currentProcess;
    private Factory dao;
    private SyncDataDao syncDatadao;
    private Context context;

    private int indexSyncInfo;
    private int currentPage;
    private int currentRow;
    private int totalRow;
    private int currentProgress;
    private int countTimeout;
    private int countNoConnection;

    private double indexProgress;

    private long startTime;
    private long startTimeTable;
    private long startTimeTeste;

    private boolean isCancel;

    private String url;
    private String token;
    private String idUser;
    private String version;

    private SavePref savePref;

    private int teste;

    /**
     * Construtor
     */
    public SyncDataManager(Context context, ConfigUserSync configUserSync, OnUpdateChangeScreen onUpdateChangeScreen) {
        this.context = context;

        this.onUpdateChangeScreen = onUpdateChangeScreen;
        this.url = configUserSync.getUrl();
        this.idUser = configUserSync.getIdUser();
        this.token = configUserSync.getToken();
        this.version = configUserSync.getVersion();

        savePref = new SavePref();
        gson = new Gson();
        dao = new Factory(context);
        syncDatadao = new SyncDataDao(context);

        countTimeout = 1;
        countNoConnection = 1;

        SAVE_DICTIONARY = SAVE_DICTIONARY_KEY +  context.getPackageName();
        SAVE_INFO_SYNC = SAVE_INFO_SYNC_KEY + context.getPackageName();

        createObjectConnection();
    }


    /**
     * Usado apenas para restore e delete
     * @param context
     * @param configUserSync
     */
    public SyncDataManager(Context context, ConfigUserSync configUserSync) {
        this.context = context.getApplicationContext();

        syncDatadao = new SyncDataDao(context);
        savePref = new SavePref();
        gson = new Gson();
        dao = new Factory(context);

        countTimeout = 1;
        countNoConnection = 1;

        SAVE_DICTIONARY = SAVE_DICTIONARY_KEY +  context.getPackageName();
        SAVE_INFO_SYNC = SAVE_INFO_SYNC_KEY + context.getPackageName();

        createObjectConnection();
    }

    /**
     * New page
     */
    private void newPage() {
        String dateUpdate = syncInfo.getServerDate();

        if (dateUpdate == null) {
            dateUpdate = FormatUtils.toDefaultDateFormat(new Date(startTimeTable));
        }

        syncDatadao.setDataSync(idUser, syncInfo.getListElement().get(indexSyncInfo).getName(), dateUpdate);
        indexSyncInfo++;
        currentPage = 1;
    }

    /**
     * start process update new page
     */
    private void startRequestSync() {
        if (!isCancel) {

            if (!isTotalPage()) {
                newPage();
            }

            if (currentPage == 1) {
                startTimeTable = System.currentTimeMillis();
            }

            if (indexSyncInfo < syncInfo.getListElement().size()) {
                LogUser.log(Config.TAG, String.format(Locale.getDefault(), "SyncRequest: table: %s - page %s - isTotalPage: %s ", syncInfo.getListElement().get(indexSyncInfo).getName(), currentPage, syncInfo.getListElement().get(indexSyncInfo).getTotalPage()));
                startTimeTeste = System.currentTimeMillis();
                syncDataConnection.syncRequest(SyncDataConnection.SYNCDATA, null, syncInfo.getListElement().get(indexSyncInfo), Integer.toString(currentPage));
            } else {
                long timeTotalUpdate = (System.currentTimeMillis() - startTime) / 1000;
                LogUser.log(Config.TAG, String.format(Locale.getDefault(), "JJVEL SYNC END - Tempo total %d seconds", timeTotalUpdate));

                String syncDate = syncInfo.getServerDate();

                if (syncDate == null) {
                    syncDate = FormatUtils.toDefaultDateFormat(new Date(startTimeTable));
                }

                syncDatadao.setDataSync(idUser, syncDate);

                saveInfoSync(context, idUser, syncDate);

                onUpdateChangeScreen.onFinish(currentRow, timeTotalUpdate);
            }
        } else {
            onUpdateChangeScreen.onCancel();
        }
    }

    /**
     * Connection Error
     */
    private void errorRequestSync(String message) {
        if (!isCancel) {
            onUpdateChangeScreen.onError(message);
        } else {
            onUpdateChangeScreen.onCancel();
        }
    }

    private void sucessRequestSync(InputStreamReader reader) {

        if (isCancel){
            onUpdateChangeScreen.onCancel();
            return;
        }

        try {

            long start = System.currentTimeMillis();
            Element element = dao.getElement(syncInfo.getListElement().get(indexSyncInfo).getName());

            String error;
            if(reader == null){
                error = "";
            } else {

                boolean isRegSync = false;
                if(element.getFields().get(element.getFields().size() - 1).getFieldname().equals(Config.REGSYC)){
                    isRegSync = true;
                }

                error = dao.persistDataElement(element, reader, true, currentProcess, isRegSync);
            }

            if (error.length() == 0) {
                long finish = System.currentTimeMillis();
                long total = finish - start;

                LogUser.log(Config.TAG, String.format(Locale.getDefault(), "SYNC TABLE %s OK - Page: %d - Total %d - Tempo Total %d", syncInfo.getListElement().get(indexSyncInfo).getName(), currentPage, syncInfo.getListElement().get(indexSyncInfo).getTotalPage(), total));
                currentPage++;
                startRequestSync();

            } else {
                onUpdateChangeScreen.onError(syncInfo.getListElement().get(indexSyncInfo).getName() + ": " + error);
            }

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "SYNC ERROR TABLE: " + ex.toString());
            onUpdateChangeScreen.onError(syncInfo.getListElement().get(indexSyncInfo).getName() + ": " + ex.toString());
        }
    }

    public boolean isTotalPage() {
        boolean isTotal = true;

        if (currentPage > syncInfo.getListElement().get(indexSyncInfo).getSize()) {
            isTotal = false;
        }

        return isTotal;
    }

    /**
     * Create connection
     */
    private void createObjectConnection() {
        syncDataConnection = new SyncDataConnection(context, new SyncDataConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader) {

                switch (typeConnection) {
                    case SyncDataConnection.SYNCMASTERDATA:
                        LogUser.log("JJVEL - SYNCMASTERDATA - CONEXAO - TOTAL: " + (System.currentTimeMillis() - startTimeTeste));
                        createDictionaryTables(response);
                        break;
                    case SyncDataConnection.SYNCAVAILABLEUPDATE:
                        LogUser.log("JJVEL - SYNCAVAILABLEUPDATE - CONEXAO: " + (System.currentTimeMillis() - startTimeTeste));
                        try {

                            long startTimeUp = System.currentTimeMillis();
                            syncInfo = gson.fromJson(response, SyncInfo.class);

                            LogUser.log("JJVEL - SYNCAVAILABLEUPDATE - PARSER/INSERT: " + (System.currentTimeMillis() - startTimeUp) );

                            startProcessUpdate();
                            currentProcess.setProgress(0);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            onUpdateChangeScreen.onErrorConnection();
                        }

                        break;
                    case SyncDataConnection.SYNCDATA:
                        countTimeout = 1;
                        countNoConnection = 1;

                        sucessRequestSync(reader);
                        break;
                }
            }

            @Override
            public void onError(int code, VolleyError volleyError, int typeConnection, String response) {
                try {
                    /*if (volleyError != null && volleyError.networkResponse != null && volleyError.networkResponse.statusCode == Connection.NOT_REGISTER) {
                        currentPage = syncInfo.getListElement().get(indexSyncInfo).getSize() + 1;
                        startRequestSync();
                        return;
                    }*/

                    ValidationLetter errorConnection = new ValidationLetter();

                    if (!TextUtils.isNullOrEmpty(response)) {
                        errorConnection = gson.fromJson(response, ValidationLetter.class);
                    }

                    if (code == Connection.AUTH_FAILURE) {
                        errorRequestSync(errorConnection.getMessage());
                    } else {
                        switch (typeConnection) {
                            case SyncDataConnection.SYNCMASTERDATA:
                            case SyncDataConnection.SYNCAVAILABLEUPDATE:
                                if (code == Connection.NO_CONNECTION) {
                                    errorRequestSync(context.getString(R.string.title_connection_error));
                                } else {

                                    if (errorConnection != null && errorConnection.getMessage() != null)
                                        errorRequestSync(errorConnection.getMessage());
                                    else
                                        errorRequestSync(context.getString(R.string.title_connection_error));
                                }
                                break;
                            case SyncDataConnection.SYNCDATA:
                                if (errorConnection != null && errorConnection.getMessage() != null || Connection.PARSE_ERROR == code) {
                                    LogUser.log(errorConnection.getMessage());
                                    errorRequestSync(errorConnection.getMessage());
                                } else if (Connection.CANCEL == code || isCancel) {
                                    onUpdateChangeScreen.onCancel();
                                } else if (Connection.TIMEOUT == code && countTimeout <= COUNT_TIMEOUT_MAX) {
                                    onUpdateChangeScreen.onProgressStatus(context.getString(R.string.timeout_sync, String.valueOf(countTimeout), String.valueOf(COUNT_TIMEOUT_MAX)));
                                    syncDataConnection.syncRequest(SyncDataConnection.SYNCDATA, null, syncInfo.getListElement().get(indexSyncInfo), Integer.toString(currentPage));
                                    countTimeout++;
                                } else if (Connection.NO_CONNECTION == code && countNoConnection <= COUNT_TIMEOUT_MAX) {
                                    onUpdateChangeScreen.onProgressStatus(context.getString(R.string.noconnection_sync, String.valueOf(countNoConnection), String.valueOf(COUNT_TIMEOUT_MAX)));

                                    Timer timer = new Timer();
                                    TimerTask timerTask = new TimerTask() {
                                        @Override
                                        public void run() {
                                            if (syncDataConnection != null) {
                                                syncDataConnection.syncRequest(SyncDataConnection.SYNCDATA, null, syncInfo.getListElement().get(indexSyncInfo), Integer.toString(currentPage));
                                                countNoConnection++;
                                            }
                                        }
                                    };
                                    timer.schedule(timerTask, 4000);
                                } else {
                                    errorRequestSync(context.getString(R.string.title_connection_error));
                                }
                                break;
                        }
                    }
                }catch (Exception ex){
                    errorRequestSync(context.getString(R.string.title_connection_error));
                }
            }
        }, url, token, version);
    }

    private void createDictionaryTables(String json){
        try {
            long startTime = System.currentTimeMillis();
            JsonFormElement[] syncDictionaries = gson.fromJson(json, JsonFormElement[].class);
            LogUser.log("JJVEL - SYNCMASTERDATA - PARSER/json: " + (System.currentTimeMillis() - startTime));

            List<Element> tables = new ArrayList<>();
            for (JsonFormElement e : syncDictionaries) {

                if(TLoadingDataType.fromInteger(e.getMode()) == TLoadingDataType.ONLINE)
                    continue;

                //Insert custom field
                ElementField elementField =  new ElementField();
                elementField.setDatatype(TField.INT);
                elementField.setDatatype(TRegSync.INSERT.getValue());
                elementField.setFieldname(Config.REGSYC);
                elementField.setIspk(false);
                elementField.setIsrequired(false);
                elementField.setDatabehavior(TBehavior.REAL.getValue());
                elementField.setDataItem(new FormElementDataItem());
                elementField.setAutonum(false);
                e.getFields().add(elementField);

                tables.add(e);
            }

            startTime = System.currentTimeMillis();
            dao.createTable(tables);
            LogUser.log("JJVEL - SYNCMASTERDATA - CREATE TABLES - TOTAL: " + (System.currentTimeMillis() - startTime));

            startTime = System.currentTimeMillis();
            DictionaryDao dicDao = new DictionaryDao(context);
            dicDao.setDictionary(dao.getDb(), syncDictionaries);
            LogUser.log("JJVEL - SYNCMASTERDATA - SAVE JSON - TOTAL: " + (System.currentTimeMillis() - startTime));

            setDictionarySave(context);
            LogUser.log("JJVEL - SYNCMASTERDATA - INSERT DIC - TOTAL: " + (System.currentTimeMillis() - startTime));

            syncUpdateAvailable();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "Create Dictionary Error: " + ex.toString());
            errorRequestSync(ex.getMessage());
        }
    }


    /**
     * Start update database
     */
    private void startProcessUpdate() {

        onUpdateChangeScreen.onStart();

        currentPage = 1;

        if(startTime == 0){
            startTime = System.currentTimeMillis();
        }

        currentProcess = new CurrentProcess();

        currentProcess.setOnInsertProgress(size -> {
            currentRow += size;

            teste += size;

            currentProgress = (int) (currentRow / indexProgress);
            if (currentProgress > 100) {
                currentProgress = 100;
            }

            if(currentRow > totalRow){
                currentRow = totalRow;
            }

            if (!isCancel) {
                onUpdateChangeScreen.onProgress(totalRow, currentRow, currentProgress);
            }
        });

        if (syncInfo != null) {

            String syncDate = syncInfo.getServerDate();

            if (syncDate == null) {
                syncDate = FormatUtils.toDefaultDateFormat(new Date(startTimeTable));
            }

            isCancel = false;

            List<SyncInfo.SyncInfoElement> syncInfoTemp = new ArrayList<>();

            for (SyncInfo.SyncInfoElement si : syncInfo.getListElement()) {

                if (si.getRecordSize() > 0) {
                    int size = si.getRecordSize() / Config.PAGE_SYNC;// si.getTotPerPage();
                    size++;

                    si.setTotalPage(size);

                    totalRow += si.getRecordSize();

                    si.setSize(size);

                    for (DicSyncParam master : listDate) {
                        if (si.getName().equals(master.getName())) {
                            if (master.getLastSync() != null) {
                                si.setLastSync(master.getLastSync());
                            }
                            break;
                        }
                    }
                    syncInfoTemp.add(si);
                } else {
                    syncDatadao.setDataSync(idUser, si.getName(), syncDate);
                }
            }

            syncInfo.setListElement(syncInfoTemp);

            if (syncInfo.getListElement().size() > 0) {
                indexProgress = totalRow / 100;
                startRequestSync();
            } else {
                syncDatadao.setDataSync(idUser, syncDate);
                saveInfoSync(context, idUser, syncDate);
                onUpdateChangeScreen.onUpdateNotAvailabe();
            }

        } else {
            onUpdateChangeScreen.onUpdateNotAvailabe();
        }
    }

    public void syncMasterDate() {
        startTimeTeste = System.currentTimeMillis();
        syncDataConnection.syncRequest(SyncDataConnection.SYNCMASTERDATA);
    }

    /**
     * Get Update available
     */
    private void syncUpdateAvailable() {

        try {
            long start = System.currentTimeMillis();

            listDate = syncDatadao.getListDateSync(idUser);
            String json = gson.toJson(listDate);

            LogUser.log("JJVEL - SYNCAVAILABLEUPDATE - CRIA JSON ENVIO - TOTAL: " + (System.currentTimeMillis() - start) );

            syncDataConnection.syncRequest(SyncDataConnection.SYNCAVAILABLEUPDATE, json);
        } catch (Exception ex) {
            ex.printStackTrace();

            if (onUpdateChangeScreen != null) {
                onUpdateChangeScreen.onErrorConnection();
            }
        }
    }

    /**
     * Get Dictionary
     */
    public void connectionStart() {
        onUpdateChangeScreen.onPreparation();
        onUpdateChangeScreen.onProgressStatus(context.getString(R.string.loading_sync));

        if (!isDictionarySave(context)) {
            LogUser.log(Config.TAG, "Dictionary not exists");
            syncMasterDate();
        } else {
            LogUser.log(Config.TAG, "Dictionary exists");
            syncUpdateAvailable();
        }
    }

    /**
     * Cancel sync
     */
    public void setCancel(boolean cancel) {
        isCancel = cancel;

        if (isCancel) {
            onUpdateChangeScreen.onProgressStatus(context.getString(R.string.cancel_sync));
        }

        try {
            if (syncDataConnection != null && syncDataConnection.connection != null)
                syncDataConnection.connection.cancelRequest();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isCancel() {
        return isCancel;
    }

    private void setDictionarySave(Context context) {
        savePref.saveSharedPreferences(SAVE_DICTIONARY, context.getPackageName(), "SAVE", context);
    }

    private boolean isDictionarySave(Context context) {
        boolean isDictionary = false;
        String dictionarySave = savePref.getPref(SAVE_DICTIONARY, context.getPackageName(), context);
        if (dictionarySave != null && dictionarySave.length() > 0) {
            isDictionary = true;
        }
        return isDictionary;

    }

    /**
     * Delete Dictionary
     */
    public void deleteDictionary(Context context) {
        savePref.deleteSharedPreferences(SAVE_DICTIONARY, context.getPackageName(), context);
    }


    public void saveInfoSync(Context context, String nome, String lastSync) {
        savePref.saveSharedPreferences(SAVE_INFO_SYNC, context.getPackageName(), String.format("%s | %s", nome, lastSync), context);
    }

    public String loadInfoSync(Context context) {
        String info = savePref.getPref(SAVE_INFO_SYNC, context.getPackageName(), context);

        if (info == null || info.length() == 0) {
            return "|";
        } else {
            return info;
        }
    }

    public void deleteInfoSync(Context context) {
        savePref.deleteSharedPreferences(SAVE_INFO_SYNC, context.getPackageName(), context);
    }

    //TODO: Metodo repetido em RestoreDataStorage
    public static void restore(SQLiteDatabase sqLiteDatabase, Context context, String name, int version) {
        Factory dao = new Factory(context);

        if (sqLiteDatabase != null) {

            DataAccess dataAccess = new DataAccess();

            dataAccess.dropAllUserTables(sqLiteDatabase);
            dao.createTable(sqLiteDatabase, MasterData.class);
            dao.createTable(sqLiteDatabase, MasterDataSync.class);

            ConfigUserSync configUserSync = new ConfigUserSync();
            configUserSync.setDatabaseVersion(version);
            configUserSync.setDatabaseName(name);

           SyncDataManager syncDataManager = new SyncDataManager(context, configUserSync);
           syncDataManager.deleteDictionary(context);
           syncDataManager.deleteInfoSync(context);
        }
    }

    public static boolean isProgress() {
        return isProgress;
    }

    public static void setIsProgress(boolean isProgress) {
        SyncDataManager.isProgress = isProgress;
    }
}
