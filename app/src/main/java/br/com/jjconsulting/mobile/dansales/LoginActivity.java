package br.com.jjconsulting.mobile.dansales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;;

import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.InputStreamReader;
import java.util.ArrayList;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.LoginConnection;
import br.com.jjconsulting.mobile.dansales.database.UnidadeNegocioDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.kotlin.UpdateUser;
import br.com.jjconsulting.mobile.dansales.kotlin.model.TStatusRD;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.AutoStartSyncService;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.FirebaseUtils;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.dao.DataAccess;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataDao;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.syncData.model.ConfigUserSync;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterData;
import br.com.jjconsulting.mobile.jjlib.syncData.model.MasterDataSync;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.NetworkUtils;
import br.com.jjconsulting.mobile.jjlib.util.PasswordTransformation;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;


public class LoginActivity extends BaseActivity {
    private static final String KEY_FILTER_CREATE_STATE = "filter_create_state";
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 225;
    private final int WARNING_CHANGE_PASSWORD = 131;
    private final int CHANGE_PASSWORD = 130;
    private final int UPDATE_USER_START = 160;
    private final int UPDATE_USER_FINAL = 164;

    private final int LOGIN_OK = 0;

    private LoginConnection loginConnection;
    private Login login;

    private LinearLayout mProgressLinearLayout;
    private RelativeLayout mBaseRelativeLayout;

    private EditText mUserEditText;
    private EditText mPasswordEditText;

    private TextView mMessageErrorTextView;
    private TextView mAppVersionTextView;
    private TextView mAppHomTextView;

    private ImageView mShowPasswordImageView;

    private ImageButton mSettingsImageButton;
    private ImageButton mHelpImageButton;

    private Button mLoginButton;
    private Button mForgotPassword;

    private LoginTypeDialog loginTypeDialog;

    private ProgressDialog bar;

    private boolean isShowPassword;
    private boolean isToken;



    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        JJSDK.initializeAuthConnection(this, BuildConfig.URL_API, BuildConfig.USER, BuildConfig.USERKEY, BuildConfig.DATABASE_NAME, BuildConfig.DATABASE_VERSION);

        configDialogProgress();

        if(getIntent().getExtras() != null && getIntent().getExtras().containsKey(Current.REQUEST_SESSION_EXPIRED)){
            boolean sessionExpired = getIntent().getExtras().getBoolean(Current.REQUEST_SESSION_EXPIRED, false);
            if (sessionExpired) {
                Toast.makeText(this, getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
            }
        }

        AutoStartSyncService.scheduleAutoSync(LoginActivity.this, false);
        SyncDataManager.setIsProgress(false);

        mProgressLinearLayout = findViewById(R.id.loading_linear_layout);
        mBaseRelativeLayout = findViewById(R.id.base_relative_layout);
        mMessageErrorTextView = findViewById(R.id.message_error_text_view);
        mUserEditText = findViewById(R.id.user_edit_text);
        mPasswordEditText = findViewById(R.id.password_edit_text);
        mLoginButton = findViewById(R.id.login_button);

        mForgotPassword = findViewById(R.id.recover_password_button);
        mAppVersionTextView = findViewById(R.id.app_version_text_view);
        mSettingsImageButton = findViewById(R.id.settings_image_button);
        mHelpImageButton = findViewById(R.id.help_image_button);
        mShowPasswordImageView = findViewById(R.id.show_password_image_button);
        mAppHomTextView = findViewById(R.id.app_hom_text_view);

        FirebaseMessaging.getInstance().subscribeToTopic("APP");

        if (BuildConfig.DEBUG) {
            mUserEditText.setText("800035");
            mPasswordEditText.setText("D@none07");
        }

        mAppVersionTextView.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));

               if (getPackageName().contains("hml") || getPackageName().contains("dev")) {
            mAppHomTextView.setText(getString(R.string.title_hom));
        } else {
            mAppHomTextView.setVisibility(View.GONE);
        }

        mPasswordEditText.setTransformationMethod(new PasswordTransformation());

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

        mLoginButton.setOnClickListener(view -> login());

        mForgotPassword.setOnClickListener(view -> {
            Intent recuperarSenhaIntent = new Intent(this,
                    RecuperaSenhaActivity.class);
            startActivityForResult(recuperarSenhaIntent, Config.REQUEST_ALTERA_SENHA_PASSTROUGH);
        });

        mSettingsImageButton.setOnClickListener(view -> {
            Intent settingsIntent = new Intent(LoginActivity.this,
                    SettingsActivity.class);
            settingsIntent.putExtra(SettingsActivity.KEY_LOGIN, true);
            startActivity(settingsIntent);
        });

        mHelpImageButton.setOnClickListener(view -> {
            Intent helpIntent = new Intent(LoginActivity.this,
                    HelpActivity.class);
            startActivity(helpIntent);
        });

        loginConnection = new LoginConnection(this, new LoginConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                if (typeConnection == LoginConnection.LOGIN) {
                    try {


                        Login loginCurrent = gson.fromJson(response, Login.class);

                        if(login == null){
                            login = loginCurrent;
                        } else {
                            String user = login.getUser();
                            String pass = login.getPassword();

                            login = loginCurrent;
                            login.setUser(user);
                            login.setPassword(pass);
                        }
                        //Usuário sem permissão para usar app
                       if(!login.isEnableMobile() && (login.getIdRetorno() == LOGIN_OK || login.getIdRetorno() == WARNING_CHANGE_PASSWORD || login.getIdRetorno() == CHANGE_PASSWORD)){
                            dialogsDefault.showDialogMessage(getString(R.string.permission_mobile_error), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
                                @Override
                                public void onClick() {
                                    showProgress(false);
                                }
                            });
                            return;
                        }

                        //Login ok
                        if (login.getIdRetorno() == LOGIN_OK) {
                            doLogin();

                        //Senha prestes a expirar
                        } else if (login.getIdRetorno() == WARNING_CHANGE_PASSWORD) {
                            dialogsDefault.showDialogMessage(getString(R.string.login_expired_alert), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
                                public void onClick() {
                                    LogUser.log(Config.TAG, "WARNING_CHANGE_PASSWORD: " + WARNING_CHANGE_PASSWORD);
                                    doLogin();
                                }
                            });
                            //Atualização de dados
                        } else if (TStatusRD.Companion.isCheckExists(login.getIdRetorno())) {
                           TStatusRD tStatus = TStatusRD.Companion.getStatusName(login.getIdRetorno());
                              dialogsDefault.showDialogMessage(login.getMessage(), DialogsCustom.DIALOG_TYPE_WARNING, () -> {

                                if(tStatus != TStatusRD.ALERTAR_EMAIL &&  tStatus != TStatusRD.BLOQUEIO_NOME &&
                                        tStatus != TStatusRD.BLOQUEIO_AUT_EMAIL && tStatus != TStatusRD.BLOQUEIO_EMAIL &&
                                        tStatus != TStatusRD.AUTENTICAR_EMAIL && tStatus != TStatusRD.AGUARDE_CONFIRMACAO){
                                    Intent it = new Intent(LoginActivity.this, UpdateUser.class);
                                    it.putExtra("key", login.getIdRetorno());
                                    it.putExtra("token", login.getToken());
                                    it.putExtra("user", login.getUserId());

                                    startActivityForResult(it, login.getIdRetorno());
                                } else if(tStatus == TStatusRD.ALERTAR_EMAIL){
                                    doLogin();
                                } else {
                                    userInfo.deleteUserInfo(LoginActivity.this);
                                    showProgress(false);
                                }

                              });
                         //Login obrigatório altera a senha
                        } else if (login.getIdRetorno() == CHANGE_PASSWORD) {
                            dialogsDefault.showDialogMessage(login.getMessage(), DialogsCustom.DIALOG_TYPE_WARNING, () -> {
                                LogUser.log(Config.TAG, "CHANGE_PASSWORD: " + CHANGE_PASSWORD);
                                Intent it = new Intent(LoginActivity.this, AlteraSenhaActivity.class);
                                it.putExtra(AlteraSenhaActivity.KEY_ID_USER, mUserEditText.getText().toString());
                                startActivityForResult(it, Config.REQUEST_ALTERA_SENHA);
                            });
                        } else {
                            dialogsDefault.showDialogMessage(login.getMessage(), DialogsCustom.DIALOG_TYPE_ERROR, null);
                            userInfo.deleteUserInfo(LoginActivity.this);
                            showProgress(false);

                        }

                        FirebaseUtils.setUser(login.getUserId(), "", "");
                    } catch (Exception ex) {

                        LogUser.log(Config.TAG, "Error login parser - open main ");
                        Login login = userInfo.getUserInfo(LoginActivity.this);
                        if (login != null && login.getToken() != null) {
                            openMain(login.getUserId());
                        } else {
                            dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                        }

                        showProgress(false);
                    }
                }

            }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                if (typeConnection == LoginConnection.LOGIN) {
                    showProgress(false);
                    try {
                        if (!TextUtils.isNullOrEmpty(response)) {
                            Login loginCurrent = gson.fromJson(response, Login.class);

                            if (loginCurrent != null) {
                                dialogsDefault.showDialogMessage(loginCurrent.getMessage(), DialogsCustom.DIALOG_TYPE_ERROR, null);
                                return;
                            }
                        }


                        Login login = userInfo.getUserInfo(getApplicationContext());

                        if (login != null && login.getToken() != null) {
                            LogUser.log(Config.TAG, "Error login connection - open main: " + code);
                            openMain(login.getUserId());
                        } else {
                            dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                        }

                    }catch (Exception ex){
                        dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                    }

                }
            }
        });

          if (  ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        //Force DB Connection
        new Thread(() -> {
            new SyncDataDao(LoginActivity.this).getLastDateSync("0");

            try {
                if (!this.isFinishing() && bar != null) {
                    bar.dismiss();
                }
            } catch (Exception ex) {
                LogUser.log(Config.TAG, ex.toString());
            }

            if (savedInstanceState == null) {
                loginToken();
            }

        }).start();

        try{
            SQLiteDatabase db = WebSalesDatabaseHelper.getInstance(this, BuildConfig.DATABASE_VERSION, BuildConfig.DATABASE_NAME).getWritableDatabase();
            DataAccess dao = new DataAccess();

            if(!dao.tableExist(db, "TB_MASTERDATA")){
                dao.createTable(db, MasterData.class);
            }

            if(!dao.tableExist(db, "TB_MASTERDATA_SYNC")){
                dao.createTable(db, MasterDataSync.class);
            }

        }catch (Exception ex){
            LogUser.log(ex.toString());
        }
   }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_FILTER_CREATE_STATE, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    public void loginToken() {
        isToken = true;
        login = userInfo.getUserInfo(this);
        if (login != null) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                LogUser.log(Config.TAG, "Login Token - isNetworkAvailable = true");

                runOnUiThread(() -> {
                    showProgress(true);
                    mMessageErrorTextView.setText(getString(R.string.title_connection_login));
                });

                Bundle params = new Bundle();
                params.putString("Result", "Pass Through");
                FirebaseUtils.sendEvent(this, FirebaseAnalytics.Event.LOGIN, params);
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(task -> {
                            String token = "";

                            if (task.isSuccessful()) {
                                token = task.getResult();
                            }

                            loginConnection.login(login.getUser(), login.getPassword(), token);

                        });

            } else {
                LogUser.log(Config.TAG, "Login Token - isNetworkAvailable = false");
                openMain(login.getUserId());
            }
        } else {
            runOnUiThread(() -> {
                loginTypeDialog =  new LoginTypeDialog(this);
                loginTypeDialog.show();
            });
        }
    }

    public void login() {

        isToken = false;
        String error = validData();
        if (error.length() == 0) {
            mMessageErrorTextView.setText(getString(R.string.title_connection_login));
            showProgress(true);

            Bundle params = new Bundle();
            params.putString("Result", "Digits");
            FirebaseUtils.sendEvent(this, FirebaseAnalytics.Event.LOGIN, params);

            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        String token = "";

                        if (task.isSuccessful()) {
                            token = task.getResult();
                        }
                        loginConnection.login(mUserEditText.getText().toString(),
                                mPasswordEditText.getText().toString(), token);
                    });


        } else {
            showProgress(false);
            dialogsDefault.showDialogMessage(error, DialogsCustom.DIALOG_TYPE_ERROR, null);
        }
    }

    private String validData() {
        if (mUserEditText.getText().toString().trim().length() == 0) {
            return getString(R.string.user_blank);
        } else if (mPasswordEditText.getText().toString().trim().length() == 0) {
            return getString(R.string.password_blank);
        } else {
            return "";
        }
    }

    private void showProgress(boolean show) {
        mProgressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mMessageErrorTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        mBaseRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void openMain(String codigoUsuario) {
        boolean isVersionOld = false;

        if (login != null) {

            if (!BuildConfig.VERSION_NAME.equals(login.getVersion())) {
                isVersionOld = true;
            }

            if (!isVersionOld) {
                LogUser.log(Config.TAG, "Aplicativo atualizado");

                if (userInfo.isFirstLogin(this, codigoUsuario)) {
                    Intent syncDataIntent = new Intent(this, SyncDataActivity.class);
                    startActivityForResult(syncDataIntent, Config.REQUEST_SYNC_DATABASE);
                } else {
                    UsuarioDao usuarioDao = new UsuarioDao(this);
                    UnidadeNegocioDao unidadeNegocioDao = new UnidadeNegocioDao(this);
                    Usuario usuario = usuarioDao.get(codigoUsuario);
                    if (usuario != null) {

                        String unidadeNegocioSelecionada = userInfo.getUserUnidadeNegocioSelected(getApplicationContext());
                        UnidadeNegocio unidadeNegocio = null;

                        if (TextUtils.isNullOrEmpty(unidadeNegocioSelecionada)) {
                            unidadeNegocio = unidadeNegocioDao.getAll(usuario.getCodigo()).get(0);
                            userInfo.saveUserUnidadeNegocioSelected(unidadeNegocio.getCodigo(), getApplicationContext());
                        } else {
                            for (UnidadeNegocio item : unidadeNegocioDao.getAll(usuario.getCodigo())) {
                                if (item.getCodigo().equals(unidadeNegocioSelecionada)) {
                                    unidadeNegocio = item;
                                    break;
                                }
                            }
                        }

                        Current.setValues(usuario, unidadeNegocio);

                        Intent mainIntent = new Intent(LoginActivity.this, MasterActivity.class);
                        finish();
                        startActivity(mainIntent);

                        try {
                            Perfil perfil = Current.getInstance(this).getUsuario().getPerfil();
                            if (perfil != null) {
                                if (!perfil.isPermitePlanejamentoRota() && perfil.isPermiteRotaGuiada() && UsuarioUtils.isPromotor(Current.getInstance(this).getUsuario().getCodigoFuncao())) {
                                    Intent intent = RotaGuiadaActivity.newIntent(this);
                                    startActivity(intent);

                                }
                            }
                        }catch (Exception ex){
                            LogUser.log(Config.TAG, ex.toString());
                        }

                    } else {
                        ConfigUserSync configUserSync = new ConfigUserSync(BuildConfig.DATABASE_VERSION, BuildConfig.DATABASE_NAME);
                        SyncDataManager syncDataManager = new SyncDataManager(this, configUserSync);
                        syncDataManager.deleteDictionary(this);

                        Intent syncDataIntent = new Intent(this, SyncDataActivity.class);
                        startActivityForResult(syncDataIntent, Config.REQUEST_SYNC_DATABASE);
                    }
                }
            } else {
                LogUser.log(Config.TAG, "Aplicativo nao esta atualizado");

                Intent systemUpdate = new Intent(LoginActivity.this,
                        SystemUpdateActivity.class);
                startActivityForResult(systemUpdate, Config.REQUEST_UPDATE_APK);
            }

        } else {
            showProgress(false);
        }
    }

    private void doLogin() {
        try {

            if (!isToken) {
                login.setUser(mUserEditText.getText().toString());
                login.setPassword(mPasswordEditText.getText().toString());
            }

            userInfo.saveUserInfo(login, LoginActivity.this);
            initializeJJSDK();
            openMain(login.getUserId());

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_ALTERA_SENHA) {
            showProgress(false);
            if (resultCode == RESULT_OK) {
                login = userInfo.getUserInfo(this);
                openMain(login.getUserId());
            }
        } else if(login != null && requestCode == login.getIdRetorno() && TStatusRD.Companion.isCheckExists(login.getIdRetorno())) {
            var tStatus = TStatusRD.Companion.getStatusName(login.getIdRetorno());
            if(tStatus != TStatusRD.BLOQUEIO_AUT_EMAIL &&
                        tStatus != TStatusRD.BLOQUEIO_EMAIL &&
                        tStatus != TStatusRD.BLOQUEIO_NOME) {
                doLogin();
            } else {
                showProgress(false);
            }
        } else if (requestCode == Config.REQUEST_UPDATE_APK  ) {
            showProgress(false);
        } else if (requestCode == Config.REQUEST_SYNC_DATABASE || (requestCode == Config.REQUEST_SSO)) {
            showProgress(false);
            if (resultCode == RESULT_OK) {
                login = userInfo.getUserInfo(LoginActivity.this);
                openMain(login.getUserId());
            }

            if(login != null && loginTypeDialog != null){
                loginTypeDialog.dismiss();
            }
        } else if (requestCode == Config.REQUEST_ALTERA_SENHA_PASSTROUGH) {
            if (resultCode == RESULT_OK && data != null) {
                mPasswordEditText.setText(data.getStringExtra(AlteraSenhaComPassThroughActivity.KEY_PASSWORD));
                mUserEditText.setText(data.getStringExtra(AlteraSenhaComPassThroughActivity.KEY_USERID));
                login();
            } else{
                mPasswordEditText.setText("");
                mUserEditText.setText("");
            }
        }
    }

    private void configDialogProgress() {
        try {
            runOnUiThread(() -> {
                bar = new ProgressDialog(LoginActivity.this);
                bar.setCancelable(false);
                bar.setIndeterminate(true);
                String msg = getString(R.string.aguarde_reset_login);
                bar.setMessage(msg);

                if (!LoginActivity.this.isFinishing() && bar != null) {
                    bar.show();
                }
            });


        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void initializeJJSDK() {
        UserInfo userInfo = new UserInfo();
        JJSDK.setToken(this, userInfo.getUserInfo(this).getToken());
        JJSDK.setCodUser(this, userInfo.getUserInfo(this).getUserId());
    }

}
