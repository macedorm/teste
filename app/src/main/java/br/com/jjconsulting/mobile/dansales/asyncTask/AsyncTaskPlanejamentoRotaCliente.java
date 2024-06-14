package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class AsyncTaskPlanejamentoRotaCliente extends AsyncTask<Void, Void, List<Cliente>> {

    private Context context;
    private OnAsyncResponse onAsyncResponse;

    private Date currentDate;

    private ClienteFilter mClienteFilter;
    private ClienteDao mClienteDao;

    private int index;

    private String mNome;
    private String mPromotor;

    public AsyncTaskPlanejamentoRotaCliente(Context context, int index, ClienteFilter mClienteFilter, String mNome, String promotor, Date date, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mClienteFilter = mClienteFilter;
        this.mNome = mNome;
        this.index = index;
        this.mPromotor = promotor;
        this.currentDate = date;
        mClienteDao = new ClienteDao(context);
    }

    @Override
    protected List<Cliente> doInBackground(Void... params) {
        List<Cliente> mClientes;

        Current current = Current.getInstance(context);
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        if(TextUtils.isNullOrEmpty(mPromotor)){
            String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
            mClientes = mClienteDao.getAll(codigoUsuario, codigoUnidadeNegocio, mNome, null, mClienteFilter, index);
        } else {
            mClientes = mClienteDao.getAll(mPromotor, codigoUnidadeNegocio, mNome, null, null, -1);
            ArrayList<Cliente> clientesTemp = new ArrayList<>();

            for(Cliente cliente:mClientes) {
                if(cliente.getPlanoCampo() != null) {
                    String planoCampo = PlanoCampoUtils.getPlanoCampoFromDate(cliente.getPlanoCampo(), currentDate);
                    if (!PlanoCampoUtils.PEDIDO.equals(planoCampo) &&  !PlanoCampoUtils.TIPO_E.equals(planoCampo) && !PlanoCampoUtils.NAO_POSSUI.equals(planoCampo)) {
                        clientesTemp.add(cliente);
                    }
                }
            }

            mClientes = clientesTemp;
        }

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
