package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.PlanoCampo;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PlanoCampoDao extends BaseDansalesDao {

    public PlanoCampoDao(Context context) {
        super(context);
    }

    public Date getProxVisita(String unidadeNegocio, Cliente cliente){

        Date dataPedido = new Date();
        Date dataFinal;

        try {

            boolean isPlanoCampo = checkPlanoCampo(unidadeNegocio, cliente.getCodigo(), dataPedido);
            int leadTime = getLeadTime(unidadeNegocio, cliente.getCodigo());
            dataFinal = FormatUtils.addDay(dataPedido, leadTime);

            if (isPlanoCampo && !checkFeriado(dataPedido) &&
                    !checkFeriado(dataFinal)) {
                return dataFinal;
            } else {
                int count = 0;
                dataFinal = dataPedido;

                if (!isPlanoCampo) {
                    while (!checkPlanoCampo(unidadeNegocio, cliente.getCodigo(), dataFinal) && count < 7) {
                        dataFinal = FormatUtils.addDay(dataFinal, 1);
                        count++;

                        if(count == 7){
                            dataFinal = dataPedido;
                        }
                    }
                }

                if (checkFeriado(dataFinal)) {
                    while (checkFeriado(dataFinal) && dataFinal.after(dataPedido) ) {
                        dataFinal = FormatUtils.addDay(dataFinal, -1);
                    }
                }

                Date leadTimeDate = FormatUtils.addDay(dataFinal, leadTime);

                if (checkFeriado(leadTimeDate)) {
                    while (checkFeriado(leadTimeDate)) {
                        leadTimeDate = FormatUtils.addDay(leadTimeDate, 1);
                    }
                }

                dataFinal = leadTimeDate;


                return dataFinal;
            }
        } catch (Exception ex){
            return dataPedido;
        }

    }


    public boolean checkFeriado(Date date) {

        StringBuilder query = new StringBuilder();

        String dateString = FormatUtils.toTextToCompareDateInSQlite(date);

        query.append(" SELECT distinct 1 as IS_FERIADO  FROM  TBFERIADO ");
        query.append(" WHERE CASE WHEN length(DT_FERIADO) < 10 ");
        query.append(" THEN   DT_FERIADO = strftime('%d', datetime(?))  || '/' || strftime('%m', datetime(?)) ");
        query.append(" ELSE  DT_FERIADO = strftime('%d', datetime(?))  || '/' || strftime('%m', datetime(?)) || '/' || strftime('%Y', datetime(?)) ");
        query.append(" END ");

        String[] whereArgs = new String[5];

        for(int ind = 0; ind < 5; ind++){
            whereArgs[ind] = dateString;
        }

        SQLiteDatabase database = getDb();
        boolean isFeriado = false;
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs)) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                isFeriado = cursor.getString(cursor.getColumnIndex("IS_FERIADO")).equals("1");
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        if(!isFeriado){
            int dayWeekend = FormatUtils.getDayOfWeek(date);
            if(dayWeekend == 1) {
                isFeriado = true;
            }

        }

        return isFeriado;
    }

    public int getLeadTime(String unidadeNegocio, String codCli){

        int leadTime = 0;

        StringBuilder query = new StringBuilder();
        query.append(" SELECT CODIGO_DE_ENTREGA FROM TB_DECLIENTEUN ");
        query.append(" WHERE COD_UNID_NEGOC = ? AND COD_EMITENTE = ? ");

        String[] whereArgs = {unidadeNegocio, codCli};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs)) {

            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                leadTime = cursor.getInt(cursor.getColumnIndex("CODIGO_DE_ENTREGA"));
            }
            database.close();

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return leadTime;
    }

    public boolean checkPlanoCampo(String unidadeNegocio, String codCli, Date date){
        boolean isPlano = false;

        StringBuilder query = new StringBuilder();
        query.append("SELECT CASE cast (strftime('%w', datetime(?)) as integer) ");
        query.append(" WHEN 0 THEN TB.PDC_TXT_VIS_DOM ");
        query.append(" WHEN 1 THEN TB.PDC_TXT_VIS_SEG ");
        query.append(" WHEN 2 THEN TB.PDC_TXT_VIS_TER ");
        query.append(" WHEN 3 THEN TB.PDC_TXT_VIS_QUA ");
        query.append(" WHEN 4 THEN TB.PDC_TXT_VIS_QUI ");
        query.append(" WHEN 5 THEN TB.PDC_TXT_VIS_SEX ");
        query.append(" WHEN 6 THEN TB.PDC_TXT_VIS_SAB END PDC_TXT_VIS ");
        query.append(" FROM TB_PLANODECAMPO TB ");
        query.append(" INNER JOIN (SELECT PDC_TXT_UNID_NEGOC, PDC_TXT_CODCLIENTE, ");
        query.append(" Max(PDC_DAT_VIGENCIA) AS VIGENCIA ");
        query.append(" FROM TB_PLANODECAMPO ");
        query.append(" WHERE  PDC_DAT_VIGENCIA <= datetime(?) ");
        query.append(" GROUP  BY PDC_TXT_UNID_NEGOC, PDC_TXT_CODCLIENTE) X ");
        query.append(" ON TB.PDC_TXT_UNID_NEGOC = X.PDC_TXT_UNID_NEGOC ");
        query.append(" AND TB.PDC_TXT_CODCLIENTE = X.PDC_TXT_CODCLIENTE ");
        query.append(" AND TB.PDC_DAT_VIGENCIA = X.VIGENCIA ");
        query.append(" WHERE TB.PDC_TXT_CODCLIENTE = ? ");
        query.append(" AND TB.PDC_TXT_UNID_NEGOC = ? ");

        String[] whereArgs = {FormatUtils.toTextToCompareDateInSQlite(date), FormatUtils.toTextToCompareDateInSQlite(date), codCli, unidadeNegocio};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs)) {
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                String planoCampo = cursor.getString(cursor.getColumnIndex("PDC_TXT_VIS"));
                isPlano = (planoCampo.equals(PlanoCampoUtils.VISITA_PEDIDO) || planoCampo.equals(PlanoCampoUtils.PEDIDO));
            }
            database.close();

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return isPlano;
    }

    public PlanoCampo getPlanoCampo(String unidadeNegocio, String codCli) {

        PlanoCampo planoCampo = null;

        String query = "select * from TB_PLANODECAMPO AS PC";
        String whereClause = " WHERE PC.PDC_TXT_UNID_NEGOC = ?  and PC.PDC_TXT_CODCLIENTE = ?";
        whereClause += " AND PC.PDC_DAT_VIGENCIA <= datetime(?)";
        whereClause += " ORDER BY PC.PDC_DAT_VIGENCIA";

        String[] whereArgs = {unidadeNegocio, codCli, FormatUtils.toTextToCompareDateInSQlite(new Date())};
        query += whereClause;

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                planoCampo = new PlanoCampo();
                planoCampo = new PlanoCampoCursorWrapper(cursor).getPlanoCampo();
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return planoCampo;

    }


    public class PlanoCampoCursorWrapper extends CursorWrapper {

        public PlanoCampoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public PlanoCampo getPlanoCampo() {
            PlanoCampo planoCampo = new PlanoCampo();
            planoCampo.setValidade(getString(getColumnIndex("PDC_DAT_VIGENCIA")));
            planoCampo.setVisSeg(getString(getColumnIndex("PDC_TXT_VIS_SEG")));
            planoCampo.setVisTer(getString(getColumnIndex("PDC_TXT_VIS_TER")));
            planoCampo.setVisQua(getString(getColumnIndex("PDC_TXT_VIS_QUA")));
            planoCampo.setVisQui(getString(getColumnIndex("PDC_TXT_VIS_QUI")));
            planoCampo.setVisSex(getString(getColumnIndex("PDC_TXT_VIS_SEX")));
            planoCampo.setVisSab(getString(getColumnIndex("PDC_TXT_VIS_SAB")));
            planoCampo.setVisDom(getString(getColumnIndex("PDC_TXT_VIS_DOM")));

            return planoCampo;
        }
    }
}
