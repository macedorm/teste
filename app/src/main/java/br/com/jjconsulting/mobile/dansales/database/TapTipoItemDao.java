package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.TapItem;
import br.com.jjconsulting.mobile.dansales.model.TapTipoInvest;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapTipoItemDao extends BaseDansalesDao {

    public TapTipoItemDao(Context context) {
        super(context);
    }

    public ArrayList<TapTipoInvest> get(String codigoUnidadeNegocio, List<TapItem> item) {
        ArrayList<TapTipoInvest> tapTipoItemArrayList = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT TIP.TPI_INT_ID,");
        query.append(" TIP.TPI_TXT_ATIVO, ");
        query.append(" TIP.TPI_TXT_VLEST, ");
        query.append(" TIP.TPI_TXT_DESCRICAO ");
        query.append("FROM TBETAPCADTIPOINVEST TIP ");
        query.append("WHERE TPI_TXT_UNID_NEGOC = ? AND ");

        if(item !=  null && item.size() > 0){
            if(item.get(0).isValorEstimado()){
                query.append(" TPI_TXT_VLEST = 'S' AND");
            } else {
                query.append(" TPI_TXT_VLEST = 'N' AND");
            }
        }

        query.append(" TPI_TXT_ATIVO = 'S' AND");
        query.append(" DEL_FLAG <> '1'");

        String[] args = {codigoUnidadeNegocio};
        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tapTipoItemArrayList.add(new TapTipoItemCursorWrapper(cursor).getTapTipoItem());
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return tapTipoItemArrayList;
    }

    public class TapTipoItemCursorWrapper extends CursorWrapper {

        public TapTipoItemCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public TapTipoInvest getTapTipoItem() {
            TapTipoInvest tapTipoItem = new TapTipoInvest();
            tapTipoItem.setIdTipoInvest(getInt(getColumnIndex("TPI_INT_ID")));
            tapTipoItem.setDescricao(getString(getColumnIndex("TPI_TXT_DESCRICAO")));
            tapTipoItem.setVlest(getString(getColumnIndex("TPI_TXT_VLEST")));

            return tapTipoItem;
        }
    }
}
