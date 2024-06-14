package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Familia;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class FamiliaDao extends BaseDansalesDao {

    public FamiliaDao(Context context) {
        super(context);
    }

    public ArrayList<Familia> getAll(String codUnNeg) {

        StringBuilder sWhere = new StringBuilder();

        sWhere.append(" left join TB_DEPRODUTO as prod");
        sWhere.append(" on fam.COD_FAMILIA = prod.COD_FAMILIA");
        sWhere.append(" where");
        sWhere.append(" prod.COD_UNID_NEGOC = ?");

        String[] whereArgs = {codUnNeg};

        return query(sWhere.toString(), whereArgs, null);
    }

    private ArrayList<Familia> query(String whereClause, String[] args, String orderBy) {
        ArrayList<Familia> familias = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT");
        query.append(" DISTINCT(fam.COD_FAMILIA),");
        query.append(" fam.DSC_FAMILIA");
        query.append(" from TB_DEFAMILIA as fam ");

        if (whereClause != null) {
            query.append(whereClause);
        }

        if (orderBy != null) {
            query.append(" ");
            query.append(orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                familias.add(new FamiliaCursorWrapper(cursor).getFamilia());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return familias;
    }

    public String getFamilia(int codFamilia) {
        ArrayList<Familia> familias = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT");
        query.append(" fam.COD_FAMILIA,");
        query.append(" fam.DSC_FAMILIA");
        query.append(" from TB_DEFAMILIA as fam ");
        query.append(" WHERE fam.COD_FAMILIA = ? ");

        String[] whereArgs = {String.valueOf(codFamilia)};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs)) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("DSC_FAMILIA"));
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);

            return "";
        }
    }

    public class FamiliaCursorWrapper extends CursorWrapper {

        public FamiliaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Familia getFamilia() {
            Familia fam = new Familia();
            fam.setCodigo(getString(getColumnIndex("COD_FAMILIA")));
            fam.setNome(getString(getColumnIndex("DSC_FAMILIA")));

            return fam;
        }
    }
}
