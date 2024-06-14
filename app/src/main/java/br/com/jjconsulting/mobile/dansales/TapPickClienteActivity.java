package br.com.jjconsulting.mobile.dansales;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.WebUser;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionTap;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapPickClienteActivity extends SingleFragmentActivity
        implements ClientesFragment.OnClienteClickListener {

    public static final int WEB_USER_REQUEST = 100;

    private Gson gson;

    private DialogsCustom dialogsDefault;

    private String codCli;

    private ProgressDialog progressDialog;

    private TapConnection tapConnection;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, TapPickClienteActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        gson = new Gson();
        dialogsDefault = new DialogsCustom(this);
        createDialogProgress();
        return ClientesFragment.newInstance();
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        codCli = cliente.getCodigo();
        createTap(null);
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WEB_USER_REQUEST) {
            if (resultCode == RESULT_OK) {
                WebUser webUser = (WebUser) data.getSerializableExtra(TapWebUserActivity.WEB_USER_RESULT);
                createTap(webUser.getCod());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void createTap(String codVend) {
        tapConnection = new TapConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                String msg = "";

                TapDetail mTapDetail = gson.fromJson(response, TapDetail.class);

                if (mTapDetail.getCodErr() != null && mTapDetail.getCodErr().equals("LISTUSERMC")) {
                    startActivityForResult(TapWebUserActivity.newIntent(TapPickClienteActivity.this, mTapDetail.getListUser()), WEB_USER_REQUEST);
                }
                if (mTapDetail.getCodErr() != null && mTapDetail.getCodErr().equals("MASTCONTRASC")) {

                    msg += formatTextError(mTapDetail.getDescErr());
                    msg += "\n" + getResources().getString(R.string.message_etap_rascunho);

                    dialogsDefault.showDialogMessageLeftQuestion(msg, dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                        @Override
                        public void onClickPositive() {
                            Current current = Current.getInstance(getApplicationContext());
                            String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                                    current.getUnidadeNegocio().getCodigo());
                            tapConnection.addETapDetail(current.getUsuario().getCodigo(), empresaFilial[0], empresaFilial[1], codCli, true);
                        }

                        @Override
                        public void onClickNegative() {
                            finish();
                        }
                    });
                } else if (mTapDetail.getCodErr() != null && mTapDetail.getCodErr().equals("MASTCONT")) {
                    msg += formatTextError(mTapDetail.getDescErr());
                    dialogsDefault.showDialogMessage(msg, dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogMessage() {
                        @Override
                        public void onClick() {
                            finish();
                        }
                    });
                } else {
                    if (TextUtils.isNullOrEmpty(mTapDetail.getDescErr())) {
                        CurrentActionTap.getInstance().setUpdateListTap(true);
                        finish();
                        startActivity(TapPedidoDetailActivity.newIntent(TapPickClienteActivity.this, mTapDetail, TapActionType.TAP_LIST));
                    } else {
                        runOnUiThread(() -> {
                            dialogsDefault.showDialogMessage(mTapDetail.getDescErr(), dialogsDefault.DIALOG_TYPE_ERROR, null);
                        });
                    }
                }

            }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }


                dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        });

        Current current = Current.getInstance(this);
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                current.getUnidadeNegocio().getCodigo());

        progressDialog.show();

        if (codVend == null) {
            tapConnection.addETapDetail(current.getUsuario().getCodigo(), empresaFilial[0], empresaFilial[1], codCli, false);
        } else {
            tapConnection.addETapDetail(codVend, empresaFilial[0], empresaFilial[1], codCli, false);
        }
    }

    public String formatTextError(String error){
        String msg = "";

        try {
            String msgError[] = error.split("-");

            for (int ind = 0; ind < msgError.length; ind++) {
                if (ind > 0) {
                    msg += " - " + msgError[ind] + "\n";
                }
            }


        } catch (Exception ex) {
            msg = error;
        }

        return msg;
    }
}
