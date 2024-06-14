package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedido;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoDetail;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotasItem;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioCarteiraPedidosDetailDao extends BaseDansalesDao {

    public RelatorioCarteiraPedidosDetailDao(Context context) {
       super(context);
    }

    public ArrayList<RelatorioCarteiraPedidoDetail> findAll(String codigoUnidadeNegocio, String numeroPedido, int indexOffSet) {

        long start = System.currentTimeMillis();

        StringBuilder query = new StringBuilder();

        query.append("SELECT  ");
        query.append("ped.cSku, ");
        query.append("pro.DESCRICAO cSkuDescription, ");
        query.append("ped.cOrderWeight, ");
        query.append("ped.cStatus ");
        query.append("FROM TB_DECARTEIRAPEDIDO ped ");
        query.append("INNER JOIN TB_DEPRODUTO pro ");
        query.append("  ON  pro.COD_SKU = ped.cSku ");
        query.append("  AND pro.DEL_FLAG = '0' ");
        query.append("  AND pro.COD_UNID_NEGOC = ? ");

        List<String> whereArgs = new ArrayList<>();
        String whereClause = "WHERE ped.cIDOrderLegacy = ? ";
        whereArgs.add(codigoUnidadeNegocio);
        whereArgs.add(numeroPedido);

        if (whereClause != null) {
            query.append(" " + whereClause);
        }

        if (indexOffSet != -1) {
            query.append(" " + "LIMIT " + Config.SIZE_PAGE + " OFFSET " + indexOffSet);
        }

        ArrayList<RelatorioCarteiraPedidoDetail> listRelatorioCarteiraPedidoDetail = new ArrayList<>();
        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RelatorioCarteiraPedidoDetail item = new RelatorioCarteiraPedidoCursorWrapper(cursor).get();
                listRelatorioCarteiraPedidoDetail.add(item);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return listRelatorioCarteiraPedidoDetail;
    }


    public class RelatorioCarteiraPedidoCursorWrapper extends CursorWrapper {

        public RelatorioCarteiraPedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public RelatorioCarteiraPedidoDetail get() {
            RelatorioCarteiraPedidoDetail item = new RelatorioCarteiraPedidoDetail();
            item.setNome(getString(getColumnIndex("cSkuDescription")));
            item.setCodigo(getString(getColumnIndex("cSku")));
            item.setPeso(getString(getColumnIndex("cOrderWeight")));
            item.setStatus(getString(getColumnIndex("cStatus")));

            return item;
        }
    }
}
