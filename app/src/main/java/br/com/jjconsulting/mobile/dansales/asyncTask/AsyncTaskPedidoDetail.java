package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.ItemPedidoDao;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskPedidoDetail extends AsyncTask<String, Void, ArrayList<Object>> {

    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private PedidoViewType pedidoViewType;

    public AsyncTaskPedidoDetail(Context context, PedidoViewType pedidoViewType,
                                 OnAsyncResponse onAsyncResponse) {
        this.context = context.getApplicationContext();
        this.onAsyncResponse = onAsyncResponse;
        this.pedidoViewType = pedidoViewType;
    }

    @Override
    protected ArrayList<Object> doInBackground(String... params) {
        ArrayList<Object> result = new ArrayList<>();

        PedidoBusiness pedidoBusiness = new PedidoBusiness();
        PedidoDao pedidoDao = new PedidoDao(context);
        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);

        Pedido pedido = pedidoDao.get(params[0]);

        if (pedido == null) {
            return null;
        }

        boolean tipoVisualizacao = pedidoBusiness.vizualizationMode(pedido, pedidoViewType);

        ArrayList<ItemPedido> itens;
        if (pedidoViewType == PedidoViewType.APROVACAO || pedidoViewType == PedidoViewType.LIBERACAO) {
            itens = itemPedidoDao.getAllAprovacaoOuLiberacao(Current.getInstance(context).getUsuario(),
                    pedido);
        } else {
            itens = itemPedidoDao.getAll(Current.getInstance(context).getUsuario(),
                    pedido, tipoVisualizacao);
        }

        result.add(pedido);
        result.add(tipoVisualizacao);
        result.add(itens);

        return result;
    }

    /**
     * @param objects (0) pedido; (1) tipo de visualização; (2) itens do pedido
     */
    @Override
    protected void onPostExecute(ArrayList<Object> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {

        void processFinish(ArrayList<Object> result);
    }
}
