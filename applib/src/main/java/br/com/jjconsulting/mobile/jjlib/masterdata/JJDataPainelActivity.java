package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.util.KeyboardUtils;

public class JJDataPainelActivity extends SingleFragmentDataPainelActivity {

    private static JJDataPainelView jjDataPainelView;
    private static JJTabContentView.OnFinish onFinish;

    public static Intent newIntent(Context context, String elementName, DataTable values, TPageState tPageState, boolean showTitle, boolean reopenForm, JJTabContentView.OnFinish finish) {
        Intent it = new Intent(context, JJDataPainelActivity.class);
        it.putExtra(DATA_KEY_ELEMENT_NAME, elementName);
        it.putExtra(DATA_KEY_PAGE_STATE, tPageState.getValue());
        it.putExtra(DATA_KEY_SHOW_TITLE, showTitle);
        it.putExtra(DATA_KEY_REOPEN_FORM, reopenForm);


        if (values != null) {
            it.putExtra(DATA_KEY_DATA_TABLE, values);
        }

        onFinish = finish;

        jjDataPainelView = null;
        return it;
    }




    public static Intent newIntent(Context context, String elementName, TPageState tPageState, Hashtable relations, boolean showTitle, boolean reopenForm, JJTabContentView.OnFinish finish) {
        Intent it = new Intent(context, JJDataPainelActivity.class);
        it.putExtra(DATA_KEY_ELEMENT_NAME, elementName);
        it.putExtra(DATA_KEY_PAGE_STATE, tPageState.getValue());
        it.putExtra(DATA_KEY_SHOW_TITLE, showTitle);
        it.putExtra(DATA_KEY_RELATION, relations);
        it.putExtra(DATA_KEY_REOPEN_FORM, reopenForm);

        jjDataPainelView = null;

        onFinish = finish;

        return it;
    }


    @Override
    protected Fragment createFragment() {

        jjDataPainelView = JJDataPainelView.renderFragment(this, getElementName(), getDataTable(), getTPageState(), isReopenForm());
        jjDataPainelView.setShowTitle(isShowTitle());
        jjDataPainelView.setOnFinish(onFinish);

        if(getRelationValues() != null){
            jjDataPainelView.setRelationValues(getRelationValues());
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return jjDataPainelView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                KeyboardUtils.hideKeyboard(JJDataPainelActivity.this);
                finish();
                return true;
            default:
                jjDataPainelView.addItens();
                return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        jjDataPainelView.onActivityResult(requestCode, resultCode, data);
    }

    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}
