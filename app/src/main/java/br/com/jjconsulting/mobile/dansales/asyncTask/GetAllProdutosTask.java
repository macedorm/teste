package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.os.AsyncTask;
import android.text.TextUtils;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.database.DbQueryPagingService;
import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.Usuario;

public class GetAllProdutosTask
        extends AsyncTask<GetAllProdutosTask.Parameters, Void, GetAllProdutosTask.Result> {

    private OnPostExecuteListener onPostExecuteListener;

    public GetAllProdutosTask(OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
    }

    @Override
    protected Result doInBackground(Parameters... parameters) {
        ArrayList<Produto> produtos;
        Parameters p = parameters[0];

        if (!TextUtils.isEmpty(p.filtro)) {
            produtos = p.produtoDao.findAllWithPreco(p.usuario, p.pedido,
                    p.filtro, p.pagingService);
            return new Result(produtos, p.reset);
        } else {
            produtos = p.produtoDao.getAllWithPreco(p.usuario, p.pedido, p.pagingService);
        }

        return new Result(produtos, p.reset);
    }

    @Override
    protected void onPostExecute(Result result) {
        onPostExecuteListener.onPostExecute(result);
    }

    public interface OnPostExecuteListener {

        void onPostExecute(Result result);
    }

    public static class Parameters {

        private final ProdutoDao produtoDao;
        private final DbQueryPagingService pagingService;
        private final Usuario usuario;
        private final Pedido pedido;
        private final String filtro;
        private final boolean reset;

        public Parameters(ProdutoDao produtoDao, Usuario usuario, Pedido pedido,
                          String filtro, boolean result) {
            this.produtoDao = produtoDao;
            this.pagingService = null;
            this.usuario = usuario;
            this.pedido = pedido;
            this.filtro = filtro;
            this.reset = result;
        }

        public Parameters(ProdutoDao produtoDao, DbQueryPagingService pagingService,
                          Usuario usuario, Pedido pedido, String filtro, boolean reset) {
            this.produtoDao = produtoDao;
            this.pagingService = pagingService;
            this.usuario = usuario;
            this.pedido = pedido;
            this.filtro = filtro;
            this.reset = reset;
        }
    }

    public static class Result {

        private final ArrayList<Produto> produtos;
        private final boolean reset;

        public Result(ArrayList<Produto> produtos, boolean reset) {
            this.produtos = produtos;
            this.reset = reset;
        }

        public ArrayList<Produto> getProdutos() {
            return produtos;
        }

        public boolean isReset() {
            return reset;
        }
    }
}
