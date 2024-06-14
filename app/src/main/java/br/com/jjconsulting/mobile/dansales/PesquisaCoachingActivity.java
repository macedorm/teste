package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.Date;

import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.jjlib.SingleFragmentActivity;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaCoachingActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context packageContext, String promotor, Date date) {
        Intent intent = new Intent(packageContext, PesquisaCoachingActivity.class);
        if(!TextUtils.isNullOrEmpty(promotor)){
            intent.putExtra(KEY_DATA_PAR, promotor);
        }

        if(date != null){
            intent.putExtra(KEY_CALENDAR_PAR, FormatUtils.toTextToCompareDateInSQlite(date));
        }

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Date date;

        try{
            date = FormatUtils.toDate(getCurrentDate());
        }catch (Exception ex){
            date = new Date();
        }

        return PesquisaFragment.newInstance(getData(), date, PesquisaDao.TTypePesquisa.COACHING);
    }

    @Override
    protected boolean useOnBackPressedInUpNavigation() {
        return true;
    }


}
