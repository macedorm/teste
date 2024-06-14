package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ChecklistNotaFilter;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisa;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisaPilar;
import br.com.jjconsulting.mobile.dansales.model.RelatorioChecklistNotas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioChecklistNotasDao extends BaseDansalesDao {

    public RelatorioChecklistNotasDao(Context context) {
        super(context);
    }

    public ArrayList<RelatorioChecklistNotas> findAll(String codigoUsuario,
                                                String codigoUnidadeNegocio,
                                                String nome,
                                                ChecklistNotaFilter filter,
                                                int indexOffSet) {

            ArrayList<RelatorioChecklistNotas> listRelatorioChecklistNotas = new ArrayList<>();
            StringBuilder query = new StringBuilder();

            query.append("SELECT  ren.*, ");
            query.append(" cli.NOM_CLIENTE, ");
            query.append(" pes.PES_TXT_NOME, ");
            query.append(" SUM(REN_NOTA) AS NOTA, ");
            query.append(" SUM(REN_ESPACO) AS ESPACO ");
            query.append("FROM TB_PESQUISA_RESPOSTA_NOTA ren ");
            query.append("INNER JOIN TB_PESQUISA pes ");
            query.append("  ON  pes.PES_INT_ID = ren.REN_INT_PESQUISA_ID ");
            query.append("  AND pes.DEL_FLAG = '0' ");
            query.append("INNER JOIN TB_DECLIENTE cli ");
            query.append("  ON  cli.COD_EMITENTE = ren.REN_TXT_CLIENTE ");
            query.append("  AND cli.DEL_FLAG = '0' ");
            query.append("INNER JOIN TB_DECLIENTEUN cun ");
            query.append("  ON  cun.COD_EMITENTE = cli.COD_EMITENTE ");
            query.append("  AND cun.COD_UNID_NEGOC = ? ");
            query.append("  AND cun.DEL_FLAG = '0' ");
            query.append("  AND cun.INATIVO <> '1' ");
            query.append("INNER JOIN TB_DECLIUNREG cur ");
            query.append("  ON  cur.COD_EMITENTE = cun.COD_EMITENTE ");
            query.append("  AND cur.COD_UNID_NEGOC = ? ");
            query.append("  AND cur.DEL_FLAG = '0' ");
            query.append("  AND cur.SEQUENCIA = '000' ");
            query.append("WHERE  ren.DEL_FLAG <> '1' ");
            query.append("  AND ren.REN_DAT_RESPOSTA BETWEEN datetime(?) AND datetime(?) ");

        List<String> whereArgs = new ArrayList<>();
            whereArgs.add(codigoUnidadeNegocio);
            whereArgs.add(codigoUnidadeNegocio);
            whereArgs.add(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.addDate(new Date(), -30, 0,0)));
            whereArgs.add(FormatUtils.toTextToCompareDateInSQlite(new Date()));

            if (nome != null) {
                query.append(" AND ((pes.PES_TXT_NOME LIKE ? ) ");
                query.append(" OR (REN_TXT_CLIENTE LIKE ? )) ");
                whereArgs.add("%" + nome + "%");
                whereArgs.add("%" + nome + "%");
            }

            if (filter != null) {

            if (filter.getCliente() != null) {
                query.append(" AND cli.COD_EMITENTE = ? ");
                whereArgs.add(filter.getCliente().getCodigo());
            }

            if (filter.getHierarquiaComercial() != null &&
                    filter.getHierarquiaComercial().size() > 0) {

                query.append(" and cur.COD_REG_FUNC in (");
                for (int i = 0; i < filter.getHierarquiaComercial().size(); i++) {
                    Usuario usuario = filter.getHierarquiaComercial().get(i);
                    if (i > 0) {
                        query.append(",");
                    }
                    query.append("'" + usuario.getCodigo() + "'");
                }
                query.append(") ");

            } else {
                query.append(" and cur.COD_REG_FUNC = ? ");
                whereArgs.add(codigoUsuario);
            }
        } else {
            query.append(" and cur.COD_REG_FUNC = ? ");
            whereArgs.add(codigoUsuario);
        }

            query.append(" GROUP BY ren.REN_DAT_RESPOSTA, REN_TXT_CLIENTE ");

            if (indexOffSet != -1) {
            query.append(" " + "LIMIT " + Config.SIZE_PAGE + " OFFSET " + indexOffSet);
        }

        SQLiteDatabase database = getDb();
            try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RelatorioChecklistNotas obj = new ChecklistNotaCursorWrapper(cursor).get();
                listRelatorioChecklistNotas.add(obj);
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listRelatorioChecklistNotas;
    }

    public SyncPesquisa getPilarNotas(RelatorioChecklistNotas relatorioChecklistNotas) {

        SyncPesquisa syncPesquisa = new SyncPesquisa();
        syncPesquisa.setNotaFiscal(Float.parseFloat(relatorioChecklistNotas.getNota()));
        syncPesquisa.setListPilar(new ArrayList<>());

        StringBuilder query = new StringBuilder();

        query.append("SELECT ren.REN_NOTA, ");
        query.append("  for.FOR_TXT_NOME, ");
        query.append("  for.FOR_TXT_IMAGEM, ");
        query.append("  for.FOR_FLO_PONTUACAO, ");
        query.append("  pes.PES_TXT_NOME ");
        query.append("FROM TB_PESQUISA_RESPOSTA_NOTA ren ");
        query.append("INNER JOIN TB_PESQUISA pes ");
        query.append("  ON  pes.PES_INT_ID = ren.REN_INT_PESQUISA_ID ");
        query.append("INNER JOIN TB_PESQUISA_FORMULA for ");
        query.append("  ON  for.FOR_INT_ID = ren.REN_INT_FORMULA_ID ");
        query.append("WHERE  ren.DEL_FLAG <> '1' ");
        query.append("  AND ren.REN_INT_PESQUISA_ID = ? ");
        query.append("  AND ren.REN_INT_REG_FUNC = ? ");
        query.append("  AND ren.REN_TXT_CLIENTE = ? ");
        query.append("  AND ren.REN_DAT_RESPOSTA = ? ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(relatorioChecklistNotas.getIdPesquisa());
        whereArgs.add(relatorioChecklistNotas.getCodRegFunc());
        whereArgs.add(relatorioChecklistNotas.getCodigoCliente());
        whereArgs.add(relatorioChecklistNotas.getDataResposta());

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SyncPesquisaPilar obj = new SyncPesquisaPilarCursorWrapper(cursor).get();
                syncPesquisa.getListPilar().add(obj);
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return syncPesquisa;
    }

    public class ChecklistNotaCursorWrapper extends CursorWrapper {

        public ChecklistNotaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public RelatorioChecklistNotas get() {
            RelatorioChecklistNotas obj = new RelatorioChecklistNotas();

            obj.setNomePesquisa(getString(getColumnIndex("PES_TXT_NOME")));
            obj.setCodigoCliente(getString(getColumnIndex("REN_TXT_CLIENTE")));
            obj.setNomeCliente(getString(getColumnIndex("NOM_CLIENTE")));
            obj.setCodRegFunc(getString(getColumnIndex("REN_INT_REG_FUNC")));
            obj.setDataResposta(getString(getColumnIndex("REN_DAT_RESPOSTA")));
            obj.setIdPesquisa(getString(getColumnIndex("REN_INT_PESQUISA_ID")));

            obj.setNota(getString(getColumnIndex("NOTA")));
            obj.setEspaco(getString(getColumnIndex("ESPACO")));

            return obj;
        }
    }


    public class SyncPesquisaPilarCursorWrapper  extends CursorWrapper {

        public SyncPesquisaPilarCursorWrapper (Cursor cursor) {
            super(cursor);
        }

        public SyncPesquisaPilar get() {
            SyncPesquisaPilar obj = new SyncPesquisaPilar();

            obj.setTextHeader("");
            obj.setShowHeader(false);

            obj.setPilar(getString(getColumnIndex("FOR_TXT_NOME")));
            obj.setUrlImg(getString(getColumnIndex("FOR_TXT_IMAGEM")));
            obj.setPeso(getFloat(getColumnIndex("FOR_FLO_PONTUACAO")));
            obj.setNota(getFloat(getColumnIndex("REN_NOTA")));
            obj.setStatus(5);

            return obj;
        }
    }


}
