package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTaskType;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaGuiadaTarefaDao extends BaseDansalesDao {

    private static final String TASK_CODIGO_IDENTITY_LETTER = "RGTASK";

    private PedidoDao pedidoDao;
    private PesquisaDao pesquisaDao;

    public RotaGuiadaTarefaDao(Context context) {
        super(context);
        pesquisaDao = new PesquisaDao(context);
        pedidoDao = new PedidoDao(context);
    }

    public ArrayList<RotaTarefas> getTaskSync() {
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(" WHERE DT_ULT_ALT IS NULL");
        whereClause.append(" ORDER BY TSK_INT_TYPE");

        return query(null, null, whereClause.toString(), null, null, true);
    }

    public ArrayList<RotaTarefas> getAllTask(Rotas rota, String cliente) {

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("WHERE TSK_DAT_DIAPLANO = ? AND COD_UNID_NEGOC = ? ");
        whereClause.append("  AND COD_REG_FUNC = ? AND TSK_TXT_CODCLI = ? AND DEL_FLAG <> '1'  ");

        StringBuilder orderBy = new StringBuilder();
        orderBy.append("ORDER BY TSK_INT_TYPE");

        String[] args = new String[]{rota.getDate(), rota.getCodUnidNeg(), rota.getCodRegFunc(), rota.getCliente().getCodigo()};

        return query(rota, cliente, whereClause.toString(),args, orderBy.toString(), false);

    }

    private ArrayList<RotaTarefas> query(Rotas rota , String cliente, String whereClause, String[] args, String orderBy, boolean isSync) {
        ArrayList<RotaTarefas> listRotaTarefas = new ArrayList<>();

        Date date;

        try {
            if(rota == null){
                date = new Date();
            } else {
                date = FormatUtils.toDate(rota.getDate());
            }
        }catch (Exception ex){
            date = new Date();
        }


        StringBuilder query = new StringBuilder();
        query.append("SELECT TSK_TXT_ID,");
        query.append(" TSK_TXT_IDITEM,");
        query.append(" TSK_INT_TYPE,");
        query.append(" DT_ULT_ALT,");
        query.append(" TSK_TXT_CODCLI, ");
        query.append(" TSK_DAT_DIAPLANO, ");
        query.append(" COD_REG_FUNC, ");
        query.append(" COD_UNID_NEGOC, ");
        query.append(" TSK_INT_JUSTIF_ATIV ");
        query.append(" FROM TB_ROTAGUIADA_TAREFA");

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
                RotaTarefas rotaTarefas = new RotaGuiadaTaskWrapper(cursor).getTask(isSync);

                if(rotaTarefas.getStatus() == RotaGuiadaTaskType.PEDIDO){
                    rotaTarefas.setPedido(pedidoDao.get(rotaTarefas.getIdItem()));
                    if(rotaTarefas.getPedido() == null){
                        deleteTask(rotaTarefas);
                    } else {
                        listRotaTarefas.add(rotaTarefas);
                    }

                } else {
                    if(!TextUtils.isNullOrEmpty(cliente)){
                        String userId = Current.getInstance(getContext()).getUsuario().getCodigo();
                        rotaTarefas.setPesquisa(pesquisaDao.get(Integer.parseInt(rotaTarefas.getIdItem()), userId, cliente, date));
                    }

                    listRotaTarefas.add(rotaTarefas);
                }

                cursor.moveToNext();
            }

        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return listRotaTarefas;
    }

    public boolean hasTask(String id, Rotas rota){
        boolean hasTask = false;

        StringBuilder query = new StringBuilder();
        query.append("SELECT TSK_TXT_IDITEM ");
        query.append("FROM TB_ROTAGUIADA_TAREFA ");
        query.append("WHERE TSK_TXT_IDITEM = ? ");
        query.append("AND COD_REG_FUNC = ? ");
        query.append("AND TSK_DAT_DIAPLANO = ? ");
        query.append("AND COD_UNID_NEGOC = ? ");
        query.append("AND TSK_TXT_CODCLI = ? ");

        String[] args = {id, rota.getCodRegFunc(), rota.getDate(), rota.getCodUnidNeg(), rota.getCodCliente()};
        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                hasTask = true;
                break;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return hasTask;
    }

    public void insertTask(RotaTarefas item) {
        Gson gson = new Gson();
        String json =  gson.toJson(item);
        SQLiteDatabase database = getDb();

        try{
            ContentValues contentValues = getContentValuesTask(item);
            database.insertWithOnConflict("TB_ROTAGUIADA_TAREFA", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }catch (Exception ex){
            LogUser.log(ex.toString() + json);
        }finally {
            database.close();
        }
    }

    public void deleteTask(RotaTarefas item) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.putNull("DT_ULT_ALT");
            contentValues.put("DEL_FLAG", "1");

            database.update("TB_ROTAGUIADA_TAREFA", contentValues,
                    "TSK_TXT_IDITEM = ? AND COD_REG_FUNC = ? AND TSK_DAT_DIAPLANO = ? AND TSK_TXT_CODCLI = ? AND COD_UNID_NEGOC = ?",
                    new String[]{item.getIdItem(), item.getCodRegFunc(), item.getDataRota(), item.getCodClie(), item.getCodUnNeg()});
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }


    public void updateSync(RotaTarefas rotas, Date date) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(date));


            String[] args = new String[]{
                    rotas.getIdItem(),
                    rotas.getCodRegFunc(),
                    rotas.getDataRota(),
                    rotas.getCodUnNeg(),
                    rotas.getCodClie()};

            database.update("TB_ROTAGUIADA_TAREFA", contentValues,
                    "TSK_TXT_IDITEM = ? AND COD_REG_FUNC = ? AND TSK_DAT_DIAPLANO = ? " +
                            "AND COD_UNID_NEGOC = ? AND TSK_TXT_CODCLI = ?", args);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }

    public void updateCodigoPedidoTaks(String novoCodigoPedido, String codigoAntigoPedido) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("TSK_TXT_IDITEM", novoCodigoPedido);
            contentValues.putNull("DT_ULT_ALT");

            database.update("TB_ROTAGUIADA_TAREFA", contentValues,
                    "TSK_TXT_IDITEM = ?", new String[]{codigoAntigoPedido});
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }

    public void setJusticativaAtividade(int codigoPesquisa, int idJustifiativa, Rotas rotas) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("TSK_INT_JUSTIF_ATIV", idJustifiativa);
            contentValues.putNull("DT_ULT_ALT");

            String[] args = new String[]{codigoPesquisa + "",
                    rotas.getCodRegFunc(),
                    rotas.getDate(),
                    rotas.getCodUnidNeg(),
                    rotas.getCliente().getCodigo()};

            database.update("TB_ROTAGUIADA_TAREFA", contentValues,
                    "TSK_TXT_IDITEM = ? AND COD_REG_FUNC = ? AND TSK_DAT_DIAPLANO = ? " +
                            "AND COD_UNID_NEGOC = ? AND TSK_TXT_CODCLI = ?", args);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }

    public void removeJusticativaAtividade(Rotas rotas) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.putNull("TSK_INT_JUSTIF_ATIV");
            contentValues.putNull("DT_ULT_ALT");

            String[] args = new String[]{
                    rotas.getCodRegFunc(),
                    rotas.getDate(),
                    rotas.getCodUnidNeg(),
                    rotas.getCliente().getCodigo()};

            database.update("TB_ROTAGUIADA_TAREFA", contentValues,
                    "COD_REG_FUNC = ? AND TSK_DAT_DIAPLANO = ? " +
                            "AND COD_UNID_NEGOC = ? AND TSK_TXT_CODCLI = ?", args);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }


    private static ContentValues getContentValuesTask(RotaTarefas item) {
        ContentValues values = new ContentValues();
        values.put("TSK_TXT_ID", item.getId());
        values.put("TSK_TXT_IDITEM", item.getIdItem());
        values.put("TSK_INT_TYPE", item.getStatus().getValue());
        values.put("COD_REG_FUNC", item.getCodRegFunc());
        values.put("COD_UNID_NEGOC", item.getCodUnNeg());
        values.put("TSK_TXT_CODCLI", item.getCodClie());
        values.put("TSK_DAT_DIAPLANO", item.getDataRota());
        values.put("DEL_FLAG", "0");


        if(item.getDtUltAlt() != null){
            values.put("DT_ULT_ALT", item.getDtUltAlt());
        } else {
            values.putNull("DT_ULT_ALT");
        }


        return values;
    }

    public RotaTarefas createNewTask(boolean isInsert, Rotas rota, String idItem, RotaGuiadaTaskType rotaGuiadaTaskType){
        RotaTarefas rotaTarefas = new RotaTarefas();
        rotaTarefas.setId(createIDTask());
        rotaTarefas.setIdItem(idItem);
        rotaTarefas.setCodClie(rota.getCliente().getCodigo());
        rotaTarefas.setDataRota(rota.getDate());
        rotaTarefas.setCodRegFunc(rota.getCodRegFunc());
        rotaTarefas.setCodUnNeg(rota.getCodUnidNeg());
        rotaTarefas.setStatus(rotaGuiadaTaskType.getValue());

        if(isInsert){
            insertTask(rotaTarefas);
        }

        return rotaTarefas;
    }

    private String createIDTask() {
        Current current = Current.getInstance(getContext());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
        final String now = simpleDateFormat.format(new Date());
        final String userId = current.getUsuario().getCodigo();
        return String.format("%s%s%s", TASK_CODIGO_IDENTITY_LETTER, now, userId);
    }

    public class RotaGuiadaTaskWrapper extends CursorWrapper {

        public RotaGuiadaTaskWrapper(Cursor cursor) {
            super(cursor);
        }

        public RotaTarefas getTask(boolean isSync) {
            RotaTarefas rotasResumo = new RotaTarefas();
            rotasResumo.setId(getString(getColumnIndex("TSK_TXT_ID")));
            rotasResumo.setIdItem(getString(getColumnIndex("TSK_TXT_IDITEM")));
            rotasResumo.setStatus(getInt(getColumnIndex("TSK_INT_TYPE")));
            rotasResumo.setDataRota(getString(getColumnIndex("TSK_DAT_DIAPLANO")));
            rotasResumo.setCodClie(getString(getColumnIndex("TSK_TXT_CODCLI")));
            rotasResumo.setCodUnNeg(getString(getColumnIndex("COD_UNID_NEGOC")));
            rotasResumo.setCodRegFunc(getString(getColumnIndex("COD_REG_FUNC")));

            try {
                rotasResumo.setAtividJust(getInt(getColumnIndex("TSK_INT_JUSTIF_ATIV")));
            }catch (Exception ex){

            }

            if(isSync){
                rotasResumo.setDtUltAlt(FormatUtils.toTextToCompareDateInSQlite(new Date()));
            } else {
                rotasResumo.setDtUltAlt(getString(getColumnIndex("DT_ULT_ALT")));
            }

            return rotasResumo;
        }
    }
}
