package br.com.jjconsulting.mobile.dansales.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.model.LayoutUserSync;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class LayoutDao extends BaseDansalesDao{

    public LayoutDao(Context context) {
        super(context);
    }

    /**
     * Armazena layout escolhido pelo usuario
     * @param codigoPlanogram
     * @param codigoPesquisa
     * @param codigoCliente
     * @param codigoUsuario
     * @param freq
     */
    public void insertLayout(String codigoPlanogram, int codigoPesquisa,  String codigoCliente, String codigoUsuario, String freq) {
        SQLiteDatabase database = getDb();

        try {
            ContentValues contentValues = getContentInsertLayout(codigoPlanogram, codigoPesquisa, codigoCliente, codigoUsuario, freq);
            database.insertWithOnConflict("TB_PESQUISA_LAYOUT", null, contentValues,
                    SQLiteDatabase.CONFLICT_REPLACE);
        } finally {
            database.close();
        }
    }

    public void deleteLayout(int codigoPesquisa,  String codigoCliente, String codigoUsuario, String freq) {
        SQLiteDatabase database = getDb();
        try {
            String whereClause = " LAY_INT_PESQUISA_ID = ? AND LAY_TXT_CLIENTE = ?" +
                    " AND LAY_INT_COD_REG_FUNC = ? AND LAY_TXT_DTFREQ = ? ";
            database.delete("TB_PESQUISA_LAYOUT", whereClause, new String[]{
                    String.valueOf(codigoPesquisa), codigoCliente, codigoUsuario, freq});
        }finally {
            database.close();
        }
    }


    /**
     * Retorna layout escolhido pelo usuário para um checklist
     */
    public Layout getLayoutUser(int codigoPesquisa, String codigoUsuario, String codigoCliente, String freq) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT sort.PLANOGRAM_CODE, ");
        query.append(" sort.PLANOGRAM_NAME,");
        query.append(" sort.GROUP_DESCRIPTION,");
        query.append(" SUBSTR( PLANOGRAM_NAME,1, INSTR(PLANOGRAM_NAME , ' - ' )) as REGIAO, ");
        query.append(" sort.START_DATE ");
        query.append("FROM TB_SORT_LAYOUT as sort ");
        query.append(" INNER JOIN TB_PESQUISA_LAYOUT AS lay ");
        query.append(" ON sort.PLANOGRAM_CODE = lay.LAY_TXT_PLANOGRAM_CODE ");
        query.append("WHERE ");
        query.append(" lay.LAY_INT_PESQUISA_ID = ?  ");
        query.append(" AND lay.LAY_INT_COD_REG_FUNC = ?  ");
        query.append(" AND lay.LAY_TXT_CLIENTE = ?  ");
        query.append(" AND lay.LAY_TXT_DTFREQ = ?  ");
        query.append(" AND lay.DEL_FLAG <> '1'  ");
        query.append(" AND sort.DEL_FLAG <> '1'  ");

        String[] args = { String.valueOf(codigoPesquisa), codigoUsuario,  codigoCliente, freq};

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                Layout layout = new LayoutDao.LayoutTaskWrapper(cursor).getLayout();
                return layout;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        } finally {
            database.close();
        }

        return null;

    }

    /**
     * Verifica se existe um layout escolhido pelo usuário para um checklist
     */
    public boolean isExistsLayoutUser(LayoutUserSync layoutUserSync) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * ");
        query.append("FROM TB_PESQUISA_LAYOUT ");
        query.append("WHERE ");
        query.append(" LAY_TXT_PLANOGRAM_CODE = ?  ");
        query.append(" AND LAY_INT_PESQUISA_ID = ?  ");
        query.append(" AND LAY_INT_COD_REG_FUNC = ?  ");
        query.append(" AND LAY_TXT_CLIENTE = ?  ");
        query.append(" AND LAY_TXT_DTFREQ = ?  ");
        query.append(" AND DEL_FLAG <> '1'  ");

        String[] args = {layoutUserSync.getLay_txt_planogram_code(),layoutUserSync.getLay_int_pesquisa_id() + "" ,
                layoutUserSync.getCod_reg_func() + "",  layoutUserSync.getLay_txt_cliente(), layoutUserSync.getLay_txt_dtfreq()};

        boolean isExists = false;

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                isExists = true;
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        } finally {
            database.close();
        }

        return isExists;

    }

    /**
     * Verifica se layout escolhido pelo usuário (checklist) ainda está disponivel
     */
    public boolean isExistsLayout(String unidadeNegocio, String planogramCode) {
        LayoutFilter filter = new LayoutFilter();
        filter.setCodigo(planogramCode);

        List<Layout> list = getListLayout(unidadeNegocio, null, filter);

        if(list != null && list.size() > 0){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Recupera lista de layouts disponíveis.
     */
    public List<Layout> getListLayout(String unidadeNegocio, String nome, LayoutFilter layoutFilter) {

        List<String> args = new ArrayList<>();
        args.add(unidadeNegocio);

        StringBuilder query = new StringBuilder();

        query.append("SELECT PLANOGRAM_CODE, ");
        query.append(" PLANOGRAM_NAME,");
        query.append(" GROUP_DESCRIPTION,");
        query.append(" DEL_FLAG,");
        query.append(" SUBSTR( PLANOGRAM_NAME,1, INSTR(PLANOGRAM_NAME , ' - ' )) AS REGIAO, ");
        query.append(" START_DATE ");
        query.append(" FROM ( ");
        query.append("  SELECT PLANOGRAM_CODE, GROUP_DESCRIPTION, DEL_FLAG, (SELECT PLANOGRAM_NAME FROM TB_SORT_LAYOUT WHERE TB_SORT_LAYOUT.PLANOGRAM_CODE =A.PLANOGRAM_CODE LIMIT 1) PLANOGRAM_NAME, ");
        query.append("     MAX(START_DATE) START_DATE FROM TB_SORT_LAYOUT A ");
        query.append(" WHERE ");
        query.append(" SALES_ORGANIZATION = ? ");
        query.append(" AND DEL_FLAG <> '1' ");



        if (!TextUtils.isNullOrEmpty(nome)) {
            query.append(" AND (PLANOGRAM_NAME LIKE ? OR PLANOGRAM_CODE LIKE ? )" );
            args.add("%" + nome + "%");
            args.add("%" + nome + "%");
        }

        if(layoutFilter != null){
            if(layoutFilter.getDateStart() != null){
                query.append("AND START_DATE = ?  " );
                args.add(FormatUtils.toTextToCompareDateInSQlite(layoutFilter.getDateStart()));
            }

            if(!TextUtils.isNullOrEmpty(layoutFilter.getCodigo())){
                query.append("  AND PLANOGRAM_CODE = ?  ");
                args.add(layoutFilter.getCodigo());
            }

            if(!TextUtils.isNullOrEmpty(layoutFilter.getFilter())){
                query.append("  AND GROUP_DESCRIPTION = ?  ");
                args.add(layoutFilter.getFilter());
            }
        }

        query.append("  AND EXISTS(SELECT * FROM TB_SORT_SORTIMENTO B WHERE A.PLANOGRAM_CODE = B.PLANOGRAM_CODE AND B.DEL_FLAG <> '1' ");
        query.append("  AND A.SALES_ORGANIZATION = B.SALES_ORGANIZATION ) ");
        query.append("  GROUP BY PLANOGRAM_CODE ) order by 3 ");

        SQLiteDatabase database = getDb();

        List<Layout> layoutList = new ArrayList<>();

        try (Cursor cursor = database.rawQuery(query.toString(),  args.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Layout layout = new LayoutDao.LayoutTaskWrapper(cursor).getLayout();
                layoutList.add(layout);
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        } finally {
            database.close();
        }


        return layoutList;
    }

    /**
     * Retorna layout de um cliente
     */
    public Layout getLayout(String unidadeNegocio, String codigoCliente,
                            Date dataReferencia) {
        String date = FormatUtils.toTextToCompareDateInSQlite(dataReferencia);

        String query = "select sl.planogram_code, sl.planogram_name, sl.start_date, sl.GROUP_DESCRIPTION, "  +
                "substr (planogram_name, 1, instr(planogram_name, ' - ')) as regiao " +
                " from tb_sort_layout sl" +
                " inner join tb_sort_cliente sc" +
                " on sc.planogram_code = sl.planogram_code" +
                " and sc.sales_organization = sl.sales_organization" +
                " inner join tb_sort_sortimento ss" +
                " on sc.planogram_code = ss.planogram_code" +
                " and sc.sales_organization = ss.sales_organization" +
                " where sl.sales_organization = ?" +
                " and sc.customer_code = (" +
                "   select c.NUM_CNPF_CPF from tb_decliente c" +
                "   where c.COD_EMITENTE = ?" +
                " )" +
                " and datetime(sl.start_date) <= '" + date + "'" +
                " and datetime(ss.start_date) <= '" + date + "'" +
                " and datetime(sc.start_date) <= '" + date + "'" +
                " and sc.DEL_FLAG <> '1'" +
                " and sl.DEL_FLAG <> '1'" +
                " order by sl.start_date desc, sc.start_date desc, sl.planogram_code desc " +
                " limit 1";

        String[] args = { unidadeNegocio,  codigoCliente };

        SQLiteDatabase database = getDb();

        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                return new LayoutDao.LayoutTaskWrapper(cursor).getLayout();

            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return null;
    }

    /**
     * Retorna codigo do layout usado em um cliente (Planogram)
     */
    public String getLayouCode(String unidadeNegocio, String codigoCliente,
                               Date dataReferencia) {

        Layout layout = getLayout(unidadeNegocio, codigoCliente, dataReferencia);

        if(layout == null ){
            return null;
        } else {
            return layout.getCodigo();
        }
    }

    /**
     * Retorna codigo do layout usado em um cliente (Planogram Name)
     */
    public String getLayoutName(String unidadeNegocio, String codigoCliente) {

        Layout layout = getLayout(unidadeNegocio, codigoCliente, new Date());

        if(layout == null ){
            return null;
        } else {
            return layout.getNome();
        }
    }

    private static ContentValues getContentInsertLayout(String codigoPlanogram, int codigoPesquisa,  String codigoCliente, String codigoUsuario, String freq) {
        ContentValues values = new ContentValues();
        values.put("LAY_TXT_PLANOGRAM_CODE", codigoPlanogram);
        values.put("LAY_INT_PESQUISA_ID", codigoPesquisa);
        values.put("LAY_TXT_CLIENTE", codigoCliente);
        values.put("LAY_INT_COD_REG_FUNC", codigoUsuario);
        values.put("LAY_TXT_DTFREQ",  freq);
        values.put("DEL_FLAG", "0");

        return values;
    }

    public class LayoutTaskWrapper extends CursorWrapper {

        public LayoutTaskWrapper(Cursor cursor) {
            super(cursor);
        }

        public Layout getLayout() {
            Layout layout = new Layout();
            layout.setCodigo(getString(getColumnIndex("PLANOGRAM_CODE")));
            layout.setNome(getString(getColumnIndex("PLANOGRAM_NAME")));
            layout.setData(getString(getColumnIndex("START_DATE")));
            layout.setGrupo(getString(getColumnIndex("GROUP_DESCRIPTION")));

            if (getColumnIndex("REGIAO") != -1) {
                layout.setRegiao(getString(getColumnIndex("REGIAO")));
            }

            return layout;
        }
    }
}
