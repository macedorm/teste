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
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.HierarquiaComercialViewHolder;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncPlanejamentoRotaFilter;
import br.com.jjconsulting.mobile.dansales.data.PlanejamentoRotaFilter;
import br.com.jjconsulting.mobile.dansales.model.TreeNodePageConfiguration;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.jjlib.base.Tree;

public class PlanejamentoRotaFilterActivity extends AppCompatActivity
        implements AsyncPlanejamentoRotaFilter.OnAsyncResponse, View.OnClickListener {

    public static final String FILTER_RESULT_DATA_KEY = "filter_result";
    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";

    private AsyncPlanejamentoRotaFilter mAsyncTaskFilter;

    private PlanejamentoRotaFilter mPlanejamentoRotaFilter;

    private AndroidTreeView mHierarquiaComercialTreeView;
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
        setContentView(R.layout.activity_planejamento_rota_filter);


        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
            mPlanejamentoRotaFilter = (PlanejamentoRotaFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        } else if (getIntent().hasExtra(FILTER_RESULT_DATA_KEY)) {
            mPlanejamentoRotaFilter = (PlanejamentoRotaFilter) getIntent()
                    .getSerializableExtra(FILTER_RESULT_DATA_KEY);
        }

        mProgressLinearLayout = findViewById(R.id.loading_planejamento_rota_filter);
        mBaseLinearLayout = findViewById(R.id.base_planejamento_rota_filter);


        mProgressLinearLayout.setVisibility(View.VISIBLE);
        mBaseLinearLayout.setVisibility(View.GONE);

        // It's needed to lock screen rotation during async task orientation, so:
        // 1. store the actual orientation;
        // 2. lock rotation;
        // 3. release rotation feature.
        mScreenOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        mAsyncTaskFilter = new AsyncPlanejamentoRotaFilter(this,
                this);

        mAsyncTaskFilter.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bundleFilter();
        outState.putSerializable(KEY_FILTER_RESULT_STATE, mPlanejamentoRotaFilter);
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
    public void processFinish(Object[] objects) {
        setupHierarquiaComercialTreeView(objects[0]);

        mIsVisibleMenu = true;
        invalidateOptionsMenu();
        mProgressLinearLayout.setVisibility(View.GONE);
        mBaseLinearLayout.setVisibility(View.VISIBLE);
        setRequestedOrientation(mScreenOrientation);
    }

    private void bundleFilter() {
        List<Usuario> hierarquiaComercial = null;

        if (mHierarquiaComercialTreeView != null){
            hierarquiaComercial = mHierarquiaComercialTreeView.getSelectedValues(
                    Usuario.class);
        }

        mPlanejamentoRotaFilter = new PlanejamentoRotaFilter(hierarquiaComercial);
    }

    private void bundleEmptyFilter() {
        mPlanejamentoRotaFilter = new PlanejamentoRotaFilter();
    }

    private void sendBundledFilter() {
        Intent data = new Intent();
        data.putExtra(FILTER_RESULT_DATA_KEY, mPlanejamentoRotaFilter);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void setupHierarquiaComercialTreeView(Object objects) {
        TreeNode root = TreeNode.root();

        mHierarquiaComercialTreeView = new AndroidTreeView(this, root);
        mHierarquiaComercialTreeView.setDefaultViewHolder(HierarquiaComercialViewHolder.class);
        mHierarquiaComercialTreeView.setSelectionModeEnabled(true);
        mHierarquiaComercialTreeView.setDefaultAnimation(false);

        Tree<Usuario> hierarquiaComercial = (Tree<Usuario>) objects;
        if (hierarquiaComercial.size() > 100) {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial, 1, 0,
                    new TreeNodePageConfiguration(TreeNodeUtils.DEFAULT_PAGE_SIZE,
                            TreeNodeUtils.DEFAULT_LEVEL_USAGE, null));
        } else {
            TreeNodeUtils.buildTreeNode(root, hierarquiaComercial);
        }

        if (mPlanejamentoRotaFilter != null) {
            TreeNodeUtils.selectUsuariosInTreeNode(root, mPlanejamentoRotaFilter.getHierarquiaComercial());
        }

        mHierarquiaComercialTreeView.setRoot(root);


        ViewGroup treeViewRootViewGroup = findViewById(R.id.tree_view_root_view_group);
        treeViewRootViewGroup.addView(mHierarquiaComercialTreeView.getView());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onClick(View view) {
    }

}
