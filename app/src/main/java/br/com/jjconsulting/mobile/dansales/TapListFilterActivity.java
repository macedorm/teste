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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskListETapFilter;
import br.com.jjconsulting.mobile.dansales.data.TapFilter;
import br.com.jjconsulting.mobile.dansales.model.TapStatus;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class TapListFilterActivity extends AppCompatActivity
        implements AsyncTaskListETapFilter.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";

    public final static int DIF_DATA_DEFAULT = -3;

    private AsyncTaskListETapFilter mAsyncTaskFilter;
    private String mParameters[];
    private TapFilter mTapFilter;

    private SpinnerArrayAdapter<TapStatus> mStatusSpinnerAdapter;
    private Spinner mStatusSpinner;

    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;
    private TextView mDataStartTextView;
    private LinearLayout mDataStartLinearLayout;
    private DatePickerDialog.OnDateSetListener mDataStartDateSetListener;
    private DatePickerDialog mDataStartdatePickerDialog;

    //Picker Data End
    private TextView mDataEndTextView;
    private LinearLayout mDataEndLinearLayout;
    private DatePickerDialog.OnDateSetListener mDataEndDateSetListener;
    private DatePickerDialog mDataEnddatePickerDialog;

    private CheckBox mPendingApprovalCheckBox;

    private boolean mIsVisibleMenu;
    private int mScreenOrientation;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etap_filter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mTapFilter = (TapFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mTapFilter = (TapFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mStatusSpinner = findViewById(R.id.status_spinner);
        mProgressLinearLayout = findViewById(R.id.loading_cliente_filter);
        mBaseLinearLayout = findViewById(R.id.base_cliente_filter);
        mPendingApprovalCheckBox  = findViewById(R.id.pending_approval_check_box);

        //Setup Data Picker Start
        mDataStartTextView = findViewById(R.id.data_start_text_view);
        mDataStartLinearLayout = findViewById(R.id.data_start_linear_layout);

        // Data Picker End
        mDataEndTextView = findViewById(R.id.data_end_text_view);
        mDataEndLinearLayout = findViewById(R.id.data_end_linear_layout);

        mProgressLinearLayout.setVisibility(View.VISIBLE);
        mBaseLinearLayout.setVisibility(View.GONE);

        mParameters = new String[2];

        // It's needed to lock screen rotation during async task orientation, so:
        // 1. store the actual orientation;
        // 2. lock rotation;
        // 3. release rotation feature.
        mScreenOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        mAsyncTaskFilter = new AsyncTaskListETapFilter(this, true, this);
        mAsyncTaskFilter.execute(String.valueOf(AsyncTaskListETapFilter.STATUS));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mTapFilter);
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

    @Override
    public void processFinish(int type, boolean isStartLoading, Object[] objects) {
        String codigoOrganizacao = "";

        setupStatusSpinner(objects);
        setupDataPicker();
        setupCheckBox();
        mIsVisibleMenu = true;
        invalidateOptionsMenu();
        mProgressLinearLayout.setVisibility(View.GONE);
        mBaseLinearLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(mScreenOrientation);
    }

    private void bundleFilter() {
        TapStatus status = null;
        Date dateStart = null;
        Date dateEnd = null;

        boolean isThereAnyStatusSelected = mStatusSpinnerAdapter.isThereAnyItemSelected(
                mStatusSpinner);
        if (isThereAnyStatusSelected) {
            status = (TapStatus) mStatusSpinner.getSelectedItem();
        }


        if (!mDataStartTextView.equals(getString(R.string.data_start_hint))) {
            try {
                dateStart = FormatUtils.toDate(FormatUtils.toConvertDate(mDataStartTextView.getText().toString(), "dd/MM/yyyy", "yyyy-MM-dd HH:mm"));
            } catch (Exception ex) {
                dateStart = null;
            }
        }

        if (!mDataEndTextView.equals(getString(R.string.data_end_hint))) {
            try {
                dateEnd = FormatUtils.toDate(FormatUtils.toConvertDate(mDataEndTextView.getText().toString() + " " + getString(R.string.hour_end), "dd/MM/yyyy HH:mm", "yyyy-MM-dd HH:mm"));
            } catch (Exception ex) {
                dateEnd = null;
            }
        }

        mTapFilter = new TapFilter(status, dateStart, dateEnd, mPendingApprovalCheckBox.isChecked());
    }

    private void bundleEmptyFilter() {
        mTapFilter = new TapFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mTapFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupCheckBox(){
        if (mTapFilter != null) {
            mPendingApprovalCheckBox.setChecked(mTapFilter.isPendingApproval());
        }

    }

    private void setupStatusSpinner(Object[] objects) {
        mStatusSpinnerAdapter = new SpinnerArrayAdapter<TapStatus>(
                this, objects, true) {
            @Override
            public String getItemDescription(TapStatus item) {
                return item.getNome();
            }
        };

        mStatusSpinner.setAdapter(mStatusSpinnerAdapter);

        if (mTapFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mTapFilter.getStatus());
            if (indexOf > 0) {
                mStatusSpinner.setSelection(indexOf);
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

        if (mTapFilter != null) {
            if (mTapFilter.getDateStart() != null) {
                String date = FormatUtils.toDefaultDateFormat(mTapFilter.getDateStart());
                try {
                    mDataStartTextView.setText(FormatUtils.toDateTimeText(date));
                } catch (ParseException e) {
                    LogUser.log(Config.TAG, e.toString());
                }
            } else {
                setDefaultDateStart();
            }

            if (mTapFilter.getDateEnd() != null) {
                String date = FormatUtils.toDefaultDateFormat(mTapFilter.getDateEnd());
                try {
                    mDataEndTextView.setText(FormatUtils.toDateTimeText(date));
                } catch (ParseException e) {
                    LogUser.log(Config.TAG, e.toString());
                }
            } else {
                setDefaultDateEnd();
            }
        } else {
            setDefaultDateStart();
            setDefaultDateEnd();
        }
    }

    private void setDefaultDateStart() {
        try {
            mDataStartTextView.setText(
                    FormatUtils.toTextToDatePT(
                            FormatUtils.getDateTimeNow(0, DIF_DATA_DEFAULT, 0)));
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());

        }
    }

    private void setDefaultDateEnd() {
        try {
            mDataEndTextView.setText(
                    FormatUtils.toTextToDatePT(
                            FormatUtils.getDateTimeNow(0, 0, 0)));
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());

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
}
