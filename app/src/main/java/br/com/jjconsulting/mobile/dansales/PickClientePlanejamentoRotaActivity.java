package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;

public class PickClientePlanejamentoRotaActivity extends SingleFragmentActivity
        implements ClientesPlanejamentoRotaFragment.OnClienteClickListener {

    public static final String FILTER_RESULT_DATA_KEY = "filter_cliente_planejamento_result";

    public static Intent newIntent(Context packageContext, String promotor, String date) {
        Intent intent = new Intent(packageContext, PickClientePlanejamentoRotaActivity.class);
        intent.putExtra(KEY_DATA_PAR, promotor);
        intent.putExtra(KEY_CALENDAR_PAR, date);

        return intent;
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, PickClientePlanejamentoRotaActivity.class);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Fragment createFragment() {
        return ClientesPlanejamentoRotaFragment.newInstance(getData(), getCurrentDate());
    }

    @Override
    public void onClienteClick(List<Cliente> clientes) {
        Intent data = new Intent();

        Gson gson = new Gson();
        String json = gson.toJson(clientes);

        data.putExtra(FILTER_RESULT_DATA_KEY, json);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
