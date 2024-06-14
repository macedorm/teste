package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.cursor.ProdutoCursorWrapper;
import br.com.jjconsulting.mobile.dansales.model.EstoqueDAT;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.QuantidadeSugerida;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ItemPedidoDao extends BaseDansalesDao {

    private ProdutoDao produtoDao;
    private SortimentoDao sortimentoDao;

    public ItemPedidoDao(Context context) {
        super(context);
        produtoDao = new ProdutoDao(context);
        sortimentoDao = new SortimentoDao(context);
    }

    private static ContentValues getContentValuesSimple(ItemPedido itemPedido) {
        ContentValues values = new ContentValues();

        values.put("ITE_INT_QTD", itemPedido.getQuantidade());
        values.put("ITE_TXT_OBS", itemPedido.getObservacao());
        values.put("ITE_TXT_CODSOL", itemPedido.getCodSol());
        values.put("ITE_FLO_PRECOVENDA", itemPedido.getPrecoVenda());
        values.put("ITE_INT_QTD_MIGRADA", itemPedido.getQtdSug());

        values.put("ITE_FLO_PRECOVENDA", itemPedido.getPrecoVenda());
        values.put("ITE_FLO_VLRTOTAL", itemPedido.getValorTotal());
        values.put("ITE_FLO_DESCVLR", itemPedido.getValorDesconto());
        values.put("ITE_FLO_DESCPERC", itemPedido.getPercentualDesconto());
        values.put("ITE_FLO_DESC_CLIE", itemPedido.getDescontoCliente());
        values.put("ITE_FLO_DESC_FLAGSHIP", itemPedido.getDescontoFlagship());
        values.put("ITE_FLO_DESC_GERENCIAL", itemPedido.getDescontoGerencial());
        values.put("ITE_FLO_DESC_PROMOCIONAL", itemPedido.getDescontoPromocional());
        values.put("ITE_FLO_DESC_CONDPAG", itemPedido.getDescontoCondicaoPagamento());
        values.put("ITE_FLO_DESC_EDV", itemPedido.getDescontoEdv());
        values.put("ITE_FLO_DESC_TATICO", itemPedido.getDescontoTatico());

        values.put("ITE_FLO_BB_PRCTAB", itemPedido.getBbPrecoTabela());
        values.put("ITE_FLO_BB_PRCLOC", itemPedido.getBbPrecoLoc());
        values.put("ITE_FLO_BB_FATOR", itemPedido.getBbFator());


        values.put("ITE_TXT_PRODEMP", itemPedido.getCodigoEmpresaProduto());
        values.put("ITE_TXT_PRODFILIAL", itemPedido.getCodigoFilialProduto());
        values.put("ITE_FLO_PRODPRECOTB", itemPedido.getPrecoTabelaProduto());
        values.put("ITE_FLO_PRODPESO", itemPedido.getPesoProduto());
        values.put("ITE_FLO_PRODPESOLIQ", itemPedido.getPesoLiquidoProduto());
        values.put("ITE_INT_PROD_STATUS", itemPedido.getStatusProduto());
        values.put("DEL_FLAG", "0");

        return values;
    }

    private static ContentValues getContentValues(ItemPedido itemPedido) {
        ContentValues values = new ContentValues();

        values.put("ITE_INT_ID", itemPedido.getId());
        values.put("ITE_TXT_ORCID", itemPedido.getCodigoPedido());
        values.put("ITE_INT_QTD", itemPedido.getQuantidade());
        values.put("ITE_TXT_OBS", itemPedido.getObservacao());
        values.put("ITE_TXT_LOTE", itemPedido.getLote());
        values.put("ITE_TXT_ITSOL", itemPedido.getItSol());
        values.put("ITE_TXT_CODSOL", itemPedido.getCodSol());
        values.put("ITE_FLO_PRECOVENDA", itemPedido.getPrecoVenda());
        values.put("ITE_INT_QTD_MIGRADA", itemPedido.getQtdSug());

        values.put("ITE_FLO_PRECOVENDA", itemPedido.getPrecoVenda());
        values.put("ITE_FLO_VLRTOTAL", itemPedido.getValorTotal());
        values.put("ITE_FLO_DESCVLR", itemPedido.getValorDesconto());
        values.put("ITE_FLO_DESCPERC", itemPedido.getPercentualDesconto());
        values.put("ITE_FLO_DESC_CLIE", itemPedido.getDescontoCliente());
        values.put("ITE_FLO_DESC_FLAGSHIP", itemPedido.getDescontoFlagship());
        values.put("ITE_FLO_DESC_GERENCIAL", itemPedido.getDescontoGerencial());
        values.put("ITE_FLO_DESC_PROMOCIONAL", itemPedido.getDescontoPromocional());
        values.put("ITE_FLO_DESC_CONDPAG", itemPedido.getDescontoCondicaoPagamento());
        values.put("ITE_FLO_DESC_EDV", itemPedido.getDescontoEdv());
        values.put("ITE_FLO_DESC_TATICO", itemPedido.getDescontoTatico());

        values.put("ITE_FLO_BB_PRCTAB", itemPedido.getBbPrecoTabela());
        values.put("ITE_FLO_BB_PRCLOC", itemPedido.getBbPrecoLoc());
        values.put("ITE_FLO_BB_FATOR", itemPedido.getBbFator());


        values.put("ITE_TXT_PRODCOD", itemPedido.getCodigoProduto());
        values.put("ITE_TXT_PRODEMP", itemPedido.getCodigoEmpresaProduto());
        values.put("ITE_TXT_PRODFILIAL", itemPedido.getCodigoFilialProduto());
        values.put("ITE_FLO_PRODPRECOTB", itemPedido.getPrecoTabelaProduto());
        values.put("ITE_FLO_PRODPESO", itemPedido.getPesoProduto());
        values.put("ITE_FLO_PRODPESOLIQ", itemPedido.getPesoLiquidoProduto());
        values.put("ITE_INT_PROD_STATUS", itemPedido.getStatusProduto());
        values.put("DEL_FLAG", "0");

        return values;
    }

    public void insert(ItemPedido item) {
        ContentValues contentValues = getContentValues(item);
        SQLiteDatabase database = getDb();
        database.insert("TBORCAMENTOITEM", null, contentValues);
        database.close();
    }

    public void update(ItemPedido item) {
        ContentValues contentValues = getContentValues(item);
        SQLiteDatabase database = getDb();
        database.update("TBORCAMENTOITEM", contentValues,
                "ITE_INT_ID = " + String.valueOf(item.getId()), null);
        database.close();
    }

    public void updateSimple(ItemPedido item) {
        ContentValues contentValues = getContentValuesSimple(item);
        SQLiteDatabase database = getDb();
        database.update("TBORCAMENTOITEM", contentValues,
                "ITE_INT_ID = " + String.valueOf(item.getId()), null);
    }

    public void updateAllItemCodPed(String codigoAntigoPedido, String novoCodigoPedido) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ITE_TXT_ORCID", novoCodigoPedido);
        SQLiteDatabase database = getDb();
        database.update("TBORCAMENTOITEM", contentValues,
                "ITE_TXT_ORCID = ?", new String[]{codigoAntigoPedido});
    }

    public void deleteAllItensPedido(String codigoPedido) {
        SQLiteDatabase database = getDb();
        database.delete("TBORCAMENTOITEM",
                "ITE_TXT_ORCID = ? ", new String[]{codigoPedido});
    }

    public void delete(int itemId) {
        SQLiteDatabase database = getDb();
        database.delete("TBORCAMENTOITEM",
                "ITE_INT_ID = " + String.valueOf(itemId), null);
    }

    public void delete(String codigoPedido) {
        SQLiteDatabase database = getDb();
        database.delete("TBORCAMENTOITEM", "ITE_TXT_ORCID = ?",
                new String[]{codigoPedido});
    }

    public ItemPedido get(Usuario usuario, Pedido pedido, int codigoItem, boolean getPreco) {
        String whereClause = "and ite.ITE_INT_ID = " + String.valueOf(codigoItem);
        ArrayList<ItemPedido> itens = query(whereClause, null, null);
        return itens.size() > 0 ? setAggregatedData(usuario, pedido, itens.get(0), getPreco) : null;
    }

    public int getQuantidade(ItemPedido item, int quantidade, Pedido pedido) {
        if (pedido.getUnidadeMedida().equals(Pedido.UNIDADE_MEDIDA_UNIDADE)) {
            int multiplo = item.getProduto().getMultiplo();
            quantidade = quantidade * multiplo;
        }

        return quantidade;

    }

    public ArrayList<ItemPedido> createItemSugerido(ArrayList<ItensSortimento> itensSortimento, Usuario usuario, Pedido pedido) {
        ArrayList<ItemPedido> itens = new ArrayList<>();
        int maxId = getMaxId();

        PedidoBusiness pedidoBusiness = new PedidoBusiness();
        ArrayList<QuantidadeSugerida> quantidadesSugeridas = getQuantidadeItemSugerido(pedido);

        if (itensSortimento != null) {

            for (ItensSortimento itemSortimento : itensSortimento) {
                ItemPedido itemPedido = newItem(usuario, pedido, maxId, itemSortimento.getSKU());
                if (itemPedido == null)
                    continue;

                int qtdSub = -1;
                if (quantidadesSugeridas.size() > 0) {

                    for (int ind = 0; ind < quantidadesSugeridas.size(); ind++) {
                        if (itemSortimento.getSKU().equals(quantidadesSugeridas.get(ind).getSku())) {
                            quantidadesSugeridas.get(ind).setItemNovo(true);
                            itemPedido.setQuantidade(getQuantidade(itemPedido, quantidadesSugeridas.get(ind).getQuantidade(), pedido));

                            if (itemPedido.getQuantidade() > 0) {
                                itemPedido = calculaItem(itemPedido, pedido);
                            }
                        } else {
                            if (!TextUtils.isNullOrEmpty(itemSortimento.getSUBSTITUTE())) {
                                if (itemSortimento.getSUBSTITUTE().equals(quantidadesSugeridas.get(ind).getSku())) {
                                    quantidadesSugeridas.get(ind).setItemNovo(true);
                                    qtdSub = quantidadesSugeridas.get(ind).getQuantidade();
                                }
                            }
                        }
                    }
                }

                itens.add(itemPedido);
                maxId += 1;
                if (!TextUtils.isNullOrEmpty(itemSortimento.getSUBSTITUTE())) {
                    if (pedidoBusiness.checkSubstituto(itens, itensSortimento, itemSortimento.getSUBSTITUTE())) {
                        ItemPedido itemPedidoSub = newItem(usuario, pedido, maxId, itemSortimento.getSUBSTITUTE());
                        if (itemPedidoSub != null) {
                            if (qtdSub > 0) {
                                itemPedidoSub.setQuantidade(getQuantidade(itemPedidoSub, qtdSub, pedido));
                                itemPedidoSub = calculaItem(itemPedidoSub, pedido);
                            }

                            itens.add(itemPedidoSub);
                            maxId += 1;
                        }
                    }
                }

            }
        }

        for (QuantidadeSugerida item : quantidadesSugeridas) {
            if (!item.isItemNovo()) {
                ItemPedido itemPedido = newItem(usuario, pedido, maxId, item.getSku());

                if (itemPedido != null) {
                    itemPedido.setQuantidade(getQuantidade(itemPedido, item.getQuantidade(), pedido));
                    itens.add(calculaItem(itemPedido, pedido));
                    maxId += 1;
                }
            }
        }

        for (ItemPedido item : itens) {
            insert(item);
        }

        return itens;
    }

    private ItemPedido calculaItem(ItemPedido itemPedido, Pedido pedido) {
        PedidoBusiness pedidoBusiness = new PedidoBusiness();

        if (itemPedido != null && itemPedido.getQuantidade() > 0) {
            if (TipoVenda.REB.equals(pedido.getCodigoTipoVenda())) {
                itemPedido = pedidoBusiness.buildNewItemReb(pedido, itemPedido, 1);
            } else {
                itemPedido = pedidoBusiness.buildNewItem(pedido, itemPedido);
            }
        }

        return itemPedido;
    }

    private ItemPedido newItem(Usuario usuario, Pedido pedido, int maxId, String cod) {
        PedidoBusiness pedidoBusiness = new PedidoBusiness();

        ItemPedido item = pedidoBusiness.createNewItem(maxId, pedido);
        item.setProduto(produtoDao.getByCod(usuario, pedido, cod, "", true, true));

        if (item.getProduto() != null) {
            item.setCodigoProduto(item.getProduto().getCodigo());
            item.setQuantidade(-1);
            return item;
        } else {
            return null;
        }

    }

    public ArrayList<ItemPedido> getAll(Usuario usuario, Pedido pedido, boolean getPreco) {
        String whereClause = "and ite.ITE_TXT_ORCID = ?";
        String[] whereArgs = {pedido.getCodigo()};
        String orderBy = " order by ite.ITE_INT_ID desc";

        if (TipoVenda.DAT.equals(pedido.getCodigoTipoVenda())) {
            return setAggregatedData(usuario, pedido, query(whereClause, whereArgs, orderBy),
                    getPreco);
        } else {
            PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);
            ArrayList<ItemPedido> items = queryWithProduto(pedido, getPreco,
                    perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                    perfilVenda.isIgnoraExclusividadeProdutoAtivo(), whereClause, whereArgs, orderBy);

            if (!getPreco)
                return items;

            boolean isSortimentoHabilitado = perfilVenda.isSortimentoHabilitado();

            if (!isSortimentoHabilitado || pedido.getCodigoSortimento() == null) {
                return items;
            }

            Date dataReferencia = new Date();

            for (ItemPedido item : items) {
                setSortimento(pedido, item, dataReferencia);
            }

            return items;
        }
    }

    public ArrayList<ItemPedido> getAllAprovacaoOuLiberacao(Usuario usuario, Pedido pedido) {
        String whereClause = "and ite.ITE_TXT_ORCID = ?";
        String[] whereArgs = {pedido.getCodigo()};
        String orderBy = " order by ite.ITE_INT_ID desc";

        // essas configurações irão aderir a um valor padrão para aprovação,
        // ignorando o perfil pois é apenas leitura e deve visualizar o pedido
        // como é, até porque o perfil de venda do vendedor pode estar
        // diferente do aprovador (o que irá gerar inconsistência na visualização)
        boolean getPreco = false;
        boolean permiteApenasCadastroProdutoAtivo = false;
        boolean ignoraExclusividadeProdutoAtivo = true;

        if (TipoVenda.DAT.equals(pedido.getCodigoTipoVenda())) {
            return setAggregatedData(usuario, pedido, query(whereClause, whereArgs, orderBy),
                    getPreco);
        } else {
            ArrayList<ItemPedido> items = queryWithProduto(pedido, getPreco,
                    permiteApenasCadastroProdutoAtivo,
                    ignoraExclusividadeProdutoAtivo, whereClause, whereArgs, orderBy);

            return items;
        }
    }

    private void setSortimento(Pedido pedido, ItemPedido item, Date dataReferencia) {
        Produto produto = item.getProduto();

        Integer sortimento = sortimentoDao.getTipoSortimento(pedido.getCodigoUnidadeNegocio(),
                pedido.getCodigoSortimento(), produto.getCodigo(), dataReferencia);

        if (sortimento == null)
            return;

        produto.setTipoSortimento(sortimento);
        produto.setDescricaoSortimento(
                produto.getTipoSortimento() == Produto.SORTIMENTO_TIPO_OBRIGATORIO ?
                        "obrigatório" : "recomendado");
    }

    public int getMaxId() {
        String query = "select max(ITE_INT_ID) from TBORCAMENTOITEM";

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.isNull(0)) {
                    return 0;
                }

                return cursor.getInt(0);
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return 0;
    }

    public ArrayList<QuantidadeSugerida> getQuantidadeItemSugerido(Pedido pedido) {
        ArrayList<QuantidadeSugerida> quantidadeSugerida = new ArrayList<>();

        String query = "select *" +
                " from TB_SORT_CARREGAMENTO sort" +
                " where sort.CUSTOMERCODE = ?" +
                " and sort.START_DATE <= ?" +
                " and sort.SALES_ORGANIZATION = ?" +
                " and sort.TIPO_OPERACAO = ?";

        String[] whereArgs = {pedido.getCliente().getCodigo(), FormatUtils.toTextToCompareDateInSQlite(new Date()),
                pedido.getCodigoUnidadeNegocio(),
                pedido.getTipoVenda().getCodigo()};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                QuantidadeSugerida quantidade = new QuantidadeSugerida();
                quantidade.setQuantidade(cursor.getInt(cursor.getColumnIndex("AMOUNT")));
                quantidade.setSku(cursor.getString(cursor.getColumnIndex("PRODUCT_CODE")));
                quantidadeSugerida.add(quantidade);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return quantidadeSugerida;
    }

    public boolean isProdutoAlreadyInThePedido(String codigoPedido, String codigoProduto, String lote) {
        String query = "select count(*) QTD" +
                " from TBORCAMENTOITEM ite" +
                " where ite.ITE_TXT_ORCID = ?" +
                " and ite.ITE_TXT_PRODCOD = ?" +
                " and ite.ITE_TXT_LOTE = ?";
        String[] whereArgs = {codigoPedido, codigoProduto, lote};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return true; // forcing error
    }

    public boolean isProdutoAlreadyInThePedido(String codigoPedido, String codigoProduto) {
        String query = "select count(*) QTD" +
                " from TBORCAMENTOITEM ite" +
                " where ite.ITE_TXT_ORCID = ?" +
                " and ite.ITE_TXT_PRODCOD = ?";
        String[] whereArgs = {codigoPedido, codigoProduto};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return true; // forcing error
    }

    public boolean isProdutoWithLoteAlreadyInThePedido(String codigoPedido, String codigoProduto,
                                                       String lote) {
        String query = "select count(*) QTD" +
                " from TBORCAMENTOITEM ite" +
                " where ite.ITE_TXT_ORCID = ?" +
                " and ite.ITE_TXT_PRODCOD = ?" +
                " and ite.ITE_TXT_LOTE = ?";
        String[] whereArgs = {codigoPedido, codigoProduto, lote};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return cursor.getInt(0) > 0;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return true; // forcing error
    }

    public Integer getQuantidadePedidoAnterior(String codigoUsuario, String codigoPedido,
                                               String codigoProduto) {
        String query = "select" +
                " case" +
                "   when ped.ORC_TXT_UNMED = 'PAC' then ite.ITE_INT_QTD * 1" +
                "   else ite.ITE_INT_QTD * prd.QTD_CAIXA" +
                " end" +
                " from TBORCAMENTO ped" +
                " inner join TBORCAMENTOITEM ite" +
                " on ped.ORC_TXT_ID = ite.ITE_TXT_ORCID" +
                " inner join TB_DEPRODUTO prd" +
                " on ite.ITE_TXT_PRODCOD = prd.COD_SKU" +
                " and (ifnull(ped.ORC_TXT_EMP, '') || ifnull(ped.ORC_TXT_FILIAL, ''))" +
                "   = prd.COD_UNID_NEGOC" +
                " where ped.ORC_TXT_VENDCOD = ?" +
                " and ite.ITE_TXT_PRODCOD = ?" +
                " and ite.ITE_INT_QTD > -1" +
                " and ped.ORC_DAT_ORCAMENTO < (" +
                "   select ped2.ORC_DAT_ORCAMENTO from TBORCAMENTO ped2" +
                "   where ped2.ORC_TXT_ID = ?" +
                " )" +
                " order by ped.ORC_DAT_ORCAMENTO desc" +
                " limit 1";
        String[] whereArgs = {codigoUsuario, codigoProduto, codigoPedido};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.isNull(0)) {
                    return null;
                }

                return cursor.getInt(0);
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return null;
    }

    public Integer getQuantidadeMediaPorPedido(String codigoUsuario, String codigoProduto) {
        String query = "select" +
                " avg(" +
                "   case" +
                "       when ped.ORC_TXT_UNMED = 'PAC' then ite.ITE_INT_QTD * 1" +
                "       else ite.ITE_INT_QTD * prd.QTD_CAIXA" +
                "   end" +
                " )" +
                " from TBORCAMENTO ped" +
                " inner join TBORCAMENTOITEM ite" +
                " on ped.ORC_TXT_ID = ite.ITE_TXT_ORCID" +
                " inner join TB_DEPRODUTO prd" +
                " on ite.ITE_TXT_PRODCOD = prd.COD_SKU" +
                " and (ifnull(ped.ORC_TXT_EMP, '') || ifnull(ped.ORC_TXT_FILIAL, ''))" +
                "   = prd.COD_UNID_NEGOC" +
                " where ped.ORC_TXT_VENDCOD = ?" +
                " and ite.ITE_INT_QTD != ?" +
                " and ite.ITE_TXT_PRODCOD = ?";
        String[] whereArgs = {codigoUsuario, "-1", codigoProduto};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (cursor.isNull(0)) {
                    return null;
                }

                return cursor.getInt(0);
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return null;
    }

    private ItemPedido setAggregatedData(Usuario usuario, Pedido pedido, ItemPedido item,
                                         boolean getPreco) {
        boolean getEstoqueDAT = TipoVenda.DAT.equals(pedido.getCodigoTipoVenda())
                && pedido.getCodigoPlanta() != null && pedido.getUnidadeMedida() != null
                && item.getLote() != null;

        boolean isSortimentoAvailable = usuario.getPerfil()
                .getPerfilVenda(pedido)
                .isSortimentoHabilitado();

        if (item.getProduto() == null) {
            Produto produto = produtoDao.getByCod(usuario, pedido, item.getCodigoProduto(),
                    null, getPreco, isSortimentoAvailable);
            item.setProduto(produto);
        }

        if (item.getProduto() == null)
            return item;

        if (getEstoqueDAT) {
            EstoqueDAT estoqueDAT = produtoDao.getEstoqueDAT(pedido.getCodigoUnidadeNegocio(),
                    item.getCodigoProduto(), pedido.getCodigoPlanta(), item.getLote(),
                    pedido.getUnidadeMedida());

            item.getProduto().setEstoqueDAT(estoqueDAT);
        }

        return item;
    }

    private ArrayList<ItemPedido> setAggregatedData(Usuario usuario, Pedido pedido,
                                                    ArrayList<ItemPedido> itens, boolean getPreco) {
        boolean getEstoqueDAT = TipoVenda.DAT.equals(pedido.getCodigoTipoVenda())
                && pedido.getCodigoPlanta() != null && pedido.getUnidadeMedida() != null;

        boolean isSortimentoHabilitado = usuario.getPerfil()
                .getPerfilVenda(pedido)
                .isSortimentoHabilitado();

        for (ItemPedido item : itens) {
            if (item.getProduto() == null) {
                Produto produto = produtoDao.getByCod(usuario, pedido, item.getCodigoProduto(),
                        null, getPreco, isSortimentoHabilitado);
                item.setProduto(produto);

                if (produto == null)
                    continue;
            }

            if (getEstoqueDAT && item.getLote() != null) {
                EstoqueDAT estoqueDAT = produtoDao.getEstoqueDAT(pedido.getCodigoUnidadeNegocio(),
                        item.getCodigoProduto(), pedido.getCodigoPlanta(), item.getLote(),
                        pedido.getUnidadeMedida());
                item.getProduto().setEstoqueDAT(estoqueDAT);
            }
        }

        return itens;
    }

    private ArrayList<ItemPedido> query(String whereClause, String[] args, String orderBy) {
        ArrayList<ItemPedido> itens = new ArrayList<>();

        String query = "select" +
                " ite.*" +
                " from TBORCAMENTOITEM ite" +
                " inner join TBORCAMENTO orc" +
                " on orc.ORC_TXT_ID = ite.ITE_TXT_ORCID" +
                " where ite.DEL_FLAG <> '1' ";

        if (whereClause != null) {
            query += " " + whereClause;
        }

        if (orderBy != null) {
            query += " " + orderBy;
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itens.add(new ItemPedidoDao.ItemPedidoCursorWrapper(cursor).getItemPedido());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return itens;
    }

    private ArrayList<ItemPedido> queryWithProduto(Pedido pedido, boolean getPreco,
                                                   boolean permiteApenasCadastroProdutoAtivo,
                                                   boolean ignoraExclusividadeProdutoAtivo,
                                                   String whereClause, String[] args,
                                                   String orderBy) {
        ArrayList<ItemPedido> itens = new ArrayList<>();

        final String unidadeNegocio = pedido.getCodigoUnidadeNegocio();

        String fromTable = " from TBORCAMENTOITEM ite" +
                " inner join TBORCAMENTO orc" +
                " on orc.ORC_TXT_ID = ite.ITE_TXT_ORCID";

        DbQueryProduto.OnJoiningListener onJoiningListener = (from) -> {
            from.append(" on ite.ITE_TXT_PRODCOD = prd.COD_SKU");
            from.append(" and " + unidadeNegocio + " = prd.COD_UNID_NEGOC");
        };

        DbQueryProduto query = new DbQueryProduto(DbQueryProduto.SelectionType.Join, fromTable,
                onJoiningListener);
        query.buildQuery(pedido, getPreco, permiteApenasCadastroProdutoAtivo,
                ignoraExclusividadeProdutoAtivo);

        query.getWhere().append(" AND ite.DEL_FLAG <> '1' ");

        query.getSelect().append(", ite.*");

        if (whereClause != null) {
            query.getWhere().append(" " + whereClause);
        }

        if (orderBy != null) {
            query.getOrderBy().append(" " + orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.getQuery(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ItemPedido item = new ItemPedidoDao.ItemPedidoCursorWrapper(cursor).getItemPedido();
                Produto produto = new ProdutoCursorWrapper(cursor, pedido, getPreco).getProduto();

                if (item != null && produto != null) {
                    item.setProduto(produto);
                }

                itens.add(item);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return itens;
    }

    public class ItemPedidoCursorWrapper extends CursorWrapper {

        public ItemPedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public ItemPedido getItemPedido() {
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setId(getInt(getColumnIndex("ITE_INT_ID")));
            itemPedido.setQtdSug(getInt(getColumnIndex("ITE_INT_QTD_MIGRADA")));
            itemPedido.setCodigoPedido(getString(getColumnIndex("ITE_TXT_ORCID")));
            itemPedido.setQuantidade(getInt(getColumnIndex("ITE_INT_QTD")));
            itemPedido.setObservacao(getString(getColumnIndex("ITE_TXT_OBS")));
            itemPedido.setLote(getString(getColumnIndex("ITE_TXT_LOTE")));
            itemPedido.setItSol(getString(getColumnIndex("ITE_TXT_ITSOL")));
            itemPedido.setCodSol(getString(getColumnIndex("ITE_TXT_CODSOL")));

            itemPedido.setPrecoVenda(getDouble(getColumnIndex("ITE_FLO_PRECOVENDA")));
            itemPedido.setValorTotal(getDouble(getColumnIndex("ITE_FLO_VLRTOTAL")));
            itemPedido.setValorDesconto(getDouble(getColumnIndex("ITE_FLO_DESCVLR")));

            itemPedido.setPercentualDesconto(getDouble(getColumnIndex(
                    "ITE_FLO_DESCPERC")));
            itemPedido.setDescontoCliente(getDouble(getColumnIndex(
                    "ITE_FLO_DESC_CLIE")));
            itemPedido.setDescontoFlagship(getDouble(getColumnIndex(
                    "ITE_FLO_DESC_FLAGSHIP")));
            itemPedido.setDescontoGerencial(getDouble(getColumnIndex(
                    "ITE_FLO_DESC_GERENCIAL")));
            itemPedido.setDescontoPromocional(getDouble(getColumnIndex(
                    "ITE_FLO_DESC_PROMOCIONAL")));
            itemPedido.setDescontoCondicaoPagamento(getDouble(getColumnIndex(
                    "ITE_FLO_DESC_CONDPAG")));
            itemPedido.setDescontoEdv(getDouble(getColumnIndex(
                    "ITE_FLO_DESC_EDV")));
            itemPedido.setDescontoTatico(getDouble(getColumnIndex(
                    "ITE_FLO_DESC_TATICO")));

            // bb?
            itemPedido.setBbPrecoTabela(getDouble(getColumnIndex("ITE_FLO_BB_PRCTAB")));
            itemPedido.setBbPrecoLoc(getDouble(getColumnIndex("ITE_FLO_BB_PRCLOC")));
            itemPedido.setBbFator(getDouble(getColumnIndex("ITE_FLO_BB_FATOR")));

            // produto
            itemPedido.setCodigoProduto(getString(getColumnIndex("ITE_TXT_PRODCOD")));
            itemPedido.setCodigoEmpresaProduto(getString(getColumnIndex(
                    "ITE_TXT_PRODEMP")));
            itemPedido.setCodigoFilialProduto(getString(getColumnIndex(
                    "ITE_TXT_PRODFILIAL")));
            itemPedido.setPrecoTabelaProduto(getDouble(getColumnIndex(
                    "ITE_FLO_PRODPRECOTB")));
            itemPedido.setPesoProduto(getDouble(getColumnIndex("ITE_FLO_PRODPESO")));
            itemPedido.setPesoLiquidoProduto(getDouble(getColumnIndex(
                    "ITE_FLO_PRODPESOLIQ")));
            itemPedido.setStatusProduto(getInt(getColumnIndex("ITE_INT_PROD_STATUS")));

            return itemPedido;
        }
    }
}
