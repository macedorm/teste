package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskCRDetail extends AsyncTask<Void, Void, List<ItensListSortimento>> {

    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private SortimentoDao mSortimentoDao;

    private String mPlanogramCode;
    private String mNome;

    public AsyncTaskCRDetail(Context context, String planogramCode, String nome, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mPlanogramCode = planogramCode;
        this.mNome = nome;
    }

    @Override
    protected List<ItensListSortimento> doInBackground(Void... params) {
        mSortimentoDao = new SortimentoDao(context);
        List<ItensListSortimento> itensListSortimentos;

        Current current = Current.getInstance(context);
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();
        itensListSortimentos = mSortimentoDao.getItensSortimentoCR(mPlanogramCode, mNome, codigoUnidadeNegocio);

        return itensListSortimentos;
    }

    @Override
    protected void onPostExecute(List<ItensListSortimento>  objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<ItensListSortimento>  objects);
    }
}
