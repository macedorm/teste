package br.com.jjconsulting.mobile.dansales;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskItensSortimento;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.ClienteEntregaDao;
import br.com.jjconsulting.mobile.dansales.database.CondicaoPagamentoDao;
import br.com.jjconsulting.mobile.dansales.database.GrupoDao;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.database.PlanoCampoDao;
import br.com.jjconsulting.mobile.dansales.database.PlantaDao;
import br.com.jjconsulting.mobile.dansales.database.TipoVendaDao;
import br.com.jjconsulting.mobile.dansales.database.UnidadeMedidaDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.CondicaoPagamento;
import br.com.jjconsulting.mobile.dansales.model.Grupo;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.ClienteEntrega;
import br.com.jjconsulting.mobile.dansales.model.OrigemPedido;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoBonificado;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.Planta;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.model.UnidadeMedida;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.CustomTextInputLayout;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextWatcherUtils;

public class PedidoCabecalhoFragment extends PedidoBaseFragment implements OnPageSelected {

    private PedidoBusiness mPedidoBusiness;

    private ArrayList<TipoVenda> tipoVendaArray;
    private ArrayList<UnidadeMedida> unidadeMedidaArray;
    private ArrayList<PedidoBonificado> pedidoVendaArray;
    private ArrayList<Grupo> grupoVendaArray;
    private ArrayList<Planta> plantaVendaArray;
    private ArrayList<CondicaoPagamento> condPagArray;
    private ArrayList<ClienteEntrega> localEntregaArray;

    private DatePickerDialog.OnDateSetListener mEntregueEmDateSetListener;
    private TextWatcher mEmpenhoTextWatcher;
    private TextWatcher mOrdemCompraTextWatcher;

    private SpinnerArrayAdapter<TipoVenda> mTipoVendaSpinnerAdapter;
    private SpinnerArrayAdapter<UnidadeMedida> mUnidadeMedidaSpinnerAdapter;
    private SpinnerArrayAdapter<PedidoBonificado> mPedidoVendaSpinnerAdapter;
    private SpinnerArrayAdapter<Grupo> mGrupoSpinnerAdapter;
    private SpinnerArrayAdapter<Planta> mPlantaSpinnerAdapter;
    private SpinnerArrayAdapter<CondicaoPagamento> mCondPagSpinnerAdapter;
    private SpinnerArrayAdapter<String> mUrgenteSpinnerAdapter;
    private SpinnerArrayAdapter<ClienteEntrega> mLocalEntregaSpinnerAdapter;

    private DatePickerDialog mEntregueEmdatePickerDialog;
    private TextView mAgendaTextView;
    private TextView mClienteCodigoTextView;
    private TextView mClienteNomeTextView;
    private TextView mEntregaEmTextView;
    private EditText mOrdemCompraEditText;
    private EditText mEmpenhoEditText;
    private CustomTextInputLayout mOrdemCompraTextInputLayout;
    private CustomTextInputLayout mEmpenhoTextInputLayout;
    private ImageView mEntregarEmImageView;
    private ViewGroup mClienteFormWrapper;
    private ViewGroup mPedidoVendaFormWrapper;
    private ViewGroup mGrupoFormWrapper;
    private ViewGroup mAgendaFormWrapper;
    private ViewGroup mPlantaFormWrapper;
    private ViewGroup mUrgenteFormWrapper;
    private ViewGroup mCondicaoPagamentoFormWrapper;
    private ViewGroup mLocalEntregaFormWrapper;
    private Spinner mTipoVendSpinner;
    private Spinner mUnidadeMedidaSpinner;
    private Spinner mPedidoVendaSpinner;
    private Spinner mGrupoSpinner;
    private Spinner mPlantaSpinner;
    private Spinner mCondPagSpinner;
    private Spinner mUrgenteSpinner;
    private Spinner mLocalEntregaSpinner;
    private LinearLayout mEntregaEmLinearLayout;

    private boolean userSelectTpVend;
    private boolean userSelectTpUnMed;

    private ProgressDialog progressDialog;

    public PedidoCabecalhoFragment() {
    }

    public static PedidoCabecalhoFragment newInstance() {
        return new PedidoCabecalhoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_cabecalho, container,
                false);

        mEntregaEmLinearLayout = view.findViewById(R.id.pedido_data_linearlayout);
        mEntregaEmTextView = view.findViewById(R.id.pedido_data_text_view);
        mEmpenhoEditText = view.findViewById(R.id.empenho_pedido_edit_text);
        mAgendaTextView = view.findViewById(R.id.pedido_agenda_text_view);
        mClienteCodigoTextView = view.findViewById(R.id.pedido_cliente_codigo_text_view);
        mClienteNomeTextView = view.findViewById(R.id.pedido_cliente_nome_text_view);
        mOrdemCompraEditText = view.findViewById(R.id.pedido_detail_ordem_compra_edit_text);
        mOrdemCompraTextInputLayout = view.findViewById(R.id.pedido_detail_ordem_compra_text_input_layout);
        mEmpenhoTextInputLayout = view.findViewById(R.id.empenho_pedido_text_input_layout);
        mEntregarEmImageView = view.findViewById(R.id.pedido_data_image_view);
        mTipoVendSpinner = view.findViewById(R.id.tipo_venda_spinner);
        mUnidadeMedidaSpinner = view.findViewById(R.id.unidade_medida_spinner);
        mPedidoVendaSpinner = view.findViewById(R.id.pedido_venda_spinner);
        mGrupoSpinner = view.findViewById(R.id.grupo_spinner);
        mPlantaSpinner = view.findViewById(R.id.planta_spinner);
        mCondPagSpinner = view.findViewById(R.id.cond_pag_spinner);
        mUrgenteSpinner = view.findViewById(R.id.urgente_spinner);
        mLocalEntregaSpinner = view.findViewById(R.id.local_entrega_spinner);
        mClienteFormWrapper = view.findViewById(R.id.cliente_form_wrapper);
        mPedidoVendaFormWrapper = view.findViewById(R.id.pedido_venda_form_wrapper);
        mGrupoFormWrapper = view.findViewById(R.id.grupo_form_wrapper);
        mAgendaFormWrapper = view.findViewById(R.id.pedido_agenda_form_wrapper);
        mPlantaFormWrapper = view.findViewById(R.id.planta_form_wrapper);
        mUrgenteFormWrapper = view.findViewById(R.id.urgente_form_wrapper);
        mLocalEntregaFormWrapper = view.findViewById(R.id.local_entrega_form_wrapper);
        mCondicaoPagamentoFormWrapper = view.findViewById(R.id.cond_pag_form_wrapper);

        createDialogProgress();

        mPedidoBusiness = new PedidoBusiness();

        //Try catch para garantir que caso o device, por falta de memoria, limpe da memória o pedido
        //não crash o aplicativo e volte para a tela anteriorh
        try {
            if (getCurrentPedido() == null) {
                getActivity().finish();
            } else {
                // poputate spinners
                setupTipoVenda(getCurrentPedido());
                configSpinnersDepTipoVenda();
                setupPedidoVenda(getCurrentPedido());
                setupGrupo(getCurrentPedido());
                setupUrgente(getCurrentPedido());
                setupLocalEntrega(getCurrentPedido());

                // bind values
                bindPedido(getCurrentPedido());

                // enable disable and hide fields
                enableFileds(isEditMode());
                showHideFields(getCurrentPedido());

                mUrgenteSpinner.post(() -> addListeners());
            }
        }catch (Exception ex){
            getActivity().finish();
        }

        return view;
    }

    @Override
    public void onPageSelected(int position) {
        enableFileds(isEditMode());
    }

    private void bindPedido(Pedido pedido) {
        mAgendaTextView.setText(pedido.getCodigoAgenda());
        mClienteCodigoTextView.setText(pedido.getCliente().getCodigo());
        mClienteNomeTextView.setText(pedido.getCliente().getNome());

        if (TextUtils.isNullOrEmpty(pedido.getOrdemCompra()) && !isEditMode()) {
            mOrdemCompraEditText.setText(getString(R.string.empty));
        } else {
            mOrdemCompraEditText.setText(pedido.getOrdemCompra());
        }

        if (TextUtils.isNullOrEmpty(pedido.getEmpenho()) && !isEditMode()) {
            mEmpenhoEditText.setText(getString(R.string.empty));
        } else {
            mEmpenhoEditText.setText(pedido.getEmpenho());
        }


        if (!mClienteFormWrapper.hasOnClickListeners()) {
            mClienteFormWrapper.setOnClickListener(view -> {
                startActivity(ClienteDetailActivity.newIntent(getActivity(),
                        pedido.getCliente().getCodigo(), false));
            });
        }

        mOrdemCompraTextInputLayout.loadStyle();
        mEmpenhoTextInputLayout.loadStyle();
        mClienteNomeTextView.requestFocus();
    }

    private void addListeners() {

        mOrdemCompraTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            Pedido pedido = getCurrentPedido();
            if (pedido.getOrdemCompra() == null || !editable.toString().equals(pedido.getOrdemCompra())) {
                pedido.setOrdemCompra(editable.toString());
                setCurrentPedido(pedido);
            }
        });
        mOrdemCompraEditText.addTextChangedListener(mOrdemCompraTextWatcher);

        mEmpenhoTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            Pedido pedido = getCurrentPedido();
            if (pedido.getEmpenho() == null || !editable.toString().equals(pedido.getEmpenho())) {
                pedido.setEmpenho(editable.toString());
                setCurrentPedido(pedido);
            }
        });
        mEmpenhoEditText.addTextChangedListener(mEmpenhoTextWatcher);

        mEntregueEmDateSetListener = (DatePicker datePicker, int year, int month, int day) -> {
            Pedido pedido = getCurrentPedido();
            try {
                Date date = FormatUtils.toCreateDatePicker(year, month, day);
                mEntregaEmTextView.setText(FormatUtils.toDefaultDateFormat(getContext(), date));
                pedido.setEntregaEm(date);
                setCurrentPedido(pedido);
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        };

        createDataPicker(getCurrentPedido().getEntregaEm());

        mEntregaEmLinearLayout.setOnClickListener(view -> mEntregueEmdatePickerDialog.show());
        mTipoVendSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mTipoVendSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelectTpVend = true;

                return false;
            }
        });

        mTipoVendSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(!userSelectTpVend){
                    return;
                }

                Usuario usuario = getCurrentUsuario();
                Pedido pedido = getCurrentPedido();
                pedido.setTipoVenda(null);
                pedido.setCodigoTipoVenda(null);
                pedido.setEntregaEm(null);
                pedido.setCodigoPlanta(pedido.getCliente().getPlanta());
                pedido.setUnidadeMedida(mPedidoBusiness.getUnMedidaPadrao(usuario, pedido));
                pedido.setCodigoCondPag(pedido.getCliente().getCondicaoPagamento().getCodigo());

                if (i > 0) {
                    TipoVenda tipoVenda = tipoVendaArray.get(i - 1);

                    if (tipoVenda != null && tipoVenda.getCodigo() != null) {
                        pedido.setTipoVenda(tipoVenda);
                        pedido.setCodigoTipoVenda(tipoVenda.getCodigo());
                        pedido.setUnidadeMedida(mPedidoBusiness.getUnMedidaPadrao(
                                getCurrentUsuario(), pedido));

                        if(pedido.getCliente().getStatusCredito() == Cliente.STATUS_CREDITO_BLACK && !getCurrentPerfilVenda().isPermitePedidoCliCredBloq()){
                            mTipoVendSpinner.setSelection(0);
                            dialogsDefault.showDialogMessage(getString(R.string.detail_pedido_tp_venda_bloq), dialogsDefault.DIALOG_TYPE_WARNING, null);
                            pedido.setTipoVenda(null);
                            pedido.setCodigoTipoVenda(null);
                            pedido.setEntregaEm(null);
                            pedido.setCodigoPlanta(pedido.getCliente().getPlanta());
                            pedido.setUnidadeMedida(mPedidoBusiness.getUnMedidaPadrao(getCurrentUsuario(),
                                    pedido));
                            pedido.setCodigoCondPag(pedido.getCliente().getCondicaoPagamento().getCodigo());
                            return;
                        }

                        setCurrentPedido(pedido);
                    }
                } else {
                    setCurrentPedido(pedido);
                }

                if(getCurrentPerfilVenda().isSortimentoHabilitado()){
                    if(getCurrentItens().isEmpty()){
                        Perfil perfil = usuario.getPerfil();
                        if ((perfil.isPermiteRotaGuiada() && UsuarioUtils.isPromotor(usuario.getCodigoFuncao()))) {
                            AsyncTaskItensSortimento asyncTaskItensSortimento = new AsyncTaskItensSortimento(getContext(), usuario, pedido, new AsyncTaskItensSortimento.OnAsyncResponse() {
                                @Override
                                public void processFinish(ArrayList<ItemPedido> objects) {
                                    setCurrentItens(objects);
                                    CurrentActionPedido.getInstance().setUpdateListItem(true);

                                    enableFileds(isEditMode());

                                    if (getActivity().getWindow().getDecorView().isShown()) {
                                        if(progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                        }
                                    }
                                }
                            });

                            progressDialog.show();
                            asyncTaskItensSortimento.execute();
                        }
                    }
                }


                configSpinnersDepTipoVenda();
                enableFileds(isEditMode());
                showHideFields(getCurrentPedido());

                if (isEditMode()) {
                    mPedidoBusiness.updatePedido(getActivity(), getCurrentPedido(), null,
                            null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mPedidoVendaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pedido pedido = getCurrentPedido();

                if (i > 0) {
                    if (!pedidoVendaArray.get(i - 1).equals(pedido.getCodigoPedidoVenda())) {
                        PedidoBonificado pedidoBonificado = pedidoVendaArray.get(i - 1);
                        pedido.setCodigoPedidoVenda(pedidoBonificado.getCodigo());
                        pedido.setEntregaEm(pedidoBonificado.getEntregueEm());
                        pedido.setCodigoCondPag(pedidoBonificado.getCodigoCondPag());
                        pedido.setUnidadeMedida(pedidoBonificado.getUnidadeMedida());
                        mEntregaEmTextView.setText(FormatUtils.toDefaultDateFormat(getContext(), pedido.getEntregaEm()));
                        setUnMed(pedido.getUnidadeMedida(), pedido);
                        setCondPag(pedidoBonificado.getCodigoCondPag(), pedido);
                        setCurrentPedido(pedido);

                    }
                } else {
                    pedido.setCodigoPedidoVenda(null);
                    setCurrentPedido(pedido);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mUnidadeMedidaSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelectTpUnMed = true;

                return false;
            }
        });

        mUnidadeMedidaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(!userSelectTpUnMed){
                    return;
                }

                Pedido pedido = getCurrentPedido();
                String novaUnidadeMedida = unidadeMedidaArray.get(i).getCodigo();

                if (novaUnidadeMedida.equals(pedido.getUnidadeMedida())) {
                    return;
                }

                boolean itemsUpdated = updateItemsWithNewUnidadeMedida(pedido.getUnidadeMedida(),
                        novaUnidadeMedida, getCurrentPedido().getTipoVenda());

                if (itemsUpdated) {
                    pedido.setUnidadeMedida(novaUnidadeMedida);
                    if (isEditMode()) {
                        mPedidoBusiness.updatePedido(getActivity(), pedido, null,
                                null);
                        setCurrentPedido(pedido);
                    }
                } else {
                    dialogsDefault.showDialogMessage(
                            getString(R.string.message_waring_un_med_un_to_cx),
                            dialogsDefault.DIALOG_TYPE_WARNING,
                            null);
                    setUnMed(pedido.getUnidadeMedida(), pedido);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mGrupoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pedido pedido = getCurrentPedido();
                if (i > 0) {
                    if (!grupoVendaArray.get(i - 1).getCod().equals(pedido.getCodigoGrupo())) {
                        pedido.setCodigoGrupo(grupoVendaArray.get(i - 1).getCod());
                        setCurrentPedido(pedido);
                    }
                } else {
                    pedido.setCodigoGrupo(null);
                    setCurrentPedido(pedido);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mCondPagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pedido pedido = getCurrentPedido();
                if (!condPagArray.get(i).getCodigo().equals(pedido.getCodigoCondPag())) {
                    pedido.setCodigoCondPag(condPagArray.get(i).getCodigo());
                    setCurrentPedido(pedido);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mUrgenteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pedido pedido = getCurrentPedido();
                if (i == 0) {
                    pedido.setUrgente("S");
                } else {
                    pedido.setUrgente("N");
                }
                setCurrentPedido(pedido);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mPlantaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Pedido pedido = getCurrentPedido();
                    if (!plantaVendaArray.get(i).getCodPlanta().equals(pedido.getCodigoPlanta())) {
                        pedido.setCodigoPlanta(plantaVendaArray.get(i).getCodPlanta());
                        setCurrentPedido(pedido);
                    }
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mLocalEntregaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pedido pedido = getCurrentPedido();
                if (!localEntregaArray.get(i).getLojaEntrega().equals(pedido.getLojaEntrega())) {
                    pedido.setLojaEntrega(localEntregaArray.get(i).getLojaEntrega());
                    setCurrentPedido(pedido);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void enableFileds(boolean value) {
        PerfilVenda perfilVenda = getCurrentPerfilVenda();

        if (getCurrentPedido().getTipoVenda() != null && "SUG".equals(getCurrentPedido().getTipoVenda().getCodigo())) {
            mTipoVendSpinner.setEnabled(false);
        } else {
            if (getCurrentItens() != null && getCurrentItens().size() > 0) {
                mTipoVendSpinner.setEnabled(false);
            } else {
                mTipoVendSpinner.setEnabled(value);
            }
        }

        mPedidoVendaSpinner.setEnabled(value);
        mGrupoSpinner.setEnabled(value);
        mUrgenteSpinner.setEnabled(value);
        mEntregaEmLinearLayout.setEnabled(value);
        mEmpenhoEditText.setEnabled(value);
        mOrdemCompraEditText.setEnabled(value);

        if (!value) {
            mOrdemCompraEditText.setFocusable(false);
            mEmpenhoEditText.setFocusable(false);
        }

        if (perfilVenda.isEdicaoLocalEntregaHabilitada()) {
            mLocalEntregaSpinner.setEnabled(value);
        } else {
            mLocalEntregaSpinner.setEnabled(false);
        }

        if (perfilVenda.isAlteraPlanta()) {
            mPlantaSpinner.setEnabled(value);
        } else {
            mPlantaSpinner.setEnabled(false);
        }

        if (perfilVenda.getTipoCondPag() == PerfilVenda.TIPO_COND_PAG_PERMITE_ALTERAR) {
            mCondPagSpinner.setEnabled(value);
        } else {
            mCondPagSpinner.setEnabled(false);
        }

        if (perfilVenda.isEdicaoDataEntregaHabilitada()) {
            enableEntregaEm(value);
        } else {
            enableEntregaEm(false);
        }


        mUnidadeMedidaSpinner.setEnabled(perfilVenda.isEdicaoUnidadeMedidaHabiltiada() && value);
    }

    private void showHideFields(Pedido pedido) {

        PerfilVenda perfilVenda = getCurrentPerfilVenda();

        TipoVenda tipoVenda = null;

        if (pedido.getTipoVenda() != null) {

            tipoVenda = pedido.getTipoVenda();

            //Grupo
            mGrupoFormWrapper.setVisibility(tipoVenda.getCodigo().equals("VCO") ? View.VISIBLE : View.GONE);

            //Pedido de Venda
            if (tipoVenda == null || !tipoVenda.getCodigo().equals("BON")) {
                mPedidoVendaFormWrapper.setVisibility(View.GONE);
            } else {
                mPedidoVendaFormWrapper.setVisibility(View.VISIBLE);
            }

            //Ordem de Compra
            mOrdemCompraTextInputLayout.setVisibility(!tipoVenda.getCodigo().equals("BON") ? View.VISIBLE : View.GONE);

        } else {
            //Grupo, Pedido, Investimentos e Ordem de Compra TipoVenda = null
            mGrupoFormWrapper.setVisibility(View.GONE);
            mPedidoVendaFormWrapper.setVisibility(View.GONE);
            mOrdemCompraTextInputLayout.setVisibility(View.GONE);
            mCondicaoPagamentoFormWrapper.setVisibility(View.GONE);
        }

        //Agenda
        mAgendaFormWrapper.setVisibility(
                pedido.getTipoAgenda() != Pedido.TIPO_AGENDA_NAO_POSSUI ? View.VISIBLE : View.GONE);

        //Pedido Urgente
        mUrgenteFormWrapper.setVisibility(
                perfilVenda.isMarcacaoUrgenciaHabilitada() ? View.VISIBLE : View.GONE);

        //Entregar Em
        mEntregaEmLinearLayout.setVisibility(
                perfilVenda.isExibicaoEntregarEm() ? View.VISIBLE : View.GONE);

        //Ordem compra
        mOrdemCompraTextInputLayout.setVisibility(
                perfilVenda.isExibicaoOrdemCompra() ? View.VISIBLE : View.GONE);

        if (isEditMode()) {

            //Local Entrega
            ClienteEntregaDao clienteEntregaDao = new ClienteEntregaDao(getContext());
            String unNeg = Current.getInstance(getContext()).getUnidadeNegocio().getCodigo();
            boolean isLocalEntrega = clienteEntregaDao.hasClienteEntrega(unNeg, pedido.getCliente());

            if (isLocalEntrega && perfilVenda.isEdicaoLocalEntregaHabilitada())
                mLocalEntregaFormWrapper.setVisibility(View.VISIBLE);
            else
                mLocalEntregaFormWrapper.setVisibility(View.GONE);

            //Cond Pagamento
            if (perfilVenda.getTipoCondPag() == PerfilVenda.TIPO_COND_PAG_NAO_EXIBE)
                mCondicaoPagamentoFormWrapper.setVisibility(View.GONE);
            else
                mCondicaoPagamentoFormWrapper.setVisibility(View.VISIBLE);


            //Empenho
            if (perfilVenda.isExibicaoEmpenhoHabilitada())
                mEmpenhoTextInputLayout.setVisibility(View.VISIBLE);
            else
                mEmpenhoTextInputLayout.setVisibility(View.GONE);

        } else {
            //Local Entrega
            if (pedido.getLojaEntrega() != null && pedido.getLojaEntrega().trim().length() > 0)
                mLocalEntregaFormWrapper.setVisibility(View.VISIBLE);
            else
                mLocalEntregaFormWrapper.setVisibility(View.GONE);

            //Planta
            if (pedido.getCodigoPlanta() != null && pedido.getCodigoPlanta().trim().length() > 0)
                mPlantaFormWrapper.setVisibility(View.VISIBLE);
            else
                mPlantaFormWrapper.setVisibility(View.GONE);

            //Empenho
            if (pedido.getEmpenho() != null && pedido.getEmpenho().trim().length() > 0)
                mEmpenhoTextInputLayout.setVisibility(View.VISIBLE);
            else
                mEmpenhoTextInputLayout.setVisibility(View.GONE);


            boolean isNotShowCondPag = false;

            if (perfilVenda != null && perfilVenda.getTipoCondPag() == PerfilVenda.TIPO_COND_PAG_NAO_EXIBE) {
                isNotShowCondPag = true;
            }

            //Condicao Pagamento
            if (TextUtils.isNullOrEmpty(pedido.getCodigoCondPag()) || isNotShowCondPag)
                mCondicaoPagamentoFormWrapper.setVisibility(View.GONE);
            else
                mCondicaoPagamentoFormWrapper.setVisibility(View.VISIBLE);


        }
    }

    private void configSpinnersDepTipoVenda() {
        setupDataEntrega(getCurrentPedido());
        setupPlanta(getCurrentPedido());
        setupUnidadeMedida(getCurrentPedido());
        setupCondPagamento(getCurrentPedido());
    }

    private void setupPlanta(Pedido pedido) {
        PlantaDao plantaDao = new PlantaDao(getContext());
        plantaVendaArray = new ArrayList<>();

        String codigoTipoVenda = "";

        PerfilVenda perfilVenda = getCurrentPerfilVenda();

        if (perfilVenda.isAlteraPlanta()) {
            if (pedido.getTipoVenda() != null && pedido.getTipoVenda().getCodigo() != null) {
                codigoTipoVenda = pedido.getTipoVenda().getCodigo();
            }
            plantaVendaArray = plantaDao.getPlantas(getCurrentUnidNeg().getCodigo(), codigoTipoVenda, pedido.getCodigoCliente());
        } else {
            Planta itemDefault = new Planta();
            String codigoPlanta = pedido.getCliente().getPlanta();
            itemDefault.setDescPLanta(codigoPlanta);
            itemDefault.setCodPlanta(codigoPlanta);
            plantaVendaArray.add(itemDefault);
        }

        if (plantaVendaArray.size() > 0) {
            mPlantaSpinnerAdapter = new SpinnerArrayAdapter<Planta>(
                    getContext(), plantaVendaArray.toArray(), false) {
                @Override
                public String getItemDescription(Planta item) {
                    return item.getDescPLanta();
                }
            };

            mPlantaSpinner.setAdapter(mPlantaSpinnerAdapter);
            setPlanta(pedido.getCodigoPlanta(), pedido);
        }
    }

    private void setupUnidadeMedida(Pedido pedido) {
        UnidadeMedidaDao unidadeMedidaDao = new UnidadeMedidaDao(getContext());
        boolean isAllowedToEditUnMedida = getCurrentPerfilVenda().isEdicaoUnidadeMedidaHabiltiada();

        if (!isEditMode() ||
                (isEditMode() && pedido.getTipoVenda() != null && isAllowedToEditUnMedida)) {
            unidadeMedidaArray = unidadeMedidaDao.getAll();
        } else {
            String unMedidaPadrao = mPedidoBusiness.getUnMedidaPadrao(getCurrentUsuario(), pedido);
            UnidadeMedida unMedida = unidadeMedidaDao.getUnidadeMedida(unMedidaPadrao);

            unidadeMedidaArray = new ArrayList<>();
            unidadeMedidaArray.add(unMedida);
        }

        if (unidadeMedidaArray.size() > 0) {
            mUnidadeMedidaSpinnerAdapter = new SpinnerArrayAdapter<UnidadeMedida>(getContext(),
                    unidadeMedidaArray.toArray(), false) {
                @Override
                public String getItemDescription(UnidadeMedida item) {
                    return item.getNome();
                }
            };

            mUnidadeMedidaSpinner.setAdapter(mUnidadeMedidaSpinnerAdapter);

            setUnMed(pedido.getUnidadeMedida(), pedido);
        }
    }

    private void setupTipoVenda(Pedido pedido) {
        TipoVendaDao tpVendaDao = new TipoVendaDao(getContext());
        tipoVendaArray = new ArrayList<>();

        if (!isEditMode()) {
            tipoVendaArray.add(pedido.getTipoVenda());
        } else {
            for (PerfilVenda pv : getCurrentUsuario().getPerfil().getPerfisVenda()) {
                if (!pv.isVendaHabilitada()  ||
                        !pv.getCodigoUnidadeNegocio().equals(getCurrentUnidNeg().getCodigo())) {
                    continue;
                }

                int pdTpAgenda = pedido.getTipoAgenda();
                int pvTpAgenda = pv.getTipoAgenda();

                if (pdTpAgenda == Pedido.TIPO_AGENDA_AGENDA ||
                        pdTpAgenda == Pedido.TIPO_AGENDA_AGENDA_DISTRIBUIDOR) {
                    if (pvTpAgenda == PerfilVenda.TIPO_AGENDA_PERMITIR_AMBOS ||
                            pvTpAgenda == PerfilVenda.TIPO_AGENDA_PERMITIR_SOMENTE_AGENDA) {
                        if (pv.getTipoVenda().equals("SUG")) {
                            if (pedido.getOrigem().getCodigo() == OrigemPedido.TEVEC) {
                                tipoVendaArray.add(tpVendaDao.get(pv.getTipoVenda()));
                            }
                        } else {
                            tipoVendaArray.add(tpVendaDao.get(pv.getTipoVenda()));
                        }
                    }
                } else if (pvTpAgenda != PerfilVenda.TIPO_AGENDA_PERMITIR_SOMENTE_AGENDA) {
                    if (pv.getTipoVenda().equals("SUG")) {
                        if (pedido.getOrigem().getCodigo() == OrigemPedido.TEVEC) {
                            tipoVendaArray.add(tpVendaDao.get(pv.getTipoVenda()));
                        }
                    } else {
                        if (tpVendaDao.get(pv.getTipoVenda()) != null) {
                            tipoVendaArray.add(tpVendaDao.get(pv.getTipoVenda()));
                        }
                    }
                }
            }
        }

        if (tipoVendaArray.size() > 0) {
            Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tipoVendaArray.toArray(),
                    getString(R.string.select_tipo_venda));
            mTipoVendaSpinnerAdapter = new SpinnerArrayAdapter<TipoVenda>(
                    getContext(), objects, true) {
                @Override
                public String getItemDescription(TipoVenda item) {
                    return item.getNome();
                }
            };
            mTipoVendSpinner.setAdapter(mTipoVendaSpinnerAdapter);

            setTpVend(pedido.getTipoVenda(), pedido);
        } else {
            dialogsDefault.showDialogMessage(
                    getString(R.string.permission_message),
                    dialogsDefault.DIALOG_TYPE_ERROR, () -> getActivity().finish());
        }
    }

    private void setupPedidoVenda(Pedido pedido) {
        PedidoDao pedidoDao = new PedidoDao(getContext());
        pedidoVendaArray = pedidoDao.getListPedToBon(getCurrentUsuario().getCodigo(),
                getCurrentUnidNeg().getCodigo(), pedido.getCodigoCliente(), pedido.getCodigo());


        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(pedidoVendaArray.toArray(),
                getString(R.string.select_pedido_venda));

        if (pedidoVendaArray.size() > 0) {

            mPedidoVendaSpinnerAdapter = new SpinnerArrayAdapter<PedidoBonificado>(
                    getContext(), objects, true) {
                @Override
                public String getItemDescription(PedidoBonificado item) {
                    return item.getCodigo();
                }
            };

            mPedidoVendaSpinner.setAdapter(mPedidoVendaSpinnerAdapter);

            setPedVend(pedido.getCodigoPedidoVenda(), pedido);
        }
    }

    private void setupGrupo(Pedido pedido) {
        GrupoDao grupoDao = new GrupoDao(getContext());
        grupoVendaArray = grupoDao.getGrupo(getCurrentUnidNeg().getCodigo());

        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(grupoVendaArray.toArray(),
                getString(R.string.select_grupo));

        if (grupoVendaArray.size() > 0) {

            mGrupoSpinnerAdapter = new SpinnerArrayAdapter<Grupo>(
                    getContext(), objects, true) {
                @Override
                public String getItemDescription(Grupo item) {
                    return item.getNome().toString();
                }
            };

            mGrupoSpinner.setAdapter(mGrupoSpinnerAdapter);

            setGrupo(pedido.getCodigoGrupo(), pedido);
        }
    }

    private void setupCondPagamento(Pedido pedido) {
        CondicaoPagamentoDao condicaoPagamentoDao = new CondicaoPagamentoDao(getContext());
        condPagArray = new ArrayList<>();

        if (!isEditMode()) {
            CondicaoPagamento cond = condicaoPagamentoDao.get(pedido.getCodigoCondPag());
            if (cond != null) {
                condPagArray.add(cond);
            }

        } else if (pedido.getCodigoTipoVenda() == null) {
            condPagArray.add(pedido.getCliente().getCondicaoPagamento());
        } else {

            ArrayList<CondicaoPagamento> condPagArrayTemp = new ArrayList<>();
            String condpag = getCurrentPerfilVenda().getCondicaoPagamentoEspecifica();
            if (condpag != null && condpag.length() > 0) {
                condPagArrayTemp = condicaoPagamentoDao.getCondPagArray(condpag);
            }

            if (condPagArrayTemp.size() > 0) {
                condPagArray = condPagArrayTemp;
            } else {
                condPagArray.add(pedido.getCliente().getCondicaoPagamento());
            }
        }

        if (condPagArray.size() > 0) {

            mCondPagSpinnerAdapter = new SpinnerArrayAdapter<CondicaoPagamento>(
                    getContext(), condPagArray.toArray(), false) {
                @Override
                public String getItemDescription(CondicaoPagamento item) {
                    return item.getNome().toString();
                }
            };

            mCondPagSpinner.setAdapter(mCondPagSpinnerAdapter);

            setCondPag(pedido.getCodigoCondPag(), pedido);

        }
    }

    private void setupUrgente(Pedido pedido) {
        mUrgenteSpinner.setOnItemSelectedListener(null);
        Object[] objects = getResources().getStringArray(R.array.array_question);

        mUrgenteSpinnerAdapter = new SpinnerArrayAdapter<String>(
                getContext(), objects, false) {
            @Override
            public String getItemDescription(String item) {
                return item;
            }
        };

        mUrgenteSpinner.setAdapter(mUrgenteSpinnerAdapter);
        setUrgente(pedido.getUrgente(), pedido);
    }

    private void setupLocalEntrega(Pedido pedido) {
        ClienteEntregaDao clienteEntregaDao = new ClienteEntregaDao(getContext());
        localEntregaArray = clienteEntregaDao.getClienteEntrega(getCurrentUnidNeg().getCodigo(), pedido.getCliente());

        if (localEntregaArray.size() > 0) {
            mLocalEntregaSpinnerAdapter = new SpinnerArrayAdapter<ClienteEntrega>(
                    getContext(), localEntregaArray.toArray(), false) {
                @Override
                public String getItemDescription(ClienteEntrega item) {
                    return item.getEndereco();
                }
            };

            mLocalEntregaSpinner.setAdapter(mLocalEntregaSpinnerAdapter);
            setLocalEntrega(pedido.getLojaEntrega(), pedido);
        }
    }

    public void setupDataEntrega(Pedido pedido) {
        try {

            if (isEditMode()) {
                String unidNeg = Current.getInstance(getContext()).getUnidadeNegocio().getCodigo();
                boolean editaEntrega = getCurrentPerfilVenda().isEdicaoDataEntregaHabilitada();
                boolean isPositivacaoHabilitado = getCurrentPerfilVenda() != null && getCurrentPerfilVenda().isPositivacaoHabilitado();
                if (editaEntrega) {
                    if (pedido.getEntregaEm() == null) {
                        if (isPositivacaoHabilitado) {
                            PlanoCampoDao planoCampoDao = new PlanoCampoDao(getContext());
                            pedido.setEntregaEm(planoCampoDao.getProxVisita(unidNeg, pedido.getCliente()));
                        } else {
                            Date now = FormatUtils.getDateTimeNow(1, 0, 0);
                            pedido.setEntregaEm(now);
                        }
                        setCurrentPedido(pedido);
                    }
                } else {
                    if (isPositivacaoHabilitado) {
                        PlanoCampoDao planoCampoDao = new PlanoCampoDao(getContext());
                        pedido.setEntregaEm(planoCampoDao.getProxVisita(unidNeg, pedido.getCliente()));
                    } else {
                        pedido.setEntregaEm(new Date());
                    }
                    setCurrentPedido(pedido);
                }
            }


            if (pedido.getEntregaEm() != null) {
                mEntregaEmTextView.setText(FormatUtils.toDefaultDateFormat(
                        getContext(), pedido.getEntregaEm()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTpVend(TipoVenda value, Pedido pedido) {
        if (value != null && mTipoVendaSpinnerAdapter != null) {
            for (int ind = 1; ind < mTipoVendaSpinnerAdapter.getCount(); ind++) {
                TipoVenda tipoVenda = TipoVenda.class.cast(mTipoVendaSpinnerAdapter.getItem(ind));

                if (value.getCodigo().equals(tipoVenda.getCodigo())) {
                    mTipoVendSpinner.setSelection(ind);
                    ind = mTipoVendaSpinnerAdapter.getCount();
                }
            }
        } else if (value == null && mTipoVendSpinner.getSelectedItemPosition() > 0) {
            TipoVenda tipoVenda = TipoVenda.class.cast(mTipoVendSpinner.getSelectedItem());
            pedido.setTipoVenda(tipoVenda);
            pedido.setCodigoTipoVenda(tipoVenda.getCodigo());
            setCurrentPedido(pedido);
        }

        if (mTipoVendSpinner != null && mTipoVendSpinner.getSelectedItemPosition() == 0) {
            if(getCurrentPedido().getTipoVenda() == null ||!"SUG".equals(getCurrentPedido().getTipoVenda().getCodigo())){
                setCurrentItens(new ArrayList<>());
            }
        }
    }

    public void setUnMed(String value, Pedido pedido) {
        boolean possuiValor = value != null && mUnidadeMedidaSpinnerAdapter != null;

        if (possuiValor) {
            for (int i = 0; i < mUnidadeMedidaSpinnerAdapter.getCount(); i++) {
                Object obj = mUnidadeMedidaSpinnerAdapter.getItem(i);
                UnidadeMedida unidadeMedida = UnidadeMedida.class.cast(obj);

                if (value.equals(unidadeMedida.getCodigo())) {
                    mUnidadeMedidaSpinner.setSelection(i);
                    break;
                }
            }
        } else {
            Object obj = mUnidadeMedidaSpinner.getSelectedItem();
            UnidadeMedida unidadeMedida = UnidadeMedida.class.cast(obj);

            pedido.setUnidadeMedida(unidadeMedida.getCodigo());
            setCurrentPedido(pedido);
        }

        //pedido.setUnidadeMedida(Pedido.UNIDADE_MEDIDA_UNIDADE);
        //setCurrentPedido(pedido);
    }

    public void setPedVend(String value, Pedido pedido) {
        if (value != null && mPedidoVendaSpinnerAdapter != null) {
            for (int ind = 1; ind < mPedidoVendaSpinnerAdapter.getCount(); ind++) {
                String pedidoVenda = mPedidoVendaSpinnerAdapter.getItem(ind).toString();

                if (value.equals(pedidoVenda)) {
                    mPedidoVendaSpinner.setSelection(ind);
                    ind = mPedidoVendaSpinnerAdapter.getCount();
                }
            }
        } else if (value == null && mPedidoVendaSpinner.getSelectedItemPosition() > 0) {
            String pedidoVenda = String.class.cast(mPedidoVendaSpinner.getSelectedItemPosition());
            pedido.setCodigoPedidoVenda(pedidoVenda);
            setCurrentPedido(pedido);
        }
    }

    public void setGrupo(String value, Pedido pedido) {
        if (value != null && mGrupoSpinnerAdapter != null) {
            for (int ind = 1; ind < mGrupoSpinnerAdapter.getCount(); ind++) {
                Grupo grupo = Grupo.class.cast(mGrupoSpinnerAdapter.getItem(ind));

                if (value.equals(grupo.getCod())) {
                    mGrupoSpinner.setSelection(ind);
                    ind = mGrupoSpinnerAdapter.getCount();
                }
            }
        } else if (mGrupoSpinner.getSelectedItemPosition() > 0) {
            Grupo grupo = Grupo.class.cast(mGrupoSpinner.getSelectedItem());
            pedido.setCodigoGrupo(grupo.getCod());
            setCurrentPedido(pedido);
        }
    }

    public void setCondPag(String value, Pedido pedido) {
        if (value != null) {
            for (int ind = 0; ind < mCondPagSpinnerAdapter.getCount(); ind++) {
                CondicaoPagamento condicaoPagamento = CondicaoPagamento.class.cast(mCondPagSpinnerAdapter.getItem(ind));

                if (value.equals(condicaoPagamento.getCodigo())) {
                    mCondPagSpinner.setSelection(ind);
                    ind = mCondPagSpinnerAdapter.getCount();
                }
            }
        } else {
            CondicaoPagamento condicaoPagamento = CondicaoPagamento.class.cast(mCondPagSpinner.getSelectedItem());
            pedido.setCodigoCondPag(condicaoPagamento.getCodigo());
            setCurrentPedido(pedido);
        }
    }

    public void setPlanta(String value, Pedido pedido) {
        if (value != null && mPlantaSpinnerAdapter != null) {
            for (int ind = 0; ind < mPlantaSpinnerAdapter.getCount(); ind++) {
                Planta planta = Planta.class.cast(mPlantaSpinnerAdapter.getItem(ind));

                if (value.equals(planta.getCodPlanta())) {
                    mPlantaSpinner.setSelection(ind);
                    ind = mPlantaSpinnerAdapter.getCount();
                }
            }
        } else {
            Planta planta = Planta.class.cast(mPlantaSpinner.getSelectedItem());
            pedido.setCodigoPlanta(planta.getCodPlanta());
            setCurrentPedido(pedido);
        }
    }

    public void setLocalEntrega(String value, Pedido pedido) {
        if (value != null && mLocalEntregaSpinnerAdapter != null) {
            for (int ind = 0; ind < mLocalEntregaSpinnerAdapter.getCount(); ind++) {
                ClienteEntrega localEntrega = ClienteEntrega.class.cast(mLocalEntregaSpinnerAdapter.getItem(ind));

                if (value.equals(localEntrega.getLojaEntrega())) {
                    mLocalEntregaSpinner.setSelection(ind);
                    ind = mLocalEntregaSpinnerAdapter.getCount();
                }
            }
        }
    }

    public void setUrgente(String value, Pedido pedido) {
        if (value != null && mUrgenteSpinnerAdapter != null) {
            for (int ind = 0; ind < mUrgenteSpinnerAdapter.getCount(); ind++) {
                String urgente = (String) mUrgenteSpinnerAdapter.getItem(ind);

                if (urgente.substring(0, 1).toLowerCase().equals(value.toLowerCase())) {
                    mUrgenteSpinner.setSelection(ind);
                    ind = mUrgenteSpinnerAdapter.getCount();
                }
            }
        } else {
            mUrgenteSpinner.setSelection(mUrgenteSpinnerAdapter.getCount() - 1);
            String urgente = (String) mUrgenteSpinner.getSelectedItem();

            if (urgente != null) {
                pedido.setUrgente(urgente.substring(0, 1).toUpperCase());
                setCurrentPedido(pedido);
            }
        }
    }

    private void createDataPicker(Date date) {
        if(getActivity() == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();

        if (date != null) {
            try {
                calendar.setTime(date);
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mEntregueEmdatePickerDialog = new DatePickerDialog(
                getActivity(), mEntregueEmDateSetListener, year, month, day);


        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                long minTimeMillis = System.currentTimeMillis() + (1000 * 60 * 60 * 24);
                final Calendar minDate = Calendar.getInstance();
                minDate.setTimeInMillis(minTimeMillis);
                minDate.set(Calendar.HOUR_OF_DAY, minDate.getMinimum(Calendar.HOUR_OF_DAY));
                minDate.set(Calendar.MINUTE, minDate.getMinimum(Calendar.MINUTE));
                minDate.set(Calendar.SECOND, minDate.getMinimum(Calendar.SECOND));
                minDate.set(Calendar.MILLISECOND, minDate.getMinimum(Calendar.MILLISECOND));
                mEntregueEmdatePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            } catch (Exception ex) {
                LogUser.log(Config.TAG, ex.toString());
            }
        } else {
            mEntregueEmdatePickerDialog.getDatePicker().setMinDate(FormatUtils.getDateTimeNow(1, 0, 0).getTime());
        }

        //mEntregueEmdatePickerDialog.getDatePicker().setMaxDate(FormatUtils.getDateTimeNow(0, 3, 0).getTime());

    }

    private boolean updateItemsWithNewUnidadeMedida(String currentUnidadeMedida,
                                                    String newUnidadeMedida, TipoVenda tipoVenda) {
        if (currentUnidadeMedida.equals(newUnidadeMedida)) {
            return true;
        }

        ArrayList<ItemPedido> itemsPedido = getCurrentItens();
        ArrayList<ItemPedido> itemsPedidoTemp = new ArrayList<>();

        if (itemsPedido == null || itemsPedido.size() == 0) {
            return true;
        }

        boolean changeToCaixa = currentUnidadeMedida.equals(Pedido.UNIDADE_MEDIDA_UNIDADE)
                && newUnidadeMedida.equals(Pedido.UNIDADE_MEDIDA_CAIXA);

        // check if the items can have their "unidade de medida" changed
        for (ItemPedido item : itemsPedido) {
            int multiplo = item.getProduto().getMultiplo();
            int quantidade = item.getQuantidade();



            if(quantidade > 0) {

                if(tipoVenda.getCodigo().equals(TipoVenda.REB)){
                    if (changeToCaixa && (quantidade % multiplo == 0)) {
                        item.setQuantidade(quantidade / multiplo);
                        item.setPrecoVenda(item.getPrecoVenda() * item.getProduto().getMultiplo());
                        itemsPedidoTemp.add(item);
                    } else if (!changeToCaixa) {
                        item.setQuantidade(quantidade * multiplo);
                        item.setPrecoVenda(item.getPrecoVenda() / item.getProduto().getMultiplo());
                        itemsPedidoTemp.add(item);
                    }
                } else {
                    if (changeToCaixa && (quantidade % multiplo == 0)) {
                        item.setQuantidade(quantidade / multiplo);
                        itemsPedidoTemp.add(item);
                    } else if (!changeToCaixa) {
                        item.setQuantidade(quantidade * multiplo);
                        itemsPedidoTemp.add(item);
                    }
                }
            } else {
                itemsPedidoTemp.add(item);
            }
        }

        // if they can't, do nothing and return false
        if (itemsPedido.size() != itemsPedidoTemp.size()) {
            return false;
        }

        // change the unidade de medida from pedido (it's requested to update the item) temporarily
        Pedido pedido = getCurrentPedido();
        pedido.setUnidadeMedida(newUnidadeMedida);

        // if it's all ok, update the model and persist data into database (then notify)
        for (ItemPedido item : itemsPedidoTemp) {
            if(TipoVenda.REB.equals(getCurrentPedido().getCodigoTipoVenda())){
                mPedidoBusiness.buildNewItemReb(pedido, item, 3);
            } else {
                mPedidoBusiness.buildNewItem(pedido, item);
            }
        }

        mPedidoBusiness.updateItemPedido(getActivity(), itemsPedidoTemp);
        CurrentActionPedido.getInstance().setUpdateListItem(true);
        setCurrentItens(itemsPedidoTemp);

        // back pedido to previous unidade de medida
        pedido.setUnidadeMedida(currentUnidadeMedida);

        return true;
    }

    private void enableEntregaEm(boolean enable) {
        if (enable) {
            mEntregaEmTextView.setTextColor(getResources().getColor(R.color.formLabelTextColor));
            mEntregaEmLinearLayout.setEnabled(true);
            mEntregarEmImageView.setVisibility(View.VISIBLE);
        } else {
            mEntregaEmTextView.setTextColor(getResources().getColor(R.color.formLabelTextColorDisable));
            mEntregaEmLinearLayout.setEnabled(false);
            mEntregarEmImageView.setVisibility(View.GONE);
        }
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }


}
