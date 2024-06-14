package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.RelatorioNotasItem;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioNotasItensDao extends BaseDansalesDao {

    public RelatorioNotasItensDao(Context context) {
        super(context);
    }

    public ArrayList<RelatorioNotasItem> findAll(String codigoUnidadeNegocio,
                                                 String nota, String serie, int indexOffSet) {
        StringBuilder query = new StringBuilder();
        List<String> whereArgs = new ArrayList<>();

        query.append("SELECT");
        query.append(" DEPROD.DESCRICAO,");
        query.append(" DEPROD.COD_SKU,");
        query.append(" PRO.QTD_ITEM_NF,");
        query.append(" PRO.VL_ICMS_ST,");
        query.append(" PRO.VL_ICMS,");
        query.append(" PRO.VL_IPI,");
        query.append(" PRO.DESCONTO,");
        query.append(" PRO.VAL_TOTAL_NET_ITEM,");
        query.append(" PRO.VAL_UNIT_NET_ITEM");
        query.append(" FROM TB_NF_CAB CAB");
        query.append(" INNER JOIN TB_NF_PRO PRO");
        query.append(" ON PRO.NUM_NF = CAB.NUM_NF");
        query.append(" AND PRO.NUM_SERIE_NF = CAB.NUM_SERIE_NF");
        query.append(" INNER JOIN TB_DEPRODUTO DEPROD");
        query.append(" ON DEPROD.COD_SKU = PRO.COD_SKU");
        query.append(" AND DEPROD.COD_UNID_NEGOC = CAB.COD_SALES_ORG");
        query.append(" AND DEPROD.DEL_FLAG = '0'");
        query.append(" WHERE CAB.COD_SALES_ORG = ?");
        query.append(" AND CAB.NUM_NF = ?");
        query.append(" AND CAB.NUM_SERIE_NF = ?");

        whereArgs.add(codigoUnidadeNegocio);
        whereArgs.add(nota);
        whereArgs.add(serie);

        if (indexOffSet != -1) {
            query.append(" LIMIT ");
            query.append(Config.SIZE_PAGE);
            query.append(" OFFSET ");
            query.append(indexOffSet);
        }

        ArrayList<RelatorioNotasItem> produtos = new ArrayList<>();
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                produtos.add(new NotasItemCursorWrapper(cursor).get());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return produtos;
    }

    public class NotasItemCursorWrapper extends CursorWrapper {

        public NotasItemCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public RelatorioNotasItem get() {
            RelatorioNotasItem item = new RelatorioNotasItem();
            item.setNome(getString(getColumnIndex("DESCRICAO")));
            item.setId(getString(getColumnIndex("COD_SKU")));
            item.setQtd(FormatUtils.insertCaseDecimal(
                    getString(getColumnIndex("QTD_ITEM_NF")), 3));
            item.setIcmsSt(FormatUtils.insertCaseDecimal(
                    getString(getColumnIndex("VL_ICMS_ST")), 2));
            item.setVlrIcms(FormatUtils.insertCaseDecimal(
                    getString(getColumnIndex("VL_ICMS")), 2));
            item.setVlrIpi(FormatUtils.insertCaseDecimal(
                    getString(getColumnIndex("VL_IPI")), 2));
            item.setVlrDesc(FormatUtils.insertCaseDecimal(
                    getString(getColumnIndex("DESCONTO")), 2));
            item.setValorTotal(FormatUtils.insertCaseDecimal(
                    getString(getColumnIndex("VAL_TOTAL_NET_ITEM")), 2));
            item.setVlrUnitario(FormatUtils.insertCaseDecimal(
                    getString(getColumnIndex("VAL_UNIT_NET_ITEM")), 2));

            return item;
        }
    }
}
