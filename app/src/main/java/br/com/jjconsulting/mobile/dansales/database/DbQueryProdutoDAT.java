package br.com.jjconsulting.mobile.dansales.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;

public class DbQueryProdutoDAT {

    private SelectionType selectionType;
    private String fromTable;
    private OnJoiningListener onJoiningListener;

    private StringBuilder select;
    private StringBuilder from;
    private StringBuilder where;
    private StringBuilder orderBy;

    public static String toTextToCompareDateInESTOQUEDAT_WEBSALES(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(date);
    }

    public DbQueryProdutoDAT() {
        selectionType = SelectionType.Select;

        select = new StringBuilder();
        from = new StringBuilder();
        where = new StringBuilder();
        orderBy = new StringBuilder();
    }

    public DbQueryProdutoDAT(SelectionType selectionType, String fromTable,
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

    public void buildQuery(Pedido pedido, boolean permiteApenasCadastroProdutoAtivo,
                           boolean ignoraExclusividadeProdutoAtivo) {
        String codigoPlanta = pedido.getCodigoPlanta();
        String data = toTextToCompareDateInESTOQUEDAT_WEBSALES(new Date());

        // SELECT

        select.append("select");
        select.append(" prd.COD_SKU,");
        select.append(" prd.DESCRICAO,");
        select.append(" prd.COD_SIMPLIFICADO,");
        select.append(" ifnull(fam.DSC_FAMILIA, prd.COD_FAMILIA) FAMILIA,");
        select.append(" prd.PESO_BRUTO,");
        select.append(" prd.PESO_LIQUIDO,");
        select.append(" ifnull(prd.QTD_CAIXA, 1) QTD_CAIXA,");
        select.append(" ifnull(prd.CX_LASTRO, 0) CX_LASTRO,");
        select.append(" ifnull(prd.CX_PALLET, 0) CX_PALLET,");
        select.append(" null B1_BLOQ,"); // bloqueio
        select.append(" null DA1_PRCVEN,"); // preço de venda
        select.append(" null DA1_DESCMAX,"); // desconto
        select.append(" null POSSUI_PRECO,"); // possui preco

        select.append(" ifnull(prd.ITEM_DESCONTINUADO, 'N') B1_BLOQ_BATCH,"); // bloqueio batch

        select.append(" (");
        select.append("    select ifnull(sum(etq.EST_INT_QTD - etq.EST_INT_QTDUT), 0)");
        select.append("    from ESTOQUEDAT_WEBSALES etq");
        select.append("    where etq.EST_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC");
        select.append("    and etq.EST_TXT_SKU = prd.COD_SKU");
        select.append("    and etq.EST_TXT_PLANTA = '" + codigoPlanta + "'");
        select.append("    and '" + data + "' between etq.EST_DAT_INICIO and etq.EST_DAT_FIM");
        select.append("    and etq.EST_INT_QTD > etq.EST_INT_QTDUT");
        select.append(" ) QTDPROD,");

        select.append(" (");
        select.append("    select count(etq.EST_TXT_LOTE)");
        select.append("    from ESTOQUEDAT_WEBSALES etq");
        select.append("    where etq.EST_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC");
        select.append("    and etq.EST_TXT_SKU = prd.COD_SKU");
        select.append("    and etq.EST_TXT_PLANTA = '" + codigoPlanta + "'");
        select.append("    and '" + data + "' between etq.EST_DAT_INICIO and etq.EST_DAT_FIM");
        select.append("    and etq.EST_INT_QTD > etq.EST_INT_QTDUT");
        select.append(" ) QTDBATCH");

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

        if (permiteApenasCadastroProdutoAtivo) {
            from.append(" inner join PRODATIVO_WEBSALES pra");
            from.append(" on pra.PRD_TXT_SKU = prd.COD_SKU");
            from.append(" and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC");
            from.append(" and pra.PRD_TXT_TPPED = 'DAT'");
        }

        // WHERE

        where.append(" where 1 = 1");
        where.append(" and prd.DEL_FLAG = '0'");

        if (!ignoraExclusividadeProdutoAtivo) {
            where.append(" and not exists (");
            where.append("   select 1 from PRODATIVO_WEBSALES pra");
            where.append("   where pra.PRD_TXT_SKU = prd.COD_SKU");
            where.append("   and pra.PRD_TXT_UNID_NEGOC = prd.COD_UNID_NEGOC");
            where.append("   and pra.PRD_TXT_TPPED <> 'DAT'");
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
