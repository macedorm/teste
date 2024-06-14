package br.com.jjconsulting.mobile.jjlib.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import br.com.jjconsulting.mobile.jjlib.dao.DataAccess;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterData;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterDataSync;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.SavePref;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class WebSalesDatabaseHelper extends SQLiteOpenHelper {

    private static WebSalesDatabaseHelper sWebSalesDatabaseHelper;
    private Context context;
    private String name;
    private int version;

    private WebSalesDatabaseHelper(Context context, int version, String name) {
        super(context, name, null, version);
        this.name = name;
        this.version = version;
        this.context = context;
    }

    public static synchronized WebSalesDatabaseHelper getInstance(Context context, int version, String name) {
        if (sWebSalesDatabaseHelper == null) {
            sWebSalesDatabaseHelper = new WebSalesDatabaseHelper(context.getApplicationContext(), version, name);
            sWebSalesDatabaseHelper.setName(name);
            sWebSalesDatabaseHelper.setVersion(version);
        }

        try {
            if (TextUtils.isNullOrEmpty(name) || version == 0)
                throw new Exception("WebSalesDatabaseHelper not initialize");
            return sWebSalesDatabaseHelper;
        } catch (Exception ex) {
            LogUser.log(ex.toString());
            return null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            DataAccess dao = new DataAccess();
            dao.createTable(db, MasterData.class);
            dao.createTable(db, MasterDataSync.class);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        SyncDataManager.restore(sqLiteDatabase, context, name, version);
    }

    public static long getSizeDatabase(Context context, String name) {
        File f = context.getDatabasePath(name);
        long dbSize = f.length();
        return dbSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
