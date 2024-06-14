package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.data.ObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioObjetivoDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioObjetivo;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class AsyncTaskRelatorioObjetivo extends AsyncTask<Void, Void, List<RelatorioObjetivo>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private ObjetivoFilter mObjetivoFilter;
    private RelatorioObjetivoDao mRelatorioObjetivoDao;
    private String mNome;
    private int index;


    public AsyncTaskRelatorioObjetivo(Context context, int index, String mNome, ObjetivoFilter mObjetivoFilter, RelatorioObjetivoDao mRelatorioObjetivoDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mObjetivoFilter = mObjetivoFilter;
        this.mRelatorioObjetivoDao = mRelatorioObjetivoDao;
        this.mNome = mNome;
        this.index = index;
    }

    @Override
    protected List<RelatorioObjetivo> doInBackground(Void... params) {
        List<RelatorioObjetivo> mRelatorioObjetivo;

        try {
            Current current = Current.getInstance(context);
            String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
            String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

            mRelatorioObjetivo = mRelatorioObjetivoDao.findAll(codigoUsuario, codigoUnidadeNegocio, mNome,
                    mObjetivoFilter, index);

        } catch (Exception ex){
            mRelatorioObjetivo = new ArrayList<>();
            LogUser.log(ex.toString());
        }

        return mRelatorioObjetivo;
    }

    @Override
    protected void onPostExecute(List<RelatorioObjetivo> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<RelatorioObjetivo> objects);
    }
}
