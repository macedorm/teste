package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class ClienteDetailActivity extends SingleFragmentActivity {

    private static final String ARG_CODIGO = "codigo";
    private static final String ARG_IS_ROUTE = "is_route";


    public static Intent newIntent(Context packageContext, String codigo, boolean isRoute) {
        Intent intent = new Intent(packageContext, ClienteDetailActivity.class);
        intent.putExtra(ARG_CODIGO, codigo);
        intent.putExtra(ARG_IS_ROUTE, isRoute);

        return intent;
    }


    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("Must provide arguments");
        }

        String codigo = intent.getStringExtra(ARG_CODIGO);
        boolean isRoute = intent.getBooleanExtra(ARG_IS_ROUTE, false);

        return ClienteDetailFragment.newInstance(codigo, isRoute);
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}
