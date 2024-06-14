package br.com.jjconsulting.mobile.jjlib.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import br.com.jjconsulting.mobile.jjlib.dao.entity.Element;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementIndex;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRegSync;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

/**
 * Manipulacao de dados genericos
 *
 * @category Lib
 */
public class DataAccess {


    public InfoTable getInfoTable(Class<? extends Object> obj) {
        InfoTable tbInfo = null;
        if (obj.isAnnotationPresent(InfoTable.class)) {
            tbInfo = (InfoTable) obj.getAnnotation(InfoTable.class);
        }

        return tbInfo;
    }

    public ArrayList<InfoField> getInfoField(Class<? extends Object> obj) {
        ArrayList<InfoField> list = new ArrayList<InfoField>();

        for (Field method : obj.getDeclaredFields()) {
            if (method.isAnnotationPresent(InfoField.class)) {
                InfoField fieldInfo = (InfoField) method.getAnnotation(InfoField.class);
                list.add(fieldInfo);
            }
        }

        return list;
    }

    public InfoField getFirstPk(Class<? extends Object> classobj) {
        ArrayList<InfoField> listField = getInfoField(classobj);
        InfoField pk = null;
        for (InfoField infoField : listField) {
            if (infoField.isPK()) {
                pk = infoField;
                break;
            }
        }
        return pk;
    }

    public void createTable(SQLiteDatabase db, Class<? extends Object> obj) {
        db.execSQL(getScriptCreateTable(obj));
    }

    public void createTable(SQLiteDatabase db, Element element) {
        //Create Table
        db.execSQL(getScriptCreateTable(element));

        //Create Indexes
        List<String> scritIndex = getScriptCreateIndex(element);
        for (String script : scritIndex) {
            db.execSQL(script);
        }
    }

    public void createTable(SQLiteDatabase db, List<Element> tables) {

        db.beginTransaction();
        try {
            for (Element e : tables) {
                //Create Table
                db.execSQL(getScriptCreateTable(e));

                //TODO: PAN esse deveria ser criado após o primeiro sincronismo
                //Create Indexes
                List<String> scritIndex = getScriptCreateIndex(e);
                for (String script : scritIndex) {
                    db.execSQL(script);
                }
            }
            db.setTransactionSuccessful();
        }catch (Exception ex) {
            throw ex;
        }finally {
            db.endTransaction();
        }
    }


    public String getScriptCreateTable(Class<? extends Object> obj) {
        StringBuilder sSql = new StringBuilder();
        StringBuilder sPk = new StringBuilder();
        InfoTable tbInfo = getInfoTable(obj);
        // Process @InfoTable
        if (tbInfo != null) {

            sSql.append("create table [");
            sSql.append(tbInfo. tableName());
            sSql.append("] (");

            // Process @InfoField
            boolean isFirst = true;
            for (Field method : obj.getDeclaredFields()) {

                if (method.isAnnotationPresent(InfoField.class)) {
                    InfoField fieldInfo = (InfoField) method.getAnnotation(InfoField.class);

                    if (isFirst)
                        isFirst = false;
                    else
                        sSql.append(",");

                    sSql.append("[");
                    sSql.append(fieldInfo.fieldName());
                    sSql.append("] ");
                    sSql.append(fieldInfo.type().toString());

                    if (!fieldInfo.isNull()) {
                        sSql.append(" not null ");
                    }

                    if (fieldInfo.isPK()) {
                        if (sPk.length() > 0)
                            sPk.append(",");

                        sPk.append("[");
                        sPk.append(fieldInfo.fieldName());
                        sPk.append("]");
                    }
                }
            }

            if (sPk.length() > 0) {
                sSql.append(", CONSTRAINT [PK_");
                sSql.append(tbInfo.tableName());
                sSql.append("] PRIMARY KEY (");
                sSql.append(sPk.toString());
                sSql.append(")");
            }

            sSql.append(");");
        }

        return sSql.toString();
    }

    public String getScriptCreateTable(Element element) {
        StringBuilder sSql = new StringBuilder();
        StringBuilder sPk = new StringBuilder();

        if (element != null) {
            sSql.append("create table if not exists [");
            sSql.append(element.getTableName());
            sSql.append("] (");

            boolean isFirst = true;
            for (ElementField fieldInfo : element.getFields()) {
                if (isFirst)
                    isFirst = false;
                else
                    sSql.append(",");

                sSql.append("[");
                sSql.append(fieldInfo.getFieldname());
                sSql.append("] ");
                sSql.append(fieldInfo.getTypeDbInfo().toString());

                if (fieldInfo.getIsrequired()) {
                    sSql.append(" not null ");
                }

                if (fieldInfo.getIspk()) {
                    if (sPk.length() > 0)
                        sPk.append(",");

                    sPk.append("[");
                    sPk.append(fieldInfo.getFieldname());
                    sPk.append("]");
                }
            }

            if (sPk.length() > 0) {
                sSql.append(", CONSTRAINT [PK_");
                sSql.append(element.getTableName());
                sSql.append("] PRIMARY KEY (");
                sSql.append(sPk.toString());
                sSql.append(")");
            }

            sSql.append(");");
        }

        LogUser.log("DataAccess", "SQL: " + sSql.toString());

        return sSql.toString();
    }


    public List<String> getScriptCreateIndex(Element element) {
        List<String> list = new ArrayList<>();

        if (element != null &&
                element.getIndexes() != null &&
                element.getIndexes().size() > 0) {

            int nIndex = 1;
            for (ElementIndex index : element.getIndexes()) {

                StringBuilder sSql = new StringBuilder();
                sSql.append("\r\n");
                sSql.append("CREATE");
                sSql.append(index.isUnique() ? " UNIQUE" : "");
                sSql.append(" INDEX IF NOT EXISTS IX_");
                sSql.append(element.getTableName());
                sSql.append("_");
                sSql.append(nIndex);
                sSql.append(" ON ");
                sSql.append(element.getTableName());
                sSql.append(" (");
                for (int i = 0; i < index.getColumns().size(); i++) {
                    if (i > 0)
                        sSql.append(", ");

                    sSql.append(index.getColumns().get(i));
                }

                sSql.append("); ");
                list.add(sSql.toString());
                LogUser.log("DataAccess", "SQL INDEX: " + sSql.toString());
                nIndex++;
            }
        }

        return list;
    }

    /**
     * Apaga todos os registros da tabela
     *
     * @param db  Banco de dados aberto
     * @param obj Tipo dp objeto
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public void truncateTable(SQLiteDatabase db, Class<? extends Object> obj) {
        db.execSQL(getScriptTruncateTable(obj));
    }

    public String getScriptTruncateTable(Class<? extends Object> obj) {
        StringBuilder sSql = new StringBuilder();
        InfoTable tbInfo = getInfoTable(obj);

        if (tbInfo != null) {
            sSql.append("DELETE FROM [");
            sSql.append(tbInfo.tableName());
            sSql.append("] ");
        }

        return sSql.toString();
    }

    /**
     * Apaga todos os registros da tabela
     *
     * @param db  Banco de dados aberto
     * @param obj Tipo dp objeto
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public void dropTable(SQLiteDatabase db, Class<? extends Object> obj) {
        db.execSQL(getScriptDropTable(obj));
    }

    public String getScriptDropTable(Class<? extends Object> obj) {
        StringBuilder sSql = new StringBuilder();
        InfoTable tbInfo = getInfoTable(obj);

        if (tbInfo != null) {
            sSql.append("DROP TABLE [");
            sSql.append(tbInfo.tableName());
            sSql.append("] ");
        }

        return sSql.toString();
    }

    /**
     * Apaga todos os registros da tabela
     *
     * @param db Banco de dados aberto
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public void truncateTable(SQLiteDatabase db, String name) {
        db.execSQL(getScriptTruncateTable(name));
    }

    public String getScriptTruncateTable(String name) {
        StringBuilder sSql = new StringBuilder();

        sSql.append("DELETE FROM [");
        sSql.append(name);
        sSql.append("] ");


        return sSql.toString();
    }


    /**
     * Deleta uma table
     *
     * @param db Banco de dados aberto
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public void dropTable(SQLiteDatabase db, String name) {
        db.execSQL(getScriptDropTable(name));
    }

    public String getScriptDropTable(String name) {
        StringBuilder sSql = new StringBuilder();

        sSql.append("DROP TABLE [");
        sSql.append(name);
        sSql.append("] ");


        return sSql.toString();
    }

    /**
     * Recupera Content Values do Objeto
     *
     * @param bean Referencia do Objeto instaciado
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public ContentValues getContentValues(Object bean) {
        Class<? extends Object> obj = bean.getClass();
        Field[] listMethod = obj.getDeclaredFields();
        ContentValues cv = new ContentValues();

        InfoField fieldInfo = null;
        for (Field method : listMethod) {

            if (method.isAnnotationPresent(InfoField.class)) {
                method.setAccessible(true);
                fieldInfo = (InfoField) method.getAnnotation(InfoField.class);

                try {
                    switch (fieldInfo.type()) {
                        case INTEGER:
                            int nValue = method.getInt(bean);
                            if (nValue == Integer.MIN_VALUE)
                                cv.putNull(fieldInfo.fieldName());
                            else
                                cv.put(fieldInfo.fieldName(), nValue);

                            break;
                        case REAL:
                            float fValue = method.getFloat(bean);
                            if (fValue == Float.MIN_VALUE)
                                cv.putNull(fieldInfo.fieldName());
                            else
                                cv.put(fieldInfo.fieldName(), fValue);

                        default:
                            String sValue = String.valueOf(method.get(bean));
                            if (sValue == null && fieldInfo.isNull())
                                cv.putNull(fieldInfo.fieldName());
                            else
                                cv.put(fieldInfo.fieldName(), sValue);
                            break;
                    }

                } catch (Exception e) {

                }
            }
        }

        return cv;
    }

    /**
     * Recupera um objeto do banco de dados
     *
     * @param db       Banco de dados aberto
     * @param id       C&oacute;digo (PK)
     * @param classobj Tipo dp objeto
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public Object getContentObjById(SQLiteDatabase db, Object id, Class<? extends Object> classobj) {
        InfoTable tbInfo = getInfoTable(classobj);
        Object obj = null;
        if (tbInfo != null) {
            try {
                InfoField pk = getFirstPk(classobj);
                StringBuilder sqlQ = new StringBuilder();
                sqlQ.append("select * from [");
                sqlQ.append(tbInfo.tableName());
                sqlQ.append("] where [");
                sqlQ.append(pk.fieldName());
                sqlQ.append("] = '");
                sqlQ.append(id);
                sqlQ.append("'");


                Cursor cursor = db.rawQuery(sqlQ.toString(), null);
                while (cursor.moveToNext()) {
                    obj = (Object) Class.forName(classobj.getName()).newInstance();
                    InfoField fieldInfo;
                    for (Field method : classobj.getDeclaredFields()) {

                        if (method.isAnnotationPresent(InfoField.class)) {
                            fieldInfo = (InfoField) method.getAnnotation(InfoField.class);
                            method.setAccessible(true);
                            int columnIndex = cursor.getColumnIndex(fieldInfo.fieldName());

                            switch (fieldInfo.type()) {
                                case INTEGER:
                                    method.set(obj, cursor.getInt(columnIndex));

                                    break;
                                case REAL:
                                    method.set(obj, cursor.getFloat(columnIndex));
                                default:
                                    method.set(obj, cursor.getString(columnIndex));
                                    break;
                            }

                        }
                    }
                    return obj;
                }

            } catch (Exception e) {
                //throw e;
                e.printStackTrace();
            }
        }

        return obj;
    }

    /**
     * Recupera um objeto do banco de dados
     *
     * @param db       Banco de dados aberto
     * @param id       C&oacute;digo (PK)
     * @param classobj Tipo dp objeto
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public Object getObjById(SQLiteDatabase db, Object id, Class<? extends Object> classobj) {
        InfoTable tbInfo = getInfoTable(classobj);
        Object obj = null;
        if (tbInfo != null) {
            try {
                InfoField pk = getFirstPk(classobj);
                StringBuilder sqlQ = new StringBuilder();
                sqlQ.append("select * from [");
                sqlQ.append(tbInfo.tableName());
                sqlQ.append("] where [");
                sqlQ.append(pk.fieldName());
                sqlQ.append("] = '");
                sqlQ.append(id);
                sqlQ.append("'");


                Cursor cursor = db.rawQuery(sqlQ.toString(), null);
                while (cursor.moveToNext()) {
                    obj = (Object) Class.forName(classobj.getName()).newInstance();
                    InfoField fieldInfo;
                    for (Field method : classobj.getDeclaredFields()) {

                        if (method.isAnnotationPresent(InfoField.class)) {
                            fieldInfo = (InfoField) method.getAnnotation(InfoField.class);
                            method.setAccessible(true);
                            int columnIndex = cursor.getColumnIndex(fieldInfo.fieldName());
                            if (!cursor.isNull(columnIndex)) {
                                switch (fieldInfo.type()) {
                                    case INTEGER:
                                        method.set(obj, cursor.getInt(columnIndex));
                                        break;
                                    case REAL:
                                        method.set(obj, cursor.getFloat(columnIndex));
                                        break;
                                    default:
                                        method.set(obj, cursor.getString(columnIndex));
                                        break;
                                }
                            }
                        }
                    }
                    return obj;
                }
                cursor.close();
            } catch (Exception e) {
                //throw e;
                e.printStackTrace();
            }
        }

        return obj;
    }

    /**
     * Inclui um objeto no banco de dados
     *
     * @param db   Banco de dados aberto
     * @param bean Objeto Bean
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public void insertObj(SQLiteDatabase db, Object bean) {
        Class<? extends Object> obj = bean.getClass();

        // Process @InfoTable
        if (obj.isAnnotationPresent(InfoTable.class)) {

            InfoTable tbInfo = (InfoTable) obj.getAnnotation(InfoTable.class);
            ContentValues cv = getContentValues(bean);
            db.insert(tbInfo.tableName(), null, cv);
        }
    }

    /**
     * Atualiza uma lista de objetos no banco de dados
     *
     * @param db       Banco de dados aberto
     * @param listBean Lista de Objetos Bean
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public void insertObj(SQLiteDatabase db, Object[] listBean) {
        try {
            Class<? extends Object> obj = listBean[0].getClass();
            InfoTable tbInfo = (InfoTable) obj.getAnnotation(InfoTable.class);
            Field[] listMethod = obj.getDeclaredFields();

            //Construindo o comando insert
            StringBuilder sSql = new StringBuilder();
            StringBuilder sFil = new StringBuilder();
            if (tbInfo != null) {

                sSql.append("insert into [");
                sSql.append(tbInfo.tableName());
                sSql.append("] (");

                // Process @InfoField
                for (int i = 0; i < listMethod.length; i++) {
                    Field method = listMethod[i];
                    if (method.isAnnotationPresent(InfoField.class)) {
                        InfoField fieldInfo = (InfoField) method.getAnnotation(InfoField.class);

                        if (i > 0) {
                            sSql.append(",");
                            sFil.append(",");
                        }

                        sSql.append(fieldInfo.fieldName());
                        sFil.append("?");
                    }
                }
                sSql.append(") values (");
                sSql.append(sFil.toString());
                sSql.append(");");
            }

            //Iniciando a transacao
            db.beginTransaction();

            //Compilando o comando
            SQLiteStatement stmt = db.compileStatement(sSql.toString());

            //Inserindo os registros
            InfoField fieldInfo = null;
            for (Object bean : listBean) {
                for (int i = 0; i < listMethod.length; i++) {
                    Field method = listMethod[i];
                    if (method.isAnnotationPresent(InfoField.class)) {
                        method.setAccessible(true);
                        fieldInfo = (InfoField) method.getAnnotation(InfoField.class);

                        switch (fieldInfo.type()) {
                            case INTEGER:
                                int nValue = method.getInt(bean);
                                if (nValue == Integer.MIN_VALUE)
                                    stmt.bindNull(i + 1);
                                else
                                    stmt.bindLong(i + 1, nValue);

                                break;
                            case REAL:
                                float fValue = method.getFloat(bean);
                                if (fValue == Float.MIN_VALUE)
                                    stmt.bindNull(i + 1);
                                else
                                    stmt.bindDouble(i + 1, fValue);

                                break;
                            default:
                                String sValue = String.valueOf(method.get(bean));
                                if (sValue == null && fieldInfo.isNull())
                                    stmt.bindNull(i + 1);
                                else
                                    stmt.bindString(i + 1, sValue);
                                break;
                        }
                    }
                }
                stmt.executeInsert();
                stmt.clearBindings();
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Atualiza um objeto no banco de dados
     *
     * @param db          Banco de dados aberto
     * @param bean        Objeto Bean
     * @param whereClause Clausula WHERE P-SQL
     * @category Lib
     * @author JJConsulting
     * @since 2018-02-27
     */
    public void updateObj(SQLiteDatabase db, Object bean, String whereClause) {
        Class<? extends Object> obj = bean.getClass();

        // Process @InfoTable
        if (obj.isAnnotationPresent(InfoTable.class)) {

            InfoTable tbInfo = (InfoTable) obj.getAnnotation(InfoTable.class);
            ContentValues cv = getContentValues(bean);
            db.update(tbInfo.tableName(), cv, whereClause, null);
        }
    }

    /**
     * Verifica se a tabela existe no banco de dados
     *
     * @param db        Banco de dados aberto
     * @param tableName Nome da Tabela
     */
    public boolean tableExist(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public String persistDataElement(SQLiteDatabase db, Element element, InputStreamReader reader, boolean isInsertOrReplace, CurrentProcess currentProcess, boolean isRegSync) throws Exception {
        String message = "";

        try {
            int countRow = 1;

            //Iniciando a transacao
            db.beginTransaction();

            //Compilando o comando
            SQLiteStatement stmt = db.compileStatement(getSqlReplace(element, isInsertOrReplace));

            //Lendo as linhas do arquivo
            BufferedReader bufferedReader = new BufferedReader(reader);

            //BufferedReader bufferedReader = new BufferedReader(reader);
            String line;

            while ((line = bufferedReader.readLine()) != null) {

                //Ignora linhas em branco
                if (line.trim().length() == 0)
                    continue;


                if(isRegSync){
                    line += "|" + TRegSync.SEND.getValue();
                }

                String[] values = line.split("\\|", -1);


                if (values.length != element.getFields().size()){
                    throw new Exception("Quantidade de campos inválidos");
                }

                bindCmdValues(element, values, stmt, false);
                stmt.execute();

                if (countRow == currentProcess.getSizeUpdateScreen()) {
                    currentProcess.setProgress(countRow);
                    countRow = 0;
                } else {
                    countRow++;
                }
            }

            if (countRow > 0) {
                currentProcess.setProgress(countRow);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            reader.close();

        } catch (Exception ex) {
            db.endTransaction();
            message = ex.toString();
            LogUser.log("query", ex.toString());
        }

        return message;
    }

    public String persistDataElement(SQLiteDatabase db, Element element, String[] response, boolean isInsertOrReplace, CurrentProcess currentProcess, boolean isRegSync) throws Exception {
        String message = "";

        try {
            int countRow = 1;

            //Iniciando a transacao
            db.beginTransaction();

            //Compilando o comando
            SQLiteStatement stmt = db.compileStatement(getSqlReplace(element, isInsertOrReplace));

            for(String line: response){

                //Ignora linhas em branco
                if (line.trim().length() == 0)
                    continue;

                if(isRegSync){
                    line += "|" + TRegSync.SEND.getValue();
                }

                String[] values = line.split("\\|", -1);

                if (values.length != element.getFields().size()){
                    throw new Exception("Quantidade de campos inválidos");
                }

                bindCmdValues(element, values, stmt, false);
                stmt.execute();

                if (countRow == currentProcess.getSizeUpdateScreen()) {
                    currentProcess.setProgress(countRow);
                    countRow = 0;
                } else {
                    countRow++;
                }
            }

            if (countRow > 0) {
                currentProcess.setProgress(countRow);
            }


            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception ex) {
            db.endTransaction();
            message = ex.toString();
            LogUser.log("query", ex.toString());
        }

        return message;
    }


    public String persistDataElement(SQLiteDatabase db, Element element, Hashtable values, boolean isInsertOrReplace) throws Exception {
        String message = "";

        try {

            //Iniciando a transacao
            db.beginTransaction();
            //Compilando o comando
            SQLiteStatement stmt = db.compileStatement(getSqlReplace(element, isInsertOrReplace));

            String valuesArray[]= new String[element.getFields().size()];

            int index = 0;
            for (ElementField item: element.getFields()) {
                if(values.get(item.getFieldname()) != null){
                    valuesArray[index] = values.get(item.getFieldname()).toString();
                } else {
                    valuesArray[index] = null;
                }

                index++;
            }

            bindCmdValues(element, valuesArray, stmt, false);
            stmt.execute();

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception ex) {
            db.endTransaction();
            message = ex.toString();
            LogUser.log("query", ex.toString());
        }

        return message;
    }


    public String deleteDataElement(SQLiteDatabase db, Element element, HashMap<String, String> filters )   throws Exception{
        String message = "";

        try {
            db.execSQL(getSqlDelete(element, filters));
        } catch (Exception ex) {
            message = ex.toString();
            LogUser.log("query", ex.toString());
        }

        return message;
    }


    private void bindCmdValues(Element element, String[] values, SQLiteStatement cmd, boolean isCount) {
        cmd.clearBindings();

        for (int i = 0; i < element.getFields().size(); i++) {
            ElementField elementField = element.getFields().get(i);
            if ((isCount && elementField.getIspk()) || !isCount) {
                String value = values[i];
                if(value == null){
                    value = "";
                }

                switch (elementField.getTypeDbInfo()) {
                    case INTEGER:
                        if (value.trim().length() > 0) {
                            int nValue = Integer.parseInt(value.trim());
                            if (nValue == Integer.MIN_VALUE) {
                                cmd.bindNull(i + 1);
                            } else {
                                cmd.bindLong(i + 1, nValue);
                            }
                        } else {
                            cmd.bindNull(i + 1);
                        }

                        break;
                    case REAL:
                        if (value.trim().length() > 0) {
                            Float nValue = Float.parseFloat(value);
                            cmd.bindDouble(i + 1, nValue);
                        } else {
                            cmd.bindNull(i + 1);
                        }

                        break;
                    case TEXT:
                        value = value.replace("&#182;", "\n");
                        value = value.replace("&#124;", "|");
                        if (!TextUtils.isNullOrEmpty(value)) {
                            cmd.bindString(i + 1, value);
                        } else {
                            if (elementField.getIspk()) {
                                cmd.bindString(i + 1, "");
                            } else {
                                cmd.bindNull(i + 1);
                            }
                        }
                        break;
                    default:
                        if (value.trim().length() > 0) {
                            cmd.bindString(i + 1, value);
                        } else {
                            cmd.bindNull(i + 1);
                        }

                        break;
                }
            }
        }
    }

    private String getSqlReplace(Element element, boolean isInsertOrReplace) {
        StringBuilder sSql = new StringBuilder();

        if (isInsertOrReplace) {
            sSql.append("insert or replace into [");
        } else {
            sSql.append("insert into [");
        }

        sSql.append(element.getTableName());
        sSql.append("] (");

        for (int i = 0; i < element.getFields().size(); i++) {
            ElementField elementField = element.getFields().get(i);
            if (i > 0) {
                sSql.append(",");
            }
            sSql.append(elementField.getFieldname());
        }

        sSql.append(") values (");

        for (int i = 0; i < element.getFields().size(); i++) {
            if (i > 0) {
                sSql.append(",");
            }
            sSql.append("?");
            sSql.append(i + 1);
        }
        sSql.append(");");


        return sSql.toString();
    }

    private String getSqlDelete(Element element, HashMap<String, String> filters) {
        StringBuilder sSql = new StringBuilder();
        sSql.append("DELETE FROM ");
        sSql.append(element.getTableName());
        sSql.append(" WHERE ");

        int index = 0;
        for (HashMap.Entry<String, String> entry : filters.entrySet()) {
            if (index > 0) {
                sSql.append(" and ");
            }
            sSql.append(entry.getKey());
            sSql.append(" = '");
            sSql.append(entry.getValue() + "'" );
            index++;
        }

        return sSql.toString();

    }

    private String getSqlUpdate(Element element) {
        StringBuilder sSql = new StringBuilder();
        sSql.append("update [");
        sSql.append(element.getTableName());
        sSql.append("] set ");

        for (int i = 0; i < element.getFields().size(); i++) {
            ElementField elementField = element.getFields().get(i);
            if (i > 0) {
                sSql.append(",");
            }
            sSql.append(elementField.getFieldname());
            sSql.append(" = ?");
            sSql.append(i + 1);
        }

        sSql.append(" where ");
        for (int i = 0; i < element.getFields().size(); i++) {
            ElementField elementField = element.getFields().get(i);
            if (elementField.getIspk()) {
                if (i > 0) {
                    sSql.append(" and ");
                }
                sSql.append(elementField.getFieldname());
                sSql.append(" = ?");
                sSql.append(i + 1);
            }
        }


        return sSql.toString();
    }

    private String getSqlCount(Element element) {
        StringBuilder sSql = new StringBuilder();
        sSql.append("select count(*)  from [");
        sSql.append(element.getTableName());
        sSql.append("] where ");
        for (int i = 0; i < element.getFields().size(); i++) {
            ElementField elementField = element.getFields().get(i);
            if (elementField.getIspk()) {
                if (i > 0) {
                    sSql.append(" and ");
                }
                sSql.append(elementField.getFieldname());
                sSql.append(" = ?");
                sSql.append(i + 1);
            }
        }

        return sSql.toString();
    }

    public void dropAllUserTables(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        //noinspection TryFinallyCanBeTryWithResources not available with API < 19
        try {
            List<String> tables = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {
                tables.add(cursor.getString(0));
            }

            for (String table : tables) {
                if (table.startsWith("sqlite_")) {
                    continue;
                }
                db.execSQL("DROP TABLE IF EXISTS " + table);
                LogUser.log( "Dropped table " + table);
            }
        } finally {
            cursor.close();
        }
    }

}
