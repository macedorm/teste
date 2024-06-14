package br.com.jjconsulting.mobile.jjlib.syncData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.jjlib.dao.BaseDao;
import br.com.jjconsulting.mobile.jjlib.dao.InfoTable;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.syncData.model.DicSyncParam;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterData;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterDataSync;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

/**
 * Created by jjconsulting on 11/12/18.
 */

public class SyncDataDao extends BaseDao {

    public SyncDataDao(Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public Date getLastDateSync(String userId) {
        Date date = null;
        SQLiteDatabase db = getDb();
        InfoTable tbInfo = getInfoTable(MasterData.class);
        if (tbInfo != null) {
            try {
                StringBuilder sqlQ = new StringBuilder();
                sqlQ.append("SELECT LAST_SYNC ");
                sqlQ.append("FROM TB_MASTERDATA_SYNC ");
                sqlQ.append("WHERE NAME IS NULL ");
                sqlQ.append("AND USERID = '");
                sqlQ.append(userId);
                sqlQ.append("' ");

                Cursor cursor = db.rawQuery(sqlQ.toString(), null);
                while (cursor.moveToNext()) {
                    if (!cursor.isNull(cursor.getColumnIndex("LAST_SYNC"))) {
                        String sDate = cursor.getString(cursor.getColumnIndex("LAST_SYNC"));
                        if (sDate.length() > 0) {
                            date = FormatUtils.toDate(sDate);
                        }
                    }
                }
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        }
        return date;
    }

    public List<MasterDataSync> getListTable() {
        List<MasterDataSync> list = new ArrayList<>();
        SQLiteDatabase db = getDb();

        try {
            getListTable(db);
        } catch (Exception e) {
            LogUser.log(Config.TAG, e.toString());
        }

        return list;
    }

    @SuppressLint("Range")
    public List<MasterDataSync> getListTable(SQLiteDatabase db) {
        List<MasterDataSync> list = new ArrayList<>();
        InfoTable tbInfo = getInfoTable(MasterData.class);
        if (tbInfo != null) {

            StringBuilder sqlQ = new StringBuilder();
            sqlQ.append("SELECT M.NAME, S.LAST_SYNC, S.USERID ");
            sqlQ.append("FROM TB_MASTERDATA M ");
            sqlQ.append("LEFT JOIN TB_MASTERDATA_SYNC S ");
            sqlQ.append("ON S.NAME = M.NAME ");

            Cursor cursor = db.rawQuery(sqlQ.toString(), null);
            while (cursor.moveToNext()) {
                MasterDataSync m = new MasterDataSync();
                m.setName(cursor.getString(cursor.getColumnIndex("NAME")));

                list.add(m);
            }
        }

        return list;
    }

    @SuppressLint("Range")
    public List<DicSyncParam> getListDateSync(String userId) {
        SQLiteDatabase db = getDb();
        List<DicSyncParam> list = new ArrayList<>();
        InfoTable tbInfo = getInfoTable(MasterData.class);
        if (tbInfo != null) {
            StringBuilder sqlQ = new StringBuilder();
            sqlQ.append("SELECT M.ELEMENTNAME, M.MODE, S.LAST_SYNC, S.USERID ");
            sqlQ.append("FROM TB_MASTERDATA M ");
            sqlQ.append("LEFT JOIN TB_MASTERDATA_SYNC S ");
            sqlQ.append("ON S.NAME = M.ELEMENTNAME ");
            sqlQ.append("AND S.USERID = '");
            sqlQ.append(userId);
            sqlQ.append("' WHERE M.MODE = '0' OR M.MODE ISNULL " );
            sqlQ.append(" ORDER BY ORDER_SYNC ");

            Cursor cursor = db.rawQuery(sqlQ.toString(), null);
            while (cursor.moveToNext()) {
                DicSyncParam m = new DicSyncParam();
                m.setName(cursor.getString(cursor.getColumnIndex("ELEMENTNAME")));

                if (!cursor.isNull(cursor.getColumnIndex("USERID"))) {
                    m.setUserId(cursor.getString(cursor.getColumnIndex("USERID")));
                }

                if (!cursor.isNull(cursor.getColumnIndex("LAST_SYNC"))) {
                    m.setLastSync(cursor.getString(cursor.getColumnIndex("LAST_SYNC")));
                }

                list.add(m);
            }
        }

        return list;
    }

    public void setDataSync(String userId, String elementName, String date) {
        if (userId == null || userId.length() == 0) {
            throw new IllegalArgumentException("userId");
        }

        if (elementName == null || elementName.length() == 0) {
            throw new IllegalArgumentException("elementName");
        }

        SQLiteDatabase db = getDb();
        StringBuilder sqlQ = new StringBuilder();
        sqlQ.append("INSERT OR REPLACE INTO TB_MASTERDATA_SYNC ");
        sqlQ.append("(NAME, USERID, LAST_SYNC) ");
        sqlQ.append("VALUES (?, ?, ?) ");

        Object[] args = {elementName, userId, date};
        db.execSQL(sqlQ.toString(), args);
    }

    public void setDataSync(String userId, String date) {
        if (userId == null || userId.length() == 0) {
            throw new IllegalArgumentException("userId");
        }

        SQLiteDatabase db = getDb();
        try {
            StringBuilder sqlQ = new StringBuilder();
            sqlQ.append("INSERT OR REPLACE INTO TB_MASTERDATA_SYNC ");
            sqlQ.append("(NAME, USERID, LAST_SYNC) ");
            sqlQ.append("VALUES (NULL, ?, ?) ");

            Object[] args = {userId, date};
            db.execSQL(sqlQ.toString(), args);

        } catch (Exception e) {
            LogUser.log(Config.TAG, e.toString());
        }
    }
    
}
