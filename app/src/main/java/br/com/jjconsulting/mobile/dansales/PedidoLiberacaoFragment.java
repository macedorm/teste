package br.com.jjconsulting.mobile.dansales;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import br.com.jjconsulting.mobile.dansales.connectionController.AprovacaoLiberacaoConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.SyncPedidoProcessDetail;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPedido;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.OnPageSelected;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PedidoLiberacaoFragment extends PedidoBaseFragment implements View.OnClickListener, OnPageSelected {

    private final String SAVED_CURRENT_DATE = "current_date";

    private AprovacaoLiberacaoConnection aprovacaoLiberacaoConnection;

    private DatePickerDialog.OnDateSetListener mPedidoLiberacaoDataAgendamentoSetListener;
    private DatePickerDialog mPedidoLiberacaoDataAgendamentoPickerDialog;
    private LinearLayout mPedidoLiberacaoDataAgendamentoLinearLayout;
    private ProgressDialog mPedidoLiberacaoProgressDialog;
    private ImageView mPedidoLiberacaoDataAgendamentoImageView;
    private TextView mPedidoLiberacaoDataAgendamentoTextView;
    private Button mPedidoLiberacaoAprovarButton;
    private Button mPedidoLiberacaoReprovarButton;
    private EditText mPedidoLiberacaoNotaEditText;

    private boolean isNotUHT;
    private String date;

    public PedidoLiberacaoFragment() {
    }

    public static PedidoLiberacaoFragment newInstance() {
        return new PedidoLiberacaoFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Saving current title and selected unidade de negócio
        if (date != null) {
            outState.putString(SAVED_CURRENT_DATE, date);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        aprovacaoLiberacaoConnection = new AprovacaoLiberacaoConnection(getContext(), new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgressDialog(false);


                SyncPedidoProcessDetail syncPedidoProcessDetail = gson.fromJson(response, SyncPedidoProcessDetail.class);

                if (syncPedidoProcessDetail == null) {
                    dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                } else {


                    if (syncPedidoProcessDetail.getNewStatus() != 0) {
                        PedidoDao pedidoDao = new PedidoDao(getContext());
                        Pedido pedido = getCurrentPedido();
                        pedido.setCodigoStatus(syncPedidoProcessDetail.getNewStatus());
                        pedidoDao.update(getCurrentPedido(), null);
                        CurrentActionPedido.getInstance().setUpdateListPedido(true);
                    }

                    if (syncPedidoProcessDetail.getValid()) {

                        dialogsDefault.showDialogMessage(getString(R.string.pedido_apro_lib_msg_sucess), dialogsDefault.DIALOG_TYPE_SUCESS, () -> {
                            getActivity().finish();
                        });
                    } else {
                        String messageDialog = null;

                        for (String message : syncPedidoProcessDetail.getErrors()) {
                            if (messageDialog == null) {
                                messageDialog = message;
                            } else {
                                messageDialog += (message + "\n");
                            }
                        }

                        dialogsDefault.showDialogMessage(messageDialog, dialogsDefault.DIALOG_TYPE_ERROR, () -> {
                            getActivity().finish();
                        });
                    }
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                showProgressDialog(false);
                ValidationLetter errorConnection;

                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                    errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(getContext(), errorConnection.getMessage())){
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        });

        mPedidoLiberacaoDataAgendamentoSetListener = (datePicker, year, month, day) -> {
            try {
                date = FormatUtils.toDateCreateDatePicker(year, month, day);
                mPedidoLiberacaoDataAgendamentoTextView.setText(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };


        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SAVED_CURRENT_DATE)) {
            date = savedInstanceState.getSerializable(
                    SAVED_CURRENT_DATE).toString();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_liberacao, container, false);

        mPedidoLiberacaoAprovarButton = view.findViewById(R.id.pedido_liberacao_aprova_button);
        mPedidoLiberacaoAprovarButton.setOnClickListener(this);
        mPedidoLiberacaoReprovarButton = view.findViewById(R.id.pedido_liberacao_reprova_button);
        mPedidoLiberacaoReprovarButton.setOnClickListener(this);
        mPedidoLiberacaoNotaEditText = view.findViewById(R.id.pedido_liberacao_nota_text_view);
        mPedidoLiberacaoDataAgendamentoImageView = view.findViewById(R.id.pedido_liberacao_data_agendamento_image_view);
        mPedidoLiberacaoDataAgendamentoImageView.setOnClickListener(this);
        mPedidoLiberacaoDataAgendamentoTextView = view.findViewById(R.id.pedido_liberacao_data_agendamento_text_view);
        mPedidoLiberacaoDataAgendamentoLinearLayout = view.findViewById(R.id.pedido_liberacao_data_agendamento_linear_layout);

        if (date != null) {
            mPedidoLiberacaoDataAgendamentoTextView.setText(date);
        }

        createDataPicker();
        bindPedido(getCurrentPedido());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindPedido(getCurrentPedido());
    }

    @Override
    public void onPageSelected(int position) {
        bindPedido(getCurrentPedido());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pedido_liberacao_aprova_button:
                doAprovarReprovar(true);
                break;
            case R.id.pedido_liberacao_reprova_button:
                doAprovarReprovar(false);
                break;
            case R.id.pedido_liberacao_data_agendamento_image_view:
                mPedidoLiberacaoDataAgendamentoPickerDialog.show();
                break;
        }
    }

    private void createProgressDialog() {
        mPedidoLiberacaoProgressDialog = new ProgressDialog(getActivity());
        mPedidoLiberacaoProgressDialog.setCancelable(false);
        mPedidoLiberacaoProgressDialog.setMessage(getString(R.string.aguarde));
        mPedidoLiberacaoProgressDialog.setIndeterminate(true);
        mPedidoLiberacaoProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void showProgressDialog(boolean isShow) {
        if (mPedidoLiberacaoProgressDialog == null) {
            createProgressDialog();
        }

        if (isShow) {
            mPedidoLiberacaoProgressDialog.show();
        } else {
            mPedidoLiberacaoProgressDialog.dismiss();
        }
    }

    private void createDataPicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        mPedidoLiberacaoDataAgendamentoPickerDialog = new DatePickerDialog(
                getActivity(), mPedidoLiberacaoDataAgendamentoSetListener, year, month, day);

    }

    private void bindPedido(Pedido pedido) {

        if (pedido.getTipoVenda() != null && !pedido.getTipoVenda().getCodigo().equals("UHT")) {
            isNotUHT = true;
        } else {
            isNotUHT = false;
        }

        mPedidoLiberacaoDataAgendamentoLinearLayout.setVisibility(isNotUHT ? View.INVISIBLE : View.VISIBLE);
    }

    private void doAprovarReprovar(boolean isAprovar) {
        showProgressDialog(true);

        String date = mPedidoLiberacaoDataAgendamentoTextView.getText().toString().replace(getString(R.string.pedido_liberacao_data_agendamento_hint), "");

        if (!isNotUHT && date.isEmpty()) {
            showProgressDialog(false);
            dialogsDefault.showDialogMessage(getString(R.string.pedido_apro_lib_msg_data_vazia), dialogsDefault.DIALOG_TYPE_WARNING, null);
            return;
        }

        if (isAprovar) {
            aprovacaoLiberacaoConnection.enviaAprovaReprova(getCurrentViewType(), 1, getCurrentPedido().getCodigo(),
                    mPedidoLiberacaoNotaEditText.getText().toString(), date);
        } else {
            if (mPedidoLiberacaoNotaEditText.getText().length() > 0) {
                showProgressDialog(true);
                aprovacaoLiberacaoConnection.enviaAprovaReprova(getCurrentViewType(), 0, getCurrentPedido().getCodigo(),
                        mPedidoLiberacaoNotaEditText.getText().toString(), date);
            } else {
                showProgressDialog(false);
                dialogsDefault.showDialogMessage(getString(R.string.pedido_apro_lib_msg_nota_vazia), dialogsDefault.DIALOG_TYPE_WARNING, null);
            }
        }
    }
}
