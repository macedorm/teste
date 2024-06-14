package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.data.PedidoFilter;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskPedidos extends AsyncTask<Void, Void, List<Pedido>> {

    private PedidoViewType type;
    private OnAsyncResponse onAsyncResponse;
    private PedidoFilter pedidoFilter;
    private String razaoSocialCliente;
    private PedidoDao pedidoDao;
    private Context context;

    public AsyncTaskPedidos(Context context, PedidoViewType type, PedidoFilter pedidoFilter, String razaoSocialCliente,
                            PedidoDao pedidoDao, OnAsyncResponse onAsyncResponse) {
        this.type = type;
        this.onAsyncResponse = onAsyncResponse;
        this.pedidoFilter = pedidoFilter;
        this.pedidoDao = pedidoDao;
        this.razaoSocialCliente = razaoSocialCliente;
        this.context = context;
    }

    @Override
    protected List<Pedido> doInBackground(Void... params) {
        List<Pedido> pedidos;

        Current current = Current.getInstance(context);
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        if (pedidoFilter == null) {
            switch (type) {
                case PEDIDO:
                    pedidos = pedidoDao.getAll(current.getUsuario(), codigoUnidadeNegocio,
                            true);
                    break;
                case APROVACAO:
                    pedidos = pedidoDao.getAllAprovacao(current.getUsuario(), codigoUnidadeNegocio,
                            true);
                    break;
                case LIBERACAO:
                    pedidos = pedidoDao.getAllLiberacao(current.getUsuario(), codigoUnidadeNegocio,
                            true);
                    break;
                default:
                    pedidos = null;
                    break;
            }
        } else {
            switch (type) {
                case PEDIDO:
                    pedidos = pedidoDao.findAll(current.getUsuario(), codigoUnidadeNegocio,
                            razaoSocialCliente, pedidoFilter, true);
                    break;
                case APROVACAO:
                    pedidos = pedidoDao.findAllAprovacao(current.getUsuario(), codigoUnidadeNegocio,
                            razaoSocialCliente, pedidoFilter, true);
                    break;
                case LIBERACAO:
                    pedidos = pedidoDao.findAllLiberacao(current.getUsuario(), codigoUnidadeNegocio,
                            razaoSocialCliente, pedidoFilter, true);
                    break;
                default:
                    pedidos = null;
                    break;
            }
        }

        return pedidos;
    }

    @Override
    protected void onPostExecute(List<Pedido> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<Pedido> objects);
    }
}
