package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskCliente extends AsyncTask<Void, Void, List<Cliente>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private ClienteFilter mClienteFilter;
    private String mNome;
    private String mCodigoPesquisa;
    private ClienteDao mClienteDao;
    private int index;
    private boolean isRota;

    public AsyncTaskCliente(Context context, int index, ClienteFilter mClienteFilter, String mNome, ClienteDao mClienteDao, String codigoPesquisa, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mClienteFilter = mClienteFilter;
        this.mNome = mNome;
        this.mClienteDao = mClienteDao;
        this.mCodigoPesquisa = codigoPesquisa;
        this.index = index;
        this.isRota = false;
    }

    public AsyncTaskCliente(Context context, int index, ClienteFilter mClienteFilter, String mNome, ClienteDao mClienteDao, String codigoPesquisa, OnAsyncResponse onAsyncResponse, boolean isRota) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mClienteFilter = mClienteFilter;
        this.mNome = mNome;
        this.mClienteDao = mClienteDao;
        this.mCodigoPesquisa = codigoPesquisa;
        this.index = index;
        this.isRota = isRota;
    }

    @Override
    protected List<Cliente> doInBackground(Void... params) {
        List<Cliente> mClientes;

        Current current = Current.getInstance(context);
        String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        mClientes = mClienteDao.getAll(codigoUsuario, codigoUnidadeNegocio, mNome, mCodigoPesquisa, mClienteFilter, index,isRota);

        return mClientes;
    }

    @Override
    protected void onPostExecute(List<Cliente> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<Cliente> objects);
    }
}
