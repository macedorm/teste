package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.jjconsulting.mobile.dansales.adapter.RelatorioNotasItensAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;

public class RelatorioNotasDetailItensFragment extends BaseFragment implements OnPageSelected {

    private static final String KEY_NOTAS_STATE = "filter_notas_state";

    private RelatorioNotas relatorio;

    private RelatorioNotasItensAdapter itensAdapter;
    private RecyclerView itensRecyclerView;


    public RelatorioNotasDetailItensFragment() { }

    public static RelatorioNotasDetailItensFragment newInstance(RelatorioNotas relatorioNotas) {
        RelatorioNotasDetailItensFragment frag = new RelatorioNotasDetailItensFragment();
        frag.relatorio = relatorioNotas;
        return frag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_NOTAS_STATE, relatorio);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_relatorio_notas_detail_sku, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_NOTAS_STATE)) {
            relatorio = (RelatorioNotas) savedInstanceState.getSerializable(KEY_NOTAS_STATE);
        }

        itensRecyclerView = view.findViewById(R.id.relatorio_nota_detail_sku_recycler_view);

        itensRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(itensRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        itensRecyclerView.addItemDecoration(divider);

        itensAdapter = new RelatorioNotasItensAdapter(relatorio.getSku());

        itensRecyclerView.setAdapter(itensAdapter);

        return view;
    }

    @Override
    public void onPageSelected(int position) { }
}
