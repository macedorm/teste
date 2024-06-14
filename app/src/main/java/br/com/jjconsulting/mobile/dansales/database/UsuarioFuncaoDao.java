package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.model.UsuarioFuncao;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class UsuarioFuncaoDao  extends BaseDansalesDao{

    public UsuarioFuncaoDao(Context context) {
        super(context);
    }

    public UsuarioFuncao get(String codigo) {
        String whereClause = "where COD_FUNCAO_PGV = ? AND DEL_FLAG <> '1'";
        String[] whereArgs = { codigo };
        ArrayList<UsuarioFuncao> usuarios = query(whereClause, whereArgs, null);

        if (usuarios.isEmpty()) {
            return null;
        }

        UsuarioFuncao funcao = usuarios.get(0);
        return funcao;
    }

    private ArrayList<UsuarioFuncao> query(String whereClause, String[] args, String orderBy) {
        ArrayList<UsuarioFuncao> funcs = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("select * ");
        sql.append("from TB_DEFUNCAO ");

        if (whereClause != null) {
            sql.append(whereClause);
        }

        if (orderBy != null) {
            sql.append(" ");
            sql.append(orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(sql.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                funcs.add(new UsuarioFuncaoDao.FuncaoCursorWrapper(cursor).getFuncao());
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return funcs;
    }

    public class FuncaoCursorWrapper extends CursorWrapper {

        public FuncaoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public UsuarioFuncao getFuncao() {
            UsuarioFuncao usuario = new UsuarioFuncao();
            usuario.setCodFuncao(getString(getColumnIndex("COD_FUNCAO_PGV")));
            usuario.setFlagPgv(getString(getColumnIndex("FLAG_FV")));
            usuario.setNome(getString(getColumnIndex("NOME")));
            usuario.setPosicao(getDouble(getColumnIndex("POSICAO")));
            usuario.setSigla(getString(getColumnIndex("SIGLA")));

            return usuario;
        }
    }
}
