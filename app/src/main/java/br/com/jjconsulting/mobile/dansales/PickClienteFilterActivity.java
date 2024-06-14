package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class PickClienteFilterActivity extends SingleFragmentActivity
        implements ClientesFragment.OnClienteClickListener {

    public static final String FILTER_RESULT_DATA_KEY = "filter_cliente_seatch_result";

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, PickClienteFilterActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return ClientesFragment.newInstance();
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, cliente);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
