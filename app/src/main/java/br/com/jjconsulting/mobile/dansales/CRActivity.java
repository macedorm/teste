package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class CRActivity extends SingleFragmentActivity{

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, CRActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return CRFragment.newInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
