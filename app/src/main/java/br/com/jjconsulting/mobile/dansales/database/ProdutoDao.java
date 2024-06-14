package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.database.cursor.BatchDATCursorWrapper;
import br.com.jjconsulting.mobile.dansales.database.cursor.ProdutoCursorWrapper;
import br.com.jjconsulting.mobile.dansales.database.cursor.ProdutoDATCursorWrapper;
import br.com.jjconsulting.mobile.dansales.model.BatchDAT;
import br.com.jjconsulting.mobile.dansales.model.EstoqueDAT;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TSortimento;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;

public class ProdutoDao extends BaseDansalesDao{

    private SortimentoDao sortimentoDao;
    private Context mContext;

    public ProdutoDao(Context context) {
        super(context);
        sortimentoDao = new SortimentoDao(context);
        mContext = context;
    }


    public boolean hasCaixaFracionada(String unidadeNegocio, String SKU){
        boolean isExists = false;

        StringBuilder query = new StringBuilder();
        query.append(" SELECT * ");
        query.append(" FROM TB_CAIXA_FRACIONADA ");
        query.append(" WHERE ");
        query.append(" COD_UNID_NEGOC = ? AND COD_SKU = ?");

        String[] args = { unidadeNegocio, SKU };

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                isExists = true;
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return isExists;
    }

    public String getVariante(String SKU, String unidadeNegocio) {
        String codigoProduto = "";

        StringBuilder query = new StringBuilder();
        query.append(" SELECT");
        query.append(" prd.COD_SKU, prd.COD_VARIANTE");
        query.append(" FROM TB_DEPRODUTO prd");
        query.append(" WHERE prd.COD_SKU = ? ");
        query.append(" AND prd.COD_UNID_NEGOC = ?");

        String[] args = { SKU, unidadeNegocio };

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                codigoProduto = (cursor.getString(cursor.getColumnIndex("COD_VARIANTE")));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return codigoProduto;
    }


    public String getCodigoProdByBarcode(String barcode, String unidadeNegocio) {
        String codigoProduto = "";

        StringBuilder query = new StringBuilder();
        query.append(" SELECT");
        query.append(" prd.COD_SKU");
        query.append(" FROM TB_DEPRODUTO prd");
        query.append(" WHERE (prd.COD_EAN13 = ? OR COD_DUN14 = ?)");
        query.append(" AND prd.COD_UNID_NEGOC = ?");

        String[] args = { barcode, barcode, unidadeNegocio };

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                codigoProduto = (cursor.getString(cursor.getColumnIndex("COD_SKU")));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return codigoProduto;
    }

    public Produto getByCod(Usuario usuario, Pedido pedido, String codigoProduto,
                            String codigoSimplificado, boolean loadPreco,
                            boolean isSortimentoAvailable) {
        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);
        String whereClause, codigoWhereClause;
        String[] whereArgs;
        ArrayList<Produto> produtos;

        if (TextUtils.isEmpty(codigoSimplificado)) {
            codigoWhereClause = " and prd.COD_SKU = '" + codigoProduto + "'";
        } else {
            codigoWhereClause = " and (prd.COD_SKU = '" + codigoProduto + "'" +
                    " OR prd.COD_SIMPLIFICADO = '" + codigoSimplificado + "')";
        }

        if (TipoVenda.DAT.equals(pedido.getCodigoTipoVenda())) {
            whereClause = codigoWhereClause +
                    " and prd.COD_UNID_NEGOC = ?" +
                    " and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'";
            whereArgs = new String[] { pedido.getCodigoUnidadeNegocio() };

            if (loadPreco) {
                whereClause += " and exists (" +
                        "    select 1 from ESTOQUEDAT_WEBSALES etq" +
                        "    where etq.EST_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                        "    and etq.EST_TXT_SKU = prd.COD_SKU" +
                        "    and etq.EST_TXT_PLANTA = ?" +
                        "    and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM" +
                        "    and etq.EST_INT_QTD > etq.EST_INT_QTDUT" +
                        " )";
                whereArgs = new String[] { pedido.getCodigoUnidadeNegocio(), pedido.getCodigoPlanta(),
                        DbQueryProdutoDAT.toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date()) };
            }

            produtos = queryDAT(whereClause, whereArgs, null, pedido,
                    perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                    perfilVenda.isIgnoraExclusividadeProdutoAtivo());
        } else {
            whereClause = codigoWhereClause +
                    " and prd.COD_UNID_NEGOC = ?";
            whereArgs = new String[] { pedido.getCodigoUnidadeNegocio() };
            produtos = query(whereClause, whereArgs, null, pedido, loadPreco,
                    perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                    perfilVenda.isIgnoraExclusividadeProdutoAtivo(), null);
        }

        if (produtos == null || produtos.size() == 0) {
            return null;
        }

        setAggregatedData(isSortimentoAvailable, pedido, produtos);

        return produtos.get(0);
    }


    // Produto (not DAT)

//    public ArrayList<Produto> getAll(Usuario usuario, Pedido pedido) {
//        return getAll(usuario, pedido, null);
//    }
//
//    public ArrayList<Produto> getAll(Usuario usuario, Pedido pedido,
//                                     DbQueryPagingService pagingService) {
//        String whereClause = "and prd.COD_UNID_NEGOC = ?";
//        String[] whereArgs = { pedido.getCodigoEmpresa() + pedido.getCodigoFilial() };
//        String orderBy = "order by prd.DESCRICAO";
//        ArrayList<Produto> produtos = query(whereClause, whereArgs, orderBy, pedido,
//                false, pagingService);
//        boolean isSortimentoHabilitado = usuario.getPerfil()
//                .getPerfilVenda(pedido.getCodigoTipoVenda())
//                .isSortimentoHabilitado();
//        setAggregatedData(isSortimentoHabilitado, pedido, produtos);
//        return produtos;
//    }

    public ArrayList<Produto> getAllWithPreco(Usuario usuario, Pedido pedido,
                                              DbQueryPagingService pagingService) {
        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);

        String whereClause = "and prd.COD_UNID_NEGOC = ?";
        String[] whereArgs = { pedido.getCodigoUnidadeNegocio() };
        String orderBy = "order by prd.DESCRICAO";
        ArrayList<Produto> produtos = query(whereClause, whereArgs, orderBy, pedido,
                true, perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                perfilVenda.isIgnoraExclusividadeProdutoAtivo(), pagingService);
        boolean isSortimentoHabilitado = usuario.getPerfil()
                .getPerfilVenda(pedido)
                .isSortimentoHabilitado();
        setAggregatedData(isSortimentoHabilitado, pedido, produtos);
        return produtos;
    }

    public ArrayList<Produto> findAll(Usuario usuario, Pedido pedido, String codigoOuNome) {
        return findAll(usuario, pedido, codigoOuNome, null);
    }

    public ArrayList<Produto> findAll(Usuario usuario, Pedido pedido, String codigoOuNome,
                                      DbQueryPagingService pagingService) {
        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);

        String whereClause = "and prd.COD_UNID_NEGOC = ?";
        ArrayList<String> whereArgs = new ArrayList<>();
        whereArgs.add(pedido.getCodigoUnidadeNegocio());

        if (!TextUtils.isEmpty(codigoOuNome)) {
            whereClause += " and (prd.COD_SKU like ? OR prd.DESCRICAO like ?)";
            whereArgs.add("%" + codigoOuNome + "%");
            whereArgs.add("%" + codigoOuNome + "%");
        }

        String orderBy = "order by prd.DESCRICAO";

        ArrayList<Produto> produtos = query(whereClause, whereArgs.toArray(new String[0]),
                orderBy, pedido, false, perfilVenda.isEdicaoAvancadaHabilitada(),
                perfilVenda.isIgnoraExclusividadeProdutoAtivo(), pagingService);

        boolean isSortimentoHabilitado = perfilVenda.isSortimentoHabilitado();

        setAggregatedData(isSortimentoHabilitado, pedido, produtos);

        return produtos;
    }

    public ArrayList<Produto> findAllWithPreco(Usuario usuario, Pedido pedido, String codigoOuNome,
                                               DbQueryPagingService pagingService) {
        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);

        String whereClause = "and prd.COD_UNID_NEGOC = ?";
        ArrayList<String> whereArgs = new ArrayList<>();
        whereArgs.add(pedido.getCodigoUnidadeNegocio());

        if (!TextUtils.isEmpty(codigoOuNome)) {
            whereClause += " and (" +
                    " prd.COD_SKU like ?" +
                    " OR prd.DESCRICAO like ?" +
                    " OR prd.COD_SIMPLIFICADO like ?" +
                    ")";
            whereArgs.add("%" + codigoOuNome + "%");
            whereArgs.add("%" + codigoOuNome + "%");
            whereArgs.add("%" + codigoOuNome + "%");
        }

        String orderBy = "order by prd.DESCRICAO";

        ArrayList<Produto> produtos = query(whereClause, whereArgs.toArray(new String[0]),
                orderBy, pedido, true, perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                perfilVenda.isIgnoraExclusividadeProdutoAtivo(), pagingService);

        boolean isSortimentoHabilitado = perfilVenda.isSortimentoHabilitado();

        setAggregatedData(isSortimentoHabilitado, pedido, produtos);

        return produtos;
    }

    private ArrayList<Produto> query(String whereClause, String[] args, String orderBy,
                                     Pedido pedido, boolean getPreco,
                                     boolean permiteApenasCadastroProdutoAtivo,
                                     boolean ignoraExclusividadeProdutoAtivo,
                                     DbQueryPagingService pagingService) {
        ArrayList<Produto> produtos = new ArrayList<>();

        DbQueryProduto query = new DbQueryProduto();
        query.buildQuery(pedido, getPreco, permiteApenasCadastroProdutoAtivo,
                ignoraExclusividadeProdutoAtivo);

        if (whereClause != null) {
            query.getWhere().append(" " + whereClause);
        }

        if (orderBy != null) {
            query.getOrderBy().append(" " + orderBy);
        }

        String finalQuery = pagingService == null ? query.getQuery() : query.getQuery(pagingService);

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(finalQuery, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                produtos.add(new ProdutoCursorWrapper(cursor, pedido, getPreco).getProduto());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return produtos;
    }

    // Produto DAT

    public ArrayList<Produto> getAllAsDAT(Usuario usuario, Pedido pedido) {
        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);

        String whereClause = " and prd.COD_UNID_NEGOC = ?" +
                " and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'" +
                " and exists (" +
                "    select 1 from ESTOQUEDAT_WEBSALES etq" +
                "    where etq.EST_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                "    and etq.EST_TXT_SKU = prd.COD_SKU" +
                "    and etq.EST_TXT_PLANTA = ?" +
                "    and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM" +
                "    and etq.EST_INT_QTD > etq.EST_INT_QTDUT" +
                " )";

        String[] whereArgs = { pedido.getCodigoUnidadeNegocio(), pedido.getCodigoPlanta(),
                DbQueryProdutoDAT.toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date()) };

        String orderBy = "order by prd.DESCRICAO";

        ArrayList<Produto> produtos = queryDAT(whereClause, whereArgs, orderBy, pedido,
                perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                perfilVenda.isIgnoraExclusividadeProdutoAtivo());

        boolean isSortimentoHabilitado = perfilVenda.isSortimentoHabilitado();

        setAggregatedData(isSortimentoHabilitado, pedido, produtos);

        return produtos;
    }

    public ArrayList<Produto> findAllAsDAT(Usuario usuario, Pedido pedido, String codigoOuNome) {
        PerfilVenda perfilVenda = usuario.getPerfil().getPerfilVenda(pedido);

        String whereClause = " and prd.COD_UNID_NEGOC = ?" +
                " and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'" +
                " and exists (" +
                "    select 1 from ESTOQUEDAT_WEBSALES etq" +
                "    where etq.EST_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC" +
                "    and etq.EST_TXT_SKU = prd.COD_SKU" +
                "    and etq.EST_TXT_PLANTA = ?" +
                "    and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM" +
                "    and etq.EST_INT_QTD > etq.EST_INT_QTDUT" +
                " )";

        ArrayList<String> whereArgs = new ArrayList<>();
        whereArgs.add(pedido.getCodigoUnidadeNegocio());
        whereArgs.add(pedido.getCodigoPlanta());
        whereArgs.add(DbQueryProdutoDAT.toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date()));

        if (!TextUtils.isEmpty(codigoOuNome)) {
            whereClause += " and (" +
                    "   prd.COD_SKU like ? " +
                    "   or prd.DESCRICAO like ? " +
                    "   or prd.COD_SIMPLIFICADO like ?" +
                    " )";
            whereArgs.add("%" + codigoOuNome + "%");
            whereArgs.add("%" + codigoOuNome + "%");
            whereArgs.add("%" + codigoOuNome + "%");
        }

        String orderBy = "order by prd.DESCRICAO";

        ArrayList<Produto> produtos = queryDAT(whereClause, whereArgs.toArray(new String[0]),
                orderBy, pedido, perfilVenda.isPermiteApenasCadastroProdutoAtivo(),
                perfilVenda.isIgnoraExclusividadeProdutoAtivo());

        boolean isSortimentoHabilitado = perfilVenda.isSortimentoHabilitado();

        setAggregatedData(isSortimentoHabilitado, pedido, produtos);

        return produtos;
    }

    private ArrayList<Produto> queryDAT(String whereClause, String[] args, String orderBy,
                                        Pedido pedido, boolean permiteApenasCadastroProdutoAtivo,
                                        boolean ignoraExclusividadeProdutoAtivo) {
        ArrayList<Produto> produtos = new ArrayList<>();

        DbQueryProdutoDAT query = new DbQueryProdutoDAT();
        query.buildQuery(pedido, permiteApenasCadastroProdutoAtivo, ignoraExclusividadeProdutoAtivo);

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
                produtos.add(new ProdutoDATCursorWrapper(cursor, pedido).getProduto());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return produtos;
    }

    // Produto DAT, Batch

    /**
     * Get the desconto DAT (percent).
     * @return desconto (percent) or null if it's not available
     */
    public Double getDescontoDAT(String unidadeNegocio, String codigoProduto, String planta,
                                 String lote) {
        StringBuilder query = new StringBuilder();
        query.append("select");
        query.append(" EST_FLO_DESC");
        query.append(" from TB_DEPRODUTO prd");
        query.append(" inner join ESTOQUEDAT_WEBSALES etq");
        query.append(" on prd.COD_UNID_NEGOC = etq.EST_TXT_UNID_NEGOC");
        query.append(" and prd.COD_SKU = etq.EST_TXT_SKU");
        query.append(" where prd.DEL_FLAG = '0'");
        query.append(" and prd.COD_UNID_NEGOC = ?");
        query.append(" and prd.COD_SKU = ?");
        query.append(" and etq.EST_TXT_PLANTA = ?");
        query.append(" and etq.EST_TXT_LOTE = ?");
        query.append(" and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM");
        query.append(" and etq.EST_INT_QTD > etq.EST_INT_QTDUT");
        query.append(" and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'");

        String[] args = {unidadeNegocio, codigoProduto, planta, lote,
                DbQueryProdutoDAT.toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date())};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return cursor.getDouble(0);
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return 0.0;
    }

    public int getSaldoDAT(String unidadeNegocio, String codigoProduto, String planta,
                           String lote, String unidadeMedida) {
        StringBuilder query = new StringBuilder();
        query.append("select");
        query.append(" (etq.EST_INT_QTD - etq.EST_INT_QTDUT) ESTOQUE,");
        query.append(" ifnull(prd.QTD_CAIXA, 0) MULTIPLO");
        query.append(" from TB_DEPRODUTO prd");
        query.append(" inner join ESTOQUEDAT_WEBSALES etq");
        query.append(" on prd.COD_UNID_NEGOC = etq.EST_TXT_UNID_NEGOC");
        query.append(" and prd.COD_SKU = etq.EST_TXT_SKU");
        query.append(" where prd.DEL_FLAG = '0'");
        query.append(" and prd.COD_UNID_NEGOC = ?");
        query.append(" and prd.COD_SKU = ?");
        query.append(" and etq.EST_TXT_PLANTA = ?");
        query.append(" and etq.EST_TXT_LOTE = ?");
        query.append(" and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM");
        query.append(" and etq.EST_INT_QTD > etq.EST_INT_QTDUT");
        query.append(" and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'");

        String[] args = {unidadeNegocio, codigoProduto, planta, lote,
                DbQueryProdutoDAT.toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date())};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int estoque = cursor.getInt(0);
                int multiplo = (int) MathUtils.toDoubleOrDefaultUsingLocalePTBR(cursor.getString(1));
                if (Pedido.UNIDADE_MEDIDA_CAIXA.equals(unidadeMedida) && multiplo > 0) {
                    return estoque / multiplo;
                } else {
                    return estoque;
                }
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return 0;
    }

    public EstoqueDAT getEstoqueDAT(String unidadeNegocio, String codigoProduto, String planta,
                                    String lote, String unidadeMedida) {
        StringBuilder query = new StringBuilder();
        query.append("select");
        query.append(" (etq.EST_INT_QTD - etq.EST_INT_QTDUT) ESTOQUE,");
        query.append(" ifnull(prd.QTD_CAIXA, 0) MULTIPLO");
        query.append(" from TB_DEPRODUTO prd");
        query.append(" inner join ESTOQUEDAT_WEBSALES etq");
        query.append(" on prd.COD_UNID_NEGOC = etq.EST_TXT_UNID_NEGOC");
        query.append(" and prd.COD_SKU = etq.EST_TXT_SKU");
        query.append(" where prd.DEL_FLAG = '0'");
        query.append(" and prd.COD_UNID_NEGOC = ?");
        query.append(" and prd.COD_SKU = ?");
        query.append(" and etq.EST_TXT_PLANTA = ?");
        query.append(" and etq.EST_TXT_LOTE = ?");
        query.append(" and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM");
        query.append(" and etq.EST_INT_QTD > etq.EST_INT_QTDUT");
        query.append(" and ifnull(prd.ITEM_DESCONTINUADO, 'N') <> 'S'");

        String[] args = { unidadeNegocio, codigoProduto, planta, lote,
                DbQueryProdutoDAT.toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date()) };

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int estoque = cursor.getInt(0);
                int multiplo = (int) MathUtils.toDoubleOrDefaultUsingLocalePTBR(cursor.getString(1));

                if (Pedido.UNIDADE_MEDIDA_CAIXA.equals(unidadeMedida) && multiplo > 0) {
                    estoque = estoque / multiplo;
                }

                EstoqueDAT estoqueDAT = new EstoqueDAT();
                estoqueDAT.setQuantidadeDisponivel(estoque);
                return estoqueDAT;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return null;
    }

    public ArrayList<BatchDAT> getAllBatchDAT(Pedido pedido, String codigoProduto) {
        ArrayList<BatchDAT> batches = new ArrayList<>();

        String queryPrecoDAT = " and pdat.COD_UN_NEGOCIO = ?" +
                " and pdat.COD_CLIENTE = ?" +
                " and pdat.COD_PRODUTO = ?";

        String query = "select distinct" +
                " etq.EST_TXT_LOTE," + // index 0
                " etq.EST_TXT_DTLOTE," + // index 1
                " etq.EST_FLO_DESC," + // index 2
                " (etq.EST_INT_QTD - etq.EST_INT_QTDUT) SLD_DISPONIVEL," + // index 3
                " preco_dat.DA1_PRCVEN," + // index 4
                " preco_dat.DA1_DESCMAX," + // index 5
                " ifnull(prd.QTD_CAIXA, 0) MULTIPLO" + // index 6
                " from ESTOQUEDAT_WEBSALES etq" +
                " inner join " + DbView.getPrecoDAT(true, queryPrecoDAT, pedido) +
                " on preco_dat.COD_UN_NEGOCIO = etq.EST_TXT_UNID_NEGOC" +
                " and preco_dat.COD_PRODUTO = etq.EST_TXT_SKU" +
                " and preco_dat.COD_CLIENTE = ?" +
                " inner join TB_DEPRODUTO prd" +
                " on prd.COD_UNID_NEGOC = etq.EST_TXT_UNID_NEGOC" +
                " and prd.COD_SKU = etq.EST_TXT_SKU" +
                " where etq.EST_TXT_UNID_NEGOC = ?" +
                " and etq.EST_TXT_PLANTA = ?" +
                " and etq.EST_TXT_SKU = ?" +
                " and ? between etq.EST_DAT_INICIO and etq.EST_DAT_FIM" +
                " and etq.EST_INT_QTD > etq.EST_INT_QTDUT";

        String[] args = {pedido.getCodigoUnidadeNegocio(), pedido.getCodigoCliente(),
                codigoProduto, pedido.getCodigoCliente(), pedido.getCodigoUnidadeNegocio(),
                pedido.getCodigoPlanta(), codigoProduto,
                DbQueryProdutoDAT.toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date())};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                batches.add(new BatchDATCursorWrapper(cursor, pedido).getBatchDAT());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return batches;
    }

    // All and etc.

    private void setAggregatedData(boolean isSortimentoHabilitado, Pedido pedido,
                                   ArrayList<Produto> produtos) {
        if (!isSortimentoHabilitado || pedido.getCodigoSortimento() == null) {
            return;
        }

        String unidadeNegocio = pedido.getCodigoUnidadeNegocio();
        Date dataReferencia = new Date();
        for (Produto produto : produtos) {
            produto.setTipoSortimento(sortimentoDao.getTipoSortimento(unidadeNegocio,
                    pedido.getCodigoSortimento(), produto.getCodigo(), dataReferencia));
            if (produto.getTipoSortimento() != null) {
                produto.setDescricaoSortimento((TSortimento.getDescriptionSortimento(mContext, produto.getTipoSortimento())));
            }
        }
    }
}
