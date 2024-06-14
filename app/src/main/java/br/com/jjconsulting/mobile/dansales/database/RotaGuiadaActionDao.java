package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.data.RotasFilter;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.RotaAcao;
import br.com.jjconsulting.mobile.dansales.model.RotaOrigem;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.TActionRotaGuiada;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaGuiadaActionDao extends BaseDansalesDao {

    public RotaGuiadaActionDao(Context context) {
        super(context);
    }

    public void insertAction(TActionRotaGuiada actionRotaGuiada, String dateAction, Rotas rota, RotaOrigem rotaOrigem){
        ContentValues contentValues = new ContentValues();
        contentValues.put("RGA_DAT_DIAPLANO", rota.getDate());
        contentValues.put("RGA_TXT_CODCLI", rota.getCodCliente());
        contentValues.put("RGA_TXT_UNIDNEGOC", rota.getCodUnidNeg());
        contentValues.put("RGA_INT_TIPO", actionRotaGuiada.getValue());
        contentValues.put("RGA_DAT_ACAO", dateAction);

        if(rotaOrigem != null){
            contentValues.put("RGA_FLO_LATITUDE", rotaOrigem.getLatCheckin());
            contentValues.put("RGA_FLO_LONGITUDE", rotaOrigem.getLongCheckin());
            contentValues.put("RGA_TXT_DENTRO_RAIO", rotaOrigem.isInRadius() ? "1":"0");
        }else {
            contentValues.put("RGA_TXT_DENTRO_RAIO", "0");
        }

        contentValues.put("COD_REG_FUNC", rota.getCodRegFunc());
        contentValues.put("DEL_FLAG", "0");
        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();
        try{
            database.insert("TB_ROTAGUIADA_ACAO", null, contentValues);
        }finally {
            database.close();
        }

    }

    public Date getLastDateAction(Rotas rota){

        StringBuilder query = new StringBuilder();
        query.append("SELECT RGA_DAT_ACAO ");
        query.append("FROM TB_ROTAGUIADA_ACAO ");
        query.append("WHERE COD_REG_FUNC = ? AND RGA_TXT_UNIDNEGOC = ? ");
        query.append("  AND RGA_DAT_DIAPLANO = ? AND RGA_TXT_CODCLI = ? ");
        query.append("  AND DEL_FLAG <> ? ");
        query.append("ORDER BY RGA_DAT_ACAO DESC LIMIT 1");


        String[] args = new String[]{rota.getCodRegFunc(),
                rota.getCodUnidNeg(), rota.getDate(), rota.getCodCliente(), "1"};

        Date lastDateAction = null;

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String date = cursor.getString(cursor.getColumnIndex("RGA_DAT_ACAO"));
                lastDateAction = FormatUtils.toDateSeconds(date);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return lastDateAction;
    }


    public void updateSync(RotaAcao rotaAcao, Date date) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(date));


            String[] args = new String[]{
                    rotaAcao.getDiaPlano(),
                    rotaAcao.getCodCliente(),
                    rotaAcao.getUnidadeNegocio(),
                    String.valueOf(rotaAcao.getTipo()),
                    rotaAcao.getDataAction(),
                    rotaAcao.getCodRegFunc()};

            database.update("TB_ROTAGUIADA_ACAO", contentValues,
                    "RGA_DAT_DIAPLANO = ? AND RGA_TXT_CODCLI = ? AND RGA_TXT_UNIDNEGOC = ? " +
                            "AND RGA_INT_TIPO = ? AND RGA_DAT_ACAO = ? AND COD_REG_FUNC = ? ", args);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }


    public ArrayList<RotaAcao> getSyncAcao(){

        ArrayList<RotaAcao> rotaAcaos = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TB_ROTAGUIADA_ACAO ");
        query.append("WHERE DT_ULT_ALT IS NULL ");

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                rotaAcaos.add(new RotaAcaoCursorWrapper(cursor).getAcao());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return rotaAcaos;
    }

    public class RotaAcaoCursorWrapper extends CursorWrapper {

        public RotaAcaoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public RotaAcao getAcao() {
            RotaAcao rotaAcao = new RotaAcao();
            rotaAcao.setDiaPlano(getString(getColumnIndex("RGA_DAT_DIAPLANO")));
            rotaAcao.setCodCliente(getString(getColumnIndex("RGA_TXT_CODCLI")));
            rotaAcao.setUnidadeNegocio(getString(getColumnIndex("RGA_TXT_UNIDNEGOC")));
            rotaAcao.setTipo(getInt(getColumnIndex("RGA_INT_TIPO")));
            rotaAcao.setDataAction(getString(getColumnIndex("RGA_DAT_ACAO")));
            rotaAcao.setLatitude(getDouble(getColumnIndex("RGA_FLO_LATITUDE")));
            rotaAcao.setLongitude(getDouble(getColumnIndex("RGA_FLO_LONGITUDE")));
            rotaAcao.setRaio(getString(getColumnIndex("RGA_TXT_DENTRO_RAIO")));
            rotaAcao.setCodRegFunc(getString(getColumnIndex("COD_REG_FUNC")));
            rotaAcao.setDtUltAlt(getString(getColumnIndex("DT_ULT_ALT")));
            rotaAcao.setDelFlag(getString(getColumnIndex("DEL_FLAG")));

            return rotaAcao;
        }
    }

}


