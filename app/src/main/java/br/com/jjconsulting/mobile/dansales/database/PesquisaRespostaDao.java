package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.dansales.model.TFreq;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Element;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRegSync;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaRespostaDao extends BaseDansalesDao {

    private Element element;

    private ArrayList<HashMap> pesquisaSend;

    private Date currentDate;

    public PesquisaRespostaDao(Context context, Date currentDate) {
        super(context);
        this.currentDate = currentDate;
    }

    public void upadateRespostaRegSync(PesquisaResposta pesquisaResposta) {

        pesquisaResposta.setRegSync(TRegSync.SEND);

        ContentValues contentValues = getContentValuesRespostas(pesquisaResposta);
        SQLiteDatabase database = getDb();
        StringBuilder whereClause = new StringBuilder();

        whereClause.append("RES_INT_PESQUISA_ID = ? AND RES_INT_PERGUNTA_ID = ? ");
        whereClause.append("AND RES_INT_REG_FUNC = ? AND RES_TXT_DTFREQ = ? ");
        whereClause.append("AND RES_TXT_CLIENTE = ? ");

        String whereArgs[];

        whereArgs = new String[]{String.valueOf(pesquisaResposta.getCodigoPesquisa()),
                String.valueOf(pesquisaResposta.getCodigoPergunta()),
                pesquisaResposta.getCodigoUsuario(), pesquisaResposta.getFreq(), pesquisaResposta.getCodigoCliente(),};

        try{
            database.update("TB_PESQUISA_RESPOSTA", contentValues, whereClause.toString(), whereArgs);
        } finally {
            database.close();
        }

    }

    public void insertResposta(PesquisaResposta pesquisaResposta) {
        ContentValues contentValues = getContentValuesRespostas(pesquisaResposta);
        SQLiteDatabase database = getDb();
        try{
            database.insertWithOnConflict("TB_PESQUISA_RESPOSTA", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            database.close();
        }
    }

    public void deleteResposta(PesquisaResposta pesquisaResposta) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("RES_INT_PESQUISA_ID = ? AND RES_INT_PERGUNTA_ID = ? ");
        whereClause.append("AND RES_INT_REG_FUNC = ? AND RES_TXT_DTFREQ = ? ");
        whereClause.append("AND RES_TXT_CLIENTE = ? ");

        String whereArgs[];
        whereArgs = new String[]{String.valueOf(pesquisaResposta.getCodigoPesquisa()),
                String.valueOf(pesquisaResposta.getCodigoPergunta()),
                pesquisaResposta.getCodigoUsuario(), pesquisaResposta.getFreq(), pesquisaResposta.getCodigoCliente()};

        SQLiteDatabase database = getDb();
        try{
            database.delete("TB_PESQUISA_RESPOSTA", whereClause.toString(), whereArgs);
        } finally {
            database.close();
        }
    }

    public void deleteAllRespostas(int codigoPesquisa, String codRegFunc, String codigoCliente, String freq) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("RES_INT_PESQUISA_ID = ? AND RES_INT_REG_FUNC = ? ");
        whereClause.append("AND RES_TXT_CLIENTE = ? ");
        whereClause.append("AND RES_TXT_DTFREQ = ? ");

        String whereArgs[];
        whereArgs = new String[]{String.valueOf(codigoPesquisa), codRegFunc, codigoCliente,
                freq};

        SQLiteDatabase database = getDb();
        try{
            database.delete("TB_PESQUISA_RESPOSTA", whereClause.toString(), whereArgs);
        }finally {
            database.close();
        }
    }

    public boolean isPesquisaEnviada(String codigoUsuario, String codigoCliente,
                                     TFreq tFreq, int codigoPesquisa) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT REGSYNC FROM TB_PESQUISA_RESPOSTA ");
        query.append("WHERE RES_INT_REG_FUNC = ? ");
        query.append("AND RES_INT_PESQUISA_ID = ? ");
        query.append("AND RES_TXT_DTFREQ = ? ");
        query.append("AND (DEL_FLAG IS NULL OR DEL_FLAG <> '1') ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUsuario);
        whereArgs.add(String.valueOf(codigoPesquisa));
        whereArgs.add(TFreq.getFreq(tFreq, currentDate));

        if (!TextUtils.isNullOrEmpty(codigoCliente)) {
            query.append("AND RES_TXT_CLIENTE = ? ");
            whereArgs.add(codigoCliente);
        }

        query.append(" GROUP BY REGSYNC ");

        boolean isPesquisaEnviada = false;

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int regsync = cursor.getInt(0);
                if (TRegSync.fromInteger(regsync) == TRegSync.SEND) {
                    isPesquisaEnviada = true;
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(br.com.jjconsulting.mobile.jjlib.util.Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return isPesquisaEnviada;
    }


    public ArrayList<PesquisaResposta> getRespostaPesquisa(String codigoUsuario, String codigoCliente,
                                                           TFreq tFreq, PesquisaPergunta pesquisaPergunta) {
        ArrayList<PesquisaResposta> pesquisaResposta = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM TB_PESQUISA_RESPOSTA ");
        query.append("WHERE RES_INT_REG_FUNC = ? ");
        query.append("AND RES_INT_PESQUISA_ID = ? ");
        query.append("AND RES_INT_PERGUNTA_ID = ? ");
        query.append("AND RES_TXT_DTFREQ = ? ");
        query.append("AND (DEL_FLAG IS NULL OR DEL_FLAG <> '1') ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUsuario);
        whereArgs.add(String.valueOf(pesquisaPergunta.getIdPequisa()));
        whereArgs.add(String.valueOf(pesquisaPergunta.getNumPergunta()));
        whereArgs.add(TFreq.getFreq(tFreq, currentDate));

        if (!TextUtils.isNullOrEmpty(codigoCliente)) {
            query.append("AND RES_TXT_CLIENTE = ? ");
            whereArgs.add(codigoCliente);
        }


        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PesquisaResposta item = new RespostaPesquisaPesquisaCursorWrapper(cursor)
                        .getRespostaPesquisa();
                pesquisaResposta.add(item);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return pesquisaResposta;
    }


    private static ContentValues getContentValuesRespostas(PesquisaResposta pesquisaResposta) {
        ContentValues values = new ContentValues();
        values.put("RES_INT_PESQUISA_ID", pesquisaResposta.getCodigoPesquisa());
        values.put("RES_INT_REG_FUNC", pesquisaResposta.getCodigoUsuario());
        values.put("RES_TXT_CLIENTE", pesquisaResposta.getCodigoCliente());
        values.put("RES_INT_PERGUNTA_ID", pesquisaResposta.getCodigoPergunta());
        values.put("RES_TXT_RESPOSTA", pesquisaResposta.getResposta());
        values.put("RES_TXT_RESPOSTA_OPC", pesquisaResposta.getOpcao());
        values.put("RES_TXT_EXT_ARQ", pesquisaResposta.getExtensaoArquivo());
        values.put("RES_FLO_LATITUDE", pesquisaResposta.getPosLatitute());
        values.put("RES_FLO_LONGITUDE", pesquisaResposta.getPosLongitude());
        values.put("RES_TXT_DTFREQ", pesquisaResposta.getFreq());

        values.put("REGSYNC", pesquisaResposta.getRegSync().getValue());


        try {
            values.put("RES_DAT_RESPOSTA", FormatUtils.toDefaultDateFormat(
                    pesquisaResposta.getData()));
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        return values;
    }

    public class RespostaPesquisaPesquisaCursorWrapper extends CursorWrapper {

        public RespostaPesquisaPesquisaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public PesquisaResposta getRespostaPesquisa() {
            PesquisaResposta pesquisaResposta = new PesquisaResposta();
            pesquisaResposta.setCodigoPesquisa(
                    getInt(getColumnIndex("RES_INT_PESQUISA_ID")));
            pesquisaResposta.setCodigoUsuario(
                    getString(getColumnIndex("RES_INT_REG_FUNC")));
            pesquisaResposta.setCodigoCliente(
                    getString(getColumnIndex("RES_TXT_CLIENTE")));
            pesquisaResposta.setCodigoPergunta(
                    getInt(getColumnIndex("RES_INT_PERGUNTA_ID")));
            pesquisaResposta.setResposta(
                    getString(getColumnIndex("RES_TXT_RESPOSTA")));
            pesquisaResposta.setOpcao(
                    getString(getColumnIndex("RES_TXT_RESPOSTA_OPC")));
            pesquisaResposta.setExtensaoArquivo(
                    getString(getColumnIndex("RES_TXT_EXT_ARQ")));

            pesquisaResposta.setPosLatitute(
                    getDouble(getColumnIndex("RES_FLO_LATITUDE")));
            pesquisaResposta.setPosLongitude(
                    getDouble(getColumnIndex("RES_FLO_LONGITUDE")));

            pesquisaResposta.setFreq(getString(getColumnIndex("RES_TXT_DTFREQ")));
            pesquisaResposta.setRegSync(TRegSync.fromInteger(getInt(getColumnIndex("REGSYNC"))));

            try {
                pesquisaResposta.setData(FormatUtils.toDate(
                        getString(getColumnIndex("RES_DAT_RESPOSTA"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getRespostaPesquisa: " + parseEx.toString());
            }

            return pesquisaResposta;
        }
    }


    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public ArrayList<HashMap> getPesquisaSend() {
        return pesquisaSend;
    }

    public void setPesquisaSend(ArrayList<HashMap> pesquisaSend) {
        this.pesquisaSend = pesquisaSend;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
}
