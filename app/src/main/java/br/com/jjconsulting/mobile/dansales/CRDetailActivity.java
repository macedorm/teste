package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class CRDetailActivity extends SingleFragmentActivity{

    public static Intent newIntent(Context packageContext, Layout layout, boolean isShowButton) {
        Gson gson = new Gson();
        Intent intent = new Intent(packageContext, CRDetailActivity.class);
        intent.putExtra(KEY_DATA_PAR, gson.toJson(layout));
        intent.putExtra(KEY_DATA_PAR_BOOLEAN, isShowButton);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Gson gson = new Gson();
        return CRDetailFragment.newInstance(gson.fromJson(getData(), Layout.class),   isDataBoolean());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
