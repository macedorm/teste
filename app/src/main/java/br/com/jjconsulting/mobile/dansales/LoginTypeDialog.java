package br.com.jjconsulting.mobile.dansales;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import br.com.jjconsulting.mobile.dansales.kotlin.LoginSso;
import br.com.jjconsulting.mobile.dansales.util.Config;

public class   LoginTypeDialog extends Dialog {

    private Button mSSOButton;
    private Button mLoginButton;
    private Button mExit;

    private Activity activity;

    public LoginTypeDialog(Activity context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        setCancelable(false);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_type);

        mSSOButton = findViewById(R.id.login_danone_button);
        mLoginButton = findViewById(R.id.login_button);
        mExit = findViewById(R.id.exit_button);

        mSSOButton.setOnClickListener(view -> {
            loginSSO(activity);
        });

        mLoginButton.setOnClickListener(view -> dismiss());
        mExit.setOnClickListener(view -> activity.finish());

    }

    public void loginSSO(Activity context){
        Intent ssoIntent = new Intent(context, LoginSso.class);
        context.startActivityForResult(ssoIntent, Config.REQUEST_SSO);
    }
}
