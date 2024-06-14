package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.data.CarteiraPedidoFilter;
import br.com.jjconsulting.mobile.dansales.data.PedidoFilter;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedido;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoStatus;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioCarteiraPedidoDao extends BaseDansalesDao{

    private Context context;

    public RelatorioCarteiraPedidoDao(Context context) {
        super(context);
        this.context = context;
    }

    public ArrayList<StatusPedido> getCarteiraPedidoStatus() {
        String[] list = context.getResources().getStringArray(R.array.status_carteira_pedido_array);
        ArrayList<String> status = new ArrayList<String>(Arrays.asList(list));
        ArrayList<StatusPedido> statusPedidos = new ArrayList<>();
        int index = 1;
        for (String statusTemp : status) {
            StatusPedido statusPedido = new StatusPedido();
            statusPedido.setNome(statusTemp);
            if (index == 2) {
                statusPedido.setCodigo(4);
            } else if (index == 4) {
                statusPedido.setCodigo(2);
            }
            statusPedidos.add(statusPedido);
            index++;
        }
        return statusPedidos;
    }

    public ArrayList<StatusPedido> getStatusPedido() {

        StringBuilder query = new StringBuilder();

        ArrayList<StatusPedido> listStatusPedido = new ArrayList<>();

        query.append("SELECT  ");
        query.append("DISTINCT cStatus ");
        query.append("FROM TB_DECARTEIRAPEDIDO");


        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                StatusPedido obj = new RelatorioCarteiraStatusPedidoCursorWrapper(cursor).getStatusPedido();
                listStatusPedido.add(obj);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listStatusPedido;
    }

    public ArrayList<RelatorioCarteiraPedido> findAll(String codigoUsuario, String codigoUnidadeNegocio,
                                                      String mNome, CarteiraPedidoFilter pedidoFilter,
                                                      int indexOffSet) {

        ArrayList<RelatorioCarteiraPedido> listRelatorioCarteiraPedidos = new ArrayList<>();

        StringBuilder query = new StringBuilder();

        query.append("SELECT  ");
        query.append("ped.cIDOrderLegacy, ");
        query.append("MAX(ped.cPurchaseOrder) cPurchaseOrder, ");
        query.append("MAX(ped.cIDCustomerSoldTo) cIDCustomerSoldTo, ");
        query.append("MAX(cli.NOM_CLIENTE) NOM_CLIENTE, ");
        query.append("MAX(ped.cOrderType) cOrderType, ");
        query.append("MAX(ped.nOrigin) nOrigin, ");
        query.append("MAX(ped.origem) origem, ");
        query.append("SUM(ped.cOrderWeight) cOrderWeight, ");
        query.append("MAX(ped.cOrderCreated) cOrderCreated, ");
        query.append("COUNT(case when ped.idStatus = 1 then 1 else null end) qtdFaturado, ");
        query.append("COUNT(case when ped.idStatus = 2 then 1 else null end) qtdCancelado, ");
        query.append("COUNT(case when ped.idStatus = 4 then 1 else null end) qtdCorteTotal, ");
        query.append("COUNT(*) qtdTotal ");

        query.append("FROM TB_DECARTEIRAPEDIDO ped ");
        query.append("INNER JOIN TB_DEPRODUTO pro ");
        query.append("  ON  pro.COD_SKU = ped.Csku ");
        query.append("  AND pro.DEL_FLAG = '0' ");
        query.append("  AND pro.COD_UNID_NEGOC = ? ");
        query.append("INNER JOIN TB_DECLIENTE cli ");
        query.append("  ON  cli.COD_EMITENTE = ped.cIDCustomerSoldTo ");
        query.append("  AND cli.DEL_FLAG = '0' ");
        query.append("INNER JOIN TB_DECLIENTEUN cun ");
        query.append("  ON  cun.COD_EMITENTE = cli.COD_EMITENTE ");
        query.append("  AND cun.COD_UNID_NEGOC = ? ");
        query.append("  AND cun.DEL_FLAG = '0' ");
        query.append("  AND cun.INATIVO <> '1' ");
        query.append("INNER JOIN TB_DECLIUNREG cur ");
        query.append("  ON  cur.COD_EMITENTE = cun.COD_EMITENTE ");
        query.append("  AND cur.COD_UNID_NEGOC = cun.COD_UNID_NEGOC ");
        query.append("  AND cur.DEL_FLAG = '0' ");
        query.append("WHERE ped.SALES_ORG = cun.COD_UNID_NEGOC ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUnidadeNegocio);
        whereArgs.add(codigoUnidadeNegocio);

        if (mNome != null) {
            query.append(" AND (cli.NOM_CLIENTE LIKE ? ");
            whereArgs.add("%" + mNome + "%");
            query.append(" OR ped.cIDCustomerSoldTo LIKE ? ) ");
            whereArgs.add("%" + mNome + "%");
        }


        if (pedidoFilter != null) {

            if (pedidoFilter.getDateStart() != null && pedidoFilter.getDateEnd() != null) {
                query.append(" AND ped.cOrderCreated BETWEEN datetime(?) AND datetime(?) ");
                whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateStart()));
                whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateEnd()));
            }

            if (pedidoFilter.getOrganizacao() != null) {
                query.append(" AND cli.COD_ORGANIZACAO = ? ");
                whereArgs.add(pedidoFilter.getOrganizacao().getCodigo());
            }

            if (pedidoFilter.getBandeira() != null) {
                query.append(" AND cli.COD_BANDEIRA = ? ");
                whereArgs.add(pedidoFilter.getBandeira().getCodigoBandeira());
            }

            if (pedidoFilter.getStatus() != null && pedidoFilter.getStatus().getNome() != null) {
                if (pedidoFilter.getStatus().getNome().equals(context.getString(R.string.relatorio_carteira_pedido_status_1))) {
                    query.append(" AND NOT EXISTS (");
                    query.append("  SELECT * FROM TB_DECARTEIRAPEDIDO p2");
                    query.append("  WHERE p2.cIDOrderLegacy = ped.cIDOrderLegacy");
                    query.append("  AND p2.idStatus <> 1");
                    query.append(" )");
                } else if (pedidoFilter.getStatus().getNome().equals(context.getString(R.string.relatorio_carteira_pedido_status_2))) {
                    query.append(" AND NOT EXISTS (");
                    query.append("  SELECT * FROM TB_DECARTEIRAPEDIDO p2");
                    query.append("  WHERE p2.cIDOrderLegacy = ped.cIDOrderLegacy");
                    query.append("  AND p2.idStatus <> 2");
                    query.append(" )");
                } else if (pedidoFilter.getStatus().getNome().equals(context.getString(R.string.relatorio_carteira_pedido_status_3))) {
                    query.append(" AND EXISTS (");
                    query.append("  SELECT * FROM TB_DECARTEIRAPEDIDO p2");
                    query.append("  WHERE p2.cIDOrderLegacy = ped.cIDOrderLegacy");
                    query.append("  AND p2.idStatus = 3");
                    query.append(" )");
                    query.append(" AND NOT EXISTS (");
                    query.append("  SELECT * FROM TB_DECARTEIRAPEDIDO p2");
                    query.append("  WHERE p2.cIDOrderLegacy = ped.cIDOrderLegacy");
                    query.append("  AND (p2.idStatus = 4 OR p2.idStatus = 1)");
                    query.append(" )");
                } else if (pedidoFilter.getStatus().getNome().equals(context.getString(R.string.relatorio_carteira_pedido_status_4)) || pedidoFilter.getStatus().getNome().equals(context.getString(R.string.relatorio_carteira_pedido_status_5))) {
                    query.append(" AND EXISTS (");
                    query.append("  SELECT * FROM TB_DECARTEIRAPEDIDO p2");
                    query.append("  WHERE p2.cIDOrderLegacy = ped.cIDOrderLegacy");
                    query.append("  AND p2.idStatus = 4");
                    query.append(" )");
                }
            }

            if (pedidoFilter.getHierarquiaComercial() != null &&
                    pedidoFilter.getHierarquiaComercial().size() > 0) {

                query.append(" AND cur.COD_REG_FUNC in (");
                for (int i = 0; i < pedidoFilter.getHierarquiaComercial().size(); i++) {
                    Usuario usuario = pedidoFilter.getHierarquiaComercial().get(i);
                    if (i > 0) {
                        query.append(",");
                    }
                    query.append("'" + usuario.getCodigo() + "'");
                }
                query.append(") ");

            } else {
                query.append(" AND cur.COD_REG_FUNC = ? ");
                whereArgs.add(codigoUsuario);
            }
        } else {
            query.append(" AND cur.COD_REG_FUNC = ? ");
            whereArgs.add(codigoUsuario);
        }


        query.append(" GROUP BY ");
        query.append("ped.cIDOrderLegacy ");

        query.append("ORDER BY cOrderCreated desc");

        if (indexOffSet != -1) {
            query.append(" " + "LIMIT " + Config.SIZE_PAGE + " OFFSET " + indexOffSet);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                RelatorioCarteiraPedido obj = new RelatorioCarteiraPedidoCursorWrapper(cursor).getPedido();

                boolean isAdd = true;

                if (pedidoFilter != null) {
                    if(pedidoFilter.getStatus() != null){
                        if(pedidoFilter.getStatus().getNome().equals(context.getString(R.string.relatorio_carteira_pedido_status_4)) ) {
                            if(obj.getStatus() != RelatorioCarteiraPedidoStatus.FATURADO_PARCIAL){
                                isAdd = false;
                            }
                        }

                        if(pedidoFilter.getStatus().getNome().equals(context.getString(R.string.relatorio_carteira_pedido_status_5)) ) {
                            if(obj.getStatus() != RelatorioCarteiraPedidoStatus.CORTE_TOTAL){
                                isAdd = false;
                            }
                        }

                    }
                }

                if(isAdd){
                    listRelatorioCarteiraPedidos.add(obj);
                }

                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listRelatorioCarteiraPedidos;
    }

    public class RelatorioCarteiraPedidoCursorWrapper extends CursorWrapper {

        public RelatorioCarteiraPedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public RelatorioCarteiraPedido getPedido() {
            RelatorioCarteiraPedido pedido = new RelatorioCarteiraPedido();
            pedido.setCodigo(getString(getColumnIndex("cPurchaseOrder")));
            pedido.setCliente(getString(getColumnIndex("NOM_CLIENTE")));
            pedido.setCodigoTipoVenda(getString(getColumnIndex("cOrderType")));
            pedido.setPeso(getString(getColumnIndex("cOrderWeight")));
            pedido.setRegional(getString(getColumnIndex("nOrigin")));
            pedido.setCodigoCliente(getString(getColumnIndex("cIDCustomerSoldTo")));
            pedido.setCodigoOrigem(getInt(getColumnIndex("origem")));
            pedido.setCodigoSap(getString(getColumnIndex("cIDOrderLegacy")));

            try {
                String cDateCreated = getString(getColumnIndex("cOrderCreated"));
                Date dDateCreated = FormatUtils.toDate(cDateCreated);
                pedido.setDataCadastro(dDateCreated);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //Status
            int qtdCorteTotal = getInt(getColumnIndex("qtdCorteTotal"));
            int qtdFaturado = getInt(getColumnIndex("qtdFaturado"));
            int qtdCancelado = getInt(getColumnIndex("qtdCancelado"));
            int qtdTotal = getInt(getColumnIndex("qtdTotal"));

            RelatorioCarteiraPedidoStatus status;
            if (qtdFaturado == qtdTotal) {
                status = RelatorioCarteiraPedidoStatus.FATURADO_TOTAL;
            } else if (qtdCancelado == qtdTotal) {
                status = RelatorioCarteiraPedidoStatus.CANCELADO;
            } else if (qtdFaturado > 0) {
                status = RelatorioCarteiraPedidoStatus.FATURADO_PARCIAL;
            }  else if (qtdCorteTotal == qtdTotal) {
                status = RelatorioCarteiraPedidoStatus.CORTE_TOTAL;
            } else {
                status = RelatorioCarteiraPedidoStatus.EM_ABERTO;
            }
            pedido.setStatus(status);

            return pedido;
        }
    }


    public class RelatorioCarteiraStatusPedidoCursorWrapper extends CursorWrapper {

        public RelatorioCarteiraStatusPedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public StatusPedido getStatusPedido() {
            StatusPedido status = new StatusPedido();
            status.setNome(getString(getColumnIndex("cStatus")));
            return status;
        }
    }
}
