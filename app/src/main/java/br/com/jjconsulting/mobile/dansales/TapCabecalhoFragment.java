package br.com.jjconsulting.mobile.dansales;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.TapListAdapter;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.model.AlteraMasterContrato;
import br.com.jjconsulting.mobile.dansales.model.TapCabec;
import br.com.jjconsulting.mobile.dansales.model.TapComboMesAcao;
import br.com.jjconsulting.mobile.dansales.model.TapComboRegiao;
import br.com.jjconsulting.mobile.dansales.model.TapConnectionType;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapMasterContrato;
import br.com.jjconsulting.mobile.dansales.model.TapMaterInfo;
import br.com.jjconsulting.mobile.dansales.model.TapRegiao;
import br.com.jjconsulting.mobile.dansales.model.TapStatus;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.CustomTextInputLayout;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextWatcherUtils;

public class TapCabecalhoFragment extends TapBaseFragment implements OnPageSelected {

    private TapConnection tapConnection;

    private DatePickerDialog.OnDateSetListener mEntregueEmDateSetListener;
    private DatePickerDialog mEntregueEmdatePickerDialog;

    private ViewGroup mMesAcaoFormWrapper;
    private ViewGroup mRegiaoFormWrapper;
    private ViewGroup mClienteFormWrapper;

    private TextView mClienteCodigoTextView;
    private TextView mClienteNomeTextView;

    private Spinner mMasterContratoSpinner;
    private Spinner mMesAcaoSpinner;
    private Spinner mRegiaoSpinner;

    private SpinnerArrayAdapter<TapMaterInfo> mMasterContratoSpinnerAdapter;
    private SpinnerArrayAdapter<TapComboMesAcao> mMesAcaoSpinnerAdapter;
    private SpinnerArrayAdapter<TapComboRegiao> mRegiaoSpinnerAdapter;

    private EditText mNumAcordoCliEditText;
    private EditText mConsSaldoEditText;
    private EditText mFatLiqEditText;
    private EditText mObsEditText;
    private EditText mNumEditText;
    private EditText mStatusEditText;
    private EditText mDealEditText;


    private TextWatcher mObsTextWatcher;
    private TextWatcher mNumAcordoCliTextWatcher;
    private TextWatcher mFatLiqTextWatcher;

    private CustomTextInputLayout mNumAcordoCliTextInputLayout;
    private CustomTextInputLayout mConsSaldoTextInputLayout;
    private CustomTextInputLayout mFatLiqInputLayout;
    private CustomTextInputLayout mObsTextInputLayout;
    private CustomTextInputLayout mNumTextInputLayout;
    private CustomTextInputLayout mStatusTextInputLayout;
    private CustomTextInputLayout mDealTextInputLayout;

    private TextView mDataPrevPagEmTextView;
    private TextView mDataTextView;

    private LinearLayout mDataLinearLayout;
    private LinearLayout mDataPrevLinearLayout;
    private LinearLayout mProgressLinearLayout;

    private ScrollView mBaseScrollView;

    private ImageView mDataImageView;
    private ImageView mDataPrevImageView;

    private ProgressDialog progressDialog;

    private int oldIndexSpinner;


    public TapCabecalhoFragment() {
    }

    public static TapCabecalhoFragment newInstance() {
        return new TapCabecalhoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tap_cabecalho, container,
                false);

        mProgressLinearLayout = view.findViewById(R.id.loading_linear_layout);
        mBaseScrollView = view.findViewById(R.id.base_scroll_view);

        mClienteFormWrapper = view.findViewById(R.id.cliente_form_wrapper);
        mMesAcaoFormWrapper = view.findViewById(R.id.tap_mes_acao_form_wrapper);
        mRegiaoFormWrapper = view.findViewById(R.id.tap_regiao_form_wrapper);

        mClienteCodigoTextView = view.findViewById(R.id.tap_cliente_codigo_text_view);
        mClienteNomeTextView = view.findViewById(R.id.tap_cliente_nome_text_view);

        mMasterContratoSpinner = view.findViewById(R.id.tap_master_contrato_spinner);
        mMesAcaoSpinner = view.findViewById(R.id.tap_mes_acao_spinner);
        mRegiaoSpinner = view.findViewById(R.id.tap_regiao_spinner);

        mNumAcordoCliEditText = view.findViewById(R.id.tap_num_acordo_cli_edit_text);
        mConsSaldoEditText = view.findViewById(R.id.tap_cons_saldo_edit_text);
        mFatLiqEditText = view.findViewById(R.id.tap_fat_liq_investimento_edit_text);
        mObsEditText = view.findViewById(R.id.tap_obs_edit_text);
        mNumEditText = view.findViewById(R.id.tap_num_edit_text);
        mStatusEditText = view.findViewById(R.id.tap_status_edit_text);
        mDealEditText = view.findViewById(R.id.tap_deal_edit_text);

        mNumAcordoCliTextInputLayout = view.findViewById(R.id.tap_num_acordo_cli_text_input_layout);
        mConsSaldoTextInputLayout = view.findViewById(R.id.tap_cons_saldo_text_input_layout);
        mFatLiqInputLayout = view.findViewById(R.id.tap_fat_liq_text_input_layout);
        mObsTextInputLayout = view.findViewById(R.id.tap_obs_text_input_layout);
        mNumTextInputLayout = view.findViewById(R.id.tap_num_text_input_layout);
        mStatusTextInputLayout = view.findViewById(R.id.tap_status_text_input_layout);
        mDealTextInputLayout = view.findViewById(R.id.tap_deal_text_input_layout);

        mDataPrevPagEmTextView = view.findViewById(R.id.tap_data_prev_text_view);
        mDataTextView = view.findViewById(R.id.tao_data_text_view);

        mNumAcordoCliTextInputLayout.loadStyle();
        mConsSaldoTextInputLayout.loadStyle();
        mFatLiqInputLayout.loadStyle();
        mNumTextInputLayout.loadStyle();
        mStatusTextInputLayout.loadStyle();
        mDealTextInputLayout.loadStyle();
        mClienteNomeTextView.requestFocus();

        mDataLinearLayout = view.findViewById(R.id.tap_data_linearlayout);
        mDataPrevLinearLayout = view.findViewById(R.id.tap_data_prev_pag_linearlayout);

        mDataImageView = view.findViewById(R.id.tap_data_image_view);
        mDataPrevImageView = view.findViewById(R.id.tap_data_prev_image_view);

        createDialogProgress();

        bindPedido(getCurrentTap());
        mMasterContratoSpinner.post(() -> addListeners());

        return view;
    }

    @Override
    public void onPageSelected(int position) {
        removeListener();
        bindPedido(getCurrentTap());
        mMasterContratoSpinner.post(() -> addListeners());
    }


    private void bindPedido(TapDetail tapDetail) {

        oldIndexSpinner = 0;

        TapCabec tapCabec = tapDetail.getCabec();

        if (tapCabec != null) {

            if (tapCabec.getTapCliente() != null) {
                mClienteCodigoTextView.setText(tapCabec.getTapCliente().getCodClie());
                mClienteNomeTextView.setText(tapCabec.getTapCliente().getNomeClie());
            }

            if (!mClienteFormWrapper.hasOnClickListeners()) {
                mClienteFormWrapper.setOnClickListener(view -> {
                    startActivity(ClienteDetailActivity.newIntent(getActivity(),
                            tapCabec.getTapCliente().getCodClie(), false));
                });
            }

            if (tapCabec.getNumAcordCli() != null) {
                mNumAcordoCliEditText.setText(tapCabec.getNumAcordCli());
            }

            if(tapCabec.getFatLiq() >= 1 || !isEditMode()){
                mFatLiqEditText.setText(tapCabec.getFatLiq() + "");
            }

            mNumEditText.setText(tapDetail.getCabec().getCodigo());
            mStatusEditText.setText(getResources().getStringArray(R.array.etap_status_array)[tapCabec.getStatus() - 1]);

            if (tapDetail.getCabec().getContrDeal() != null) {
                mDealEditText.setText(tapDetail.getCabec().getContrDeal());
            }

            if (!TextUtils.isNullOrEmpty(tapDetail.getCabec().getDataInc())) {
                mDataTextView.setText(tapDetail.getCabec().getDataInc());
            }

            if (!TextUtils.isNullOrEmpty(tapDetail.getCabec().getDataPrevPagto())) {
                mDataPrevPagEmTextView.setText(tapDetail.getCabec().getDataPrevPagto());
            }

            if (!TextUtils.isNullOrEmpty(tapDetail.getCabec().getObs())) {
                mObsEditText.setText(tapDetail.getCabec().getObs());
            }

            if(!TextUtils.isNullOrEmpty(tapDetail.getNomeConsSaldo())){
                mConsSaldoEditText.setText(tapDetail.getNomeConsSaldo());
            }


            try {
                // poputate spinners
                setupMasterContrato(getCurrentTap());
                setupAnoMes(getCurrentTap());
                setupRegiao(getCurrentTap());

            } catch (Exception ex) {
                LogUser.log(Config.TAG, ex.toString());
            }


            showHideFields(tapDetail);
            enableFileds(isEditMode());
        }
    }

    private void removeListener(){
        mMasterContratoSpinner.setOnItemSelectedListener(null);
        mMesAcaoSpinner.setOnItemSelectedListener(null);
        mRegiaoSpinner.setOnItemSelectedListener(null);

        if(mNumAcordoCliTextWatcher != null){
            mNumAcordoCliEditText.removeTextChangedListener(mNumAcordoCliTextWatcher);
        }

        if(mFatLiqTextWatcher != null){
            mFatLiqEditText.removeTextChangedListener(mFatLiqTextWatcher);
        }

        if(mObsTextWatcher != null){
            mObsEditText.removeTextChangedListener(mObsTextWatcher);
        }


    }

    private void addListeners() {

        mMasterContratoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0 && oldIndexSpinner != i) {
                    changeMasterContrato(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mMesAcaoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TapDetail tapDetail = getCurrentTap();
                if (i > 0) {
                    tapDetail.getCabec().setAnoMesAcao(tapDetail.getListComboAnoMes().get(i - 1).getCod());
                    setCurrentTap(tapDetail);
                } else {
                    tapDetail.getCabec().setAnoMesAcao(null);
                }
                setCurrentTap(tapDetail);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mRegiaoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TapDetail tapDetail = getCurrentTap();
                if (i > 0) {
                    TapComboRegiao tapComboRegiao = tapDetail.getListComboRegiao().get(i - 1);

                    TapRegiao tapRegiao = new TapRegiao();
                    tapRegiao.setAtivo(true);
                    tapRegiao.setCbuRegiao(tapDetail.getCabec().getCbu());
                    tapRegiao.setIdRegiao(Integer.parseInt(tapComboRegiao.getCod()));
                    tapRegiao.setDescricao(tapComboRegiao.getNome());
                    tapDetail.getCabec().setTapRegiao(tapRegiao);
                } else {
                    tapDetail.getCabec().setTapRegiao(null);
                }
                setCurrentTap(tapDetail);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mNumAcordoCliTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            TapDetail tapDetail = getCurrentTap();
            if (tapDetail.getCabec().getNumAcordCli() == null || !editable.toString().equals(String.valueOf(tapDetail.getCabec().getNumAcordCli()))) {
                try {
                    tapDetail.getCabec().setNumAcordCli(editable.toString());
                } catch (Exception ex) {
                    tapDetail.getCabec().setNumAcordCli("");
                }
                setCurrentTap(tapDetail);

            }
        });

        mNumAcordoCliEditText.addTextChangedListener(mNumAcordoCliTextWatcher);

        mFatLiqTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            TapDetail tapDetail = getCurrentTap();
            if (!editable.toString().equals(String.valueOf(tapDetail.getCabec().getFatLiq()))) {
                try {
                    tapDetail.getCabec().setFatLiq(Double.parseDouble(editable.toString().replace(",", ".")));
                    setCurrentTap(tapDetail);
                } catch (Exception ex) {
                    tapDetail.getCabec().setFatLiq(0);
                }

                setCurrentTap(tapDetail);

            }
        });
        mFatLiqEditText.addTextChangedListener(mFatLiqTextWatcher);

        mObsTextWatcher = TextWatcherUtils.buildWithAfterTextChanged(editable -> {
            TapDetail tapDetail = getCurrentTap();
            if (tapDetail.getCabec().getObs() == null || !editable.toString().equals(String.valueOf(tapDetail.getCabec().getObs()))) {
                try {
                    tapDetail.getCabec().setObs(editable.toString());
                } catch (Exception ex) {
                    tapDetail.getCabec().setObs("");
                }
                setCurrentTap(tapDetail);

            }
        });

        mObsEditText.addTextChangedListener(mObsTextWatcher);

    }

    private void enableFileds(boolean value) {
        TapDetail tapDetail = getCurrentTap();

        if (value && tapDetail.getItens() != null && tapDetail.getItens().size() > 0) {
            if(!tapDetail.getCabec().isRascunho()){
                mMasterContratoSpinner.setEnabled(false);
                mMesAcaoSpinner.setEnabled(false);
                mRegiaoSpinner.setEnabled(false);
            } else {
                mMasterContratoSpinner.setEnabled(true);
                mMesAcaoSpinner.setEnabled(true);
                mRegiaoSpinner.setEnabled(true);
            }

        } else {
            mMasterContratoSpinner.setEnabled(value);
            mMesAcaoSpinner.setEnabled(value);
            mRegiaoSpinner.setEnabled(value);

        }

        if (tapDetail.getCabec().getStatus() == TapStatus.EM_DIGITACAO) {
            mFatLiqEditText.setEnabled(value);
        } else {
            mFatLiqEditText.setEnabled(false);
        }

        mNumAcordoCliEditText.setEnabled(value);
        mObsEditText.setEnabled(value);
        mStatusEditText.setEnabled(false);
        mNumEditText.setEnabled(false);
        mConsSaldoEditText.setEnabled(false);
        mDealEditText.setEnabled(false);
        enableData(false);
        enableDataPrev(false);

    }

    private void showHideFields(TapDetail tapDetail) {
        if (!isEditMode()) {
            mNumAcordoCliTextInputLayout.setVisibility(
                    TextUtils.isNullOrEmpty(tapDetail.getCabec().getNumAcordCli()) ? View.GONE : View.VISIBLE);

            mObsTextInputLayout.setVisibility(
                    TextUtils.isNullOrEmpty(tapDetail.getCabec().getObs()) ? View.GONE : View.VISIBLE);


            mDealTextInputLayout.setVisibility(
                    TextUtils.isNullOrEmpty(tapDetail.getCabec().getContrDeal()) ? View.GONE : View.VISIBLE);

            mDataLinearLayout.setVisibility(TextUtils.isNullOrEmpty(tapDetail.getCabec().getDataInc()) ? View.GONE:View.VISIBLE);

        }

        mDataPrevLinearLayout.setVisibility(TextUtils.isNullOrEmpty(tapDetail.getCabec().getDataPrevPagto()) ? View.GONE:View.VISIBLE);
        mDealTextInputLayout.setVisibility(TextUtils.isNullOrEmpty(tapDetail.getCabec().getContrDeal()) ? View.GONE:View.VISIBLE);

        if(!TextUtils.isNullOrEmpty(tapDetail.getNomeConsSaldo())){
            mConsSaldoTextInputLayout.setVisibility(View.VISIBLE);
        } else {
            mConsSaldoTextInputLayout.setVisibility(View.GONE);
        }

        if (tapDetail.getListComboAnoMes() == null || tapDetail.getListComboAnoMes().size() == 0) {
            mMesAcaoFormWrapper.setVisibility(View.GONE);
        } else {
            mMesAcaoFormWrapper.setVisibility(View.VISIBLE);
        }
    }

    private void createDataPicker(Date date) {
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
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

        mEntregueEmdatePickerDialog.getDatePicker().setMaxDate(FormatUtils.getDateTimeNow(0, 3, 0).getTime());

    }

    private void enableData(boolean enable) {
        if (enable) {
            mDataTextView.setTextColor(getResources().getColor(R.color.formLabelTextColor));
            mDataLinearLayout.setEnabled(true);
            mDataImageView.setVisibility(View.VISIBLE);
        } else {
            mDataTextView.setTextColor(getResources().getColor(R.color.formLabelTextColorDisable));
            mDataLinearLayout.setEnabled(false);
            mDataImageView.setVisibility(View.GONE);
        }
    }

    private void enableDataPrev(boolean enable) {
        if (enable) {
            mDataPrevPagEmTextView.setTextColor(getResources().getColor(R.color.formLabelTextColor));
            mDataPrevLinearLayout.setEnabled(true);
            mDataPrevImageView.setVisibility(View.VISIBLE);
        } else {
            mDataPrevPagEmTextView.setTextColor(getResources().getColor(R.color.formLabelTextColorDisable));
            mDataPrevLinearLayout.setEnabled(false);
            mDataPrevImageView.setVisibility(View.GONE);
        }
    }

    private void setupMasterContrato(TapDetail tapDetail) {
        if(tapDetail.getListMaterInfo() != null) {

            Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tapDetail.getListMaterInfo().toArray(),
                    getString(R.string.select_master_contrato));

            if (tapDetail.getListMaterInfo().size() > 0) {
                mMasterContratoSpinnerAdapter = new SpinnerArrayAdapter<TapMaterInfo>(getContext(), objects, true) {
                    @Override
                    public String getItemDescription(TapMaterInfo item) {
                        return item.getNome();
                    }
                };

                mMasterContratoSpinner.setAdapter(mMasterContratoSpinnerAdapter);
                setMasterContrato(tapDetail);
            }
        }
    }

    public void setMasterContrato(TapDetail tapDetail) {
        if (tapDetail.getCabec().getTapMasterContrato() != null && mMasterContratoSpinnerAdapter != null) {
            for (int ind = 1; ind < mMasterContratoSpinnerAdapter.getCount(); ind++) {
                TapMaterInfo masterContrato = TapMaterInfo.class.cast(mMasterContratoSpinnerAdapter.getItem(ind));
                if (tapDetail.getCabec().getTapMasterContrato().getMasterDC150().equals(masterContrato.getCod())) {
                    mMasterContratoSpinner.setSelection(ind);
                    ind = mMasterContratoSpinnerAdapter.getCount();
                }
            }
        } else if (mMasterContratoSpinner.getSelectedItemPosition() > 0) {
            TapMaterInfo masterInfo = TapMaterInfo.class.cast(mMasterContratoSpinner.getSelectedItem());
            TapMasterContrato tapMasterContrato = new TapMasterContrato();
            tapMasterContrato.setIdMaster(Integer.parseInt(masterInfo.getCod()));
            tapDetail.getCabec().setTapMasterContrato(tapMasterContrato);
            setCurrentTap(tapDetail);
        }

        oldIndexSpinner = mMasterContratoSpinner.getSelectedItemPosition();

    }

    private void setupAnoMes(TapDetail tapDetail) {
        if(tapDetail.getListComboAnoMes() != null) {
            Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tapDetail.getListComboAnoMes().toArray(),
                    getString(R.string.select_mes_acao));

            if (tapDetail.getListComboAnoMes().size() > 0) {
                mMesAcaoSpinnerAdapter = new SpinnerArrayAdapter<TapComboMesAcao>(getContext(), objects, true) {
                    @Override
                    public String getItemDescription(TapComboMesAcao item) {
                        return item.getNome();
                    }
                };

                mMesAcaoSpinner.setAdapter(mMesAcaoSpinnerAdapter);
                setMesAcao(tapDetail);
            }
        }
    }

    public void setMesAcao(TapDetail tapDetail) {
        if (tapDetail.getCabec().getAnoMesAcao() != null && mMesAcaoSpinnerAdapter != null) {
            for (int ind = 1; ind < mMesAcaoSpinnerAdapter.getCount(); ind++) {
                TapComboMesAcao tapComboMesAcao = TapComboMesAcao.class.cast(mMesAcaoSpinnerAdapter.getItem(ind));

                if (tapDetail.getCabec().getAnoMesAcao().equals(tapComboMesAcao.getCod())) {
                    mMesAcaoSpinner.setSelection(ind);
                    ind = mMesAcaoSpinnerAdapter.getCount();
                }
            }
        } else if (mMesAcaoSpinner.getSelectedItemPosition() > 0) {
            TapComboMesAcao tapComboMesAcao = TapComboMesAcao.class.cast(mMasterContratoSpinner.getSelectedItem());
            tapDetail.getCabec().setAnoMesAcao(tapComboMesAcao.getCod());
            setCurrentTap(tapDetail);
        }
    }

    private void setupRegiao(TapDetail tapDetail) {
        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tapDetail.getListComboRegiao().toArray(),
                getString(R.string.select_regiao));

        if (tapDetail.getListComboRegiao().size() > 0) {
            mRegiaoSpinnerAdapter = new SpinnerArrayAdapter<TapComboRegiao>(getContext(), objects, true) {
                @Override
                public String getItemDescription(TapComboRegiao item) {
                    return item.getNome();
                }
            };

            mRegiaoSpinner.setAdapter(mRegiaoSpinnerAdapter);
            setRegiao(tapDetail);
        }
    }

    public void setRegiao(TapDetail tapDetail) {
        if (tapDetail.getCabec().getTapRegiao() != null && mRegiaoSpinnerAdapter != null) {
            for (int ind = 1; ind < mRegiaoSpinnerAdapter.getCount(); ind++) {
                TapComboRegiao tapComboRegiao = TapComboRegiao.class.cast(mRegiaoSpinnerAdapter.getItem(ind));
                if (tapComboRegiao.getCod().equals(
                        String.valueOf(tapDetail.getCabec().getTapRegiao().getIdRegiao()))) {
                    mRegiaoSpinner.setSelection(ind);
                    ind = mRegiaoSpinnerAdapter.getCount();
                }
            }
        } else if (mRegiaoSpinner.getSelectedItemPosition() > 0) {
            TapComboRegiao tapComboRegiao = TapComboRegiao.class.cast(mRegiaoSpinner.getSelectedItem());
            TapRegiao tapRegiao = new TapRegiao();
            tapRegiao.setDescricao(tapComboRegiao.getNome());
            tapRegiao.setIdRegiao(Integer.parseInt(tapComboRegiao.getCod()));
            tapDetail.getCabec().setTapRegiao(tapRegiao);
            setCurrentTap(tapDetail);
        }
    }

    private void changeMasterContrato(final int selectedSpinner) {
        tapConnection = new TapConnection(getContext(), new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {

                if (getActivity().getWindow().getDecorView().isShown()) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                    case TAP_ALTERAMC:
                        TapDetail tapDetail = getCurrentTap();
                        getActivity().runOnUiThread(() -> {
                            try {
                                AlteraMasterContrato alteraMasterContrato = gson.fromJson(response, AlteraMasterContrato.class);
                                tapDetail.setListComboAnoMes(alteraMasterContrato.getListAnoMes());
                                tapDetail.getCabec().setTapMasterContrato(alteraMasterContrato.getMasterContrato());
                                tapDetail.getCabec().setAnoMesAcao("");
                                setCurrentTap(tapDetail);

                                setupAnoMes(tapDetail);
                                showHideFields(tapDetail);
                            } catch (Exception ex) {
                                LogUser.log(Config.TAG, ex.toString());
                                mMasterContratoSpinner.setSelection(oldIndexSpinner);
                            }
                        });
                        break;
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                if (getActivity().getWindow().getDecorView().isShown()) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                if(code == Connection.SERVER_ERROR){
                    try {
                        RetError retError = gson.fromJson(response, RetError.class);
                        if(retError != null){
                            showMessageError(retError.getMessage());
                            return;
                        }
                    }catch (Exception ex){
                        LogUser.log(ex.toString());
                    }
                }

                dialogsDefault.showDialogMessage(getString(R.string.message_etap_master_contrato), dialogsDefault.DIALOG_TYPE_ERROR, () -> {
                    switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                        case TAP_ALTERAMC:
                            mMasterContratoSpinner.setSelection(oldIndexSpinner);
                            break;
                    }
                });

            }
        });

        if (getActivity().getWindow().getDecorView().isShown()) {
            progressDialog.show();
        }

        TapDetail tapDetail = getCurrentTap();

        Current current = Current.getInstance(getContext());

        tapConnection.changeETapMasterContrato(current.getUnidadeNegocio().getCodigo(),
                tapDetail.getListMaterInfo().get(selectedSpinner - 1).getCod(),
                tapDetail.getCabec().getId());
    }

    private void showProgress(boolean show) {
        mProgressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mBaseScrollView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

}
