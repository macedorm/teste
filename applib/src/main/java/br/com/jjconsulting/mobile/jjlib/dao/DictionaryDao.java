package br.com.jjconsulting.mobile.jjlib.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.jjlib.dao.entity.Dictionary.DicParser;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Element;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementInfo;
import br.com.jjconsulting.mobile.jjlib.dao.entity.JsonFormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.UIOptions;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterData;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class DictionaryDao extends BaseDao {


    public DictionaryDao(Context context) {
        super(context);
    }

    public DicParser getDictionary(String id){
        try {

            MasterData m = (MasterData) getObjById(id, MasterData.class);
            Gson gson = new Gson();
            Element element = gson.fromJson(m.getJson(), Element.class);
            JsonFormElement formJson = gson.fromJson(m.getJsonForm(), JsonFormElement.class);
            UIOptions uiOptions = gson.fromJson(m.getJsonUIOptions(), UIOptions.class);

            DicParser dicParser = new DicParser();
            dicParser.setTable(element);
            dicParser.setFormJson(formJson);
            dicParser.setUIOptions(uiOptions);
            return dicParser;
        }catch (Exception ex){
            return null;
        }

    }

    public void setDictionary(SQLiteDatabase db, JsonFormElement syncDictionaries[]){
        Gson gson = new Gson();
        StringBuilder sqlQ = new StringBuilder();
        sqlQ.append("INSERT OR REPLACE INTO TB_MASTERDATA ");
        sqlQ.append("(NAME, ELEMENTNAME, JSON, JSONFORM, JSONUIOPTION, ORDER_SYNC, MODE) ");
        sqlQ.append("VALUES (?, ?, ?, ?, ?, ?, ?) ");
        String sql = sqlQ.toString();

        db.beginTransaction();
        try {
            for (JsonFormElement e : syncDictionaries) {
                String json = gson.toJson(e);
                String jsonForm  = gson.toJson(e.getFormfields());

                //TODO: Ajustar json no sincronismo
                // String jsonUIOptions = null;
                /*if (e.getOptions() != null)
                    jsonUIOptions = gson.toJson(e.getOptions());*/

                Object[] args = {e.getTableName(), e.getName(), json, jsonForm, null, "0", e.getMode()};

                db.execSQL(sql, args);
            }
            db.setTransactionSuccessful();
        }catch (Exception ex) {
            throw ex;
        }finally {
            db.endTransaction();
        }
    }


}

