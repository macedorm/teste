package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class OrganizacaoDao extends BaseDansalesDao {

    public OrganizacaoDao(Context context) {
        super(context);
    }

    public Organizacao get(String codigoBandeira) {
        String whereClause = "where org.COD_EMITENTE = ?"
                + " and org.DEL_FLAG = '0' ";
        String[] whereArgs = {codigoBandeira};
        ArrayList<Organizacao> orgs = query(whereClause, whereArgs, null);
        return orgs.isEmpty() ? null : orgs.get(0);
    }

    public ArrayList<Organizacao> getAll(String codUser, String codUnNeg) {

        StringBuilder sWhere = new StringBuilder();

        sWhere.append("where ");
        sWhere.append("exists ( ");
        sWhere.append(" select * ");
        sWhere.append(" from TB_DECLIENTEUN cun ");
        sWhere.append(" inner join TB_DECLIUNREG cur");
        sWhere.append("  on  cun.COD_EMITENTE = cur.COD_EMITENTE");
        sWhere.append("  and cun.COD_UNID_NEGOC = cur.COD_UNID_NEGOC");
        sWhere.append("  and cur.DEL_FLAG <> '1' ");
        sWhere.append("  and cur.COD_REG_FUNC = ? ");
        sWhere.append(" inner join TB_DECLIENTE cli");
        sWhere.append("  on  cli.COD_EMITENTE = cun.COD_EMITENTE");
        sWhere.append("  and cli.COD_ORGANIZACAO = org.COD_EMITENTE ");
        sWhere.append("  and cli.DEL_FLAG <> '1'");
        sWhere.append(" where cun.DEL_FLAG <> '1'");
        sWhere.append(" and cun.INATIVO <> '1'");
        sWhere.append(" and cun.COD_UNID_NEGOC = ?) ");

        String orderBy = "ORDER BY org.DESCRICAO";
        String[] whereArgs = {codUser, codUnNeg};
        return query(sWhere.toString(), whereArgs, orderBy);
    }

    private ArrayList<Organizacao> query(String whereClause, String[] args, String orderBy) {
        ArrayList<Organizacao> orgs = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT");
        query.append(" org.COD_EMITENTE, ");
        query.append(" org.DESCRICAO ");
        query.append("FROM TB_DEORGANIZA org ");

        if (whereClause != null) {
            query.append(whereClause);
        }

        if (orderBy != null) {
            query.append(" ");
            query.append(orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                orgs.add(new OrganizacaoCursorWrapper(cursor).getOrganizacao());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return orgs;
    }

    public class OrganizacaoCursorWrapper extends CursorWrapper {

        public OrganizacaoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Organizacao getOrganizacao() {
            Organizacao org = new Organizacao();
            org.setCodigo(getString(getColumnIndex("COD_EMITENTE")));
            org.setNome(getString(getColumnIndex("DESCRICAO")));

            return org;
        }
    }
}
