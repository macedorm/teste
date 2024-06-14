package br.com.jjconsulting.mobile.dansales;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.RestoreDataStorage;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private static boolean isLogin;

    private RestoreDataStorage restoreDataStorage;
    private int screenOrientation;
    private Toast toast;
    private ProgressDialog bar;

    public SettingsFragment() { }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button buttonEnvironment = view.findViewById(R.id.environment_button);
        Button buttonChangePassword = view.findViewById(R.id.change_password_button);
        Button buttonRestore = view.findViewById(R.id.restore_button);
        Button buttonSync = view.findViewById(R.id.sync_button);
        Button buttonRecoverPassword = view.findViewById(R.id.recover_password_button);

        buttonEnvironment.setOnClickListener(this);
        buttonChangePassword.setOnClickListener(this);
        buttonRestore.setOnClickListener(this);
        buttonSync.setOnClickListener(this);
        buttonRecoverPassword.setOnClickListener(this);

        buttonSync.setVisibility(View.GONE);

        configDialogProgress();

        FragmentActivity activity = getActivity();

        screenOrientation = activity.getRequestedOrientation();
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        return view;
    }

    @Override
    public void onDestroy() {
        getActivity().setRequestedOrientation(screenOrientation);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.environment_button:
                onEvironmentButtonClick();
                break;
            case R.id.change_password_button:
                onChangePasswordButtonClick();
                break;
            case R.id.restore_button:
                onRestoreButtonClick();
                break;
            case R.id.sync_button:
                onSyncButtonClick();
                break;
            case R.id.recover_password_button:
                onRecoverPasswordButtonClick();
                break;
        }
    }

    private void onEvironmentButtonClick() {
        Intent it = new Intent(getActivity(), AmbienteActivity.class);
        it.putExtra(AmbienteActivity.KEY_LOGIN, isLogin);
        startActivity(it);
    }

    private void onChangePasswordButtonClick() {
        try {
            if (!isLogin) {
                Intent it = new Intent(getActivity(), AlteraSenhaActivity.class);
                it.putExtra(AlteraSenhaActivity.KEY_ID_USER, user.getUserInfo(getContext()).getUserId());
                startActivityForResult(it, Config.REQUEST_ALTERA_SENHA);
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getContext(),
                        getString(R.string.ambiente_error_change_password), Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void onRestoreButtonClick() {
        dialogsDefault.showDialogQuestion(getString(R.string.title_restore_dialog),
                dialogsDefault.DIALOG_TYPE_QUESTION,
                new DialogsCustom.OnClickDialogQuestion() {
            @Override
            public void onClickPositive() {
                restoreDataStorage = new RestoreDataStorage(getActivity());
                showProgress(true);
                restoreDataStorage.startRestore(isDeleted -> {
                    Runnable task = () -> {
                        if (isDeleted) {
                            dialogsDefault.showDialogMessage(getString(R.string.ok_restore_dialog),
                                    dialogsDefault.DIALOG_TYPE_SUCESS,
                                    new DialogsCustom.OnClickDialogMessage() {
                                public void onClick() {
                                    finishRestore();
                                }
                            });
                        } else {
                            dialogsDefault.showDialogMessage(
                                    getString(R.string.error_restore_dialog),
                                    dialogsDefault.DIALOG_TYPE_SUCESS, null);
                        }
                        showProgress(false);
                    };

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(task);
                    }
                });
            }

            @Override
            public void onClickNegative() { }
        });
    }

    private void onSyncButtonClick() {
        Intent it = new Intent(getActivity(), SyncDataActivity.class);
        startActivity(it);
    }

    private void onRecoverPasswordButtonClick() {
        Intent recoverPasswordIntent = new Intent(getActivity(), RecuperaSenhaActivity.class);
        startActivity(recoverPasswordIntent);
    }

    public void showProgress(boolean isShow) {
        if (getActivity().getWindow().getDecorView().isShown()) {
            if (isShow) {
                bar.show();
            } else {
                bar.dismiss();
            }
        }
    }

    public void updateUser() {
        user.getUserInfo(getActivity());
    }

    private void finishRestore() {
        FragmentActivity activity = getActivity();

        if (activity == null)
            return;

        if (!isLogin) {
            Intent intent = new Intent();
            activity.setResult(FragmentActivity.RESULT_OK, intent);
        }

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the existing activities in the stack
        startActivity(intent);

    }

    private void configDialogProgress() {
        bar = new ProgressDialog(getActivity());
        bar.setCancelable(false);
        bar.setIndeterminate(true);
        bar.setMessage(getString(R.string.aguarde));
    }
}
