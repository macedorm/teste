package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class StatusPedidoDao extends BaseDansalesDao {

    public StatusPedidoDao(Context context) {
        super(context);
    }

    public StatusPedido get(int codigoStatus) {
        String whereClause = "where sta.STA_INT_ID = " + String.valueOf(codigoStatus);
        String orderBy = "order by sta.STA_INT_ORDEM";
        ArrayList<StatusPedido> statuses = query(whereClause, null, orderBy);
        return statuses.isEmpty() ? null : statuses.get(0);
    }

    public ArrayList<StatusPedido> getAll() {
        String orderBy = "order by sta.STA_INT_ORDEM";
        return query(null, null, orderBy);
    }

    private ArrayList<StatusPedido> query(String whereClause, String[] args, String orderBy) {
        ArrayList<StatusPedido> statuses = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("select sta.STA_INT_ID,");
        query.append(" sta.STA_TXT_NOME,");
        query.append(" sta.STA_TXT_COR");

        query.append(" from TBORCAMENTOSTATUS sta");

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
                statuses.add(new StatusPedidoDao.StatusPedidoCursorWrapper(cursor)
                        .getStatusPedido());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return statuses;
    }

    public class StatusPedidoCursorWrapper extends CursorWrapper {

        public StatusPedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public StatusPedido getStatusPedido() {
            StatusPedido status = new StatusPedido();
            status.setCodigo(getInt(getColumnIndex("STA_INT_ID")));
            status.setNome(getString(getColumnIndex("STA_TXT_NOME")));
            status.setColor(getString(getColumnIndex("STA_TXT_COR")));

            return status;
        }
    }
}
