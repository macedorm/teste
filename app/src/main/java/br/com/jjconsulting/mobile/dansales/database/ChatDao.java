package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.kotlin.model.PedidoTrackingMessage;
import br.com.jjconsulting.mobile.dansales.model.MessageChat;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Planta;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.dao.DataAccess;
import br.com.jjconsulting.mobile.jjlib.dao.TypeDbInfo;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Element;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementField;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ChatDao extends BaseDansalesDao {

    public final static String nameTableChat  = "TB_CHAT";

    public ChatDao(Context context) {
        super(context);
        createTable();
    }

    private void createTable(){
        Element element = new Element();
        element.setName(nameTableChat);

        List<ElementField> elementFieldList = new ArrayList<>();

        ElementField elementFieldID = new ElementField();
        elementFieldID.setIspk(false);
        elementFieldID.setDatatype(1);
        elementFieldID.setFieldname("ID_MESSAGE");
        elementFieldID.setIsrequired(false);
        elementFieldList.add(elementFieldID);

        ElementField elementFieldNF= new ElementField();
        elementFieldNF.setIspk(false);
        elementFieldNF.setDatatype(1);
        elementFieldNF.setFieldname("NF");
        elementFieldNF.setIsrequired(true);
        elementFieldList.add(elementFieldNF);

        ElementField elementFieldSerial= new ElementField();
        elementFieldSerial.setIspk(false);
        elementFieldSerial.setDatatype(1);
        elementFieldSerial.setFieldname("SERIAL");
        elementFieldSerial.setIsrequired(true);
        elementFieldList.add(elementFieldSerial);

        ElementField elementFieldCNPJ = new ElementField();
        elementFieldCNPJ.setIspk(false);
        elementFieldCNPJ.setDatatype(1);
        elementFieldCNPJ.setFieldname("CNPJ");
        elementFieldCNPJ.setIsrequired(true);
        elementFieldList.add(elementFieldCNPJ);

        ElementField elementFieldMessage = new ElementField();
        elementFieldMessage.setIspk(false);
        elementFieldMessage.setDatatype(1);
        elementFieldMessage.setFieldname("MESSAGE");
        elementFieldMessage.setIsrequired(false);
        elementFieldList.add(elementFieldMessage);

        ElementField elementFieldName = new ElementField();
        elementFieldName.setIspk(false);
        elementFieldName.setDatatype(1);
        elementFieldName.setFieldname("NAME");
        elementFieldName.setIsrequired(false);
        elementFieldList.add(elementFieldName);

        ElementField elementFieldData = new ElementField();
        elementFieldData.setIspk(false);
        elementFieldData.setDatatype(1);
        elementFieldData.setFieldname("DATE");
        elementFieldData.setIsrequired(false);
        elementFieldList.add(elementFieldData);

        ElementField elementFieldType = new ElementField();
        elementFieldType.setIspk(false);
        elementFieldType.setDatatype(4);
        elementFieldType.setFieldname("TYPE");
        elementFieldType.setIsrequired(true);
        elementFieldList.add(elementFieldType);

        ElementField elementFieldTypeUser = new ElementField();
        elementFieldTypeUser.setIspk(false);
        elementFieldTypeUser.setDatatype(4);
        elementFieldTypeUser.setFieldname("TYPE_USER");
        elementFieldTypeUser.setIsrequired(true);
        elementFieldList.add(elementFieldTypeUser);

        element.setFields(elementFieldList);

        createTable(getDb(), element);
    }

    public void deleteLastMessage() {
        SQLiteDatabase database = getDb();
        String whereClause = "id = (SELECT MAX(ID) FROM TB_ISAACCHAT)";
        database.delete(nameTableChat, whereClause, null);
    }

    public PedidoTrackingMessage getMessage(int id) {
        PedidoTrackingMessage messageChatArray = null;

        String query = "SELECT * FROM " + nameTableChat + " " +
                "Where ID_MESSAGE = ? ";

        String[] whereArgs = {String.valueOf(id)};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                messageChatArray = new ChatDao.ChatCursorWrapper(cursor).getChat();
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return messageChatArray;
    }

    public ArrayList<PedidoTrackingMessage> getAll(String nf, String serial, String cnpj) {
        ArrayList<PedidoTrackingMessage> messageChatArrayList = new ArrayList<>();

        String query = "SELECT * FROM " + nameTableChat + " " +
                "Where NF =? AND SERIAL= ? AND CNPJ LIKE '%" + cnpj + "%'";

        String[] whereArgs = {nf, serial};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, whereArgs)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                messageChatArrayList.add(new ChatDao.ChatCursorWrapper(cursor).getChat());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return messageChatArrayList;
    }

    public PedidoTrackingMessage convertPedidoTrackingMessage(Map<String, String> push){
        PedidoTrackingMessage  messageChatLastSend = new PedidoTrackingMessage();
        messageChatLastSend.setNf(push.get("NumNF"));
        messageChatLastSend.setSerial(push.get("Serie"));
        messageChatLastSend.setMessage(push.get("Mensagem"));
        messageChatLastSend.setName(push.get("NomeUsuario"));
        messageChatLastSend.setCnpj(push.get("CnpjEmitente"));
        messageChatLastSend.setDate(FormatUtils.toTextToCompareDateInSQlite(new Date()));
        messageChatLastSend.setType(Integer.parseInt(push.get("TipoUsuario")));
        messageChatLastSend.setTypeUser(true);

        return  messageChatLastSend;
    }

    public void insert(PedidoTrackingMessage messageChat) {
        ContentValues contentValues = getContentValues(messageChat);
        SQLiteDatabase database = getDb();
        database.insert(nameTableChat, null, contentValues);
    }


    public class ChatCursorWrapper extends CursorWrapper {

        public ChatCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public  PedidoTrackingMessage getChat() {
            PedidoTrackingMessage messageChat = new PedidoTrackingMessage();
            messageChat.setId(getInt(getColumnIndex("ID_MESSAGE")));
            messageChat.setDate(getString(getColumnIndex("DATE")));
            messageChat.setMessage(getString(getColumnIndex("MESSAGE")));
            messageChat.setNf(getString(getColumnIndex("NF")));
            messageChat.setCnpj(getString(getColumnIndex("CNPJ")));
            messageChat.setSerial(getString(getColumnIndex("SERIAL")));
            messageChat.setName(getString(getColumnIndex("NAME")));
            messageChat.setType(getInt(getColumnIndex("TYPE")));

            if(getInt(getColumnIndex("TYPE_USER")) == 0){
                messageChat.setTypeUser(false);
            } else {
                messageChat.setTypeUser(true);
            }

            return messageChat;
        }
    }

    private static ContentValues getContentValues(PedidoTrackingMessage messageChat) {
        ContentValues values = new ContentValues();
        values.put("ID_MESSAGE", messageChat.getId());
        values.put("MESSAGE", messageChat.getMessage());
        values.put("DATE", messageChat.getDate());
        values.put("NF", messageChat.getNf());
        values.put("SERIAL", messageChat.getSerial());
        values.put("NAME", messageChat.getName());
        values.put("TYPE", messageChat.getType());
        values.put("CNPJ", messageChat.getCnpj());

        if(messageChat.getTypeUser()){
            values.put("TYPE_USER", 1);
        } else {
            values.put("TYPE_USER", 0);
        }

        return values;
    }

}
