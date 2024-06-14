package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.TapAcao;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapAcaoItemDao extends BaseDansalesDao {

    public TapAcaoItemDao(Context context) {
        super(context);
    }

    public ArrayList<TapAcao> get(String codigoUnidadeNegocio, int tipoId) {
        ArrayList<TapAcao> tapAcaoItemArrayList = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT ACA.ACA_INT_ID,");
        query.append(" ACA.ACA_TXT_DESCRICAO,");
        query.append(" ACA.ACA_TXT_UNID_NEGOC,");
        query.append(" ACA.ACA_INT_IDTPINVEST,");
        query.append(" ACA.ACA_TXT_ANEXO,");
        query.append(" ACA.ACA_TXT_APFIN,");
        query.append(" ACA.ACA_TXT_SEMCATEG ");
        query.append("FROM TBETAPCADACAO ACA ");
        query.append("WHERE ACA_TXT_UNID_NEGOC = ? AND ");
        query.append(" ACA_INT_IDTPINVEST = ? AND");
        query.append(" ACA_TXT_ATIVO = 'S' AND");
        query.append(" DEL_FLAG <> '1'");

        String[] args = {codigoUnidadeNegocio, String.valueOf(tipoId)};
        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                tapAcaoItemArrayList.add(new TapAcaoItemCursorWrapper(cursor).getTapAcaoItem());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return tapAcaoItemArrayList;
    }

    public class TapAcaoItemCursorWrapper extends CursorWrapper {

        public TapAcaoItemCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public TapAcao getTapAcaoItem() {
            TapAcao tapAcaoItem = new TapAcao();
            tapAcaoItem.setIdAcao(getInt(getColumnIndex("ACA_INT_ID")));
            tapAcaoItem.setDescricao(getString(getColumnIndex("ACA_TXT_DESCRICAO")));

            if (getString(getColumnIndex("ACA_TXT_ANEXO")).equals("S")) {
                tapAcaoItem.setAnexo(true);
            } else {
                tapAcaoItem.setAnexo(false);
            }

            if (getString(getColumnIndex("ACA_TXT_SEMCATEG")).equals("S")) {
                tapAcaoItem.setSemCateg(true);
            } else {
                tapAcaoItem.setSemCateg(false);
            }

            if (getString(getColumnIndex("ACA_TXT_APFIN")).equals("S")) {
                tapAcaoItem.setApFin(true);
            } else {
                tapAcaoItem.setApFin(false);
            }

            return tapAcaoItem;
        }
    }
}
