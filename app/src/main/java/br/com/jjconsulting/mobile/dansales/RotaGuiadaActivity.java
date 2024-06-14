package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class RotaGuiadaActivity extends SingleFragmentActivity {

    private RotaGuiadaFragment fragment;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, RotaGuiadaActivity.class);
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    protected Fragment createFragment() {
        fragment = RotaGuiadaFragment.newInstance();
        return fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}
