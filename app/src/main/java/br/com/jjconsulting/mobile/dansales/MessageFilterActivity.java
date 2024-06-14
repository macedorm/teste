package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.MessageFilter;
import br.com.jjconsulting.mobile.dansales.util.MessageUtils;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;

public class MessageFilterActivity extends AppCompatActivity {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";

    private MessageFilter mMessageFilter;

    private SpinnerArrayAdapter<TMessageType> mTypeSpinnerAdapter;
    private Spinner mTypeSpinner;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_filter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mMessageFilter = (MessageFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mMessageFilter = (MessageFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mTypeSpinner = findViewById(R.id.message_origem_spinner);
        setupTypeSpinner();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mMessageFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.apply_filter_menu, menu);
        menuInflater.inflate(R.menu.cancel_filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_apply_filter:
                bundleFilter();
                sendBundledFilter();
                return true;
            case R.id.menu_cancel_filter:
                bundleEmptyFilter();
                sendBundledFilter();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void bundleFilter() {
        TMessageType tMessageType = null;

        boolean isThereAnyMessageTypeSelected = mTypeSpinnerAdapter.isThereAnyItemSelected(
                mTypeSpinner);
        if (isThereAnyMessageTypeSelected) {
            tMessageType = (TMessageType) mTypeSpinner.getSelectedItem();
        }

        mMessageFilter = new MessageFilter(tMessageType);
    }

    private void bundleEmptyFilter() {
        mMessageFilter = new MessageFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mMessageFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupTypeSpinner() {
        Object[] objects = null;

        List<TMessageType> listMessageType = new ArrayList<>();
        listMessageType.add(TMessageType.MESSAGEM);
        listMessageType.add(TMessageType.PUSH);
        listMessageType.add(TMessageType.ROTA_GUIADA);

        objects = SpinnerArrayAdapter.makeObjectsWithHint(listMessageType.toArray(),  getString(R.string.select_origem_message));

        mTypeSpinnerAdapter = new SpinnerArrayAdapter<TMessageType>(
                this, objects, true) {
            @Override
            public String getItemDescription(TMessageType item) {
                return getString(MessageUtils.getString(item));
            }
        };

        mTypeSpinner.setAdapter(mTypeSpinnerAdapter);

        if (mMessageFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mMessageFilter.getTMessageType());
            if (indexOf > 0) {
                mTypeSpinner.setSelection(indexOf);
            }
        }
    }

}
