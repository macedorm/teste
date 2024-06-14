package br.com.jjconsulting.mobile.dansales;

import android.graphics.Canvas;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.adapter.ResumoValidationAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.GetSortimentoAsyncTask;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.data.GetSortimentoAsyncTaskParameters;
import br.com.jjconsulting.mobile.dansales.data.ResumoSortimento;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TPositivacaoStatus;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PedidoResumoFragment extends PedidoBaseFragment
        implements OnPageSelected, GetSortimentoAsyncTask.OnPostExecuteListener {

    private PedidoBusiness mPedidoBusiness;
    private SortimentoDao mSortimentoDao;

    private TextView mResumoTextView;
    private TextView mResumoStatusPedidoTextView;
    private TextView mResumoDataPedidoTextView;
    private TextView mResumoDataImportSAPTextView;
    private TextView mResumoPesoMinimoTextView;
    private TextView mResumoPesoMaximoTextView;
    private TextView mResumoValorMinimoTextView;
    private TextView mResumoPesoPedidoTextView;
    private TextView mResumoQtdPedidoTextView;
    private TextView mResumoQtdItensTextView;
    private TextView mResumoValorTextView;
    private TextView mSortimentoObrigatorioTextView;
    private TextView mSortimentoRecomendadoTextView;
    private TextView mSortimentoInovacaoTextView;
    private TextView mPositivadoTextView;
    private TextView mTitleSortimentoTextView;

    private ProgressBar mResumoPesoMinimoProgressBar;
    private ProgressBar mResumoPesoMaximoProgressBar;
    private ProgressBar mResumoValorMinimoProgressBar;
    private ProgressBar mSortimentoObrigatorioProgressBar;
    private ProgressBar mSortimentoRecomendadoProgressBar;
    private ProgressBar mSortimentoInovacaoProgressBar;


    private LinearLayout mResumoValorMinimoLinearLayout;
    private LinearLayout mResumoPesoMinimoLinearLayout;
    private LinearLayout mResumoPesoMaximoLinearLayout;
    private LinearLayout mResumeValidationLinearLayout;
    private LinearLayout mResumoDataImportacaoSapLinearLayout;


    private ViewGroup mSortimentoObrigatorioViewGroup;
    private ViewGroup mSortimentoInovacaoViewGroup;
    private ViewGroup mSortimentoRecomendadoViewGroup;
    private ViewGroup mPositivadoViewGroup;

    private ResumoValidationAdapter mResumoValidationAdapter;

    private RecyclerView mResumoValidationRecyclerView;

    public static PedidoResumoFragment newInstance() {
        return new PedidoResumoFragment();
    }

    public PedidoResumoFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_resumo, container, false);

        mResumoTextView = view.findViewById(R.id.resumo_text_view);
        mResumoStatusPedidoTextView = view.findViewById(R.id.resumo_status_pedido_text_view);
        mResumoDataPedidoTextView = view.findViewById(R.id.resumo_data_pedido_text_view);
        mResumoDataImportSAPTextView = view.findViewById(R.id.resumo_data_importacao_sap_text_view);
        mResumoPesoMinimoTextView = view.findViewById(R.id.resumo_peso_minimo_text_view);
        mResumoPesoMaximoTextView = view.findViewById(R.id.resumo_peso_max_text_view);
        mResumoValorMinimoTextView = view.findViewById(R.id.resumo_valor_minimo_text_view);
        mResumoPesoPedidoTextView = view.findViewById(R.id.resumo_peso_pedido_text_view);
        mResumoQtdPedidoTextView = view.findViewById(R.id.resumo_quantidade_pedido_text_view);
        mResumoQtdItensTextView = view.findViewById(R.id.resumo_quantidade_itens_text_view);
        mResumoValorTextView = view.findViewById(R.id.resumo_valor_itens_text_view);
        mResumoPesoMinimoProgressBar = view.findViewById(R.id.resumo_peso_minimo_progress_bar);
        mResumoPesoMaximoProgressBar = view.findViewById(R.id.resumo_peso_max_progress_bar);
        mResumoValorMinimoProgressBar = view.findViewById(R.id.resumo_valor_minimo_progress_bar);
        mResumoValorMinimoLinearLayout = view.findViewById(R.id.resumo_valor_minimo_linear_layout);
        mResumoPesoMinimoLinearLayout = view.findViewById(R.id.resumo_peso_minimo_linear_layout);
        mResumoPesoMaximoLinearLayout = view.findViewById(R.id.resumo_peso_maximo_linear_layout);
        mResumeValidationLinearLayout = view.findViewById(R.id.resumo_validation_linear_layout);
        mResumoDataImportacaoSapLinearLayout = view.findViewById(R.id.resumo_data_importacao_sap_linear_layout);
        mTitleSortimentoTextView = view.findViewById(R.id.resumo_title_sortimento_linear_layout);

        mSortimentoObrigatorioViewGroup = view.findViewById(R.id.resumo_sortimento_linear_layout);
        mSortimentoObrigatorioTextView = view.findViewById(R.id.resumo_sortimento_text_view);
        mSortimentoObrigatorioProgressBar = view.findViewById(R.id.resumo_sortimento_progress_bar);

        mSortimentoInovacaoViewGroup = view.findViewById(R.id.resumo_sortimento_inovacao_linear_layout);
        mSortimentoInovacaoTextView = view.findViewById(R.id.resumo_sortimento_inovacao_text_view);
        mSortimentoInovacaoProgressBar = view.findViewById(R.id.resumo_sortimento_inovacao_progress_bar);

        mSortimentoRecomendadoViewGroup = view.findViewById(R.id.resumo_sortimento_recomendado_linear_layout);
        mSortimentoRecomendadoTextView = view.findViewById(R.id.resumo_sortimento_recomendado_text_view);
        mSortimentoRecomendadoProgressBar = view.findViewById(R.id.resumo_sortimento_recomendado_progress_bar);

        mPositivadoViewGroup = view.findViewById(R.id.positivado_linear_layout);
        mPositivadoTextView = view.findViewById(R.id.positivado_itens_text_view);

        mResumoValidationRecyclerView = view.findViewById(R.id.resumo_validation_recycler_view);

        mResumoValidationRecyclerView.setHasFixedSize(true);
        mResumoValidationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mResumoValidationRecyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL) {
                    @Override
                    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                    }
                });

        mPedidoBusiness = new PedidoBusiness();
        mSortimentoDao = new SortimentoDao(getActivity());

        return view;
    }

    @Override
    public void onPageSelected(int position) {
        Pedido pedido = getCurrentPedido();
        ArrayList<ItemPedido> itens = getCurrentItens();
        validation(pedido, itens);
        bindPedido(pedido, itens);
    }

    private void validation(Pedido pedido, ArrayList<ItemPedido> itens) {
        if (isEditMode()) {
            mResumoValidationAdapter = new ResumoValidationAdapter(getActivity(),
                    new ValidationDan());
            mResumoValidationRecyclerView.setAdapter(mResumoValidationAdapter);
            mResumoValidationAdapter.addLoadingFooter();

            PedidoBusiness.OnAsyncResponse anAsyncResponse = objects -> {
                mResumoValidationAdapter = new ResumoValidationAdapter(getActivity(), objects);
                mResumoValidationRecyclerView.setAdapter(mResumoValidationAdapter);
            };

            mPedidoBusiness.executeValidadePedido(getContext(), pedido, itens, anAsyncResponse);
        } else {
            mResumeValidationLinearLayout.setVisibility(View.GONE);
        }
    }

    private void bindPedido(Pedido pedido, ArrayList<ItemPedido> itens) {
        int totalItens = 0;
        double valorTotal = 0;

        int qtdItens = 0;

        if (itens != null) {
            for (ItemPedido item : itens) {
                if(item != null && item.getQuantidade() > 0){
                    totalItens += item.getQuantidade();
                    valorTotal += item.getValorTotal();
                    qtdItens++;
                }
            }
        }

        if (itens != null) {
            mResumoQtdItensTextView.setText(String.valueOf(qtdItens));
        } else {
            mResumoQtdItensTextView.setText("0");
        }

        mResumoStatusPedidoTextView.setText(pedido.getStatus().getNome());
        mResumoDataPedidoTextView.setText(FormatUtils.toDefaultDateAndHourFormat(getContext(),
                pedido.getDataCadastro()));

        if (pedido.getDataImportacaoSAP() != null) {
            mResumoDataImportSAPTextView.setText(FormatUtils.toDefaultDateAndHourFormat(
                    getContext(), pedido.getDataImportacaoSAP()));
        } else {
            mResumoDataImportSAPTextView.setText("-");
        }

        mResumoQtdPedidoTextView.setText(String.valueOf(totalItens));
        mResumoValorTextView.setText(FormatUtils.toBrazilianRealCurrency(valorTotal));

        double pesoTotal = mPedidoBusiness.getPesoTotal(pedido, itens);
        mResumoPesoPedidoTextView.setText(FormatUtils.toKilogram(pesoTotal, 3));

        DecimalFormat fmt = new DecimalFormat("0.00");

        double pesoMinimo = mPedidoBusiness.getPesoMinimo(getCurrentUsuario(), pedido);
        if (pesoMinimo != 0 && isEditMode()) {
            int percentualPesoMinimo = (int) ((pesoTotal * 100) / pesoMinimo);

            if(percentualPesoMinimo > 100){
                percentualPesoMinimo = 100;
            }

            mResumoPesoMinimoProgressBar.setProgress(percentualPesoMinimo);
            setProgressBarColor(mResumoPesoMinimoProgressBar);

            mResumoPesoMinimoTextView.setText(String.format("%s %s (%s)",
                    fmt.format(pesoMinimo), getString(R.string.kg), percentualPesoMinimo + "%"));
            mResumoPesoMinimoLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mResumoPesoMinimoLinearLayout.setVisibility(View.GONE);
        }

        mResumoPesoPedidoTextView.setText(FormatUtils.toKilogram(pesoTotal, 3));

        double pesoMaximo = mPedidoBusiness.getPesoMaximo(getCurrentUsuario(), pedido);
        if (pesoMaximo != 0 && isEditMode()) {

            int percentualPesoMaximo = 0;

            if(pesoTotal <= pesoMaximo){
                percentualPesoMaximo = 100;
            } else {
                 percentualPesoMaximo = (int) (((pesoTotal * 100) / pesoMaximo)) - 100;

                if(percentualPesoMaximo == 100){
                    percentualPesoMaximo = 99;
                }
            }

            mResumoPesoMaximoProgressBar.setProgress(percentualPesoMaximo);
            setProgressBarColorPesoMax(mResumoPesoMaximoProgressBar, percentualPesoMaximo);

            mResumoPesoMaximoTextView.setText(String.format("%s %s (%s)",
                    fmt.format(pesoMaximo), getString(R.string.kg), percentualPesoMaximo + "%"));

            if(pesoMaximo == 999999999 || percentualPesoMaximo == 100){
                mResumoPesoMaximoLinearLayout.setVisibility(View.GONE);
            } else {
                mResumoPesoMaximoLinearLayout.setVisibility(View.VISIBLE);
            }
        } else {
            mResumoPesoMaximoLinearLayout.setVisibility(View.GONE);
        }


        double valorMinimo = mPedidoBusiness.getValorMinimo(getCurrentUsuario(), pedido);
        if (valorMinimo > 0 && isEditMode()) {
            int percentualValorMinimo = (int) ((valorTotal * 100) / valorMinimo);
            mResumoValorMinimoProgressBar.setProgress(percentualValorMinimo);
            setProgressBarColor(mResumoValorMinimoProgressBar);

            mResumoValorMinimoTextView.setText(String.format("%s (%s)",
                    FormatUtils.toBrazilianRealCurrency(valorMinimo), percentualValorMinimo + "%"));
            mResumoValorMinimoLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mResumoValorMinimoLinearLayout.setVisibility(View.GONE);
        }

        PerfilVenda perfilVenda = getCurrentUsuario().getPerfil().getPerfilVenda(pedido);
        if (isEditMode() && perfilVenda.isSortimentoHabilitado()) {
            GetSortimentoAsyncTaskParameters p = new GetSortimentoAsyncTaskParameters(
                    mSortimentoDao, pedido, pedido.getCodigoSortimento(), new Date());
            GetSortimentoAsyncTask task = new GetSortimentoAsyncTask(getContext(), this);
            task.execute(p);
        } else {
            mSortimentoObrigatorioViewGroup.setVisibility(View.GONE);
            mSortimentoRecomendadoViewGroup.setVisibility(View.GONE);
            mSortimentoInovacaoViewGroup.setVisibility(View.GONE);
            mTitleSortimentoTextView.setVisibility(View.GONE);
        }

        //Data Importação SAP
        mResumoDataImportacaoSapLinearLayout.setVisibility(
                perfilVenda.isExibicaoDtImpSAP() ? View.VISIBLE : View.GONE);

        if(isEditMode() || !perfilVenda.isPositivacaoHabilitado()){
            mPositivadoViewGroup.setVisibility(View.GONE);
        } else {
            if(TPositivacaoStatus.fromInteger(pedido.getIsPositivacao()) == TPositivacaoStatus.POSITIVADO){
                mPositivadoTextView.setText(getString(R.string.yes));
            } else {
                mPositivadoTextView.setText(getString(R.string.no));
            }
        }
    }

    @Override
    public void onPostExecute(ResumoSortimento resumoSortimento) {
        boolean possuiResumo = resumoSortimento != null
                && (resumoSortimento.getQuantidadeObrigatorioTotal() > 0
                || resumoSortimento.getQuantidadeRecomendadaTotal() > 0 ||resumoSortimento.getQuantidadeInovacaoTotal() > 0);

        if (possuiResumo) {
            try{
                if(resumoSortimento.getQuantidadeObrigatorioInserida() <  resumoSortimento.getQuantidadeObrigatorioTotal()) {
                    int qtdObrigatorioInserido = 0;

                    SortimentoDao sortimentoDao = new SortimentoDao(getContext());
                    Pedido pedido = getCurrentPedido();
                    Usuario usuario = Current.getInstance(getContext()).getUsuario();
                    ArrayList<ItensSortimento> itens = sortimentoDao.getItensSortimentoPedido(usuario,  pedido, pedido.getCodigoSortimento(), pedido.getDataCadastro());
                    ArrayList<ItemPedido> itensPedido = getCurrentItens();

                    PedidoBusiness pedidoBusiness = new PedidoBusiness();

                    for(ItemPedido itemPedido:itensPedido){
                        if(itemPedido.getProduto().getTipoSortimento() != null && itemPedido.getProduto().getTipoSortimento() == Produto.SORTIMENTO_TIPO_OBRIGATORIO){

                            if(itemPedido != null && itemPedido.getQuantidade() > 0){
                                qtdObrigatorioInserido++;
                            } else{
                                String codSubstituto = pedidoBusiness.getCodSubstituto(itemPedido.getProduto().getCodigo(), itens);

                                if(!TextUtils.isNullOrEmpty(codSubstituto)){
                                    for(ItemPedido itemSubstituto:itensPedido){
                                        if(itemSubstituto != null && itemSubstituto.getProduto().getCodigo().equals(codSubstituto)){
                                            if(itemSubstituto.getQuantidade() > 0){
                                                qtdObrigatorioInserido++;
                                            }

                                            break;
                                        }
                                    }
                                }

                            }
                        }
                    }

                    resumoSortimento.setQuantidadeObrigatorioInserida(qtdObrigatorioInserido);

                }

            } catch (Exception ex){
                LogUser.log(Config.TAG, ex.toString());
            }

            int percentualObrigatorio = (int) resumoSortimento.getPercentualSortimentoObrigatorio();


            String sortimentoObrigatorio = String.format("%d%% %s (%d/%d)",
                    percentualObrigatorio,
                    getString(R.string.resumo_sortimento_obrigatorio),
                    resumoSortimento.getQuantidadeObrigatorioInserida(),
                    resumoSortimento.getQuantidadeObrigatorioTotal());
            mSortimentoObrigatorioTextView.setText(sortimentoObrigatorio);
            mSortimentoObrigatorioProgressBar.setProgress(percentualObrigatorio);
            setSortimentoProgressBarColor(mSortimentoObrigatorioProgressBar);

            int percentualRecomendado = (int) resumoSortimento.getPercentualSortimentoRecomendado();
            String sortimentoRecomendado = String.format("%d%% %s (%d/%d)",
                    percentualRecomendado,
                    getString(R.string.resumo_sortimento_recomendado),
                    resumoSortimento.getQuantidadeRecomendadaInserida(),
                    resumoSortimento.getQuantidadeRecomendadaTotal());

            mSortimentoRecomendadoTextView.setText(sortimentoRecomendado);
            mSortimentoRecomendadoProgressBar.setProgress(percentualRecomendado);
            setSortimentoProgressBarColor(mSortimentoRecomendadoProgressBar);

            int percentualInovacao = (int) resumoSortimento.getPercentualSortimentoInovacao();
            String sortimentoInovacao = String.format("%d%% %s (%d/%d)", percentualInovacao,
                    getString(R.string.resumo_sortimento_inovacao),
                    resumoSortimento.getQuantidadeInovacaoInserida(),
                    resumoSortimento.getQuantidadeInovacaoTotal());
            mSortimentoInovacaoTextView.setText(sortimentoInovacao);
            mSortimentoInovacaoProgressBar.setProgress(percentualInovacao);
            setSortimentoProgressBarColor(mSortimentoInovacaoProgressBar);
        }

        mSortimentoObrigatorioViewGroup.setVisibility(possuiResumo ? View.VISIBLE : View.GONE);
        mSortimentoRecomendadoViewGroup.setVisibility(possuiResumo ? View.VISIBLE : View.GONE);
        mSortimentoInovacaoViewGroup.setVisibility(possuiResumo ? View.VISIBLE : View.GONE);
        mTitleSortimentoTextView.setVisibility(possuiResumo ? View.VISIBLE : View.GONE);
    }

    /**
     * Rules: <= 50% red; <= 90% yellow; <= 100% green.
     * @param progressBar
     */
    private void setProgressBarColor(ProgressBar progressBar) {
        try {
            if (isDetached() || getActivity() == null) {
                return;
            }

            int progress = progressBar.getProgress();
            int color;

            if (progress <= 50) {
                color = R.color.progress_bad;
            } else if (progress <= 90) {
                color = R.color.progress_good;
            } else {
                color = R.color.progress_very_good;
            }

            progressBar.getProgressDrawable().setColorFilter(
                    getResources().getColor(color), android.graphics.PorterDuff.Mode.SRC_IN);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void setProgressBarColorPesoMax(ProgressBar progressBar, int percentualPesoMaximo) {
        try {
            if (isDetached() || getActivity() == null) {
                return;
            }

            int color;

            if (percentualPesoMaximo != 100) {
                color = R.color.progress_bad;
            } else {
                color = R.color.progress_very_good;
            }

            progressBar.getProgressDrawable().setColorFilter(
                    getResources().getColor(color), android.graphics.PorterDuff.Mode.SRC_IN);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    /**
     * Rules: <= 0% red; <= 99% yellow; <= 100% green.
     * @param progressBar
     */
    private void setSortimentoProgressBarColor(ProgressBar progressBar) {
        try {
            if (isDetached() || getActivity() == null) {
                return;
            }

            int progress = progressBar.getProgress();
            int color;

            if (progress <= 0) {
                color = R.color.progress_bad;
            } else if (progress <= 99) {
                color = R.color.progress_good;
            } else {
                color = R.color.progress_very_good;
            }

            progressBar.getProgressDrawable().setColorFilter(
                    getResources().getColor(color), android.graphics.PorterDuff.Mode.SRC_IN);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }
}
