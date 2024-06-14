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

import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.data.ResumoSortimento;
import br.com.jjconsulting.mobile.dansales.model.ComplementoSortimento;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.LayoutUserSync;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TSortimento;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class SortimentoComplementoDao extends BaseDansalesDao {

    public SortimentoComplementoDao(Context context) {
        super(context);
    }

    /**
     * Get Complemento Sortimento
     * @param codigoUsuario
     * @param unidadeNegocio
     * @return
     */
    public List<ComplementoSortimento> getComplemento(String codigoUsuario, String unidadeNegocio) {
        List<ComplementoSortimento> listComplemento = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT co.*, (SELECT com.ID_FUNCAO from TB_SORT_COMPLEMENTO_ACESSO as com ");
        query.append("  INNER JOIN TB_DEREGISTRO as reg ON com.ID_FUNCAO == reg.COD_REGIONAL   OR com.ID_FUNCAO == reg.COD_FUNCAO_PGV ");
        query.append("  WHERE com.ID_COMPLEMENTO = ac.ID_COMPLEMENTO AND com.ID_FUNCAO == ac.ID_FUNCAO AND com.DEL_FLAG <> 1) as ACCESS_TYPE_TB_DEREGISTRO, ");
        query.append("      (CASE ID_FILTRO WHEN '1' ");
        query.append("          THEN  (CASE ID_FUNCAO ");
        query.append("              WHEN '" + codigoUsuario + "'" );
        query.append("                  THEN  '1' ");
        query.append("                  ELSE  null ");
        query.append("        END ) ");
        query.append("      END ) as ACCESS_TYPE_USER ");
        query.append("FROM TB_SORT_COMPLEMENTO_ACESSO as ac ");
        query.append("INNER JOIN TB_SORT_COMPLEMENTO as co ON co.ID = ac.ID_COMPLEMENTO ");
        query.append("WHERE ");
        query.append("  ( '" + FormatUtils.toTextToCompareshortDateInSQlite(new Date()) + "' between DATE(co.DT_INICIO) and DATE(co.DT_FIM)) ");
        query.append("  AND (ACCESS_TYPE_TB_DEREGISTRO notnull OR ACCESS_TYPE_USER notnull) ");
        query.append("  AND co.COD_UN = ? ");
        query.append("  AND co.DEL_FLAG <> 1 ");
        query.append("  AND ac.DEL_FLAG <> 1 ");
        query.append("GROUP by co.ID ");

        String[] args = {unidadeNegocio};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SortimentoComplementoTaskWrapper sortimentoComplementoTaskWrapper = new  SortimentoComplementoTaskWrapper(cursor);
                listComplemento.add(sortimentoComplementoTaskWrapper.getComplemento());
                cursor.moveToNext();
            }

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listComplemento;
    }

    public class SortimentoComplementoTaskWrapper extends CursorWrapper {

        public SortimentoComplementoTaskWrapper(Cursor cursor) {
            super(cursor);
        }

        public ComplementoSortimento getComplemento() {
            ComplementoSortimento complementoSortimento = new ComplementoSortimento();
            complementoSortimento.setId(getString(getColumnIndex("ID")));
            complementoSortimento.setStatus(getInt(getColumnIndex("STATUS")));
            complementoSortimento.setId(getString(getColumnIndex("ID")));
            complementoSortimento.setTitulo(getString(getColumnIndex("TITULO")));
            complementoSortimento.setSubtitulo(getString(getColumnIndex("SUBTITULO")));
            complementoSortimento.setArquivo(getString(getColumnIndex("ARQUIVO")));

            return complementoSortimento;
        }
    }
}
