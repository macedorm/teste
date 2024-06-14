package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class GetAllProdutosDATTask
        extends AsyncTask<GetAllProdutosDATTask.Parameters, Void, ArrayList<Produto>> {

    private OnPostExecuteListener onPostExecuteListener;

    public GetAllProdutosDATTask(OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
    }

    @Override
    protected ArrayList<Produto> doInBackground(Parameters... parameters) {
        Parameters p = parameters[0];
        boolean possuiFiltro = !TextUtils.isEmpty(p.filtro);
        if (possuiFiltro) {
            return p.produtoDao.findAllAsDAT(p.usuario, p.pedido, p.filtro);
        }
        return p.produtoDao.getAllAsDAT(p.usuario, p.pedido);
    }

    @Override
    protected void onPostExecute(ArrayList<Produto> produtos) {
        onPostExecuteListener.onPostExecute(produtos);
    }

    public static class Parameters {

        private final ProdutoDao produtoDao;
        private final Usuario usuario;
        private final Pedido pedido;
        private final String filtro;

        public Parameters(ProdutoDao produtoDao, Usuario usuario, Pedido pedido, String filtro) {
            this.produtoDao = produtoDao;
            this.usuario = usuario;
            this.pedido = pedido;
            this.filtro = filtro;
        }
    }

    public interface OnPostExecuteListener {
        void onPostExecute(ArrayList<Produto> produtos);
    }
}
