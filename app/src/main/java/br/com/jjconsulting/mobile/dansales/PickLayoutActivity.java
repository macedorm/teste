package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class PickLayoutActivity extends SingleFragmentActivity
        implements LayoutFragment.OnLayoutClickListener {

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, PickLayoutActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return LayoutFragment.newInstance();
    }

    @Override
    public void onLayoutClick(Layout layout) {

        Gson gson = new Gson();
        String json = gson.toJson(layout);

        Intent returnIntent = new Intent();
        returnIntent.putExtra(PesquisaResumoDialog.LAYOUT_KEY, json);

        setResult(Activity.RESULT_OK, returnIntent);

        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
