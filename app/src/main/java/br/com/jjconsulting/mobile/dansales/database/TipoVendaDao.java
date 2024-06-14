package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TipoVendaDao extends BaseDansalesDao {

    public TipoVendaDao(Context context) {
        super(context);
    }

    public TipoVenda get(String codigoTipoVenda) {
        String whereClause = " and X5_CHAVE = ?";
        String[] whereArgs = { codigoTipoVenda };
        ArrayList<TipoVenda> tiposVenda = query(whereClause, whereArgs, null);
        return tiposVenda.isEmpty() ? null : tiposVenda.get(0);
    }

    private ArrayList<TipoVenda> query(String whereClause, String[] args, String orderBy) {
        ArrayList<TipoVenda> tiposVenda = new ArrayList<>();

        String query = "select X5_CHAVE, X5_DESCRI, X5_DESCP," +
                " X5_DESCESP, X5_HASQTDMIN, VLRMINORC," +
                " VLRMAXORC, HASATTACH" +
                " from TPVEND" +
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
                tiposVenda.add(new TipoVendaDao.TipoVendaCursorWrapper(cursor)
                        .getTipoVenda());
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return tiposVenda;
    }

    public class TipoVendaCursorWrapper extends CursorWrapper {

        public TipoVendaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public TipoVenda getTipoVenda() {
            TipoVenda tipoVenda = new TipoVenda();
            tipoVenda.setCodigo(getString(getColumnIndex("X5_CHAVE")));
            tipoVenda.setNome(getString(getColumnIndex("X5_DESCRI")));
            tipoVenda.setEdicaoPercentualHabilitada("1".equals(getString(getColumnIndex(
                    "X5_DESCP"))));
            tipoVenda.setEdicaoObservacaoHabilitada("1".equals(getString(getColumnIndex(
                    "X5_DESCESP"))));
            tipoVenda.setValidacaoQtdMinimaHabilitada("1".equals(getString(getColumnIndex(
                    "X5_HASQTDMIN"))));
            tipoVenda.setPermissaoUsoAnexoHabilitado("1".equals(getString(getColumnIndex(
                    "HASATTACH"))));

            if (!isNull(getColumnIndex("VLRMINORC"))) {
                BigDecimal valorMinimo = new BigDecimal(getDouble(getColumnIndex(
                        "VLRMINORC")));
                tipoVenda.setValorMinimo(valorMinimo);
            }

            if (!isNull(getColumnIndex("VLRMAXORC"))) {
                BigDecimal valorMaximo = new BigDecimal(getDouble(getColumnIndex(
                        "VLRMAXORC")));
                tipoVenda.setValorMaximo(valorMaximo);
            }

            return tipoVenda;
        }
    }
}
