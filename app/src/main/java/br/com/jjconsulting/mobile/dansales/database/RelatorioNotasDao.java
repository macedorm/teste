package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.NotaFilter;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioNotasDao extends BaseDansalesDao{

    public RelatorioNotasDao(Context context) {
       super(context);
    }

    public ArrayList<RelatorioNotas> findAll(String codigoUsuario, String codigoUnidadeNegocio,
                                             String genericFilter, NotaFilter filter,
                                             int indexOffSet) {
        StringBuilder query = new StringBuilder();
        List<String> whereArgs = new ArrayList<>();

        query.append("SELECT");
        query.append(" cli.NOM_CLIENTE,");
        query.append(" cli.COD_EMITENTE,");
        query.append(" cli.NUM_CNPF_CPF,");
        query.append(" CAB.NUM_NF,");
        query.append(" CAB.NUM_SERIE_NF,");
        query.append(" CAB.DAT_EMISSAO_NF,");
        query.append(" CAB.NUM_PEDIDO,");
        query.append(" CPAG.DESCRICAO,");
        query.append(" ORC_TXT_CODSIGA,");
        query.append(" ORC_TXT_ID,");
        query.append(" ORC_TXT_TPVEND,");
        query.append(" 0 AS VLR_TOTAL");
        query.append(" FROM TB_NF_CAB CAB");
        query.append(" INNER JOIN TB_DECLIENTE cli");
        query.append(" ON cli.COD_EMITENTE = CAB.COD_CLIENTE");
        query.append(" AND cli.DEL_FLAG = '0'");
        query.append(" INNER JOIN TB_DECLIENTEUN cun");
        query.append(" ON cun.COD_EMITENTE = cli.COD_EMITENTE");
        query.append(" AND cun.COD_UNID_NEGOC = CAB.COD_SALES_ORG");
        query.append(" AND cun.DEL_FLAG = '0'");
        query.append(" AND cun.INATIVO <> '1'");
        query.append(" INNER JOIN TB_DECLIUNREG cur");
        query.append(" ON cur.COD_EMITENTE = cun.COD_EMITENTE");
        query.append(" AND cur.COD_UNID_NEGOC = cun.COD_UNID_NEGOC");
        query.append(" AND cur.DEL_FLAG = '0'");
        query.append(" AND (cur.SEQUENCIA = '000' OR SEQUENCIA >= '100')");
        query.append(" LEFT JOIN TBORCAMENTO");
        query.append(" ON ORC_TXT_ID = CAB.NUM_PEDIDO");
        query.append(" AND ORC_TXT_CLIENTECOD = CAB.COD_CLIENTE");
        query.append(" LEFT JOIN TPVEND");
        query.append(" ON X5_CHAVE = ORC_TXT_TPVEND");
        query.append(" LEFT JOIN TB_DECONDPAGTO CPAG");
        query.append(" ON CPAG.COD_COND_PAGTO = ORC_TXT_IDCONDPAG");
        query.append(" AND CPAG.DEL_FLAG = '0'");

        String whereClause = " WHERE CAB.COD_SALES_ORG = ?";
        whereArgs.add(codigoUnidadeNegocio);

        // filtro geral
        if (genericFilter != null) {
            String genericFilterWithLike = "%" + genericFilter + "%";

            whereClause += " AND (" +
                    " cli.NOM_CLIENTE LIKE ?" +
                    " OR cli.COD_EMITENTE LIKE ?" +
                    " OR cli.COD_CIDADE LIKE ?" +
                    " OR cli.COD_EMITENTE LIKE ?" +
                    " OR CAB.NUM_NF LIKE ?" +
                    " OR ORC_TXT_CODSIGA like ?" +
                    " ) ";

            whereArgs.add(genericFilterWithLike);
            whereArgs.add(genericFilterWithLike);
            whereArgs.add(genericFilterWithLike);
            whereArgs.add(genericFilterWithLike);
            whereArgs.add(genericFilterWithLike);
            whereArgs.add(genericFilterWithLike);
        }

        if (filter == null) {
            // filtro padrão
            whereClause += " AND cur.COD_REG_FUNC = ?";
            whereArgs.add(codigoUsuario);
        } else {
            // filtro data
            if (filter.getDateStart() != null && filter.getDateEnd() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd0000");
                query.append(" AND CAB.DAT_EMISSAO_NF BETWEEN '");
                query.append(simpleDateFormat.format(filter.getDateStart()));
                query.append("' AND '");
                query.append(simpleDateFormat.format(filter.getDateEnd()));
                query.append("'");
            }

            // filtro organização
            if (filter.getOrganizacao() != null) {
                whereClause += " AND cli.COD_ORGANIZACAO = ?";
                whereArgs.add(filter.getOrganizacao().getCodigo());
            }

            // filtro bandeira
            if (filter.getBandeira() != null) {
                whereClause += " AND cli.COD_BANDEIRA = ?";
                whereArgs.add(filter.getBandeira().getCodigoBandeira());
            }

            // filtro status
            if (filter.getStatus() != null) {
                if(filter.getStatus() == Cliente.STATUS_CREDITO_GREEN){
                    whereClause += " and (A1_LEGEND = '" + filter.getStatus() + "' OR A1_LEGEND ISNULL ) ";
                } else {
                    whereClause += " and A1_LEGEND = '" + filter.getStatus() + "' ";
                }
            }

            // filtro hierarquia
            List<Usuario> hierarquia = filter.getHierarquiaComercial();
            if (hierarquia != null && hierarquia.size() > 0) {
                whereClause += " AND cur.COD_REG_FUNC IN (";
                for (int i = 0; i < hierarquia.size(); i++) {
                    Usuario usuario = hierarquia.get(i);
                    if (i > 0) {
                        whereClause += ",";
                    }
                    whereClause += "'" + usuario.getCodigo() + "'";
                }
                whereClause += ")";
            } else {
                whereClause += " AND cur.COD_REG_FUNC = ?";
                whereArgs.add(codigoUsuario);
            }
        }

        // appending the where clause
        query.append(" ");
        query.append(whereClause);

        // appending the order by clause
        query.append(" ORDER BY DATE(SUBSTR(CAB.DAT_EMISSAO_NF, 0, 9)), CAB.NUM_PEDIDO");

        if (indexOffSet != -1) {
            query.append(" LIMIT ");
            query.append(Config.SIZE_PAGE);
            query.append(" OFFSET ");
            query.append(indexOffSet);
        }

        ArrayList<RelatorioNotas> notas = new ArrayList<>();
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notas.add(new NotasCursorWrapper(cursor).get());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return notas;
    }

    private class NotasCursorWrapper extends CursorWrapper {

        private NotasCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public RelatorioNotas get() {
            RelatorioNotas relatorioNotas = new RelatorioNotas();
            relatorioNotas.setNome(getString(getColumnIndex("NOM_CLIENTE")));
            relatorioNotas.setCodigoCliente(getString(getColumnIndex("COD_EMITENTE")));
            relatorioNotas.setSap(getString(getColumnIndex("ORC_TXT_CODSIGA")));
            relatorioNotas.setNumero(getString(getColumnIndex("NUM_NF")));
            relatorioNotas.setSerie(getString(getColumnIndex("NUM_SERIE_NF")));
            relatorioNotas.setCnpj(getString(getColumnIndex("NUM_CNPF_CPF")));
            relatorioNotas.setNumeroPedido(getString(getColumnIndex("NUM_PEDIDO")));
            relatorioNotas.setCondicaoPagamento(getString(getColumnIndex("DESCRICAO")));
            relatorioNotas.setCodigoTipoVenda(getString(getColumnIndex("ORC_TXT_TPVEND")));

            try {
                String date = getString(getColumnIndex("DAT_EMISSAO_NF"));
                date = date.substring(0, date.length() - 4);
                relatorioNotas.setData(FormatUtils.toDateText(date));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return relatorioNotas;
        }
    }
}
