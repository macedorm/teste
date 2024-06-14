package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.adapter.LogPedidoAdapter;
import br.com.jjconsulting.mobile.dansales.database.PedidoLogDao;
import br.com.jjconsulting.mobile.dansales.model.LogPedido;

public class LogPedidoFragment extends Fragment {

    private static final String ARG_CODIGO = "codigo";

    private LinearLayout mListEmptyLinearLayout;
    private RecyclerView mLogPedidoRecyclerView;

    private String codigoPedido;

    public LogPedidoFragment() {
    }

    public static LogPedidoFragment newInstance(String codigo) {
        LogPedidoFragment fragment = new LogPedidoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CODIGO, codigo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_pedido, container,
                false);

        getActivity().setTitle(getString(R.string.title_log));
        codigoPedido = getArguments().getString(ARG_CODIGO);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(codigoPedido);

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);

        mLogPedidoRecyclerView = view.findViewById(R.id.log_pedido_recycler_view);
        mLogPedidoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(mLogPedidoRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mLogPedidoRecyclerView.addItemDecoration(divider);

        PedidoLogDao pedidoDao = new PedidoLogDao(getContext());
        ArrayList<LogPedido> listLogPedido = pedidoDao.getLogPedido(codigoPedido);

        LogPedidoAdapter mLogPedidoAdapter = new LogPedidoAdapter(getContext(), listLogPedido);
        mLogPedidoRecyclerView.setAdapter(mLogPedidoAdapter);


        mListEmptyLinearLayout.setVisibility(listLogPedido.size() > 0 ? View.GONE : View.VISIBLE);
        mLogPedidoRecyclerView.setVisibility(listLogPedido.size() > 0 ? View.VISIBLE : View.GONE);

        return view;
    }
}
