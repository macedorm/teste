package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.dansales.util.UserInfo;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;

public class SettingsActivity extends SingleFragmentActivity {

    public final static String KEY_LOGIN = "login";
    private boolean isLogin;
    private SettingsFragment fragment;
    private DialogsCustom dialogsDefault;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        dialogsDefault = new DialogsCustom(this);

        try {
            isLogin = getIntent().getExtras().getBoolean(KEY_LOGIN, false);
        } catch (Exception ex) {
            isLogin = fragment.isLogin();
        }

        if (fragment != null) {
            fragment.setLogin(isLogin);
        }

    }

    @Override
    protected Fragment createFragment() {
        fragment = SettingsFragment.newInstance();
        return fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        UserInfo user = new UserInfo();

        if (fragment != null) {
            fragment.updateUser();
        }
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}
