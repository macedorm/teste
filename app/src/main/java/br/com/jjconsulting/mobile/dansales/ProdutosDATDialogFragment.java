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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.adapter.ProdutosDATAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.GetAllProdutosDATTask;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.database.ProdutoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;

public class ProdutosDATDialogFragment extends DialogFragment
        implements GetAllProdutosDATTask.OnPostExecuteListener {

    private static final String ARG_CODIGO_PEDIDO = "codigo_pedido";
    private static final String FILTER_PRODUTO = "filter_produto";

    private Pedido mPedido;
    private ProdutoDao mProdutoDao;
    private ArrayList<Produto> mProdutos;

    private OnProdutoDATClickListener mOnProdutoDATClickListener;

    private ProdutosDATAdapter mProdutosDATAdapter;
    private RecyclerView mDialogItemRecyclerView;
    private EditText mIdItemSearchEditText;

    public ProdutosDATDialogFragment() {
    }

    public static ProdutosDATDialogFragment newInstance(String codigoPedido) {
        ProdutosDATDialogFragment fragment = new ProdutosDATDialogFragment();
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

        mProdutoDao = new ProdutoDao(getActivity());
        mProdutos = new ArrayList<>();

        final boolean showSortimento = Current.getInstance(getActivity())
                .getUsuario()
                .getPerfil()
                .getPerfilVenda(mPedido)
                .isSortimentoHabilitado();

        mProdutosDATAdapter = new ProdutosDATAdapter(this.getActivity(), mProdutos, showSortimento);
        mProdutosDATAdapter.addLoadingFooter();

        mDialogItemRecyclerView.setAdapter(mProdutosDATAdapter);
        mDialogItemRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(
                mDialogItemRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mDialogItemRecyclerView.addItemDecoration(divider);

        ItemClickSupport.addTo(mDialogItemRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    mOnProdutoDATClickListener.onProdutoDATClick(mProdutos.get(position));
                    dismiss();
                });

        mIdItemSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                mProdutos.clear();
                mProdutosDATAdapter.addLoadingFooter();
                mProdutosDATAdapter.notifyDataSetChanged();
                GetAllProdutosDATTask.Parameters parameters = new GetAllProdutosDATTask.Parameters(
                        mProdutoDao, Current.getInstance(getContext()).getUsuario(), mPedido, s.toString());
                GetAllProdutosDATTask task = new GetAllProdutosDATTask(
                        ProdutosDATDialogFragment.this);
                task.execute(parameters);
            }
        });

        String filter = savedInstanceState == null ?
                null : savedInstanceState.getString(FILTER_PRODUTO);
        GetAllProdutosDATTask.Parameters parameters = new GetAllProdutosDATTask.Parameters(
                mProdutoDao, Current.getInstance(getContext()).getUsuario(), mPedido, filter);
        GetAllProdutosDATTask task = new GetAllProdutosDATTask(this);
        task.execute(parameters);

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
        if (context instanceof OnProdutoDATClickListener) {
            mOnProdutoDATClickListener = (OnProdutoDATClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProdutoDATClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnProdutoDATClickListener = null;
    }

    @Override
    public void onPostExecute(ArrayList<Produto> produtos) {
        mProdutosDATAdapter.removeLoadingFooter();
        mProdutos.clear();
        mProdutos.addAll(produtos);
        mProdutosDATAdapter.notifyDataSetChanged();
    }

    public interface OnProdutoDATClickListener {

        void onProdutoDATClick(Produto produto);
    }
}
