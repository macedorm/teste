package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.TapItem;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;

public class TapResumoFragment extends TapBaseFragment
        implements OnPageSelected {

    private TextView mResumoQtdAcoesTextView;
    private TextView mResumoValorEstimadoTextView;
    private TextView mResumoValorApuradoTextView;
    private TextView mResumoFatLiqTextView;

    public static TapResumoFragment newInstance() {
        return new TapResumoFragment();
    }

    public TapResumoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tap_resumo, container, false);

        mResumoQtdAcoesTextView = view.findViewById(R.id.etap_resumo_qtd_acoes_text_view);
        mResumoValorEstimadoTextView = view.findViewById(R.id.etap_resumo_valor_estimado_text_view);
        mResumoValorApuradoTextView = view.findViewById(R.id.etap_resumo_valor_total_apurado_text_view);
        mResumoFatLiqTextView = view.findViewById(R.id.etap_resumo_por_fat_liq_text_view);

        return view;
    }

    @Override
    public void onPageSelected(int position) {
        List<TapItem> itemTap = getCurrentTap().getItens();
        bindItem(itemTap);
    }

    private void bindItem(List<TapItem> itemTap) {
        if (itemTap != null && itemTap.size() > 0) {
            mResumoQtdAcoesTextView.setText(String.valueOf(itemTap.size()));
        }

        int valorTotalApurado = 0;
        int valorTotalEstimado = 0;
        double porFatLiq;
        double fatLiq = getCurrentTap().getCabec().getFatLiq();

        for (TapItem item : itemTap) {
            valorTotalApurado += item.getVlApur();
            valorTotalEstimado += item.getVlEst();
        }

        if (fatLiq == 0) {
            porFatLiq = 0;
        } else {
            if (valorTotalEstimado > 0) {
                porFatLiq = (valorTotalEstimado / fatLiq) * 100;
            } else {
                porFatLiq = (valorTotalApurado / fatLiq) * 100;
            }
        }

        mResumoValorApuradoTextView.setText(String.valueOf(FormatUtils.toDoubleFormat(valorTotalApurado)));
        mResumoValorEstimadoTextView.setText(String.valueOf(FormatUtils.toDoubleFormat(valorTotalEstimado)));
        mResumoFatLiqTextView.setText(String.valueOf(FormatUtils.toDoubleFormat(porFatLiq)));
    }
}
