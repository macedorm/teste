package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class MultiValuesDao extends BaseDansalesDao {


    public MultiValuesDao(Context context) {
        super(context);
    }

    public ArrayList<MultiValues> getList(TMultiValuesType codMultiValue) {
        ArrayList<MultiValues> multiValues = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT *");
        query.append(" FROM TB_MULTI_VALUES ");
        query.append(" WHERE COD_TABLE = ? AND DEL_FLAG <> '1' ");
        query.append("ORDER BY VAL_COD");

        String[] args = new String[]{String.valueOf(codMultiValue.getValue())};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                multiValues.add(new MultiValueCursorWrapper(cursor).getMultiValue());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return multiValues;
    }

    public MultiValues getItem(String cod) {
        MultiValues multiValues = new MultiValues();

        StringBuilder query = new StringBuilder();
        query.append("SELECT *");
        query.append(" FROM TB_MULTI_VALUES ");
        query.append(" WHERE VAL_COD = ? AND DEL_FLAG <> '1' ");
        query.append("ORDER BY VAL_COD");

        String[] args = new String[]{cod};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                multiValues = new MultiValueCursorWrapper(cursor).getMultiValue();
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return multiValues;
    }

    public class MultiValueCursorWrapper extends CursorWrapper {

        public MultiValueCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public MultiValues getMultiValue() {
            MultiValues multiValues = new MultiValues();
            multiValues.setCodTable(getInt(getColumnIndex("COD_TABLE")));
            multiValues.setValCod(getInt(getColumnIndex("VAL_COD")));
            multiValues.setDesc(getString(getColumnIndex("VAL_DESC")));
            return multiValues;
        }
    }
}
