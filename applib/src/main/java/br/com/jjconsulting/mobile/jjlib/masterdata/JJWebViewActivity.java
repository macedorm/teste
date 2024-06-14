package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import java.util.Hashtable;
import java.util.Set;

import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJWebViewActivity extends SingleFragmentDataPainelActivity {

    private JJWebView jjWebView;

    public static Intent newIntent(Context context, String url, Hashtable formValue) {
        try {
            Set<String> keys = formValue.keySet();

            for (String key : keys) {
                String value = formValue.get(key).toString();
                if (!TextUtils.isNullOrEmpty(value)) {
                    url = url.replace("{" + key + "}", value);
                }
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        Intent it = new Intent(context, JJWebViewActivity.class);
        it.putExtra(DATA_KEY_PARM, url);
        return it;
    }

    public static Intent newIntent(Context context, String url, FormElement formElement, DataTable dataTable) {
        Factory factory = new Factory(context);

        try {
            for (FormElementField item : formElement.getFormfields()) {
                String value = factory.getValueFieldName(dataTable, item.getFieldname(), formElement);
                if (!TextUtils.isNullOrEmpty(value)) {
                    url = url.replace("{" + item.getFieldname() + "}", value);
                }
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        Intent it = new Intent(context, JJWebViewActivity.class);
        it.putExtra(DATA_KEY_PARM, url);
        return it;
    }

    @Override
    protected Fragment createFragment() {
        jjWebView = JJWebView.renderFragment(this, getParam());
        return jjWebView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
