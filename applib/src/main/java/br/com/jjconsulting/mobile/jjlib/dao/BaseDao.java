package br.com.jjconsulting.mobile.jjlib.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.List;

import br.com.jjconsulting.mobile.jjlib.BuildConfig;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Element;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;

public class BaseDao extends DataAccess {

    private Context context;
    private WebSalesDatabaseHelper databaseHelper;

    public BaseDao(Context context) {
        // MUST use application context to avoid memory leaks.
        this.context = context.getApplicationContext();
        databaseHelper = WebSalesDatabaseHelper.getInstance(context, JJSDK.getDbVersion(context), JJSDK.getDbName(context));
    }

    public Context getContext(){
        return this.context;
    }
    /**
     * Retorna o banco de dados preparado para executar comandos
     */
    public SQLiteDatabase getDb() {
        databaseHelper = WebSalesDatabaseHelper.getInstance(context, JJSDK.getDbVersion(context), JJSDK.getDbName(context));

        try {
           return databaseHelper.getReadableDatabase();
            // Perform database operations
        } catch (SQLiteException e) {
            // Handle exception
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Apaga todos os registros da tabela
     * @param obj Tipo dp objeto
     */
    public void truncateTable(Class<? extends Object> obj) {
        SQLiteDatabase db = getDb();
        try{
            db.execSQL(getScriptTruncateTable(obj));
        }finally {
            db.close();
        }
    }

    /**
     * Recupera um objeto do banco de dados
     * @param id Código (PK)
     * @param classobj Tipo dp objeto
     */
    public Object getObjById(Object id, Class<? extends Object> classobj) {
        SQLiteDatabase db = getDb();
        Object obj;

        try{
            obj = super.getObjById(db, id, classobj);
        } finally {
            db.close();
        }

        return obj;
    }

    /**
     * Recupera um objeto preenchido do banco de dados
     * @param id Código (PK)
     * @param classobj Tipo dp objeto
     */
    public Object getContentObjById(Object id, Class<? extends Object> classobj) {
        SQLiteDatabase db = getDb();

        Object obj;

        try{
            obj = super.getContentObjById(db, id, classobj);
        } finally {
            db.close();
        }

        return obj;
    }

    /**
     * Inclui um objeto no banco de dados
     * @param bean Objeto Bean
     */
    public void insertObj(Object bean) {
        SQLiteDatabase db = getDb();

        try{
            super.insertObj(db, bean);
        }finally {
            db.close();
        }
    }

    /**
     * Atualiza uma lista de objetos no banco de dados
     * @param listBean Lista de Objetos Bean
     */
    public void insertObj(Object[] listBean) {
        if (listBean.length > 0) {
            SQLiteDatabase db = getDb();

            try{
                super.insertObj(db, listBean);
            } finally {
                db.close();
            }
        }
    }

    /**
     * Atualiza um objeto no banco de dados
     * @param bean Objeto Bean
     * @param whereClause Clausula WHERE P-SQL
     */
    public void updateObj(Object bean, String whereClause) {
        SQLiteDatabase db = getDb();

        try{
            super.updateObj(db, bean, whereClause);
        } finally {
            db.close();
        }
    }

    /**
     * Verifica se a tabela existe no banco de dados
     * @param tableName Nome da Tabela
     */
    public boolean tableExist(String tableName) {
        SQLiteDatabase db = getDb();
        boolean bRet;
        try{
            bRet = super.tableExist(db, tableName);
        } finally {
            db.close();
        }
        return  bRet;
    }

    public void createTable(List<Element> tables) {
        SQLiteDatabase db = getDb();

        try{
            createTable(db, tables);
        } finally {
            db.close();
        }
    }

}
