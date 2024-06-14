package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.TapRelSaldoMCAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.data.TapSaldoMCFilter;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.model.TapConnectionType;
import br.com.jjconsulting.mobile.dansales.model.TapRelSaldo;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionTap;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.UnidadeNegocioUtils;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapRelatorioSaldoMCActivity extends BaseActivity {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;
    private static final String ARG_TYPE_ACTION = "type_action";

    private List<TapRelSaldo> mTapRelSaldoList;
    private TapRelSaldoMCAdapter mTapRelSaldoAdapter;

    private Menu mMenu;
    private RecyclerView mETapRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private LinearLayout mProgressLinearLayout;
    private RelativeLayout mBaseRelativeLayout;
    private TextView mMessageErrorTextView;

    private String mNome;

    private TapConnection tapConnection;

    private TapActionType tapActionType;

    private Toast toast;

    private boolean isSelectAll;

    private TapSaldoMCFilter mSaldoMCFilter;

    /**
     * Use it to edit an item.
     */
    public static Intent newIntent(Context context, TapActionType tapActionType) {
        Intent intent = new Intent(context, TapRelatorioSaldoMCActivity.class);
        intent.putExtra(ARG_TYPE_ACTION, tapActionType.getValue());
        return intent;
    }

    public static TapRelatorioSaldoMCActivity newInstance() {
        return new TapRelatorioSaldoMCActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CurrentActionTap.getInstance().isUpdateListTap()) {
            CurrentActionTap.getInstance().setUpdateListTap(false);
            findETap();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rel_saldo_mc_etap);

        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mSaldoMCFilter = (TapSaldoMCFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        tapActionType = TapActionType.getTapActionType(getIntent().getIntExtra(ARG_TYPE_ACTION, 1));

        mProgressLinearLayout = findViewById(R.id.loading_linear_layout);
        mBaseRelativeLayout = findViewById(R.id.base_relative_layout);
        mMessageErrorTextView = findViewById(R.id.message_error_text_view);
        mETapRecyclerView = findViewById(R.id.rel_saldo_mc_etap_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(view -> {
            openFilter();
        });

        findETap();

        mETapRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mETapRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mETapRecyclerView.addItemDecoration(divider);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSaldoMCFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mSaldoMCFilter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.filter_menu, menu);
        getMenuInflater().inflate(R.menu.cancel_filter_menu, menu);

        setFilterMenuIcon();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                openFilter();
                return true;
            case R.id.menu_cancel_filter:
                mSaldoMCFilter = new TapSaldoMCFilter();
                findETap();
                setFilterMenuIcon();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILTER_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    findETap(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void findETap() {
        findETap(null);
    }

    private void findETap(Intent data) {
        try {
            mTapRelSaldoAdapter = new TapRelSaldoMCAdapter(this, new ArrayList<>());
            mETapRecyclerView.setAdapter(mTapRelSaldoAdapter);

            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mSaldoMCFilter = (TapSaldoMCFilter) data.getSerializableExtra(
                        TapListFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mETapRecyclerView.setVisibility(View.VISIBLE);
            }

            mTapRelSaldoAdapter.resetData();
            loadETap();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void loadETap() {
        tapConnection = new TapConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgress(false);
                switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                    case TAP_REL_SALDO_MC:
                        mTapRelSaldoList = gson.fromJson(response, new TypeToken<List<TapRelSaldo>>() {}.getType());

                        mTapRelSaldoAdapter = new TapRelSaldoMCAdapter(getBaseContext(), mTapRelSaldoList);
                        mETapRecyclerView.setAdapter(mTapRelSaldoAdapter);

                        if (mTapRelSaldoList.size() == 0) {
                            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
                            mETapRecyclerView.setVisibility(View.GONE);
                        }
                        break;
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                showProgress(false);

                switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                    case TAP_REL_SALDO_MC:
                        dialogsDefault.showDialogMessage(getString(R.string.message_etap_listl_erro), dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogMessage() {
                            @Override
                            public void onClick() {
                                finish();
                            }
                        });
                        break;
                }
            }
        });

        showProgress(true);

        Current current = Current.getInstance(this);
        String[] empresaFilial = UnidadeNegocioUtils.getCodigoEmpresaFilial(
                current.getUnidadeNegocio().getCodigo());

        String codMC = "-1";
        String dateStart = "";
        String dataEnd = "";

        if(mSaldoMCFilter != null && mSaldoMCFilter.getCodMC() != null){
            codMC = mSaldoMCFilter.getCodMC().getCod();
        }

        if(mSaldoMCFilter != null && !TextUtils.isNullOrEmpty(mSaldoMCFilter.getDateStart())){
            dateStart = mSaldoMCFilter.getDateStart();
        } else {
            try{
                Calendar date = Calendar.getInstance();
                date.set(Calendar.DAY_OF_MONTH, 1);

                dateStart =  FormatUtils.toDateTimeText(FormatUtils.toDefaultDateFormat(date.getTime()));
            }catch (Exception ex){
                LogUser.log(ex.toString());
            }
        }

        if(mSaldoMCFilter != null &&  !TextUtils.isNullOrEmpty(mSaldoMCFilter.getDateEnd())){
            dataEnd = mSaldoMCFilter.getDateEnd();
        } else {
            try {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
                dataEnd = FormatUtils.toDateTimeText(FormatUtils.toDefaultDateFormat(date.getTime()));
            }catch (Exception ex) {
                LogUser.log(ex.toString());
            }
        }


        tapConnection.getRelSaldoMC(current.getUnidadeNegocio().getCodigo(), dateStart, dataEnd, codMC);
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mSaldoMCFilter == null || mSaldoMCFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mSaldoMCFilter == null || mSaldoMCFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }

    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, TapRelatorioSaldoMCFilterActivity.class);
        if (mSaldoMCFilter != null) {
            filterIntent.putExtra(TapRelatorioSaldoMCFilterActivity.FILTER_RESULT_DATA_KEY,
                    mSaldoMCFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void showProgress(boolean show) {
        mProgressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mMessageErrorTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        mBaseRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);

        mListEmptyLinearLayout.setVisibility(View.GONE);
        mETapRecyclerView.setVisibility(View.VISIBLE);
    }
}
