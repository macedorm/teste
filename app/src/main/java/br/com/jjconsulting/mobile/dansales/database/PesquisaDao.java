package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.TFreq;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.TJustTask;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaDao extends BaseDansalesDao {

    private Date currentDate;


    public PesquisaDao(Context context) {
        super(context);
    }


    public Pesquisa get(int codigoPesquisa, String codigoUsuario, String cliente, Date date) {
        this.currentDate = date;

        String whereClause = "WHERE DEL_FLAG <> '1' AND PES_INT_ID = " + codigoPesquisa;
        String orderBy = "order by PES_INT_ID ASC";

        ArrayList<Pesquisa> statuses = query(whereClause, null, orderBy, codigoUsuario, cliente);
        return statuses.isEmpty() ? null : statuses.get(0);
    }

    public ArrayList<Pesquisa> hasPesquisaAtiva(String codigoUnidadeNegocio,
                                      Usuario user,
                                      String codigoCliente,
                                      String nome,
                                      String idPesquisa,
                                      Date currentDate) {

        this.currentDate = currentDate;

        String codigoUsuario = user.getCodigo();
        StringBuilder whereClause = new StringBuilder();

        whereClause.append(" where DEL_FLAG <> '1' AND PES_INT_STATUS = '2'");
        whereClause.append(" and PES_INT_ID = ?");
        whereClause.append(" and ('" + FormatUtils.toTextToCompareshortDateInSQlite(currentDate) + "' BETWEEN date(PES_DAT_INICIO) and date(PES_DAT_FIM)) ");
        whereClause.append(" AND (PES_TXT_UNID_NEGOC = ? OR PES_TXT_UNID_NEGOC IS NULL) ");

        List<String> whereArgs = new ArrayList<>();

        whereArgs.add(idPesquisa);
        whereArgs.add(codigoUnidadeNegocio);

        if (nome != null) {
            whereClause.append(" and PES_TXT_NOME LIKE ? ");
            whereArgs.add("%" + nome + "%");
        }

        String orderBy = "order by PES_INT_ID desc";

        PesquisaAcessoDao acessoDao = new PesquisaAcessoDao(getContext());
        ArrayList<Pesquisa> listPesquisa = query(whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, codigoUsuario, codigoCliente);
        ArrayList<Pesquisa> listAccess = new ArrayList<>();
        for (Pesquisa p : listPesquisa) {
            if (acessoDao.hasAccess(user, p.getCodigo(), codigoUnidadeNegocio, codigoCliente)) {
                listAccess.add(p);
            }
        }

        return listAccess;
    }

    public ArrayList<Pesquisa> getAll(String codigoUnidadeNegocio,
                                      Usuario user,
                                      String codigoCliente,
                                      String nome,
                                      Date currentDate,
                                      TTypePesquisa tTypePesquisa) {

        this.currentDate = currentDate;

        String codigoUsuario = user.getCodigo();
        StringBuilder whereClause = new StringBuilder();
        List<String> whereArgs = new ArrayList<>();

        whereClause.append(" where DEL_FLAG <> '1' AND PES_INT_STATUS = '2'");

        switch (tTypePesquisa){
            case ISROTA:
                whereClause.append(" and (PES_INT_TIPO = ? or PES_INT_TIPO = ?)");
                whereArgs.add(String.valueOf(Pesquisa.ATIVIDADE));
                whereArgs.add(String.valueOf(Pesquisa.CHECKLIST_RG));
                break;
            case PESQUISA:
                whereClause.append(" and (PES_INT_TIPO = ? or PES_INT_TIPO = ?)");
                whereArgs.add(String.valueOf(Pesquisa.PESQUISA));
                whereArgs.add(String.valueOf(Pesquisa.CHECKLIST));
                break;
            case COACHING:
                whereClause.append(" and (PES_INT_TIPO = ? )");
                whereArgs.add(String.valueOf(Pesquisa.COACHING));
                break;
        }

        whereClause.append(" and ('" + FormatUtils.toTextToCompareshortDateInSQlite(currentDate) + "' BETWEEN date(PES_DAT_INICIO) and date(PES_DAT_FIM)) ");
        whereClause.append(" AND (PES_TXT_UNID_NEGOC = ? OR PES_TXT_UNID_NEGOC IS NULL) ");

        whereArgs.add(codigoUnidadeNegocio);

        if (nome != null) {
            whereClause.append(" and PES_TXT_NOME LIKE ? ");
            whereArgs.add("%" + nome + "%");
        }

        String orderBy = "order by PES_INT_ID desc";

        PesquisaAcessoDao acessoDao = new PesquisaAcessoDao(getContext());
        ArrayList<Pesquisa> listPesquisa = query(whereClause.toString(), whereArgs.toArray(new String[0]), orderBy, codigoUsuario, codigoCliente);
            ArrayList<Pesquisa> listAccess = new ArrayList<>();
        for (Pesquisa p : listPesquisa) {
            if (acessoDao.hasAccess(user, p.getCodigo(), codigoUnidadeNegocio, codigoCliente)) {
                listAccess.add(p);
            }
        }

        return listAccess;
    }

    private ArrayList<Pesquisa> query(String whereClause,
                                      String[] args,
                                      String orderBy,
                                      String codigoUsuario,
                                      String codigoCliente) {
        ArrayList<Pesquisa> listPesquisa = new ArrayList<>();

        String qyeryCli = "";
        if (!TextUtils.isNullOrEmpty(codigoCliente)){
            qyeryCli = " AND (RES_TXT_CLIENTE IS NULL OR RES_TXT_CLIENTE = '" +  codigoCliente + "')";
        }

        StringBuilder query = new StringBuilder();
        query.append("select PES_INT_ID");
        query.append(" ,PES_TXT_UNID_NEGOC");
        query.append(" ,PES_TXT_EDIT");
        query.append(" ,PES_TXT_NOME");
        query.append(" ,PES_DAT_INICIO");
        query.append(" ,PES_DAT_FIM");
        query.append(" ,PES_INT_STATUS");
        query.append(" ,PES_INT_FRE_ID");
        query.append(" ,PES_TXT_OBRIGATORIO");
        query.append(" ,PES_INT_TIPO");
        query.append(" ,PES_TXT_VISUALIZA_ATIVIDADE");
        query.append(" ,PES_TXT_SELEC_CLI,");
        query.append(" (SELECT count(*) from TB_PESQUISA_RESPOSTA where (DEL_FLAG IS NULL or DEL_FLAG <> '1') AND RES_INT_REG_FUNC = '" + codigoUsuario + "' " + qyeryCli + " AND RES_INT_PESQUISA_ID = PES_INT_ID and REGSYNC = 3 and" + createFilterFreq() + ") as RES_SEND, ");
        query.append(" (SELECT count(*) from TB_PESQUISA_RESPOSTA where (DEL_FLAG IS NULL or DEL_FLAG <> '1') AND RES_INT_REG_FUNC = '" + codigoUsuario + "' " + qyeryCli + " AND RES_INT_PESQUISA_ID = PES_INT_ID and REGSYNC = 2 and" + createFilterFreq() + ") as RES_EDIT, ");
        query.append(" (SELECT count(*) from TB_PESQUISA_RESPOSTA where (DEL_FLAG IS NULL or DEL_FLAG <> '1') AND RES_INT_REG_FUNC = '" + codigoUsuario + "' " + qyeryCli + " AND RES_INT_PESQUISA_ID = PES_INT_ID and REGSYNC = 1 and" + createFilterFreq() + ") as RES_INSERT, ");
        query.append(" (SELECT count(*) from (Select count (RES_TXT_CLIENTE) FROM  TB_PESQUISA_RESPOSTA where (DEL_FLAG IS NULL or DEL_FLAG <> '1') AND RES_INT_REG_FUNC = '" + codigoUsuario + "' " + qyeryCli + " AND RES_INT_PESQUISA_ID = PES_INT_ID and" + createFilterFreq() + " group by RES_TXT_CLIENTE)) as RES_CLI_TOTAL ");

        query.append(" from TB_PESQUISA as pes ");

        if (whereClause != null) {
            query.append(" " + whereClause);
        }

        if (orderBy != null) {
            query.append(" " + orderBy);
        }

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), args)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Pesquisa pes = new PesquisaCursorWrapper(cursor).getPesquisa();
                listPesquisa.add(pes);
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listPesquisa;
    }

    private String createFilterFreq(){
        StringBuilder whereClause = new StringBuilder();
        whereClause.append(" ((PES_INT_FRE_ID = '" + TFreq.DAILY.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.DAILY, currentDate) + "') OR ");
        whereClause.append(" (PES_INT_FRE_ID = '" + TFreq.WEEKLY.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.WEEKLY, currentDate) + "') OR ");
        whereClause.append(" (PES_INT_FRE_ID = '" + TFreq.BIWEEKLY.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.BIWEEKLY, currentDate) + "') OR ");
        whereClause.append(" (PES_INT_FRE_ID = '" + TFreq.MONTHLY.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.MONTHLY, currentDate) + "') OR ");
        whereClause.append(" (PES_INT_FRE_ID = '" + TFreq.BIMONTHLY.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.BIMONTHLY, currentDate) + "') OR ");
        whereClause.append(" (PES_INT_FRE_ID = '" + TFreq.QUARTERLY.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.QUARTERLY, currentDate) + "') OR ");
        whereClause.append(" (PES_INT_FRE_ID = '" + TFreq.SEMIANNUAL.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.SEMIANNUAL, currentDate) + "') OR ");
        whereClause.append(" (PES_INT_FRE_ID = '" + TFreq.YEARLY.getValue() + "' AND RES_TXT_DTFREQ = '" + TFreq.getFreq(TFreq.YEARLY, currentDate) + "'))");

        return whereClause.toString();
    }



    public ArrayList<MultiValues> getPesquisaJustificativa(String idPesquisa) {
        ArrayList<MultiValues> listMultiValues = new ArrayList<>();


        StringBuilder query = new StringBuilder();
        query.append("select ID_JUSTIFICATIVA, ");
        query.append(" ID_PESQUISA, ");
        query.append(" DESCR, ");
        query.append(" DEL_FLAG");
        query.append(" FROM TB_PESQUISA_JUSTIFICATIVA as pes ");
        query.append(" WHERE ID_PESQUISA = ? AND DEL_FLAG <> '1' ");

        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(idPesquisa);

        SQLiteDatabase database = getDb();
        try (Cursor cursor = database.rawQuery(query.toString(), whereArgs.toArray(new String[0]))) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                MultiValues multiValues = new MultiValues();
                multiValues.setDesc(cursor.getString(cursor.getColumnIndex("DESCR")));
                multiValues.setValCod(cursor.getInt(cursor.getColumnIndex("ID_JUSTIFICATIVA")));
                listMultiValues.add(multiValues);

                cursor.moveToNext();
            }
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return listMultiValues;
    }


    public class PesquisaCursorWrapper extends CursorWrapper {

        public PesquisaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Pesquisa getPesquisa() {
            Pesquisa pesquisa = new Pesquisa();

            if(currentDate != null){
                pesquisa.setCurrentDate(currentDate);
            }

            pesquisa.setCodigo(getInt(getColumnIndex("PES_INT_ID")));
            pesquisa.setNome(getString(getColumnIndex("PES_TXT_NOME")));
            pesquisa.setEdit(getInt(getColumnIndex("PES_TXT_EDIT")));

            pesquisa.setStatus(getInt(getColumnIndex("PES_INT_STATUS")));
            pesquisa.setTipo(getInt(getColumnIndex("PES_INT_TIPO")));

            if(pesquisa.getTipo() == Pesquisa.COACHING){
                pesquisa.setSelecionaCliente(true);
            } else {
                pesquisa.setSelecionaCliente(
                        getString(getColumnIndex("PES_TXT_SELEC_CLI")).equals("S"));
            }

            try {
                pesquisa.setDataInicio(FormatUtils.toDate(
                        getString(getColumnIndex("PES_DAT_INICIO"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getPesquisa: " + parseEx.toString());
            }

            try {
                pesquisa.setDataFim(FormatUtils.toDate(
                        getString(getColumnIndex("PES_DAT_FIM"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getPesquisa: " + parseEx.toString());
            }

            try {
                pesquisa.setAtividadeObrigatoria(getString(getColumnIndex("PES_TXT_OBRIGATORIO")).equals("1"));
            } catch (Exception parseEx) {
                LogUser.log(Config.TAG, "getPesquisa: " + parseEx.toString());
            }


            pesquisa.setFreq(getInt(getColumnIndex("PES_INT_FRE_ID")));

            try {
                int REGSEND =  getInt(getColumnIndex(
                        "RES_SEND"));

                int REGEDIT =  getInt(getColumnIndex(
                        "RES_EDIT"));

                int REGINSERT =  getInt(getColumnIndex(
                        "RES_INSERT"));

                int RESPESCLI =  getInt(getColumnIndex(
                        "RES_CLI_TOTAL"));

                if(REGSEND > 0 || REGEDIT > 0){
                    pesquisa.setStatusResposta(Pesquisa.OBRIGATORIAS_RESPONDIDAS);
                } else if(REGINSERT > 0) {
                    pesquisa.setStatusResposta(Pesquisa.PARCIL_RESPONDIDAS);
                } else {
                    pesquisa.setStatusResposta(Pesquisa.OBRIGATORIAS_NAO_RESPONDIDAS);
                }

                pesquisa.setQtdCli(RESPESCLI);
                pesquisa.setVisualizaAtividade(getString(getColumnIndex("PES_TXT_VISUALIZA_ATIVIDADE")).equals("1"));

            } catch (Exception exception) {
                LogUser.log(Config.TAG, exception.getMessage());
            }

            return pesquisa;
        }
    }

    public enum TTypePesquisa {
        ISROTA(1),
        PESQUISA(2),
        COACHING(3);

        private int intValue;

        TTypePesquisa(int value) {
            intValue = value;
        }

        public int getValue() {
            return intValue;
        }

        public static TTypePesquisa fromInteger(int value) {
            TTypePesquisa typePesquisa = TTypePesquisa.PESQUISA;
            switch (value) {
                case 1:
                    typePesquisa = TTypePesquisa.ISROTA;
                    break;
                case 2:
                    typePesquisa = TTypePesquisa.PESQUISA;
                    break;
                case 3:
                    typePesquisa = TTypePesquisa.COACHING;
                    break;
            }

            return typePesquisa;
        }
    }
}



