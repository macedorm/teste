package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioChecklistNotasFilter;
import br.com.jjconsulting.mobile.dansales.data.ChecklistNotaFilter;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioChecklistNotasFilterActivity extends AppCompatActivity
        implements AsyncTaskRelatorioChecklistNotasFilter.OnAsyncResponse, View.OnClickListener {

    public static final String FILTER_RESULT_DATA_KEY = "filter_result";
    public static final int FILTER_CLIENTE_REQUEST_CODE = 1;
    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private AsyncTaskRelatorioChecklistNotasFilter mAsyncTaskFilter;
    private Cliente cliente;
    private ChecklistNotaFilter mChecklistNotasFilter;

    private AndroidTreeView mHierarquiaComercialTreeView;
    private LinearLayout mProgressLinearLayout;
    private LinearLayout mBaseLinearLayout;
    private TextView mSearchClienteTextView;
    private ImageButton mSearchClienteButton;

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
        setContentView(R.layout.activity_checklist_notas_filter);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mChecklistNotasFilter = (ChecklistNotaFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mChecklistNotasFilter = (ChecklistNotaFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mProgressLinearLayout = findViewById(R.id.loading_pedido_filter);
        mBaseLinearLayout = findViewById(R.id.base_pedido_filter);
        mSearchClienteTextView = findViewById(R.id.relatorio_objetivo_filter_seach_cli_text_view);
        mSearchClienteButton = findViewById(R.id.produto_search_image_view);
        mSearchClienteButton.setOnClickListener(this);

        mProgressLinearLayout.setVisibility(View.VISIBLE);
        mBaseLinearLayout.setVisibility(View.GONE);

        // It's needed to lock screen rotation during async task orientation, so:
        // 1. store the actual orientation;
        // 2. lock rotation;
        // 3. release rotation feature.
        mScreenOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        mAsyncTaskFilter = new AsyncTaskRelatorioChecklistNotasFilter(this, true,
                this);

        mAsyncTaskFilter.execute();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mChecklistNotasFilter);
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

        setupHierarquiaComercialTreeView(objects);

        setupClienteTextView();
        mIsVisibleMenu = true;
        invalidateOptionsMenu();
        mProgressLinearLayout.setVisibility(View.GONE);
        mBaseLinearLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(mScreenOrientation);
    }

    private void bundleFilter() {
        if (mHierarquiaComercialTreeView == null)
            return;

        List<Usuario> hierarquiaComercial = mHierarquiaComercialTreeView.getSelectedValues(
                Usuario.class);

        if (cliente != null) {
            mSearchClienteTextView.setText(cliente.getNome());
        }

        mChecklistNotasFilter = new ChecklistNotaFilter(cliente, hierarquiaComercial);
    }

    private void bundleEmptyFilter() {
        cliente = null;
        mSearchClienteTextView.setText(getString(R.string.search_cliente_hint));
        mChecklistNotasFilter = new ChecklistNotaFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mChecklistNotasFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    public void setupClienteTextView() {
        if (mChecklistNotasFilter != null && mChecklistNotasFilter.getCliente() != null) {
            cliente = mChecklistNotasFilter.getCliente();
            mSearchClienteTextView.setText(cliente.getNome());
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

        if (mChecklistNotasFilter != null) {
            TreeNodeUtils.selectUsuariosInTreeNode(root, mChecklistNotasFilter.getHierarquiaComercial());
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

        if (cliente != null) {
            this.cliente = cliente;
            mSearchClienteTextView.setText(cliente.getNome());
        }
    }


}
