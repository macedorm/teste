package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.RecuperaSenhaRequest;
import br.com.jjconsulting.mobile.dansales.model.ResultadoSolicitacaoRecuperacaoSenha;
import br.com.jjconsulting.mobile.dansales.model.SolicitacaoRecuperacaoSenha;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.HardwareUtil;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RecuperaSenhaActivity extends AppCompatActivity {

    private Gson gson;
    private DialogsCustom dialog;
    private RecuperaSenhaRequest recuperaSenhaRequest;

    private ViewGroup formViewGroup;
    private EditText emailTextView;
    private Button recoverPasswordButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_senha);

        formViewGroup = findViewById(R.id.formulario_linear_layout);
        emailTextView = findViewById(R.id.email_edit_text);
        recoverPasswordButton = findViewById(R.id.recover_password_button);
        progressBar = findViewById(R.id.recupera_senha_progress_bar);

        gson = new Gson();
        dialog = new DialogsCustom(this);
        recuperaSenhaRequest = new RecuperaSenhaRequest(this, new ConnectionListener());

        recoverPasswordButton.setOnClickListener(view -> recuperaSenha());
    }

    private void recuperaSenha() {
        String identificador = emailTextView.getText().toString();

        if (identificador.isEmpty()) {
            dialog.showDialogMessage(getResources().getString(R.string.recover_password_validation),
                    dialog.DIALOG_TYPE_WARNING, null);
            return;
        }

        SolicitacaoRecuperacaoSenha solicitacaoRecuperacaoSenha;

        try {
            solicitacaoRecuperacaoSenha = new SolicitacaoRecuperacaoSenha();
            solicitacaoRecuperacaoSenha.setUser(identificador);
            solicitacaoRecuperacaoSenha.setDispositivoIMEI(HardwareUtil.getDeviceIMEI(this));

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get the FCM registration token
                        String token = task.getResult();
                        solicitacaoRecuperacaoSenha.setAppId(token);
                        showProgressDialog();
                        recuperaSenhaRequest.recuperaSenha(solicitacaoRecuperacaoSenha);
                    });



        } catch (Exception ex) {
            dialog.showDialogMessage(
                    getResources().getString(R.string.sending_recover_password_error),
                    dialog.DIALOG_TYPE_ERROR, null);

            return;
        }

    }

    private void goToChangePassword(ResultadoSolicitacaoRecuperacaoSenha resultadoSolicitacao) {
        Intent alterarSenhaIntent = AlteraSenhaComPassThroughActivity.newIntent(
                this, resultadoSolicitacao);
        startActivityForResult(alterarSenhaIntent, Config.REQUEST_ALTERA_SENHA_PASSTROUGH);
    }

    private void showErrorMessageOrDefault(String msg) {
        String defaultMsg = getResources().getString(R.string.recover_password_internal_server_error);
        String errorMsg = StringUtils.isEmpty(msg) ? defaultMsg : msg;
        dialog.showDialogMessage(errorMsg, dialog.DIALOG_TYPE_ERROR, null);
    }

    private void showProgressDialog() {
        formViewGroup.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        formViewGroup.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private class ConnectionListener implements BaseConnection.ConnectionListener {

        @Override
        public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
            try {
                hideProgressDialog();

                final ResultadoSolicitacaoRecuperacaoSenha result = gson.fromJson(response,
                        ResultadoSolicitacaoRecuperacaoSenha.class);

                if (result == null) {
                    dialog.showDialogMessage(response, dialog.DIALOG_TYPE_WARNING,
                            null);
                    return;
                }

                if (result.isPossuiErro()) {
                    dialog.showDialogMessage(result.getErro(),
                            dialog.DIALOG_TYPE_WARNING,
                            null);
                } else if (result.isPossuiAviso()) {
                    dialog.showDialogMessage(
                            result.getAviso(),
                            dialog.DIALOG_TYPE_WARNING,
                            () -> {
                                if (result.isLiberadoParaRecuperarSenhaNoApp()) {
                                    goToChangePassword(result);
                                } else{
                                    finish();
                                }
                            });
                } else {
                    goToChangePassword(result);
                }
            } catch (Exception ex) {
                LogUser.log(ex.toString());
                showErrorMessageOrDefault(null);
            }
        }

        @Override
        public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
            hideProgressDialog();
            showErrorMessageOrDefault(volleyError.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_ALTERA_SENHA_PASSTROUGH) {
            setResult(RESULT_OK, data);
            finish();
        }
    }
}
