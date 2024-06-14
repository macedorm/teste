package br.com.jjconsulting.mobile.dansales.database;


import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Grupo;
import br.com.jjconsulting.mobile.dansales.model.Planta;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PlantaDao extends BaseDansalesDao {

    public PlantaDao(Context context) {
        super(context);
    }

    /**
     * Load enabled list of order id to bonification
     */
    public ArrayList<Planta> getPlantas(String codigoUnidadeNegocio, String codTipoVenda, String codCliente) {

        ArrayList<Planta> plantas = new ArrayList<>();

        StringBuilder sqlPedidos = new StringBuilder();
        sqlPedidos.append("SELECT DISTINCT LTRIM(CODPLANTA) AS CODPLANTA, DESCPLANTA, ORDEM ");
        sqlPedidos.append("FROM ");
        sqlPedidos.append(" (SELECT PLA_TXT_PLANTA AS CODPLANTA, PLA_TXT_PLANTA AS DESCPLANTA, 1 AS ORDEM");
        sqlPedidos.append(" FROM planta_websales WHERE pla_txt_unid_negoc = '" + codigoUnidadeNegocio + "'");
        sqlPedidos.append(" AND pla_txt_tpped ='" + codTipoVenda + "'");
        sqlPedidos.append(" AND pla_txt_cliente = '" + codCliente + "'");
        sqlPedidos.append(" AND PLA_TXT_PADRAO = '1'");
        sqlPedidos.append(" UNION ALL ");
        sqlPedidos.append(" SELECT PLA_TXT_PLANTA AS CODPLANTA,PLA_TXT_PLANTA AS DESCPLANTA, 3 AS ORDEM ");
        sqlPedidos.append(" FROM planta_websales");
        sqlPedidos.append(" WHERE pla_txt_unid_negoc = '" + codigoUnidadeNegocio + "'");
        sqlPedidos.append(" AND pla_txt_tpped ='" + codTipoVenda + "'");
        sqlPedidos.append(" AND pla_txt_cliente = '" + codCliente + "'");
        sqlPedidos.append(" AND PLA_TXT_PADRAO = '0'");
        sqlPedidos.append(" UNION all");
        sqlPedidos.append(" SELECT PLANTA AS CODPLANTA, PLANTA AS DESCPLANTA, 2 AS ORDEM  ");
        sqlPedidos.append(" FROM TB_DECLIENTEUN");
        sqlPedidos.append(" WHERE COD_UNID_NEGOC = '" + codigoUnidadeNegocio + "'");
        sqlPedidos.append(" AND COD_EMITENTE = '" + codCliente + "'");
        sqlPedidos.append(" ) ORDER BY ORDEM ");

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sqlPedidos.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Planta planta;
                planta = new PlantaDao.PlantaCursorWrapper(cursor).getPlanta();
                plantas.add(planta);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        } finally {
            database.close();
        }

        return plantas;
    }


    public class PlantaCursorWrapper extends CursorWrapper {

        public PlantaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Planta getPlanta() {
            Planta planta = new Planta();
            planta.setCodPlanta(getString(getColumnIndex("CODPLANTA")));
            planta.setDescPLanta(getString(getColumnIndex("DESCPLANTA")));

            return planta;
        }
    }
}

