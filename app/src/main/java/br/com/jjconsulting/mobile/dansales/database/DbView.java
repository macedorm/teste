package br.com.jjconsulting.mobile.dansales.database;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Pedido;

public class DbView {

    private DbView() { }

    public static String getClienteViewAsSelect(String codigoUnidadeNegocio, boolean useAlias) {
        return "(" +
                " select" +
                " emp.EMP_TXT_CODEMP A1_EMP," +
                " emp.EMP_TXT_CODFIL A1_FILIAL," +
                " ifnull(emp.EMP_TXT_CODEMP, '') || ifnull(emp.EMP_TXT_CODFIL, '') COD_UN_NEGOCIO," +
                " cli.COD_EMITENTE A1_COD," +
                " cliun.PLANTA," +
                " cliun.COD_CANAL_VENDA," +
                " cli.COD_ESTADO" +
                " from TB_DECLIENTE cli" +
                " inner join TB_DECLIUNREG cliunreg" +
                "   on cliunreg.COD_EMITENTE = cli.COD_EMITENTE" +
                "   and cliunreg.COD_UNID_NEGOC = '" + codigoUnidadeNegocio + "'" +
                "   and (cliunreg.SEQUENCIA = '000' OR cliunreg.SEQUENCIA >= '100')" +
                "   and cliunreg.DEL_FLAG = '0'" +
                " inner join TB_DECLIENTEUN cliun" +
                "   on cliun.COD_EMITENTE = cli.COD_EMITENTE" +
                "   and cliun.COD_UNID_NEGOC = cliunreg.COD_UNID_NEGOC" +
                "   and cliun.INATIVO = 0" + // A1_BLOQ = FALSE
                "   and cliun.DEL_FLAG = '0'" +
                " inner join TBEMPFIL emp" +
                "   on emp.EMP_TXT_COD_UN = cliunreg.COD_UNID_NEGOC" +
                " where cli.DEL_FLAG = '0'" +
                " )" + (useAlias ? " cliente" : "");
    }

    public static String getDescontoViewAsSelect(String codigoUnidadeNegocio, boolean useAlias) {
        return "(" +
                " select" +
                " desc1.COD_CLIENTE," +
                " desc1.COD_UNID_NEGOC," +
                " desc1.COD_PRODUTO," +
                " desc1.VAL_MAX_DISCOUNT" +
                " from TB_DESCONTO desc1" +
                " where desc1.COD_CLIENTE <> 'C'" +
                " and desc1.DEL_FLAG = '0'" +
                " union" +
                " select" +
                " cliente.A1_COD," +
                " desc2.COD_UNID_NEGOC," +
                " desc2.COD_PRODUTO," +
                " desc2.VAL_MAX_DISCOUNT" +
                " from TB_DESCONTO desc2" +
                " inner join" +
                getClienteViewAsSelect(codigoUnidadeNegocio, true) +
                "   on desc2.COD_ESTADO = cliente.COD_ESTADO" +
                "   and desc2.COD_CANAL_VENDA = cliente.COD_CANAL_VENDA" +
                "   and desc2.COD_UNID_NEGOC = cliente.COD_UN_NEGOCIO" +
                "   and not exists (" +
                "     select * from TB_DESCONTO desc3" +
                "     where desc3.COD_CLIENTE = cliente.A1_COD" +
                "     and desc3.COD_UNID_NEGOC = cliente.COD_UN_NEGOCIO" +
                "     and desc3.COD_PRODUTO = desc2.COD_PRODUTO" +
                "     and desc3.DEL_FLAG = '0'" +
                "   )" +
                " where desc2.DEL_FLAG = '0'" +
                " )" + (useAlias ? " desconto" : "");
    }

    public static String getProdutoBloqueioViewAsSelect(Pedido pedido, boolean useAlias) {
        Cliente cliente = pedido.getCliente();
        String codigo = cliente.getCodigo() == null ? "" : cliente.getCodigo();
        String planta = cliente.getPlanta() == null ? "" : cliente.getPlanta();
        String canalVenda = cliente.getCodCanal() == null ? "" : cliente.getCodCanal();
        String unidadeNegocio = pedido.getCodigoUnidadeNegocio();

        String sql = "(" +

                "select" +
                " '" + unidadeNegocio + "' COD_UN_NEGOCIO," +
                " '" + codigo + "' A1_COD," +
                " bloq1.COD_SKU COD_SKU" +
                " from TB_DEBLOQPROD01 bloq1" +
                "   where bloq1.COD_CANAL_DISTRIB = '00'" +
                "   and bloq1.COD_UNID_NEGOCIO = '2430'" +
                "   and bloq1.COD_CLIENTE = '" + codigo + "'" +
                "   and bloq1.COD_ESTABEL = '0'" +
                "   and bloq1.COD_CANAL = '0'" +
                "   and bloq1.DEL_FLAG = '0'" +

                " union" +

                " select" +
                " '" + unidadeNegocio + "' COD_UN_NEGOCIO," +
                " '" + codigo + "' A1_COD," +
                " bloq1.COD_SKU COD_SKU" +
                " from TB_DEBLOQPROD01 bloq1" +
                "   where bloq1.COD_CANAL_DISTRIB = '00'" +
                "   and bloq1.COD_UNID_NEGOCIO = '2430'" +
                "   and bloq1.COD_CLIENTE = '0'" +
                "   and bloq1.COD_ESTABEL = '" + planta + "'" +
                "   and bloq1.COD_CANAL = '" + canalVenda + "'" +
                "   and bloq1.DEL_FLAG = '0'" +

                " union" +

                " select" +
                " '" + unidadeNegocio + "' COD_UN_NEGOCIO," +
                " '" + codigo + "' A1_COD," +
                " bloq1.COD_SKU COD_SKU" +
                " from TB_DEBLOQPROD01 bloq1" +
                "   where bloq1.COD_CANAL_DISTRIB = '00'" +
                "   and bloq1.COD_UNID_NEGOCIO = '2430'" +
                "   and bloq1.COD_CLIENTE = '0' " +
                "   and bloq1.COD_ESTABEL = '0' " +
                "   and bloq1.COD_CANAL = '" + canalVenda + "'" +
                "   and bloq1.DEL_FLAG = '0'" +

                " union" +

                " select" +
                " '" + unidadeNegocio + "' COD_UN_NEGOCIO," +
                " '" + codigo + "' A1_COD," +
                " bloq1.COD_SKU COD_SKU" +
                " from TB_DEBLOQPROD01 bloq1" +
                "   where bloq1.COD_CANAL_DISTRIB = '00'" +
                "   and bloq1.COD_UNID_NEGOCIO = '2430'" +
                "   and bloq1.COD_CLIENTE = '0' " +
                "   and bloq1.COD_ESTABEL = '" + planta + "'" +
                "   and bloq1.COD_CANAL = '0'" +
                "   and bloq1.DEL_FLAG = '0'" +

                " )" + (useAlias ? " produto_bloqueio" : "");

        return sql;
    }

    public static String getPrecoDAT(boolean useAlias, String whereClause, Pedido pedido) {
        if ("2400".equals(pedido.getCodigoUnidadeNegocio())) {
            return getPrecoDAT2400(useAlias, whereClause);
        } else {
            return getPrecoDAT(useAlias, whereClause);
        }
    }

    private static String getPrecoDAT(boolean useAlias, String whereClause) {
        return "(" +
                "select distinct" +
                " pdat.COD_UN_NEGOCIO," +
                " pdat.COD_CLIENTE," +
                " pdat.COD_PRODUTO, " +
                " pdat.VAL_PRECO_EDV DA1_PRCVEN," +
                " 100 DA1_DESCMAX" +
                " from TB_DEPRECO_ZBCL pdat" +
                " inner join TB_DEPRODUTO prd" +
                "   on prd.COD_SKU = pdat.COD_PRODUTO" +
                "   and prd.COD_UNID_NEGOC = pdat.COD_UN_NEGOCIO" +
                "   and prd.DEL_FLAG = '0'" +
                " inner join TB_DEFAMILIA fam" +
                "   on fam.COD_FAMILIA = prd.COD_FAMILIA" +
                "   and fam.DEL_FLAG = '0'" +
                " where pdat.COD_UN_NEGOCIO in ('2430', '2310')" +
                " and ifnull(prd.ITEM_DESCONTINUADO, '') = ''" +
                (whereClause == null ? "" : " " + whereClause) +
                ")" + (useAlias ? "preco_dat" : "");
    }

    private static String getPrecoDAT2400(boolean useAlias, String whereClause) {
        return "(" +
                "select" +
                " pdat.COD_UN_NEGOCIO," +
                " pdat.COD_CLIENTE," +
                " pdat.COD_PRODUTO," +
                " case when pdat.VAL_PRECO_PROMO > 0 then pdat.VAL_PRECO_PROMO" +
                " else pdat.VAL_PRECO_EDV" +
                " end DA1_PRCVEN," +
                " 100 DA1_DESCMAX" +
                " from TB_DEPRECO pdat" +
                " inner join TB_DEPRODUTO prd" +
                " on prd.COD_SKU = pdat.COD_PRODUTO" +
                " and prd.COD_UNID_NEGOC = pdat.COD_UN_NEGOCIO" +
                " and prd.DEL_FLAG = '0'" +
                " inner join TB_DEFAMILIA fam" +
                " on fam.COD_FAMILIA = prd.COD_FAMILIA" +
                " and fam.DEL_FLAG = '0'" +
                " where pdat.COD_UN_NEGOCIO = '2400'" +
                " and pdat.DEL_FLAG = '0'" +
                " and ifnull(prd.ITEM_DESCONTINUADO, '') = ''" +
                (whereClause == null ? "" : " " + whereClause) +
                ")" + (useAlias ? "preco_dat" : "");
    }
}
