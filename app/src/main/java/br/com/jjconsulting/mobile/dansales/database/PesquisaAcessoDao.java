package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import br.com.jjconsulting.mobile.dansales.model.PesquisaAcesso;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaAcessoDao  extends BaseDansalesDao{

    public PesquisaAcessoDao(Context context) {
        super(context);
    }

    public PesquisaAcesso getAcesso(int codigoPesquisa) {
        PesquisaAcesso acesso = new PesquisaAcesso();

        StringBuilder query = new StringBuilder();
        query.append(" select * ");
        query.append(" from TB_PESQUISA_ACESSO ");
        query.append(" where DEL_FLAG = '0' ");
        query.append(" and ID_PESQUISA = ");
        query.append(codigoPesquisa);

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                 int idFiltro = cursor.getInt(cursor.getColumnIndex("ID_FILTRO"));
                 String value = cursor.getString(cursor.getColumnIndex("ID_FUNCAO"));
                 acesso.addFilter(idFiltro, value);
                cursor.moveToNext();
            }
            acesso.setPesquisaId(codigoPesquisa);
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return acesso;
    }

    public boolean hasAccess(Usuario user, int codigoPesquisa, String codigoUnidadeNegocio) {
        return hasAccess(user, codigoPesquisa, codigoUnidadeNegocio, null);
    }

    public boolean hasAccess(Usuario user, int codigoPesquisa, String codigoUnidadeNegocio, String codigoCliente){
        PesquisaAcesso acesso = getAcesso(codigoPesquisa);

        //acesso por usuário
        if (acesso.getListUser().size() > 0) {
            if (acesso.getListUser().contains(user.getCodigo()))
                return true;
        }

        //acesso por função
        if (acesso.getListFuncao().size() > 0) {
            if (acesso.getListFuncao().contains(user.getCodigoFuncao()))
                return true;
        }

        //acesso por cliente
        if (acesso.hasClienteFilter()) {
            if (hasClienteAccess(acesso, user, codigoUnidadeNegocio, codigoCliente))
                return true;
        }

        return false;
    }

    private boolean hasClienteAccess(PesquisaAcesso acesso, Usuario user, String codigoUnidadeNegocio, String codigoCliente) {
        int count = 0;

        try {
            StringBuilder query = new StringBuilder();
            query.append(" select count(*) as qtd");
            query.append(" from TB_DECLIUNREG cur");
            query.append(" inner join TB_DECLIENTEUN cun");
            query.append("   on cun.COD_EMITENTE = cur.COD_EMITENTE");
            query.append("   and cun.COD_UNID_NEGOC = cur.COD_UNID_NEGOC");
            query.append("   and cun.DEL_FLAG = '0'");
            query.append("   and cun.INATIVO <> '1'");
            query.append(" inner join TB_DECLIENTE cli");
            query.append("   on cli.COD_EMITENTE = cun.COD_EMITENTE");
            query.append("   and cli.DEL_FLAG = '0'");
            query.append(" where cur.DEL_FLAG = '0' ");
            query.append(" and (cur.SEQUENCIA = '000' OR SEQUENCIA >= '100') ");
            query.append(" and cur.COD_UNID_NEGOC = '" + codigoUnidadeNegocio + "'");

            //Se for promotor filtramos o usuário, caso contrário compara com todos para evitar a chamada da hierarquia
            if (user.getCodigoFuncao().equals("46")) {
                query.append(" and cur.COD_REG_FUNC = '" + user.getCodigo() + "'");
            }

            if (!TextUtils.isNullOrEmpty(codigoCliente)) {
                query.append(" and cli.COD_EMITENTE = '");
                query.append(codigoCliente);
                query.append("'");
            }

            //aplica acesso por cliente
            query.append(getClienteFilter(acesso));

            SQLiteDatabase db = getDb();
            Cursor mCount = db.rawQuery(query.toString(), null);
            mCount.moveToFirst();
            count = mCount.getInt(0);
            mCount.close();

        }catch (Exception ex){
            LogUser.log(ex.toString());
        }

        return count > 0;


    }


    public String getClienteFilter(int codigoPesquisa) {
        PesquisaAcesso acesso = getAcesso(codigoPesquisa);
        return getClienteFilter(acesso);
    }


    public String getClienteFilter(PesquisaAcesso acesso) {

        StringBuilder query = new StringBuilder();
        query.append(" and ( 1=2 ");
        query.append(parseFilter("cli.COD_ORGANIZACAO", acesso.getListOrg()));
        query.append(parseFilter("cli.COD_ESTADO", acesso.getListUF()));
        query.append(parseFilter("cun.COD_REGIONAL", acesso.getListRegional()));
        query.append(parseFilter("cli.COD_BANDEIRA", acesso.getListBandeira()));
        query.append(parseFilter("cli.COD_CIDADE", acesso.getListCidade()));
        query.append(parseFilter("cli.COD_EMITENTE", acesso.getListCliente()));
        query.append(parseFilter("cun.COD_CANAL_VENDA", acesso.getListCanal()));
        query.append(" ) ");

        return query.toString();
    }


    private String parseFilter(String field, List<String> values) {
        if (values.size() == 0)
            return "";

        StringBuilder filter = new StringBuilder();
        filter.append(" or ");
        filter.append(field);

        if (values.size() == 1) {
            filter.append(" = '");
            filter.append(values.get(0));
            filter.append("' ");
        } else {
            filter.append(" in (");
            for(int i =0;i<values.size();i++) {
                if (i > 0)
                    filter.append(",");

                filter.append("'");
                filter.append(values.get(i));
                filter.append("'");
            }
            filter.append(")");
        }

        return filter.toString();
    }

}



