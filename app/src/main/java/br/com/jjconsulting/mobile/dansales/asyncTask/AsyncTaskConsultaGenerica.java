package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ConsultaGenericaFilter;
import br.com.jjconsulting.mobile.dansales.database.ConsultaGenericaDao;
import br.com.jjconsulting.mobile.dansales.model.ConsultaGenerica;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskConsultaGenerica extends AsyncTask<Void, Void, List<ConsultaGenerica>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private ConsultaGenericaFilter mConsultaGenericaFilter;
    private String mNome;
    private ConsultaGenericaDao mCosultaGenericaDao;
    private int index;


    public AsyncTaskConsultaGenerica(Context context, int index, ConsultaGenericaFilter consultaFilter, String nome, ConsultaGenericaDao consultaDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mConsultaGenericaFilter = consultaFilter;
        this.mNome = nome;
        this.mCosultaGenericaDao = consultaDao;
        this.index = index;
    }

    @Override
    protected List<ConsultaGenerica> doInBackground(Void... params) {
        List<ConsultaGenerica> mConsultaGenerica;

        Current current = Current.getInstance(context);
        String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());

        mConsultaGenerica = mCosultaGenericaDao.findAll(codigoUsuario, mNome,
                mConsultaGenericaFilter, index);

        return mConsultaGenerica;
    }

    @Override
    protected void onPostExecute(List<ConsultaGenerica> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<ConsultaGenerica> objects);
    }
}
