package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.FirebaseUtils;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.OnUpdateChangeScreen;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.syncData.model.ConfigUserSync;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.NetworkUtils;

public class SyncDataFragment extends BaseFragment implements View.OnClickListener {

    private SyncDataManager mManagerSyncData;

    private OnUpdateChangeScreen mOnUpdateChangeScreen;

    private ProgressBar mProgressBarDatabaseSync;
    private TextView mMessageErrorTextView;
    private TextView mProgressTextView;
    private Button mButtonStartCancel;

    private boolean mIsFinish;
    private boolean mIsLogin;

    public static SyncDataFragment newInstance() {
        return new SyncDataFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mOnUpdateChangeScreen = new SyncHandler();
    }

    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        lockRotate(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync_data, container, false);

        lockRotate(true);

        mProgressBarDatabaseSync = view.findViewById(R.id.pb_database_sync);
        mProgressBarDatabaseSync.setSecondaryProgress(100);
        mMessageErrorTextView = view.findViewById(R.id.message_error_text_view);
        mProgressTextView = view.findViewById(R.id.sync_progress_text_view);
        mButtonStartCancel = view.findViewById(R.id.process_button);

        changeButton(false);
        mButtonStartCancel.setOnClickListener(this);
        showProgressUdapte(false);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.process_button:
                if (mButtonStartCancel.getText().equals(getString(R.string.cancel))) {

                    dialogsDefault.showDialogQuestion(getString(R.string.mensagem_cancelar_update), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                        @Override
                        public void onClickPositive() {
                            mManagerSyncData.setCancel(true);
                        }

                        @Override
                        public void onClickNegative() {

                        }
                    });

                } else {
                    switch (NetworkUtils.getNetworkType(getContext())) {
                        case 0:
                            mIsFinish = false;
                            dialogsDefault.showDialogMessage(getString(R.string.not_connected), dialogsDefault.DIALOG_TYPE_ERROR, null);
                            break;
                        case 1:
                            startSync();
                            break;
                        case 2:
                            dialogsDefault.showDialogQuestion(getString(R.string.udapte_not_wifi), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                                @Override
                                public void onClickPositive() {
                                    startSync();
                                }

                                @Override
                                public void onClickNegative() {

                                }
                            });

                            break;
                    }
                }
                break;
        }
    }

    private void changeButton(boolean isCancel) {
        getActivity().runOnUiThread(() -> {
            if (!isCancel) {
                mButtonStartCancel.setVisibility(View.VISIBLE);
                mButtonStartCancel.setText(getString(R.string.start));
            } else {
                mButtonStartCancel.setVisibility(View.VISIBLE);
                mButtonStartCancel.setText(getString(R.string.cancel));
            }
        });
    }

    private void showProgressUdapte(boolean show) {
        if (!mIsLogin) {
            try {
                ((MasterActivity) getActivity()).setDrawerEnabled(!show);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (show) {
            mMessageErrorTextView.setVisibility(View.VISIBLE);
            mProgressBarDatabaseSync.setVisibility(View.VISIBLE);
            mProgressTextView.setVisibility(View.VISIBLE);
        } else {
            mMessageErrorTextView.setVisibility(View.INVISIBLE);
            mProgressBarDatabaseSync.setVisibility(View.INVISIBLE);
            mProgressTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void setProgress(int progress) {
        mProgressBarDatabaseSync.setProgress(progress);
        mProgressTextView.setText(progress + "%");
    }

    private void finishSync() {
        //Atualiza o perfil do usuário após o sincronismo
        Context context = getContext();
        Current current = Current.getInstance(context);
        UsuarioDao usuarioDao = new UsuarioDao(context);
        String userId = current.getUsuario().getCodigo();
        UnidadeNegocio unNeg = current.getUnidadeNegocio();
        current.setValues(usuarioDao.get(userId), unNeg);

        MasterActivity master = (MasterActivity) getActivity();
        master.finish();
        startActivity(master.getIntent());
    }

    private void backHome() {
        MasterActivity master = (MasterActivity) getActivity();
        Fragment fragment = master.createFragmentByNavId(R.id.nav_home);

        master.setupHeaderData();
        MenuItem item = master.getNavigationView().getMenu().findItem(R.id.nav_home);

        if (fragment != null && item != null) {
            master.setupNewFragment(fragment, item);
        }
    }

    private void startSync() {
        lockRotate(true);
        showProgressUdapte(true);
        mButtonStartCancel.setVisibility(View.GONE);
        setProgress(0);

        UserInfo userInfo = new UserInfo();

        JJSDK.initializeAuthConnection(getContext(), BuildConfig.URL_API, BuildConfig.USER, BuildConfig.USERKEY, BuildConfig.DATABASE_NAME, BuildConfig.DATABASE_VERSION);

        ConfigUserSync configUserSync = new ConfigUserSync(BuildConfig.DATABASE_VERSION, BuildConfig.DATABASE_NAME);
        configUserSync.setIdUser(userInfo.getUserInfo(getContext()).getUserId());
        configUserSync.setVersion(BuildConfig.VERSION_NAME);
        configUserSync.setToken(userInfo.getUserInfo(getContext()).getToken());
        configUserSync.setUrl(JJSDK.getHost(getActivity()));

        mManagerSyncData = new SyncDataManager(getActivity(), configUserSync, mOnUpdateChangeScreen);
        mManagerSyncData.setCancel(false);

        mManagerSyncData.connectionStart();

    }

    public void setIsLogin(boolean value) {
        mIsLogin = value;
    }

    public class SyncHandler implements OnUpdateChangeScreen {

        @Override
        public void onPreparation() {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                try {
                    handleHomeUpButtonOnParentActivity(activity, false);
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onPreparation: " + ex.toString());
                }
            });
        }

        @Override
        public void onStart() {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                try {
                    changeButton(true);
                    handleHomeUpButtonOnParentActivity(activity, false);
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onStart: " + ex.toString());
                }
            });
        }

        @Override
        public void onUpdateNotAvailabe() {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                try {
                    LogUser.log(Config.TAG, "No updates");
                    //lockRotate(false);
                    mIsFinish = true;
                    showProgressUdapte(false);
                    mButtonStartCancel.setVisibility(View.GONE);
                    handleHomeUpButtonOnParentActivity(activity, true);

                    dialogsDefault.showDialogMessage(getString(R.string.not_update), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
                        @Override
                        public void onClick() {
                            try {
                                if (mIsLogin) {
                                    UserInfo userInfo = new UserInfo();

                                    String codUser = JJSDK.getCodUser(getContext());

                                    if (userInfo.isFirstLogin(getActivity(), codUser)) {
                                        userInfo.setFirstLogin(getActivity(), codUser);
                                    }

                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();
                                } else {
                                    backHome();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onUpdateNotAvailabe: " + ex);
                }
            });
        }

        @Override
        public void onFinish(int totalRow, long timeTotalUpdate) {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                try {
                    LogUser.log(Config.TAG, "Updates sucess");
                    //lockRotate(false);
                    setProgress(100);
                    showProgressUdapte(false);
                    mIsFinish = true;
                    mButtonStartCancel.setVisibility(View.GONE);
                    handleHomeUpButtonOnParentActivity(activity, true);

                    dialogsDefault.showDialogMessage(getString(R.string.update_sucess), dialogsDefault.DIALOG_TYPE_SUCESS, new DialogsCustom.OnClickDialogMessage() {
                        @Override
                        public void onClick() {
                            try {

                                Bundle params = new Bundle();
                                params.putString("Result", "Success");
                                params.putString("TotalRecords", String.valueOf(totalRow));
                                params.putString("TotalSeconds", String.valueOf(timeTotalUpdate));
                                params.putBoolean("AutoSync", false);

                                FirebaseUtils.sendEvent(getContext(),"sync_data", params);

                                if (mIsFinish) {
                                    mIsFinish = false;
                                    if (mIsLogin) {

                                        user.setFirstLogin(getContext(), JJSDK.getCodUser(getContext()));
                                        getActivity().setResult(Activity.RESULT_OK);
                                        getActivity().finish();
                                    } else {
                                        finishSync();
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    UserInfo userInfo = new UserInfo();
                    String codUser = JJSDK.getCodUser(getContext());

                    if (userInfo.isFirstLogin(getActivity(), codUser)) {
                        userInfo.setFirstLogin(getActivity(), codUser);
                    }

                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onFinish: " + ex.toString());
                }
            });
        }

        @Override
        public void onCancel() {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            activity.runOnUiThread(() -> {
                try {
                    LogUser.log(Config.TAG, "Update Cancel");
                    //lockRotate(false);
                    changeButton(false);
                    handleHomeUpButtonOnParentActivity(activity, true);
                    showProgressUdapte(false);
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onCancel: " + ex.toString());
                }
            });
        }

        @Override
        public void onError(String message) {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            try {
                Bundle params = new Bundle();
                params.putString("Result", "Error");
                params.putString("Message", message);
                params.putBoolean("AutoSync", false);

                FirebaseUtils.sendEvent(getContext(), "sync_data", params);

            }catch (Exception ex){
                LogUser.log(ex.toString());
            }

            activity.runOnUiThread(() -> {
                try {
                    LogUser.log(Config.TAG, "Updates error");
                    //lockRotate(false);
                    setProgress(100);
                    showProgressUdapte(false);
                    changeButton(false);
                    mButtonStartCancel.setVisibility(View.GONE);
                    handleHomeUpButtonOnParentActivity(activity, true);

                    if(ManagerSystemUpdate.isRequiredUpadate(getContext(), message)){
                        changeButton(false);
                        return;
                    }

                    dialogsDefault.showDialogMessage(message, dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogMessage() {
                        @Override
                        public void onClick() {
                            try {
                                changeButton(false);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onError: " + ex.toString());
                }
            });
        }

        @Override
        public void onProgressStatus(String message) {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                mMessageErrorTextView.setText(message);
            });
        }

        @Override
        public void onProgress(int totalRow, int currentRow, int currentProgress) {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                try {
                    setProgress(currentProgress);
                    mMessageErrorTextView.setText(Html.fromHtml(
                            String.format("%s %s %s %s %s", getString(R.string.update),
                                    FormatUtils.toStringIntFormat(currentRow), getString(R.string.de), FormatUtils.toStringIntFormat(totalRow),
                                    getString(R.string.rows))));
                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onProgress: " + ex.toString());
                }
            });
        }

        @Override
        public void onErrorConnection() {
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() -> {
                try {
                    showProgressUdapte(false);
                    mButtonStartCancel.setVisibility(View.GONE);
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    handleHomeUpButtonOnParentActivity(activity, true);

                    dialogsDefault.showDialogMessage(getString(R.string.udapte_error), dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogMessage() {
                        @Override
                        public void onClick() {
                            try {
                                changeButton(false);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                } catch (Exception ex) {
                    LogUser.log(Config.TAG, "onErrorConnection: " + ex);
                }
            });
        }

        public void handleHomeUpButtonOnParentActivity(Activity activity, boolean show) {
            if (!activity.getClass().equals(SyncDataActivity.class)) {
                return;
            }

            SyncDataActivity syncDataActivity = (SyncDataActivity) activity;
            if (show) {
                syncDataActivity.setCanGoBack(true);
                syncDataActivity.showHomeUpButton();
            } else {
                syncDataActivity.setCanGoBack(false);
                syncDataActivity.hideHomeUpButton();
            }
        }

    }


    public void lockRotate(boolean lock) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (lock) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            getActivity().getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public boolean getManagerSyncData() {
        return mManagerSyncData.isProgress();
    }


}
