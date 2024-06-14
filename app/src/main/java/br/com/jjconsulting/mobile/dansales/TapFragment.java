package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class TapFragment extends Fragment implements View.OnClickListener {

    private Button mETapListaButton;
    private Button mETapFinancasPedidoButton;
    private Button mETapConsultarButton;

    public TapFragment() {
        // Required empty public constructor
    }

    public static TapFragment newInstance() {
        return new TapFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_etap, container, false);
        mETapListaButton = view.findViewById(R.id.etap_lista_button);
        mETapListaButton.setOnClickListener(this);
        mETapFinancasPedidoButton = view.findViewById(R.id.etap_financas_button);
        mETapFinancasPedidoButton.setOnClickListener(this);
        mETapConsultarButton = view.findViewById(R.id.etap_consultar_button);
        mETapConsultarButton.setOnClickListener(this);

        Perfil perfil = Current.getInstance(getContext()).getUsuario().getPerfil();

        if (perfil.isPermiteModuloETap()) {
            mETapListaButton.setVisibility(View.VISIBLE);
        } else {
            mETapListaButton.setVisibility(View.GONE);
        }

        if (perfil.isPermiteEtapAnFin()) {
            mETapFinancasPedidoButton.setVisibility(View.VISIBLE);
        } else {
            mETapFinancasPedidoButton.setVisibility(View.GONE);
        }

        if (perfil.isPermiteEtapContrInt()) {
            mETapConsultarButton.setVisibility(View.VISIBLE);
        } else {
            mETapConsultarButton.setVisibility(View.GONE);
        }

        return view;
    }


    @Override
    public void onClick(View view) {
        TapActionType tapActionType;
        Intent it = null;

        switch (view.getId()) {
            case R.id.etap_lista_button:
                it = TapListActivity.newIntent(getContext(), TapActionType.TAP_LIST);
                break;
            case R.id.etap_financas_button:
                it = TapListActivity.newIntent(getContext(), TapActionType.TAP_ANALISE_FINANCEIRA);
                break;
            case R.id.etap_consultar_button:
                it = TapListActivity.newIntent(getContext(), TapActionType.TAP_CONSULTA);
                break;
        }

        if(it != null){
            startActivity(it);
        }
    }
}
