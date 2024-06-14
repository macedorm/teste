package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.data.RotasFilter;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.TActionRotaGuiada;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RotaGuiadaDao extends BaseDansalesDao {

    private ClienteDao mClienteDao;
    private ArrayList<Cliente> mCliente;

    public RotaGuiadaDao(Context context) {
        super(context);
        mClienteDao = new ClienteDao(context);
    }

    /**
     * Atualiza DT_ULT_ALT após envio para servidor
     * @param rota
     * @param date
     */
    public void updateSync(Rotas rota, Date date) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(date));

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA", contentValues,
                "RG_TXT_CODCLI = ? AND RG_DAT_DIAPLANO = ? AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ",
                new String[]{rota.getCodCliente(), rota.getDate(), rota.getCodRegFunc(), rota.getCodUnidNeg()});
    }

    /**
     * Justifica uma rota não realizada
     * @param rota
     * @param multiValues
     */
    public void setJustificaVisita(Rotas rota, MultiValues multiValues){
        ContentValues contentValues = new ContentValues();
        contentValues.put("RG_INT_JUSTIF_VISITA", multiValues.getValCod());
        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA", contentValues,
                "RG_TXT_CODCLI = ? AND RG_DAT_DIAPLANO = ? AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ",
                new String[]{rota.getCodCliente(), rota.getDate(), rota.getCodRegFunc(), rota.getCodUnidNeg()});
    }

    public void setJustificaCheckoutManual(Rotas rota, MultiValues multiValues){
        ContentValues contentValues = new ContentValues();
        contentValues.put("RG_INT_JUSTIF_VISITA", multiValues.getValCod());

        if(rota.getCheckout() == null){
            contentValues.putNull("RG_DAT_CHECKOUT");
        } else {
            contentValues.put("RG_DAT_CHECKOUT", rota.getCheckout());
        }

        contentValues.put("RG_FLO_DIFF_CHECKIN_OUT", rota.getDiffCheck());
        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA", contentValues,
                "RG_TXT_CODCLI = ? AND RG_DAT_DIAPLANO = ? AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ",
                new String[]{rota.getCodCliente(), rota.getDate(), rota.getCodRegFunc(), rota.getCodUnidNeg()});

    }

    public void setJustificaPedido(Rotas rota, MultiValues multiValues){
        ContentValues contentValues = new ContentValues();
        contentValues.put("RG_INT_JUSTIF_PEDIDO", multiValues.getValCod());
        contentValues.putNull("DT_ULT_ALT");
        SQLiteDatabase database = getDb();

        database.update("TB_ROTAGUIADA", contentValues,
                "RG_TXT_CODCLI = ? AND RG_DAT_DIAPLANO = ? AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ",
                new String[]{rota.getCodCliente(), rota.getDate(), rota.getCodRegFunc(), rota.getCodUnidNeg()});
    }

    public void updateRota(Rotas rota, TActionRotaGuiada actionRotaGuiada){
        ContentValues contentValues = new ContentValues();
        contentValues.put("RG_DAT_CHECKIN", rota.getCheckin());
        contentValues.put("RG_INT_STATUS", rota.getStatus());
        contentValues.put("RG_TXT_CHECKIN_DENTRO", rota.isCheckinDentro());
        contentValues.put("RG_FLO_DIFF_CHECKIN_OUT", rota.getDiffCheck());
        contentValues.put("RG_FLO_DIFF_PAUSE", rota.getDiffPause());

        if(rota.getCheckout() == null){
            contentValues.putNull("RG_DAT_CHECKOUT");
        } else {
            contentValues.put("RG_DAT_CHECKOUT", rota.getCheckout());
        }

        if(actionRotaGuiada == TActionRotaGuiada.CHECKIN){
            contentValues.putNull("RG_INT_JUSTIF_VISITA");
            contentValues.putNull("RG_INT_JUSTIF_PEDIDO");
            contentValues.putNull("RG_TXT_JUSTIF_ATIV_OBRIG");
        }

        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA", contentValues,
                "RG_TXT_CODCLI = ? AND RG_DAT_DIAPLANO = ? AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ",
                new String[]{rota.getCliente().getCodigo(), rota.getDate(), rota.getCodRegFunc(), rota.getCodUnidNeg()});
     }

    public void deleteRota(String codCliente, String date, String codUser, String uniNeg){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DEL_FLAG", "1");
        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA", contentValues,
                "RG_TXT_CODCLI = ? AND RG_DAT_DIAPLANO = ? AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ",
                new String[]{codCliente, date, codUser, uniNeg});
    }

    public void deleteRotaTarefa(String codCliente, String date, String codUser, String uniNeg){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DEL_FLAG", "1");
        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA_TAREFA", contentValues,
                "TSK_TXT_CODCLI = ? AND TSK_DAT_DIAPLANO = ? AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ",
                new String[]{codCliente, date, codUser, uniNeg});
    }

    /**
     * Simula uma routa
     * @param date
     * @param name
     * @param rotasFilter
     * @return
     */
    public List<Rotas> simulateRoute(Date date, String name, RotasFilter rotasFilter){
        ClienteFilter filter = new ClienteFilter();
        filter.setPlanoCampo(FormatUtils.getDayOfWeek(date));
        filter.setHierarquiaComercial(rotasFilter.getHierarquiaComercial());
        return initListRoute(false, date, name, filter);
    }

    /**
     * Simula uma routa
     * @param date
     * @return
     */
    public List<Rotas> simulateRoute(Date date, Usuario usuario){
        ClienteFilter filter = new ClienteFilter();
        filter.setPlanoCampo(FormatUtils.getDayOfWeek(date));
        List<Usuario> hierarquiaUsuarios = new ArrayList<>();
        hierarquiaUsuarios.add(usuario);

        filter.setHierarquiaComercial(hierarquiaUsuarios);
        return initListRoute(false, date, null, filter);
    }

    public Rotas getRota(String cliente, String diaPlano, Usuario usuario, String unidadeNegocio, boolean isSimulate, boolean isIgonrePlanoCampo){
        StringBuilder whereClause = new StringBuilder();
        List<String> whereArgs = new ArrayList<>();

        String orderBy = " order by RG_INT_STATUS ";

        whereClause.append(" WHERE RG_DAT_DIAPLANO = ? AND RG_TXT_CODCLI = ? ");
        whereClause.append("AND COD_UNID_NEGOC = ? AND COD_REG_FUNC = ? ");

        whereArgs.add(diaPlano);
        whereArgs.add(cliente);
        whereArgs.add(unidadeNegocio);
        whereArgs.add(usuario.getCodigo());

        List<Rotas> listRotas = query(true, isIgonrePlanoCampo, whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, null);

        if(isSimulate) {
            if (listRotas.size() > 0) {
                return listRotas.get(0);
            } else {
                mCliente = new ArrayList<>();
                mCliente.add(mClienteDao.get(unidadeNegocio, cliente));
                try {
                    listRotas = simulateRoute(FormatUtils.toDate(diaPlano), usuario);
                } catch (Exception ex) {
                    LogUser.log(ex.getMessage());
                }
            }
        }
        
        return listRotas.size() > 0 ? listRotas.get(0):null;
    }

    /**
     * Cria rota do dia
     * @param routeDay
     * @return
     */
    public List<Rotas> createListRoute(Date routeDay){
        ClienteFilter filter = new ClienteFilter();
        filter.setPlanoCampo(FormatUtils.getDayOfWeek(routeDay));
        return initListRoute(true, routeDay, null, filter);
    }

    private List<Rotas> initListRoute(boolean isCreate, Date routeDay, String name, ClienteFilter filter){
        List<Rotas> rotas = new ArrayList<>();

        Current current = Current.getInstance(getContext());
        String codUser = current.getUsuario().getCodigo();
        String codUnNeg = current.getUnidadeNegocio().getCodigo();

        if(filter.getHierarquiaComercial() != null && filter.getHierarquiaComercial().size() > 0){
            //Existindo hierarquia vai criar rota com base no código do usuário de cada um
            for (int i = 0; i < filter.getHierarquiaComercial().size(); i++) {
                Usuario usuario = filter.getHierarquiaComercial().get(i);
                rotas.addAll(createUserListRoute(isCreate, usuario.getCodigo(), codUnNeg, name, routeDay));

            }
        } else {
            rotas = createUserListRoute(isCreate, codUser, codUnNeg, name, routeDay);
        }

        return rotas;
    }

    public ArrayList<Cliente> loadingCliente(String codUser, String codUnNeg) {
        return mClienteDao.getAll(codUser, codUnNeg, null, null, null, -1, true);
    }

    public void setCliente(ArrayList<Cliente> cliente){
        mCliente = cliente;
    }

    private List<Rotas> createUserListRoute(boolean isInsert, String codUser, String codUnNeg, String name,  Date routeDay){
        List<Rotas> rotas = new ArrayList<>();

        if(mCliente == null){
            mCliente = mClienteDao.getAll(codUser, codUnNeg, name, null, null, -1, true);
        }

        for(Cliente cliente:mCliente) {
            if(cliente.getPlanoCampo() != null) {
                String planoCampo = PlanoCampoUtils.getPlanoCampoFromDate(cliente.getPlanoCampo(), routeDay);

                if (!PlanoCampoUtils.PEDIDO.equals(planoCampo) &&  !PlanoCampoUtils.TIPO_E.equals(planoCampo) && !PlanoCampoUtils.NAO_POSSUI.equals(planoCampo)) {
                        Rotas rota = newRoute(cliente, routeDay, true, codUser, codUnNeg);

                    if (rota != null && isInsert) {
                        insertRoute(rota);
                    }

                    rotas.add(rota);
                }
            }
        }

        mCliente = null;

        return  rotas;
    }

    public ArrayList<Cliente> getClientePlanoDia(String codUser, String codUnNeg, Date date){
         ArrayList<Cliente> clientes = mClienteDao.getAll(codUser, codUnNeg, null, null, null, -1, true );
        ArrayList<Cliente> clientesFinal = new ArrayList<>();
        for(Cliente cliente:clientes) {
            if(cliente.getPlanoCampo() != null) {
                String planoCampo = PlanoCampoUtils.getPlanoCampoFromDate(cliente.getPlanoCampo(), date);

                if (!PlanoCampoUtils.PEDIDO.equals(planoCampo) &&  !PlanoCampoUtils.TIPO_E.equals(planoCampo) && !PlanoCampoUtils.NAO_POSSUI.equals(planoCampo)) {
                    clientesFinal.add(cliente);
                }
            }
        }

        return  clientesFinal;
    }

    public Rotas newRoute(String codCliente, Date routeDay, boolean isRoute,  String codRecFunc, String codUnidNeg) {
        Cliente cliente = mClienteDao.get(codUnidNeg, codCliente);
        Rotas rotas = newRoute(cliente, routeDay, isRoute, codRecFunc, codUnidNeg);

        if(!routeDay.after(FormatUtils.toDateTimeFixed())){
            insertRoute(rotas);
        }

        return rotas;
    }

    public Rotas newRoute(Cliente cliente, Date routeDay, boolean isRoute,  String codRecFunc, String codUnidNeg){
        Rotas rotas = createObject(cliente, routeDay, isRoute, codRecFunc, codUnidNeg);

        if(!routeDay.after(FormatUtils.toDateTimeFixed())){
            insertRoute(rotas);
        }

        return rotas;
    }

    private Rotas createObject(Cliente cliente, Date routeDay, boolean isRoute, String codRecFunc, String codUnidNeg){
        Rotas rotas = new Rotas();
        rotas.setCodCliente(cliente.getCodigo());
        rotas.setCliente(cliente);
        rotas.setStatus(RotaGuiadaUtils.STATUS_RG_NAO_INICIADO);
        rotas.setRota(isRoute ? "1":"0");
        rotas.setCodRegFunc(codRecFunc);
        rotas.setCodUnidNeg(codUnidNeg);

        try {
            rotas.setDate(FormatUtils.toTextToCompareDateInSQlite(routeDay));
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        return rotas;
    }

    /**
     * Insere rota no banco de dados
     * @param rota
     */
    public void insertRoute(Rotas rota){
        ContentValues contentValues = getContentValues(rota);
        SQLiteDatabase database = getDb();
        database.insertWithOnConflict("TB_ROTAGUIADA", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Retorna todas as rotas que precisam ser sincronizadas com servidor
     * @param codUnNegocio
     * @return
     */
    public ArrayList<Rotas> getRouteSync(String codUnNegocio) {
        StringBuilder whereClause = new StringBuilder();

        whereClause.append("WHERE COD_UNID_NEGOC = ? AND ");
        whereClause.append(" DT_ULT_ALT IS NULL ");

        String[] args = new String[]{codUnNegocio};

        return query(false, false, whereClause.toString(), args, null, null);
    }

    public ArrayList<Rotas> getListRouteCreated(boolean isAgregatedCliente, String codigoUsuario, String codUnNegocio, Date routeDay, String nome, RotasFilter rotaGuiadaFilter) {
        StringBuilder whereClause = new StringBuilder();
        List<String> whereArgs = new ArrayList<>();

        String orderBy = " order by RG_INT_STATUS ";

        whereClause.append("WHERE RG_DAT_DIAPLANO = ? AND COD_UNID_NEGOC = ? AND (DEL_FLAG == '0' OR DEL_FLAG ISNULL) ");

        String dateNow = "";

        try{
            dateNow = FormatUtils.toTextToCompareDateInSQlite(routeDay);
            whereArgs.add(dateNow);
        }catch (Exception ex){
            whereArgs.add("");
            LogUser.log(Config.TAG, ex.toString());
        }

        whereArgs.add(codUnNegocio);

        if(rotaGuiadaFilter != null){

            if(rotaGuiadaFilter != null && rotaGuiadaFilter.getStatus() != null){
                whereClause.append(" AND RG_INT_STATUS = ? ");
                whereArgs.add(rotaGuiadaFilter.getStatus() + "");
            }

            if (rotaGuiadaFilter.getHierarquiaComercial() != null &&
                    rotaGuiadaFilter.getHierarquiaComercial().size() > 0) {
                 whereClause.append(" AND COD_REG_FUNC in (");
                for (int i = 0; i < rotaGuiadaFilter.getHierarquiaComercial().size(); i++) {
                    if(i == 0){
                        whereClause.append("(?)");
                    } else {
                        whereClause.append(",(?)");
                    }

                    whereArgs.add(rotaGuiadaFilter.getHierarquiaComercial().get(i).getCodigo());
                }

                whereClause.append(" )");

                orderBy = " order by COD_REG_FUNC ";

            } else {
                whereClause.append(" AND COD_REG_FUNC = ? ");
                whereArgs.add(codigoUsuario);
            }
        }

        return query(isAgregatedCliente, false, whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, nome);
    }


    public ArrayList<Rotas> getUnrealizedRoute(String codigoUsuario, String codUnidNeg, Date date) {
        ArrayList<Rotas> listRotas = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" RG_TXT_CODCLI,");
        query.append(" RG_INT_STATUS,");
        query.append(" RG_INT_ISROTA,");
        query.append(" COD_UNID_NEGOC,");
        query.append(" RG_DAT_CHECKIN,");
        query.append(" RG_DAT_CHECKOUT,");
        query.append(" RG_FLO_DIFF_CHECKIN_OUT,");
        query.append(" RG_DAT_DIAPLANO,");
        query.append(" RG_TXT_CHECKIN_DENTRO,");
        query.append(" RG_INT_JUSTIF_VISITA,");
        query.append(" RG_INT_JUSTIF_PEDIDO,");
        query.append(" RG_TXT_JUSTIF_ATIV_OBRIG,");
        query.append(" RG_FLO_DIFF_PAUSE, ");
        query.append(" DT_ULT_ALT,");
        query.append(" COD_REG_FUNC ");
        query.append("FROM TB_ROTAGUIADA ");
        query.append("WHERE COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? AND ");
        query.append("  RG_DAT_DIAPLANO = (SELECT MAX(RG_DAT_DIAPLANO) FROM TB_ROTAGUIADA ");
        query.append("WHERE RG_DAT_DIAPLANO < datetime(?) AND COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? ) AND");
        query.append(" (RG_INT_JUSTIF_VISITA IS NULL OR RG_INT_JUSTIF_VISITA = '0') AND ");
        query.append(" RG_INT_STATUS != ? AND ");
        query.append(" RG_INT_STATUS != ? AND ");
        query.append(" RG_INT_ISROTA = ?  ORDER BY RG_INT_STATUS DESC");

        String dateNow = "";

        try{
            dateNow = FormatUtils.toTextToCompareDateInSQlite(date);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        String[] args = new String[]{codigoUsuario, codUnidNeg, dateNow, codigoUsuario, codUnidNeg, String.valueOf(RotaGuiadaUtils.STATUS_RG_FORA_ROTA),
                String.valueOf(RotaGuiadaUtils.STATUS_RG_FINALIZADO), "1"};

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Rotas rotas = new RotasCursorWrapper(cursor).getRotas(true);

                if(rotas.getCliente() == null ||
                        (rotas.isRota() && rotas.getCliente().getPlanoCampo() == null)){
                    cursor.moveToNext();
                    continue;
                } else {
                    listRotas.add(rotas);
                }

                cursor.moveToNext();

            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listRotas;
    }


    /**
     * Verifica se existe rota com determinado status
     * @param rota
     * @param status
     * @return
     */
    public boolean hasStatusInRoute(Rotas rota, int status){
        boolean hasRoute = false;

        StringBuilder query = new StringBuilder();
        query.append("SELECT RG_DAT_DIAPLANO ");
        query.append("FROM TB_ROTAGUIADA ");
        query.append("WHERE COD_REG_FUNC  = ? AND COD_UNID_NEGOC = ? AND RG_TXT_CODCLI <> ? AND ");
        query.append(" RG_DAT_DIAPLANO = ? ");
        query.append(" AND RG_INT_STATUS = ? ");

        String[] args = {rota.getCodRegFunc(), rota.getCodUnidNeg(),
                rota.getCliente().getCodigo(), rota.getDate(),
                status + ""};

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                hasRoute = true;
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return hasRoute;
    }


    /**
     * Verifica se usuário possiu rota para o cliente em determinado dia
     * @param codigoUsuario
     * @param codUndNeg
     * @param codClie
     * @param date
     * @return
     */
    public boolean hasClienteInRoute(String codigoUsuario, String codUndNeg, String codClie, Date date){
        boolean hasCliente = false;

        StringBuilder query = new StringBuilder();
        query.append("SELECT RG_DAT_DIAPLANO ");
        query.append("FROM TB_ROTAGUIADA ");
        query.append("WHERE COD_REG_FUNC = ? AND COD_UNID_NEGOC = ? AND RG_TXT_CODCLI = ? AND ");
        query.append(" RG_DAT_DIAPLANO = ? ");

        String dateNow = "";

        try{
            dateNow = FormatUtils.toTextToCompareDateInSQlite(date);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        String[] args = {codigoUsuario, codUndNeg, codClie, dateNow};
        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                hasCliente = true;
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return hasCliente;
    }


    public Cliente setAggregatedDataCliente(String codCliente) {
        String codUnNeg = Current.getInstance(getContext()).getUnidadeNegocio().getCodigo();
        Cliente cliente =  mClienteDao.get(codUnNeg, codCliente);
        return cliente;
    }

    public ArrayList<Rotas> query(boolean aggregatedCliente, boolean isIgonrePlanoCampo, String whereClause, String[] args, String orderBy, String nome) {
        ArrayList<Rotas> listRotas = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" RG_TXT_CODCLI,");
        query.append(" RG_INT_STATUS,");
        query.append(" COD_UNID_NEGOC,");
        query.append(" RG_INT_ISROTA,");
        query.append(" RG_DAT_CHECKIN,");
        query.append(" RG_DAT_CHECKOUT,");
        query.append(" RG_FLO_DIFF_CHECKIN_OUT,");
        query.append(" RG_DAT_DIAPLANO,");
        query.append(" RG_NUM_LAT_CHECKIN,");
        query.append(" RG_NUM_LON_CHECKIN,");
        query.append(" RG_TXT_CHECKIN_DENTRO,");
        query.append(" RG_INT_JUSTIF_VISITA,");
        query.append(" RG_INT_JUSTIF_PEDIDO,");
        query.append(" RG_TXT_JUSTIF_ATIV_OBRIG,");
        query.append(" DT_ULT_ALT,");
        query.append(" COD_REG_FUNC, ");
        query.append(" DEL_FLAG, ");
        query.append(" RG_DAT_PAUSE, ");
        query.append(" RG_FLO_DIFF_PAUSE ");
        query.append("FROM TB_ROTAGUIADA ");

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
                Rotas rotas = new RotasCursorWrapper(cursor).getRotas(aggregatedCliente);

                //Caso aggregatedCliente = true e não encontrar cliente ou não tiver plano de comapo e for rota
                //não adiciona na lista
                if(aggregatedCliente) {
                    if (rotas.getCliente() == null ||
                            (!isIgonrePlanoCampo && rotas.isRota() && rotas.getCliente().getPlanoCampo() == null)) {
                        cursor.moveToNext();
                        continue;
                    }
                }

                if (nome != null) {
                    if (rotas.getCliente().getNome().toLowerCase().contains(nome.toLowerCase())) {
                        listRotas.add(rotas);
                    }
                } else {
                    listRotas.add(rotas);
                }
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listRotas;
    }

    public class RotasCursorWrapper extends CursorWrapper {

        public RotasCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Rotas getRotas(boolean aggregatedCliente) {
            Rotas rotas = new Rotas();
            rotas.setStatus(getInt(getColumnIndex("RG_INT_STATUS")));
            rotas.setRota(getString(getColumnIndex("RG_INT_ISROTA")));
            rotas.setCheckin(getString(getColumnIndex("RG_DAT_CHECKIN")));
            rotas.setCheckout(getString(getColumnIndex("RG_DAT_CHECKOUT")));
            rotas.setDiffCheck(getFloat(getColumnIndex("RG_FLO_DIFF_CHECKIN_OUT")));
            rotas.setDate(getString(getColumnIndex("RG_DAT_DIAPLANO")));
            rotas.setChekinDentro(getString(getColumnIndex("RG_TXT_CHECKIN_DENTRO")));
            rotas.setCodRegFunc(getString(getColumnIndex("COD_REG_FUNC")));
            rotas.setJustifVisita(getInt(getColumnIndex("RG_INT_JUSTIF_VISITA")));
            rotas.setJustifPedido(getInt(getColumnIndex("RG_INT_JUSTIF_PEDIDO")));
            rotas.setJustifAtivObrig(getString(getColumnIndex("RG_TXT_JUSTIF_ATIV_OBRIG")));
            rotas.setDtUltAlt(getString(getColumnIndex("DT_ULT_ALT")));;
            rotas.setCodUnidNeg(getString(getColumnIndex("COD_UNID_NEGOC")));;
            rotas.setCodCliente(getString(getColumnIndex("RG_TXT_CODCLI")));

            if(rotas.getCodCliente() == null){
                LogUser.log("Rota cliente null " + rotas.getCodRegFunc());
            }

            rotas.setDiffPause(getFloat(getColumnIndex("RG_FLO_DIFF_PAUSE")));

            if(aggregatedCliente){
                rotas.setCliente(setAggregatedDataCliente(getString(getColumnIndex("RG_TXT_CODCLI"))));
            }

            return rotas;
        }
    }

    private ContentValues getContentValues(Rotas rota) {
        Current current = Current.getInstance(getContext());
        ContentValues values = new ContentValues();
        values.put("RG_INT_STATUS", rota.getStatus());
        values.put("RG_INT_ISROTA", rota.isRota() ? "1":"0");
        values.put("RG_DAT_DIAPLANO", rota.getDate());
        values.put("RG_DAT_DIAPLANO", rota.getDate());
        values.put("RG_FLO_DIFF_CHECKIN_OUT", "0");

        if(rota.getCliente() != null){
            values.put("RG_TXT_CODCLI", rota.getCliente().getCodigo());
        } else {
            values.put("RG_TXT_CODCLI", rota.getCodCliente());
        }
        values.put("COD_REG_FUNC", current.getUsuario().getCodigo());
        values.put("COD_UNID_NEGOC", current.getUnidadeNegocio().getCodigo());
        values.put("DEL_FLAG", "0");

        return values;
    }

}


