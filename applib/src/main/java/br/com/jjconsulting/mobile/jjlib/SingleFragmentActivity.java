package br.com.jjconsulting.mobile.jjlib;

import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;

import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public abstract class SingleFragmentActivity extends SuperActivity {

    protected static String KEY_DATA_PAR = "data_par";
    protected static String KEY_DATA_SUB_PAR = "data_sub_par";
    protected static String KEY_CALENDAR_PAR = "data_calendar_par";
    protected static String KEY_DATA_PAR_BOOLEAN = "data_par_boolean";


    protected abstract Fragment createFragment();

    private String mData;
    private String mDataSub;
    private String mCurrentDate;
    private boolean mDataBoolean;

    /**
     * Returns a value indicating if the Activity must handle up/back navigation.
     * <br>
     * The default value is true - override only if you don't want to enable it, returning false.
     */
    protected boolean enableUpNavigation() {
        return true;
    }

    /**
     * You should override it and return true when the current activity has more than one parent.
     */
    protected boolean useOnBackPressedInUpNavigation() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        try {

            if (getIntent().getExtras() != null && getIntent().getExtras().getString(KEY_DATA_PAR) != null) {
                mData = getIntent().getExtras().getString(KEY_DATA_PAR);
            }


            if (getIntent().getExtras() != null && getIntent().getExtras().getString(KEY_DATA_SUB_PAR) != null) {
                mDataSub = getIntent().getExtras().getString(KEY_DATA_SUB_PAR);
            }

            if (getIntent().getExtras() != null && getIntent().getExtras().getString(KEY_CALENDAR_PAR) != null) {
                mCurrentDate = getIntent().getExtras().getString(KEY_CALENDAR_PAR);
            }

            if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean(KEY_DATA_PAR_BOOLEAN)) {
                mDataBoolean = getIntent().getExtras().getBoolean(KEY_DATA_PAR_BOOLEAN);
            }

            getSupportActionBar().setDisplayHomeAsUpEnabled(enableUpNavigation());

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);

            if (fragment == null) {
                fragment = createFragment();
                fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
            }

        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (useOnBackPressedInUpNavigation()) {
                    // I'm using onBackPressed cause we have a few activities with more
                    // than one parent activity (no parent activity), so using
                    // navigateUpFromSameTask doesn't work
                    onBackPressed();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideHomeUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        invalidateOptionsMenu();
    }

    public void showHomeUpButton() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        invalidateOptionsMenu();
    }

    public String getData() {
        return mData;
    }

    public void setData(String mData) {
        this.mData = mData;
    }

    public String getDataSub() {
        return mDataSub;
    }

    public void setDataSub(String mDataSub) {
        this.mDataSub = mDataSub;
    }

    public String getCurrentDate() {
        return mCurrentDate;
    }

    public void setCurrentDate(String mCurrentDate) {
        this.mCurrentDate = mCurrentDate;
    }

    public boolean isDataBoolean() {
        return mDataBoolean;
    }

    public void setDataBoolean(boolean mDataBoolean) {
        this.mDataBoolean = mDataBoolean;
    }
}
