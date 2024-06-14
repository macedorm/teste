package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskFilter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.data.NotaFilter;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class LayoutFilterActivity extends AppCompatActivity {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";
    public static final String FILTER_REGIAO_DATA_KEY = "filter_regiao";

    private LayoutFilter mLayoutFilter;

    private SpinnerArrayAdapter<String> mRegiaoSpinnerAdapter;
    private Spinner mRegiaoSpinner;

    private String[] mRegiaoList;

    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;

    private TextView mDataStartTextView;
    private LinearLayout mDataStartLinearLayout;
    private DatePickerDialog.OnDateSetListener mDataStartDateSetListener;
    private DatePickerDialog mDataStartdatePickerDialog;

    private boolean mIsVisibleMenu;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_filter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mLayoutFilter = (LayoutFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mLayoutFilter = (LayoutFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        if (getIntent().hasExtra(FILTER_REGIAO_DATA_KEY)) {
            mRegiaoList = getIntent()
                    .getStringArrayExtra(FILTER_REGIAO_DATA_KEY);
        }

        mRegiaoSpinner = findViewById(R.id.regiao_spinner);

        mProgressLinearLayout = findViewById(R.id.loading_layout_filter);
        mBaseLinearLayout = findViewById(R.id.base_layout_filter);

        //Setup Data Picker Start
        mDataStartTextView = findViewById(R.id.data_start_text_view);
        mDataStartLinearLayout = findViewById(R.id.data_start_linear_layout);

        mProgressLinearLayout.setVisibility(View.VISIBLE);
        mBaseLinearLayout.setVisibility(View.GONE);


        setupRegiaoSpinner(mRegiaoList);
        setupDataPicker();
        mIsVisibleMenu = true;
        invalidateOptionsMenu();
        mProgressLinearLayout.setVisibility(View.GONE);
        mBaseLinearLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mLayoutFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsVisibleMenu) {
            MenuInflater menuInflater = getMenuInflater();
            menuInflater.inflate(R.menu.apply_filter_menu, menu);
            menuInflater.inflate(R.menu.cancel_filter_menu, menu);
        }
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
        String regiao = null;
        Date dateStart = null;

        boolean isThereAnyOrganizacaoSelected = mRegiaoSpinnerAdapter.isThereAnyItemSelected(
                mRegiaoSpinner);
        if (isThereAnyOrganizacaoSelected) {
            regiao = (String) mRegiaoSpinner.getSelectedItem();
        }


        if (!mDataStartTextView.equals(getString(R.string.data_start_hint))) {
            try {
                dateStart = FormatUtils.toDate(FormatUtils.toConvertDate(mDataStartTextView.getText().toString(), "dd/MM/yyyy", "yyyy-MM-dd HH:mm"));
            } catch (Exception ex) {
                dateStart = null;
            }
        }

        mLayoutFilter = new LayoutFilter(regiao, dateStart);
    }

    private void bundleEmptyFilter() {
        mLayoutFilter = new LayoutFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mLayoutFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupRegiaoSpinner(String[] regioes) {

        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(regioes,
                getString(R.string.select_regiao));


        mRegiaoSpinnerAdapter = new SpinnerArrayAdapter<String>(
                this, objects, true) {
            @Override
            public String getItemDescription(String item) {
                return item;
            }
        };

        mRegiaoSpinner.setAdapter(mRegiaoSpinnerAdapter);

        if (mLayoutFilter != null && !TextUtils.isNullOrEmpty(mLayoutFilter.getFilter())) {
            int indexOf = ArrayUtils.indexOf(objects, mLayoutFilter.getFilter());
            if (indexOf > 0) {
                mRegiaoSpinner.setSelection(indexOf);
            }

        }
    }


    private void setupDataPicker() {

        //Date Picker Start
        mDataStartLinearLayout.setOnClickListener(view -> {
            mDataStartdatePickerDialog.show();
        });

        mDataStartDateSetListener = (DatePicker datePicker, int year, int month, int day) -> {
            try {
                String date = FormatUtils.toDateCreateDatePicker(year, month, day);
                mDataStartTextView.setText(date);
                createStartDataPicker(false, year, month, day);

            } catch (ParseException e) {
                LogUser.log(Config.TAG, e.toString());
            }
        };

        createStartDataPicker(true, 0, 0, 0);


        if (mLayoutFilter != null) {
            if (mLayoutFilter.getDateStart() != null) {
                String date = FormatUtils.toDefaultDateFormat(mLayoutFilter.getDateStart());
                try {
                    mDataStartTextView.setText(FormatUtils.toDateTimeText(date));
                } catch (ParseException e) {
                    LogUser.log(Config.TAG, e.toString());
                }
            }

        }
    }

    private void createStartDataPicker(boolean isDataNow, int year, int month, int day) {

        if (isDataNow) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        mDataStartdatePickerDialog = new DatePickerDialog(
                    this, mDataStartDateSetListener, year, month, day);

    }
}
