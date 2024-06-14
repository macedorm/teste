package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.LogPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PedidoLogDao extends BaseDansalesDao{

    public PedidoLogDao(Context context) {
        super(context);
    }


    public ArrayList<LogPedido> getLogPedido(String codigoPedido) {
        StringBuilder sqlLogPedidos = new StringBuilder();
        sqlLogPedidos.append("SELECT OBS_TXT_VENDEDORNOME, OBS_TXT_DESCRICAO, OBS_DAT_CONTROLE ");
        sqlLogPedidos.append("FROM TBORCAMENTOOBS ");
        sqlLogPedidos.append("WHERE OBS_TXT_ORCID = ? ");
        sqlLogPedidos.append("ORDER BY obs_int_id DESC");

        String[] whereArgs = {codigoPedido};

        ArrayList<LogPedido> logPedidos = new ArrayList<>();

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sqlLogPedidos.toString(), whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                logPedidos.add(new PedidoLogDao.LogPedidoCursorWrapper(cursor).getLogPedido());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return logPedidos;
    }


    public class LogPedidoCursorWrapper extends CursorWrapper {

        public LogPedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public LogPedido getLogPedido() {
            LogPedido logPedido = new LogPedido();
            logPedido.setNome(getString(getColumnIndex("OBS_TXT_VENDEDORNOME")));
            logPedido.setDescricao(getString(getColumnIndex("OBS_TXT_DESCRICAO")));

            try {
                logPedido.setData(FormatUtils.toDate(
                        getString(getColumnIndex("OBS_DAT_CONTROLE"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getLogPedido: " + parseEx.toString());
            }

            return logPedido;
        }
    }
}
