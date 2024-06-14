package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.data.NotaFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioNotasDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class AsyncTaskNotas extends AsyncTask<Void, Void, List<RelatorioNotas>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private NotaFilter mNotaFilter;
    private String mNome;
    private RelatorioNotasDao mRelatorioNotasDao;
    private int index;


    public AsyncTaskNotas(Context context, int index, NotaFilter mNotaFilter, String mNome, RelatorioNotasDao mRelatorioNotasDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mNotaFilter = mNotaFilter;
        this.mNome = mNome;
        this.mRelatorioNotasDao = mRelatorioNotasDao;
        this.index = index;
    }

    @Override
    protected List<RelatorioNotas> doInBackground(Void... params) {
        List<RelatorioNotas> mNotas;

        try {

            Current current = Current.getInstance(context);
            String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
            String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

            mNotas = mRelatorioNotasDao.findAll(codigoUsuario, codigoUnidadeNegocio, mNome,
                    mNotaFilter, index);

        }catch (Exception ex){
            LogUser.log(ex.toString());
            mNotas = new ArrayList<>();
        }

        return mNotas;
    }

    @Override
    protected void onPostExecute(List<RelatorioNotas> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<RelatorioNotas> objects);
    }
}
