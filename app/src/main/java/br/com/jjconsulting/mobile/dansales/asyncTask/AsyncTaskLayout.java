package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.database.LayoutDao;
import br.com.jjconsulting.mobile.dansales.database.SortimentoComplementoDao;
import br.com.jjconsulting.mobile.dansales.model.ComplementoSortimento;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskLayout extends AsyncTask<Void, Void, Object[]> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private LayoutFilter mLayoutFilter;

    private String mNome;

    private boolean isComplemento;

    public AsyncTaskLayout(Context context , OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mLayoutFilter = null;
        this.mNome = null;
        this.isComplemento = false;
    }

    public AsyncTaskLayout(Context context, LayoutFilter layoutFilter, String mNome, boolean isComplemento , OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mLayoutFilter = layoutFilter;
        this.mNome = mNome;
        this.isComplemento = isComplemento;
    }

    @Override
    protected Object[] doInBackground(Void... params) {
        Object object[] = new Object[2];

        List<Layout> mLayout;

        Current current = Current.getInstance(context);
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        LayoutDao layoutDao = new LayoutDao(context);
        mLayout = layoutDao.getListLayout(codigoUnidadeNegocio, mNome, mLayoutFilter);

        object[0] = mLayout;

        if(isComplemento) {
            SortimentoComplementoDao sortimentoComplementoDao = new SortimentoComplementoDao(context);
            List<ComplementoSortimento> mComplementoSortimentos = sortimentoComplementoDao.getComplemento(current.getUsuario().getCodigo(), codigoUnidadeNegocio);
            object[1] = mComplementoSortimentos;
        }

        return object;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(Object[] objects);
    }
}
