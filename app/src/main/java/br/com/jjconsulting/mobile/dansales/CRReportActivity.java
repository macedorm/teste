package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class CRReportActivity extends SingleFragmentActivity{

    public static Intent newIntent(Context packageContext, Layout layout) {
        Gson gson = new Gson();
        Intent intent = new Intent(packageContext, CRReportActivity.class);
        intent.putExtra(KEY_DATA_PAR, gson.toJson(layout));
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Gson gson = new Gson();
        return CRReportFragment.newInstance(gson.fromJson(getData(), Layout.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
