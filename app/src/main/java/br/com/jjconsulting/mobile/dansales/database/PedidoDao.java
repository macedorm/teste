package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.data.PedidoFilter;
import br.com.jjconsulting.mobile.dansales.kotlin.model.NotaFiscal;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.OrigemPedido;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoBonificado;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.TPerfilAgendaAprov;
import br.com.jjconsulting.mobile.dansales.model.TPerfilRegraAprov;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PedidoDao extends BaseDansalesDao{

    private TipoVendaDao tipoVendaDao;
    private ClienteDao clienteDao;
    private StatusPedidoDao statusPedidoDao;
    private OrigemPedidoDao origemPedidoDao;
    private LayoutDao layoutDao;

    private int indexOffSet;

    public PedidoDao(Context context) {
        super(context);
        tipoVendaDao = new TipoVendaDao(context);
        clienteDao = new ClienteDao(context);
        statusPedidoDao = new StatusPedidoDao(context);
        origemPedidoDao = new OrigemPedidoDao(context);
        layoutDao = new LayoutDao(context);

        indexOffSet = 0;
    }

    public void insert(Pedido pedido) {
        ContentValues contentValues = getContentValues(pedido);
        SQLiteDatabase database = getDb();
        database.insert("TBORCAMENTO", null, contentValues);
    }

    public void update(Pedido pedido, String novoCodigoPedido) {
        ContentValues contentValues = getContentValues(pedido);
        SQLiteDatabase database = getDb();

        database.update("TBORCAMENTO", contentValues, "ORC_TXT_ID = ?",
                new String[]{novoCodigoPedido == null ? pedido.getCodigo() : novoCodigoPedido});
    }

    public void delete(String codigoPedido) {
        SQLiteDatabase database = getDb();

        String whereClause = "ORC_TXT_ID = ?";
        String whereArgs[] = new String[]{codigoPedido};

        database.delete("TBORCAMENTO", whereClause, whereArgs);
    }

    public ArrayList<Pedido> getPedidoSugerido(String codigoUsuario, String codigoUnidadeNegocio,String codClie) {

        String date = FormatUtils.toTextToCompareshortDateInSQlite(FormatUtils.getDateTimeNow(0, 0, 0));

        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);
        String whereClause = "and ped.ORC_TXT_VENDCOD = ?" +
                " and ped.ORC_TXT_EMP = ?" +
                " and ped.ORC_TXT_FILIAL = ?" +
                " and ped.ORC_INT_STATUS == 1" +
                " and ped.ORC_TXT_CLIENTECOD = ?" +
                " and ped.ORC_TXT_TPVEND = 'SUG'" +
                " and ped.ORC_INT_ORIGEM = 4";
        String orderBy = "order by ped.ORC_DAT_ORCAMENTO desc";


        String[] whereArgs = {codigoUsuario, empresaFilial[0], empresaFilial[1], codClie};

        ArrayList<Pedido> pedidos = query(whereClause, whereArgs, orderBy, false);
        if (!pedidos.isEmpty()) {
            for(int ind = 0; ind < pedidos.size(); ind++){
                pedidos.set(ind, setAggregatedData(pedidos.get(ind)));
            }
        }

        return pedidos;
    }

    public boolean isPedidoSugerido(String codigoUsuario, String codigoUnidadeNegocio, String codCli) {

        String date = FormatUtils.toTextToCompareshortDateInSQlite(FormatUtils.getDateTimeNow(0, 0, 0));

        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);
        String whereClause = "and ped.ORC_TXT_VENDCOD = ?" +
                " and ped.ORC_TXT_EMP = ?" +
                " and ped.ORC_TXT_FILIAL = ?" +
                " and ped.ORC_INT_STATUS == 1" +
                " and ped.ORC_TXT_CLIENTECOD = ?" +
                " and ped.ORC_DAT_ORCAMENTO >= date(?)" +
                " and ped.ORC_TXT_TPVEND = 'SUG'" +
                " and ped.ORC_INT_ORIGEM = 4";
        String orderBy = "order by ped.ORC_DAT_ORCAMENTO desc";


        String[] whereArgs = {codigoUsuario, empresaFilial[0], empresaFilial[1], codCli, date};

        return querySUG(whereClause, whereArgs, orderBy);
    }

    public Pedido get(String codigo) {
        String whereClause = "and ped.ORC_TXT_ID = ?";
        String[] whereArgs = {codigo};
        ArrayList<Pedido> pedidos = query(whereClause, whereArgs, null, false);
        if (pedidos.isEmpty()) {
            return null;
        } else {
            Pedido pedido = pedidos.get(0);
            return setAggregatedData(pedido);
        }
    }


    public ArrayList<Pedido> getAll(Usuario usuario, String codigoUnidadeNegocio, boolean limit) {
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);
        String whereClause = "and ped.ORC_TXT_VENDCOD = ?" +
                " and ped.ORC_TXT_EMP = ?" +
                " and ped.ORC_TXT_FILIAL = ?" +
                " and ped.ORC_INT_STATUS <> 5";
        String orderBy = "order by ped.ORC_DAT_ORCAMENTO desc";
        String[] whereArgs = {usuario.getCodigo(), empresaFilial[0], empresaFilial[1]};
        return setAggregatedData(query(whereClause, whereArgs, orderBy, limit));
    }

    private String getDefaultFilterAprovacao(Usuario usuario, String codigoUnidadeNegocio){
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(codigoUnidadeNegocio);

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("and ped.ORC_TXT_EMP = '");
        whereClause.append(empresaFilial[0]);
        whereClause.append("' and ped.ORC_TXT_FILIAL = '");
        whereClause.append(empresaFilial[1]);
        whereClause.append("' and ped.ORC_INT_STATUS in (");
        whereClause.append(StatusPedido.ENVIADO_ADM + ", ");
        whereClause.append(StatusPedido.ENVIADO_APROVACAO + ", ");
        whereClause.append(StatusPedido.PEDIDO_GERADO + ", ");
        whereClause.append(StatusPedido.REPROVADO + ", ");
        whereClause.append(StatusPedido.EXPORTADO + ")");

        whereClause.append(" and (");
        for (PerfilVenda pf : usuario.getPerfil().getPerfisVenda()) {
            if (!pf.getCodigoUnidadeNegocio().equals(codigoUnidadeNegocio))
                continue;

            if (!pf.getAprov().isAprovacaoHabilitada())
                continue;

            whereClause.append("(ORC_TXT_TPVEND = '");
            whereClause.append(pf.getTipoVenda());
            whereClause.append("' ");

            if (pf.getAprov().getFiltroAgenda() == TPerfilAgendaAprov.APROVA_SOMENTE_COM_AGENDA)
                whereClause.append(" AND ORC_INT_AGENDA <> 0 ");
            else if (pf.getAprov().getFiltroAgenda() == TPerfilAgendaAprov.APROVA_SOMENTE_SEM_AGENDA)
                whereClause.append(" AND ORC_INT_AGENDA = 0 ");

            //Não existe pedido com quebra no app
            whereClause.append(" AND ORC_TXT_QUEBRA <> '1' ");

            whereClause.append(") ");
            whereClause.append(" OR ");

        }
        whereClause.append("1 <> 1 )");

        return whereClause.toString();
    }

    public ArrayList<Pedido> getAllAprovacao(Usuario usuario, String codigoUnidadeNegocio,
                                             boolean limit) {

        String whereClause = getDefaultFilterAprovacao(usuario, codigoUnidadeNegocio);
        String orderBy = "order by ped.orc_dat_orcamento desc, ped.orc_txt_urgente desc";
        String[] whereArgs = {};
        return setAggregatedData(query(whereClause, whereArgs, orderBy, limit));
    }

    public ArrayList<Pedido> getAllLiberacao(Usuario usuario, String codigoUnidadeNegocio,
                                             boolean limit) {
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(" and ped.ORC_TXT_EMP = ?");
        whereClause.append(" and ped.ORC_TXT_FILIAL = ?");
        whereClause.append(" and ped.ORC_INT_STATUS in (");
        whereClause.append(StatusPedido.ENVIADO_ADM + ", ");
        whereClause.append(StatusPedido.ENVIADO_APROVACAO + ", ");
        whereClause.append(StatusPedido.PEDIDO_GERADO + ", ");
        whereClause.append(StatusPedido.REPROVADO + ", ");
        whereClause.append(StatusPedido.EXPORTADO + ")");
        whereClause.append(" and ped.orc_txt_tpvend in (");
        for (PerfilVenda pf : usuario.getPerfil().getPerfisVenda()) {
            if (pf.isLiberacaoHabilitada()) {
                whereClause.append("'" + pf.getTipoVenda() + "', ");
            }
        }
        whereClause.append("'XXX')");
        String orderBy = "order by ped.orc_dat_orcamento desc, ped.orc_txt_urgente desc";
        String[] whereArgs = {empresaFilial[0], empresaFilial[1]};
        return setAggregatedData(query(whereClause.toString(), whereArgs, orderBy, limit));
    }

    public ArrayList<Pedido> findAll(Usuario usuario, String codigoUnidadeNegocio,
                                     String quickFilter, PedidoFilter pedidoFilter, boolean limit) {
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("and ped.ORC_TXT_EMP = ?");
        whereClause.append(" and ped.ORC_TXT_FILIAL = ?");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(empresaFilial[0]);
        whereArgs.add(empresaFilial[1]);

        if (!TextUtils.isEmpty(quickFilter)) {
            whereClause.append(" and (cli.NOM_CLIENTE LIKE ?" +
                    " or cli.COD_EMITENTE LIKE ?" +
                    " or ped.ORC_TXT_CODSIGA LIKE ?" +
                    " or ped.ORC_TXT_TPVEND LIKE ?" +
                    " or ped.ORC_TXT_ID LIKE ?)");

            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
        }

        if (pedidoFilter.getDateStart() != null && pedidoFilter.getDateEnd() != null) {
            whereClause.append(" AND ped.ORC_DAT_ORCAMENTO BETWEEN datetime(?) AND datetime(?) ");
            whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateStart()));
            whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateEnd()));
        }

        if (pedidoFilter.getOrganizacao() != null) {
            whereClause.append(" and cli.COD_ORGANIZACAO = ?");
            whereArgs.add(pedidoFilter.getOrganizacao().getCodigo());
        }

        if (pedidoFilter.getBandeira() != null) {
            whereClause.append(" and cli.COD_BANDEIRA = ?");
            whereArgs.add(pedidoFilter.getBandeira().getCodigoBandeira());
        }

        if (pedidoFilter.getStatus() != null) {
            whereClause.append(" and ped.ORC_INT_STATUS = "
                    + String.valueOf(pedidoFilter.getStatus().getCodigo()));
        } else {
            whereClause.append(" and ped.ORC_INT_STATUS <> 5");
        }

        if (pedidoFilter.getHierarquiaComercial().size() > 0) {
            whereClause.append(" and ped.ORC_TXT_VENDCOD in (");
            for (int i = 0; i < pedidoFilter.getHierarquiaComercial().size(); i++) {
                Usuario u = pedidoFilter.getHierarquiaComercial().get(i);
                whereClause.append("'" + u.getCodigo() + "',");
            }
            // it's better than an if in every loop
            whereClause.deleteCharAt(whereClause.length() - 1);
            whereClause.append(")");
        } else {
            whereClause.append(" and ped.ORC_TXT_VENDCOD = ?");
            whereArgs.add(usuario.getCodigo());
        }

        String orderBy = "order by ped.ORC_DAT_ORCAMENTO desc";

        return setAggregatedData(query(whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, limit));
    }

    public ArrayList<Pedido> findAllAprovacao(Usuario usuario, String codigoUnidadeNegocio,
                                              String quickFilter, PedidoFilter pedidoFilter,
                                              boolean limit) {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(getDefaultFilterAprovacao(usuario, codigoUnidadeNegocio));
        List<String> whereArgs = new ArrayList<>();

        if (!TextUtils.isEmpty(quickFilter)) {
            whereClause.append(" and (cli.NOM_CLIENTE LIKE ?" +
                    " or cli.COD_EMITENTE LIKE ?" +
                    " or ped.ORC_TXT_CODSIGA LIKE ?" +
                    " or ped.ORC_TXT_TPVEND LIKE ?" +
                    " or ped.ORC_TXT_ID LIKE ?)");

            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
        }

        if (pedidoFilter.getDateStart() != null && pedidoFilter.getDateEnd() != null) {
            whereClause.append(" AND ped.ORC_DAT_ORCAMENTO BETWEEN datetime(?) AND datetime(?) ");
            whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateStart()));
            whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateEnd()));
        }

        if (pedidoFilter.getOrganizacao() != null) {
            whereClause.append(" and cli.COD_ORGANIZACAO = ?");
            whereArgs.add(pedidoFilter.getOrganizacao().getCodigo());
        }

        if (pedidoFilter.getBandeira() != null) {
            whereClause.append(" and cli.COD_BANDEIRA = ?");
            whereArgs.add(pedidoFilter.getBandeira().getCodigoBandeira());
        }

        if (pedidoFilter.getStatus() != null) {
            whereClause.append(" and ped.ORC_INT_STATUS = "
                    + String.valueOf(pedidoFilter.getStatus().getCodigo()));
        }

        if (pedidoFilter.getHierarquiaComercial().size() > 0) {
            whereClause.append(" and ped.ORC_TXT_VENDCOD in (");
            for (int i = 0; i < pedidoFilter.getHierarquiaComercial().size(); i++) {
                Usuario u = pedidoFilter.getHierarquiaComercial().get(i);
                whereClause.append("'" + u.getCodigo() + "',");
            }
            // it's better than an if in every loop
            whereClause.deleteCharAt(whereClause.length() - 1);
            whereClause.append(")");
        }

        String orderBy = "order by ped.orc_dat_orcamento desc, ped.orc_txt_urgente desc";
        return setAggregatedData(query(whereClause.toString(),
                whereArgs.toArray(new String[0]), orderBy, limit));
    }

    public ArrayList<Pedido> findAllLiberacao(Usuario usuario, String codigoUnidadeNegocio,
                                              String quickFilter, PedidoFilter pedidoFilter,
                                              boolean limit) {
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("and ped.ORC_TXT_EMP = ?");
        whereClause.append(" and ped.ORC_TXT_FILIAL = ?");
        whereClause.append(" and ped.ORC_INT_STATUS in (");
        whereClause.append(StatusPedido.ENVIADO_ADM + ", ");
        whereClause.append(StatusPedido.ENVIADO_APROVACAO + ", ");
        whereClause.append(StatusPedido.PEDIDO_GERADO + ", ");
        whereClause.append(StatusPedido.REPROVADO + ", ");
        whereClause.append(StatusPedido.EXPORTADO + ")");
        whereClause.append(" and ped.orc_txt_tpvend in (");
        for (PerfilVenda pf : usuario.getPerfil().getPerfisVenda()) {
            if (pf.isLiberacaoHabilitada()) {
                whereClause.append("'" + pf.getTipoVenda() + "', ");
            }
        }
        whereClause.append("'XXX')");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(empresaFilial[0]);
        whereArgs.add(empresaFilial[1]);

        if (!TextUtils.isEmpty(quickFilter)) {
            whereClause.append(" and (cli.NOM_CLIENTE LIKE ?" +
                    " or cli.COD_EMITENTE LIKE ?" +
                    " or ped.ORC_TXT_CODSIGA LIKE ?" +
                    " or ped.ORC_TXT_TPVEND LIKE ?" +
                    " or ped.ORC_TXT_ID LIKE ?)");

            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
            whereArgs.add("%" + quickFilter + "%");
        }

        if (pedidoFilter.getDateStart() != null && pedidoFilter.getDateEnd() != null) {
            whereClause.append(" AND ped.ORC_DAT_ORCAMENTO BETWEEN datetime(?) AND datetime(?) ");
            whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateStart()));
            whereArgs.add(FormatUtils.toDefaultDateFormat(pedidoFilter.getDateEnd()));
        }

        if (pedidoFilter.getOrganizacao() != null) {
            whereClause.append(" and cli.COD_ORGANIZACAO = ?");
            whereArgs.add(pedidoFilter.getOrganizacao().getCodigo());
        }

        if (pedidoFilter.getBandeira() != null) {
            whereClause.append(" and cli.COD_BANDEIRA = ?");
            whereArgs.add(pedidoFilter.getBandeira().getCodigoBandeira());
        }

        if (pedidoFilter.getStatus() != null) {
            whereClause.append(" and ped.ORC_INT_STATUS = "
                    + String.valueOf(pedidoFilter.getStatus().getCodigo()));
        }

        if (pedidoFilter.getHierarquiaComercial().size() > 0) {
            whereClause.append(" and ped.ORC_TXT_VENDCOD in (");
            for (int i = 0; i < pedidoFilter.getHierarquiaComercial().size(); i++) {
                Usuario u = pedidoFilter.getHierarquiaComercial().get(i);
                whereClause.append("'" + u.getCodigo() + "',");
            }
            // it's better than an if in every loop
            whereClause.deleteCharAt(whereClause.length() - 1);
            whereClause.append(")");
        }

        String orderBy = "order by ped.orc_dat_orcamento desc, ped.orc_txt_urgente desc";
        return setAggregatedData(query(whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, limit));
    }

    private Pedido setAggregatedData(Pedido pedido) {
        String codigoUnidadeNegocio = pedido.getCodigoUnidadeNegocio();
        if (pedido.getCodigoTipoVenda() != null) {
            pedido.setTipoVenda(tipoVendaDao.get(pedido.getCodigoTipoVenda()));
        }
        pedido.setCliente(clienteDao.get(codigoUnidadeNegocio, pedido.getCodigoCliente()));
        pedido.setStatus(statusPedidoDao.get(pedido.getCodigoStatus()));
        pedido.setOrigem(origemPedidoDao.get(pedido.getCodigoOrigem()));

        try {
            // the sortimento's codigo must be get first than the itens
            pedido.setCodigoSortimento(layoutDao.getLayouCode(codigoUnidadeNegocio,
                    pedido.getCodigoCliente(), new Date()));

        }catch (Exception ex){
            LogUser.log(ex.toString());
        }

        return pedido;
    }

    /**
     * Load and set aggregated data like cliente, tipo de venda and more.
     */
    private ArrayList<Pedido> setAggregatedData(ArrayList<Pedido> pedidos) {
        Map<String, TipoVenda> tiposVenda = new HashMap<>();
        Map<String, Cliente> clientes = new HashMap<>();
        Map<Integer, StatusPedido> statuses = new HashMap<>();
        Map<Integer, OrigemPedido> origens = new HashMap<>();
        Date dataReferencia = new Date();

        for (Pedido pedido : pedidos) {
            String codigoUnidadeNegocio = pedido.getCodigoUnidadeNegocio();

            // tipo venda
            if (tiposVenda.containsKey(pedido.getCodigoTipoVenda())) {
                pedido.setTipoVenda(tiposVenda.get(pedido.getCodigoTipoVenda()));
            } else if (pedido.getCodigoTipoVenda() != null) {
                TipoVenda tipoVenda = tipoVendaDao.get(pedido.getCodigoTipoVenda());
                if (tipoVenda != null) {
                    pedido.setTipoVenda(tipoVenda);
                    tiposVenda.put(tipoVenda.getCodigo(), tipoVenda);
                }
            }

            // cliente
            if (clientes.containsKey(pedido.getCodigoCliente())) {
                pedido.setCliente(clientes.get(pedido.getCodigoCliente()));
            } else if (pedido.getCodigoCliente() != null) {
                Cliente cliente = clienteDao.get(codigoUnidadeNegocio, pedido.getCodigoCliente());
                if (cliente != null) {
                    pedido.setCliente(cliente);
                    clientes.put(cliente.getCodigo(), cliente);
                }
            }

            // status
            if (statuses.containsKey(pedido.getCodigoStatus())) {
                pedido.setStatus(statuses.get(pedido.getCodigoStatus()));
            } else {
                StatusPedido status = statusPedidoDao.get(pedido.getCodigoStatus());
                if (status != null) {
                    pedido.setStatus(status);
                    statuses.put(status.getCodigo(), status);
                }
            }

            // origem pedido
            if (origens.containsKey(pedido.getCodigoOrigem())) {
                pedido.setOrigem(origens.get(pedido.getCodigoOrigem()));
            } else {
                OrigemPedido origem = origemPedidoDao.get(pedido.getCodigoOrigem());
                if (origem != null) {
                    pedido.setOrigem(origem);
                    origens.put(origem.getCodigo(), origem);
                }
            }

            // the sortimento's codigo must be get first than the itens
            pedido.setCodigoSortimento(layoutDao.getLayouCode(codigoUnidadeNegocio,
                    pedido.getCodigoCliente(), dataReferencia));
        }

        return pedidos;
    }

    public List<NotaFiscal> getNotaFiscal(String unidadeNegocio, String codigo, boolean isNF){
        StringBuilder where = new StringBuilder();

        if(isNF){
            where.append(" NF.NUM_NF = ? COLLATE NOCASE");
        } else {
            where.append(" NF.NUM_PEDIDO = ? COLLATE NOCASE");
        }

        String[] whereArgs = {codigo};

        return queryNotaFiscal(unidadeNegocio, where.toString(), whereArgs);
    }

    private List<NotaFiscal> queryNotaFiscal(String unidadeNegocio, String where, String[] whereArgs) {
        List<NotaFiscal> notaFiscal = new ArrayList<>();

        StringBuilder sqlPedidos = new StringBuilder();
        sqlPedidos.append("SELECT NF.NUM_NF, ");
        sqlPedidos.append(" NF.NUM_SERIE_NF, ");
        sqlPedidos.append(" NF.DAT_EMISSAO_NF, ");
        sqlPedidos.append(" NF.NUM_PEDIDO, ");
        sqlPedidos.append(" ORC.ORC_INT_STATUS, ");
        sqlPedidos.append(" ORC.ORC_INT_ORIGEM, ");
        sqlPedidos.append(" ORC.ORC_TXT_CODSIGA, ");
        sqlPedidos.append(" ORC_TXT_CLIENTECOD, ");
        sqlPedidos.append(" (SELECT ifnull(sum(ITE_FLO_VLRTOTAL), 0) ");
        sqlPedidos.append(" FROM TBORCAMENTOITEM ");
        sqlPedidos.append("  WHERE ITE_TXT_ORCID = ORC.ORC_TXT_ID) AS VL_TOTAL ");
        sqlPedidos.append("FROM TB_NF_CAB AS NF ");
        sqlPedidos.append("INNER JOIN TBORCAMENTO AS ORC ON ORC.ORC_TXT_ID = NF.NUM_PEDIDO ");
        sqlPedidos.append("WHERE ");
        sqlPedidos.append(where);

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sqlPedidos.toString(), whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notaFiscal.add(new NotaFiscalWrapper(cursor).getNotaFiscal(unidadeNegocio));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return notaFiscal;
    }

    /**
     * Load enabled list of order id to bonification
     */
    public ArrayList<PedidoBonificado> getListPedToBon(String codigoUsuario, String codigoUnidadeNegocio,
                                                       String codigoCliente, String codOrc) {
        ArrayList<PedidoBonificado> pedidos = new ArrayList<>();
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);

        StringBuilder sqlPedidos = new StringBuilder();
        sqlPedidos.append("SELECT ORC_TXT_ID AS COD, ORC_TXT_IDCONDPAG, ORC_DAT_FATURAR_EM, ORC_TXT_UNMED, ORC_DAT_ORCAMENTO ");
        sqlPedidos.append("FROM TBORCAMENTO ");
        sqlPedidos.append("WHERE ORC_TXT_TPVEND IN ('VEN', 'SUG') ");
        sqlPedidos.append("AND ORC_TXT_EMP = '");
        sqlPedidos.append(empresaFilial[0]);
        sqlPedidos.append("' AND ORC_TXT_FILIAL = '");
        sqlPedidos.append(empresaFilial[1]);
        sqlPedidos.append("' AND ORC_TXT_VENDCOD = '");
        sqlPedidos.append(codigoUsuario);
        sqlPedidos.append("' AND ORC_TXT_CLIENTECOD = '");
        sqlPedidos.append(codigoCliente);
        sqlPedidos.append("' AND DATE(ORC_DAT_ENVIO) = '"
                + FormatUtils.toTextToCompareshortDateInSQlite(new Date()) + "' ");
        sqlPedidos.append("AND ORC_TXT_ID NOT IN ");
        sqlPedidos.append("(SELECT ORC_TXT_ID_VEN ");
        sqlPedidos.append("FROM TBORCAMENTO ");
        sqlPedidos.append("WHERE ORC_TXT_TPVEND = 'BON' ");
        sqlPedidos.append("AND ORC_TXT_EMP = '");
        sqlPedidos.append(empresaFilial[0]);
        sqlPedidos.append("' AND ORC_TXT_FILIAL = '");
        sqlPedidos.append(empresaFilial[1]);
        sqlPedidos.append("' AND ORC_TXT_VENDCOD = '");
        sqlPedidos.append(codigoUsuario);
        sqlPedidos.append("' AND ORC_TXT_CLIENTECOD = '");
        sqlPedidos.append(codigoCliente);
        sqlPedidos.append("' AND orc_txt_id <> '");
        sqlPedidos.append(codOrc);
        sqlPedidos.append("' AND orc_int_status <> ");
        sqlPedidos.append(StatusPedido.CANCELADO);
        sqlPedidos.append(" AND ORC_TXT_ID_VEN IS NOT NULL) ");
        sqlPedidos.append("ORDER BY ORC_TXT_ID ");

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sqlPedidos.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                pedidos.add(new PedidoDao.PedidoBonificadoWrapper(cursor).getPedidoBonificado());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return pedidos;
    }

    private boolean querySUG(String whereClause, String[] args, String orderBy) {
        String query = "select " +
                " ped.ORC_TXT_ID, ped.ORC_TXT_EMP, DATE(ped.ORC_DAT_ORCAMENTO)," +
                " ped.ORC_TXT_FILIAL, ped.ORC_TXT_VENDCOD, ped.ORC_TXT_CLIENTECOD," +
                " ped.ORC_DAT_ORCAMENTO, ped.ORC_TXT_TPVEND," +
                " ped.ORC_INT_STATUS, ped.ORC_INT_ORIGEM " +
                " from TBORCAMENTO ped" +
                " inner join TB_DECLIENTE cli" +
                " on cli.COD_EMITENTE = ped.ORC_TXT_CLIENTECOD" +
                " and cli.DEL_FLAG = '0'" +
                " where 1 = 1";

        if (whereClause != null) {
            query += " " + whereClause;
        }

        if (orderBy != null) {
            query += " " + orderBy;
        }


        int count = 0;

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cursor.moveToNext();
                count++;
            }

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        if (count > 0)
            return true;
        else
            return false;

    }


    private ArrayList<Pedido> query(String whereClause, String[] args, String orderBy, boolean limit) {
        ArrayList<Pedido> pedidos = new ArrayList<>();

        String query = "select ped.ORC_TXT_ESPECIFIC3, ped.ORC_TXT_ESPECIFIC2, ped.ORC_TXT_LOJAENTREGA," +
                " ped.ORC_TXT_ID, ped.ORC_TXT_EMP, ped.ORC_INT_POSITIVACAO, " +
                " ped.ORC_TXT_FILIAL, ped.ORC_TXT_VENDCOD, ped.ORC_TXT_CLIENTECOD, ORC_TXT_URGENTE," +
                " ped.ORC_DAT_ORCAMENTO, ped.ORC_DAT_IMPORTACAO_SAP, ped.ORC_TXT_CODSIGA, ped.ORC_TXT_TPVEND," +
                " ped.ORC_INT_STATUS, ped.ORC_TXT_JUSTIF, ped.ORC_TXT_OBSNF," +
                " ped.ORC_INT_ORIGEM, ped.ORC_TXT_UNMED, ped.ORC_DAT_ENVIO, ORC_DAT_FATURAR_EM, ORC_DAT_ENVIO, " +
                " ped.ORC_TXT_PLANTA, ped.ORC_INT_GRUPOPROD, ped.ORC_TXT_IDCONDPAG," +
                " ped.ORC_TXT_ID_VEN, ped.ORC_TXT_AGENDA, ped.ORC_INT_AGENDA," +
                " (" +
                "   select ifnull(sum(ITE_FLO_VLRTOTAL), 0) from TBORCAMENTOITEM" +
                "   where ITE_TXT_ORCID = ped.ORC_TXT_ID" +
                " ) VL_TOTAL" +
                " from TBORCAMENTO ped" +
                " inner join TB_DECLIENTE cli" +
                " on cli.COD_EMITENTE = ped.ORC_TXT_CLIENTECOD" +
                " and cli.DEL_FLAG = '0'" +
                " where 1 = 1";

        if (whereClause != null) {
            query += " " + whereClause;
        }

        if (orderBy != null) {
            query += " " + orderBy;
        }

        if (limit) {
            query += " LIMIT " + Config.SIZE_PAGE + " OFFSET " + indexOffSet;
        }

        SQLiteDatabase database = getDb();

        Cursor cursor = database.rawQuery(query, args);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            pedidos.add(new PedidoDao.PedidoCursorWrapper(cursor).getPedido());
            cursor.moveToNext();
        }
        return pedidos;
    }

    public void resetIndexOffSet() {
        indexOffSet = 0;
    }

    public void addIndexOffSet() {
        this.indexOffSet += Config.SIZE_PAGE;
    }

    private static ContentValues getContentValues(Pedido pedido) {
        ContentValues values = new ContentValues();
        values.put("ORC_TXT_ID", pedido.getCodigo());
        values.put("ORC_TXT_EMP", pedido.getCodigoEmpresa());
        values.put("ORC_TXT_FILIAL", pedido.getCodigoFilial());
        values.put("ORC_TXT_VENDCOD", pedido.getCodigoVendedor());
        values.put("ORC_TXT_CODSIGA", pedido.getCodigoSap());
        values.put("ORC_TXT_JUSTIF", pedido.getObservacao());
        values.put("ORC_TXT_OBSNF", pedido.getObservacaoNotaFiscal());
        values.put("ORC_TXT_TPVEND", pedido.getCodigoTipoVenda());
        values.put("ORC_TXT_CLIENTECOD", pedido.getCodigoCliente());
        values.put("ORC_INT_STATUS", pedido.getCodigoStatus());
        values.put("ORC_INT_ORIGEM", pedido.getCodigoOrigem());
        values.put("ORC_DAT_ORCAMENTO", FormatUtils.toDefaultDateFormat(pedido.getDataCadastro()));
        values.put("ORC_TXT_UNMED", pedido.getUnidadeMedida());
        values.put("ORC_TXT_ESPECIFIC2", pedido.getOrdemCompra());
        values.put("ORC_TXT_ESPECIFIC3", pedido.getEmpenho());
        values.put("ORC_INT_POSITIVACAO", pedido.getIsPositivacao());

        if (pedido.getLojaEntrega() != null) {
            values.put("ORC_TXT_LOJAENTREGA", pedido.getLojaEntrega());
        }

        if (pedido.getCodigoAgenda() != null) {
            values.put("ORC_TXT_AGENDA", pedido.getCodigoAgenda());
            values.put("ORC_INT_AGENDA", pedido.getTipoAgenda());
        }
        values.put("ORC_TXT_ESPECIFIC3", pedido.getEmpenho());

        try {
            values.put("ORC_DAT_FATURAR_EM", FormatUtils.toDefaultDateFormat(pedido.getEntregaEm()));
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        try {
            values.put("ORC_DAT_ENVIO", FormatUtils.toDefaultDateFormat(pedido.getDataEnvio()));
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        values.put("ORC_TXT_PLANTA", pedido.getCodigoPlanta());
        values.put("ORC_INT_GRUPOPROD", pedido.getCodigoGrupo());
        values.put("ORC_TXT_IDCONDPAG", pedido.getCodigoCondPag());
        values.put("ORC_TXT_ID_VEN", pedido.getCodigoPedidoVenda());
        values.put("ORC_TXT_URGENTE", pedido.getUrgente());

        if (pedido.getTipoVenda() != null) {
            values.put("ORC_TXT_TPVEND", pedido.getTipoVenda().getCodigo());
        }

        return values;
    }

    public class PedidoCursorWrapper extends CursorWrapper {

        public PedidoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Pedido getPedido() {
            Pedido pedido = new Pedido();
            pedido.setCodigo(getString(getColumnIndex("ORC_TXT_ID")));
            pedido.setCodigoEmpresa(getString(getColumnIndex("ORC_TXT_EMP")));
            pedido.setCodigoFilial(getString(getColumnIndex("ORC_TXT_FILIAL")));
            pedido.setCodigoVendedor(getString(getColumnIndex("ORC_TXT_VENDCOD")));
            pedido.setCodigoSap(getString(getColumnIndex("ORC_TXT_CODSIGA")));
            pedido.setObservacao(getString(getColumnIndex("ORC_TXT_JUSTIF")));
            pedido.setObservacaoNotaFiscal(getString(getColumnIndex("ORC_TXT_OBSNF")));
            pedido.setCodigoTipoVenda(getString(getColumnIndex("ORC_TXT_TPVEND")));
            pedido.setCodigoCliente(getString(getColumnIndex("ORC_TXT_CLIENTECOD")));
            pedido.setCodigoStatus(getInt(getColumnIndex("ORC_INT_STATUS")));
            pedido.setCodigoOrigem(getInt(getColumnIndex("ORC_INT_ORIGEM")));
            pedido.setUnidadeMedida(getString(getColumnIndex("ORC_TXT_UNMED")));
            pedido.setOrdemCompra(getString(getColumnIndex("ORC_TXT_ESPECIFIC2")));
            pedido.setEmpenho(getString(getColumnIndex("ORC_TXT_ESPECIFIC3")));
            pedido.setCodigoPlanta(getString(getColumnIndex("ORC_TXT_PLANTA")));
            pedido.setCodigoGrupo(getString(getColumnIndex("ORC_INT_GRUPOPROD")));
            pedido.setCodigoCondPag(getString(getColumnIndex("ORC_TXT_IDCONDPAG")));
            pedido.setUrgente(getString(getColumnIndex("ORC_TXT_URGENTE")));
            pedido.setCodigoPedidoVenda(getString(getColumnIndex("ORC_TXT_ID_VEN")));
            pedido.setValorTotal(getDouble(getColumnIndex("VL_TOTAL")));
            pedido.setCodigoAgenda(getString(getColumnIndex("ORC_TXT_AGENDA")));
            pedido.setTipoAgenda(getInt(getColumnIndex("ORC_INT_AGENDA")));
            pedido.setLojaEntrega(getString(getColumnIndex("ORC_TXT_LOJAENTREGA")));
            pedido.setIsPositivacao(getInt(getColumnIndex("ORC_INT_POSITIVACAO")));

            try {
                pedido.setEntregaEm(FormatUtils.toDate(
                        getString(getColumnIndex("ORC_DAT_FATURAR_EM"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getPedido: " + parseEx.toString());
            }

            try {
                pedido.setDataEnvio(FormatUtils.toDate(
                        getString(getColumnIndex("ORC_DAT_ENVIO"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getPedido: " + parseEx.toString());
            }

            try {
                pedido.setDataCadastro(FormatUtils.toDate(
                        getString(getColumnIndex("ORC_DAT_ORCAMENTO"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getPedido: " + parseEx.toString());
            }

            try {
                pedido.setDataImportacaoSAP(FormatUtils.toDate(
                        getString(getColumnIndex("ORC_DAT_IMPORTACAO_SAP"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getPedido: " + parseEx.toString());
            }

            return pedido;
        }
    }

    public class PedidoBonificadoWrapper extends CursorWrapper {

        public PedidoBonificadoWrapper(Cursor cursor) {
            super(cursor);
        }

        public PedidoBonificado getPedidoBonificado() {
            PedidoBonificado pedido = new PedidoBonificado();
            pedido.setCodigo(getString(getColumnIndex("COD")));
            pedido.setCodigoCondPag(getString(getColumnIndex("ORC_TXT_IDCONDPAG")));
            pedido.setUnidadeMedida(getString(getColumnIndex("ORC_TXT_UNMED")));

            try {
                pedido.setEntregueEm(FormatUtils.toDate(
                        getString(getColumnIndex("ORC_DAT_FATURAR_EM"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getPedidoBon: " + parseEx.toString());
            }
            return pedido;
        }
    }

    public class NotaFiscalWrapper extends CursorWrapper {

        public NotaFiscalWrapper(Cursor cursor) {
            super(cursor);
        }

        public NotaFiscal getNotaFiscal(String unidadeNegocio) {
            NotaFiscal nf = new NotaFiscal();
            nf.setNotaFiscal(getString(getColumnIndex("NUM_NF")));
            nf.setSerieNotaFiscal(getString(getColumnIndex("NUM_SERIE_NF")));
            nf.setPedido(getString(getColumnIndex("NUM_PEDIDO")));
            nf.setSap(getString(getColumnIndex("ORC_TXT_CODSIGA")));

            nf.setData(getString(getColumnIndex("DAT_EMISSAO_NF")));
            nf.setStatus(getInt(getColumnIndex("ORC_INT_STATUS")));
            nf.setOrigem(getInt(getColumnIndex("ORC_INT_ORIGEM")));

            return nf;
        }
    }
}
