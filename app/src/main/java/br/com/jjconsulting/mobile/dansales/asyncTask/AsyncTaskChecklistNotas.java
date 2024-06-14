package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ChecklistNotaFilter;
import br.com.jjconsulting.mobile.dansales.data.NotaFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioChecklistNotasDao;
import br.com.jjconsulting.mobile.dansales.database.RelatorioNotasDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioChecklistNotas;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskChecklistNotas extends AsyncTask<Void, Void, List<RelatorioChecklistNotas>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private ChecklistNotaFilter mChecklistNotaFilter;
    private String mNome;
    private RelatorioChecklistNotasDao mRelatorioChecklistNotasDao;
    private int index;


    public AsyncTaskChecklistNotas(Context context, int index, ChecklistNotaFilter checklistNotaFilter, String nome, RelatorioChecklistNotasDao relatorioNotasDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mChecklistNotaFilter = checklistNotaFilter;
        this.mNome = nome;
        this.mRelatorioChecklistNotasDao = relatorioNotasDao;
        this.index = index;
    }

    @Override
    protected List<RelatorioChecklistNotas> doInBackground(Void... params) {
        List<RelatorioChecklistNotas> mChecklistNotas;

        Current current = Current.getInstance(context);
        String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        mChecklistNotas = mRelatorioChecklistNotasDao.findAll(codigoUsuario, codigoUnidadeNegocio, mNome,
                mChecklistNotaFilter, index);

        return mChecklistNotas;
    }

    @Override
    protected void onPostExecute(List<RelatorioChecklistNotas> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<RelatorioChecklistNotas> objects);
    }
}
