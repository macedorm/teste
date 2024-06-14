
package br.com.jjconsulting.mobile.dansales.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.ClienteResumo;
import br.com.jjconsulting.mobile.dansales.model.CondicaoPagamento;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.MathUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ClienteDao extends BaseDansalesDao {

    private BandeiraDao bandeiraDao;
    private CondicaoPagamentoDao condicaoPagamentoDao;
    private PlanoCampoDao planoCampoDao;
    private LayoutDao layoutDao;

    public ClienteDao(Context context) {
        super(context);
        bandeiraDao = new BandeiraDao(context);
        condicaoPagamentoDao = new CondicaoPagamentoDao(context);
        planoCampoDao = new PlanoCampoDao(context);
        layoutDao = new LayoutDao(context);
    }

    public ClienteResumo getResume (String codigoUnidadeNegocio, String codigoCliente) {
        ClienteResumo cliente = null;

        StringBuilder query = new StringBuilder();
        query.append("select ULT_CHECKLIST_NOTA,");
        query.append(" ULT_CHECKLIST_ESPACO ");
        query.append(" COD_EMITENTE, ");
        query.append(" COD_UNID_NEGOC ");
        query.append(" from TB_DECLIENTEUN_CHECKLIST ");
        query.append(" WHERE COD_UNID_NEGOC = ? AND COD_EMITENTE = ?");

        String[] args = {codigoUnidadeNegocio, codigoCliente};
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cliente = new ClienteResumo();
                cliente.setNota(cursor.getInt(0));
                cliente.setEspaco(cursor.getInt(1));

                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return cliente;
    }

    public Cliente get(String codigoUnidadeNegocio, String codigoCliente) {
        Cliente cliente = null;

        StringBuilder query = new StringBuilder();
        query.append("select CLI.COD_EMITENTE,");
        query.append(" CLI.LATITUDE,");
        query.append(" CLI.LONGITUDE,");
        query.append(" CLI.NOM_CLIENTE,");
        query.append(" CLI.NOME_ABREVIADO,");
        query.append(" CLI.DSC_ENDERECO,");
        query.append(" CLI.COD_CEP,");
        query.append(" CLI.DSC_BAIRRO,");
        query.append(" CLI.COD_CIDADE,");
        query.append(" CLI.COD_ESTADO,");
        query.append(" CLI.NUM_CNPF_CPF,");
        query.append(" CLI.COD_BANDEIRA,");
        query.append(" cun.COD_CANAL_VENDA,");
        query.append(" cun.CONDICAO_DE_PAGAMENTO,");
        query.append(" cun.PESO_MINIMO,");
        query.append(" cun.VALOR_MINIMO,");
        query.append(" cun.COD_UNID_NEGOC,");
        query.append(" cun.PLANTA,");
        query.append(" cun.INATIVO,");
        query.append(" cun.ULT_INSPETORIA_NOTA,");
        query.append(" cun.ULT_INSPETORIA_ESPACO,");
        query.append(" cun.ULT_CHECKLIST_NOTA,");
        query.append(" cun.ULT_CHECKLIST_ESPACO,");
        query.append(" cun.STATUS_CRED AS A1_LEGEND,");
        query.append(" case when (cun.COD_CANAL_VENDA = '55') then '1' else '0' end ONLYAGENDA,");
        query.append(" case when (UNIDADE_DE_VENDA_PERMITIDA = 'PACU'");
        query.append("   OR UNIDADE_DE_VENDA_PERMITIDA IS NULL)");
        query.append(" then 'PAC' else UNIDADE_DE_VENDA_PERMITIDA end UNMED");
        query.append(" from TB_DECLIENTE CLI");
        query.append(" inner join TB_DECLIENTEUN CUN");
        query.append(" on CUN.COD_EMITENTE = CLI.COD_EMITENTE");
        query.append(" and CUN.COD_UNID_NEGOC = ?");
        query.append(" where CLI.COD_EMITENTE = ?");

        String[] args = {codigoUnidadeNegocio, codigoCliente};
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cliente = new ClienteCursorWrapper(cursor).getCliente();
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return cliente != null ? setAggregatedData(cliente) : null;
    }

    public Cliente getNoAgregated(String codigoUnidadeNegocio, String codigoCliente) {
        Cliente cliente = null;

        StringBuilder query = new StringBuilder();
        query.append("select CLI.COD_EMITENTE,");
        query.append(" CLI.LATITUDE,");
        query.append(" CLI.LONGITUDE,");
        query.append(" CLI.NOM_CLIENTE,");
        query.append(" CLI.NOME_ABREVIADO,");
        query.append(" CLI.DSC_ENDERECO,");
        query.append(" CLI.COD_CEP,");
        query.append(" CLI.DSC_BAIRRO,");
        query.append(" CLI.COD_CIDADE,");
        query.append(" CLI.COD_ESTADO,");
        query.append(" CLI.NUM_CNPF_CPF,");
        query.append(" CLI.COD_BANDEIRA,");
        query.append(" cun.COD_CANAL_VENDA,");
        query.append(" cun.CONDICAO_DE_PAGAMENTO,");
        query.append(" cun.PESO_MINIMO,");
        query.append(" cun.VALOR_MINIMO,");
        query.append(" cun.COD_UNID_NEGOC,");
        query.append(" cun.PLANTA,");
        query.append(" cun.INATIVO,");
        query.append(" cun.ULT_INSPETORIA_NOTA,");
        query.append(" cun.ULT_INSPETORIA_ESPACO,");
        query.append(" cun.ULT_CHECKLIST_NOTA,");
        query.append(" cun.ULT_CHECKLIST_ESPACO,");
        query.append(" cun.STATUS_CRED AS A1_LEGEND,");
        query.append(" case when (cun.COD_CANAL_VENDA = '55') then '1' else '0' end ONLYAGENDA,");
        query.append(" case when (UNIDADE_DE_VENDA_PERMITIDA = 'PACU'");
        query.append("   OR UNIDADE_DE_VENDA_PERMITIDA IS NULL)");
        query.append(" then 'PAC' else UNIDADE_DE_VENDA_PERMITIDA end UNMED");
        query.append(" from TB_DECLIENTE CLI");
        query.append(" inner join TB_DECLIENTEUN CUN");
        query.append(" on CUN.COD_EMITENTE = CLI.COD_EMITENTE");
        query.append(" and CUN.COD_UNID_NEGOC = ?");
        query.append(" where CLI.COD_EMITENTE = ?");

        String[] args = {codigoUnidadeNegocio, codigoCliente};
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cliente = new ClienteCursorWrapper(cursor).getCliente();
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return cliente;
    }

    public ArrayList<Cliente> getAll(String codigoUsuario,
                                     String codigoUnidadeNegocio,
                                     String nome,
                                     String codigoPesquisa,
                                     ClienteFilter filter,
                                     int indexOffSet) {

        long start = System.currentTimeMillis();

        String whereClause = "where cur.DEL_FLAG = '0'"
                + " and (cur.SEQUENCIA = '000' OR SEQUENCIA >= '100') "
                + " and cur.COD_UNID_NEGOC = ?"
                + " and cur.DEL_FLAG = '0'";

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUnidadeNegocio);

        if (!TextUtils.isNullOrEmpty(nome)) {
            whereClause += " and (cli.NOM_CLIENTE LIKE ? or cli.COD_CIDADE LIKE ?" +
                    " or cli.COD_EMITENTE LIKE ? or cli.DSC_ENDERECO LIKE ?) ";
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
        }

        if (filter != null) {
            if (filter.getOrganizacao() != null) {
                whereClause += " and cli.COD_ORGANIZACAO = ? ";
                whereArgs.add(filter.getOrganizacao().getCodigo());
            }

            if (filter.getBandeira() != null) {
                whereClause += " and cli.COD_BANDEIRA = ? ";
                whereArgs.add(filter.getBandeira().getCodigoBandeira());
            }

            if (filter.getStatus() != null) {
                if (filter.getStatus() == Cliente.STATUS_CREDITO_GREEN) {
                    whereClause += " and (A1_LEGEND = '" + filter.getStatus() + "' OR A1_LEGEND ISNULL ) ";
                } else {
                    whereClause += " and A1_LEGEND = '" + filter.getStatus() + "' ";
                }
            }

            if (filter.getPlanoCampo() != null) {
                whereClause += " and cli.COD_EMITENTE in (" + dayOfWeek(codigoUnidadeNegocio, filter.getPlanoCampo()) + ")";
            }
        }

        if (filter != null && filter.getHierarquiaComercial() != null && filter.getHierarquiaComercial().size() > 0) {
            whereClause += " and cur.COD_REG_FUNC in (";
            for (int i = 0; i < filter.getHierarquiaComercial().size(); i++) {
                Usuario usuario = filter.getHierarquiaComercial().get(i);
                if (i > 0) {
                    whereClause += ",";
                }
                whereClause += "'" + usuario.getCodigo() + "'";
            }
            whereClause += ") ";

        } else {
            whereClause += " and cur.COD_REG_FUNC = ?";
            whereArgs.add(codigoUsuario);
        }

        if (!TextUtils.isNullOrEmpty(codigoPesquisa)){
            PesquisaAcessoDao acessoDao = new PesquisaAcessoDao(getContext());
            whereClause += acessoDao.getClienteFilter(Integer.parseInt(codigoPesquisa));
        }

        if (indexOffSet > -1) {
            whereClause += " " + "Limit " + Config.SIZE_PAGE + " OFFSET " + indexOffSet;
        }

        long finalTime = System.currentTimeMillis() - start;

        LogUser.log(Config.TAG, "tempo carregar cliente: " + finalTime);

        return setAggregatedData(query(whereClause, whereArgs.toArray(new String[0]), null, false));
    }

    public ArrayList<Cliente> getAll(String codigoUsuario,
                                     String codigoUnidadeNegocio,
                                     String nome,
                                     String codigoPesquisa,
                                     ClienteFilter filter,
                                     int indexOffSet, boolean isRota) {

        long start = System.currentTimeMillis();

        String whereClause = "where cur.DEL_FLAG = '0'"
                + " and (cur.SEQUENCIA = '000' OR SEQUENCIA >= '100') "
                + " and cur.COD_UNID_NEGOC = ?"
                + " and cur.DEL_FLAG = '0'";

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(codigoUnidadeNegocio);

        if (!TextUtils.isNullOrEmpty(nome)) {
            whereClause += " and (cli.NOM_CLIENTE LIKE ? or cli.COD_CIDADE LIKE ?" +
                    " or cli.COD_EMITENTE LIKE ? or cli.DSC_ENDERECO LIKE ?) ";
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
        }

        if (filter != null) {
            if (filter.getOrganizacao() != null) {
                whereClause += " and cli.COD_ORGANIZACAO = ? ";
                whereArgs.add(filter.getOrganizacao().getCodigo());
            }

            if (filter.getBandeira() != null) {
                whereClause += " and cli.COD_BANDEIRA = ? ";
                whereArgs.add(filter.getBandeira().getCodigoBandeira());
            }

            if (filter.getStatus() != null) {
                if (filter.getStatus() == Cliente.STATUS_CREDITO_GREEN) {
                    whereClause += " and (A1_LEGEND = '" + filter.getStatus() + "' OR A1_LEGEND ISNULL ) ";
                } else {
                    whereClause += " and A1_LEGEND = '" + filter.getStatus() + "' ";
                }
            }

            if (filter.getPlanoCampo() != null) {
                whereClause += " and cli.COD_EMITENTE in (" + dayOfWeek(codigoUnidadeNegocio, filter.getPlanoCampo()) + ")";
            }
        }

        if (filter != null && filter.getHierarquiaComercial() != null && filter.getHierarquiaComercial().size() > 0) {
            whereClause += " and cur.COD_REG_FUNC in (";
            for (int i = 0; i < filter.getHierarquiaComercial().size(); i++) {
                Usuario usuario = filter.getHierarquiaComercial().get(i);
                if (i > 0) {
                    whereClause += ",";
                }
                whereClause += "'" + usuario.getCodigo() + "'";
            }
            whereClause += ") ";

        } else {
            whereClause += " and cur.COD_REG_FUNC = ?";
            whereArgs.add(codigoUsuario);
        }

        if (!TextUtils.isNullOrEmpty(codigoPesquisa)){
            PesquisaAcessoDao acessoDao = new PesquisaAcessoDao(getContext());
            whereClause += acessoDao.getClienteFilter(Integer.parseInt(codigoPesquisa));
        }

        if (indexOffSet > -1) {
            whereClause += " " + "Limit " + Config.SIZE_PAGE + " OFFSET " + indexOffSet;
        }

        long finalTime = System.currentTimeMillis() - start;

        LogUser.log(Config.TAG, "tempo carregar cliente: " + finalTime);

        return setAggregatedData(query(whereClause, whereArgs.toArray(new String[0]),null, isRota));
    }

    /**
     * Load and set aggregated data like bandeira and more.
     */
    private Cliente setAggregatedData(Cliente cliente) {
        if (cliente.getCodigoBandeira() != null) {
            cliente.setBandeira(bandeiraDao.get(cliente.getCodigoBandeira()));
        }

        if (cliente.getCodigoCondicaoPagamento() != null) {
            cliente.setCondicaoPagamento(condicaoPagamentoDao.get(
                    cliente.getCodigoCondicaoPagamento()));

            if(cliente.getCondicaoPagamento() == null){
                CondicaoPagamento condicaoPagamento = new CondicaoPagamento();
                condicaoPagamento.setNome(cliente.getCodigoCondicaoPagamento());
                condicaoPagamento.setCodigo(cliente.getCodigoCondicaoPagamento());
                cliente.setCondicaoPagamento(condicaoPagamento);
            }
        }

        Current current = Current.getInstance(getContext());
        boolean habPositivacao = current.getUsuario().getPerfil().isHabPositivacao();

        if (cliente.getPlanoCampo() == null && habPositivacao) {
            cliente.setPlanoCampo(planoCampoDao.getPlanoCampo(cliente.getCodigoUnidadeNegocio(), cliente.getCodigo()));
        }

        UnidadeNegocio unidadeNegocio = current.getUnidadeNegocio();
        if(unidadeNegocio != null){
            Layout layout = layoutDao.getLayout(unidadeNegocio.getCodigo(), cliente.getCodigo(), new Date());
            cliente.setLayout(layout);
        }

        return cliente;
    }

    /**
     * Load and set aggregated data like bandeira and more.
     */
    private ArrayList<Cliente> setAggregatedData(ArrayList<Cliente> clientes) {
        Map<String, Bandeira> bandeiras = new HashMap<>();
        Map<String, CondicaoPagamento> condicoes = new HashMap<>();

        for (Cliente cliente : clientes) {
            if (bandeiras.containsKey(cliente.getCodigoBandeira())) {
                cliente.setBandeira(bandeiras.get(cliente.getCodigoBandeira()));
            } else if (cliente.getCodigoBandeira() != null) {
                Bandeira bandeira = bandeiraDao.get(cliente.getCodigoBandeira());
                if (bandeira != null) {
                    cliente.setBandeira(bandeira);
                    bandeiras.put(bandeira.getCodigoBandeira(), bandeira);
                }
            }

            if (condicoes.containsKey(cliente.getCodigoCondicaoPagamento())) {
                cliente.setCondicaoPagamento(condicoes.get(cliente.getCodigoCondicaoPagamento()));
            } else if (cliente.getCodigoCondicaoPagamento() != null) {
                CondicaoPagamento condicao = condicaoPagamentoDao.get(
                        cliente.getCodigoCondicaoPagamento());
                if (condicao != null) {
                    cliente.setCondicaoPagamento(condicao);
                    condicoes.put(condicao.getCodigo(), condicao);
                } else {
                    CondicaoPagamento condicaoPagamento = new CondicaoPagamento();
                    condicaoPagamento.setCodigo( cliente.getCodigoCondicaoPagamento());
                    condicaoPagamento.setNome(cliente.getCodigoCondicaoPagamento());
                    cliente.setCondicaoPagamento(condicaoPagamento);
                }
            }

            boolean habPositivacao = Current.getInstance(getContext()).getUsuario().getPerfil().isHabPositivacao();
            if (cliente.getPlanoCampo() == null && habPositivacao) {
                cliente.setPlanoCampo(planoCampoDao.getPlanoCampo(cliente.getCodigoUnidadeNegocio(), cliente.getCodigo()));
            }
        }

        return clientes;
    }

    private ArrayList<Cliente> query(String whereClause, String[] args, String orderBy, boolean isRota) {
        ArrayList<Cliente> clientes = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("select distinct cli.COD_EMITENTE,");
        query.append(" cli.LATITUDE,");
        query.append(" cli.LONGITUDE,");
        query.append(" cli.NOM_CLIENTE,");
        query.append(" cli.NOME_ABREVIADO,");
        query.append(" cli.DSC_ENDERECO,");
        query.append(" cli.COD_CEP,");
        query.append(" cli.DSC_BAIRRO,");
        query.append(" cli.COD_CIDADE,");
        query.append(" cli.COD_ESTADO,");
        query.append(" cli.NUM_CNPF_CPF,");
        query.append(" cli.COD_BANDEIRA,");
        query.append(" cun.COD_CANAL_VENDA,");
        query.append(" cun.CONDICAO_DE_PAGAMENTO,");
        query.append(" cun.PESO_MINIMO,");
        query.append(" cun.VALOR_MINIMO,");
        query.append(" cun.COD_UNID_NEGOC,");
        query.append(" cun.PLANTA,");
        query.append(" cun.ULT_INSPETORIA_NOTA,");
        query.append(" cun.ULT_INSPETORIA_ESPACO,");
        query.append(" cun.ULT_CHECKLIST_NOTA,");
        query.append(" cun.INATIVO,");
        query.append(" cun.ULT_CHECKLIST_ESPACO,");
        query.append(" cun.STATUS_CRED AS A1_LEGEND,");
        query.append(" case when (cun.COD_CANAL_VENDA = '55') then '1' else '0' end ONLYAGENDA,");
        query.append(" case when (UNIDADE_DE_VENDA_PERMITIDA = 'PACU'");
        query.append(" OR UNIDADE_DE_VENDA_PERMITIDA IS NULL)");
        query.append(" then 'PAC' else UNIDADE_DE_VENDA_PERMITIDA end UNMED");
        query.append(" from TB_DECLIUNREG cur");
        query.append(" inner join TB_DECLIENTEUN cun");
        query.append("   on cun.COD_EMITENTE = cur.COD_EMITENTE");
        query.append("   and cun.COD_UNID_NEGOC = cur.COD_UNID_NEGOC");
        if (!isRota){
            query.append(" and cun.INATIVO <> '1'");
        }
        query.append("   and cun.DEL_FLAG = '0'");
        query.append(" inner join TB_DECLIENTE cli");
        query.append("   on cli.COD_EMITENTE = cun.COD_EMITENTE");
        query.append("   and cli.DEL_FLAG = '0'");

        if (whereClause != null) {
            query.append(" " + whereClause);
        }

        if (orderBy != null) {
            query.append(" " + orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                clientes.add(new ClienteCursorWrapper(cursor).getCliente());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return clientes;
    }

    public String getDataUltimoPedido(Cliente cliente) {

        String dtUltPed = " - ";
        String codigoCliente = cliente.getCodigo();
        String codigoUnidadeNegocio = cliente.getCodigoUnidadeNegocio();
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                codigoUnidadeNegocio);

        StringBuilder sqlPedidos = new StringBuilder();
        sqlPedidos.append("SELECT ORC_DAT_ORCAMENTO ");
        sqlPedidos.append("FROM TBORCAMENTO ");
        sqlPedidos.append("WHERE ORC_INT_STATUS NOT IN(");
        sqlPedidos.append(StatusPedido.CANCELADO);
        sqlPedidos.append(",");
        sqlPedidos.append(StatusPedido.NAO_ENVIADO);
        sqlPedidos.append(",");
        sqlPedidos.append(StatusPedido.REPROVADO);
        sqlPedidos.append(",");
        sqlPedidos.append(StatusPedido.SEM_REGRA);
        sqlPedidos.append(") AND ORC_TXT_EMP = '");
        sqlPedidos.append(empresaFilial[0]);
        sqlPedidos.append("' AND ORC_TXT_FILIAL = '");
        sqlPedidos.append(empresaFilial[1]);
        sqlPedidos.append("' AND ORC_TXT_CLIENTECOD = '");
        sqlPedidos.append(codigoCliente);
        sqlPedidos.append("' ORDER BY ORC_DAT_ENVIO DESC ");
        sqlPedidos.append("LIMIT 1 ");

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sqlPedidos.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                @SuppressLint("Range") String dt = cursor.getString(cursor.getColumnIndex("ORC_DAT_ORCAMENTO"));
                dtUltPed = FormatUtils.toDefaultDateAndHourFormat(this.getContext(), FormatUtils.toDate(dt));

                break;
            }

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        } finally {
            database.close();
        }
        return dtUltPed;
    }


    public boolean hasCampanha(String codigoCliente) {

        StringBuilder sqlPedidos = new StringBuilder();

        sqlPedidos.append("SELECT COUNT(*) QTD ");
        sqlPedidos.append("FROM CAD_CLIENTEPOLITICA ");
        sqlPedidos.append("WHERE COD_CLIENTE = ? ");
        sqlPedidos.append("AND COD_PROMO IN(");
        sqlPedidos.append("SELECT COD_PROMO ");
        sqlPedidos.append("FROM CAD_POLITICA ");
        sqlPedidos.append("WHERE ? BETWEEN DT_INI AND DT_FIM ");
        sqlPedidos.append(") ");

        String currentDate = FormatUtils.toTextToCompareDateInSQlite(Calendar.getInstance().getTime());
        String[] args = {codigoCliente, currentDate};

        int qtd = 0;
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sqlPedidos.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                qtd = cursor.getInt(cursor.getColumnIndex("QTD"));
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        } finally {
            database.close();
        }

        return (qtd > 0);
    }

    private String dayOfWeek(String unidadeNegocio, int diaVisita){

        StringBuilder sSql = new StringBuilder();

        sSql.append("SELECT TB.PDC_TXT_CODCLIENTE ");
        sSql.append("FROM TB_PLANODECAMPO TB ");
        sSql.append("INNER JOIN (");
        sSql.append("SELECT PDC_TXT_UNID_NEGOC , PDC_TXT_CODCLIENTE, MAX(PDC_DAT_VIGENCIA) AS PDC_DAT_VIGENCIA ");
        sSql.append("FROM TB_PLANODECAMPO ");
        sSql.append("WHERE PDC_TXT_UNID_NEGOC = '");
        sSql.append(unidadeNegocio);
        sSql.append("' AND PDC_DAT_VIGENCIA <= datetime('" + FormatUtils.toTextToCompareDateInSQlite(new Date()) + "')");
        sSql.append("GROUP BY PDC_TXT_UNID_NEGOC, PDC_TXT_CODCLIENTE ");
        sSql.append(") SS ");
        sSql.append("ON TB.PDC_TXT_UNID_NEGOC = SS.PDC_TXT_UNID_NEGOC ");
        sSql.append("AND TB.PDC_TXT_CODCLIENTE = SS.PDC_TXT_CODCLIENTE ");
        sSql.append("AND TB.PDC_DAT_VIGENCIA = SS.PDC_DAT_VIGENCIA ");

        if(diaVisita == 1) {
            sSql.append("WHERE PDC_TXT_VIS_DOM <> 'N'  ");
        }else if(diaVisita == 2) {
            sSql.append("WHERE PDC_TXT_VIS_SEG <> 'N' ");
        } else if(diaVisita == 3) {
            sSql.append("WHERE PDC_TXT_VIS_TER <> 'N' ");
        } else if(diaVisita == 4) {
            sSql.append("WHERE PDC_TXT_VIS_QUA <> 'N' ");
        }else if(diaVisita == 5) {
            sSql.append("WHERE PDC_TXT_VIS_QUI <> 'N' ");
        }else if(diaVisita == 6) {
            sSql.append("WHERE PDC_TXT_VIS_SEX <> 'N' ");
        }else if(diaVisita == 7) {
            sSql.append("WHERE PDC_TXT_VIS_SAB <> 'N' ");
        }

        return sSql.toString();
    }


    public class ClienteCursorWrapper extends CursorWrapper {

        public ClienteCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Cliente getCliente() {
            Cliente cliente = new Cliente();
            cliente.setCodigo(getString(getColumnIndex("COD_EMITENTE")));
            cliente.setNome(getString(getColumnIndex("NOM_CLIENTE")));
            cliente.setNomeReduzido(getString(getColumnIndex("NOME_ABREVIADO")));
            cliente.setEndereco(getString(getColumnIndex("DSC_ENDERECO")));
            cliente.setCep(getString(getColumnIndex("COD_CEP")));
            cliente.setBairro(getString(getColumnIndex("DSC_BAIRRO")));
            cliente.setMunicipio(getString(getColumnIndex("COD_CIDADE")));
            cliente.setUf(getString(getColumnIndex("COD_ESTADO")));
            cliente.setCnpj(getString(getColumnIndex("NUM_CNPF_CPF")));
            cliente.setCodigoBandeira(getString(getColumnIndex("COD_BANDEIRA")));
            cliente.setCodigoCondicaoPagamento(getString(getColumnIndex(
                    "CONDICAO_DE_PAGAMENTO")));


            cliente.setStatusCredito(getInt(getColumnIndex("A1_LEGEND")));
            cliente.setExclusivoPedidoAgenda("1".equals(getString(getColumnIndex(
                    "ONLYAGENDA"))));
            cliente.setUnidadeMedidaPadrao(getString(getColumnIndex("UNMED")));
            cliente.setCodCanal(getString(getColumnIndex("COD_CANAL_VENDA")));

            if (!TextUtils.isNullOrEmpty(getString(getColumnIndex("PESO_MINIMO")))){
                cliente.setPesoMin(MathUtils.toDoubleOrDefaultUsingLocalePTBR(
                        getString(getColumnIndex("PESO_MINIMO"))));
            } else{
                cliente.setPesoMin(0);
            }
            cliente.setValMin(MathUtils.toDoubleOrDefaultUsingLocalePTBR(
                    getString(getColumnIndex("VALOR_MINIMO"))));
            cliente.setCodigoUnidadeNegocio(getString(getColumnIndex("COD_UNID_NEGOC")));
            cliente.setPlanta(getString(getColumnIndex("PLANTA")));
            cliente.setInativo("1".equals(getString(getColumnIndex(
                    "INATIVO"))));
            try{
                cliente.setLatitude(getDouble(getColumnIndex("LATITUDE")));
                cliente.setLongitude(getDouble(getColumnIndex("LONGITUDE")));
            }catch (Exception ex){
                LogUser.log(Config.TAG, ex.toString());
            }

            try {
                cliente.setUltCheckListEspaco(getFloat(getColumnIndex("ULT_CHECKLIST_ESPACO")));
                cliente.setUltCheckListNota(getFloat(getColumnIndex("ULT_CHECKLIST_NOTA")));

                cliente.setUltInspetoriaEspaco(getFloat(getColumnIndex("ULT_INSPETORIA_ESPACO")));
                cliente.setUltInspetoriaNota(getFloat(getColumnIndex("ULT_INSPETORIA_NOTA")));

            }catch (Exception ex){
                LogUser.log(ex.toString());
            }

            return cliente;
        }
    }
}
