package br.com.jjconsulting.mobile.dansales;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;

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
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PedidoAprovacaoFragment extends PedidoBaseFragment implements View.OnClickListener, OnPageSelected {

    private AprovacaoLiberacaoConnection aprovacaoLiberacaoConnection;

    private ProgressDialog mPedidoAprovacaoProgressDialog;
    private Button mPedidoAprovacaoAprovarButton;
    private Button mPedidoAprovacaoReprovarButton;
    private EditText mPedidoAprovacaoNotaEditText;

    public static PedidoAprovacaoFragment newInstance() {
        return new PedidoAprovacaoFragment();
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
                        pedidoDao.update(pedido, null);
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
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                showProgressDialog(false);

                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                    ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(getContext(), errorConnection.getMessage())){
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pedido_aprovacao, container, false);

        mPedidoAprovacaoAprovarButton = view.findViewById(R.id.pedido_aprovacao_aprova_button);
        mPedidoAprovacaoAprovarButton.setOnClickListener(this);
        mPedidoAprovacaoReprovarButton = view.findViewById(R.id.pedido_aprovacao_reprova_button);
        mPedidoAprovacaoReprovarButton.setOnClickListener(this);
        mPedidoAprovacaoNotaEditText = view.findViewById(R.id.pedido_aprovacao_nota_text_view);

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
            case R.id.pedido_aprovacao_aprova_button:
                doAprovarReprovar(true);
                break;
            case R.id.pedido_aprovacao_reprova_button:
                doAprovarReprovar(false);
                break;
        }
    }

    private void createProgressDialog() {
        mPedidoAprovacaoProgressDialog = new ProgressDialog(getActivity());
        mPedidoAprovacaoProgressDialog.setCancelable(false);
        mPedidoAprovacaoProgressDialog.setMessage(getString(R.string.aguarde));
        mPedidoAprovacaoProgressDialog.setIndeterminate(true);
        mPedidoAprovacaoProgressDialog.setCanceledOnTouchOutside(false);
    }

    private void showProgressDialog(boolean isShow) {
        if (mPedidoAprovacaoProgressDialog == null) {
            createProgressDialog();
        }

        if (isShow) {
            mPedidoAprovacaoProgressDialog.show();
        } else {
            mPedidoAprovacaoProgressDialog.dismiss();
        }
    }

    private void bindPedido(Pedido pedido) {

    }

    private void doAprovarReprovar(boolean isAprovar) {
        showProgressDialog(true);


        if (isAprovar) {
            aprovacaoLiberacaoConnection.enviaAprovaReprova(getCurrentViewType(), 1, getCurrentPedido().getCodigo(),
                    mPedidoAprovacaoNotaEditText.getText().toString(), "");
        } else {
            if (mPedidoAprovacaoNotaEditText.getText().length() > 0) {
                showProgressDialog(true);
                aprovacaoLiberacaoConnection.enviaAprovaReprova(getCurrentViewType(), 0, getCurrentPedido().getCodigo(),
                        mPedidoAprovacaoNotaEditText.getText().toString(), "");
            } else {
                showProgressDialog(false);
                dialogsDefault.showDialogMessage(getString(R.string.pedido_apro_lib_msg_nota_vazia), dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        }
    }
}
