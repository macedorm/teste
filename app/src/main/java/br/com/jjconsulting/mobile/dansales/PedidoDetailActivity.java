package br.com.jjconsulting.mobile.dansales;

import android.app.Dialog;
import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskPedidoDetail;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncPedidoConnection;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.data.ValidationMessage;
import br.com.jjconsulting.mobile.dansales.data.ValidationMessageType;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaTarefaDao;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoLogActivity;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingActivity;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingFragment;
import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingListNFActivity;
import br.com.jjconsulting.mobile.dansales.model.ItemPedido;
import br.com.jjconsulting.mobile.dansales.model.ItensSortimento;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoTabType;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.SyncPedidoProcessDetail;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.dansales.model.TipoVenda;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.DialogsMultiValue;
import br.com.jjconsulting.mobile.dansales.util.FirebaseUtils;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.dansales.viewModel.PedidoDetailViewModel;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PedidoDetailActivity extends BaseActivity
        implements AsyncTaskPedidoDetail.OnAsyncResponse {

    private static final String ARG_CODIGO_PEDIDO = "codigo_pedido";
    private static final String ARG_TIPO_PEDIDO = "tipo_pedido";
    private static final String ARG_FORCE_SYNC = "force_sync";
    private static final String ARG_ROTA = "is_rota";
    private static final String ARG_ROTA_SORTIMENTO = "is_rota_sortimento";

    private static final int JUST_REQUEST = 99;


    private AsyncTaskPedidoDetail asyncTaskPedidoDetail;
    private PedidoDetailViewModel mPedidoDetailViewModel;
    private ArrayList<PedidoTabType> arrayPedidoTab;
    private PedidoViewType viewType;
    private PedidoBusiness pedidoBusiness;
    private ArrayList<ItemPedido> itemObr;

    private PedidoFragmentPagerAdapter mPedidoFragmentPagerAdapter;
    private TabLayout mPedidoTabLayout;
    private ViewPager mPedidoViewPager;
    private ProgressDialog progressDialog;
    private ViewGroup mRootView;

    private boolean isEditMode;
    private boolean isPedidoChanged;
    private boolean isInitObservablePedido;
    private boolean isInitObservableItemPedido;
    private boolean isForceSync;
    private boolean isRota;

    private int positionSelected;

    public static Intent newIntent(Context context, String codigoPedido, PedidoViewType tipoPedido,
                                   boolean forceSync, boolean isRota) {
        Intent intent = new Intent(context, PedidoDetailActivity.class);
        intent.putExtra(ARG_CODIGO_PEDIDO, codigoPedido);
        intent.putExtra(ARG_TIPO_PEDIDO, tipoPedido);
        intent.putExtra(ARG_FORCE_SYNC, forceSync);
        intent.putExtra(ARG_ROTA, isRota);

        return intent;
    }

    public PedidoDetailActivity() {
    }



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_pedido_detail));
        getSupportActionBar().setSubtitle(getIntent().getStringExtra(ARG_CODIGO_PEDIDO));

        isForceSync = getIntent().getBooleanExtra(ARG_FORCE_SYNC, false);
        isRota = getIntent().getBooleanExtra(ARG_ROTA, false);

        // It must be done for good visual style using TabLayout + ViewPager
        getSupportActionBar().setElevation(0);

        viewType = (PedidoViewType) getIntent().getSerializableExtra(ARG_TIPO_PEDIDO);

        pedidoBusiness = new PedidoBusiness();

        mRootView = findViewById(R.id.pedido_detail_linear_layout);
        mPedidoTabLayout = findViewById(R.id.pedido_tab_layout);
        mPedidoViewPager = findViewById(R.id.pedido_view_pager);

        createDialogProgress();
        progressDialog.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        asyncTaskPedidoDetail = new AsyncTaskPedidoDetail(getApplicationContext(), viewType, this);
        asyncTaskPedidoDetail.execute(getIntent().getStringExtra(ARG_CODIGO_PEDIDO));
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            if (isPedidoChanged) {
                updatePedido(null);
            }
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_pedido_menu, menu);
        if (isEditMode) {
            MenuItem menuSave = menu.findItem(R.id.action_save);
            menuSave.setVisible(true);
            MenuItem menuDelete = menu.findItem(R.id.action_delete);
            menuDelete.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isEditMode) {
                    if (isPedidoChanged) {
                        updatePedido(null);
                    }
                    finish();
                } else {
                    super.onBackPressed();
                }
                return true;
            case R.id.action_log:
                startActivity(PedidoLogActivity.newIntent(getApplicationContext(),
                        mPedidoDetailViewModel.getObservablePedido().getValue().getCodigo()));
                return true;
            case R.id.action_rastreio:
                startActivity(PedidoTrackingListNFActivity.Companion.newIntent(getApplicationContext(), mPedidoDetailViewModel.getObservablePedido().getValue().getCodigo()));
                return true;
            case R.id.action_delete:
                dialogsDefault.showDialogQuestion(getString(R.string.detail_pedido_delete), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        pedidoBusiness.deletePedido(getApplicationContext(), mPedidoDetailViewModel.getObservablePedido().getValue());
                        CurrentActionPedido.getInstance().setUpdateListPedido(true);
                        finish();
                    }

                    @Override
                    public void onClickNegative() {
                    }
                });
                return true;
            case R.id.action_save:
                validPedido(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void processFinish(ArrayList<Object> result) {
        if (result == null || result.size() == 0) {

            if (getWindow().getDecorView().isShown()) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            dialogsDefault.showDialogMessage(
                    getString(R.string.pedido_not_found),
                    dialogsDefault.DIALOG_TYPE_WARNING,
                    () -> {
                        CurrentActionPedido.getInstance().setUpdateListPedido(true);
                        finish();
                    });
            return;
        }

        // The PedidoDetailViewModel is a ViewModel and holds a reference of LiveData<T>
        // where T is the model (Pedido). These two objects handle state under lifecycle
        // and configuration changes. We can also get the model object and listen to
        // changes on it through observe method.
        mPedidoDetailViewModel = ViewModelProviders.of(this).get(PedidoDetailViewModel.class);

        Pedido pedido = (Pedido) result.get(0);

        isEditMode = (boolean) result.get(1);

        if (pedido.getCliente() == null) {

            if (getWindow().getDecorView().isShown()) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            dialogsDefault.showDialogMessage("Você não tem permissão para acessar esse cliente.", dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
                @Override
                public void onClick() {
                    finish();
                }
            });
            return;
        }

        if (result.size() > 2) {
            ArrayList<ItemPedido> itemPedido = (ArrayList<ItemPedido>) result.get(2);
            mPedidoDetailViewModel.getObservableItens().setValue(itemPedido);
        }

        mPedidoDetailViewModel.getObservablePedido().setValue(pedido);
        mPedidoDetailViewModel.getObservableViewType().setValue(viewType);
        mPedidoDetailViewModel.getObservableEditMode().setValue(isEditMode);

        createArrayViewPager(isEditMode, pedido);

        mPedidoFragmentPagerAdapter = new PedidoFragmentPagerAdapter(getSupportFragmentManager());
        mPedidoViewPager.setAdapter(mPedidoFragmentPagerAdapter);
        mPedidoTabLayout.setupWithViewPager(mPedidoViewPager);

        if (isEditMode) {
            invalidateOptionsMenu();
        }

        addListener();

        if (isForceSync) {
            isForceSync = false;
            validPedido(false);
        } else {
            if (getWindow().getDecorView().isShown()) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        }

    }

    private void actionSave(boolean justificado, boolean isQuestionOK, ArrayList<ItemPedido> itens){
        itemObr = new ArrayList<>();
        Pedido pedido = mPedidoDetailViewModel.getObservablePedido().getValue();
        Current current = Current.getInstance(this);
        PerfilVenda perfilVenda = current.getUsuario().getPerfil().getPerfilVenda(pedido);

        if(!justificado){
            SortimentoDao sortimentoDao = new SortimentoDao(this);
            ArrayList<ItensSortimento> itensSortimento = sortimentoDao.getItensSortimentoPedido(current.getUsuario(), pedido, pedido.getCodigoSortimento(), pedido.getDataCadastro());
            itemObr = pedidoBusiness.checkItens(mPedidoDetailViewModel.getObservableItens().getValue(), itensSortimento);
            Hashtable justificativa = mPedidoDetailViewModel.getObservablePedido().getValue().getJustificativa();

            if(justificativa  != null && justificativa.size() > 0){
                itemObr = new ArrayList<>();
            }
        }

        if(perfilVenda.getTipoJustificativaSortimento() == PerfilVenda.SORTIMENTO_NAOJUSTIFICA ||(justificado || itemObr.isEmpty())){
            updatePedido(null);
            dialogsDefault.showDialogQuestion(getString(R.string.detail_pedido_sync_question), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                @Override
                public void onClickPositive() {
                    sendPedido(false, itens);
                }

                @Override
                public void onClickNegative() {
                    finish();
                }
            });
        } else {
            if(perfilVenda.getTipoJustificativaSortimento() == PerfilVenda.ESCOLHA_USUARIO){
                showDialogCustomButton(getString(R.string.detail_pedido_just_escolhe_usuairo), getString(R.string.detail_button_title_just_pedido),
                        getString(R.string.detail_button_title_just_item),  new DialogsCustom.OnClickDialogQuestion() {
                            @Override
                            public void onClickPositive() {
                                justificativaPedido();
                            }

                            @Override
                            public void onClickNegative() {
                                justificativaItem(itemObr);
                            }

                        });
            } else {
                dialogsDefault.showDialogQuestion(getString(R.string.detail_pedido_just_question), DialogsCustom.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        if (perfilVenda.getTipoJustificativaSortimento() == PerfilVenda.SORTIMENTO_JUSTIFICAPEDIDO) {
                            justificativaPedido();
                        } else {
                            justificativaItem(itemObr);
                        }
                    }

                    @Override
                    public void onClickNegative() {

                    }

                });
            }
        }
    }

    private void justificativaItem(ArrayList<ItemPedido> itens){
        Intent it = PedidoItensJustificativaActivty.newIntent(PedidoDetailActivity.this, itens);
        startActivityForResult(it, JUST_REQUEST);
    }


    private void justificativaPedido(){
        DialogsMultiValue dialogsMultiValue = new DialogsMultiValue(PedidoDetailActivity.this);
        dialogsMultiValue.showDialogSpinner(TMultiValuesType.RG_ITEM_PEDIDO_JUST, getString(R.string.valid_checkout_success), dialogsMultiValue.DIALOG_TYPE_WARNING, new DialogsMultiValue.OnClickDialogMessage() {
            @Override
            public void onClick(MultiValues multiValues) {
                Hashtable justificativa = new Hashtable();
                justificativa.put("Pedido", multiValues.getValCod());
                pedidoJustificado(justificativa, true);
            }
        });
    }

    private void addListener() {
        mPedidoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                positionSelected = position;
                OnPageSelected listener = (OnPageSelected) mPedidoFragmentPagerAdapter
                        .getFragment(position);
                if (listener != null) {
                    listener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mPedidoDetailViewModel.getObservablePedido().observe(this, pedidoViewModel -> {
            if (isInitObservablePedido) {
                isPedidoChanged = true;
            } else {
                isInitObservablePedido = true;
            }
        });

        mPedidoDetailViewModel.getObservableItens().observe(this, pedidoViewModel -> {
            if (isInitObservableItemPedido) {
                isPedidoChanged = true;
            } else {
                isInitObservableItemPedido = true;
            }
        });
    }

    private void createArrayViewPager(boolean isEditMode, Pedido pedido) {
        arrayPedidoTab = new ArrayList<>();

        //Show TAB "Aprovavação" where viewType = Liberaçao/Aprovaçao
        if (viewType == PedidoViewType.LIBERACAO) {
            arrayPedidoTab.add(PedidoTabType.LIBERACAO_FRAGMENT);
        } else if (viewType == PedidoViewType.APROVACAO) {
            arrayPedidoTab.add(PedidoTabType.APROVACAO_FRAGMENT);
        }

        arrayPedidoTab.add(PedidoTabType.CABECALHO_FRAGMENT);
        arrayPedidoTab.add(PedidoTabType.ITENS_FRAGMENT);

        if (isEditMode) {
            arrayPedidoTab.add(PedidoTabType.OBSERVACAO_FRAGMENT);
        } else {
            boolean obsPreenchida = pedido.getObservacao() != null
                    && !pedido.getObservacao().isEmpty();
            boolean obsNfPreenchida = pedido.getObservacaoNotaFiscal() != null
                    && !pedido.getObservacaoNotaFiscal().isEmpty();
            if (obsPreenchida || obsNfPreenchida) {
                arrayPedidoTab.add(PedidoTabType.OBSERVACAO_FRAGMENT);
            }
        }
        arrayPedidoTab.add(PedidoTabType.RESUMO_FRAGMENT);

    }

    private void processingResponseSyncPedido(SyncPedidoProcessDetail syncPedidoProcessDetail) {
        ValidationDan validationDan = new ValidationDan();
        validationDan.setMessage(new ArrayList<>());

        Pedido pedido = mPedidoDetailViewModel.getObservablePedido().getValue();

        String codigoAntigoPedido = null;
        boolean isUpdate = false;

        ValidationMessage validationMessage = null;

        if (TextUtils.isNullOrEmpty(syncPedidoProcessDetail.getQuestionMessage())) {

            if (!TextUtils.isNullOrEmpty(syncPedidoProcessDetail.getWarningMessage())) {
                validationMessage = new ValidationMessage();
                validationMessage.setMessage(syncPedidoProcessDetail.getWarningMessage());
                validationMessage.setType(ValidationMessageType.ALERT);
                validationDan.getMessage().add(validationMessage);
            }

            //Check update codigo pedido
            if (!TextUtils.isNullOrEmpty(syncPedidoProcessDetail.getCodPedido()) &&
                    !syncPedidoProcessDetail.getCodPedido().equals(pedido.getCodigo())) {
                codigoAntigoPedido = pedido.getCodigo();
                pedido.setIsPositivacao(syncPedidoProcessDetail.getPositivado());
                pedido.setCodigo(syncPedidoProcessDetail.getCodPedido());
                isUpdate = true;

                validationMessage = new ValidationMessage();
                validationMessage.setMessage(getString(R.string.message_warning_pedido_sync_codigo, syncPedidoProcessDetail.getCodPedido()));
                validationMessage.setType(ValidationMessageType.INFO);
                validationDan.getMessage().add(validationMessage);
            }

            //Check update status pedido
            if (syncPedidoProcessDetail.getNewStatus() > 0) {
                pedido.setCodigoStatus(syncPedidoProcessDetail.getNewStatus());
                pedido.setDataEnvio(Calendar.getInstance().getTime());
                isUpdate = true;
            }

            if (syncPedidoProcessDetail.getErrors() != null) {
                for (String message : syncPedidoProcessDetail.getErrors()) {
                    validationMessage = new ValidationMessage();
                    validationMessage.setType(ValidationMessageType.ERROR);
                    validationMessage.setMessage(message);
                    validationDan.getMessage().add(validationMessage);
                }
            }

             if (syncPedidoProcessDetail.getInfos() != null) {
                 for (String message : syncPedidoProcessDetail.getInfos()) {
                     validationMessage = new ValidationMessage();
                     validationMessage.setType(ValidationMessageType.INFO);
                     validationMessage.setMessage(message);
                     validationDan.getMessage().add(validationMessage);
                 }
             }

            // Update item and pedido in Database
            if (isUpdate) {
                mPedidoDetailViewModel.getObservablePedido().setValue(pedido);
                try {
                    updatePedido(codigoAntigoPedido);

                    if (codigoAntigoPedido != null) {
                        pedidoBusiness.updateCodPedItem(this, codigoAntigoPedido, pedido.getCodigo());
                    }

                    RotaGuiadaTarefaDao rotaGuiadaTarefaDao = new RotaGuiadaTarefaDao(this);
                    rotaGuiadaTarefaDao.updateCodigoPedidoTaks(pedido.getCodigo(), codigoAntigoPedido);
                } catch (Exception ex) {
                    if (codigoAntigoPedido != null) {
                        pedido.setCodigo(codigoAntigoPedido);
                        mPedidoDetailViewModel.getObservablePedido().setValue(pedido);
                    }
                    validationMessage = new ValidationMessage();
                    validationMessage.setMessage(getString(R.string.message_error_update_pedido_sync, pedido.getCodigo()));
                    validationMessage.setType(ValidationMessageType.ERROR);
                    validationDan.getMessage().add(validationMessage);
                }
            }

            //Update Itens
            ArrayList<ItemPedido> itensSincronizados = syncPedidoProcessDetail.getItensPedido();

            if (itensSincronizados != null && itensSincronizados.size() > 0) {
                isUpdate = true;
                pedidoBusiness.deleteAllItemPedido(this, pedido.getCodigo());
                pedidoBusiness.insertItemPedido(this, itensSincronizados, pedido.getCodigo());
            }

            if (syncPedidoProcessDetail.getValid()) {
                ArrayList<ItemPedido> itemPedidos = mPedidoDetailViewModel.getObservableItens().getValue();
                if (pedido != null && itemPedidos != null) {

                    Bundle params = new Bundle();
                    params.putString("BU", pedido.getCodigoUnidadeNegocio());
                    params.putString(FirebaseAnalytics.Param.PRICE, String.valueOf(BigDecimal.valueOf(pedido.getValorTotal())));
                    params.putString(FirebaseAnalytics.Param.CURRENCY, Currency.getInstance("BRL").toString());
                    params.putString(FirebaseAnalytics.Param.ITEMS, String.valueOf(itemPedidos.size()));

                    FirebaseUtils.sendEvent(this, FirebaseAnalytics.Event.BEGIN_CHECKOUT, params);

                }
            }

            showDialogValidationError(validationDan, isUpdate, syncPedidoProcessDetail.getValid());
        } else {
            dialogsDefault.showDialogQuestion(syncPedidoProcessDetail.getQuestionMessage(),
                    dialogsDefault.DIALOG_TYPE_QUESTION,
                    new DialogsCustom.OnClickDialogQuestion() {
                        @Override
                        public void onClickPositive() {
                            ArrayList<ItemPedido> itensPedido = pedidoBusiness.removeItensNegativo(mPedidoDetailViewModel.getObservableItens().getValue());
                            sendPedido(true, itensPedido);
                        }

                        @Override
                        public void onClickNegative() {
                        }
                    });
        }
    }

    private void updatePedido(String codigoAntigoPedido) {
        //Toast.makeText(this, "pedido salvo: " +
          //      mPedidoDetailViewModel.getObservablePedido().getValue().getUnidadeMedida(), Toast.LENGTH_LONG).show();

        pedidoBusiness.updatePedido(PedidoDetailActivity.this,
                mPedidoDetailViewModel.getObservablePedido().getValue(),
                mPedidoDetailViewModel.getObservableItens().getValue(), codigoAntigoPedido);
        CurrentActionPedido.getInstance().setUpdateListPedido(true);
    }

    private void validPedido(boolean isQuestionOk){

        ArrayList<ItemPedido> itensPedido = pedidoBusiness.removeItensNegativo(mPedidoDetailViewModel.getObservableItens().getValue());

        PedidoBusiness.OnAsyncResponse onAsyncResponse = (ValidationDan objects) -> {
            if (!objects.isValid()) {
                if (getWindow().getDecorView().isShown()) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                dialogsDefault.showDialogMessage(
                        getString(R.string.detail_pedido_validation_error),
                        dialogsDefault.DIALOG_TYPE_ERROR,
                        () -> mPedidoViewPager.setCurrentItem(arrayPedidoTab.size() - 1));
                return;
            }

            String message = "";

            boolean warning = false;
            for (ValidationMessage msg : objects.getMessage()) {
                if (msg.getType() == ValidationMessageType.ALERT) {
                    warning = true;
                    message += "\n - " + msg.getMessage() + "\n";
                    break;
                }
            }

            if (warning && !mPedidoDetailViewModel.getObservablePedido().getValue().getCodigoTipoVenda().equals(TipoVenda.REB)) {
                showDialogWarningQuestion(message, isQuestionOk, itensPedido);
            } else {
                actionSave(false, isQuestionOk, itensPedido);
            }

        };

        pedidoBusiness.executeValidadePedido(this,
                mPedidoDetailViewModel.getObservablePedido().getValue(),
                itensPedido,
                onAsyncResponse);

    }

    private void sendPedido(boolean isQuestionOk, ArrayList<ItemPedido> itens) {
        SyncPedidoConnection syncPedidoConnection = new SyncPedidoConnection(
                PedidoDetailActivity.this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection,
                                 InputStreamReader reader, ArrayList<String[]> responseList) {
                try {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }

                try {
                    SyncPedidoProcessDetail syncPedidoProcessDetail = gson.fromJson(
                            response, SyncPedidoProcessDetail.class);

                    if (syncPedidoProcessDetail == null) {
                        dialogsDefault.showDialogMessage(
                                getString(R.string.title_connection_error),
                                dialogsDefault.DIALOG_TYPE_ERROR, null);
                    } else {
                        processingResponseSyncPedido(syncPedidoProcessDetail);
                    }
                }catch (Exception ex){
                    dialogsDefault.showDialogMessage(
                            getString(R.string.title_connection_error),
                            dialogsDefault.DIALOG_TYPE_ERROR, null);

                    LogUser.log(Config.TAG, ex.toString());
                }
              }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                if (getWindow().getDecorView().isShown()) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }

                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                   ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(PedidoDetailActivity.this, errorConnection.getMessage())){
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                String error = getString(R.string.detail_pedido_sync_error_connection);

                error += "\n\n";

                error += "Código do erro: " + code + "\n\n";

                if(volleyError != null && volleyError != null) {
                    error += "Erro técnico: " + volleyError.getMessage();
                }

                dialogsDefault.showDialogMessage(
                        error,
                        dialogsDefault.DIALOG_TYPE_ERROR, null);

            }
        }, isQuestionOk);

        try{
            progressDialog.show();

            syncPedidoConnection.syncPedido(
                    mPedidoDetailViewModel.getObservablePedido().getValue(), itens);
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    public void showDialogWarningQuestion(String message, boolean isQuestionOK, ArrayList<ItemPedido> itens) {
        try {
            Dialog questionDialog = new Dialog(this,
                    android.R.style.Theme_Translucent_NoTitleBar);
            questionDialog.setCancelable(false);
            questionDialog.setContentView(R.layout.dialog_two_buttons_warning_pedido);

            TextView questionDialogTwoButtonTextView = questionDialog.findViewById(
                    R.id.tv_dialog_message);
            TextView questionDescDialogTwoButtonTextView = questionDialog.findViewById(
                    R.id.tv_dialog_desc_message);
            TextView okDialogQuestionButton = questionDialog.findViewById(
                    R.id.ok_dialog_two_buttons);
            TextView cancelDialogQuestionButton = questionDialog.findViewById(
                    R.id.cancel_dialog_two_buttons);

            questionDialogTwoButtonTextView.setText(getString(R.string.detail_pedido_sync_warning));
            questionDescDialogTwoButtonTextView.setText(message);

            okDialogQuestionButton.setOnClickListener(
                    (View view) -> {
                        actionSave(false, isQuestionOK, itens);
                        okDialogQuestionButton.setEnabled(false);
                        cancelDialogQuestionButton.setEnabled(false);
                        questionDialog.dismiss();
                    }
            );

            cancelDialogQuestionButton.setOnClickListener(view -> {
                questionDialog.dismiss();
                if (getWindow().getDecorView().isShown()) {
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }
            });

            questionDialog.show();

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void pedidoJustificado(Hashtable justificativa, boolean isSend){
        ArrayList<ItemPedido> itensPedido = pedidoBusiness.removeItensNegativo(mPedidoDetailViewModel.getObservableItens().getValue());

        Pedido pedido = mPedidoDetailViewModel.getObservablePedido().getValue();
        pedido.setJustificativa(justificativa);
        mPedidoDetailViewModel.getObservablePedido().setValue(pedido);

        if(isSend){
            sendPedido(false, itensPedido);
        }
    }


    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

    private void showDialogValidationError(ValidationDan validation, boolean isUpdate, boolean isSucess) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();

            PedidoValidationDialogFragment pedidoValidationDialogFragment =
                    PedidoValidationDialogFragment.newInstance(validation, isSucess);
            pedidoValidationDialogFragment.show(fragmentManager, "");
            fragmentManager.executePendingTransactions();


            pedidoValidationDialogFragment.setOnDissmisDialog(new PedidoValidationDialogFragment.OnDissmisDialog() {
                @Override
                public void onFinish() {
                    if (isUpdate) {
                        if(!isRota) {
                            Intent openThisActivityIntent = newIntent(getApplicationContext(),
                                    mPedidoDetailViewModel.getObservablePedido().getValue().getCodigo(),
                                    viewType, false, false);
                            startActivity(openThisActivityIntent);
                        }

                        finish();
                    } else {
                        mPedidoViewPager.setCurrentItem(1);
                    }
                }
            });


        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request it is that we're responding to
        if (requestCode == JUST_REQUEST) {
            boolean isSend = resultCode == RESULT_OK;

            if(resultIntent == null || !resultIntent.getExtras().containsKey(PedidoItensJustificativaActivty.ARG_JUST))
                return;

            Hashtable justificativa = new Hashtable();
            justificativa.putAll((HashMap)resultIntent.getExtras().get(PedidoItensJustificativaActivty.ARG_JUST));
            pedidoJustificado(justificativa, isSend);
        } else {
            super.onActivityResult(requestCode, resultCode, resultIntent);
        }
    }
    private class PedidoFragmentPagerAdapter extends FragmentPagerAdapter {

        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;

        public PedidoFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTags = new HashMap<>();
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (arrayPedidoTab.get(position)) {
                case APROVACAO_FRAGMENT:
                    return PedidoAprovacaoFragment.newInstance();
                case LIBERACAO_FRAGMENT:
                    return PedidoLiberacaoFragment.newInstance();
                case CABECALHO_FRAGMENT:
                    return PedidoCabecalhoFragment.newInstance();
                case ITENS_FRAGMENT:
                    return PedidoItensFragment.newInstance();
                case OBSERVACAO_FRAGMENT:
                    return PedidoObservacaoFragment.newInstance();
                case RESUMO_FRAGMENT:
                    return PedidoResumoFragment.newInstance();
                 default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return arrayPedidoTab.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                String tag = fragment.getTag();
                mFragmentTags.put(position, tag);
            }
            return object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (arrayPedidoTab.get(position)) {
                case APROVACAO_FRAGMENT:
                    return getString(R.string.tab_pedido_aprovacao);
                case LIBERACAO_FRAGMENT:
                    return getString(R.string.tab_pedido_liberacao);
                case CABECALHO_FRAGMENT:
                    return getString(R.string.tab_pedido_cabecalho);
                case ITENS_FRAGMENT:
                    return getString(R.string.tab_pedido_itens);
                case OBSERVACAO_FRAGMENT:
                    return getString(R.string.tab_pedido_observacao);
                case RESUMO_FRAGMENT:
                    return getString(R.string.tab_pedido_resumo);
                case LOG_FRAGMENT:
                    return getString(R.string.tab_pedido_log);
                default:
                    return null;
            }
        }

        public Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }
    }

    // Dialog com botões positivo/negativo
    public void showDialogCustomButton(String message, String titlePositive, String titleNegative, DialogsCustom.OnClickDialogQuestion onClickDialogQuestion) {
        try {
            Dialog mQuestionDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            mQuestionDialog.setCancelable(false);
            mQuestionDialog.setContentView(R.layout.dialog_two_msg_left_buttons);

            TextView mQuestionDialogTwoButtonTextView = mQuestionDialog.findViewById(br.com.jjconsulting.mobile.jjlib.R.id.tv_dialog_message);
            mQuestionDialogTwoButtonTextView.setText(message);
            ImageView icon = mQuestionDialog.findViewById(R.id.icon_image_view);
            icon.setColorFilter(getResources().getColor(R.color.questionCollor), android.graphics.PorterDuff.Mode.SRC_ATOP);
            icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_info_black_24dp));

            TextView mOkDialogQuestionButton = mQuestionDialog.findViewById(br.com.jjconsulting.mobile.jjlib.R.id.ok_dialog_two_buttons);
            mOkDialogQuestionButton.setText(titlePositive);
            mOkDialogQuestionButton.setOnClickListener(view -> {
                mQuestionDialog.dismiss();
                if (onClickDialogQuestion != null) {
                    onClickDialogQuestion.onClickPositive();
                }
            });

            TextView mCancelDialogQuestionButton = mQuestionDialog.findViewById(br.com.jjconsulting.mobile.jjlib.R.id.cancel_dialog_two_buttons);
            mCancelDialogQuestionButton.setText(titleNegative);
            mCancelDialogQuestionButton.setOnClickListener(view -> {
                mQuestionDialog.dismiss();
                if (onClickDialogQuestion != null) {
                    onClickDialogQuestion.onClickNegative();
                }
            });

            mQuestionDialog.show();

            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        } catch (Exception ex) {
            LogUser.log(br.com.jjconsulting.mobile.jjlib.util.Config.TAG, ex.toString());
        }
    }
}
