package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.AlteraSenhaComPassThroughRequest;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.data.SyncResult;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.model.ResultadoSolicitacaoRecuperacaoSenha;
import br.com.jjconsulting.mobile.dansales.model.SolicitacaoAlteracaoSenha;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.HardwareUtil;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.PasswordTransformation;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class AlteraSenhaComPassThroughActivity extends BaseActivity {

    public static final String KEY_RESULTADO_SOLICITACAO = "resultado_solicitacao";
    public static final String KEY_USERID = "USERID";
    public static final String KEY_PASSWORD = "PASSWORD";
    private ResultadoSolicitacaoRecuperacaoSenha resultadoSolicitacao;

    private Gson gson;
    private DialogsCustom dialog;
    private AlteraSenhaComPassThroughRequest alteraSenhaComPassThroughRequest;

    private ViewGroup progressLinearLayout;
    private ViewGroup baseRelativeLayout;
    private TextView messageErrorTextView;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private ImageView showNewPasswordImageView;
    private ImageView showConfirmPasswordImageView;
    private Button alterarSenhaButton;

    private boolean isNewShowPassword;
    private boolean isConfirmShowPassword;

    public static Intent newIntent(Context context,
                                   ResultadoSolicitacaoRecuperacaoSenha resultadoSolicitacao) {
        Intent intent = new Intent(context, AlteraSenhaComPassThroughActivity.class);
        intent.putExtra(KEY_RESULTADO_SOLICITACAO, resultadoSolicitacao);

        return intent;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_altera_senha_com_pass_through);

        if(getIntent().getExtras().containsKey(KEY_RESULTADO_SOLICITACAO)){
            resultadoSolicitacao = (ResultadoSolicitacaoRecuperacaoSenha)getIntent().getExtras()
                    .getSerializable(KEY_RESULTADO_SOLICITACAO);
        } else {
            finish();
            return;
        }


        progressLinearLayout = findViewById(R.id.loading_linear_layout);
        baseRelativeLayout = findViewById(R.id.base_relative_layout);
        messageErrorTextView = findViewById(R.id.message_error_text_view);
        newPasswordEditText = findViewById(R.id.new_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        showNewPasswordImageView = findViewById(R.id.show_new_password_image_button);
        showConfirmPasswordImageView = findViewById(R.id.show_confirm_password_image_button);
        alterarSenhaButton = findViewById(R.id.alterar_button);


        gson = new Gson();
        dialog = new DialogsCustom(this);
        alteraSenhaComPassThroughRequest = new AlteraSenhaComPassThroughRequest(this,
                new ConnectionListener());

        newPasswordEditText.setTransformationMethod(new PasswordTransformation());
        confirmPasswordEditText.setTransformationMethod(new PasswordTransformation());

        showNewPasswordImageView.setOnClickListener(view -> {
            isNewShowPassword = setEditTextState(newPasswordEditText, showNewPasswordImageView,
                    isNewShowPassword);
        });

        confirmPasswordEditText.setOnClickListener(view -> {
            isConfirmShowPassword = setEditTextState(confirmPasswordEditText,
                    showConfirmPasswordImageView, isConfirmShowPassword);
        });

        alterarSenhaButton.setOnClickListener(view -> {

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        String token = "";

                        if (!task.isSuccessful()) {
                        } else {
                            token = task.getResult();
                            ValidationDan validacaoSenha = validaSenha();

                            if (validacaoSenha.isValid()) {
                                SolicitacaoAlteracaoSenha solicitacaoAlteracaoSenha;

                                try {
                                    solicitacaoAlteracaoSenha = criaSolicitacaoAlteracaoSenha(token);
                                } catch (Exception ex) {
                                    LogUser.log(ex.toString());
                                    dialog.showDialogMessage(ex.getMessage(),
                                            dialogsDefault.DIALOG_TYPE_WARNING, null);
                                    return;
                                }
                                showProgress(true);
                                alteraSenhaComPassThroughRequest.alteraSenhaComPassThrough(solicitacaoAlteracaoSenha);
                            } else {
                                dialog.showDialogMessage(validacaoSenha.GetAllErrorMessages(),
                                        dialogsDefault.DIALOG_TYPE_WARNING, null);
                            }
                        }
                    });
        });
    }

    private ValidationDan validaSenha() {
        ValidationDan validationDan = new ValidationDan();

        if (newPasswordEditText.getText().length() == 0) {
            validationDan.addError(getString(R.string.error_empty_new_passowrd));
        } else if (!newPasswordEditText.getText().toString().equals(
                confirmPasswordEditText.getText().toString())) {
            validationDan.addError(getString(R.string.error_confirm_passowrd));
        }

        return validationDan;
    }

    private boolean setEditTextState(EditText editText, ImageView imageView, boolean hasFocus) {
        if (!hasFocus) {
            editText.setTransformationMethod(null);
            editText.setSelection(editText.getText().length());
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.password_on));
        } else {
            editText.setTransformationMethod(new PasswordTransformation());
            editText.setSelection(editText.getText().length());
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.password_off));
        }

        return !hasFocus;
    }

    private void showProgress(boolean show) {
        progressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        messageErrorTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        baseRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private SolicitacaoAlteracaoSenha criaSolicitacaoAlteracaoSenha(String token) {
        SolicitacaoAlteracaoSenha solicitacaoAlteracaoSenha = new SolicitacaoAlteracaoSenha();

        if (!TextUtils.isNullOrEmpty(resultadoSolicitacao.getCodigoUsuario())) {
            solicitacaoAlteracaoSenha.setUser(resultadoSolicitacao.getCodigoUsuario());
        } else {
            solicitacaoAlteracaoSenha.setUser(resultadoSolicitacao.getEmailUsuario());

        }

        solicitacaoAlteracaoSenha.setAppImei(HardwareUtil.getDeviceIMEI(this));


        solicitacaoAlteracaoSenha.setAppId(token);


        solicitacaoAlteracaoSenha.setPwdNew(newPasswordEditText.getText().toString());
        solicitacaoAlteracaoSenha.setPwdConfirm(
                confirmPasswordEditText.getText().toString());

        return solicitacaoAlteracaoSenha;
    }

    private class ConnectionListener implements BaseConnection.ConnectionListener {
        @Override
        public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
            try {
                showProgress(false);

                final SyncResult result = gson.fromJson(response, SyncResult.class);

                if (result == null) {
                    dialog.showDialogMessage(response, dialog.DIALOG_TYPE_WARNING,
                            null);
                    return;
                }

                if (result.isValid()) {
                    dialog.showDialogMessage(getString(R.string.sucess_recover_pwd),
                            dialog.DIALOG_TYPE_SUCESS, () -> CloseActivity());
                } else {
                    showProgress(false);
                    dialog.showDialogMessage(result.getMessage(),
                            dialog.DIALOG_TYPE_WARNING, null);
                }
            } catch (Exception ex) {
                showProgress(false);
                dialogsDefault.showDialogMessage(
                        ex.getMessage(),
                        dialogsDefault.DIALOG_TYPE_ERROR,
                        null);
            }
        }

        @Override
        public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
            showProgress(false);

            if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                if(ManagerSystemUpdate.isRequiredUpadate(AlteraSenhaComPassThroughActivity.this, errorConnection.getMessage())){
                    return;
                }

                showMessageError(errorConnection.getMessage());
                return;
            }

            if(response != null){
                SyncResult result = gson.fromJson(response, SyncResult.class);
                dialogsDefault.showDialogMessage(
                        result.getMessage(),
                        dialogsDefault.DIALOG_TYPE_WARNING,
                        null);

                return;
            }


            dialogsDefault.showDialogMessage(
                    getString(R.string.title_connection_error),
                    dialogsDefault.DIALOG_TYPE_ERROR,
                    null);
        }
    }

    private void CloseActivity(){

        Intent intent = new Intent();
        if (resultadoSolicitacao != null){
            String codUsuario = resultadoSolicitacao.getCodigoUsuario();
            if (!TextUtils.isNullOrEmpty(codUsuario)){
                intent.putExtra(KEY_USERID, codUsuario);
                intent.putExtra(KEY_PASSWORD, newPasswordEditText.getText().toString());
            }
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
