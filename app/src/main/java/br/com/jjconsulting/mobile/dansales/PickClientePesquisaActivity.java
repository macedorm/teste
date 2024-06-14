package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class PickClientePesquisaActivity extends SingleFragmentActivity
        implements ClientesFragment.OnClienteClickListener {

    public static final String FILTER_RESULT_DATA_KEY = "filter_cliente_pesquisa_result";

    public static Intent newIntent(Context packageContext, String idPesquisa) {
        Intent intent = new Intent(packageContext, PickClientePesquisaActivity.class);
        intent.putExtra(KEY_DATA_PAR, idPesquisa);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return ClientesFragment.newInstance(getData());
    }

    @Override
    public void onClienteClick(Cliente cliente) {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, cliente.getCodigo());
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
