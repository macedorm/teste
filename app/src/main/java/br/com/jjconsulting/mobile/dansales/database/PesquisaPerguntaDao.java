package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.CondicaoPerguntaPesquisa;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaOpcoes;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaType;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.dansales.model.TFreq;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaPerguntaDao extends BaseDansalesDao{

    private PesquisaRespostaDao pesquisaRespostaDao;

    public PesquisaPerguntaDao(Context context, Date currentDate) {
        super(context);
        pesquisaRespostaDao = new PesquisaRespostaDao(context, currentDate);
    }

    public ArrayList<PesquisaPergunta> getPerguntasParent(int codigoPesquisa, int codigoPerPai, String codigoUsuario, String codigoCliente, TFreq tFreq) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("WHERE DEL_FLAG <> '1' AND PER_INT_PESQUISA_ID = ? ");
        whereClause.append(" and PER_INT_NUM_PERGUNTA_PAI = ? ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(String.valueOf(codigoPesquisa));
        whereArgs.add(String.valueOf(codigoPerPai));

        String orderBy = "ORDER BY PER_INT_ORDEM ASC";

        return queryPerguntas(codigoUsuario, codigoCliente, tFreq, whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, true);
    }

    public ArrayList<PesquisaPergunta> getPerguntas(int codigoPesquisa, String codigoUsuario, String codigoCliente, TFreq tFreq) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("WHERE DEL_FLAG <> '1' AND PER_INT_PESQUISA_ID = ? ");
        whereClause.append(" AND (PER_INT_NUM_PERGUNTA_PAI IS NULL");
        whereClause.append(" OR PER_INT_NUM_PERGUNTA_PAI = ?)");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(String.valueOf(codigoPesquisa));
        whereArgs.add("0");

        String orderBy = "ORDER BY PER_INT_ORDEM ASC";

        return queryPerguntas(codigoUsuario, codigoCliente, tFreq, whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, false);
    }

    private ArrayList<PesquisaPergunta> queryPerguntas(String codigoUsuario, String codigoCliente, TFreq tFreq, String whereClause, String[] args, String orderBy, boolean isParent) {
        ArrayList<PesquisaPergunta> listPerguntas = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT PER_INT_NUM_PERGUNTA ");
        query.append(",PER_INT_PESQUISA_ID ");
        query.append(",PER_INT_NUM_PERGUNTA_PAI ");
        query.append(",PER_TXT_DESC ");
        query.append(",PER_TXT_TITULO ");
        query.append(",PER_TXT_SUBTITULO ");
        query.append(",PER_TXT_DESCRICAO ");
        query.append(",PER_TXT_IMG ");
        query.append(",PER_INT_OBJ_ID ");
        query.append(",PER_TXT_OBRIGAT ");
        query.append(",PER_INT_ORDEM ");
        query.append(",PER_INT_TAM_MAX ");
        query.append(",PER_FLO_VALMIN ");
        query.append(",PER_FLO_VALMAX ");
        query.append(",PER_FLO_VALMIN_1");
        query.append(",PER_FLO_VALMAX_1 ");
        query.append(",PER_FLO_VALMIN_2");
        query.append(",PER_FLO_VALMAX_2 ");
        query.append(",PER_FLO_VALMIN_3");
        query.append(",PER_FLO_VALMAX_3 ");
        query.append(",PER_FLO_VALMIN_4");
        query.append(",PER_FLO_VALMAX_4 ");
        query.append(",PER_FLO_VALMIN_5");
        query.append(",PER_FLO_VALMAX_5 ");
        query.append(",PER_TXT_TIPOCAMERA ");
        query.append(",PER_INT_NUM_PERGUNTA_DEPENDENCIA ");
        query.append(",PER_INT_CASADECIMAL ");
        query.append("FROM TB_PESQUISA_PERGUNTA ");

        if (whereClause != null) {
            query.append(" " + whereClause);
        }

        if (orderBy != null) {
            query.append(" " + orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                PesquisaPergunta pesquisaPergunta = new PerguntaPesquisaPesquisaCursorWrapper(cursor)
                        .getPerguntas(codigoUsuario, codigoCliente, tFreq);

                if (!isParent) {
                    pesquisaPergunta.setPesquisaPerguntaParent(getPerguntasParent(pesquisaPergunta.getIdPequisa(),
                            pesquisaPergunta.getNumPergunta(), codigoUsuario, codigoCliente, tFreq));
                }

                listPerguntas.add(pesquisaPergunta);
                cursor.moveToNext();

            }
        } catch (Exception e) {
            LogUser.log(br.com.jjconsulting.mobile.jjlib.util.Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return listPerguntas;
    }

    public ArrayList<PesquisaPerguntaOpcoes> getOpcoes(PesquisaPergunta pesquisaPergunta) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("WHERE DEL_FLAG <> '1' AND OPC_INT_PESQUISA_ID = ? ");
        whereClause.append("AND OPC_INT_NUM_PERGUNTA = ? ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(String.valueOf(pesquisaPergunta.getIdPequisa()));
        whereArgs.add(String.valueOf(pesquisaPergunta.getNumPergunta()));

        String orderBy = "ORDER BY OPC_INT_ORDEM ASC";

        return queryOpcoes(whereClause.toString(), whereArgs.toArray(new String[0]), orderBy);
    }


    private ArrayList<PesquisaPerguntaOpcoes> queryOpcoes(String whereClause, String[] args, String orderBy) {
        ArrayList<PesquisaPerguntaOpcoes> listOpcoes = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT OPC_INT_NUM_OPCAO   ");
        query.append(",OPC_TXT_VALOR ");
        query.append(",OPC_TXT_DESC ");
        query.append("FROM TB_PESQUISA_PERGUNTA_OPCOES");

        if (whereClause != null) {
            query.append(" " + whereClause);
        }

        if (orderBy != null) {
            query.append(" " + orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listOpcoes.add(new OpcoesPerguntaPesquisaCursorWrapper(cursor)
                        .getOpcoes());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return listOpcoes;
    }


    private ArrayList<PesquisaResposta> setAggregatedRespostaPesquisa(String codigoUsuario, String codigoCliente, TFreq tfreq, PesquisaPergunta pesquisaPergunta) {
        return pesquisaRespostaDao.getRespostaPesquisa(codigoUsuario, codigoCliente, tfreq, pesquisaPergunta);
    }

    private ArrayList<CondicaoPerguntaPesquisa> setAggregatedCondicao(PesquisaPergunta pesquisaPergunta) {
        ArrayList<CondicaoPerguntaPesquisa> condicaoPerguntaPesquisa = getCondicoes(pesquisaPergunta.getIdPequisa(),
                pesquisaPergunta.getNumPergunta());

        return condicaoPerguntaPesquisa;
    }

    public ArrayList<CondicaoPerguntaPesquisa> getCondicoes(int codigoPesquisa, int numeroPergunta) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("WHERE DEL_FLAG <> '1' AND CND_INT_PESQUISA_ID = ? ");
        whereClause.append("AND CND_INT_NUM_PERGUNTA = ? ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(String.valueOf(codigoPesquisa));
        whereArgs.add(String.valueOf(numeroPergunta));

        return queryCondicoes(whereClause.toString(), whereArgs.toArray(new String[0]), null);
    }

    private ArrayList<CondicaoPerguntaPesquisa> queryCondicoes(String whereClause, String[] args, String orderBy) {
        ArrayList<CondicaoPerguntaPesquisa> listCondicoes = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT CND_INT_ID   ");
        query.append(",CND_INT_OPERADOR ");
        query.append(",CND_TXT_VALOR1 ");
        query.append(",CND_TXT_VALOR2 ");
        query.append(",CND_INT_COND_NUM_PERGUNTA ");
        query.append("FROM TB_PESQUISA_PERGUNTA_CONDICAO");

        if (whereClause != null) {
            query.append(" " + whereClause);
        }

        if (orderBy != null) {
            query.append(" " + orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listCondicoes.add(new CondicoesPerguntaPesquisaCursorWrapper(cursor)
                        .getCondicoes());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return listCondicoes;
    }

    public class PerguntaPesquisaPesquisaCursorWrapper extends CursorWrapper {

        public PerguntaPesquisaPesquisaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public PesquisaPergunta getPerguntas(String codigoUsuario, String codigoCliente, TFreq tFreq) {
            PesquisaPergunta pesquisaPergunta = new PesquisaPergunta();
            pesquisaPergunta.setIdPequisa(getInt(getColumnIndex("PER_INT_PESQUISA_ID")));
            pesquisaPergunta.setNumPergunta(getInt(getColumnIndex("PER_INT_NUM_PERGUNTA")));
            pesquisaPergunta.setDescPergunta(getString(getColumnIndex("PER_TXT_DESC")));
            pesquisaPergunta.setTitlePergunta(getString(getColumnIndex("PER_TXT_TITULO")));
            pesquisaPergunta.setSubTitlePergunta(getString(getColumnIndex("PER_TXT_SUBTITULO")));
            pesquisaPergunta.setDescTitlePergunta(getString(getColumnIndex("PER_TXT_DESCRICAO")));
            pesquisaPergunta.setImage(getString(getColumnIndex("PER_TXT_IMG")));

            pesquisaPergunta.setTipo(PesquisaPerguntaType.getEnumValue(getInt(getColumnIndex("PER_INT_OBJ_ID"))));
            pesquisaPergunta.setObrigatoria(getString(getColumnIndex("PER_TXT_OBRIGAT")).equals("S"));
            pesquisaPergunta.setOrdem(getInt(getColumnIndex("PER_INT_ORDEM")));
            pesquisaPergunta.setTamMax(getInt(getColumnIndex("PER_INT_TAM_MAX")));

            pesquisaPergunta.setCasasDecimais(getInt(getColumnIndex("PER_INT_CASADECIMAL")));

            if(pesquisaPergunta.getCasasDecimais() > 0){
                pesquisaPergunta.setValorMax(getDouble(getColumnIndex("PER_FLO_VALMAX_" + pesquisaPergunta.getCasasDecimais())));
                pesquisaPergunta.setValorMin(getDouble(getColumnIndex("PER_FLO_VALMIN_" + + pesquisaPergunta.getCasasDecimais())));
            } else {
                pesquisaPergunta.setValorMax(getDouble(getColumnIndex("PER_FLO_VALMAX")));
                pesquisaPergunta.setValorMin(getDouble(getColumnIndex("PER_FLO_VALMIN")));
            }


            String isCamera = getString(getColumnIndex("PER_TXT_TIPOCAMERA"));

            if (!TextUtils.isNullOrEmpty(isCamera)) {
                pesquisaPergunta.setOnlyCamera(getString(getColumnIndex("PER_TXT_TIPOCAMERA")).equals("1"));
            }

            String dep = getString(getColumnIndex("PER_INT_NUM_PERGUNTA_DEPENDENCIA"));
            if(!TextUtils.isNullOrEmpty(dep)){
                pesquisaPergunta.setIdDependeciaResposta(Integer.parseInt(dep));
            }

            String id = String.format("%d%d", pesquisaPergunta.getIdPequisa(), pesquisaPergunta.getNumPergunta());
            pesquisaPergunta.setId(Integer.parseInt(id));

            pesquisaPergunta.setCondicaoPerguntaPesquisa(setAggregatedCondicao(pesquisaPergunta));
            pesquisaPergunta.setRespostaPesquisa(setAggregatedRespostaPesquisa(codigoUsuario, codigoCliente, tFreq, pesquisaPergunta));
            return pesquisaPergunta;
        }
    }

    public class OpcoesPerguntaPesquisaCursorWrapper extends CursorWrapper {

        public OpcoesPerguntaPesquisaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public PesquisaPerguntaOpcoes getOpcoes() {
            PesquisaPerguntaOpcoes pesquisaPerguntaOpcoes = new PesquisaPerguntaOpcoes();
            pesquisaPerguntaOpcoes.setDesc(getString(getColumnIndex("OPC_TXT_DESC")));
            pesquisaPerguntaOpcoes.setValor(getString(getColumnIndex("OPC_TXT_VALOR")));
            pesquisaPerguntaOpcoes.setNumOpcao(getInt(getColumnIndex("OPC_INT_NUM_OPCAO")));
            return pesquisaPerguntaOpcoes;
        }
    }

    public class CondicoesPerguntaPesquisaCursorWrapper extends CursorWrapper {

        public CondicoesPerguntaPesquisaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public CondicaoPerguntaPesquisa getCondicoes() {
            CondicaoPerguntaPesquisa condicaoPerguntaPesquisa = new CondicaoPerguntaPesquisa();
            condicaoPerguntaPesquisa.setId(getInt(getColumnIndex("CND_INT_ID")));
            condicaoPerguntaPesquisa.setOperador(getInt(getColumnIndex("CND_INT_OPERADOR")));
            condicaoPerguntaPesquisa.setCondicaoValor1(getString(getColumnIndex("CND_TXT_VALOR1")));
            condicaoPerguntaPesquisa.setCondicaoValor2(getString(getColumnIndex("CND_TXT_VALOR2")));
            condicaoPerguntaPesquisa.setCondicaoNumPergunta(getInt(getColumnIndex("CND_INT_COND_NUM_PERGUNTA")));
            return condicaoPerguntaPesquisa;
        }
    }
}