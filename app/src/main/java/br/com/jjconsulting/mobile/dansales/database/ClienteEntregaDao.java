package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.ClienteEntrega;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ClienteEntregaDao extends BaseDansalesDao {

    public ClienteEntregaDao(Context context) {
        super(context);
    }

    public boolean hasClienteEntrega(String codigoUnidadeNegocio, Cliente cliente) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT count(*) QTD ");
        query.append("FROM TB_DECLIENTRG ");
        query.append("WHERE COD_UN_NEGOCIO = ? ");
        query.append("AND COD_EMITENTE = ? ");
        query.append("AND DEL_FLAG = '0' ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUnidadeNegocio);
        whereArgs.add(cliente.getCodigo());

        int count = 0;
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                count = cursor.getInt(cursor.getColumnIndex("QTD"));
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return (count > 0);
    }

    public ArrayList<ClienteEntrega> getClienteEntrega(String codigoUnidadeNegocio, Cliente cliente) {
        ArrayList<ClienteEntrega> listLocalEntrega = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TB_DECLIENTRG ");
        query.append("WHERE COD_UN_NEGOCIO = ? ");
        query.append("AND COD_EMITENTE = ? ");
        query.append("AND DEL_FLAG = '0'  AND NOM_CLIENTE NOTNULL ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUnidadeNegocio);
        whereArgs.add(cliente.getCodigo());

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listLocalEntrega.add(new ClienteEntregaCursorWrapper(cursor).getLocalEntrega());
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listLocalEntrega;
    }

    public class ClienteEntregaCursorWrapper extends CursorWrapper {

        public ClienteEntregaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public ClienteEntrega getLocalEntrega() {
            ClienteEntrega localEntrega = new ClienteEntrega();

            StringBuilder endereco = new StringBuilder();
            endereco.append(TextUtils.firstLetterUpperCase(getString(getColumnIndex("DSC_ENDERECO"))));
            endereco.append(", " + TextUtils.firstLetterUpperCase(getString(getColumnIndex("DSC_BAIRRO"))));
            endereco.append(", " + TextUtils.firstLetterUpperCase(getString(getColumnIndex("DSC_CIDADE"))));
            endereco.append(", " + getString(getColumnIndex("COD_ESTADO")));
            localEntrega.setEndereco(endereco.toString());
            localEntrega.setLojaEntrega(getString(getColumnIndex("COD_EMITENTE_REL")));

            return localEntrega;
        }
    }
}
