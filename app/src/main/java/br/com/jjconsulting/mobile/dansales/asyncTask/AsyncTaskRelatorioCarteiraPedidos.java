package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.CarteiraPedidoFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioCarteiraPedidoDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedido;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskRelatorioCarteiraPedidos extends AsyncTask<Void, Void, List<RelatorioCarteiraPedido>> {

    private OnAsyncResponse onAsyncResponse;
    private CarteiraPedidoFilter pedidoFilter;
    private String razaoSocialCliente;
    private RelatorioCarteiraPedidoDao pedidoDao;
    private int index;

    private Context context;

    public AsyncTaskRelatorioCarteiraPedidos(Context context, CarteiraPedidoFilter pedidoFilter, int index, String razaoSocialCliente,
                                             RelatorioCarteiraPedidoDao pedidoDao, OnAsyncResponse onAsyncResponse) {
        this.onAsyncResponse = onAsyncResponse;
        this.pedidoFilter = pedidoFilter;
        this.pedidoDao = pedidoDao;
        this.razaoSocialCliente = razaoSocialCliente;
        this.index = index;
        this.context = context;
    }

    @Override
    protected List<RelatorioCarteiraPedido> doInBackground(Void... params) {
        List<RelatorioCarteiraPedido> pedidos = null;
        Current current = Current.getInstance(context);
        String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();
        index = pedidoFilter != null ? -1:index;
        pedidos = pedidoDao.findAll(codigoUsuario, codigoUnidadeNegocio, razaoSocialCliente, pedidoFilter, index);

        return pedidos;
    }

    @Override
    protected void onPostExecute(List<RelatorioCarteiraPedido> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<RelatorioCarteiraPedido> objects);
    }
}
