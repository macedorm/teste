package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.RelatorioNotasItensDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotasItem;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskRelatorioNotasItens extends AsyncTask<Void, Void, List<RelatorioNotasItem>> {

    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private RelatorioNotasItensDao mRelatorioNotasItensDao;
    private String nota;
    private String serie;
    private int index;

    public AsyncTaskRelatorioNotasItens(Context context, int index, String nota, String serie, RelatorioNotasItensDao mRelatorioNotasItensDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mRelatorioNotasItensDao = mRelatorioNotasItensDao;
        this.nota = nota;
        this.serie = serie;
        this.index = index;
    }

    @Override
    protected List<RelatorioNotasItem> doInBackground(Void... params) {
        List<RelatorioNotasItem> mSKU = new ArrayList<>();

        Current current = Current.getInstance(context);
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        mSKU = mRelatorioNotasItensDao.findAll(codigoUnidadeNegocio, nota, serie, index);

        return mSKU;
    }

    @Override
    protected void onPostExecute(List<RelatorioNotasItem> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<RelatorioNotasItem> objects);
    }
}
