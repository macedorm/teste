package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class TapLogActivity extends SingleFragmentActivity {

    private static final String ARG_TAP_DETAIL = "tap_detail";

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, TapLogActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("Must provide arguments");
        }

        return TapLogFragment.newInstance();
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}
