package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.data.RotasFilter;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RotaGuiadaFilterActivity extends AppCompatActivity {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";

    private RotasFilter mRotaFilter;

    private SpinnerArrayAdapter<Integer> mStatusSpinnerAdapter;

    private Spinner mStatusSpinner;

    private AndroidTreeView mHierarquiaComercialTreeView;

    private LinearLayout mHierarquiaComercialLinearLayout;
    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;
    private LinearLayout mDataLinearLayout;

    private TextView mDateTextView;

    private DatePickerDialog mDatePickerDialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private Date mDate;

    private boolean isPromotor;

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rota_guiada_filter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mRotaFilter = (RotasFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mRotaFilter = (RotasFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mStatusSpinner = findViewById(R.id.status_spinner);
        mHierarquiaComercialLinearLayout = findViewById(R.id.hierarquia_comercial_linear_layout);

        mProgressLinearLayout = findViewById(R.id.loading_rota_guiada_filter);
        mBaseLinearLayout = findViewById(R.id.base_rota_guiada_filter);

        mProgressLinearLayout.setVisibility(View.GONE);
        mBaseLinearLayout.setVisibility(View.VISIBLE);

        int[] statuses = RotaGuiadaUtils.getStatuses();
        Object objects[] = SpinnerArrayAdapter.makeObjectsWithHint(statuses,
                getString(R.string.select_status_rota_guiada));


        mDataLinearLayout = findViewById(R.id.data_linear_layout);
        mDateTextView =   findViewById(R.id.rg_data_text_view);

        isPromotor = UsuarioUtils.isPromotor(Current.getInstance(this).getUsuario().getCodigoFuncao());

        addListener();
        createDataPicker(new Date());
        setupStatusSpinner(objects);

        if(!isPromotor){
            setupHierarquiaComercialTreeView();
        }

        visible();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mRotaFilter);
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



    private void addListener(){
        mDateSetListener = (DatePicker datePicker, int year, int month, int day) -> {
            try {
                mDate = FormatUtils.toCreateDatePicker(year, month, day);
                mDateTextView.setText(FormatUtils.toDefaultDateFormat(this, mDate));
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        };

        mDataLinearLayout.setOnClickListener((v)-> {
            mDatePickerDialog.show();
        });
    }

    private void visible(){
        if(isPromotor){
            mHierarquiaComercialLinearLayout.setVisibility(View.GONE);
        }
    }


    private void bundleFilter() {
        Integer status = null;

        if(mStatusSpinnerAdapter != null) {
            boolean isThereAnyStatusSelected = mStatusSpinnerAdapter.isThereAnyItemSelected(
                    mStatusSpinner);
            if (isThereAnyStatusSelected) {
                status = (Integer) mStatusSpinner.getSelectedItem();
            }
        }

        List<Usuario> hierarquiaComercial = null;

        if (mHierarquiaComercialTreeView != null){
             hierarquiaComercial = mHierarquiaComercialTreeView.getSelectedValues(
                    Usuario.class);
        }

        mRotaFilter = new RotasFilter(status, mDate, hierarquiaComercial);
    }

    private void bundleEmptyFilter() {
        mRotaFilter = new RotasFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mRotaFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupStatusSpinner(Object[] objects) {
        mStatusSpinnerAdapter = new SpinnerArrayAdapter<Integer>(
                this, objects, true) {
            @Override
            public String getItemDescription(Integer item) {
                    return getString(RotaGuiadaUtils.getStatusStringResourceId(item));
            }
        };

        mStatusSpinner.setAdapter(mStatusSpinnerAdapter);

        if (mRotaFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mRotaFilter.getStatus());
            if (indexOf > 0) {
                mStatusSpinner.setSelection(indexOf);
            }
        }
    }

    private void createDataPicker(Date date) {
        Calendar calendar = Calendar.getInstance();

        if (date != null) {
            try {
                calendar.setTime(date);
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePickerDialog = new DatePickerDialog(
                this, mDateSetListener, year, month, day);


        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Calendar minDate = Calendar.getInstance();
                minDate.setTime(new Date());
                minDate.set(Calendar.HOUR_OF_DAY, minDate.getMinimum(Calendar.HOUR_OF_DAY));
                minDate.set(Calendar.MINUTE, minDate.getMinimum(Calendar.MINUTE));
                minDate.set(Calendar.SECOND, minDate.getMinimum(Calendar.SECOND));
                minDate.set(Calendar.MILLISECOND, minDate.getMinimum(Calendar.MILLISECOND));
                minDate.add(Calendar.DAY_OF_MONTH, -30);


                final Calendar maxDate = Calendar.getInstance();
                maxDate.setTime(new Date());
                maxDate.set(Calendar.HOUR_OF_DAY, minDate.getMinimum(Calendar.HOUR_OF_DAY));
                maxDate.set(Calendar.MINUTE, minDate.getMinimum(Calendar.MINUTE));
                maxDate.set(Calendar.SECOND, minDate.getMinimum(Calendar.SECOND));
                maxDate.set(Calendar.MILLISECOND, minDate.getMinimum(Calendar.MILLISECOND));
                maxDate.add(Calendar.DAY_OF_MONTH, 30);

                mDatePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                mDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            } catch (Exception ex) {
                LogUser.log(Config.TAG, ex.toString());
            }
        } else {
            mDatePickerDialog.getDatePicker().setMinDate(FormatUtils.getDateTimeNow(0, -1, 0).getTime());
        }


        mDatePickerDialog.getDatePicker().setMaxDate(FormatUtils.getDateTimeNow(0, 1, 0).getTime());

        if (mRotaFilter != null) {
            mDate = mRotaFilter.getDate();

            if(mDate != null){
                mDateTextView.setText(FormatUtils.toDefaultDateFormat(this, mDate));
             }
        }
    }

    private void setupHierarquiaComercialTreeView() {

        Usuario usuario = Current.getInstance(this).getUsuario();
        UnidadeNegocio unidadeNegocio = Current.getInstance(this).getUnidadeNegocio();

        UsuarioDao usuarioDao = new UsuarioDao(this);
        Tree<Usuario> hierarquiaComercial = usuarioDao.getHierarquiaComercial(usuario,
                unidadeNegocio.getCodigo());

        TreeNode root = TreeNode.root();

        mHierarquiaComercialTreeView = new AndroidTreeView(this, root);
        mHierarquiaComercialTreeView.setDefaultViewHolder(HierarquiaComercialViewHolder.class);
        mHierarquiaComercialTreeView.setSelectionModeEnabled(true);
        mHierarquiaComercialTreeView.setDefaultAnimation(false);
        mHierarquiaComercialTreeView.setDefaultContainerStyle(R.style.TreeNodeStyle,
                false);

        if (hierarquiaComercial.size() > 100) {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial, 1, 0,
                    new TreeNodePageConfiguration(TreeNodeUtils.DEFAULT_PAGE_SIZE,
                            TreeNodeUtils.DEFAULT_LEVEL_USAGE, null));
        } else {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial);
        }

        if (mRotaFilter != null) {
            TreeNodeUtils.selectUsuariosInTreeNode(root, mRotaFilter.getHierarquiaComercial());
        }

        mHierarquiaComercialTreeView.setRoot(root);

        ViewGroup treeViewRootViewGroup = findViewById(R.id.tree_view_root_view_group);
        treeViewRootViewGroup.addView(mHierarquiaComercialTreeView.getView());
    }

}
