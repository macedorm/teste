package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.adapter.ProdutosAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.GetAllProdutosTask;
import br.com.jjconsulting.mobile.dansales.database.DbQueryPagingService;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ProdutosDialogFragment extends DialogFragment
        implements GetAllProdutosTask.OnPostExecuteListener {

    private static final String ARG_CODIGO_PEDIDO = "codigo_pedido";
    private static final String FILTER_PRODUTO = "filter_produto";

    private Pedido mPedido;
    private ProdutoDao mProdutoDao;
    private ArrayList<Produto> mProdutos;
    private DbQueryPagingService mPagingService;
    private String mFiltro;
    private boolean mPaginationFinished;
    private boolean mNoMoreProductsAvailable;

    private OnProdutoClickListener mOnProdutoClickListener;

    private ProdutosAdapter mProdutosAdapter;
    private RecyclerView mDialogItemRecyclerView;
    private EditText mIdItemSearchEditText;

    public ProdutosDialogFragment() { }

    public static ProdutosDialogFragment newInstance(String codigoPedido) {
        ProdutosDialogFragment fragment = new ProdutosDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CODIGO_PEDIDO, codigoPedido);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogProductStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.produtos_dialog_fragment, container);

        // workaround to turn the background transparent - to make it looks like a popup
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mDialogItemRecyclerView = rootView.findViewById(R.id.prod_dialog_recycler_view);
        mIdItemSearchEditText = rootView.findViewById(R.id.prod_dialog_search_edit_text);

        String codigoPedido = getArguments().getString(ARG_CODIGO_PEDIDO);
        PedidoDao pedidoDao = new PedidoDao(getActivity());
        mPedido = pedidoDao.get(codigoPedido);

        final boolean showSortimento = Current.getInstance(getActivity())
                .getUsuario()
                .getPerfil()
                .getPerfilVenda(mPedido)
                .isSortimentoHabilitado();

        mProdutoDao = new ProdutoDao(getActivity());
        mProdutos = new ArrayList<>();
        mPagingService = new DbQueryPagingService(Config.SIZE_PAGE);

        mProdutosAdapter = new ProdutosAdapter(this.getActivity(), mProdutos, showSortimento);

        mDialogItemRecyclerView.setAdapter(mProdutosAdapter);
        mDialogItemRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(
                mDialogItemRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mDialogItemRecyclerView.addItemDecoration(divider);

        mDialogItemRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount() - 1;
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && mPaginationFinished
                        && !mNoMoreProductsAvailable) {
                    mDialogItemRecyclerView.post(() -> executeGetAllProdutosTask(false));
                }
            }
        });

        ItemClickSupport.addTo(mDialogItemRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        if (mProdutos != null && position < mProdutos.size()) {
                            if (mProdutos.get(position).getCodigo() != null) {
                                 mOnProdutoClickListener.onProdutoClick(mProdutos.get(position));
                            }
                        }
                    }catch (Exception ex){
                        LogUser.log(Config.TAG, ex.toString());
                    }

                    dismiss();
                });

        mIdItemSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFiltro = s.toString();
                executeGetAllProdutosTask(true);
            }
        });

        mFiltro = savedInstanceState == null ?
                null : savedInstanceState.getString(FILTER_PRODUTO);

        executeGetAllProdutosTask(true);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String filter = mIdItemSearchEditText.getText().toString();
        if (!TextUtils.isEmpty(filter)) {
            outState.putString(FILTER_PRODUTO, filter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnProdutoClickListener) {
            mOnProdutoClickListener = (OnProdutoClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProdutoClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnProdutoClickListener = null;
    }

    @Override
    public void onPostExecute(GetAllProdutosTask.Result result) {
        ArrayList<Produto> produtos = result.getProdutos();

        if (result.isReset()) {
            mProdutos.clear();
        }

        mPaginationFinished = true;
        mNoMoreProductsAvailable = mPagingService.getPageSize() > produtos.size();
        mProdutosAdapter.removeLoadingFooter();
        mProdutos.addAll(produtos);
        mProdutosAdapter.notifyDataSetChanged();
    }

    private void executeGetAllProdutosTask(boolean reset) {

        if (reset) {
            mProdutos.clear();
            mPagingService.goToFirstPage();
        } else {
            mPagingService.goToNextPage();
        }

        mProdutosAdapter.addLoadingFooter();
        mProdutosAdapter.notifyDataSetChanged();

        mPaginationFinished = false;
        mNoMoreProductsAvailable = false;

        GetAllProdutosTask.Parameters parameters = new GetAllProdutosTask.Parameters(mProdutoDao,
                mPagingService, Current.getInstance(getContext()).getUsuario(), mPedido, mFiltro, reset);
        GetAllProdutosTask task = new GetAllProdutosTask(this);
        task.execute(parameters);
    }

    public interface OnProdutoClickListener {

        void onProdutoClick(Produto produto);
    }
}
