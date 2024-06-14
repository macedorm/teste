package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskFilter;
import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.data.ObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Familia;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RelatorioObjetivoFilterActivity extends AppCompatActivity
        implements AsyncTaskRelatorioObjetivoFilter.OnAsyncResponse, View.OnClickListener {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    public static final String FILTER_RESULT_DATA_KEY = "filter_result";
    public static final int FILTER_CLIENTE_REQUEST_CODE = 1;

    private AsyncTaskRelatorioObjetivoFilter mAsyncTaskFilter;
    private Object[] mOrganizacaos;
    private String mParameters[];
    private Cliente cliente;
    private ObjetivoFilter.TObjetivoUn objetivoUn;
    private ObjetivoFilter mObjetivoFilter;

    private SpinnerArrayAdapter<Organizacao> mOrganizacaoSpinnerAdapter;
    private SpinnerArrayAdapter<Bandeira> mBandeiraSpinnerAdapter;
    private SpinnerArrayAdapter<Familia> mFamiliaSpinnerAdapter;

    private Spinner mOrganizacaoSpinner;
    private Spinner mBandeiraSpinner;
    private Spinner mFamiliaSpinner;

    private AndroidTreeView mHierarquiaComercialTreeView;
    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;
    private TextView mLoadingBandeiraSpinnerTextView;
    private TextView mSearchClienteTextView;
    private ImageButton mSearchClienteButton;
    private RadioGroup mTipoRadioGroup;

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
        setContentView(R.layout.activity_objetivo_filter);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mObjetivoFilter = (ObjetivoFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mObjetivoFilter = (ObjetivoFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mOrganizacaoSpinner = findViewById(R.id.org_spinner);
        mBandeiraSpinner = findViewById(R.id.bandeira_spinner);
        mFamiliaSpinner = findViewById(R.id.marca_spinner);
        mProgressLinearLayout = findViewById(R.id.loading_pedido_filter);
        mBaseLinearLayout = findViewById(R.id.base_pedido_filter);
        mLoadingBandeiraSpinnerTextView = findViewById(R.id.loading_bandeira_spinner_text_view);
        mSearchClienteTextView = findViewById(R.id.relatorio_objetivo_filter_seach_cli_text_view);
        mSearchClienteButton = findViewById(R.id.produto_search_image_view);
        mSearchClienteButton.setOnClickListener(this);
        mTipoRadioGroup = findViewById(R.id.relatorio_objetivo_filter_caixa_radio_group);

        mProgressLinearLayout.setVisibility(View.VISIBLE);
        mBaseLinearLayout.setVisibility(View.GONE);

        mParameters = new String[2];

        // It's needed to lock screen rotation during async task orientation, so:
        // 1. store the actual orientation;
        // 2. lock rotation;
        // 3. release rotation feature.
        mScreenOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        mAsyncTaskFilter = new AsyncTaskRelatorioObjetivoFilter(this, true,
                this);

        mAsyncTaskFilter.execute(String.valueOf(AsyncTaskRelatorioObjetivoFilter.ORGANIZACAO));

        mTipoRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.relatorio_objetivo_filter_caixa_radio_button:
                    objetivoUn = ObjetivoFilter.TObjetivoUn.CAIXA;
                    break;
                case R.id.relatorio_objetivo_filter_ton_radio_button:
                    objetivoUn = ObjetivoFilter.TObjetivoUn.TON;
                    break;
                case R.id.relatorio_objetivo_filter_fat_radio_button:
                    objetivoUn = ObjetivoFilter.TObjetivoUn.FAT;
                    break;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mObjetivoFilter);
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
            case AsyncTaskRelatorioObjetivoFilter.ORGANIZACAO:
                setupOrganizacaoSpinner(objects, codigoOrganizacao);
                break;
            case AsyncTaskRelatorioObjetivoFilter.BANDEIRA:
                setupBandeiraSpinner(objects);
                break;
            case AsyncTaskRelatorioObjetivoFilter.FAMILIA:
                setupFamiliaSpinner(objects);
                break;
            case AsyncTaskRelatorioObjetivoFilter.HIERARQUIA:
                setupHierarquiaComercialTreeView(objects);
                break;
        }

        if (isStartLoading && type < AsyncTaskRelatorioObjetivoFilter.HIERARQUIA) {
            type++;
            mParameters[0] = String.valueOf(type);
            mParameters[1] = codigoOrganizacao;
            mAsyncTaskFilter = new AsyncTaskRelatorioObjetivoFilter(this, true, this);
            mAsyncTaskFilter.execute(mParameters);
        } else if (isStartLoading) {
            setupClienteTextView();
            setupTipoRadioGroup();
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
        Familia familia = null;

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

        boolean isThereAnyFamiliaSelected = mFamiliaSpinnerAdapter.isThereAnyItemSelected(
                mFamiliaSpinner);
        if (isThereAnyFamiliaSelected) {
            familia = (Familia) mFamiliaSpinner.getSelectedItem();
        }

        List<Usuario> hierarquiaComercial = mHierarquiaComercialTreeView.getSelectedValues(
                Usuario.class);

        if (cliente != null) {
            mSearchClienteTextView.setText(cliente.getNome());
        }

        mObjetivoFilter = new ObjetivoFilter(cliente, organizacao, bandeira, familia, hierarquiaComercial, objetivoUn);
    }

    private void bundleEmptyFilter() {
        cliente = null;
        mSearchClienteTextView.setText(getString(R.string.search_cliente_hint));
        mObjetivoFilter = new ObjetivoFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mObjetivoFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public void setupClienteTextView() {
        if (mObjetivoFilter != null && mObjetivoFilter.getCliente() != null) {
            cliente = mObjetivoFilter.getCliente();
            mSearchClienteTextView.setText(cliente.getNome());
        }
    }

    private void setupTipoRadioGroup() {
        if (mObjetivoFilter != null && mObjetivoFilter.getObjetivoUn() != null) {
            switch (mObjetivoFilter.getObjetivoUn()) {
                case CAIXA:
                    mTipoRadioGroup.check(R.id.relatorio_objetivo_filter_caixa_radio_button);
                    break;
                case FAT:
                    mTipoRadioGroup.check(R.id.relatorio_objetivo_filter_fat_radio_button);
                    break;
                case TON:
                    mTipoRadioGroup.check(R.id.relatorio_objetivo_filter_ton_radio_button);
                    break;
            }
        }
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

        if (mObjetivoFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mObjetivoFilter.getOrganizacao());
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
                    mAsyncTaskFilter = new AsyncTaskRelatorioObjetivoFilter(RelatorioObjetivoFilterActivity.this,
                            false, RelatorioObjetivoFilterActivity.this);
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

        if (mObjetivoFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mObjetivoFilter.getBandeira());
            if (indexOf > 0) {
                mBandeiraSpinner.setSelection(indexOf);
            }
        }

        showSpinnerBandeira(true);
    }

    private void setupFamiliaSpinner(Object[] objects) {
        mFamiliaSpinnerAdapter = new SpinnerArrayAdapter<Familia>(
                this, objects, true) {
            @Override
            public String getItemDescription(Familia item) {
                return TextUtils.firstLetterUpperCase(item.getNome());
            }
        };

        mFamiliaSpinner.setAdapter(mFamiliaSpinnerAdapter);

        if (mObjetivoFilter != null) {
            int indexOf = ArrayUtils.indexOf(objects, mObjetivoFilter.getFamilia());
            if (indexOf > 0) {
                mFamiliaSpinner.setSelection(indexOf);
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

        if (mObjetivoFilter != null) {
            TreeNodeUtils.selectUsuariosInTreeNode(root, mObjetivoFilter.getHierarquiaComercial());
        }

        mHierarquiaComercialTreeView.setRoot(root);

        ViewGroup treeViewRootViewGroup = findViewById(R.id.tree_view_root_view_group);
        treeViewRootViewGroup.addView(mHierarquiaComercialTreeView.getView());
    }

    private void showSpinnerBandeira(boolean show) {
        mBandeiraSpinner.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mLoadingBandeiraSpinnerTextView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
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

        if (cliente != null) {
            this.cliente = cliente;
            mSearchClienteTextView.setText(cliente.getNome());
        }
    }


}
