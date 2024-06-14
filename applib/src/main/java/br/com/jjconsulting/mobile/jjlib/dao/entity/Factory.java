package br.com.jjconsulting.mobile.jjlib.dao.entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.dao.BaseDao;
import br.com.jjconsulting.mobile.jjlib.dao.CommandType;
import br.com.jjconsulting.mobile.jjlib.dao.CurrentProcess;
import br.com.jjconsulting.mobile.jjlib.dao.DataAccess;
import br.com.jjconsulting.mobile.jjlib.dao.DataAccessCommand;
import br.com.jjconsulting.mobile.jjlib.dao.DataAccessParameter;
import br.com.jjconsulting.mobile.jjlib.dao.DbType;
import br.com.jjconsulting.mobile.jjlib.dao.DictionaryDao;
import br.com.jjconsulting.mobile.jjlib.dao.ParameterDirection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Dictionary.DicParser;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.masterdata.FieldManager;
import br.com.jjconsulting.mobile.jjlib.masterdata.JJIcon;
import br.com.jjconsulting.mobile.jjlib.model.DataItem;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.model.RetFields;
import br.com.jjconsulting.mobile.jjlib.model.SpinnerDataItem;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterData;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class  Factory extends BaseDao {
    public Factory(Context context) {
        super(context);
    }

    public String persistDataElement(Element element, InputStreamReader reader, boolean isInsertOrReplace, CurrentProcess CurrentProcess, boolean isRegSync) throws Exception {
        SQLiteDatabase db = getDb();

        String persist = "";

        try{
            persist = persistDataElement(db, element, reader, isInsertOrReplace, CurrentProcess, isRegSync);
        } finally {
            db.close();
        }
        return persist;
    }

    public String persistDataElement(Element element, String[] value, boolean isInsertOrReplace, CurrentProcess CurrentProcess, boolean isRegSync) throws Exception {
        SQLiteDatabase db = getDb();
        String persist = "";
        try {
            persist = persistDataElement(db, element, value, isInsertOrReplace, CurrentProcess, isRegSync);
        }finally {
            db.close();
        }

        return persist;
    }

    public void setElement(ElementInfo info) {
        Gson gson = new Gson();
        Element element = info.getTable();
        String json = gson.toJson(element);

        String jsonForm = null;
        if (info.getForm() != null)
            jsonForm = gson.toJson(info.getForm());

        String jsonUIOptions = null;
        if (info.getUioptions() != null)
            jsonUIOptions = gson.toJson(info.getUioptions());

        MasterData m = new MasterData();
        m.setName(element.getName());
        m.setJson(json);
        m.setJsonForm(jsonForm);
        m.setJsonUIOptions(jsonUIOptions);
        m.setMode(info.getTable().getMode());

        if (getObjById(element.getName(), MasterData.class) == null) {
            insertObj(m);
        } else {
            String whereClause = String.format("NAME = '%s'", element.getName());
            updateObj(m, whereClause);
        }
    }


    public void setElement(Element element) {
        Gson gson = new Gson();
        String json = gson.toJson(element);

        MasterData m = new MasterData();
        m.setName(element.getName());
        m.setJson(json);

        if (getObjById(element.getName(), MasterData.class) == null) {
            insertObj(m);
        } else {
            String whereClause = String.format("NAME = '%s'", element.getName());
            updateObj(m, whereClause);
        }
    }


    public void setFormElement(FormElement formElement) {

        Element element = formElement;
        Gson gson = new Gson();
        String json = gson.toJson(element);
        String jsonForm = gson.toJson(formElement);

        MasterData m = new MasterData();
        m.setName(element.getName());
        m.setJson(json);
        m.setJsonForm(jsonForm);

        if (getObjById(element.getName(), MasterData.class) == null) {
            insertObj(m);
        } else {
            String whereClause = String.format("NAME = '%s'", element.getName());
            updateObj(m, whereClause);
        }
    }


    public Element getElement(String name) {
        MasterData m = (MasterData) getObjById(name, MasterData.class);
        Gson gson = new Gson();
        Element element = gson.fromJson(m.getJson(), Element.class);
        return element;
    }


    public FormElement getFormElement(String name) {

        DictionaryDao dao = new DictionaryDao(getContext());
        DicParser dic = dao.getDictionary(name);

        if (dic == null || dic.getFormJson() == null)
            throw new IllegalArgumentException("Element name não foi encontrado");


        return dic.getFormElement();

    }

    public DataTable convertResponseRetField(Hashtable retField, FormElement formElement){
        DataTable dataTable = new DataTable();

        FieldManager fieldManager = new FieldManager();

        for (Object key : retField.keySet()) {
            for(FormElementField formElementField: formElement.getFormFields()){
                DataItem item = new DataItem();

                item.setValue(fieldManager.formatVal(retField.get(formElementField.getFieldname().toLowerCase()), formElementField));
                dataTable.addNew(item);
            }
        }

        return dataTable;
    }

    public DataTable convertHastableInDataTable(Hashtable hashtable, String elementName) {
        FormElement formElement = getFormElement(elementName);
        return convertHastableInDataTable(hashtable, formElement);
    }

    public DataTable convertHastableInDataTable(Hashtable hashtable, FormElement formElement){
        FieldManager fieldManager = new FieldManager();
        DataTable dataTable = new DataTable();

        for(FormElementField formElementField: formElement.getFormFields()){
            DataItem item = new DataItem();

            item.setValue(fieldManager.formatVal(hashtable.get(formElementField.getFieldname()), formElementField));
            dataTable.addNew(item);
        }

        return dataTable;
    }

    public Hashtable convertDataTableInHastable(DataTable dataTable, FormElement formElement){
        Hashtable hashtable = new Hashtable();

        int index = 0;
        for(FormElementField formElementField: formElement.getFormFields()){
            LogUser.log(Config.TAG, formElementField.getFieldname());

            if(dataTable  != null && dataTable.getDataItens().get(index).getValue() != null){
                hashtable.put(formElementField.getFieldname(), dataTable.getDataItens().get(index).getValue().toString());
            }

            index++;
        }

        return hashtable;
    }

    public ArrayList<DataTable> convertResponseRetFields(RetFields retField, FormElement formElement){
        ArrayList<DataTable> arrayDataTable = new ArrayList<>();

        FieldManager fieldManager = new FieldManager();

        for (HashMap dataTableItem : retField.getFields()) {
            DataTable dataTable = new DataTable();

            for (FormElementField formElementField : formElement.getFormFields()) {
                DataItem item = new DataItem();
                item.setValue(fieldManager.formatVal(dataTableItem.get(formElementField.getFieldname().toLowerCase()), formElementField));
                dataTable.addNew(item);
            }

            arrayDataTable.add(dataTable);
        }

        return arrayDataTable;
    }


    public String getValueFieldName(DataTable dataTable, String fieldName, FormElement formElement){
        String value = "";

        if (dataTable != null && dataTable.getDataItens() != null) {
            for (int ind = 0; ind < dataTable.getDataItens().size(); ind++) {
                DataItem dataTableItem = dataTable.getDataItens().get(ind);
                FormElementField form = formElement.getFormFields().get(ind);

                if(form.getFieldname().equals(fieldName)){
                    value = dataTableItem.getValue().toString();
                    ind = dataTable.getDataItens().size();
                }
            }
        }

        return value;
    }

    @SuppressLint("Range")
    public ArrayList<DataTable> getDataTable(Element element, Hashtable filters, String orderby, boolean fastSearch, int regporpag, int pag, int tot) {
        ArrayList<DataTable> dataTable = new ArrayList<>();

        ArrayList<ElementField> fields = new ArrayList<>();

        for (ElementField item : element.getFields()) {
            if (item.getDatabehavior().getValue() >= TBehavior.REAL.getValue()) {
                fields.add(item);
            }
        }

        StringBuilder sSql = new StringBuilder();
        DataAccessCommand cmd = new DataAccessCommand();
        cmd.setCmdType(CommandType.Text);

        int size = filters == null ? 0:filters.size();

        cmd.parameters = new String[size];

        sSql.append("SELECT * FROM ");
        sSql.append(element.getTableName());
        sSql.append(" WHERE (COD_REG_FUNC IS NULL  OR COD_REG_FUNC = '" + JJSDK.getCodUser(getContext()) + "') ");

        int index = 0;

        if(filters != null) {

            sSql.append(" AND ( ");

            for (Object o : filters.entrySet()) {
                Map.Entry entry = (Map.Entry) o;

                for (ElementField item : element.getFields()) {
                    if (item.getFieldname().equals(entry.getKey().toString())) {

                        if (fastSearch) {
                            sSql.append(entry.getKey().toString() + " LIKE ?");
                            String param = "%" + entry.getValue().toString() + "%";
                            cmd.parameters[index] = param;
                        } else {
                            if (item.getElementFilter().getType() == TFilter.CONTAIN) {
                                sSql.append(entry.getKey().toString() + " LIKE ?");
                                String param = "%" + entry.getValue().toString() + "%";
                                cmd.parameters[index] = param;
                                break;
                            } else {
                                sSql.append(entry.getKey().toString());
                                sSql.append(" = ");
                                sSql.append("?");

                                String param = entry.getValue().toString();
                                cmd.parameters[index] = param;
                                break;
                            }
                        }
                    }
                }

                sSql.append(" COLLATE NOCASE ");


                index++;

                if(index < filters.size()){
                    if (fastSearch) {
                        sSql.append(" OR ");
                    } else {
                        sSql.append(" AND ");
                    }
                }
            }

            sSql.append(" ) ");

        }

        if (!TextUtils.isNullOrEmpty(orderby)) {
            sSql.append(" ORDER BY ");
            sSql.append(orderby);
        }

        if (tot == 0 && regporpag > 0) {
            int offset = (pag - 1) * regporpag;
            sSql.append(" LIMIT ");
            sSql.append(regporpag);
            sSql.append(" OFFSET ");
            sSql.append(offset);
        }

        cmd.setSql(sSql.toString());
        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(cmd.getSql(), cmd.parameters)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                DataTable dataTableItem = null;
                dataTableItem = new DataTable();

                for (ElementField item: fields) {
                    DataItem dataItem = new DataItem();

                    switch (item.getDatatype()){
                        case INT:
                            dataItem.setValue(cursor.getInt(cursor.getColumnIndex(item.getFieldname())));
                            break;
                        case FLOAT:
                            dataItem.setValue(cursor.getFloat(cursor.getColumnIndex(item.getFieldname())));
                            break;
                        default:
                            dataItem.setValue(cursor.getString(cursor.getColumnIndex(item.getFieldname())));
                            break;
                    }

                    dataItem.setDataType(item.getDatatype());
                    dataTableItem.addNew(dataItem);
                }

                dataTable.add(dataTableItem);
                cursor.moveToNext();

            }
        } catch (Exception e) {
            LogUser.log(e.toString());
        } finally {
            database.close();
        }
        return dataTable;
    }

    public void insert(Element element, Hashtable valuesForm, Hashtable responseServer) {
        try {

            Hashtable values = new Hashtable();

            if(responseServer != null && responseServer.size() > 0){
                for(ElementField elementField:element.getFields()){
                    String fieldName = elementField.getFieldname().toUpperCase();
                    String fieldNameLowerCase = fieldName.toLowerCase();

                    if(responseServer.get(fieldName.toLowerCase()) != null){
                        if(elementField.getDatatype() == TField.INT){
                            String value = responseServer.get(fieldNameLowerCase).toString();
                            int valueInt = (int) Float.parseFloat(value);
                            values.put(fieldName, String.valueOf(valueInt));
                        } else {
                            values.put(fieldName, responseServer.get(fieldName.toLowerCase()));
                        }
                    } else {
                        if(valuesForm.get(fieldName) != null){
                            values.put(fieldName, valuesForm.get(fieldName));
                        }
                    }
                }
            } else {
                values.putAll(valuesForm);
            }

            SQLiteDatabase db = getDb();
            DataAccess dataAccess = new DataAccess();
            dataAccess.persistDataElement(db, element, values, true);
            db.close();
        } catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    public void delete(Element element, HashMap<String, String> filters) throws Exception{
        SQLiteDatabase db = getDb();
        DataAccess dataAccess = new DataAccess();
        dataAccess.deleteDataElement(db, element, filters);
    }

    public ArrayList<SpinnerDataItem>  getDataCombo(String sql){
        ArrayList<SpinnerDataItem> spinnerArray = new ArrayList<>();

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sql, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                SpinnerDataItem spinnerDataItem = new SpinnerDataItem();
                spinnerDataItem.setValue(cursor.getString(0));
                spinnerDataItem.setName(cursor.getString(1));

                try{
                    spinnerDataItem.setIcon(cursor.getInt(2));
                    spinnerDataItem.setColorIcon(cursor.getString(3));
                }catch (Exception ex){
                    LogUser.log(Config.TAG, ex.toString());
                }

                spinnerArray.add(spinnerDataItem);
                cursor.moveToNext();

            }

            database.close();
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        return spinnerArray;
    }

    @SuppressLint("Range")
    public int getFirstRowAutoNum(String elementName, String elementField){
        SQLiteDatabase db = getDb();

        StringBuilder sSql = new StringBuilder();
        sSql.append("SELECT ");
        sSql.append(elementField + " FROM ");
        sSql.append(elementName + " ");
        sSql.append("ORDER BY " + elementField + " ASC limit 1");

        Cursor c = db.rawQuery(sSql.toString(), null);

        if (c != null && c.moveToFirst()) {
            return c.getInt(c.getColumnIndex(elementField
            ));
        } else {
            return 0;
        }
    }
}
