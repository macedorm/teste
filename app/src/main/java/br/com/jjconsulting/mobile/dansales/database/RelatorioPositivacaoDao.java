package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.model.Familia;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.model.RelatorioObjetivo;
import br.com.jjconsulting.mobile.dansales.model.RelatorioPositivacao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioPositivacaoDao extends BaseDansalesDao {

    private UsuarioDao usuarioDao;
    private Context context;

    public RelatorioPositivacaoDao(Context context) {
        super(context);
        this.context = context;
        usuarioDao = new UsuarioDao(context);
    }


    public RelatorioPositivacao getRelPositivacao(String codigoUsuario, String unidNegoc){
        StringBuilder query = new StringBuilder();

        query.append("SELECT R.COD_REG_FUNC, V.CVV_TXT_UNID_NEGOC, V.CVV_DAT_VISITA, ");
        query.append("ifnull(SUM(CVV_INT_PLANEJADO),0) PLANEJADO, ");
        query.append("ifnull(SUM(CVV_INT_POSITIVADO),0) ADERENCIA, ");
        query.append("ifnull(SUM(CVV_INT_FORAPRAZO),0) FORAPRAZO ");
        query.append(" FROM TB_CLIVISITA V " );
        query.append(" INNER JOIN TB_DECLIUNREG R " );
        query.append(" ON  R.COD_UNID_NEGOC = V.CVV_TXT_UNID_NEGOC ");
        query.append(" AND R.COD_EMITENTE = V.CVV_TXT_CLIENTE ");
        query.append(" AND R.COD_REG_FUNC IN ( ");
        query.append(usuarioDao.getAllUsersHierarquiaComercial(codigoUsuario, unidNegoc));
        query.append(")");
        query.append(" WHERE ((R.SEQUENCIA = '000' OR R.SEQUENCIA >= '100') OR (R.COD_UNID_NEGOC = ?)) " );
        query.append(" AND V.CVV_DAT_VISITA  BETWEEN DATETIME(?) AND DATETIME(?) ");
        query.append("GROUP BY V.COD_REG_FUNC, V.CVV_TXT_UNID_NEGOC ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(unidNegoc);
        whereArgs.add(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.getLastDayMonthBefore()));
        whereArgs.add(FormatUtils.toTextToCompareDateInSQlite(new Date()));

        RelatorioPositivacao relatorioPositivacao = new RelatorioPositivacao();
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                relatorioPositivacao = new PositivacaoWrapper(cursor).getPositivacao();
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return relatorioPositivacao;
    }


    public ArrayList<Usuario> getSubordinados(String codigoUsuario, String unidNegoc){
        StringBuilder query = new StringBuilder();

        query.append("SELECT DISTINCT D.COD_REG_FUNC, D.NOME_COMPLETO FROM TB_DEREGISTRO D ");
        query.append("INNER JOIN TB_DECLIUNREG R ");
        query.append("ON R.COD_REG_FUNC = D.COD_REG_FUNC ");
        query.append("INNER JOIN TB_CLIVISITA ");
        query.append("ON CVV_TXT_CLIENTE = COD_EMITENTE ");
        query.append("WHERE COD_SUPERIOR_PGV = ? ");
        query.append("AND (((R.SEQUENCIA = '000' OR R.SEQUENCIA >= '100') OR (R.COD_UNID_NEGOC = ?))) ");
        query.append("AND R.COD_REG_FUNC <> ? ");
        query.append("ORDER BY D.COD_REG_FUNC ");


        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUsuario);
        whereArgs.add(unidNegoc);
        whereArgs.add(codigoUsuario);

        ArrayList<Usuario> codUserSub = new ArrayList();
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                Usuario usuario = new UsuarioWrapper(cursor).getUsuario();
                codUserSub.add(usuario);
                cursor.moveToNext();
            }

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return  codUserSub;
    }

    public class UsuarioWrapper extends CursorWrapper {

        public UsuarioWrapper(Cursor cursor) {
            super(cursor);
        }

        public Usuario getUsuario() {
            Usuario usuario = new Usuario();
            usuario.setCodigo(getString(getColumnIndex("COD_REG_FUNC")));
            usuario.setNome(getString(getColumnIndex("NOME_COMPLETO")));

            return usuario;
        }
    }

    public class PositivacaoWrapper extends CursorWrapper {

        public PositivacaoWrapper(Cursor cursor) {
            super(cursor);
        }

        public RelatorioPositivacao getPositivacao() {
            RelatorioPositivacao relatorioPositivacao = new RelatorioPositivacao();
            relatorioPositivacao.setPlanejadoQtd(getInt(getColumnIndex("PLANEJADO")));
            relatorioPositivacao.setAderenciaQtd(getInt(getColumnIndex("ADERENCIA")));
            relatorioPositivacao.setForaPlanoQtd(getInt(getColumnIndex("FORAPRAZO")));
            return relatorioPositivacao;
        }
    }


}
