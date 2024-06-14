package br.com.jjconsulting.mobile.dansales;

import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.model.Login;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.database.WebSalesDatabaseHelper;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataDao;
import br.com.jjconsulting.mobile.jjlib.syncData.SyncDataManager;
import br.com.jjconsulting.mobile.jjlib.syncData.model.ConfigUserSync;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.HardwareUtil;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class AmbienteActivity extends BaseActivity {

    public static final String KEY_LOGIN = "login";

    private boolean mIsLogin;

    private TextView mImeiTextView;
    private TextView mStatusSyncTextView;
    private TextView mVersionTextView;
    private TextView mDatabaseVersionTextView;

    private Spinner mAmbienteSpinner;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_ambiente);

        mIsLogin = getIntent().getExtras().getBoolean(KEY_LOGIN, false);

        mImeiTextView = findViewById(R.id.ambiente_emei_text_view);
        mStatusSyncTextView = findViewById(R.id.ambiente_status_sync_text_view);
        mVersionTextView = findViewById(R.id.ambiente_version_text_view);
        mDatabaseVersionTextView = findViewById(R.id.database_version_text_view);
        mAmbienteSpinner = findViewById(R.id.ambiente_url_spinner);

        String size = "0";
        try {
             size = FormatUtils.convertFileSize(WebSalesDatabaseHelper.getSizeDatabase(this, BuildConfig.DATABASE_NAME));
        } catch (Exception ex) {
            LogUser.log(Config.TAG, size);
        }

        mImeiTextView.setText(HardwareUtil.getDeviceIMEI(this));
        mStatusSyncTextView.setText(getLastDateSync());
        mVersionTextView.setText(String.format("%s  (%d)", BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE));
        mDatabaseVersionTextView.setText(String.format("%s  (%d)", size, BuildConfig.DATABASE_VERSION));

        if (mIsLogin) {
            mStatusSyncTextView.setText(getLastDateSyncAndUser());
        }

        Object[] objects = getResources().getStringArray(R.array.tipo_ambiente_array);
        SpinnerArrayAdapter mSpinnerAdapter = new SpinnerArrayAdapter<String>(this, objects,
                false) {
            @Override
            public String getItemDescription(String item) {
                return item;
            }
        };

        mAmbienteSpinner.setAdapter(mSpinnerAdapter);
        mAmbienteSpinner.setEnabled(false);

        if(getApplicationContext().getPackageName().contains("hml")){
            mAmbienteSpinner.setSelection(0);
        } else if(getApplicationContext().getPackageName().contains("dev")){
            mAmbienteSpinner.setSelection(1);
        } else {
            mAmbienteSpinner.setSelection(2);
        }


    }

    private String getLastDateSyncAndUser() {
        ConfigUserSync configUserSync = new ConfigUserSync(BuildConfig.DATABASE_VERSION, BuildConfig.DATABASE_NAME);
        SyncDataManager syncDataManager =   new SyncDataManager(this, configUserSync);

        String syncInfo = syncDataManager.loadInfoSync(this);
        String syncInfoArray[] = syncInfo.split("\\|");

        try {
            String date = FormatUtils.toDefaultDateAndHourFormat(this,
                    FormatUtils.toDate(syncInfoArray[1]));
            return String.format("%s - %s", syncInfoArray[0], date);
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
            return "-";
        }
    }

    private String getLastDateSync() {
        Login login = userInfo.getUserInfo(this);

        if (login == null) {
            return "-";
        }


        SyncDataDao masterDataDao = new SyncDataDao(this);
        Date dataUltimaSincronizacao = masterDataDao.getLastDateSync(login.getUserId());

        if (dataUltimaSincronizacao == null) {
            return "-";
        }

        return FormatUtils.toDefaultDateAndHourFormat(this, dataUltimaSincronizacao);
    }
}
