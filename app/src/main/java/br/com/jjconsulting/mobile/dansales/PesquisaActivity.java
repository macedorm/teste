package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext, String data) {
        Intent intent = new Intent(packageContext, PesquisaActivity.class);
        if(!TextUtils.isNullOrEmpty(data)){
            intent.putExtra(KEY_DATA_PAR, data);
        }
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PesquisaFragment.newInstance(getData(), PesquisaDao.TTypePesquisa.PESQUISA);
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }


}
