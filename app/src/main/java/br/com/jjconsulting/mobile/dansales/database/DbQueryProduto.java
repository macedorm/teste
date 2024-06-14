package br.com.jjconsulting.mobile.dansales.database;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;

public class DbQueryProduto {

    private SelectionType selectionType;
    private String fromTable;
    private OnJoiningListener onJoiningListener;

    private StringBuilder select;
    private StringBuilder from;
    private StringBuilder where;
    private StringBuilder orderBy;

    public DbQueryProduto() {
        selectionType = SelectionType.Select;

        select = new StringBuilder();
        from = new StringBuilder();
        where = new StringBuilder();
        orderBy = new StringBuilder();
    }

    public DbQueryProduto(SelectionType selectionType, String fromTable,
                          OnJoiningListener onJoiningListener) {
        if (selectionType == null)
            throw new IllegalArgumentException("selectionType cannot be null");

        if (SelectionType.Select != selectionType && onJoiningListener == null)
            throw new IllegalArgumentException(
                    "When using a type of JOIN you must provide onJoiningListener implementation");

        this.selectionType = selectionType;
        this.fromTable = fromTable;
        this.onJoiningListener = onJoiningListener;

        select = new StringBuilder();
        from = new StringBuilder();
        where = new StringBuilder();
        orderBy = new StringBuilder();
    }

    public StringBuilder getSelect() {
        return select;
    }

    public StringBuilder getFrom() {
        return from;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public StringBuilder getOrderBy() {
        return orderBy;
    }

    public void buildQuery(Pedido pedido, boolean getPreco,
                           boolean permiteApenasCadastroProdutoAtivo,
                           boolean ignoraExclusividadeProdutoAtivo) {
        String unidadeNegocio = pedido.getCodigoUnidadeNegocio();
        String tipoVenda = pedido.getCodigoTipoVenda();
        String codigoCliente = pedido.getCodigoCliente();
        String codigoGrupo = pedido.getCodigoGrupo();

        // SELECT

        select.append("select");
        select.append(" prd.COD_SKU,");
        select.append(" prd.COD_SIMPLIFICADO,");
        select.append(" prd.DESCRICAO,");
        select.append(" ifnull(fam.DSC_FAMILIA, prd.COD_FAMILIA) FAMILIA,");
        select.append(" prd.PESO_BRUTO,");
        select.append(" prd.PESO_LIQUIDO,");
        select.append(" ifnull(prd.QTD_CAIXA, 1) QTD_CAIXA,");
        select.append(" ifnull(prd.CX_LASTRO, 0) CX_LASTRO,");
        select.append(" ifnull(prd.CX_PALLET, 0) CX_PALLET,");

        if (getPreco) {
            select.append(" case when prc.VAL_PRECO_PROMO > 0 then prc.VAL_PRECO_PROMO");
            select.append(" else prc.VAL_PRECO_EDV");
            select.append(" end DA1_PRCVEN,"); // preço de venda
            select.append(" ifnull(desconto.VAL_MAX_DISCOUNT, 0) DA1_DESCMAX,"); // desconto
            select.append(" case when prd.ITEM_DESCONTINUADO is null then");
            select.append("   case when produto_bloqueio.COD_SKU is null then 0 else 1 end");
            select.append(" else 2");
            select.append(" end B1_BLOQ,"); // bloq
            select.append(" case when prc.VAL_PRECO_EDV is null then 0 else 1 end POSSUI_PRECO"); // possui preco
        } else {
            select.append(" null DA1_PRCVEN,"); // preço de venda
            select.append(" null DA1_DESCMAX,"); // desconto
            select.append(" null B1_BLOQ,"); // bloq
            select.append(" null POSSUI_PRECO"); // possui preco
        }

        // FROM/JOIN

        switch (selectionType) {
            case Join:
                from.append(" " + fromTable);
                from.append(" inner join TB_DEPRODUTO prd ");
                onJoiningListener.onJoining(from);
                break;
            case LeftJoin:
                from.append(" " + fromTable);
                from.append(" left join TB_DEPRODUTO prd ");
                onJoiningListener.onJoining(from);
                break;
            case RightJoin:
                from.append(" " + fromTable);
                from.append(" right join TB_DEPRODUTO prd ");
                onJoiningListener.onJoining(from);
                break;
            default:
                from.append(" from TB_DEPRODUTO prd");
                break;
        }

        from.append(" left join TB_DEFAMILIA fam");
        from.append(" on prd.COD_FAMILIA = fam.COD_FAMILIA");
        from.append(" and fam.DEL_FLAG = '0'");

        if (getPreco) {
            from.append(" inner join TB_DEPRECO prc");
            from.append(" on prd.COD_UNID_NEGOC = prc.COD_UN_NEGOCIO");
            from.append(" and prd.COD_SKU = prc.COD_PRODUTO");
            from.append(" and prc.COD_CLIENTE = '" + codigoCliente + "'");
            from.append(" and prc.DEL_FLAG = '0'");
            from.append(" left join " + DbView.getDescontoViewAsSelect(unidadeNegocio,
                    true));
            from.append(" on prc.COD_CLIENTE = desconto.COD_CLIENTE");
            from.append(" and prc.COD_UN_NEGOCIO = desconto.COD_UNID_NEGOC");
            from.append(" and prc.COD_PRODUTO = desconto.COD_PRODUTO");
            from.append(" left join " + DbView.getProdutoBloqueioViewAsSelect(pedido, true));
            from.append(" on prc.COD_UN_NEGOCIO = produto_bloqueio.COD_UN_NEGOCIO");
            from.append(" and prc.COD_CLIENTE = produto_bloqueio.A1_COD");
            from.append(" and prc.COD_PRODUTO = produto_bloqueio.COD_SKU");
        }

        if (TipoVenda.UHT.equals(tipoVenda) || TipoVenda.VCO.equals(tipoVenda)
                || permiteApenasCadastroProdutoAtivo) {
            from.append(" inner join PRODATIVO_WEBSALES pra");
            from.append(" on pra.PRD_TXT_SKU = prd.COD_SKU");
            from.append(" and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC");
            from.append(" and pra.PRD_TXT_TPPED = '" + tipoVenda + "'");

            if (TipoVenda.VCO.equals(tipoVenda)) {
                from.append(" and pra.PRD_INT_IDGRUPO = " + codigoGrupo);
            }
        }

        // WHERE

        where.append(" where 1 = 1");
        where.append(" and prd.DEL_FLAG = '0'");

        if (!ignoraExclusividadeProdutoAtivo) {
            where.append(" and not exists (");
            where.append("   select 1 from PRODATIVO_WEBSALES pra");
            where.append("   where pra.PRD_TXT_SKU = prd.COD_SKU");
            where.append("   and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC");
            where.append("   and pra.PRD_TXT_TPPED <> '" + tipoVenda + "'");
            where.append("   and pra.PRD_TXT_EXCLU = '1'");
            where.append(" )");
        }
    }

    public String getQuery() {
        return select
                .append(" ")
                .append(from)
                .append(" ")
                .append(where)
                .append(" ")
                .append(orderBy)
                .toString();
    }

    public String getQuery(DbQueryPagingService pagingService) {
        return select
                .append(" ")
                .append(from)
                .append(" ")
                .append(where)
                .append(" ")
                .append(orderBy)
                .append(" ")
                .append(String.format(" LIMIT %d OFFSET %d", pagingService.getPageSize(),
                        pagingService.getRecordCounter()))
                .toString();
    }

    public void clearQuery() {
        select.setLength(0);
        from.setLength(0);
        where.setLength(0);
        orderBy.setLength(0);
    }

    /**
     * When using INNER, LEFT or RIGHT JOIN you can must use this method to make the join.
     */
    public interface OnJoiningListener {

        void onJoining(StringBuilder from);
    }

    public enum SelectionType {

        Select, Join, LeftJoin, RightJoin
    }
}
