package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.data.NotaFilter;
import br.com.jjconsulting.mobile.dansales.database.ItemPedidoDao;
import br.com.jjconsulting.mobile.dansales.database.RelatorioNotasDao;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskItensSortimento extends AsyncTask<Void, Void, ArrayList<ItemPedido>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private Usuario mUsuario;
    private Pedido mPedido;


    public AsyncTaskItensSortimento(Context context, Usuario usuario, Pedido pedido, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mUsuario = usuario;
        this.mPedido = pedido;
    }

    @Override
    protected ArrayList<ItemPedido> doInBackground(Void... params) {
        SortimentoDao sortimentoDao = new SortimentoDao(context);
        ArrayList<ItensSortimento> itens = sortimentoDao.getItensSortimentoPedido(mUsuario,  mPedido, mPedido.getCodigoSortimento(), mPedido.getDataCadastro());

        ItemPedidoDao itemPedidoDao = new ItemPedidoDao(context);
        itemPedidoDao.createItemSugerido(itens, mUsuario,  mPedido);

        ArrayList<ItemPedido> itensPedidos = itemPedidoDao.getAll(Current.getInstance(context).getUsuario(),
                mPedido, true);

        return itensPedidos;
    }

    @Override
    protected void onPostExecute(ArrayList<ItemPedido> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(ArrayList<ItemPedido> objects);
    }
}
