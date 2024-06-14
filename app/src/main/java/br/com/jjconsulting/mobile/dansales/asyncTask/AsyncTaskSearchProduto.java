package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class AsyncTaskSearchProduto extends AsyncTask<Void, Void, Produto> {

    private String mCodigo;
    private Pedido mPedido;
    private boolean mIsSortimentoAvailable;

    private ProdutoDao mProdutoDao;
    private OnAsyncResponse mOnAsyncResponse;

    private Context context;

    public AsyncTaskSearchProduto(Context context, String codigo, Pedido pedido, boolean isSortimentoAvailable,
                                  ProdutoDao produtoDao, OnAsyncResponse onAsyncResponse) {
        this.mCodigo = codigo;
        this.mPedido = pedido;
        this.mIsSortimentoAvailable = isSortimentoAvailable;
        this.mProdutoDao = produtoDao;
        this.mOnAsyncResponse = onAsyncResponse;
        this.context = context;
    }

    @Override
    protected Produto doInBackground(Void... params) {
        String codigoProduto = TextUtils.fillLeftWithUntil(mCodigo,
                '0', 18);

        return mProdutoDao.getByCod(Current.getInstance(context).getUsuario(), mPedido, codigoProduto,
                mCodigo, true, mIsSortimentoAvailable);
    }

    @Override
    protected void onPostExecute(Produto objects) {
        mOnAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {

        void processFinish(Produto produto);
    }
}
