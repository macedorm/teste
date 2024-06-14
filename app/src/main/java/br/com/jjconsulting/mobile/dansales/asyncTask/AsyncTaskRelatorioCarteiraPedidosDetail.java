package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.PedidoFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioCarteiraPedidoDao;
import br.com.jjconsulting.mobile.dansales.database.RelatorioCarteiraPedidosDetailDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedido;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoDetail;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskRelatorioCarteiraPedidosDetail extends AsyncTask<Void, Void, List<RelatorioCarteiraPedidoDetail>> {

    private OnAsyncResponse onAsyncResponse;
    private RelatorioCarteiraPedidosDetailDao pedidoDao;
    private int index;
    private String numeroPedido;

    private Context context;

    public AsyncTaskRelatorioCarteiraPedidosDetail(Context context, String numeroPedido, int index, RelatorioCarteiraPedidosDetailDao pedidoDao, OnAsyncResponse onAsyncResponse) {
        this.onAsyncResponse = onAsyncResponse;
        this.pedidoDao = pedidoDao;
        this.index = index;
        this.numeroPedido = numeroPedido;
        this.context = context;
    }

    @Override
    protected List<RelatorioCarteiraPedidoDetail> doInBackground(Void... params) {
        List<RelatorioCarteiraPedidoDetail> pedidos = null;

        String codUnNeg = Current.getInstance(context).getUnidadeNegocio().getCodigo();
        pedidos = pedidoDao.findAll(codUnNeg, numeroPedido, index);

        return pedidos;
    }

    @Override
    protected void onPostExecute(List<RelatorioCarteiraPedidoDetail> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<RelatorioCarteiraPedidoDetail> objects);
    }
}
