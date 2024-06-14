package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.dansales.kotlin.PedidoTrackingFragment;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class LogPedidoActivity extends SingleFragmentActivity {

    private static final String ARG_CODIGO = "codigo";

    public static Intent newIntent(Context packageContext, String codigo) {
        Intent intent = new Intent(packageContext, LogPedidoActivity.class);
        intent.putExtra(ARG_CODIGO, codigo);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        if (intent == null) {
            throw new RuntimeException("Must provide arguments");
        }

        return null; //PedidoTrackingFragment.Companion.newInstance("2554221", "20", false);
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }
}
