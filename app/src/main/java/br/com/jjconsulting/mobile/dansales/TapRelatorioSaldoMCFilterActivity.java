package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.data.TapSaldoMCFilter;
import br.com.jjconsulting.mobile.dansales.model.TapComboRelSaldo;
import br.com.jjconsulting.mobile.dansales.model.TapConnectionType;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapRelatorioSaldoMCFilterActivity extends AppCompatActivity {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";

    private String mParameters[];
    private Object[] mMasterContrato;
    private TapSaldoMCFilter mTapSaldoMCFilter;

    private SpinnerArrayAdapter<TapComboRelSaldo> mMasterContratoSpinnerAdapter;;
    private Spinner mMasterContratoSpinner;

    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;

    private TextView mLoadingBandeiraSpinnerTextView;

    //Picker Data Start
    private TextView mDataStartTextView;
    private LinearLayout mDataStartLinearLayout;
    private DatePickerDialog.OnDateSetListener mDataStartDateSetListener;
    private DatePickerDialog mDataStartdatePickerDialog;

    //Picker Data End
    private TextView mDataEndTextView;
    private LinearLayout mDataEndLinearLayout;
    private DatePickerDialog.OnDateSetListener mDataEndDateSetListener;
    private DatePickerDialog mDataEnddatePickerDialog;

    private List<TapComboRelSaldo> mTapComboRelSaldo;


    private boolean mIsVisibleMenu;
    private int mScreenOrientation;

    TapConnection tapConnection;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saldo_mc_filter);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {

            mTapSaldoMCFilter = (TapSaldoMCFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {

            mTapSaldoMCFilter = (TapSaldoMCFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mMasterContratoSpinner = findViewById(R.id.org_spinner);
        mProgressLinearLayout = findViewById(R.id.loading_pedido_filter);
        mBaseLinearLayout = findViewById(R.id.base_pedido_filter);
        mLoadingBandeiraSpinnerTextView = findViewById(R.id.loading_bandeira_spinner_text_view);

        mProgressLinearLayout.setVisibility(View.VISIBLE);
        mBaseLinearLayout.setVisibility(View.GONE);

        mParameters = new String[2];

        // It's needed to lock screen rotation during async task orientation, so:
        // 1. store the actual orientation;
        // 2. lock rotation;
        // 3. release rotation feature.
        mScreenOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        //Setup Data Picker Start
        mDataStartTextView = findViewById(R.id.data_start_text_view);
        mDataStartLinearLayout = findViewById(R.id.data_start_linear_layout);

        // Data Picker End
        mDataEndTextView = findViewById(R.id.data_end_text_view);
        mDataEndLinearLayout = findViewById(R.id.data_end_linear_layout);

        loadSpinnerMC();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mTapSaldoMCFilter);
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
        TapComboRelSaldo tapComboRelSaldo = null;
        String dateStart = null;
        String dateEnd = null;


        boolean isThereAnyOrganizacaoSelected = mMasterContratoSpinnerAdapter.isThereAnyItemSelected(
                mMasterContratoSpinner);
        if (isThereAnyOrganizacaoSelected) {
            tapComboRelSaldo = (TapComboRelSaldo) mMasterContratoSpinner.getSelectedItem();
        }


        if (!mDataStartTextView.getText().toString().equals(getString(R.string.data_start_hint))) {
            try {
                dateStart = mDataStartTextView.getText().toString();
            } catch (Exception ex) {
                dateStart = null;
            }
        }

        if (!mDataEndTextView.getText().toString().equals(getString(R.string.data_end_hint))) {
            try {
                dateEnd = mDataEndTextView.getText().toString();
            } catch (Exception ex) {
                dateEnd = null;
            }
        }

        mTapSaldoMCFilter = new TapSaldoMCFilter(tapComboRelSaldo, dateStart, dateEnd);
    }

    private void bundleEmptyFilter() {
        mTapSaldoMCFilter = new TapSaldoMCFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mTapSaldoMCFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupMCSpinner(Object[] objects) {
        mMasterContrato = objects;


        mMasterContratoSpinnerAdapter = new SpinnerArrayAdapter<TapComboRelSaldo>(
                this, objects, true) {
            @Override
            public String getItemDescription(TapComboRelSaldo item) {
                return item.getNome();
            }
        };

        mMasterContratoSpinner.setAdapter(mMasterContratoSpinnerAdapter);


        if (mTapSaldoMCFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mTapSaldoMCFilter.getCodMC());
            if (indexOf > 0) {
                mMasterContratoSpinner.setSelection(indexOf);
            }
        }

        mMasterContratoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
                createStartDataPicker(false, true, year, month, day);

            } catch (ParseException e) {
                LogUser.log(Config.TAG, e.toString());
            }
        };

        createStartDataPicker(true, true, 0, 0, 0);

        //Date Picker End
        mDataEndLinearLayout.setOnClickListener(view -> {
            mDataEnddatePickerDialog.show();
        });

        mDataEndDateSetListener = (DatePicker datePicker, int year, int month, int day) -> {
            try {
                String date = FormatUtils.toDateCreateDatePicker(year, month, day);
                mDataEndTextView.setText(date);
                createStartDataPicker(false, false, year, month, day);

            } catch (ParseException e) {
                LogUser.log(Config.TAG, e.toString());
            }
        };

        createStartDataPicker(true, false, 0, 0, 0);

        if (mTapSaldoMCFilter != null) {
            if (!TextUtils.isNullOrEmpty(mTapSaldoMCFilter.getDateStart())) {
                mDataStartTextView.setText(mTapSaldoMCFilter.getDateStart());
            }

            if (!TextUtils.isNullOrEmpty(mTapSaldoMCFilter.getDateEnd())) {
                mDataEndTextView.setText(mTapSaldoMCFilter.getDateEnd());
            }
        }
    }

    private void createStartDataPicker(boolean isDataNow, boolean isStart, int year, int month, int day) {

        if (isDataNow) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        if (isStart) {
            mDataStartdatePickerDialog = new DatePickerDialog(
                    this, mDataStartDateSetListener, year, month, day);
        } else {
            mDataEnddatePickerDialog = new DatePickerDialog(
                    this, mDataEndDateSetListener, year, month, day);
        }
    }

    private void loadSpinnerMC() {
        tapConnection = new TapConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgress(false);
                switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                    case TAP_MASTER_CONTRATO:

                        Gson gson = new Gson();
                        mTapComboRelSaldo = gson.fromJson(response, new TypeToken<List<TapComboRelSaldo>>() {}.getType());

                        setupMCSpinner(SpinnerArrayAdapter.makeObjectsWithHint(mTapComboRelSaldo.toArray(),
                                getString(R.string.select_master_contrato)));
                        mIsVisibleMenu = true;
                        invalidateOptionsMenu();

                        setupDataPicker();

                        break;
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                showProgress(false);

                switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                    case TAP_MASTER_CONTRATO:
                        break;
                }
            }
        });

        showProgress(true);

        Current current = Current.getInstance(this);
        tapConnection.getMC(current.getUnidadeNegocio().getCodigo());
    }

    private void showProgress(boolean show) {
        mProgressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mBaseLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);

    }
}
