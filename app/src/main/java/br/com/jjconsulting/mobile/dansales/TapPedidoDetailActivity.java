package br.com.jjconsulting.mobile.dansales;

import android.app.ProgressDialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.model.RetProcess;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.model.TapConnectionType;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapTabType;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionTap;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.dansales.viewModel.TapDetailViewModel;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapPedidoDetailActivity extends BaseActivity {


    private static final String ARG_DETAIL_TAP = "detail_tap";
    private static final String ARG_FORCE_SYNC = "force_sync";
    private static final String ARG_TYPE_ACTION = "type_action";

    private TapConnection tapConnection;
    private TapDetailViewModel mTapDetailViewModel;
    private ArrayList<TapTabType> arrayTapTab;
    private PedidoBusiness pedidoBusiness;
    private TapActionType tapActionType;

    private TapFragmentPagerAdapter mTapFragmentPagerAdapter;
    private TabLayout mTapTabLayout;
    private ViewPager mTapViewPager;
    private ProgressDialog progressDialog;

    private boolean isEditMode;
    private boolean isTapChanged;
    private boolean isForceSync;
    private boolean isFinishActivity;
    private boolean isSaveAndSend;

    private OnPageSelected listener;

    public static Intent newIntent(Context context, TapDetail tapDetail, TapActionType tapActionType) {
        Intent intent = new Intent(context, TapPedidoDetailActivity.class);
        intent.putExtra(ARG_DETAIL_TAP, tapDetail);
        intent.putExtra(ARG_TYPE_ACTION, tapActionType.getValue());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_tap));

        TapDetail mTapDetail = (TapDetail) getIntent().getSerializableExtra(ARG_DETAIL_TAP);
        isEditMode = mTapDetail.isEdit();

        tapActionType = TapActionType.getTapActionType(getIntent().getIntExtra(ARG_TYPE_ACTION, 1));

        mTapDetailViewModel = ViewModelProviders.of(this).get(TapDetailViewModel.class);
        mTapDetailViewModel.getObservableTap().setValue(mTapDetail);
        mTapDetailViewModel.getObservableEditMode().setValue(isEditMode);

        isForceSync = getIntent().getBooleanExtra(ARG_FORCE_SYNC, false);

        getSupportActionBar().setElevation(0);

        pedidoBusiness = new PedidoBusiness();

        mTapTabLayout = findViewById(R.id.tap_tab_layout);
        mTapViewPager = findViewById(R.id.tap_view_pager);

        createDialogProgress();
        createArrayViewPager();

        mTapFragmentPagerAdapter = new TapFragmentPagerAdapter(getSupportFragmentManager());
        mTapViewPager.setAdapter(mTapFragmentPagerAdapter);
        mTapTabLayout.setupWithViewPager(mTapViewPager);

        addListener();
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            if (isTapChanged) {
                saveTap(true, false);
            } else {
                finish();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_tap_menu, menu);

        TapDetail tapDetail = mTapDetailViewModel.getObservableTap().getValue();
        int status = tapDetail.getCabec().getStatus();
        if (isEditMode && (status == 1 || status == 6)) {
            MenuItem menuSave = menu.findItem(R.id.action_save);
            menuSave.setVisible(true);
        }

        if (tapDetail.isCancel()) {
            MenuItem menuSave = menu.findItem(R.id.action_cancel);
            menuSave.setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isEditMode) {
                    if (isTapChanged) {
                        saveTap(true, false);
                    } else {
                        finish();
                    }
                } else {
                    super.onBackPressed();
                }
                return true;
            case R.id.action_cancel:
                dialogsDefault.showDialogQuestion(getString(R.string.detail_etapo_delete), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        cancelTap();
                    }

                    @Override
                    public void onClickNegative() {

                    }
                });
                return true;
            case R.id.action_save:
                ValidationDan validationLetter = new ValidationDan();

                TapDetail tapDetail = mTapDetailViewModel.getObservableTap().getValue();

                if (tapDetail.getCabec().getTapMasterContrato() == null) {
                    validationLetter.addError(getString(R.string.message_etap_send_validation_mc_error));
                }

                if (TextUtils.isNullOrEmpty(tapDetail.getCabec().getAnoMesAcao())) {
                    validationLetter.addError(getString(R.string.message_etap_send_validation_mes_acao_error));
                }

                if (tapDetail.getCabec().getTapRegiao() == null || tapDetail.getCabec().getTapRegiao().getIdRegiao() == 0) {
                    validationLetter.addError(getString(R.string.message_etap_send_validation_regiao_error));
                }

                if (tapDetail.getCabec().getFatLiq() < 1) {
                    validationLetter.addError(getString(R.string.message_etap_send_validation_fat_liq_error));
                }

                if (validationLetter.getMessage().size() > 0) {
                    showDialogValidationError(validationLetter);
                } else {
                    if (isTapChanged) {
                        saveTap(false, true);
                    } else {
                        sendTap();
                    }

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createArrayViewPager() {
        arrayTapTab = new ArrayList<>();
        arrayTapTab.add(TapTabType.CABECALHO_FRAGMENT);
        arrayTapTab.add(TapTabType.ITENS_FRAGMENT);
        arrayTapTab.add(TapTabType.RESUMO_FRAGMENT);

        if (mTapDetailViewModel.getObservableTap().getValue().isAprov() || mTapDetailViewModel.getObservableTap().getValue().isBloq()
        || mTapDetailViewModel.getObservableTap().getValue().isDes()) {
            arrayTapTab.add(TapTabType.APROVACAO_FRAGMENT);
        }

        TapDetail tapDetail = mTapDetailViewModel.getObservableTap().getValue();

        if(tapDetail.getListStep() != null && tapDetail.getListStep().size() > 0){
            arrayTapTab.add(TapTabType.LOG_FRAGMENT);
        }


    }

    private void createConnectionTap() {
        tapConnection = new TapConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                try {
                    if (!isSaveAndSend) {
                        showProgressDialog(false);
                    }

                    if (!isFinishActivity) {
                        String message = "";
                        isTapChanged= false;

                        switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                            case TAP_SAVE:
                                if (!isSaveAndSend) {

                                    if (listener != null) {
                                        listener.onPageSelected(0);
                                        listener = null;
                                    } else {
                                        if (TextUtils.isNullOrEmpty(message)) {
                                            message = getString(R.string.message_etap_save);
                                        }
                                        showMessageSuccess(message);
                                    }
                                } else {
                                    isSaveAndSend = false;
                                    sendTap();
                                }

                                break;
                            case TAP_DENVIA:
                                if (TextUtils.isNullOrEmpty(message)) {
                                    message = getString(R.string.message_etap_send);
                                }

                                showMessageSuccess(message);
                                break;
                            case TAP_DELETE:
                                if (TextUtils.isNullOrEmpty(message)) {
                                    message = getString(R.string.message_etap_delete);
                                }
                                showMessageSuccess(message);
                                break;

                        }
                    } else {
                        finish();
                    }
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                    showMessageError(getString(R.string.message_etap_send_erro));
                }


            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                showProgressDialog(false);

                try {
                    if (!isFinishActivity) {

                        if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                            ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                            if(ManagerSystemUpdate.isRequiredUpadate(TapPedidoDetailActivity.this, errorConnection.getMessage())){
                                return;
                            }

                            showMessageError(errorConnection.getMessage());
                            return;
                        }

                        switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                            case TAP_SAVE:
                            case TAP_DENVIA:
                                showMessageError(getString(R.string.message_etap_save_erro));
                                break;
                            case TAP_DELETE:
                                showMessageError(getString(R.string.message_etap_delete_erro));
                                break;
                        }
                    }
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, ex.toString());
                }
            }
        });

        showProgressDialog(true);

        progressDialog.setOnDismissListener((DialogInterface dialog) -> {
            if (isFinishActivity) {
                finish();
            }
        });
    }

    private void showMessageSuccess(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogsDefault.showDialogMessage(message, DialogsCustom.DIALOG_TYPE_SUCESS, () -> {
                    CurrentActionTap.getInstance().setUpdateListTap(true);
                    finish();

                });
            }
        });
    }

    private void sendTap() {
        this.isFinishActivity = false;

        dialogsDefault.showDialogQuestion(getString(R.string.message_etap_send_aprov), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
            @Override
            public void onClickPositive() {
                createConnectionTap();

                TapDetail tapDetail = mTapDetailViewModel.getObservableTap().getValue();
                tapConnection.sendETap(tapDetail.getCabec(), "I", "0", "", "", 0);
            }

            @Override
            public void onClickNegative() {
                showProgressDialog(false);
            }
        });
    }

    private void saveTap(boolean isFinishActivity, boolean isSaveAndSend) {
        this.isFinishActivity = isFinishActivity;
        this.isSaveAndSend = isSaveAndSend;

        createConnectionTap();
        TapDetail tapDetail = mTapDetailViewModel.getObservableTap().getValue();

        tapConnection.saveETap(tapDetail.getCabec());
    }

    private void cancelTap() {
        this.isFinishActivity = false;
        createConnectionTap();
        tapConnection.deleteETap(mTapDetailViewModel.getObservableTap().getValue().getCabec().getId());
    }

    private void addListener() {
        mTapViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                listener = (OnPageSelected) mTapFragmentPagerAdapter
                        .getFragment(position);
                if (listener != null) {
                    int sizeItens = mTapDetailViewModel.getObservableTap().getValue().getItens().size();

                    if (arrayTapTab.get(position) == TapTabType.ITENS_FRAGMENT && sizeItens == 0 && isTapChanged) {
                        saveTap(false, false);
                    } else {
                        listener.onPageSelected(position);
                        listener = null;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTapDetailViewModel.getObservableTap().observe(this, pedidoViewModel -> {
            isTapChanged = true;
        });
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

    private void showProgressDialog(boolean isShow) {
        if (getWindow().getDecorView().isShown()) {
            if (!isShow) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } else {
                progressDialog.show();
            }
        }
    }

    private void showDialogValidationError(ValidationDan validation) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();

            TapValidationDialogFragment tapValidationDialogFragment =
                    TapValidationDialogFragment.newInstance(validation);
            tapValidationDialogFragment.show(fragmentManager, "");
            fragmentManager.executePendingTransactions();

            tapValidationDialogFragment.getDialog().setOnDismissListener(dialogInterface -> {

            });
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }


    private class TapFragmentPagerAdapter extends FragmentPagerAdapter {

        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;

        public TapFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTags = new HashMap<>();
            mFragmentManager = fm;
        }

        @Override
        public TapBaseFragment getItem(int position) {
            switch (arrayTapTab.get(position)) {
                case CABECALHO_FRAGMENT:
                    return TapCabecalhoFragment.newInstance();
                case ITENS_FRAGMENT:
                    return TapItensFragment.newInstance();
                case RESUMO_FRAGMENT:
                    return TapResumoFragment.newInstance();
                case APROVACAO_FRAGMENT:
                    return TapAprovacaoFragment.newInstance();
                case LOG_FRAGMENT:
                    return TapLogFragment.newInstance();
                case EMPTY:
                    return TapEmptyFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return arrayTapTab.size();
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
            switch (arrayTapTab.get(position)) {
                case EMPTY:
                    return getString(R.string.tab_pedido_cabecalho);
                case APROVACAO_FRAGMENT:
                    return getString(R.string.tab_pedido_aprovacao);
                case CABECALHO_FRAGMENT:
                    return getString(R.string.tab_pedido_cabecalho);
                case ITENS_FRAGMENT:
                    return getString(R.string.tab_pedido_itens);
                case OBSERVACAO_FRAGMENT:
                    return getString(R.string.tab_pedido_observacao);
                case RESUMO_FRAGMENT:
                    return getString(R.string.tab_pedido_resumo);
                case LOG_FRAGMENT:
                    return getString(R.string.  title_log_tap);
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

}
