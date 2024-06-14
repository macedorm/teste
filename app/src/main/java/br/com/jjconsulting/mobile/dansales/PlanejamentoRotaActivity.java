package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class PlanejamentoRotaActivity extends SingleFragmentActivity {

    private PlanejamentoRotaFragment fragment;


    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, PlanejamentoRotaActivity.class);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public Fragment createFragment() {
        fragment = PlanejamentoRotaFragment.newInstance();
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(fragment != null)
            fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}

