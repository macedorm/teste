package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class RelatorioNotasDetailCabecalhoFragment extends BaseFragment implements OnPageSelected {

    private static final String KEY_NOTAS_STATE = "filter_notas_state";

    private RelatorioNotas relatorio;

    public RelatorioNotasDetailCabecalhoFragment() { }

    public static RelatorioNotasDetailCabecalhoFragment newInstance(RelatorioNotas relatorioNotas) {
        RelatorioNotasDetailCabecalhoFragment frag = new RelatorioNotasDetailCabecalhoFragment();
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
        View view = inflater.inflate(R.layout.fragment_relatorio_notas_detail_cabecalho, container, false);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_NOTAS_STATE)) {
            relatorio = (RelatorioNotas) savedInstanceState.getSerializable(KEY_NOTAS_STATE);
        }

        TextView clienteTextView = view.findViewById(R.id.relatorio_notas_cliente_text_view);
        TextView cnpjTextView = view.findViewById(R.id.relatorio_notas_cnpj_text_view);
        TextView sapTextView = view.findViewById(R.id.relatorio_notas_sap_text_view);
        TextView numeroTextView = view.findViewById(R.id.relatorio_notas_numero_text_view);
        TextView serieTextView = view.findViewById(R.id.relatorio_notas_serie_text_view);
        TextView pedidoTextView = view.findViewById(R.id.relatorio_notas_pedido_text_view);
        TextView valorTotalTextView = view.findViewById(R.id.relatorio_notas_valor_total_text_view);
        TextView icmsTextView = view.findViewById(R.id.relatorio_notas_icms_text_view);
        TextView condicaoPagamentoTextView = view.findViewById(R.id.relatorio_notas_cond_text_view);

        clienteTextView.setText(relatorio.getNome());
        cnpjTextView.setText(FormatUtils.maskCNPJ(relatorio.getCnpj()));
        sapTextView.setText(relatorio.getSap() == null ? getString(R.string.null_field) : relatorio.getSap());
        numeroTextView.setText(relatorio.getNumero());
        serieTextView.setText(relatorio.getSerie());
        pedidoTextView.setText(relatorio.getNumeroPedido());
        valorTotalTextView.setText(FormatUtils.toBrazilianRealCurrency(relatorio.getValor()));
        icmsTextView.setText(FormatUtils.toBrazilianRealCurrency(relatorio.getIcmsTotal()));
        condicaoPagamentoTextView.setText(relatorio.getCondicaoPagamento());

        return view;
    }

    @Override
    public void onPageSelected(int position) {

    }
}
