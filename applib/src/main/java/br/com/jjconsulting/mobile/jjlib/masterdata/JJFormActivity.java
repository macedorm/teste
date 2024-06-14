package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import java.util.Hashtable;

import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.util.KeyboardUtils;

public class JJFormActivity extends SingleFragmentDataPainelActivity {

    private JJFormView jjFormView;

    public static Intent newIntent(Context context, String elementName, DataTable values, TPageState tPageState, boolean showTitle) {
        Intent it = new Intent(context, JJFormActivity.class);
        it.putExtra(DATA_KEY_ELEMENT_NAME, elementName);
        it.putExtra(DATA_KEY_PAGE_STATE, tPageState.getValue());
        it.putExtra(DATA_KEY_SHOW_TITLE, showTitle);

        if (values != null) {
            it.putExtra(DATA_KEY_DATA_TABLE, values);
        }

        return it;
    }

    public static Intent newIntent(Context context, String elementName, TPageState tPageState, Hashtable relations, boolean showTitle) {
        Intent it = new Intent(context, JJFormActivity.class);
        it.putExtra(DATA_KEY_ELEMENT_NAME, elementName);
        it.putExtra(DATA_KEY_PAGE_STATE, tPageState.getValue());
        it.putExtra(DATA_KEY_SHOW_TITLE, showTitle);
        it.putExtra(DATA_KEY_RELATION, relations);

        return it;
    }

    @Override
    protected Fragment createFragment() {
        jjFormView = JJFormView.renderFragment(this, getElementName());
        jjFormView.setShowTitle(isShowTitle());

        return jjFormView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                KeyboardUtils.hideKeyboard(JJFormActivity.this);
                finish();
                return true;
            default:
                jjFormView.onOptionsItemSelected(item);
                return false;
        }
    }
}
