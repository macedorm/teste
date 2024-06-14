package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Grupo;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class GrupoDao extends BaseDansalesDao {

    public GrupoDao(Context context) {
        super(context);
    }

    public ArrayList<Grupo> getGrupo(String codigoUnidadeNegocio) {
        ArrayList<Grupo> grupos = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT GRP.*");
        query.append(" FROM PRODATIVO_WEBSALES_GRUPO as GRP");
        query.append(" INNER JOIN PRODATIVO_WEBSALES as PRO");
        query.append(" ON PRO.PRD_INT_IDGRUPO = GRP.PGR_INT_ID");
        query.append(" WHERE PRO.PRD_TXT_TPPED = 'VCO'");
        query.append(" AND PRO.PRD_TXT_UNID_NEGOC = '" + codigoUnidadeNegocio + "'");
        query.append(" ORDER BY 2");

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                grupos.add(new GrupoDao.GrupoCursorWrapper(cursor).getGrupo());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return grupos;
    }

    public class GrupoCursorWrapper extends CursorWrapper {

        public GrupoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Grupo getGrupo() {
            Grupo grupo = new Grupo();
            grupo.setCod(getString(getColumnIndex("PGR_INT_ID")));
            grupo.setNome(getString(getColumnIndex("PGR_TXT_DESC")));

            return grupo;
        }
    }
}
