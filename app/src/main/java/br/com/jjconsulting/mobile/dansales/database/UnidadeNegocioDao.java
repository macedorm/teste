package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class UnidadeNegocioDao extends BaseDansalesDao{

    public UnidadeNegocioDao(Context context) {
        super(context);
    }

    public UnidadeNegocio get(String codigoUsuario, String codigoUnidadeNegocio) {
        String whereClause = "where unn.COD_REG_FUNC = ?"
                + " and unn.COD_UNID_NEGOC = ? AND unn.DEL_FLAG <> '1'";
        String[] whereArgs = { codigoUsuario, codigoUnidadeNegocio };
        ArrayList<UnidadeNegocio> unidades = query(whereClause, whereArgs, null);
        return unidades.isEmpty() ? null : unidades.get(0);
    }

    public ArrayList<UnidadeNegocio> getAll(String codigoUsuario) {
        String whereClause = " where unn.COD_REG_FUNC = ? AND unn.DEL_FLAG <> '1'";
        String[] whereArgs = { codigoUsuario };
        return query(whereClause, whereArgs, null);
    }

    private ArrayList<UnidadeNegocio> query(String whereClause, String[] args, String orderBy) {
        ArrayList<UnidadeNegocio> unidades = new ArrayList<>();

        String query = "select unn.COD_UNID_NEGOC, emp.EMP_TXT_NOMEEMP,"
                + " emp.EMP_TXT_NOMEFIL"
                + " from TB_DEREGISTROUN unn"
                + " inner join TBEMPFIL emp"
                + " on emp.EMP_TXT_COD_UN = unn.COD_UNID_NEGOC";

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
                unidades.add(new UnidadeNegocioDao.UnidadeNegocioCursorWrapper(cursor)
                        .getUnidadeNegocio());
                cursor.moveToNext();
            }

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        } finally {
            database.close();
        }

        return unidades;
    }

    public class UnidadeNegocioCursorWrapper extends CursorWrapper {

        public UnidadeNegocioCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public UnidadeNegocio getUnidadeNegocio() {
            UnidadeNegocio unidadeNegocio = new UnidadeNegocio();
            unidadeNegocio.setCodigo(getString(getColumnIndex("COD_UNID_NEGOC")));
            String empresaFilial = String.format("%1$s %2$s - %3$s",
                    getString(getColumnIndex("COD_UNID_NEGOC")),
                    getString(getColumnIndex("EMP_TXT_NOMEEMP")),
                    getString(getColumnIndex("EMP_TXT_NOMEFIL"))
            );
            unidadeNegocio.setNome(empresaFilial);

            return unidadeNegocio;
        }
    }
}
