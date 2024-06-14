package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public class RelatoriosFragment extends Fragment implements View.OnClickListener {

    private Button mRelatorioObjetivoButton;
    private Button mRelatorioCarteiraPedidoButton;
    private Button mRelatorioNotasButton;
    private Button mRelatorioSaldoMasterContratoButton;
    private Button mRelatorioPositivacaoButton;
    private Button mRelatorioCheckListNotaButton;

    public RelatoriosFragment() {
        // Required empty public constructor
    }

    public static RelatoriosFragment newInstance() {
        return new RelatoriosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_relatorios, container, false);
        mRelatorioObjetivoButton = view.findViewById(R.id.objetivo_button);
        mRelatorioObjetivoButton.setOnClickListener(this);
        mRelatorioCarteiraPedidoButton = view.findViewById(R.id.carteira_pedidos_button);
        mRelatorioCarteiraPedidoButton.setOnClickListener(this);
        mRelatorioNotasButton = view.findViewById(R.id.notas_button);
        mRelatorioNotasButton.setOnClickListener(this);
        mRelatorioSaldoMasterContratoButton = view.findViewById(R.id.saldo_master_contrato_button);
        mRelatorioSaldoMasterContratoButton.setOnClickListener(this);
        mRelatorioPositivacaoButton = view.findViewById(R.id.positivacao_button);
        mRelatorioPositivacaoButton.setOnClickListener(this);
        mRelatorioCheckListNotaButton = view.findViewById(R.id.checklist_nota_button);
        mRelatorioCheckListNotaButton.setOnClickListener(this);

        Perfil perfil = Current.getInstance(getContext()).getUsuario().getPerfil();
        if (perfil.isRelatorioObjetivoRafHabilitado()) {
            mRelatorioObjetivoButton.setVisibility(View.VISIBLE);
        } else {
            mRelatorioObjetivoButton.setVisibility(View.GONE);
        }

        if (perfil.isRelatorioCarteiraPedidosHabilitado()) {
            mRelatorioCarteiraPedidoButton.setVisibility(View.VISIBLE);
        } else {
            mRelatorioCarteiraPedidoButton.setVisibility(View.GONE);
        }

        if (perfil.isRelatorioHistoricoNotasHabilitado()) {
            mRelatorioNotasButton.setVisibility(View.VISIBLE);
        } else {
            mRelatorioNotasButton.setVisibility(View.GONE);
        }

        if (perfil.isRelatorioETapSaldoMC()) {
            mRelatorioSaldoMasterContratoButton.setVisibility(View.VISIBLE);
        } else {
            mRelatorioSaldoMasterContratoButton.setVisibility(View.GONE);
        }

        if (perfil.isPermiteRelPositivacao()) {
            mRelatorioPositivacaoButton.setVisibility(View.VISIBLE);
        } else {
            mRelatorioPositivacaoButton.setVisibility(View.GONE);
        }

        if (perfil.isPermiteRelatorioChecklist()) {
            mRelatorioCheckListNotaButton.setVisibility(View.VISIBLE);
        } else {
            mRelatorioCheckListNotaButton.setVisibility(View.GONE);
        }

        return view;
    }



    @Override
    public void onClick(View view) {
        Intent it = null;
        switch (view.getId()) {
            case R.id.objetivo_button:
                it = new Intent(getActivity(), RelatorioObjetivoActivity.class);
                startActivity(it);
                break;
            case R.id.carteira_pedidos_button:
                it = new Intent(getActivity(), RelatorioCarteiraPedidosActivity.class);
                startActivity(it);
                break;
            case R.id.notas_button:
                it = new Intent(getActivity(), RelatorioNotasActivity.class);
                startActivity(it);
                break;
            case R.id.saldo_master_contrato_button:
                it = new Intent(getActivity(), TapRelatorioSaldoMCActivity.class);
                startActivity(it);
                break;
            case R.id.positivacao_button:
                if(UsuarioUtils.isPromGaGr(Current.getInstance(getContext()).getUsuario().getCodigoFuncao())){
                    it = new Intent(getActivity(), RelatorioPositivacaoActivity.class);
                    startActivity(it);
                } else {
                    DialogsCustom dialogsDefault = new DialogsCustom(getActivity());
                    dialogsDefault.showDialogMessage(getString(R.string.relatorio_positivacao_tipo_usuario_erro), dialogsDefault.DIALOG_TYPE_ERROR, null);
                }
                break;
            case R.id.checklist_nota_button:
                it = new Intent(getActivity(), RelatorioChecklistNotasActivity.class);
                startActivity(it);
                break;
        }
    }
}
