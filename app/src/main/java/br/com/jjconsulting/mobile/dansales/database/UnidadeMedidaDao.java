package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.UnidadeMedida;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class UnidadeMedidaDao extends BaseDansalesDao{

    public UnidadeMedidaDao(Context context) {
        super(context);
    }

    public ArrayList<UnidadeMedida> getAll() {
        return query(null, null, "ORDER BY DESCRI");
    }

    public UnidadeMedida getUnidadeMedida(String codigoUnidadeMedida) {
        UnidadeMedida unidadeMedida = new UnidadeMedida();

        String query = "select COD, DESCRI from UNMED where COD = ?";
        String selectionArgs[] = new String[]{codigoUnidadeMedida};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, selectionArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                unidadeMedida = new UnMedCursorWrapper(cursor).get();
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return unidadeMedida;
    }

    private ArrayList<UnidadeMedida> query(String whereClause, String[] args, String orderBy) {
        ArrayList<UnidadeMedida> list = new ArrayList<>();

        String query = "select COD, DESCRI from UNMED where 1 = 1 ";

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
                list.add(new UnMedCursorWrapper(cursor).get());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return list;
    }

    public class UnMedCursorWrapper extends CursorWrapper {

        public UnMedCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public UnidadeMedida get() {
            UnidadeMedida unMed = new UnidadeMedida();
            unMed.setCodigo(getString(getColumnIndex("COD")));
            unMed.setNome(getString(getColumnIndex("DESCRI")));

            return unMed;
        }
    }
}
