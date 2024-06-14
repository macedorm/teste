    package br.com.jjconsulting.mobile.dansales.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.RotasFilter;
import br.com.jjconsulting.mobile.dansales.model.Atividade;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.PlanejamentoRotaAtividadeType;
import br.com.jjconsulting.mobile.dansales.model.RotaAcao;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.JJSyncRotaGuiada;
import br.com.jjconsulting.mobile.dansales.util.PesquisaUtils;
import br.com.jjconsulting.mobile.dansales.util.PlanejamentoRotaUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEvent;

public class PlanejamentoRotaGuiadaDao extends BaseDansalesDao {

    private final String EVENT_CODIGO_IDENTITY_LETTER = "E";

    private PesquisaDao  pesquisaDao;
    private RotaGuiadaDao rotaGuiadaDao;
    private ClienteDao clienteDao;



    public PlanejamentoRotaGuiadaDao(Context context) {
        super(context);
        rotaGuiadaDao = new RotaGuiadaDao(getContext());
        clienteDao = new ClienteDao(getContext());
        pesquisaDao = new PesquisaDao(getContext());
    }

    private String createCodigoTemp(String usuario, int task) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        final String now = simpleDateFormat.format(new Date());
        final String userId = usuario;
        return String.format("%s%s%s%s", EVENT_CODIGO_IDENTITY_LETTER, now, userId, task);
    }

    public void insertPlanejamentoRota(JJCalendarEvent calendarEvent){
        JJCalendarEvent event = JJCalendarEvent.copy(calendarEvent);

        ContentValues contentValues = new ContentValues();
        contentValues.put("PLR_ID", createCodigoTemp(event.getUserID(), event.getType()));

        contentValues.put("PLR_STATUS", RotaGuiadaUtils.STATUS_RG_NAO_INICIADO);
        contentValues.put("PLR_DATA", event.getDate());
        contentValues.put("PLR_ATV_ID", event.getType());
        contentValues.put("PLR_USERID", event.getUserID());
        contentValues.put("PLR_UNID_NEGOC", event.getUnNeg());

        if(!TextUtils.isNullOrEmpty(event.getHoursStart())){
            contentValues.put("PLR_HR_INICIO", event.getHoursStart());
        }

        if(!TextUtils.isNullOrEmpty(event.getHoursEnd())){
            contentValues.put("PLR_HR_FIM", event.getHoursEnd());
        }

        if(!TextUtils.isNullOrEmpty(event.getPromotor())){
            contentValues.put("PLR_PROMOTOR", event.getPromotor());
        }

        if(!TextUtils.isNullOrEmpty(event.getPromotorName())){
            contentValues.put("PLR_PROMOTOR_GRID", event.getPromotorName());
        }

        if(!TextUtils.isNullOrEmpty(event.getCodCliente())){
            contentValues.put("PLR_CLIENTE", event.getCodCliente());
        }

        if(!TextUtils.isNullOrEmpty(event.getNote())){
            contentValues.put("PLR_OBS", event.getNote());
        }

        contentValues.put("DEL_FLAG", "0");

        SQLiteDatabase database = getDb();
        database.insert("TB_ROTAGUIADA_PLANEJAMENTO_ROTA", null,contentValues);
        database.close();


        //Caso for uma visita com promotor adicionar automaticamente um evento de coaching promotor
        if(event.getType() == PlanejamentoRotaUtils.VISITAPROMOTOR.getValue()){
            insertCounchingPromotor(event);
        }
    }

    public void insertCounchingPromotor(JJCalendarEvent calendarEvent){
        JJCalendarEvent event = JJCalendarEvent.copy(calendarEvent);
        event.setType(PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue());
        event.setHoursStart(null);
        event.setHoursEnd(null);
        event.setCodCliente(null);

        if(!hasEvent(event)){
            insertPlanejamentoRota(event);
        }
    }

    public void updateSync(String id, Date date) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(date));

            SQLiteDatabase database = getDb();

            String[] args = new String[]{id};

            database.update("TB_ROTAGUIADA_PLANEJAMENTO_ROTA", contentValues,
                    "PLR_ID = ? ", args);
            database.close();
    }

    public void updateCoaching(String user, String unNeg, String date, String promotor, int status){
        ContentValues contentValues = new ContentValues();
        contentValues.put("PLR_STATUS", status);
        contentValues.putNull("DT_ULT_ALT");

        String whereArgs = " (DEL_FLAG ISNULL OR DEL_FLAG = ? ) ";
        whereArgs += " AND PLR_USERID == ? AND PLR_UNID_NEGOC == ? ";
        whereArgs += " AND PLR_DATA = ? AND PLR_PROMOTOR == ? AND PLR_ATV_ID = ? ";


        SQLiteDatabase database = getDb();
       database.update("TB_ROTAGUIADA_PLANEJAMENTO_ROTA", contentValues,
                whereArgs,
                new String[]{"0", user, unNeg, date, promotor, String.valueOf(PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue())}
        );
        database.close();
    }

    public void updatePlanejamentoRota(String obs, int status, String id){
        ContentValues contentValues = new ContentValues();
        contentValues.put("PLR_STATUS", status);
        contentValues.putNull("DT_ULT_ALT");

        if(!TextUtils.isNullOrEmpty(obs)){
            contentValues.put("PLR_OBS", obs);
        }

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA_PLANEJAMENTO_ROTA", contentValues,
                "PLR_ID = ?",
                new String[]{id});
        database.close();
    }

    public void managerDeletePlanejamentoRota(JJCalendarEvent calendarEvent){
        switch (calendarEvent.getType()){
            case 1:
            case 2:
                deleteVisitaPlanejamentoRota(calendarEvent);
                deleteRota(calendarEvent);

                if(calendarEvent.getType() == 2){
                    if(countVisitaPromotor(calendarEvent) == 0){
                        calendarEvent.setType(PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue());
                        calendarEvent.setCodCliente(null);
                        deleteVisitaPlanejamentoRota(calendarEvent);
                    }
                }
                break;
            case 3:

                break;
            default:
                deleteAdminPlanejamentoRota(calendarEvent.getId());
                break;
        }
    }

    private void deleteRota(JJCalendarEvent calendarEvent){
        RotaGuiadaDao rotaGuiadaDao = new RotaGuiadaDao(getContext());
        rotaGuiadaDao.deleteRota(calendarEvent.getCodCliente(), calendarEvent.getDate(), calendarEvent.getUserID(), calendarEvent.getUnNeg());
        //rotaGuiadaDao.deleteRotaTarefa(calendarEvent.getCodCliente(), calendarEvent.getDate(), calendarEvent.getUserID(), calendarEvent.getUnNeg());
    }

    private void deleteAdminPlanejamentoRota(String id){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DEL_FLAG", "1");
        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();
        database.update("TB_ROTAGUIADA_PLANEJAMENTO_ROTA", contentValues,
                "PLR_ID = ? ",
                new String[]{id});
        database.close();
    }

    private void deleteVisitaPlanejamentoRota(JJCalendarEvent calendarEvent){

        ContentValues contentValues = new ContentValues();
        contentValues.put("DEL_FLAG", "1");
        contentValues.putNull("DT_ULT_ALT");

        SQLiteDatabase database = getDb();

        String query = " PLR_DATA = ?  AND PLR_ATV_ID = ? ";

        List<String> whereArgs = new ArrayList<>();

        whereArgs.add(calendarEvent.getDate());
        whereArgs.add(String.valueOf(calendarEvent.getType()));

        if(!TextUtils.isNullOrEmpty(calendarEvent.getCodCliente())){
            query += " AND PLR_CLIENTE = ? ";
            whereArgs.add(calendarEvent.getCodCliente());
        }


        if(!TextUtils.isNullOrEmpty(calendarEvent.getPromotor())){
            query += " AND PLR_PROMOTOR = ? ";
            whereArgs.add(calendarEvent.getPromotor());
        }

        database.update("TB_ROTAGUIADA_PLANEJAMENTO_ROTA", contentValues, query, whereArgs.toArray(new String[0]));
        database.close();
    }


    public int countVisitaPromotor(JJCalendarEvent calendarEvent){
        int count = 0;

        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TB_ROTAGUIADA_PLANEJAMENTO_ROTA ");
        query.append("WHERE PLR_USERID == ? AND PLR_UNID_NEGOC == ? ");
        query.append(" AND PLR_DATA = ? AND PLR_PROMOTOR == ? ");
        query.append(" AND PLR_ATV_ID == ? ");
        query.append(" AND (DEL_FLAG ISNULL OR DEL_FLAG = '0' )  ");

        String[] args = new String[]{calendarEvent.getUserID(), calendarEvent.getUnNeg(),
                calendarEvent.getDate(), calendarEvent.getPromotor(), String.valueOf(calendarEvent.getType())};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                count++;
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return count;

    }


    public boolean hasEvent(JJCalendarEvent calendarEvent){
        boolean isExists = false;

        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TB_ROTAGUIADA_PLANEJAMENTO_ROTA ");
        query.append("WHERE PLR_USERID == ? AND PLR_UNID_NEGOC == ? ");
        query.append(" AND PLR_DATA = ? ");
        query.append(" AND (DEL_FLAG ISNULL OR DEL_FLAG = '0' )  ");

        List<String> whereArgs = new ArrayList<>();

        whereArgs.add(calendarEvent.getUserID());
        whereArgs.add(calendarEvent.getUnNeg());
        whereArgs.add(calendarEvent.getDate());

        if(calendarEvent.getType() == PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue()){
            query.append(" AND PLR_ATV_ID == ? ");
            whereArgs.add(String.valueOf(calendarEvent.getType()));

            if(!TextUtils.isNullOrEmpty(calendarEvent.getPromotor())){
                query.append(" AND PLR_PROMOTOR == ? ");
                whereArgs.add(calendarEvent.getPromotor());
            }
        }

        if(!TextUtils.isNullOrEmpty(calendarEvent.getCodCliente())){
            query.append(" AND PLR_CLIENTE == ? ");
            whereArgs.add(calendarEvent.getCodCliente());
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                isExists = true;
                break;
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return isExists;

    }

    public ArrayList<HashMap<String, Object>> getSyncPlanejamentoRota(){

        ArrayList<HashMap<String, Object>> planejamentoSync = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TB_ROTAGUIADA_PLANEJAMENTO_ROTA ");
        query.append("WHERE DT_ULT_ALT IS NULL ");

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                planejamentoSync.add(new PlanejamentoRotaGuiadaDao.EventsCursorWrapper(cursor).getEventSync());
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return planejamentoSync;
    }

    private  List<JJCalendarEvent> queryEvents(StringBuilder where, String[] args, String unidadeNeg){
        List<JJCalendarEvent> listCalendarEvent = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(" pl.*, at.ATV_NOME ");
        query.append(" FROM TB_ROTAGUIADA_PLANEJAMENTO_ROTA AS pl");
        query.append(" INNER JOIN TB_ROTAGUIADA_ATIVIDADE AS at ");
        query.append(" ON at.ATV_ID == PLR_ATV_ID  ");
        query.append(where.toString());
        query.append(" ORDER BY pl.PLR_ATV_ID ");

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                JJCalendarEvent calendarEvent = new PlanejamentoRotaGuiadaDao.EventsCursorWrapper(cursor).getEvent(true, unidadeNeg);
                listCalendarEvent.add(calendarEvent);
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listCalendarEvent;
    }

    public  List<JJCalendarEvent> getGAEvents(String usuario, String unidadeNeg, Calendar calendar) {
        StringBuilder where = new StringBuilder();
        where.append("WHERE PLR_USERID = ? AND PLR_UNID_NEGOC = ? AND ");
        where.append("  (pl.DEL_FLAG ISNULL OR pl.DEL_FLAG = '0' ) AND ");
        where.append("  (at.DEL_FLAG ISNULL OR at.DEL_FLAG = '0' ) AND ");
        where.append("  PLR_DATA = ? ");

        String[] args = new String[]{usuario, unidadeNeg, FormatUtils.toTextToCompareDateInSQlite(calendar.getTime())};

        return queryEvents(where, args, unidadeNeg);
    }

    public  List<JJCalendarEvent> getGAAllEvents(String usuario, String unidadeNeg, Calendar calendar) {
        StringBuilder where = new StringBuilder();
        where.append("WHERE PLR_USERID = ? AND PLR_UNID_NEGOC = ? AND ");
        where.append("  (pl.DEL_FLAG ISNULL OR pl.DEL_FLAG = '0' ) AND ");
        where.append("  (at.DEL_FLAG ISNULL OR at.DEL_FLAG = '0' ) AND ");
        where.append("  strftime('%Y-%m',  PLR_DATA) ");
        where.append("  = strftime('%Y-%m',  ? ) ");

        String[] args = new String[]{usuario,  unidadeNeg,  FormatUtils.toTextToCompareDateInSQlite(calendar.getTime())};

        return queryEvents(where, args, unidadeNeg);
    }

    public boolean hasVisitaPromotor(JJCalendarEvent event, String userID, String unNegoc){
        StringBuilder query = new StringBuilder();

        query.append(" SELECT * FROM TB_ROTAGUIADA_PLANEJAMENTO_ROTA WHERE ");
        query.append("  (DEL_FLAG ISNULL OR DEL_FLAG = '0' )  ");
        query.append(" AND PLR_CLIENTE == ? ");
        query.append(" AND PLR_USERID == ? AND PLR_UNID_NEGOC == ? ");
        query.append(" AND PLR_DATA == ? ");

        String[] args = new String[]{event.getCodCliente(), userID, unNegoc, event.getDate()};

        try (Cursor cursor = getDb().rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                return true;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }


        return false;
    }

    public List<Atividade> getTask(){
        List<Atividade> tasks = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append(" SELECT ATV_NOME, ATV_ID FROM TB_ROTAGUIADA_ATIVIDADE WHERE ");
        query.append("  ATV_ATIVA == '1' AND DEL_FLAG <> '1' ");
        query.append("  AND ATV_ID != '3' ");

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Atividade task = new PlanejamentoRotaGuiadaDao.EventsCursorWrapper(cursor).getTask();
                tasks.add(task);
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return tasks;
    }


    public  List<JJCalendarEvent> getPromotorEvents(Usuario usuario, String unidadeNeg, Calendar calendar) {
        List<JJCalendarEvent> listCalendarEvent = createEvents(getRoutePromotor(true, usuario, unidadeNeg, calendar ));
        return listCalendarEvent;
    }

    public  List<JJCalendarEvent> getPromotorAllEvents(Usuario usuario, String unidadeNeg, Calendar calendar) {
        List<JJCalendarEvent> jjCalendarEvents = new ArrayList<>();

        Calendar calendarCurrent = (Calendar) calendar.clone();
        calendarCurrent = FormatUtils.removeTimeCalendar(calendarCurrent);

        List<Usuario> hierarquiaUsuarios = new ArrayList<>();
        hierarquiaUsuarios.add(usuario);

        int maxDay = calendarCurrent.getActualMaximum(Calendar.DAY_OF_MONTH);

         ArrayList<Cliente> clientes = rotaGuiadaDao.loadingCliente(usuario.getCodigo(),unidadeNeg);

         for (int co = 0; co < maxDay; co++) {
            calendarCurrent.set(Calendar.DAY_OF_MONTH, co +  1);
            rotaGuiadaDao.setCliente(clientes);
            jjCalendarEvents.addAll(createEvents(getRoutePromotor(false, usuario, unidadeNeg, calendarCurrent)));
         }

        return jjCalendarEvents;
    }

     private List<Rotas> getRoutePromotor(boolean isAgregatedCliente, Usuario usuario, String unidadeNeg, Calendar calendar){
         RotasFilter rotasFilter = new RotasFilter();
         List<Usuario> hierarquiaUsuarios = new ArrayList<>();
         hierarquiaUsuarios.add(usuario);
         rotasFilter.setHierarquiaComercial(hierarquiaUsuarios);
         rotasFilter.setStatus(null);
         rotasFilter.setDate(null);

         ArrayList<Rotas> list = new ArrayList<>();

         Date dateRoute = FormatUtils.resetTimeToMidnight(calendar.getTime());
         Date dateNow = FormatUtils.resetTimeToMidnight(new Date());

         ArrayList<Rotas> listTemp = new ArrayList<>();

         if (!dateRoute.after(dateNow)) {
             //Retorna todas as rotas criadas no dispositivo do promotor desde que não sejam um dia futuro
             listTemp = rotaGuiadaDao.getListRouteCreated(isAgregatedCliente, usuario.getCodigo(), unidadeNeg, calendar.getTime(), null, rotasFilter);
         } else {
             //Caso no dia não retornar nenhuma rota, será feito a simulação
             if(listTemp.size() == 0){
                 List<Usuario> usuarios = new ArrayList<>();
                 usuarios.add(usuario);
                 listTemp = (ArrayList<Rotas>) rotaGuiadaDao.simulateRoute(calendar.getTime(), usuario);
             }
         }

         list.addAll(listTemp);

         return routeUnrealizad(list);

     }

     private List<Rotas> routeUnrealizad(List<Rotas> route){
         for(int ind = 0; ind < route.size(); ind++){
             route.get(ind).setStatus(getStatus(route.get(ind).getStatus(), route.get(ind).getDate()));
         }
         return route;
     }


    public void insertRoute(JJCalendarEvent calendarEvent){
        JJCalendarEvent event = JJCalendarEvent.copy(calendarEvent);
        Rotas rota = convertJJCalendarInRoute(event);
        rotaGuiadaDao.insertRoute(rota);
    }

    private Rotas convertJJCalendarInRoute(JJCalendarEvent calendarEvent){
        Rotas rota = new Rotas();
        rota.setCodCliente(calendarEvent.getCodCliente());
        rota.setDate(calendarEvent.getDate());
        rota.setStatus(calendarEvent.getStatus());
        rota.setIsRota("1");
        rota.setCodRegFunc(calendarEvent.getUserID());
        rota.setCodUnidNeg(calendarEvent.getUnNeg());
        rota.setCliente(null);

        return rota;
    }


    private List<JJCalendarEvent> createEvents (List<Rotas> route){
        List<JJCalendarEvent> jjCalendarEvents = new ArrayList<>();

        //Verifica se rota foi realizada (apenas para datas anteriores ao dia atual)
        for(int ind = 0; ind < route.size(); ind++){
            Rotas rotas = route.get(ind);

            JJCalendarEvent event = new JJCalendarEvent("Visita Cliente " + rotas.getCodCliente(), rotas.getDate(), rotas.getStatus());
            event.setCodCliente(rotas.getCodCliente());
            event.setType(1);
            event.setUserID(route.get(ind).getCodRegFunc());

            if(rotas.getCliente() != null) {
                event.setAdresss(rotas.getCliente().getEndereco());
                event.setCity(rotas.getCliente().getMunicipio());
                event.setUf(rotas.getCliente().getUf());
                event.setName(rotas.getCliente().getNome());
            }

            event.setRoute(rotas.isRota());

            jjCalendarEvents.add(event);
        }

        return jjCalendarEvents;
    }

    private int getStatus(int status, String date){
        try {
            Date dateRoute = FormatUtils.resetTimeToMidnight(FormatUtils.toDateShort(date));
            Date dateNow = FormatUtils.resetTimeToMidnight(new Date());

            if (dateRoute.before(dateNow) && !dateNow.equals(dateRoute)) {

                if(status != RotaGuiadaUtils.STATUS_RG_FINALIZADO
                    && status != RotaGuiadaUtils.STATUS_RG_FORA_ROTA) {

                    if (status == RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO) {
                        return RotaGuiadaUtils.STATUS_RG_INCOMPLETO;
                    } else {
                        return RotaGuiadaUtils.STATUS_RG_NAO_REALIZADO;
                    }
                } else{
                    return status;
                }
            } else {
                return status;
            }
        }catch (Exception ex){
            LogUser.log(ex.getMessage());
            return status;
        }
    }

    public void setStatusCoaching(Activity activity, String codUser, String unNeg, String promotor, Date date){
        boolean isFinished = true;
        
        Usuario user = new Usuario();
        user.setCodigo(codUser);

        List<Pesquisa> listPesquisa = pesquisaDao.getAll(codUser, user, promotor, null, date, PesquisaDao.TTypePesquisa.COACHING);
        
        for(Pesquisa pesquisa: listPesquisa){
           if(pesquisa.getStatusResposta() != Pesquisa.OBRIGATORIAS_RESPONDIDAS){
               isFinished = false;
           }
        }

        if(isFinished){
            updateCoaching(codUser, unNeg,
                    FormatUtils.toTextToCompareDateInSQlite(
                            FormatUtils.resetTimeToMidnight(date)),
                    promotor, RotaGuiadaUtils.STATUS_RG_FINALIZADO);

            JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
            jjSyncRotaGuiada.syncRotaGuiada(activity, null);
        }
    }

    public boolean isEventEquals(String date){
        boolean isDateDifferent = false;

        try{
            Date currentDate = FormatUtils.resetTimeToMidnight(new Date());
            Date dateEvent = FormatUtils.toDate(date);

            isDateDifferent = dateEvent.equals(currentDate);

        }catch (Exception ex){
            LogUser.log(ex.toString());
        }

        return isDateDifferent;
    }


    public boolean isEventDateBefore(String date){
        boolean isDateBefore = false;

        try{
            Date currentDate = FormatUtils.resetTimeToMidnight(new Date());
            Date dateEvent = FormatUtils.toDate(date);

            if(dateEvent.before(currentDate)){
                isDateBefore = true;
            }

        }catch (Exception ex){
            LogUser.log(ex.toString());
        }

        return isDateBefore;
    }

    public class EventsCursorWrapper extends CursorWrapper {

        public EventsCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Atividade getTask(){
            Atividade atividade = new Atividade();
            atividade.setCod(getString(getColumnIndex("ATV_ID")));
            atividade.setNome(getString(getColumnIndex("ATV_NOME")));

            return atividade;
        }

        public JJCalendarEvent getEvent(boolean isAgregatedClient, String unidadeNegocio) {
            JJCalendarEvent event = new JJCalendarEvent();

            event.setId(getString(getColumnIndex("PLR_ID")));
            event.setStatus(getInt(getColumnIndex("PLR_STATUS")));
            event.setDate(getString(getColumnIndex("PLR_DATA")));
            event.setType(getInt(getColumnIndex("PLR_ATV_ID")));
            event.setHoursStart(getString(getColumnIndex("PLR_HR_INICIO")));
            event.setHoursEnd(getString(getColumnIndex("PLR_HR_FIM")));
            event.setNote(getString(getColumnIndex("PLR_OBS")));
            event.setUserID(getString(getColumnIndex("PLR_USERID")));
            event.setPromotorName(getString(getColumnIndex("PLR_PROMOTOR_GRID")));
            event.setPromotor(getString(getColumnIndex("PLR_PROMOTOR")));
            event.setUnNeg(getString(getColumnIndex("PLR_UNID_NEGOC")));


            String cliente = getString(getColumnIndex("PLR_CLIENTE"));

            if(event.getType() > 2){
                event.setEvent(getString(getColumnIndex("ATV_NOME")));
            } else {
                event.setEvent(PlanejamentoRotaAtividadeType.getLabelEnum(getInt(getColumnIndex("PLR_ATV_ID")), cliente, false));
            }


            if(isAgregatedClient && !TextUtils.isNullOrEmpty(cliente)){
                 Cliente client = clienteDao.getNoAgregated(unidadeNegocio, cliente);
                 if(client != null){
                     event.setCodCliente(cliente);
                     event.setName(client.getNome());
                     event.setUf(client.getUf());
                     event.setCity(client.getMunicipio());
                     event.setAdresss(client.getEndereco());
                 }
            }

            event.setRoute(true);

            event.setStatus(getStatus(event.getStatus(), getString(getColumnIndex("PLR_DATA"))));


            /*if(event.getType() == PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue()){
                event.setStatus(aggregateStatusPesquisa(event));
            } else {
            }*/

            return event;
        }

        public HashMap<String,Object> getEventSync() {
            HashMap<String,Object> event = new HashMap<>();
            event.put("PLR_ID", getString(getColumnIndex("PLR_ID")));
            event.put("PLR_STATUS", getInt(getColumnIndex("PLR_STATUS")));
            event.put("PLR_DATA", getString(getColumnIndex("PLR_DATA")));
            event.put("PLR_ATV_ID", getInt(getColumnIndex("PLR_ATV_ID")));
            event.put("PLR_HR_INICIO", getString(getColumnIndex("PLR_HR_INICIO")));
            event.put("PLR_HR_FIM", getString(getColumnIndex("PLR_HR_FIM")));
            event.put("PLR_OBS", getString(getColumnIndex("PLR_OBS")));
            event.put("PLR_USERID", getString(getColumnIndex("PLR_USERID")));
            event.put("PLR_PROMOTOR", getString(getColumnIndex("PLR_PROMOTOR")));
            event.put("PLR_PROMOTOR_GRID", getString(getColumnIndex("PLR_PROMOTOR_GRID")));
            event.put("PLR_CLIENTE", getString(getColumnIndex("PLR_CLIENTE")));
            event.put("DEL_FLAG", getString(getColumnIndex("DEL_FLAG")));
            event.put("PLR_UNID_NEGOC", getString(getColumnIndex("PLR_UNID_NEGOC")));

            return event;
        }

    }

}


