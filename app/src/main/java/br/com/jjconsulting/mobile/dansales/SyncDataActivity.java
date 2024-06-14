package br.com.jjconsulting.mobile.dansales;

import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class SyncDataActivity extends SingleFragmentActivity {

    private boolean canGoBack;

    public SyncDataActivity() {
        super();
        canGoBack = true;
    }

    @Override
    public void onBackPressed() {
        if (canGoBack) {
            super.onBackPressed();
        }
    }

    @Override
    protected Fragment createFragment() {
        SyncDataFragment fragment = SyncDataFragment.newInstance();
        fragment.setIsLogin(true);
        return fragment;
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return false;
    }

    public boolean isCanGoBack() {
        return canGoBack;
    }

    public void setCanGoBack(boolean canGoBack) {
        this.canGoBack = canGoBack;
    }
}
