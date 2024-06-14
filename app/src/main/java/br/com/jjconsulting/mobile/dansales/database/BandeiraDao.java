package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class BandeiraDao extends BaseDansalesDao {

    public BandeiraDao(Context context) {
        super(context);
    }

    public Bandeira get(String codigoBandeira) {
        if (codigoBandeira == null)
            return null;

        String whereClause = "where ban.COD_BANDEIRA = ?";
        String[] whereArgs = { codigoBandeira };
        ArrayList<Bandeira> bandeiras = query(whereClause, whereArgs, null);
        return bandeiras.isEmpty() ? null : bandeiras.get(0);
    }

    public ArrayList<Bandeira> getAll(String codigoOrganizacao) {
        String whereClause = "where ban.COD_EMITENTE = ?" +
                " and ban.DEL_FLAG = '0'";
        String orderBy = "order by DESC_BANDEIRA";
        String[] whereArgs = { codigoOrganizacao };
        return query(whereClause, whereArgs, orderBy);
    }

    private ArrayList<Bandeira> query(String whereClause, String[] args, String orderBy) {
        ArrayList<Bandeira> bandeiras = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("select ban.COD_EMITENTE,");
        query.append(" ban.COD_BANDEIRA,");
        query.append(" ban.DESC_BANDEIRA");
        query.append(" from TB_DEBANDEIRA ban ");

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
                bandeiras.add(new BandeiraDao.BandeiraCursorWrapper(cursor).getBandeira());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return bandeiras;
    }

    public class BandeiraCursorWrapper extends CursorWrapper {

        public BandeiraCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Bandeira getBandeira() {
            Bandeira bandeira = new Bandeira();
            bandeira.setCodigoCliente(getString(getColumnIndex("COD_EMITENTE")));
            bandeira.setCodigoBandeira(getString(getColumnIndex("COD_BANDEIRA")));
            bandeira.setNomeBandeira(getString(getColumnIndex("DESC_BANDEIRA")));

            return bandeira;
        }
    }
}
