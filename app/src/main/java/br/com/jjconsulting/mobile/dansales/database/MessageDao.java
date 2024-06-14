package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.MessageFilter;
import br.com.jjconsulting.mobile.dansales.model.AttachMessage;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.model.MessageAccess;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class MessageDao extends BaseDansalesDao {


    public MessageDao(Context context) {
        super(context);
    }


    public void updateSync(String codigo, Date date) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(date));

            database.update("TB_MENSAGEM_LIDAS", contentValues,
                    "ID_MENSAGEM = ?", new String[]{codigo});
        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.dansales.util.Config.TAG, ex.toString());
        } finally {
            database.close();
        }
    }

    public void insertMessageRead(Message message) {
        SQLiteDatabase database = getDb();

        try {
            database.insertWithOnConflict("TB_MENSAGEM_LIDAS", "ID_MENSAGEM",
                    getContentMessageRead(message), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.dansales.util.Config.TAG, ex.toString());
        } finally {
            database.close();
        }
    }

    public void pushAllMessageRead(Context context) {
        SQLiteDatabase database = getDb();

        try {
            List<Message>messages = getMessages(Current.getInstance(context).getUsuario(),
                    Current.getInstance(context).getUnidadeNegocio().getCodigo(),
                    new Date(),null,null, true, false, false, -1);


            for(Message msg:messages){
                if(msg.getIdMessage() < 0){
                    database.insertWithOnConflict("TB_MENSAGEM_LIDAS", "ID_MENSAGEM",
                            getContentMessageRead(msg), SQLiteDatabase.CONFLICT_REPLACE);
                }
            }

        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.dansales.util.Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }

    public void insertAccess(MessageAccess message) {
        SQLiteDatabase database = getDb();

        try {
            database.insertWithOnConflict("TB_MENSAGEM_ACESSO", "ID_MENSAGEM",
                    getContentValuesMessageAccess(message), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.dansales.util.Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }

    public void insert(Message message) {
        SQLiteDatabase database = getDb();

        try {
            database.insertWithOnConflict("TB_MENSAGEM", "ID_MENSAGEM",
                    getContentValues(message), SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.dansales.util.Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }

    public void delete(String codigo) {
        SQLiteDatabase database = getDb();

        try {
            database.delete("TB_MENSAGEM_LIDAS",
                    "ID_MENSAGEM = ?", new String[]{codigo});
        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.dansales.util.Config.TAG, ex.toString());
        }finally {
            database.close();
        }
    }

    public ArrayList<Integer> getSyncMensagensLidas(){
        ArrayList<Integer> listMensagens = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_MENSAGEM ");
        query.append("FROM TB_MENSAGEM_LIDAS ");
        query.append(" WHERE DT_ULT_ALT IS NULL ");

        List<String> whereArgs = new ArrayList<>();

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                listMensagens.add(cursor.getInt(cursor.getColumnIndex("ID_MENSAGEM")));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }


        return listMensagens;
    }

    public int getIDNewMessage(){
        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_MENSAGEM ");
        query.append("FROM TB_MENSAGEM ");
        query.append("WHERE ID_MENSAGEM  < 0 ");
        query.append("ORDER BY ID_MENSAGEM ASC ");
        query.append("LIMIT 1");

        List<String> whereArgs = new ArrayList<>();

        int id = -1;

        SQLiteDatabase database = getDb();
        try {
            Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]));
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("ID_MENSAGEM"));
                id = id - 1;
                cursor.moveToNext();
            }
        } finally {
            database.close();
        }
        return id;
    }


    public ArrayList<AttachMessage> getAttach(int idMessage) {
        ArrayList<AttachMessage> listAttach = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT ID_ANEXO, ");
        query.append(" ID_MENSAGEM, ");
        query.append(" TIPO_ANEXO, ");
        query.append(" NOME, ");
        query.append(" TAMANHO_BYTES, ");
        query.append(" DT_ULT_ALT ");
        query.append("FROM TB_MENSAGEM_ANEXO ");
        query.append(" WHERE DEL_FLAG <> '1' AND ID_MENSAGEM = ? ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(String.valueOf(idMessage));

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                AttachMessage message = new AttachMessageCursorWrapper(cursor).getAttachMessage();
                listAttach.add(message);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return listAttach;
    }


    public List<Message> getMessagesVigenciaExt(Usuario usuario, String unidadeNegocio, Date date, String nome,  MessageFilter messageFilter, int indexOffSet) {
        return  getMessages(usuario, unidadeNegocio, date, nome, messageFilter, false, true, true, indexOffSet);
    }

    public List<Message> getMessages(Usuario usuario, String unidadeNegocio, Date date, MessageFilter messageFilter, boolean isFilterRead) {
        return  getMessages(usuario, unidadeNegocio, date, null, messageFilter, isFilterRead, true, false, -1);
    }

    public List<Message> getMessages(Usuario usuario, String unidadeNegocio, Date date, String nome,  MessageFilter messageFilter, int indexOffSet) {
        return  getMessages(usuario, unidadeNegocio, date, nome, messageFilter, false, true, false, indexOffSet);
    }

    public int countMensagensNovas(Usuario usuario, String unidadeNegocio) {
        return  getMessages(usuario, unidadeNegocio, new Date(), null, null, true, false, false, -1).size();
    }


    private List<Message> getMessages(Usuario usuario, String unidadeNegocio, Date date, String nome, MessageFilter messageFilter, boolean isFilterRead, boolean isAddAttach, boolean vigenciaExt, int indexOffSet) {
        List<Message> listMessages = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT men.ID_MENSAGEM, ");
        query.append(" men.REMETENTE, ");
        query.append(" men.TITULO, ");
        query.append(" men.VIGENCIA_DE, ");
        query.append(" men.VIGENCIA_ATE, ");
        query.append(" men.DATA_ENVIO, ");
        query.append(" men.UNID_NEG, ");
        query.append(" men.MENSAGEM, ");
        query.append(" men.TIPO, ");
        query.append(" men.COD_REG_FUNC, ");
        query.append(" men.DT_ULT_ALT, ");
        query.append(" men.DEL_FLAG, ");
        query.append(" (SELECT lidas.ID_MENSAGEM FROM TB_MENSAGEM_LIDAS as lidas ");
        query.append(     "WHERE lidas.ID_MENSAGEM = men.ID_MENSAGEM AND COD_REG_FUNC = '");
        query.append(      usuario.getCodigo() + "') as MESSAGEM_LIDA ");
        query.append("FROM TB_MENSAGEM AS men");
        query.append(" INNER JOIN TB_MENSAGEM_ACESSO AS aces");
        query.append(" ON men.ID_MENSAGEM = aces.ID_MENSAGEM ");
        query.append("WHERE men.DEL_FLAG <> '1' ");
        query.append(" AND (men.UNID_NEG ISNULL OR men.UNID_NEG = ?) ");
        query.append(" AND aces.ID_FUNCAO = ? AND aces.ID_FILTRO IN (0,1) ");
        query.append(" AND men.VIGENCIA_DE  <= datetime('" + FormatUtils.toTextToCompareshortDateInSQlite(date) + "') ");

        if(vigenciaExt){
            query.append(" AND datetime(men.VIGENCIA_ATE,'+90 day') >= datetime('" + FormatUtils.toTextToCompareshortDateInSQlite(date) + "')");
        } else {
            query.append(" AND men.VIGENCIA_ATE  >= datetime('" + FormatUtils.toTextToCompareshortDateInSQlite(date) + "')");
        }

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(String.valueOf(unidadeNegocio));
        whereArgs.add(String.valueOf(usuario.getCodigo()));

        if (!TextUtils.isNullOrEmpty(nome)) {
            query.append(" AND men.TITULO LIKE ? ");
            whereArgs.add("%" + nome + "%");
        }

        if (messageFilter != null) {
            if (messageFilter.getTMessageType() != null) {
                query.append(" AND men.TIPO = ? ");
                whereArgs.add(String.valueOf(messageFilter.getTMessageType().getValue()));
            }
        }

        if(isFilterRead){
            query.append(" AND MESSAGEM_LIDA IS NULL ");
        }

        query.append(" group by aces.ID_MENSAGEM ");
        query.append(" order by aces.ID_MENSAGEM ASC ");

        if (indexOffSet > -1) {
            query.append(" " + "Limit " + br.com.jjconsulting.mobile.dansales.util.Config.SIZE_PAGE + " OFFSET " + indexOffSet);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Message message = new MessageCursorWrapper(cursor).getMessage();

                if(isAddAttach){
                    message.setAttachMessage(getAttach(message.getIdMessage()));
                }

                listMessages.add(message);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        if(listMessages != null && listMessages.size() > 0) {
           // organizeListMessage(listMessages);
        }

        return listMessages;
    }

    public void setMessagemLida(Message message){
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID_MENSAGEM", message.getIdMessage());

        if(message.getIdMessage() > 0){
            contentValues.putNull("DT_ULT_ALT");
        }

        contentValues.put("COD_REG_FUNC", Current.getInstance(getContext()).getUsuario().getCodigo());
        SQLiteDatabase database = getDb();
        try{
            database.insertWithOnConflict("TB_MENSAGEM_LIDAS",  null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }finally {
            database.close();
        }
    }

    public class MessageCursorWrapper extends CursorWrapper {

        public MessageCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Message getMessage() {
            Message message = new Message();

            message.setIdMessage(getInt(getColumnIndex("ID_MENSAGEM")));
            message.setTitle(getString(getColumnIndex("TITULO")));
            message.setSender(getString(getColumnIndex("REMETENTE")));
            message.setStartDate(getString(getColumnIndex("VIGENCIA_DE")));
            message.setEndDate(getString(getColumnIndex("VIGENCIA_ATE")));
            message.setDate(getString(getColumnIndex("DATA_ENVIO")));
            message.setBody(getString(getColumnIndex("MENSAGEM")));
            message.setCodRegFunc(getString(getColumnIndex("COD_REG_FUNC")));

            message.setType(TMessageType.fromInteger(getInt(getColumnIndex("TIPO"))));

            String idMessageRead = getString(getColumnIndex("MESSAGEM_LIDA"));
            message.setRead(TextUtils.isNullOrEmpty(idMessageRead) ? false:true);

            return message;
        }
    }


    public class AttachMessageCursorWrapper extends CursorWrapper {

        public AttachMessageCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public AttachMessage getAttachMessage() {
            AttachMessage attachMessage = new AttachMessage();

            attachMessage.setIdMessage(getInt(getColumnIndex("ID_MENSAGEM")));
            attachMessage.setIdAttach(getInt(getColumnIndex("ID_ANEXO")));
            attachMessage.setName(getString(getColumnIndex("NOME")));
            attachMessage.setType(getInt(getColumnIndex("TIPO_ANEXO")));

            return attachMessage;
        }
    }

    private ContentValues getContentValues(Message message) {
        ContentValues values = new ContentValues();

        values.put("ID_MENSAGEM", message.getIdMessage());
        values.put("REMETENTE", message.getSender());
        values.put("TITULO", message.getTitle());
        values.put("VIGENCIA_DE", message.getStartDate());
        values.put("VIGENCIA_ATE", message.getEndDate());
        values.put("DATA_ENVIO", message.getDate());
        values.put("OBRIGATORIO_LEITURA", message.getDate());

        if(message.getUnidNeg() == null){
            values.putNull("UNID_NEG");
        } else {
            values.put("UNID_NEG", message.getUnidNeg());
        }

        if(message.getCodRegFunc() == null){
            values.putNull("COD_REG_FUNC");
        } else {
            values.put("COD_REG_FUNC", message.getCodRegFunc());
        }

        values.put("MENSAGEM", message.getBody());
        values.put("TIPO", message.getType().getValue());
        values.put("DEL_FLAG", "0");
        values.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(new Date()));

        return values;
    }

    private ContentValues getContentMessageRead(Message message) {
        ContentValues values = new ContentValues();

        values.put("ID_MENSAGEM", message.getIdMessage());

        if(message.getCodRegFunc() == null){
            values.putNull("COD_REG_FUNC");
        } else {
            values.put("COD_REG_FUNC", message.getCodRegFunc());
        }

        values.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(new Date()));

        return values;
    }


    private ContentValues getContentValuesMessageAccess(MessageAccess message) {
        ContentValues values = new ContentValues();

        values.put("ID_MENSAGEM", message.getIdMessage());

        if(message.getIdFuncao() == null){
            values.putNull("ID_FUNCAO");
        } else {
            values.put("ID_FUNCAO", message.getIdFuncao());
        }

        if(message.getIdFiltro() == null){
            values.putNull("ID_FILTRO");
        } else {
            values.put("ID_FILTRO", message.getIdFiltro());
        }

        if(message.getCodRecFunc() == null){
            values.putNull("COD_REG_FUNC");
        } else {
            values.put("COD_REG_FUNC", message.getCodRecFunc());
            values.put("COD_REG_FUNC", message.getCodRecFunc());
        }

        values.put("DEL_FLAG", "0");
        values.put("DT_ULT_ALT", FormatUtils.toTextToCompareDateInSQlite(new Date()));

        return values;
    }

}