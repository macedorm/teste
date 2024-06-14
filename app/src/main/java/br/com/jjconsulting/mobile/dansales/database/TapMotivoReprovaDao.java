package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.TapMotivoReprova;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapMotivoReprovaDao extends BaseDansalesDao {

    public TapMotivoReprovaDao(Context context) {
        super(context);
    }

    public ArrayList<TapMotivoReprova> get(String codigoUnidadeNegocio) {
        ArrayList<TapMotivoReprova> tapMotivoReprovaArrayList = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TBETAPCADMOTIVREPR CRP ");
        query.append("WHERE MRP_TXT_UNID_NEGOC = ? AND");
        query.append(" MRP_TXT_ATIVO = 'S'");

        String[] args = {codigoUnidadeNegocio};
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tapMotivoReprovaArrayList.add(new TapMotivoReprovaCursorWrapper(cursor).getTapMotivoReprova());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return tapMotivoReprovaArrayList;
    }

    public class TapMotivoReprovaCursorWrapper extends CursorWrapper {

        public TapMotivoReprovaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public TapMotivoReprova getTapMotivoReprova() {
            TapMotivoReprova tapMotivoReprova = new TapMotivoReprova();
            tapMotivoReprova.setId(getInt(getColumnIndex("MRP_INT_ID")));
            tapMotivoReprova.setDescricao(getString(getColumnIndex("MRP_TXT_DESCRICAO")));

            return tapMotivoReprova;
        }
    }
}
