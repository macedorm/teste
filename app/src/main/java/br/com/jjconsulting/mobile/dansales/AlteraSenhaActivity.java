package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.AlteraSenhaConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.PasswordTransformation;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class AlteraSenhaActivity extends BaseActivity {

    public static final String KEY_ID_USER = "id";

    private AlteraSenhaConnection alteraSenhaConnection;

    private LinearLayout mProgressLinearLayout;
    private RelativeLayout mBaseRelativeLayout;
    private TextView mMessageErrorTextView;
    private EditText mPasswordEditText;
    private EditText mNewPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private ImageView mShowPasswordImageView;
    private ImageView mShowNewPasswordImageView;
    private ImageView mShowConfirmPasswordImageView;
    private Button mAterarSenhaButton;

    private boolean isShowPassword;
    private boolean isNewShowPassword;
    private boolean isConfirmShowPassword;

    private String user;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_altera_senha);

        user = getIntent().getExtras().getString(KEY_ID_USER);

        mProgressLinearLayout = findViewById(R.id.loading_linear_layout);
        mBaseRelativeLayout = findViewById(R.id.base_relative_layout);
        mMessageErrorTextView = findViewById(R.id.message_error_text_view);

        mPasswordEditText = findViewById(R.id.password_edit_text);
        mNewPasswordEditText = findViewById(R.id.new_password_edit_text);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        mShowPasswordImageView = findViewById(R.id.show_password_image_button);
        mShowNewPasswordImageView = findViewById(R.id.show_new_password_image_button);
        mShowConfirmPasswordImageView = findViewById(R.id.show_confirm_password_image_button);
        mAterarSenhaButton = findViewById(R.id.alterar_button);

        mPasswordEditText.setTransformationMethod(new PasswordTransformation());
        mNewPasswordEditText.setTransformationMethod(new PasswordTransformation());
        mConfirmPasswordEditText.setTransformationMethod(new PasswordTransformation());

        mShowPasswordImageView.setOnClickListener(view -> {
            if (!isShowPassword) {
                mPasswordEditText.setTransformationMethod(null);
                mPasswordEditText.setSelection(mPasswordEditText.getText().length());
                mShowPasswordImageView.setImageDrawable(getResources().getDrawable(R.drawable.password_on));
                isShowPassword = true;
            } else {
                mPasswordEditText.setTransformationMethod(new PasswordTransformation());
                mPasswordEditText.setSelection(mPasswordEditText.getText().length());
                mShowPasswordImageView.setImageDrawable(getResources().getDrawable(R.drawable.password_off));
                isShowPassword = false;
            }
        });

        mShowNewPasswordImageView.setOnClickListener(view -> {
            if (!isNewShowPassword) {
                mNewPasswordEditText.setTransformationMethod(null);
                mNewPasswordEditText.setSelection(mNewPasswordEditText.getText().length());
                mShowNewPasswordImageView.setImageDrawable(getResources().getDrawable(R.drawable.password_on));
                isNewShowPassword = true;
            } else {
                mNewPasswordEditText.setTransformationMethod(new PasswordTransformation());
                mNewPasswordEditText.setSelection(mNewPasswordEditText.getText().length());
                mShowNewPasswordImageView.setImageDrawable(getResources().getDrawable(R.drawable.password_off));
                isNewShowPassword = false;
            }
        });

        mConfirmPasswordEditText.setOnClickListener(view -> {
            if (!isConfirmShowPassword) {
                mConfirmPasswordEditText.setTransformationMethod(null);
                mConfirmPasswordEditText.setSelection(mConfirmPasswordEditText.getText().length());
                mShowConfirmPasswordImageView.setImageDrawable(getResources().getDrawable(R.drawable.password_on));
                isConfirmShowPassword = true;
            } else {
                mConfirmPasswordEditText.setTransformationMethod(new PasswordTransformation());
                mConfirmPasswordEditText.setSelection(mConfirmPasswordEditText.getText().length());
                mShowConfirmPasswordImageView.setImageDrawable(getResources().getDrawable(R.drawable.password_off));
                isConfirmShowPassword = false;
            }
        });

        mAterarSenhaButton.setOnClickListener(view -> {
            String msg = validPassword();

            if (msg.length() > 0) {
                dialogsDefault.showDialogMessage(msg, dialogsDefault.DIALOG_TYPE_WARNING, null);
            } else {
                showProgress(true);
                alteraSenhaConnection.alteraSenha(mPasswordEditText.getText().toString(), mNewPasswordEditText.getText().toString(), mConfirmPasswordEditText.getText().toString(), user);
            }
        });

        alteraSenhaConnection = new AlteraSenhaConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgress(false);
                Intent intent = new Intent();

                Login login = gson.fromJson(response, Login.class);

                login.setPassword(mNewPasswordEditText.getText().toString());
                login.setUser(user);

                //Login ok
                if (login.getIdRetorno() == 0) {
                    userInfo.saveUserInfo(login, AlteraSenhaActivity.this);
                    initializeJJSDK(login.getToken(), user);

                    setResult(RESULT_OK, intent);
                    finish();


                    Toast toast = Toast.makeText(AlteraSenhaActivity.this, getString(R.string.ok_confirm_password), Toast.LENGTH_LONG);
                    toast.show();

                } else {
                    dialogsDefault.showDialogMessage(login.getMessage(), dialogsDefault.DIALOG_TYPE_ERROR, null);
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                showProgress(false);

                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                    ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(AlteraSenhaActivity.this, errorConnection.getMessage())){
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                if(!TextUtils.isNullOrEmpty(response)){
                    Login login = gson.fromJson(response, Login.class);
                    dialogsDefault.showDialogMessage(login.getMessage(), dialogsDefault.DIALOG_TYPE_ERROR, null);
                    return;
                }

                dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
            }
        });
    }

    private String validPassword() {
        String msg = "";

        if (mPasswordEditText.getText().length() == 0) {
            msg = getString(R.string.error_empty_passowrd);
        } else if (mNewPasswordEditText.getText().length() == 0) {
            msg = getString(R.string.error_empty_new_passowrd);
        } else if (!mNewPasswordEditText.getText().toString().equals(mConfirmPasswordEditText.getText().toString())) {
            msg = getString(R.string.error_confirm_passowrd);
        }
        return msg;
    }

    private void showProgress(boolean show) {
        mProgressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mMessageErrorTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        mBaseRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void initializeJJSDK(String token, String codUser) {
        JJSDK.setToken(this, token);
        JJSDK.setCodUser(this, codUser);
    }

}
