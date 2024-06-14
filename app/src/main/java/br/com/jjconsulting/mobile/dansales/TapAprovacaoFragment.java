package br.com.jjconsulting.mobile.dansales;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.database.TapMotivoReprovaDao;
import br.com.jjconsulting.mobile.dansales.model.RetProcess;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapMotivoReprova;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionTap;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapAprovacaoFragment extends TapBaseFragment implements View.OnClickListener, OnPageSelected {

    private TapConnection tapConnection;

    private ProgressDialog mTapAprovacaoProgressDialog;
    private Button mTapAprovacaoAprovarButton;
    private EditText mTapAprovacaoNotaEditText;

    private ViewGroup mTapMotFormWrapper;
    private LinearLayout mViewDefaultLinearLayout;
    private RadioGroup mRespostaRadioGroup;

    private Spinner mMotivoSpinner;

    private SpinnerArrayAdapter<TapMotivoReprova> mMotivoSpinnerAdapter;

    private int idTipo;

    public TapAprovacaoFragment() {
    }

    public static TapAprovacaoFragment newInstance() {
        return new TapAprovacaoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tapConnection = new TapConnection(getContext(), new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgressDialog(false);

                try {
                    dialogsDefault.showDialogMessage(getString(R.string.tap_apro_lib_msg_sucess), dialogsDefault.DIALOG_TYPE_SUCESS, () -> {
                        CurrentActionTap.getInstance().setUpdateListTap(true);
                        getActivity().finish();
                    });
                } catch (Exception ex) {
                    dialogsDefault.showDialogMessage(getString(R.string.message_etap_add_item_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                showProgressDialog(false);

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

                dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tap_aprovacao, container, false);

        mTapAprovacaoAprovarButton = view.findViewById(R.id.tap_aprovacao_aprova_button);
        mTapAprovacaoAprovarButton.setOnClickListener(this);
        mTapAprovacaoNotaEditText = view.findViewById(R.id.tap_aprovacao_nota_text_view);
        mViewDefaultLinearLayout = view.findViewById(R.id.view_tipo_linear_layout);
        mMotivoSpinner = view.findViewById(R.id.tap_mot_spinner);
        mTapMotFormWrapper = view.findViewById(R.id.tap_mot_form_wrapper);

        setupMasterContrato();
        createRadioGroup(getCurrentTap().getCabec().getStatus());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tap_aprovacao_aprova_button:
                doAprovarReprovar();
                break;
        }
    }

    private void createProgressDialog() {
        mTapAprovacaoProgressDialog = new ProgressDialog(getActivity());
        mTapAprovacaoProgressDialog.setCancelable(false);
        mTapAprovacaoProgressDialog.setMessage(getString(R.string.aguarde));
        mTapAprovacaoProgressDialog.setIndeterminate(true);
        mTapAprovacaoProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void doAprovarReprovar() {

        TapDetail tapDetail = getCurrentTap();

        ValidationDan validationDan = new ValidationDan();

        if (idTipo != 1 && mTapAprovacaoNotaEditText.getText().length() < 3) {
            validationDan.addError(getString(R.string.tap_apro_lib_msg_nota_vazia));
        }


        if(!tapDetail.isDes() && !tapDetail.isBloq() ) {
            if (idTipo != 1 && mMotivoSpinner.getSelectedItemPosition() == 0) {
                validationDan.addError(getString(R.string.tap_apro_lib_msg_status));
            }
        }

        if (validationDan.getMessage().size() > 0) {
            showDialogValidationError(validationDan);
        } else {
            showProgressDialog(true);

            if (idTipo == 3) {
                tapConnection.sendETap(getCurrentTap().getCabec(), "B", "1", "B",
                        mTapAprovacaoNotaEditText.getText().toString(), 0);
            } else if (idTipo == 4) {
                tapConnection.sendETap(getCurrentTap().getCabec(), "D", "1", "D",
                        mTapAprovacaoNotaEditText.getText().toString(), 0);
            } else {
                tapConnection.sendETap(getCurrentTap().getCabec(), "", "", idTipo == 1 ? "A" : "R",
                        mTapAprovacaoNotaEditText.getText().toString(), mMotivoSpinner.getSelectedItemPosition());
            }
        }
    }

    private void createRadioGroup(int status) {
        LinearLayout child = (LinearLayout) getLayoutInflater().inflate(R.layout.tap_aprov_tipo_group, null);
        mViewDefaultLinearLayout.addView(child);
        mRespostaRadioGroup = child.findViewById(R.id.tap_aprov_radio_group);

        TapDetail tapDetail = getCurrentTap();

        String tipo[] = getResources().getStringArray(R.array.array_tipo_aprov);

        if(tapDetail.isBloq() || tapDetail.isDes()){
            int id = tapDetail.isBloq()  ? tipo.length - 1 : tipo.length;
            String label =  tipo[id - 1];
            // Bloquear - id: 3
            // Bloquear - id: 4
            createRadioButton(id, label, true);
            mTapMotFormWrapper.setVisibility(View.GONE);
        } else {
            // Aprovar - id: 1
            // Reprovar - id: 2
            for (int ind = 0; ind < (tipo.length - 2); ind++) {
                createRadioButton((ind + 1), tipo[ind], ind == 0);
            }
        }
    }

    private void createRadioButton(int id, String desc, boolean selected) {
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(desc);
        radioButton.setId(id);
        radioButton.setChecked(selected);
        mRespostaRadioGroup.addView(radioButton);

        radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    changeType(buttonView.getId());
                }
            }
        });

        if (selected) {
            changeType(id);
        }
    }

    private void changeType(int id) {
        idTipo = id;
        if (id == 1) {
            mTapAprovacaoNotaEditText.setEnabled(false);
            mTapAprovacaoNotaEditText.setText("APROVADO");
            mMotivoSpinner.setSelection(0);
            mMotivoSpinner.setEnabled(false);
        } else {
            mTapAprovacaoNotaEditText.setEnabled(true);
            mTapAprovacaoNotaEditText.setText("");
            mMotivoSpinner.setEnabled(true);
        }
    }

    private void setupMasterContrato() {
        TapMotivoReprovaDao tapMotivoReprovaDao = new TapMotivoReprovaDao(getContext());
        String unNeg = Current.getInstance(getContext()).getUnidadeNegocio().getCodigo();
        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(tapMotivoReprovaDao.get(unNeg).toArray(),
                getString(R.string.select_motivo_reprovacao));

        if (objects.length > 0) {
            mMotivoSpinnerAdapter = new SpinnerArrayAdapter<TapMotivoReprova>(getContext(), objects, true) {
                @Override
                public String getItemDescription(TapMotivoReprova item) {
                    return item.getDescricao();
                }
            };

            mMotivoSpinner.setAdapter(mMotivoSpinnerAdapter);
        }
    }

    private void showDialogValidationError(ValidationDan validation) {
        try {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

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

    private void showProgressDialog(boolean isShow) {
        if (mTapAprovacaoProgressDialog == null) {
            createProgressDialog();
        }

        if (getActivity().getWindow().getDecorView().isShown()) {
            if (!isShow) {
                if (mTapAprovacaoProgressDialog.isShowing()) {
                    mTapAprovacaoProgressDialog.dismiss();
                }
            } else {
                mTapAprovacaoProgressDialog.show();
            }
        }
    }
}
