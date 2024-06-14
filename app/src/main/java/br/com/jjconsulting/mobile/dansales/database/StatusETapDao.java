package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.TapStatus;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class StatusETapDao extends BaseDansalesDao{


    public StatusETapDao(Context context) {
        super(context);
    }

    public TapStatus get(int codigoStatus) {
        String whereClause = "where sta.STA_INT_ID = " + String.valueOf(codigoStatus);
        String orderBy = "order by sta.STA_INT_ORDEM";
        ArrayList<TapStatus> statuses = query(whereClause, null, orderBy);
        return statuses.isEmpty() ? null : statuses.get(0);
    }

    public ArrayList<TapStatus> getAll() {
        String orderBy = "order by sta.STA_INT_ORDEM";
        return query(null, null, orderBy);
    }

    private ArrayList<TapStatus> query(String whereClause, String[] args, String orderBy) {
        ArrayList<TapStatus> statuses = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("select sta.STA_INT_ID,");
        query.append(" sta.STA_TXT_NOME");
        query.append(" from TBETAPCABECSTATUS sta");

        if (whereClause != null) {
            query.append(" " + whereClause);
        }

        if (orderBy != null) {
            query.append(" " + orderBy);
        }

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                statuses.add(new StatusETapDao.StatusETapCursorWrapper(cursor)
                        .getStatusETap());
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return statuses;
    }

    public class StatusETapCursorWrapper extends CursorWrapper {

        public StatusETapCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public TapStatus getStatusETap() {
            TapStatus status = new TapStatus();
            status.setCodigo(getInt(getColumnIndex("STA_INT_ID")));
            status.setNome(getString(getColumnIndex("STA_TXT_NOME")));

            return status;
        }
    }
}
