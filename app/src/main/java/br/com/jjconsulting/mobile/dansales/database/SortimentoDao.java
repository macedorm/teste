package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.data.ResumoSortimento;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.LayoutUserSync;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TSortimento;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class SortimentoDao extends BaseDansalesDao {

    public SortimentoDao(Context context) {
        super(context);
    }

    public ItensListSortimento  getPrecoSortimento(ItensListSortimento itensListSortimento,
                                                  String canal, String variante, String unidadeNegocio){
        String query = "select cons.PRC_PRECO_EDV_CONSUMIDOR, " +
                "  cons.PRC_PRECO_PROMO_CONSUMIDOR  " +
                " FROM TB_DEPRECO_CONSUMIDOR as cons" +
                " INNER JOIN TB_DECLIENTEUN_CG as cg" +
                " ON cons.PRC_CG = cg.CG_TXT_CG5 " +
                " WHERE PRC_CANAL = ? " +
                " AND PRC_VARIANTE = ? " +
                " AND CG_TXT_UNID_NEG = ? ";

        String[] args = {  canal,  variante, unidadeNegocio};

        itensListSortimento.setPrecoEDV(0);
        itensListSortimento.setPrecoPROMO(0);

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itensListSortimento.setPrecoEDV(cursor.getFloat(cursor.getColumnIndex("PRC_PRECO_EDV_CONSUMIDOR")));
                itensListSortimento.setPrecoPROMO(cursor.getFloat(cursor.getColumnIndex("PRC_PRECO_PROMO_CONSUMIDOR")));;

                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }
        return itensListSortimento;
    }

    public Integer getTipoSortimento(String unidadeNegocio, String codigoSortimento,
                                     String codigoProduto, Date dataReferencia) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);

        String query = "select sort.status " +
                    " from tb_sort_sortimento sort" +
                " where sort.sales_organization = ?" +
                " and sort.planogram_code = ?" +
                " and sort.product_code = ?" +
                " and datetime(sort.start_date) <= '" + date + "'" +
                " order by sort.start_date desc";

        String[] args = { unidadeNegocio,  codigoSortimento, codigoProduto };

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                String valueAsString = cursor.getString(0);
                if (valueAsString != null) {
                    return Integer.parseInt(valueAsString);
                }
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return null;
    }

    /**
     * Itens do sortimento para o CR
     * @param planogramCode
     * @param unidadeNegocio
     * @return
     */
    public ArrayList<ItensListSortimento> getItensSortimentoCR(String planogramCode, String nome, String unidadeNegocio){
        return getItensListSortimento(planogramCode, nome, -1, new Date(), unidadeNegocio, false);
    }

    /**
     * Itens do sortimento para o Checklist
     * @param planogramCode
     * @param status
     * @param dataReferencia
     * @param unidadeNegocio
     * @return
     */
    public ArrayList<ItensListSortimento> getItensSortimentoChecklist(String planogramCode, int status, Date dataReferencia, String unidadeNegocio, boolean isCLPreco){
        return getItensListSortimento(planogramCode, null,  status, dataReferencia, unidadeNegocio, isCLPreco);
    }

    /**
     * Consulta para os itens de sortimento para CR e Checklist
     * @param planogramCode
     * @param status
     * @param dataReferencia
     * @param unidadeNegocio
     * @return
     */
    private ArrayList<ItensListSortimento> getItensListSortimento(String planogramCode, String nome, int status, Date dataReferencia,
                                                                  String unidadeNegocio, boolean isCLPreco) {
        ArrayList<ItensListSortimento> arrayListItensSortimento = new ArrayList<>();

        if (planogramCode == null) {
            return null;
        }

        String query;

        if(isCLPreco){
            query = getSortimentolistCLPreco(planogramCode, dataReferencia, unidadeNegocio, status);
        } else {
            query = getSortimentolistQuery(planogramCode, nome, dataReferencia, unidadeNegocio, status);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                ItensListSortimento itensSortimento = new ItensListSortimento();
                itensSortimento.setSKU(cursor.getString(cursor.getColumnIndex("COD_SKU")));
                itensSortimento.setName(cursor.getString(cursor.getColumnIndex("DESCRICAO")));
                itensSortimento.setStatus(cursor.getInt(cursor.getColumnIndex("STATUS")));
                itensSortimento.setMarca(cursor.getInt(cursor.getColumnIndex("COD_FAMILIA")));
                itensSortimento.setCod(cursor.getInt(cursor.getColumnIndex("COD_SIMPLIFICADO")));
                itensSortimento.setPeso(cursor.getString(cursor.getColumnIndex("PESO_LIQUIDO")));
                itensSortimento.setUrl(cursor.getString(cursor.getColumnIndex("PRODUTO_IMAGEM")));

                if(cursor.getColumnIndex("SUBSTITUTE") != -1){
                    itensSortimento.setSUBSTITUTE(cursor.getString(cursor.getColumnIndex("SUBSTITUTE")));
                }

                if(cursor.getColumnIndex("COD_VARIANTE") != -1){
                    itensSortimento.setVariante(cursor.getString(cursor.getColumnIndex("COD_VARIANTE")));
                }

                if(cursor.getColumnIndex("DCS_VARIANTE") != -1){
                    itensSortimento.setDescVariante(cursor.getString(cursor.getColumnIndex("DCS_VARIANTE")));
                }

                arrayListItensSortimento.add(itensSortimento);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return isCLPreco ? arrayListItensSortimento:setAggregateSubstitute(arrayListItensSortimento);
    }


    private ArrayList<ItensListSortimento> setAggregateSubstitute(ArrayList<ItensListSortimento> itens) {

        ArrayList<ItensListSortimento> finalList = new ArrayList<>();

        for (ItensListSortimento item: itens){
            finalList.add(item);

            if(!TextUtils.isNullOrEmpty(item.getSUBSTITUTE())){

                boolean isExists = false;

                for(ItensListSortimento itemSort: itens){
                    if(itemSort.getSKU().equals(item.getSUBSTITUTE())) {
                        isExists = true;
                    }
                }

                if(!isExists) {
                    for (ItensListSortimento finalItem : finalList) {
                        if(finalItem.getSKU().equals(item.getSUBSTITUTE())) {
                            isExists = true;
                        }
                    }
                }

                if(!isExists){
                    ItensListSortimento itensSubListSortimento = getSortimentoBySKU(Current.getInstance(getContext()).getUnidadeNegocio().getCodigo(), item.getSUBSTITUTE());
                    if(itensSubListSortimento != null){
                        finalList.add(itensSubListSortimento);
                    }

                }
            }
        }

        return finalList;
    }

    public ItensListSortimento getSortimentoBySKU(String unidadeNegocio, String SKU) {
        StringBuilder query = new StringBuilder();
        query.append(" SELECT");
        query.append("    prd.DESCRICAO, ");
        query.append("    prd.COD_SKU, ");
        query.append("    prd.COD_FAMILIA, ");
        query.append("    prd.COD_SIMPLIFICADO, ");
        query.append("    prd.PRODUTO_IMAGEM, ");
        query.append("    prd.PESO_LIQUIDO ");
        query.append(" FROM TB_DEPRODUTO prd");
        query.append(" WHERE prd.COD_SKU = ? ");
        query.append(" AND prd.COD_UNID_NEGOC = ?");

        String[] args = { SKU, unidadeNegocio};

        ItensListSortimento itensSortimento = new ItensListSortimento();

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                itensSortimento.setSKU(cursor.getString(cursor.getColumnIndex("COD_SKU")));
                itensSortimento.setName(cursor.getString(cursor.getColumnIndex("DESCRICAO")));
                itensSortimento.setStatus(Produto.SORTIMENTO_TIPO_SUBSTITUTO);
                itensSortimento.setMarca(cursor.getInt(cursor.getColumnIndex("COD_FAMILIA")));
                itensSortimento.setCod(cursor.getInt(cursor.getColumnIndex("COD_SIMPLIFICADO")));
                itensSortimento.setPeso(cursor.getString(cursor.getColumnIndex("PESO_LIQUIDO")));
                itensSortimento.setUrl(cursor.getString(cursor.getColumnIndex("PRODUTO_IMAGEM")));

                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return itensSortimento;
    }
  
    public ArrayList<ItensSortimento> getItensSortimentoPedido(Usuario usuario,
                                                         Pedido pedido,
                                                         String planogramCode,
                                                         Date dataReferencia) {

        ArrayList<ItensSortimento> arrayListItensSortimento = new ArrayList<>();

        if (planogramCode == null) {
            return null;
        }

        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);

        String query;
        String[] args;

        if (TipoVenda.DAT.equals(pedido.getCodigoTipoVenda())) {
            query = getDATSortimentoQuery(dataReferencia);
            args = getDATArgs(pedido, planogramCode, dataReferencia);
        } else {
            query = getQueryPedidoSortimento(pedido, planogramCode, dataReferencia,
                    perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                    perfilVenda.isIgnoraExclusividadeProdutoAtivo());
            args = null;
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ItensSortimento itensSortimento = new ItensSortimento();
                itensSortimento.setSKU(cursor.getString(cursor.getColumnIndex("COD_SKU")));
                itensSortimento.setSUBSTITUTE(cursor.getString(cursor.getColumnIndex("SUBSTITUTE")));
                itensSortimento.setPrecoEDV(cursor.getFloat(cursor.getColumnIndex("VAL_PRECO_EDV")));
                itensSortimento.setPrecoPromo(cursor.getFloat(cursor.getColumnIndex("VAL_PRECO_PROMO")));;
                arrayListItensSortimento.add(itensSortimento);

                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return arrayListItensSortimento;
    }

    /**
     * Detalhe do uso dos itens de sortimento no pedido
     * @param usuario
     * @param pedido
     * @param planogramCode
     * @param dataReferencia
     * @return
     */
    public ResumoSortimento getResumoSortimento(Usuario usuario,
                                                Pedido pedido,
                                                String planogramCode,
                                                Date dataReferencia) {
        if (planogramCode == null) {
            return null;
        }

        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);

        String query;
        String[] args;

        ResumoSortimento res = new ResumoSortimento();
        res.setPlanogramCode(planogramCode);

        if (TipoVenda.DAT.equals(pedido.getCodigoTipoVenda())) {
            query = getDATQuery(dataReferencia);
            args = getDATArgs(pedido, planogramCode, dataReferencia);
        } else {
            query = getDefaultQuery(pedido, dataReferencia,
                    perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                    perfilVenda.isIgnoraExclusividadeProdutoAtivo());
            args = getDefaultArgs(pedido, planogramCode);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int status = cursor.isNull(0) ? -1 : cursor.getInt(0);
                boolean itemIncluso = 1 == cursor.getInt(1);

                if(status != -1) {

                    if (TSortimento.OBRIGATORIO == TSortimento.fromInteger(status)) {
                            res.setQuantidadeObrigatorioTotal(res.getQuantidadeObrigatorioTotal() + 1);

                        if (itemIncluso) {
                            res.setQuantidadeObrigatorioInserida(
                                    res.getQuantidadeObrigatorioInserida() + 1);
                        }
                    } else if (TSortimento.RECOMENDADO == TSortimento.fromInteger(status)) {
                        res.setQuantidadeRecomendadaTotal(res.getQuantidadeRecomendadaTotal() + 1);
                        if (itemIncluso) {
                            res.setQuantidadeRecomendadaInserida(
                                    res.getQuantidadeRecomendadaInserida() + 1);
                        }
                    } else if (TSortimento.INOVACAO == TSortimento.fromInteger(status)) {
                        res.setQuantidadeInovacaoTotal(res.getQuantidadeInovacaoTotal() + 1);
                        if (itemIncluso) {
                            res.setQuantidadeInovacaoInserida(
                                    res.getQuantidadeInovacaoInserida() + 1);
                        }
                    }
                }

                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return res;
    }

    private String getDefaultQuery(Pedido pedido, Date dataReferencia,
                                   boolean permiteApenasCadastroProdutoAtivo,
                                   boolean ignoraExclusividadeProdutoAtivo) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);
        String tipoVenda = pedido.getCodigoTipoVenda();

        String query = "select" +
                " (" +
                "   select sort.status from TB_SORT_SORTIMENTO sort" +
                "   where sort.sales_organization = prd.COD_UNID_NEGOC" +
                "   and sort.DEL_FLAG  <> '1'" +
                "   and sort.planogram_code = ?" +
                "   and sort.product_code = prd.COD_SKU" +
                "   and datetime(sort.start_date) <= '" + date + "'" +
                "   order by sort.start_date desc" +
                " ) STATUS," +
                " case when (ite.ITE_INT_ID is null OR ite.ITE_INT_QTD < '0') then 0 else 1 end ITEM_INCLUSO" +
                " from TB_DEPRODUTO prd" +
                " inner join TB_DEPRECO prc" +
                " on prd.COD_UNID_NEGOC = prc.COD_UN_NEGOCIO" +
                " and prd.COD_SKU = prc.COD_PRODUTO" +
                " and prc.COD_CLIENTE = ?" +
                " and prc.DEL_FLAG = '0'" +
                " left join " + DbView.getProdutoBloqueioViewAsSelect(pedido, true) +
                " on prc.COD_UN_NEGOCIO = produto_bloqueio.COD_UN_NEGOCIO" +
                " and prc.COD_CLIENTE = produto_bloqueio.A1_COD" +
                " and prc.COD_PRODUTO = produto_bloqueio.COD_SKU" +
                " left join TBORCAMENTOITEM ite" +
                " on prd.COD_SKU = ite.ITE_TXT_PRODCOD" +
                " and ite.ITE_TXT_ORCID = ?";

        if (TipoVenda.UHT.equals(tipoVenda) || TipoVenda.VCO.equals(tipoVenda)
                || permiteApenasCadastroProdutoAtivo) {
            query += " inner join PRODATIVO_WEBSALES pra" +
                    " on pra.PRD_TXT_SKU = prd.COD_SKU" +
                    " and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                    " and pra.PRD_TXT_TPPED = '" + tipoVenda + "'";

            if (TipoVenda.VCO.equals(tipoVenda)) {
                query += " and pra.PRD_INT_IDGRUPO = " + pedido.getCodigoGrupo();
            }
        }

        query += " where 1 = 1" +
                " and prd.DEL_FLAG = '0'" +
                " and (prd.ITEM_DESCONTINUADO is null and produto_bloqueio.COD_SKU is null)";

        if (!ignoraExclusividadeProdutoAtivo) {
            query += " and not exists (" +
                    "   select * from PRODATIVO_WEBSALES pra" +
                    "   where pra.PRD_TXT_SKU = prd.COD_SKU" +
                    "   and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                    "   and pra.PRD_TXT_TPPED <> '" + tipoVenda + "'" +
                    "   and pra.PRD_TXT_EXCLU = '1'" +
                    " )";
        }

        return query;
    }

    private String getSortimentolistQuery(String planogramCode, String nome, Date dataReferencia, String unidadeNegocio, int status) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);

        String query = "select" +
                " prd.COD_SKU, " +
                " prd.DESCRICAO, " +
                " prd.COD_FAMILIA, " +
                " prd.COD_SIMPLIFICADO, " +
                " prd.PESO_LIQUIDO, " +
                " prd.PRODUTO_IMAGEM, " +
                " prd.DESCRICAO, " +
                " (" +
                "   select sort.PRODUCT_SUBSTITUTE from TB_SORT_SORTIMENTO sort" +
                "   where sort.sales_organization = prd.COD_UNID_NEGOC" +
                "   and sort.DEL_FLAG <> '1'" +
                "   and sort.planogram_code = '" + planogramCode + "'" +
                "   and sort.product_code = prd.COD_SKU" +
                "   and datetime(sort.start_date) <= '" + date + "'" +
                "   order by sort.start_date desc" +
                " ) SUBSTITUTE, " +
                " (" +
                "   select sort.status from TB_SORT_SORTIMENTO sort" +
                "   where sort.sales_organization = prd.COD_UNID_NEGOC" +
                "   and sort.DEL_FLAG <> '1'" +
                "   and sort.planogram_code = '" + planogramCode + "'" +
                "   and sort.product_code = prd.COD_SKU" +
                "   and datetime(sort.start_date) <= '" + date + "'" +
                "   order by sort.start_date desc" +
                " ) STATUS " +
                " from TB_DEPRODUTO prd" +
                " where prd.COD_UNID_NEGOC = '" + unidadeNegocio + "'";

                if (!TextUtils.isNullOrEmpty(nome)) {
                    query += "and prd.DESCRICAO LIKE '%" + nome + "%' ";
                }

                query += " and prd.DEL_FLAG = '0'";
                if(status > 0){
                    query +=  " and STATUS = '" + status + "'";
                    query += " order by prd.COD_FAMILIA ";
                } else {
                    query +=  " and STATUS notnull";
                    query += " order by STATUS, prd.DESCRICAO  ";
                }
                
        return query;
    }


    /**
     * Retorna os itens do sortimento para o componente preço do ckeclist
     * @param planogramCode
     * @param dataReferencia
     * @param unidadeNegocio
     * @param status
     * @return
     */
    private String getSortimentolistCLPreco(String planogramCode, Date dataReferencia, String unidadeNegocio, int status) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" CASE WHEN S.PRODUCT_SUBSTITUTE = P.COD_SKU then '4' else  '1'  end as STATUS, ");
        query.append(" MIN(P.COD_SKU) AS COD_SKU, ");
        query.append(" V.COD_VARIANTE AS COD_VARIANTE, ");
        query.append(" V.DCS_VARIANTE AS DCS_VARIANTE, ");
        query.append(" MIN(P.COD_FAMILIA) AS COD_FAMILIA, ");
        query.append(" MIN(P.COD_SIMPLIFICADO) AS COD_SIMPLIFICADO, ");
        query.append(" MIN(P.PESO_LIQUIDO) AS PESO_LIQUIDO, ");
        query.append(" MIN(P.DESCRICAO) AS DESCRICAO, ");
        query.append(" MIN(P.PRODUTO_IMAGEM) AS PRODUTO_IMAGEM ");
        query.append("FROM TB_SORT_SORTIMENTO S ");
        query.append("INNER JOIN TB_DEPRODUTO ");
        query.append(" P ON (S.PRODUCT_CODE = COD_SKU OR S.PRODUCT_SUBSTITUTE = COD_SKU) AND S.SALES_ORGANIZATION = P.COD_UNID_NEGOC ");
        query.append("INNER JOIN TB_DEVARIANTE  ");
        query.append(" V ON V.COD_VARIANTE = P.COD_VARIANTE ");
        query.append("WHERE ");
        query.append(" S.SALES_ORGANIZATION = '" + unidadeNegocio + "' ");
        query.append(" AND S.PLANOGRAM_CODE = '" + planogramCode + "' ");
        query.append(" AND datetime(S.START_DATE) <= '" + date + "' ");
        query.append(" AND S.STATUS = '" + TSortimento.OBRIGATORIO.getValue() + "' ");
        query.append("GROUP BY S.PRODUCT_SUBSTITUTE, STATUS, DCS_VARIANTE, V.COD_VARIANTE ");
        query.append("ORDER BY S.STATUS, P.DESCRICAO ");

        return query.toString();
    }


    private String getQueryPedidoSortimento(Pedido pedido, String planogramCode, Date dataReferencia,
                                      boolean permiteApenasCadastroProdutoAtivo,
                                      boolean ignoraExclusividadeProdutoAtivo) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);
        String tipoVenda = pedido.getCodigoTipoVenda();

        String query = "select" +
                " prd.COD_SKU, prc.VAL_PRECO_PROMO, prc.VAL_PRECO_EDV,  " +
                " (" +
                "   select sort.PRODUCT_SUBSTITUTE from TB_SORT_SORTIMENTO sort" +
                "   where sort.sales_organization = prd.COD_UNID_NEGOC" +
                "   and sort.DEL_FLAG <> '1'" +
                "   and sort.planogram_code = '" + planogramCode + "'" +
                "   and sort.product_code = prd.COD_SKU" +
                "   and datetime(sort.start_date) <= '" + date + "'" +
                "   order by sort.start_date desc" +
                " ) SUBSTITUTE" +
                " from TB_DEPRODUTO prd" +
                " inner join TB_DEPRECO prc" +
                " on prd.COD_UNID_NEGOC = prc.COD_UN_NEGOCIO" +
                " and prd.COD_SKU = prc.COD_PRODUTO" +
                " and prc.COD_CLIENTE = '" + pedido.getCliente().getCodigo() + "'" +
                " and prc.DEL_FLAG = '0'" +
                " left join " + DbView.getProdutoBloqueioViewAsSelect(pedido, true) +
                " on prc.COD_UN_NEGOCIO = produto_bloqueio.COD_UN_NEGOCIO" +
                " and prc.COD_CLIENTE = produto_bloqueio.A1_COD" +
                " and prc.COD_PRODUTO = produto_bloqueio.COD_SKU" +
                " left join TBORCAMENTOITEM ite" +
                " on prd.COD_SKU = ite.ITE_TXT_PRODCOD" +
                " and ite.ITE_TXT_ORCID = '" + pedido.getCodigo() + "'" +
                " left join TB_SORT_SORTIMENTO sort" +
                " on prd.COD_SKU = sort.PRODUCT_CODE";

        if (TipoVenda.UHT.equals(tipoVenda) || TipoVenda.VCO.equals(tipoVenda)
                || permiteApenasCadastroProdutoAtivo) {
            query += " inner join PRODATIVO_WEBSALES pra" +
                    " on pra.PRD_TXT_SKU = prd.COD_SKU" +
                    " and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                    " and pra.PRD_TXT_TPPED = '" + tipoVenda + "'";

            if (TipoVenda.VCO.equals(tipoVenda)) {
                query += " and pra.PRD_INT_IDGRUPO = " + pedido.getCodigoGrupo();
            }
        }

        query += " where 1 = 1 and sort.PLANOGRAM_CODE = '" + planogramCode + "'" +
                " and prd.DEL_FLAG = '0'" +
                " and sort.DEL_FLAG <> '1'" +
                " and (prd.ITEM_DESCONTINUADO is null and produto_bloqueio.COD_SKU is null)";

        if (!ignoraExclusividadeProdutoAtivo) {
            query += " and not exists (" +
                    "   select * from PRODATIVO_WEBSALES pra" +
                    "   where pra.PRD_TXT_SKU = prd.COD_SKU" +
                    "   and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                    "   and pra.PRD_TXT_TPPED <> '" + tipoVenda + "'" +
                    "   and pra.PRD_TXT_EXCLU = '1'" +
                    " )";
        }

        query += " group by prd.COD_SKU";

        return query;
    }


    private String[] getDefaultArgs(Pedido pedido, String planogramCode) {
        return new String[] { planogramCode, pedido.getCodigoCliente(), pedido.getCodigo() };
    }

    private String getDATQuery(Date dataReferencia) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);

        String query = "select" +
                " (" +
                "   select sort.PRODUCT_SUBSTITUTE from TB_SORT_SORTIMENTO sort" +
                "   where sort.sales_organization = prd.COD_UNID_NEGOC" +
                "   and sort.DEL_FLAG <> '1'" +
                "   and sort.planogram_code = ?" +
                "   and sort.product_code = prd.COD_SKU" +
                "   and datetime(sort.start_date) <= '" + date + "'" +
                "   order by sort.start_date desc" +
                " ) SUBSTITUTE" +
                " case when (ite.ITE_INT_ID is null OR ite.ITE_INT_QTD < '0') then 0 else 1 end ITEM_INCLUSO" +

                " from TB_DEPRODUTO prd" +
                " left join TBORCAMENTOITEM ite" +
                " on prd.COD_SKU = ite.ITE_TXT_PRODCOD" +
                " and ite.ITE_TXT_ORCID = ?" +

                " where 1 = 1" +
                " and prd.DEL_FLAG = '0'" +
                " and prd.DEL_FLAG = '0'" +
                "  " +
                " and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'" +
                " and exists (" +
                "   select * from ESTOQUEDAT_WEBSALES etq" +
                "   where etq.EST_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                "   and etq.EST_TXT_SKU = prd.COD_SKU" +
                "   and etq.EST_TXT_PLANTA = ?" +
                "   and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM" +
                "   and etq.EST_INT_QTD > etq.EST_INT_QTDUT" +
                " )";

        return query;
    }

    private String getDATSortimentoQuery(Date dataReferencia) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);

        String query = "select" +

                " (" +
                "   select sort.status from TB_SORT_SORTIMENTO sort" +
                "   where sort.sales_organization = prd.COD_UNID_NEGOC" +
                "   and sort.DEL_FLAG <> '1'" +
                "   and sort.planogram_code = ?" +
                "   and sort.product_code = prd.COD_SKU" +
                "   and datetime(sort.start_date) <= '" + date + "'" +
                "   order by sort.start_date desc" +
                " ) STATUS," +
                " case when (ite.ITE_INT_ID is null OR ite.ITE_INT_QTD < '0') then 0 else 1 end ITEM_INCLUSO" +

                " from TB_DEPRODUTO prd" +
                " left join TBORCAMENTOITEM ite" +
                " on prd.COD_SKU = ite.ITE_TXT_PRODCOD" +
                " and ite.ITE_TXT_ORCID = ?" +

                " where 1 = 1" +
                " and prd.DEL_FLAG = '0'" +
                " and prd.DEL_FLAG = '0'" +
                "  " +
                " and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'" +
                " and exists (" +
                "   select * from ESTOQUEDAT_WEBSALES etq" +
                "   where etq.EST_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                "   and etq.EST_TXT_SKU = prd.COD_SKU" +
                "   and etq.EST_TXT_PLANTA = ?" +
                "   and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM" +
                "   and etq.EST_INT_QTD > etq.EST_INT_QTDUT" +
                " )";

        query += " group by prd.COD_SKU";

        return query;
    }

    private String[] getDATArgs(Pedido pedido, String planogramCode, Date dataReferencia) {
        return new String[] { planogramCode, pedido.getCodigo(), pedido.getCodigoUnidadeNegocio(),
                pedido.getCodigoPlanta(), toTextToCompareDateInESTOQUEDAT_WEBSALES(dataReferencia) };
    }

    private String toTextToCompareDateInESTOQUEDAT_WEBSALES(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(date);
    }


}
