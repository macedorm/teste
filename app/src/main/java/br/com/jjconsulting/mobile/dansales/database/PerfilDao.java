package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PerfilDao extends BaseDansalesDao{

    private PerfilVendaDao perfilVendaDao;

    public PerfilDao(Context context) {
        super(context);
        perfilVendaDao = new PerfilVendaDao(context);
    }

    public Perfil get(int codigoPerfil) {
        String whereClause = "where per.PRF_INT_COD = " + String.valueOf(codigoPerfil);
        ArrayList<Perfil> perfis = query(whereClause, null, null);
        return perfis.isEmpty() ? null : setAggregatedData(perfis.get(0));
    }

    private Perfil setAggregatedData(Perfil perfil) {
        perfil.setPerfisVenda(perfilVendaDao.getAll(perfil.getCodigo()));

        return perfil;
    }

    private ArrayList<Perfil> query(String whereClause, String[] args, String orderBy) {
        ArrayList<Perfil> perfis = new ArrayList<>();

        String query = "select * from TBPERFILACCESS per";

        if (whereClause != null) {
            query += " " + whereClause;
        }

        if (orderBy != null) {
            query += " " + orderBy;
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query, args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                perfis.add(new PerfilDao.PerfilCursorWrapper(cursor).getPerfil());
                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }finally {
            database.close();
        }

        return perfis;
    }

    public class PerfilCursorWrapper extends CursorWrapper {

        public PerfilCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Perfil getPerfil() {
            Perfil perfil = new Perfil();
            perfil.setCodigo(getInt(getColumnIndex("PRF_INT_COD")));

            perfil.setNome(getString(getColumnIndex("PRF_TXT_NOME")));

            perfil.setPosicaoHierarquia(getInt(getColumnIndex(
                    "PRF_INT_POSICAOHIERARQUIA")));

            perfil.setRelatorioHistoricoNotasHabilitado("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRELHISTORICODENOTAS"))));

            perfil.setRelatorioObjetivoRafHabilitado("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRELOBJETIVO"))));

            perfil.setRelatorioCarteiraPedidosHabilitado("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRELCARTPEDIDO"))));

            perfil.setRelatorioETapSaldoMC("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRELETAPSALDOMC"))));

            perfil.setRelatorioETapLista("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRELETAPLISTA"))));


            perfil.setPermiteModuloETap("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABMODETAP"))));

            perfil.setPermiteEtapInc("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABETAPINC"))));

            perfil.setPermiteEtapAnFin("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABETAPANFIN"))));

            perfil.setPermiteEtapContrInt("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABETAPCONTRINT"))));

            perfil.setPermiteRelPositivacao("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRELPOSITIVACAO"))));

            perfil.setPermiteEtapAprovMassa("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABETAPAPROVMASSA"))));

            perfil.setPermiteRotaGuiada("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABROTADIA_RG"))));

            perfil.setRotaCheckInForaArea("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABCHECKFORAAREA_RG"))));

            perfil.setChatIsaac("1".equals(getString(
                    getColumnIndex(("PRF_TXT_HABISAAC")))));

            perfil.setRotaJutificativaVisitaNaoRealizada("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABJUSTVISITANAOREALI_RG"))));

            perfil.setRotaJutificativaAtividadeNaoRealizada("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABJUSTATIVNREALI_RG"))));

            perfil.setRotaJutificativaPedidoNaoRealizado("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABJUSTPEDNREALI_RG"))));

            perfil .setRotaPermiteCheckinChekout("1".equals(getString(
                    getColumnIndex("PRF_TXT_CHECKIN_CHECKOUT_RG"))));

            perfil.setRotaRaioAderencia(getInt(getColumnIndex(
                    "PRF_INT_CADRAIO_RG")));

            perfil.setPermitePesquisa("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABPESQUISA"))));

            perfil.setPermiteCR("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABCR"))));

            perfil.setPermiteAutoSync("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABAUTOSINC"))));

            perfil.setPermiteRastreioPedido("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRASPED"))));

            perfil.setPermiteRelatorioChecklist("1".equals(getString(
                    getColumnIndex("PRF_TXT_HABRELNOTACHECKLIST"))));

            perfil.setPermiteRequisicao("1".equals(getString(
                        getColumnIndex("PRF_TXT_HABMODREQUISICAO"))));


            try{
                perfil.setPermitePlanejamentoRota("1".equals(getString(
                        getColumnIndex("PRF_TXT_HABPLANEJAMENTOROTA"))));
            }catch (Exception ex){
                perfil.setPermitePlanejamentoRota(false);
            }


            perfil.setIntervaloAutoSync(getLong(
                    getColumnIndex("PRF_INT_AUTOSINC_MINUTOS")));

            return perfil;
        }
    }
}
