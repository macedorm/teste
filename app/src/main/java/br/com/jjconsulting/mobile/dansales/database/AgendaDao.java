package br.com.jjconsulting.mobile.dansales.database;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.model.Agenda;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class AgendaDao extends BaseDansalesDao {
    public AgendaDao(Context context) {
        super(context);
    }

    public ArrayList<Agenda> getAll(String codigoCliente) {
        String whereClause = "and age.ID_CLIENTE = ?";
        String[] whereArgs = {codigoCliente};
        return query(whereClause, whereArgs, null);
    }

    public boolean hasAgendaDistribuidor(String codigoCliente) {
        String whereClause = "and age.ID_CLIENTE = ?" +
                " and age.ID_SOLUTION like '9%'";
        String[] whereArgs = {codigoCliente};
        return query(whereClause, whereArgs, null).size() > 0;
    }

    private ArrayList<Agenda> query(String whereClause, String[] args, String orderBy) {
        ArrayList<Agenda> agendas = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("select age.ID_SOLUTION,");
        query.append(" age.DATA_CTR_FIM,");
        query.append(" age.ID_EDITAL,");
        query.append(" age.ID_CLIENTE");
        query.append(" from PROCESSOS_EMPENHOS_HEADER_SOL age ");
        query.append(" where 1 = 1");

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
                agendas.add(new AgendaDao.AgendaCursorWrapper(cursor).getAgenda());
                cursor.moveToNext();
            }
            database.close();
        } catch (Exception e) {
            LogUser.log(Config.TAG, "query: " + e);
        }

        return agendas;
    }

    public class AgendaCursorWrapper extends CursorWrapper {

        public AgendaCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Agenda getAgenda() {
            Agenda agenda = new Agenda();
            agenda.setCodigo(getString(getColumnIndex("ID_SOLUTION")));
            agenda.setEdital(getString(getColumnIndex("ID_EDITAL")));
            agenda.setCodigoCliente(getString(getColumnIndex("ID_CLIENTE")));

            try {
                agenda.setValidade(FormatUtils.toDate(
                        getString(getColumnIndex("DATA_CTR_FIM"))));
            } catch (ParseException parseEx) {
                LogUser.log(Config.TAG, "getAgenda: " + parseEx.toString());
            }

            return agenda;
        }
    }
}
