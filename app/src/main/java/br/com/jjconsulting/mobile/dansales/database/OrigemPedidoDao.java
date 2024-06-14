package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.OrigemPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class OrigemPedidoDao extends BaseDansalesDao {


    public OrigemPedidoDao(Context context) {
        super(context);
    }

    public OrigemPedido get(int codigoOrigemPedido) {
        String whereClause = " and ORI_INT_CODIGO = " + String.valueOf(codigoOrigemPedido);
        ArrayList<OrigemPedido> origens = query(whereClause, null, null);
        return origens.isEmpty() ? null : origens.get(0);
    }

    private ArrayList<OrigemPedido> query(String whereClause, String[] args, String orderBy) {
        ArrayList<OrigemPedido> origens = new ArrayList<>();

        String query = "select ORI_INT_CODIGO, ORI_TXT_DESCRICAO" +
                " from TBORCAMENTOORIGEM" +
                " where 1 = 1";

        if (whereClause != null) {
            query += " " + whereClause;
        }

        if (orderBy != null) {
            query += " " + orderBy;
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                origens.add(new OrigemPedidoDao.OrigemPedidoCursorWrapper(cursor)
                        .getOrigemPedido());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return origens;
    }

    public class OrigemPedidoCursorWrapper extends CursorWrapper {

        public OrigemPedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public OrigemPedido getOrigemPedido() {
            OrigemPedido origemPedido = new OrigemPedido();
            origemPedido.setCodigo(getInt(getColumnIndex("ORI_INT_CODIGO")));
            origemPedido.setNome(getString(getColumnIndex("ORI_TXT_DESCRICAO")));

            return origemPedido;
        }
    }
}
