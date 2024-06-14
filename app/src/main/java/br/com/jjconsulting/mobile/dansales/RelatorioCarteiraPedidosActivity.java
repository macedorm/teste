package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.RelatorioCarteiraPedidosAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioCarteiraPedidos;
import br.com.jjconsulting.mobile.dansales.data.CarteiraPedidoFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioCarteiraPedidoDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedido;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioCarteiraPedidosActivity extends AppCompatActivity
        implements AsyncTaskRelatorioCarteiraPedidos.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskRelatorioCarteiraPedidos mAsyncTaskPedido;
    private List<RelatorioCarteiraPedido> mPedidos;
    private RelatorioCarteiraPedidoDao mPedidoDao;
    private RelatorioCarteiraPedidosAdapter mPedidosAdapter;
    private CarteiraPedidoFilter mPedidoFilter;

    private Menu mMenu;
    private LinearLayout mListEmptyLinearLayout;
    private RecyclerView mPedidosRecyclerView;
    private ImageButton mListEmptyImageButton;

    private String mRazaoSocialPedido;
    private boolean mIsStartLoading;
    private int indexOffSet;

    public RelatorioCarteiraPedidosActivity() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rel_cart_ped);
        getSupportActionBar().setSubtitle(getString(R.string.title_relatorios));

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mPedidoFilter = (CarteiraPedidoFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mPedidosRecyclerView = findViewById(R.id.pedidos_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(view -> openFilter());

        mPedidoDao = new RelatorioCarteiraPedidoDao(this);

        findPedidos();

        mPedidosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mPedidosRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mPedidosRecyclerView.addItemDecoration(divider);

        mPedidosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());

                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached
                        && !mPedidosAdapter.isFinishPagination()) {
                    mPedidosRecyclerView.post(() -> {
                        if(mPedidoFilter == null){
                            mPedidosAdapter.setFinishPagination(true);
                            addIndexOffSet();
                            loadPedidosPaginacao(false);
                        }
                    });
                }
            }
        });

        ItemClickSupport.addTo(mPedidosRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        RelatorioCarteiraPedido pedido = mPedidosAdapter.getPedidos().get(position);
                        if (pedido.getCodigo() != null) {
                            startActivity(RelatorioCarteiraPedidoDetailActivity.newIntent(this, pedido.getCodigoSap()));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPedidoFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mPedidoFilter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        getMenuInflater().inflate(R.menu.cancel_filter_menu, menu);
        getMenuInflater().inflate(R.menu.cliente_label_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.action_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mRazaoSocialPedido = newText;
                if (mPedidoFilter == null) {
                    mPedidoFilter = new CarteiraPedidoFilter();
                }
                findPedidos();
                return true;
            }
        });

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
                mPedidoFilter = new CarteiraPedidoFilter();
                findPedidos();
                setFilterMenuIcon();
                return true;
            case R.id.action_legendas:
                showLegendaPedido();
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
                    findPedidos(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<RelatorioCarteiraPedido> pedidos) {
        mPedidos = pedidos;
        if (mIsStartLoading) {
            mPedidosAdapter = new RelatorioCarteiraPedidosAdapter(this, mPedidos);
            mPedidosRecyclerView.setAdapter(mPedidosAdapter);
        } else {
            mPedidosAdapter.removeLoadingFooter();
            mPedidosAdapter.updateData(mPedidos);
        }

        if (mPedidoFilter == null && !mPedidosAdapter.isFinishPagination()) {
            mPedidosAdapter.addLoadingFooter();
        }

        if (mPedidosAdapter.getPedidos().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mPedidosRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mPedidosRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void findPedidos() {
        findPedidos(null);
    }

    private void findPedidos(Intent data) {
        try {
            mPedidosAdapter = new RelatorioCarteiraPedidosAdapter(this, new ArrayList<>());
            mPedidosRecyclerView.setAdapter(mPedidosAdapter);

            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(PedidoFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mPedidoFilter = (CarteiraPedidoFilter) data.getSerializableExtra(
                        PedidoFilterActivity.FILTER_RESULT_DATA_KEY);
                setFilterMenuIcon();

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mPedidosRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadPedidos();
            mPedidosAdapter.addLoadingFooter();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadPedidos() {
        resetIndexOffSet();
        mPedidosAdapter.resetData();
        loadPedidosPaginacao(true);
    }

    private void loadPedidosPaginacao(boolean isStartLoading) {
        this.mIsStartLoading = isStartLoading;
        if (mAsyncTaskPedido != null) {
            mAsyncTaskPedido.cancel(true);
        }
        mAsyncTaskPedido = new AsyncTaskRelatorioCarteiraPedidos(this, mPedidoFilter, indexOffSet, mRazaoSocialPedido, mPedidoDao,
                this);
        mAsyncTaskPedido.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mPedidoFilter == null || mPedidoFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mPedidoFilter == null || mPedidoFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }
    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, RelatorioCarteiraPedidoFilterActivity.class);
        if (mPedidoFilter != null) {
            filterIntent.putExtra(RelatorioCarteiraPedidoFilterActivity.FILTER_RESULT_DATA_KEY,
                    mPedidoFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void showLegendaPedido() {
        Dialog mDialogSubtitles = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialogSubtitles.setCancelable(true);
        mDialogSubtitles.setContentView(R.layout.dialog_legenda_carteira_pedido);

        TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
        tvOkSubTitles.setOnClickListener(view -> mDialogSubtitles.dismiss());

        mDialogSubtitles.show();
    }

    public void resetIndexOffSet() {
        indexOffSet = 0;
    }

    public void addIndexOffSet() {
        this.indexOffSet += Config.SIZE_PAGE;
    }
}
