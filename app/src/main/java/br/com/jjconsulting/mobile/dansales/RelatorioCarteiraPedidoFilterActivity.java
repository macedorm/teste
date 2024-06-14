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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskCarteiraPedidoFilter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskFilter;
import br.com.jjconsulting.mobile.dansales.data.CarteiraPedidoFilter;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.StatusPedido;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioCarteiraPedidoFilterActivity extends AppCompatActivity
        implements AsyncTaskCarteiraPedidoFilter.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";

    private AsyncTaskCarteiraPedidoFilter mAsyncTaskFilter;
    private String mParameters[];
    private Object[] mOrganizacaos;
    private CarteiraPedidoFilter mPedidoFilter;

    private SpinnerArrayAdapter<Organizacao> mOrganizacaoSpinnerAdapter;
    private SpinnerArrayAdapter<Bandeira> mBandeiraSpinnerAdapter;
    private SpinnerArrayAdapter<StatusPedido> mStatusSpinnerAdapter;
    private Spinner mOrganizacaoSpinner;
    private Spinner mBandeiraSpinner;
    private Spinner mStatusSpinner;

    private AndroidTreeView mHierarquiaComercialTreeView;

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
        setContentView(R.layout.activity_carteira_filter);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {

            mPedidoFilter = (CarteiraPedidoFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {

            mPedidoFilter = (CarteiraPedidoFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mOrganizacaoSpinner = findViewById(R.id.org_spinner);
        mBandeiraSpinner = findViewById(R.id.bandeira_spinner);
        mStatusSpinner = findViewById(R.id.status_spinner);
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

        mAsyncTaskFilter = new AsyncTaskCarteiraPedidoFilter(this, true,
                this);
        mAsyncTaskFilter.execute(String.valueOf(AsyncTaskFilter.ORGANIZACAO));

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mPedidoFilter);
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

        switch (type) {
            case AsyncTaskFilter.ORGANIZACAO:
                setupOrganizacaoSpinner(objects, codigoOrganizacao);
                break;
            case AsyncTaskFilter.STATUS:
                setupStatusSpinner(objects);
                break;
            case AsyncTaskFilter.BANDEIRA:
                setupBandeiraSpinner(objects);
                break;
            case AsyncTaskFilter.HIERARQUIA:
                setupHierarquiaComercialTreeView(objects);
                break;
        }

        if (isStartLoading && type < AsyncTaskFilter.HIERARQUIA) {
            type++;
            mParameters[0] = String.valueOf(type);
            mParameters[1] = codigoOrganizacao;
            mAsyncTaskFilter = new AsyncTaskCarteiraPedidoFilter(this, true,
                    this);
            mAsyncTaskFilter.execute(mParameters);
        } else if (isStartLoading) {
            mIsVisibleMenu = true;
            invalidateOptionsMenu();
            mProgressLinearLayout.setVisibility(View.GONE);
            mBaseLinearLayout.setVisibility(View.VISIBLE);
            setRequestedOrientation(mScreenOrientation);
            setupDataPicker();
        }
    }

    private void bundleFilter() {
        Organizacao organizacao = null;
        Bandeira bandeira = null;
        StatusPedido status = null;
        Date dateStart = null;
        Date dateEnd = null;

        if (mHierarquiaComercialTreeView == null)
            return;

        boolean isThereAnyOrganizacaoSelected = mOrganizacaoSpinnerAdapter.isThereAnyItemSelected(
                mOrganizacaoSpinner);
        if (isThereAnyOrganizacaoSelected) {
            organizacao = (Organizacao) mOrganizacaoSpinner.getSelectedItem();
        }

        boolean isThereAnyBandeiraSelected = mBandeiraSpinnerAdapter.isThereAnyItemSelected(
                mBandeiraSpinner);
        if (isThereAnyBandeiraSelected) {
            bandeira = (Bandeira) mBandeiraSpinner.getSelectedItem();
        }

        boolean isThereAnyStatusSelected = mStatusSpinnerAdapter.isThereAnyItemSelected(
                mStatusSpinner);
        if (isThereAnyStatusSelected) {
            status = (StatusPedido) mStatusSpinner.getSelectedItem();
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

        List<Usuario> hierarquiaComercial = mHierarquiaComercialTreeView.getSelectedValues(
                Usuario.class);

        mPedidoFilter = new CarteiraPedidoFilter(organizacao, bandeira, status, hierarquiaComercial, dateStart, dateEnd);
    }

    private void bundleEmptyFilter() {
        mPedidoFilter = new CarteiraPedidoFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mPedidoFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupOrganizacaoSpinner(Object[] objects, String codigoOrganizacao) {
        mOrganizacaos = objects;

        mOrganizacaoSpinnerAdapter = new SpinnerArrayAdapter<Organizacao>(
                this, objects, true) {
            @Override
            public String getItemDescription(Organizacao item) {
                return item.getNome();
            }
        };

        mOrganizacaoSpinner.setAdapter(mOrganizacaoSpinnerAdapter);

        if (mPedidoFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mPedidoFilter.getOrganizacao());
            if (indexOf > 0) {
                mOrganizacaoSpinner.setSelection(indexOf);
                codigoOrganizacao = ((Organizacao) mOrganizacaos[indexOf]).getCodigo();
            }
        }

        mOrganizacaoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    mParameters[0] = String.valueOf(AsyncTaskFilter.BANDEIRA);
                    mParameters[1] = ((Organizacao) mOrganizacaos[i]).getCodigo();
                    showSpinnerBandeira(false);
                    mAsyncTaskFilter = new AsyncTaskCarteiraPedidoFilter(RelatorioCarteiraPedidoFilterActivity.this,
                            false, RelatorioCarteiraPedidoFilterActivity.this);
                    mAsyncTaskFilter.execute(mParameters);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setupBandeiraSpinner(Object[] objects) {
        mBandeiraSpinnerAdapter = new SpinnerArrayAdapter<Bandeira>(
                this, objects, true) {
            @Override
            public String getItemDescription(Bandeira item) {
                return item.getNomeBandeira();
            }
        };

        mBandeiraSpinner.setAdapter(mBandeiraSpinnerAdapter);

        if (mPedidoFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mPedidoFilter.getBandeira());
            if (indexOf > 0) {
                mBandeiraSpinner.setSelection(indexOf);
            }
        }

        showSpinnerBandeira(true);
    }

    private void setupStatusSpinner(Object[] objects) {
        mStatusSpinnerAdapter = new SpinnerArrayAdapter<StatusPedido>(
                this, objects, true) {
            @Override
            public String getItemDescription(StatusPedido item) {
                return item.getNome();
            }
        };

        mStatusSpinner.setAdapter(mStatusSpinnerAdapter);

        if (mPedidoFilter != null && mPedidoFilter.getStatus() != null) {
            for(int ind = 1; ind < objects.length; ind++){
                String status = ((StatusPedido) objects[ind]).getNome();

               if(((status).equals(mPedidoFilter.getStatus().getNome()))){
                   mStatusSpinner.setSelection(ind);
               }
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

        if (mPedidoFilter != null) {
            if (mPedidoFilter.getDateStart() != null) {
                String date = FormatUtils.toDefaultDateFormat(mPedidoFilter.getDateStart());
                try {
                    mDataStartTextView.setText(FormatUtils.toDateTimeText(date));
                } catch (ParseException e) {
                    LogUser.log(Config.TAG, e.toString());
                }
            }

            if (mPedidoFilter.getDateEnd() != null) {
                String date = FormatUtils.toDefaultDateFormat(mPedidoFilter.getDateEnd());
                try {
                    mDataEndTextView.setText(FormatUtils.toDateTimeText(date));
                } catch (ParseException e) {
                    LogUser.log(Config.TAG, e.toString());
                }
            }
        }
    }

    private void setupHierarquiaComercialTreeView(Object[] objects) {
        TreeNode root = TreeNode.root();

        mHierarquiaComercialTreeView = new AndroidTreeView(this, root);
        mHierarquiaComercialTreeView.setDefaultViewHolder(HierarquiaComercialViewHolder.class);
        mHierarquiaComercialTreeView.setSelectionModeEnabled(true);
        mHierarquiaComercialTreeView.setDefaultAnimation(false);


        Tree<Usuario> hierarquiaComercial = (Tree<Usuario>) objects[0];
        if (hierarquiaComercial.size() > 100) {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial, 1, 0,
                    new TreeNodePageConfiguration(TreeNodeUtils.DEFAULT_PAGE_SIZE,
                            TreeNodeUtils.DEFAULT_LEVEL_USAGE, null));
        } else {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial);
        }

        if (mPedidoFilter != null) {
            TreeNodeUtils.selectUsuariosInTreeNode(root, mPedidoFilter.getHierarquiaComercial());
        }

        mHierarquiaComercialTreeView.setRoot(root);

        ViewGroup treeViewRootViewGroup = findViewById(R.id.tree_view_root_view_group);
        treeViewRootViewGroup.addView(mHierarquiaComercialTreeView.getView());
    }

    private void showSpinnerBandeira(boolean show) {
        mBandeiraSpinner.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mLoadingBandeiraSpinnerTextView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
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
