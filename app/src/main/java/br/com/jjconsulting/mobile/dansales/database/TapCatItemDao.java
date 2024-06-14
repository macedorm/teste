package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.TapProdCateg;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapCatItemDao extends BaseDansalesDao {

    public TapCatItemDao(Context context) {
        super(context);
    }

    public ArrayList<TapProdCateg> get(String codigoUnidadeNegocio) {
        ArrayList<TapProdCateg> tapCatItemArrayList = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TBETAPCADCATEGPROD CRP ");
        query.append("WHERE CPR_TXT_UNID_NEGOC = ? AND");
        query.append(" DEL_FLAG <> '1'");

        String[] args = {codigoUnidadeNegocio};
        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tapCatItemArrayList.add(new TapCatItemCursorWrapper(cursor).getTapCatItem());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return tapCatItemArrayList;
    }

    public class TapCatItemCursorWrapper extends CursorWrapper {

        public TapCatItemCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public TapProdCateg getTapCatItem() {
            TapProdCateg tapCatItem = new TapProdCateg();
            tapCatItem.setIdCategProd(getInt(getColumnIndex("CPR_INT_ID")));
            tapCatItem.setDescricao(getString(getColumnIndex("CPR_TXT_DESCRICAO")));

            return tapCatItem;
        }
    }
}
