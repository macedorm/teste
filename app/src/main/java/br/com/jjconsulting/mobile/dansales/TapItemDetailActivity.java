package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.adapter.TapItemAnexosAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.database.TapAcaoItemDao;
import br.com.jjconsulting.mobile.dansales.database.TapCatItemDao;
import br.com.jjconsulting.mobile.dansales.database.TapTipoItemDao;
import br.com.jjconsulting.mobile.dansales.model.Anexo;
import br.com.jjconsulting.mobile.dansales.model.RetProcess;
import br.com.jjconsulting.mobile.dansales.model.TapAcao;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapItem;
import br.com.jjconsulting.mobile.dansales.model.TapItemRules;
import br.com.jjconsulting.mobile.dansales.model.TapProdCateg;
import br.com.jjconsulting.mobile.dansales.model.TapTipoInvest;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionTap;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.CustomTextInputLayout;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapItemDetailActivity extends BaseActivity {

    private static final String ARG_ITEM = "item";
    private static final String ARG_EDIT = "edit_item";

    private TapConnection tapConnection;

    private TapDetail mTapDetail;
    private TapItem mTapItemNew;

    private ViewGroup mTapAcaoItemFormWrapper;
    private ViewGroup mTapCatItemFormWrapper;
    private ViewGroup mTapAnexoItemFormWrapper;

    private Spinner mTapTipoItemSpinner;
    private Spinner mTapAcaoItemSpinner;
    private Spinner mTapCatItemSpinner;

    private SpinnerArrayAdapter<TapTipoInvest> mTapTipoItemSpinnerAdapter;
    private SpinnerArrayAdapter<TapAcao> mTapAcaoItemSpinnerAdapter;
    private SpinnerArrayAdapter<TapProdCateg> mTapCatItemSpinnerAdapter;

    private CustomTextInputLayout mObsCustomTextInputLayout;
    private CustomTextInputLayout mValorApuradoCustomTextInputLayout;
    private CustomTextInputLayout mValorEstimadoCustomTextInputLayout;

    private TextInputEditText mValorApuradoEditText;
    private TextInputEditText mValorEstimadoEditText;
    private TextInputEditText mObsEditText;

    private Button mIncluirOuSalvarButton;

    private RecyclerView mTapItemAnexosRecyclerView;
    private TapItemAnexosAdapter mTapItemAnexosAdapter;

    private ProgressDialog progressDialog;

    private int positionItemSelected;

    private boolean isNew;

    /**
     * Use it to edit an item.
     */
    public static Intent newIntent(Context context, TapDetail tapDetail, int position) {
        Intent intent = new Intent(context, TapItemDetailActivity.class);
        intent.putExtra(ARG_ITEM, tapDetail);
        intent.putExtra(ARG_EDIT, position);
        return intent;
    }

    public static Intent newResultIntent(TapItem mTapItemNew, int position) {
        Intent intent = new Intent();
        intent.putExtra(ARG_ITEM, mTapItemNew);
        intent.putExtra(ARG_EDIT, position);
        return intent;
    }

    public static TapItem getTapItemFromResultIntent(Intent intent) {
        return (TapItem) intent.getSerializableExtra(ARG_ITEM);
    }

    public static int getPositionFromResultIntent(Intent intent) {
        return intent.getIntExtra(ARG_EDIT, -1);
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_tap_item_detail);

        mValorApuradoCustomTextInputLayout = findViewById(R.id.valor_apurado_text_input_layout);
        mValorEstimadoCustomTextInputLayout = findViewById(R.id.valor_estimado_text_input_layout);

        mValorApuradoEditText = findViewById(R.id.valor_apurado_edit_text);
        mValorEstimadoEditText = findViewById(R.id.valor_estimado_edit_text);
        mObsEditText = findViewById(R.id.obs_edit_text);

        mObsCustomTextInputLayout = findViewById(R.id.obs_text_input_layout);

        mTapTipoItemSpinner = findViewById(R.id.tipo_item_tap_spinner);
        mTapAcaoItemSpinner = findViewById(R.id.acao_item_tap_spinner);
        mTapCatItemSpinner = findViewById(R.id.cat_item_tap_spinner);

        mTapAcaoItemFormWrapper = findViewById(R.id.acao_item_tap_form_wrapper);
        mTapCatItemFormWrapper = findViewById(R.id.cat_item_tap_form_wrapper);
        mTapAnexoItemFormWrapper = findViewById(R.id.anexo_item_tap_form_wrapper);

        mIncluirOuSalvarButton = findViewById(R.id.incluir_button);
        mIncluirOuSalvarButton.requestFocus();

        mTapDetail = (TapDetail) getIntent().getSerializableExtra(ARG_ITEM);
        positionItemSelected = getIntent().getIntExtra(ARG_EDIT, -1);

        mTapItemAnexosRecyclerView = findViewById(R.id.anexos_recycler_view);
        mTapItemAnexosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mTapItemAnexosRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mTapItemAnexosRecyclerView.addItemDecoration(divider);

        ArrayList<Anexo> anexos = new ArrayList<>();

        mTapItemAnexosAdapter = new TapItemAnexosAdapter(this, anexos);
        mTapItemAnexosRecyclerView.setAdapter(mTapItemAnexosAdapter);
        mTapItemAnexosRecyclerView.setNestedScrollingEnabled(false);

        if (positionItemSelected == -1) {
            isNew = true;
            mValorApuradoCustomTextInputLayout.setVisibility(View.VISIBLE);
            mValorEstimadoCustomTextInputLayout.setVisibility(View.GONE);
            mValorApuradoCustomTextInputLayout.setHint(getString(R.string.tap_item_valor_hint));
            mValorEstimadoCustomTextInputLayout.setHint(getString(R.string.tap_item_valor_hint));
        } else {
            isNew = false;
        }

        mIncluirOuSalvarButton.setOnClickListener(view -> saveItem());

        if (mTapDetail.isEdit()) {
            getSupportActionBar().setTitle(getString(isNew ?
                    R.string.title_add_item : R.string.title_edit_item));
        } else {
            getSupportActionBar().setTitle(getString(R.string.title_visualizar_item));
        }

        mIncluirOuSalvarButton.setText(getString(isNew ?
                R.string.insert_new_item : R.string.save_existing_item));

        mTapTipoItemSpinner.post(() ->
                addListeners());

        tapConnection = new TapConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgressDialog(false);
                try {
                    ValidationLetter validationLetter = gson.fromJson(response, ValidationLetter.class);

                    if (validationLetter.getStatus() == Connection.CREATED || validationLetter.getStatus() == Connection.SUCCESS) {
                        {
                            TapItem tapItem;

                            if (isNew) {
                                mTapItemNew.setId(Integer.parseInt(validationLetter.getData().get("id").toString()));
                                tapItem = mTapItemNew;
                            } else {
                                tapItem = mTapDetail.getItens().get(positionItemSelected);
                            }

                            if (validationLetter.getData().containsKey("urlAnexo")) {
                                tapItem.setUrlAnexo(validationLetter.getData().get("urlAnexo").toString());
                            }

                            CurrentActionTap.getInstance().setUpdateListTap(true);

                            Intent resultIntent = newResultIntent(tapItem, positionItemSelected);
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                    } else {
                        dialogsDefault.showDialogMessage(validationLetter.getMessage(), dialogsDefault.DIALOG_TYPE_ERROR, null);
                    }
                } catch (Exception ex) {
                    showMessageError(getString(R.string.message_etap_add_item_error));
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                showProgressDialog(false);

                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                    ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(TapItemDetailActivity.this, errorConnection.getMessage())){
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                showMessageError(getString(R.string.message_etap_add_item_error));
            }
        });

        createDialogProgress();
        bindItens();
    }

    private void bindItens() {
        setupTapTipoItemSpinner();
        setupCatTipEmptyItemSpinner();
        setupAcaoTipEmptyItemSpinner();


        if (!isNew) {
            setupAcaoTipoItemSpinner(mTapDetail.getItens().get(positionItemSelected).getTapTipoInvest().getIdTipoInvest());
            setupCatTipoItemSpinner();

            mValorEstimadoEditText.setText(String.valueOf(mTapDetail.getItens().get(positionItemSelected).getVlEst()));
            mValorApuradoEditText.setText(String.valueOf(mTapDetail.getItens().get(positionItemSelected).getVlApur()));

            if (!TextUtils.isNullOrEmpty(mTapDetail.getItens().get(positionItemSelected).getObs())) {
                mObsEditText.setText(mTapDetail.getItens().get(positionItemSelected).getObs());
            }
        } else {
            resetValores();
        }

        enableFields();
        showFields();
    }

    private void enableFields() {
        if (!isNew) {
            mTapCatItemSpinner.setEnabled(false);
            mTapAcaoItemSpinner.setEnabled(false);
            mTapTipoItemSpinner.setEnabled(false);

            mValorEstimadoEditText.setEnabled(mTapDetail.getItens().get(positionItemSelected).getTapItemRules().isVlrEst());
            mValorApuradoEditText.setEnabled(mTapDetail.getItens().get(positionItemSelected).getTapItemRules().isVlrApur());
            mObsEditText.setEnabled((mTapDetail.getItens().get(positionItemSelected).getTapItemRules().isObs()
                    && mTapDetail.isEdit()));

        } else {
            int index = mTapTipoItemSpinner.getSelectedItemPosition();

            if (index == 0) {
                mValorEstimadoEditText.setEnabled(false);
                mValorApuradoEditText.setEnabled(false);
            } else {
                TapTipoInvest tapTipoInvest = (TapTipoInvest) mTapTipoItemSpinner.getSelectedItem();

                if (tapTipoInvest.getVlest().equals("S")) {
                    mValorEstimadoCustomTextInputLayout.setVisibility(View.VISIBLE);
                    mValorApuradoCustomTextInputLayout.setVisibility(View.GONE);
                    mValorEstimadoEditText.setText("");
                    mValorEstimadoEditText.setEnabled(true);
                    mValorApuradoEditText.setText("0.0");
                } else {
                    mValorEstimadoCustomTextInputLayout.setVisibility(View.GONE);
                    mValorApuradoCustomTextInputLayout.setVisibility(View.VISIBLE);
                    mValorEstimadoEditText.setText("0.0");
                    mValorApuradoEditText.setEnabled(true);
                    mValorApuradoEditText.setText("");
                }
            }
        }
    }

    private void showFields() {
        mIncluirOuSalvarButton.setVisibility(mTapDetail.isEdit() ? View.VISIBLE : View.GONE);

        if (!isNew) {
            mTapAnexoItemFormWrapper.setVisibility(mTapItemAnexosAdapter.getAnexos().size() == 0 ? View.GONE : View.VISIBLE);
            mObsCustomTextInputLayout.setVisibility((!mTapDetail.isEdit() &&
                    TextUtils.isNullOrEmpty(mTapDetail.getItens().get(positionItemSelected).getObs())) ? View.GONE : View.VISIBLE);
        } else {
            mTapAnexoItemFormWrapper.setVisibility(View.GONE);
        }

        //mTapAcaoItemFormWrapper.setVisibility((mTapAcaoItemSpinnerAdapter == null || mTapAcaoItemSpinnerAdapter.getCount() == 0) ? View.GONE : View.VISIBLE);
        //mTapCatItemFormWrapper.setVisibility((mTapCatItemSpinnerAdapter == null || mTapCatItemSpinnerAdapter.getCount() == 0) ? View.GONE : View.VISIBLE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_item_tap_menu, menu);

        MenuItem menuSave = menu.findItem(R.id.action_save);
        menuSave.setVisible(mTapDetail.isEdit());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                saveItem();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }


    private void addListeners() {
        mTapTipoItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    resetTapAcapItemSpinner();
                    resetTapCatItemSpinner();
                } else {
                    TapTipoInvest tapTipoItem = TapTipoInvest.class.cast(mTapTipoItemSpinner.getSelectedItem());
                    setupAcaoTipoItemSpinner(tapTipoItem.getIdTipoInvest());
                    setupCatTipoItemSpinner();
                }

                enableFields();
                showFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void cancel() {
        dialogsDefault.showDialogQuestion(
                getString(R.string.add_item_cancel),
                dialogsDefault.DIALOG_TYPE_QUESTION,
                new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }

                    @Override
                    public void onClickNegative() {
                    }
                });
    }

    private void saveItem() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        if (isNew) {
            insertItemTap();
        } else {
            saveItemTap();
        }

    }

    private void setupTapTipoItemSpinner() {
        TapTipoItemDao tapTipoItemDao = new TapTipoItemDao(this);
        String unNeg = Current.getInstance(this).getUnidadeNegocio().getCodigo();
        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tapTipoItemDao.get(unNeg, mTapDetail.getItens()).toArray(),
                getString(R.string.select_tap_tipo_item));

        if (objects.length > 0) {
            mTapTipoItemSpinnerAdapter = new SpinnerArrayAdapter<TapTipoInvest>(TapItemDetailActivity.this, objects, true) {
                @Override
                public String getItemDescription(TapTipoInvest item) {
                    return item.getDescricao();
                }
            };
            mTapTipoItemSpinner.setAdapter(mTapTipoItemSpinnerAdapter);

            if (!isNew) {
                mTapTipoItemSpinner.setEnabled(false);
                setTapTipoItemSpinner();
            }
        }
    }

    private void setTapTipoItemSpinner() {
        TapTipoInvest tapTipoInvest = mTapDetail.getItens().get(positionItemSelected).getTapTipoInvest();

        for (int ind = 1; ind < mTapTipoItemSpinner.getCount(); ind++) {
            TapTipoInvest tapTipoInvestSpinner = TapTipoInvest.class.cast(mTapTipoItemSpinnerAdapter.getItem(ind));

            if (tapTipoInvest.getIdTipoInvest() == tapTipoInvestSpinner.getIdTipoInvest()) {
                mTapTipoItemSpinner.setSelection(ind);
                ind = mTapTipoItemSpinnerAdapter.getCount();
            }
        }
    }

    private void setupAcaoTipEmptyItemSpinner(){
        Object[] objectsEmpty = new Object[0];

        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(objectsEmpty,
                getString(R.string.select_tap_acao_item));

        mTapAcaoItemSpinnerAdapter = new SpinnerArrayAdapter<TapAcao>(TapItemDetailActivity.this, objects, true) {
            @Override
            public String getItemDescription(TapAcao item) {
                return item.getDescricao();
            }
        };
        mTapAcaoItemSpinner.setAdapter(mTapAcaoItemSpinnerAdapter);
    }

    private void setupAcaoTipoItemSpinner(int id) {
        TapAcaoItemDao tapAcaoItemDao = new TapAcaoItemDao(this);
        String unNeg = Current.getInstance(this).getUnidadeNegocio().getCodigo();
        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tapAcaoItemDao.get(unNeg, id).toArray(),
                getString(R.string.select_tap_acao_item));

        if (objects.length > 0) {
            mTapAcaoItemSpinnerAdapter = new SpinnerArrayAdapter<TapAcao>(TapItemDetailActivity.this, objects, true) {
                @Override
                public String getItemDescription(TapAcao item) {
                    return item.getDescricao();
                }
            };
            mTapAcaoItemSpinner.setAdapter(mTapAcaoItemSpinnerAdapter);

            if (!isNew) {
                mTapAcaoItemSpinner.setEnabled(false);
                setAcaoItemSpinner();
            }
        }
    }

    private void setAcaoItemSpinner() {
        TapAcao tapAcao = mTapDetail.getItens().get(positionItemSelected).getTapAcao();

        for (int ind = 1; ind < mTapAcaoItemSpinner.getCount(); ind++) {
            TapAcao tapAcaoSpinner = TapAcao.class.cast(mTapAcaoItemSpinnerAdapter.getItem(ind));

            if (tapAcao.getIdAcao() == tapAcaoSpinner.getIdAcao()) {
                mTapAcaoItemSpinner.setSelection(ind);
                ind = mTapAcaoItemSpinner.getCount();
            }
        }
    }

    private void setupCatTipEmptyItemSpinner(){
        Object[] objectsEmpty = new Object[0];

        mTapCatItemSpinnerAdapter = new SpinnerArrayAdapter<TapProdCateg>(TapItemDetailActivity.this,  SpinnerArrayAdapter.makeObjectsWithHint(objectsEmpty,
                getString(R.string.select_tap_cat_item)), true) {
            @Override
            public String getItemDescription(TapProdCateg item) {
                return item.getDescricao();
            }
        };
        mTapCatItemSpinner.setAdapter(mTapCatItemSpinnerAdapter);
    }

    private void setupCatTipoItemSpinner() {
        TapCatItemDao tapCatItemDao = new TapCatItemDao(this);
        String unNeg = Current.getInstance(this).getUnidadeNegocio().getCodigo();
        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tapCatItemDao.get(unNeg).toArray(),
                getString(R.string.select_tap_cat_item));

        if (objects.length > 0) {
            mTapCatItemSpinnerAdapter = new SpinnerArrayAdapter<TapProdCateg>(TapItemDetailActivity.this, objects, true) {
                @Override
                public String getItemDescription(TapProdCateg item) {
                    return item.getDescricao();
                }
            };
            mTapCatItemSpinner.setAdapter(mTapCatItemSpinnerAdapter);

            if (!isNew) {
                setCatTipoItemSpinner();
            }
        }
    }

    private void setCatTipoItemSpinner() {
        TapProdCateg tapProdCateg = mTapDetail.getItens().get(positionItemSelected).getTapProdCateg();

        for (int ind = 1; ind < mTapCatItemSpinner.getCount(); ind++) {
            TapProdCateg tapProdCategSpinner = TapProdCateg.class.cast(mTapCatItemSpinnerAdapter.getItem(ind));

            if (tapProdCateg.getIdCategProd() == tapProdCategSpinner.getIdCategProd()) {
                mTapCatItemSpinner.setSelection(ind);
                ind = mTapCatItemSpinnerAdapter.getCount();
            }
        }
    }

    private void resetTapAcapItemSpinner() {
        mTapAcaoItemSpinner.setAdapter(null);
        mTapAcaoItemSpinnerAdapter = null;
        setupAcaoTipEmptyItemSpinner();
    }

    private void resetTapCatItemSpinner() {
        mTapCatItemSpinner.setAdapter(null);
        mTapCatItemSpinnerAdapter = null;
        setupCatTipEmptyItemSpinner();
    }

    private void saveItemTap() {
        TapItem tapItem = mTapDetail.getItens().get(positionItemSelected);
        tapItem.setObs(mObsEditText.getText().toString());

        if (tapItem.getTapItemRules().isVlrEst()) {
            if (mValorEstimadoEditText.getText().length() == 0 && mValorEstimadoEditText.getText().equals("0.0")) {
                dialogsDefault.showDialogMessage(getString(R.string.message_etap_vl_est_validation_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                return;
            } else {
                tapItem.setVlEst(Double.parseDouble(mValorEstimadoEditText.getText().toString()));
            }
        }

        if (tapItem.getTapItemRules().isVlrApur()) {
            if (mValorApuradoEditText.getText().length() == 0 && mValorApuradoEditText.getText().equals("0.0")) {
                dialogsDefault.showDialogMessage(getString(R.string.message_etap_vl_apu_validation_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                return;
            } else {
                tapItem.setVlApur(Double.parseDouble(mValorApuradoEditText.getText().toString()));
            }
        }

        showProgressDialog(true);

        tapConnection.insertItemETap(tapItem, mTapDetail.getCabec());

    }

    private void insertItemTap() {
        String campoValor = mValorApuradoEditText.getText().toString();
        double valor = 0;

        try {
            if (campoValor.equals("0.0")) {
                campoValor = mValorEstimadoEditText.getText().toString();
            }

            valor = Double.parseDouble(campoValor);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.getMessage());
        }


        ValidationDan validationLetter = new ValidationDan();

        if (mTapTipoItemSpinner.getSelectedItemPosition() == 0) {
            validationLetter.addError(getString(R.string.message_etap_send_validation_item_tipo_error));
        }

        if (mTapAcaoItemSpinner.getSelectedItemPosition() == 0) {
            validationLetter.addError(getString(R.string.message_etap_send_validation_item_acao_error));
        }

        if (mTapCatItemSpinner.getSelectedItemPosition() == 0) {
            validationLetter.addError(getString(R.string.message_etap_send_validation_item_cat_prod_error));
        }

        if ((campoValor.length() == 0 || valor == 0)) {
            validationLetter.addError(getString(R.string.message_etap_send_validation_item_valor_error));
        }


        if (validationLetter.getMessage().size() > 0) {
            showDialogValidationError(validationLetter);
        } else {
            showProgressDialog(true);

            mTapItemNew = new TapItem();
            mTapItemNew.setIdTap(mTapDetail.getCabec().getId());

            mTapItemNew.setTapProdCateg((TapProdCateg) mTapCatItemSpinner.getSelectedItem());
            mTapItemNew.setTapAcao((TapAcao) mTapAcaoItemSpinner.getSelectedItem());
            mTapItemNew.setTapTipoInvest((TapTipoInvest) mTapTipoItemSpinner.getSelectedItem());
            mTapItemNew.setObs(mObsEditText.getText().toString());

            TapItemRules tapItemRules = new TapItemRules();
            tapItemRules.setArq(true);
            tapItemRules.setDel(true);
            tapItemRules.setObs(true);

            if (mTapItemNew.getTapTipoInvest().getVlest().equals("S")) {
                mTapItemNew.setValorEstimado(true);
                tapItemRules.setVlrEst(true);
            } else {
                mTapItemNew.setValorEstimado(false);
                tapItemRules.setVlrApur(true);
            }

            mTapItemNew.setTapItemRules(tapItemRules);

            mTapItemNew.setVlEst(Double.parseDouble(mValorEstimadoEditText.getText().toString()));
            mTapItemNew.setVlApur(Double.parseDouble(mValorApuradoEditText.getText().toString()));

            tapConnection.insertItemETap(mTapItemNew, mTapDetail.getCabec());
        }
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

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

    private void resetValores() {
        mValorEstimadoEditText.setText("0.0");
        mValorApuradoEditText.setText("0.0");
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

}

