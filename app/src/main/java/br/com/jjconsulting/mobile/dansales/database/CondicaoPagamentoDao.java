package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.CondicaoPagamento;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class CondicaoPagamentoDao extends BaseDansalesDao {


    public CondicaoPagamentoDao(Context context) {
        super(context);
    }

    public ArrayList<CondicaoPagamento> getCondPagEspecific(String unidadeNegocio, String codTipoVenda) {

        ArrayList<CondicaoPagamento> condicaoPagamentos = new ArrayList<>();

        String query = "select TPV.CONDPAG from TBPERFILTPVEND AS TBPV  Inner Join " +
                "TPVEND AS TPV  on TBPV.PRV_TXT_TPVEND = X5_CHAVE ";

        String whereClause = " WHERE TBPV.PRV_TXT_KEY = 'CONDPAGESPECIFIC'  and TBPV.PRV_TXT_COD_UN = ?" +
                " and TPV.X5_CHAVE = ? GROUP BY PRV_TXT_TPVEND";

        String[] whereArgs = {unidadeNegocio, codTipoVenda};

        query += whereClause;

        String inCondPag = "";

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                inCondPag = cursor.getString(cursor.getColumnIndex("CONDPAG"));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        if (inCondPag != null && inCondPag.length() > 0) {
            condicaoPagamentos = getCondPagArray(inCondPag);
        }

        return condicaoPagamentos;

    }


    public ArrayList<CondicaoPagamento> getCondPagArray(String listCodPag) {
        String whereClause = "where cdp.COD_COND_PAGTO in(" + listCodPag + ")" +
                " and cdp.DEL_FLAG = '0'";


        ArrayList<CondicaoPagamento> condicoes = query(whereClause, null, null);

        return condicoes;
    }

    public CondicaoPagamento get(String codigoCondicaoPagamento) {
        if (codigoCondicaoPagamento == null)
            return null;

        String whereClause = "where cdp.COD_COND_PAGTO = ?" +
                " and cdp.DEL_FLAG = '0'";
        String[] whereArgs = {codigoCondicaoPagamento};
        ArrayList<CondicaoPagamento> condicoes = query(whereClause, whereArgs, null);
        return condicoes.isEmpty() ? null : condicoes.get(0);
    }

    private ArrayList<CondicaoPagamento> query(String whereClause, String[] args, String orderBy) {
        ArrayList<CondicaoPagamento> condicoes = new ArrayList<>();

        String query = "select cdp.COD_COND_PAGTO, cdp.DESCRICAO" +
                " from TB_DECONDPAGTO cdp";

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
                condicoes.add(new CondicaoPagamentoDao.CondicaoPagamentoCursorWrapper(cursor)
                        .getCondicaoPagamento());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return condicoes;
    }

    public class CondicaoPagamentoCursorWrapper extends CursorWrapper {

        public CondicaoPagamentoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public CondicaoPagamento getCondicaoPagamento() {
            CondicaoPagamento condicaoPagamento = new CondicaoPagamento();
            condicaoPagamento.setCodigo(getString(getColumnIndex("COD_COND_PAGTO")));
            condicaoPagamento.setNome(getString(getColumnIndex("DESCRICAO")));

            return condicaoPagamento;
        }
    }
}
