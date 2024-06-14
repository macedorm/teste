package br.com.jjconsulting.mobile.dansales.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.BuildConfig;
import br.com.jjconsulting.mobile.dansales.database.ChatDao;
import br.com.jjconsulting.mobile.jjlib.dao.DataAccess;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataDao;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.syncData.model.ConfigUserSync;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterData;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterDataSync;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.SavePref;

public class RestoreDataStorage {

    private UserInfo userInfo;
    private Context context;
    private OnRestoreDatabase onRestoreDatabase;

    public RestoreDataStorage(Context activity) {
        context = activity;
        userInfo = new UserInfo();
    }

    /**
     * Limpa todos os dados armazenados pelo app.
     */
    public void startRestore(OnRestoreDatabase onRestoreDatabase) {
        this.onRestoreDatabase = onRestoreDatabase;
        Runnable task = () -> restore();
        new Thread(task).start();
    }

    private void restore() {
        restore(null);
    }

    public void restore(SQLiteDatabase db) {
        try {


            long start = System.currentTimeMillis();
            SavePref savePref = new SavePref();
            savePref.deleteSharedPreferences(Config.TAG_MESSAGE_CHAT_ID, Config.TAG, context);
            savePref.saveBoolSharedPreferences(Config.TAG_CACHE, Config.TAG, true, context);

            SyncDataDao dao = new SyncDataDao(context);
            if (db == null){
                db = dao.getDb();
            }

            DataAccess dataAccess = new DataAccess();

            dataAccess.dropAllUserTables(db);
            dao.createTable(db, MasterData.class);
            dao.createTable(db, MasterDataSync.class);

            ConfigUserSync configUserSync = new ConfigUserSync(BuildConfig.DATABASE_VERSION, BuildConfig.DATABASE_NAME);
            SyncDataManager syncDataManager = new SyncDataManager(context, configUserSync);
            syncDataManager.deleteDictionary(context);
            syncDataManager.deleteInfoSync(context);

            userInfo.deleteUserInfo(context);
            userInfo.deleteFirstLogin(context);

            if (onRestoreDatabase != null)
                onRestoreDatabase.onFinish(true);

            LogUser.log(Config.TAG, "Tempo total do restore: " +
                    (System.currentTimeMillis() - start));

        } catch (Exception ex) {
            ex.printStackTrace();

            if (onRestoreDatabase != null)
                onRestoreDatabase.onFinish(false);
        }
    }




    public interface OnRestoreDatabase {

        void onFinish(boolean isDeleted);
    }
}
