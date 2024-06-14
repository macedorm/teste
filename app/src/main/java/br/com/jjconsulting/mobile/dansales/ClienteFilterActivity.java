package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskFilter;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ClienteFilterActivity extends AppCompatActivity
        implements AsyncTaskFilter.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";

    private AsyncTaskFilter mAsyncTaskFilter;
    private String mParameters[];
    private Object[] mOrganizacaos;
    private ClienteFilter mClienteFilter;

    private SpinnerArrayAdapter<Organizacao> mOrganizacaoSpinnerAdapter;
    private SpinnerArrayAdapter<Bandeira> mBandeiraSpinnerAdapter;
    private SpinnerArrayAdapter<Integer> mStatusSpinnerAdapter;
    private SpinnerArrayAdapter<Integer> mPlanoCampoSpinnerAdapter;

    private Spinner mOrganizacaoSpinner;
    private Spinner mBandeiraSpinner;
    private Spinner mStatusSpinner;
    private Spinner mPlanoCampoSpinner;

    private AndroidTreeView mHierarquiaComercialTreeView;
    private TextView mLoadingBandeiraSpinnerTextView;
    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;

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
        setContentView(R.layout.activity_cliente_filter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mClienteFilter = (ClienteFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mClienteFilter = (ClienteFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mOrganizacaoSpinner = findViewById(R.id.org_spinner);
        mBandeiraSpinner = findViewById(R.id.bandeira_spinner);
        mStatusSpinner = findViewById(R.id.status_spinner);
        mPlanoCampoSpinner = findViewById(R.id.plano_campo_spinner);

        boolean habPositivacao = Current.getInstance(this).getUsuario().getPerfil().isHabPositivacao();

        if(!habPositivacao){
            mPlanoCampoSpinner.setVisibility(View.GONE);
            findViewById(R.id.plano_campo_text_view).setVisibility(View.GONE);
        }

        mProgressLinearLayout = findViewById(R.id.loading_cliente_filter);
        mBaseLinearLayout = findViewById(R.id.base_cliente_filter);
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

        mAsyncTaskFilter = new AsyncTaskFilter(this, true, this);
        mAsyncTaskFilter.execute(String.valueOf(AsyncTaskFilter.ORGANIZACAO));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mClienteFilter);
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
            case AsyncTaskFilter.PLANOCAMPO:
                setupPlanoCampoSpinner(objects);
                break;
            case AsyncTaskFilter.HIERARQUIA:
                setupHierarquiaComercialTreeView(objects);
                break;
        }

        if (isStartLoading && type < AsyncTaskFilter.PLANOCAMPO) {
            type++;
            mParameters[0] = String.valueOf(type);
            mParameters[1] = codigoOrganizacao;
            mAsyncTaskFilter = new AsyncTaskFilter(this, true,
                    this);
            mAsyncTaskFilter.execute(mParameters);
        } else if (isStartLoading) {
            mIsVisibleMenu = true;
            invalidateOptionsMenu();
            mProgressLinearLayout.setVisibility(View.GONE);
            mBaseLinearLayout.setVisibility(View.VISIBLE);
            setRequestedOrientation(mScreenOrientation);
        }
    }

    private void bundleFilter() {
        Organizacao organizacao = null;
        Bandeira bandeira = null;
        Integer status = null;
        Integer planoCampo = null;

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
            status = (Integer) mStatusSpinner.getSelectedItem();
        }


        boolean isThereAnyPlanoCampoSelected = mPlanoCampoSpinnerAdapter.isThereAnyItemSelected(
                mPlanoCampoSpinner);
        if (isThereAnyPlanoCampoSelected) {
            planoCampo = (Integer) mPlanoCampoSpinner.getSelectedItem();
        }


        List<Usuario> hierarquiaComercial = mHierarquiaComercialTreeView.getSelectedValues(
                Usuario.class);


        mClienteFilter = new ClienteFilter(organizacao, bandeira, status,planoCampo, hierarquiaComercial);
    }

    private void bundleEmptyFilter() {
        mClienteFilter = new ClienteFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mClienteFilter);
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

        if (mClienteFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mClienteFilter.getOrganizacao());
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
                    mAsyncTaskFilter = new AsyncTaskFilter(ClienteFilterActivity.this,
                            false, ClienteFilterActivity.this);
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

        if (mClienteFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mClienteFilter.getBandeira());
            if (indexOf > 0) {
                mBandeiraSpinner.setSelection(indexOf);
            }
        }

        showSpinnerBandeira(true);
    }

    private void setupStatusSpinner(Object[] objects) {
        mStatusSpinnerAdapter = new SpinnerArrayAdapter<Integer>(
                this, objects, true) {
            @Override
            public String getItemDescription(Integer item) {
                return getString(ClienteUtils.getStatusCreditoStringResourceId(item));
            }
        };

        mStatusSpinner.setAdapter(mStatusSpinnerAdapter);

        if (mClienteFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mClienteFilter.getStatus());
            if (indexOf > 0) {
                mStatusSpinner.setSelection(indexOf);
            }
        }
    }


    private void setupPlanoCampoSpinner(Object[] objects) {
        mPlanoCampoSpinnerAdapter = new SpinnerArrayAdapter<Integer>(
                this, objects, true) {
            @Override
            public String getItemDescription(Integer item) {
                return PlanoCampoUtils.getFilterName(getContext(), item);
            }
        };

        mPlanoCampoSpinner.setAdapter(mPlanoCampoSpinnerAdapter);

        if (mClienteFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mClienteFilter.getPlanoCampo());
            if (indexOf > 0) {
                mPlanoCampoSpinner.setSelection(indexOf);
            }
        }
    }


    private void setupHierarquiaComercialTreeView(Object[] objects) {
        TreeNode root = TreeNode.root();

        mHierarquiaComercialTreeView = new AndroidTreeView(this, root);
        mHierarquiaComercialTreeView.setDefaultViewHolder(HierarquiaComercialViewHolder.class);
        mHierarquiaComercialTreeView.setSelectionModeEnabled(true);
        mHierarquiaComercialTreeView.setDefaultAnimation(false);
        mHierarquiaComercialTreeView.setDefaultContainerStyle(R.style.TreeNodeStyle,
                false);

        Tree<Usuario> hierarquiaComercial = (Tree<Usuario>) objects[0];
        if (hierarquiaComercial.size() > 100) {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial, 1, 0,
                    new TreeNodePageConfiguration(TreeNodeUtils.DEFAULT_PAGE_SIZE,
                            TreeNodeUtils.DEFAULT_LEVEL_USAGE, null));
        } else {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial);
        }

        if (mClienteFilter != null) {
            TreeNodeUtils.selectUsuariosInTreeNode(root, mClienteFilter.getHierarquiaComercial());
        }

        mHierarquiaComercialTreeView.setRoot(root);

        ViewGroup treeViewRootViewGroup = findViewById(R.id.tree_view_root_view_group);
        treeViewRootViewGroup.addView(mHierarquiaComercialTreeView.getView());
    }

    private void showSpinnerBandeira(boolean show) {
        mBandeiraSpinner.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mLoadingBandeiraSpinnerTextView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }
}
