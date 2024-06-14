package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ConsultaGenericaFilter;
import br.com.jjconsulting.mobile.dansales.model.ConsultaGenerica;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ConsultaGenericaDao extends BaseDansalesDao{

    private Context context;

    public ConsultaGenericaDao(Context context) {
        super(context);
        this.context = context;
    }

    public ArrayList<ConsultaGenerica> findAll(String codigoUsuario,
                                               String nome,
                                               ConsultaGenericaFilter filter,
                                               int indexOffSet) {

        ArrayList<ConsultaGenerica> listRequisicao = new ArrayList<>();
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM TB_REQ_GENERICA as req WHERE ");

        List<String> whereArgs = new ArrayList<>();

        if (filter != null) {

            if (filter.getHierarquiaComercial() != null &&
                    filter.getHierarquiaComercial().size() > 0) {

                query.append(" req.RGR_SOLICITANTE in (");
                for (int i = 0; i < filter.getHierarquiaComercial().size(); i++) {
                    Usuario usuario = filter.getHierarquiaComercial().get(i);
                    if (i > 0) {
                        query.append(",");
                    }
                    query.append("'" + usuario.getCodigo() + "'");
                }
                query.append(") ");

            } else {
                query.append(" req.RGR_SOLICITANTE = ? ");
                whereArgs.add(codigoUsuario);
            }

            if(filter.getStatus() > 0){
                query.append(" AND req.RGR_STATUS = ? ");
                whereArgs.add(String.valueOf(filter.getStatus()));
            }

            if(filter.getTipoCadastro() > 0){
                query.append(" AND req.RGR_TIPO_CADASTRO = ? ");
                whereArgs.add(String.valueOf(filter.getTipoCadastro()));
            }

            // filtro data
            if (filter.getDateStart() != null && filter.getDateEnd() != null) {
               try {
                   query.append(" AND DATE(req.RGR_DAT_INCLUSAO) BETWEEN DATE('");
                   query.append(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.convertTextInDatePT(filter.getDateStart())));
                   query.append("') AND DATE('");
                   query.append(FormatUtils.toTextToCompareDateInSQlite(FormatUtils.convertTextInDatePT(filter.getDateEnd())));
                   query.append("')");
               }catch (Exception ex){
                   LogUser.log(ex.getMessage());
               }
            }

        } else {
            query.append(" req.RGR_SOLICITANTE = ? ");
            whereArgs.add(codigoUsuario);
        }


        if (nome != null) {
            query.append(" AND (req.RGR_NOME_CLIENTE LIKE ? OR  req.RGR_ID LIKE ?) ");
            whereArgs.add("%" + nome + "%");
            whereArgs.add("%" + nome + "%");
        }

        query.append(" AND req.DEL_FLAG <> '1' ");

        if (indexOffSet != -1) {
            query.append(" " + "LIMIT " + Config.SIZE_PAGE + " OFFSET " + indexOffSet);
        }

        SQLiteDatabase database = getDb();
            try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ConsultaGenerica requisicao = new RequisicaoCursorWrapper(cursor).get();
                listRequisicao.add(requisicao);
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return setAggregatedData(context, listRequisicao, filter);
    }

    private  ArrayList<ConsultaGenerica> setAggregatedData(Context context, ArrayList<ConsultaGenerica> listRequisicao, ConsultaGenericaFilter filter){
        ArrayList<ConsultaGenerica> listRequisaoUpdate = new ArrayList<>();

        if(listRequisicao == null || listRequisicao.size() == 0)
            return listRequisicao;

        for(ConsultaGenerica consultaGenerica: listRequisicao){
            if(filter != null && filter.getHierarquiaComercial() != null && filter.getHierarquiaComercial().size() > 0){
                for (int i = 0; i < filter.getHierarquiaComercial().size(); i++) {
                    Usuario usuario = filter.getHierarquiaComercial().get(i);
                    if(usuario.getCodigo().equals(consultaGenerica.getSolicitante())){
                        consultaGenerica.setNomeSolicitante(usuario.getNome());
                    }
                }
            } else {
                consultaGenerica.setNomeSolicitante(Current.getInstance(context).getUsuario().getNome());
            }

            MultiValuesDao multiValuesDao = new MultiValuesDao(context);

            MultiValues status = multiValuesDao.getItem(String.valueOf(consultaGenerica.getStatus()));
            consultaGenerica.setDescStatus(status.getDesc());

            MultiValues tipoCadastro = multiValuesDao.getItem(String.valueOf(consultaGenerica.getTipoCadastro()));
            consultaGenerica.setDescTipoCadastro(tipoCadastro.getDesc());

            listRequisaoUpdate.add(consultaGenerica);
        }

        return listRequisaoUpdate;
    }

    public class RequisicaoCursorWrapper extends CursorWrapper {

        public RequisicaoCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public ConsultaGenerica get() {
            ConsultaGenerica obj = new ConsultaGenerica();

            obj.setId(getString(getColumnIndex("RGR_ID")));
            obj.setTipoCadastro(getInt(getColumnIndex("RGR_TIPO_CADASTRO")));
            obj.setNumCad(getInt(getColumnIndex("RGR_NUM_CADASTRO")));
            obj.setNome(getString(getColumnIndex("RGR_NOME_CLIENTE")));
            obj.setCnpj(getString(getColumnIndex("RGR_CNPJ")));
            obj.setStatus(getInt(getColumnIndex("RGR_STATUS")));
            obj.setDataInclusao(getString(getColumnIndex("RGR_DAT_INCLUSAO")));
            obj.setSolicitante(getString(getColumnIndex("RGR_SOLICITANTE")));
            obj.setBandeira(getString(getColumnIndex("RGR_BANDEIRA")));
            obj.setCidade(getString(getColumnIndex("RGR_CIDADE")));
            obj.setUf(getString(getColumnIndex("RGR_UF")));

            return obj;
        }
    }
}
