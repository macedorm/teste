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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncConsultaGenericaFilter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.data.ConsultaGenericaFilter;
import br.com.jjconsulting.mobile.dansales.database.MultiValuesDao;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.TapComboRelSaldo;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class ConsultaGenericaFilterActivity extends AppCompatActivity
        implements AsyncConsultaGenericaFilter.OnAsyncResponse, View.OnClickListener {

    public static final String FILTER_RESULT_DATA_KEY = "filter_result";
    public static final int FILTER_CLIENTE_REQUEST_CODE = 1;
    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";

    private AsyncConsultaGenericaFilter mAsyncTaskFilter;
    private Object[] mTipoCadastro;
    private Object[] mStatus;

    private ConsultaGenericaFilter mConsultaGenricaFilter;
    private String mParameters[];

    private AndroidTreeView mHierarquiaComercialTreeView;
    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;

    private SpinnerArrayAdapter<MultiValues> mTipoCadastroSpinnerAdapter;;
    private Spinner mTipoCadastroSpinner;


    private SpinnerArrayAdapter<MultiValues> mStatusSpinnerAdapter;;
    private Spinner mStatusSpinner;

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

    private boolean mIsVisibleMenu;
    private int mScreenOrientation;

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAsyncTaskFilter.cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_generica_filter);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mConsultaGenricaFilter = (ConsultaGenericaFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mConsultaGenricaFilter = (ConsultaGenericaFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mProgressLinearLayout = findViewById(R.id.loading_pedido_filter);
        mBaseLinearLayout = findViewById(R.id.base_pedido_filter);

        mTipoCadastroSpinner = findViewById(R.id.cg_tipo_cadastro_spinner);
        mStatusSpinner = findViewById(R.id.cg_status_spinner);

        mProgressLinearLayout.setVisibility(View.VISIBLE);
        mBaseLinearLayout.setVisibility(View.GONE);

        //Setup Data Picker Start
        mDataStartTextView = findViewById(R.id.data_start_text_view);
        mDataStartLinearLayout = findViewById(R.id.data_start_linear_layout);

        // Data Picker End
        mDataEndTextView = findViewById(R.id.data_end_text_view);
        mDataEndLinearLayout = findViewById(R.id.data_end_linear_layout);

        // It's needed to lock screen rotation during async task orientation, so:
        // 1. store the actual orientation;
        // 2. lock rotation;
        // 3. release rotation feature.
        mScreenOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        mAsyncTaskFilter = new AsyncConsultaGenericaFilter(this, true,
                this);

        mParameters = new String[1];
        mParameters[0] = String.valueOf(0);
        mAsyncTaskFilter.execute(mParameters);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mConsultaGenricaFilter);
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
        switch (type) {
            case AsyncConsultaGenericaFilter.TIPO_CADASTRO:
                setupTipoCadastro(objects);
                break;
            case AsyncConsultaGenericaFilter.STATUS:
                setupStatus(objects);
                break;
            case AsyncConsultaGenericaFilter.HIERARQUIA:
                setupHierarquiaComercialTreeView(objects[0]);
                break;
        }

        if (isStartLoading && type < AsyncConsultaGenericaFilter.HIERARQUIA) {
            type++;
            mParameters[0] = String.valueOf(type);
            mAsyncTaskFilter = new AsyncConsultaGenericaFilter(this, true, this);
            mAsyncTaskFilter.execute(mParameters);
        } else if (isStartLoading) {
            setupDataPicker();
            mIsVisibleMenu = true;
            invalidateOptionsMenu();
            mProgressLinearLayout.setVisibility(View.GONE);
            mBaseLinearLayout.setVisibility(View.VISIBLE);
            setRequestedOrientation(mScreenOrientation);
        }




        mIsVisibleMenu = true;
        invalidateOptionsMenu();
        mProgressLinearLayout.setVisibility(View.GONE);
        mBaseLinearLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(mScreenOrientation);
    }

    private void bundleFilter() {
        String dateStart = null;
        String dateEnd = null;
        int status = 0;
        int tipoCadastro = 0;
        List<Usuario> hierarquiaComercial = null;

        if (mHierarquiaComercialTreeView != null){
            hierarquiaComercial = mHierarquiaComercialTreeView.getSelectedValues(
                    Usuario.class);
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


        boolean isThereAnyStatusSelected = mStatusSpinnerAdapter.isThereAnyItemSelected(
                mStatusSpinner);
        if (isThereAnyStatusSelected) {
            status = ((MultiValues) mStatusSpinner.getSelectedItem()).getValCod();
        }


        boolean isThereAnyPlanoCampoSelected = mTipoCadastroSpinnerAdapter.isThereAnyItemSelected(
                mTipoCadastroSpinner);
        if (isThereAnyPlanoCampoSelected) {
            tipoCadastro = ((MultiValues) mTipoCadastroSpinner.getSelectedItem()).getValCod();
        }


        mConsultaGenricaFilter = new ConsultaGenericaFilter(status,tipoCadastro,  dateStart, dateEnd, hierarquiaComercial);
    }

    private void bundleEmptyFilter() {
        mConsultaGenricaFilter = new ConsultaGenericaFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mConsultaGenricaFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }



    private void setupHierarquiaComercialTreeView(Object objects) {
        TreeNode root = TreeNode.root();

        mHierarquiaComercialTreeView = new AndroidTreeView(this, root);
        mHierarquiaComercialTreeView.setDefaultViewHolder(HierarquiaComercialViewHolder.class);
        mHierarquiaComercialTreeView.setSelectionModeEnabled(true);
        mHierarquiaComercialTreeView.setDefaultAnimation(false);
        mHierarquiaComercialTreeView.setDefaultContainerStyle(R.style.TreeNodeStyle,
                false);

        Tree<Usuario> hierarquiaComercial = (Tree<Usuario>) objects;
        if (hierarquiaComercial.size() > 100) {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial, 1, 0,
                    new TreeNodePageConfiguration(TreeNodeUtils.DEFAULT_PAGE_SIZE,
                            TreeNodeUtils.DEFAULT_LEVEL_USAGE, null));
        } else {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial);
        }

        if (mConsultaGenricaFilter != null) {
            TreeNodeUtils.selectUsuariosInTreeNode(root, mConsultaGenricaFilter.getHierarquiaComercial());
        }

        mHierarquiaComercialTreeView.setRoot(root);

        ViewGroup treeViewRootViewGroup = findViewById(R.id.tree_view_root_view_group);
        treeViewRootViewGroup.addView(mHierarquiaComercialTreeView.getView());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILTER_CLIENTE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    setSearchClientes(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.produto_search_image_view:
                Intent it = new Intent(this, PickClienteFilterActivity.class);
                startActivityForResult(it, FILTER_CLIENTE_REQUEST_CODE);
                break;
        }
    }

    private void setSearchClientes(Intent data) {
        Cliente cliente = null;
        try {
            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(PickClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                cliente = (Cliente) data.getSerializableExtra(
                        PickClienteFilterActivity.FILTER_RESULT_DATA_KEY);
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
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

        if (mConsultaGenricaFilter != null) {
            if (!TextUtils.isNullOrEmpty(mConsultaGenricaFilter.getDateStart())) {
                mDataStartTextView.setText(mConsultaGenricaFilter.getDateStart());
            }

            if (!TextUtils.isNullOrEmpty(mConsultaGenricaFilter.getDateEnd())) {
                mDataEndTextView.setText(mConsultaGenricaFilter.getDateEnd());
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


    private void setupTipoCadastro(Object[] objects) {
        mTipoCadastro = objects;

        mTipoCadastroSpinnerAdapter = new SpinnerArrayAdapter<MultiValues>(
                this, objects, true) {
            @Override
            public String getItemDescription(MultiValues item) {
                return item.getDesc();
            }

        };

        mTipoCadastroSpinner.setAdapter(mTipoCadastroSpinnerAdapter);

        if (mConsultaGenricaFilter != null) {
            if(mConsultaGenricaFilter.getTipoCadastro() > 0){
                for (int i = 0; i < objects.length; i++) {
                    if(i > 0){
                        MultiValues multiValues = (MultiValues) objects[i];
                        if(multiValues.getValCod() == mConsultaGenricaFilter.getTipoCadastro()){
                            mTipoCadastroSpinner.setSelection(i);
                        }
                    }
                }
            }
        }

        mTipoCadastroSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


    private void setupStatus(Object[] objects) {
        mStatus = objects;

        mStatusSpinnerAdapter = new SpinnerArrayAdapter<MultiValues>(
                this, objects, true) {
            @Override
            public String getItemDescription(MultiValues item) {
                return item.getDesc();
            }

        };

        mStatusSpinner.setAdapter(mStatusSpinnerAdapter);

        if (mConsultaGenricaFilter != null) {
            if(mConsultaGenricaFilter.getStatus() > 0){
                for (int i = 0; i < objects.length; i++) {
                    if(i > 0){
                        MultiValues multiValues = (MultiValues) objects[i];
                        if(multiValues.getValCod() == mConsultaGenricaFilter.getStatus()){
                            mStatusSpinner.setSelection(i);
                        }
                    }
                }
            }
        }

        mStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

}
