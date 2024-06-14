package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.AgendasAdapter;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.AgendaDao;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.model.Agenda;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;

public class AgendasFragment extends Fragment {

    private static final String ARG_CODIGO_PEDIDO = "codigo_pedido";

    private Pedido mPedido;
    private List<Agenda> mAgendas;
    private AgendaDao mAgendaDao;

    private OnAgendaClickListener mOnAgendaClickListener;
    private AgendasAdapter mAgendasAdapter;
    private RecyclerView mAgendasRecyclerView;
    private ViewGroup mViewGroupNoData;
    private Button mButtonMoreOptions;

    public AgendasFragment() { }

    public static AgendasFragment newInstance(String codigoPedido) {
        AgendasFragment fragment = new AgendasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CODIGO_PEDIDO, codigoPedido);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_agendas, container, false);

        mAgendasRecyclerView = view.findViewById(R.id.agendas_recycler_view);
        mViewGroupNoData = view.findViewById(R.id.view_group_no_data);
        mButtonMoreOptions = view.findViewById(R.id.button_more_options);

        String codigoPedido = getArguments().getString(ARG_CODIGO_PEDIDO);
        PedidoDao pedidoDao = new PedidoDao(getActivity());
        mPedido = pedidoDao.get(codigoPedido);

        mAgendaDao = new AgendaDao(getActivity());
        mAgendas = mAgendaDao.getAll(mPedido.getCodigoCliente());
        mAgendasAdapter = new AgendasAdapter(getActivity(), mAgendas);

        mAgendasRecyclerView.setAdapter(mAgendasAdapter);
        mAgendasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(
                mAgendasRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mAgendasRecyclerView.addItemDecoration(divider);

        mButtonMoreOptions.setOnClickListener(v -> mOnAgendaClickListener
                .onAgendaClick(null, Pedido.TIPO_AGENDA_NAO_POSSUI));

        ItemClickSupport.addTo(mAgendasRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    Agenda agenda = mAgendas.get(position);
                    mOnAgendaClickListener.onAgendaClick(agenda, Pedido.TIPO_AGENDA_AGENDA);
                });

        Current current = Current.getInstance(getActivity());
        PedidoBusiness pedidoBusiness = new PedidoBusiness();
        boolean agendaAvailable = mAgendas.size() > 0;
        boolean moreOptionsAvailable = pedidoBusiness.isAgendaOutrasOperacoes(current.getUsuario());

        mViewGroupNoData.setVisibility(agendaAvailable ? View.GONE : View.VISIBLE);
        mButtonMoreOptions.setVisibility(moreOptionsAvailable ? View.VISIBLE : View.GONE);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.agendas_menu, menu);

        Current current = Current.getInstance(getActivity());
        AgendaDao agendaDao = new AgendaDao(getActivity());
        PedidoBusiness pedidoBusiness = new PedidoBusiness();

        if (!pedidoBusiness.isAgendaOutrasOperacoes(current.getUsuario())) {
            MenuItem outrasOperacoes = menu.findItem(R.id.action_add_new_agenda_outras_operacoes);
            outrasOperacoes.setVisible(false);
        }

        if (!pedidoBusiness.isAgendaDistribuidor(agendaDao, current.getUsuario(),
                mPedido.getCliente())) {
            MenuItem agendaDistribuidor = menu.findItem(
                    R.id.action_add_new_pedido_agenda_distribuidor);
            agendaDistribuidor.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_new_agenda_outras_operacoes:
                mOnAgendaClickListener.onAgendaClick(null, Pedido.TIPO_AGENDA_NAO_POSSUI);
                return true;
            case R.id.action_add_new_pedido_agenda_distribuidor:
                mOnAgendaClickListener.onAgendaClick(null,
                        Pedido.TIPO_AGENDA_AGENDA_DISTRIBUIDOR);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAgendaClickListener) {
            mOnAgendaClickListener = (OnAgendaClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAgendaClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnAgendaClickListener = null;
    }

    public interface OnAgendaClickListener {
        void onAgendaClick(Agenda agenda, int tipoAgenda);
    }
}
