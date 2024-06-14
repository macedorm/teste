package br.com.jjconsulting.mobile.dansales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.RelatorioCarteiraPedidoDetailAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioCarteiraPedidosDetail;
import br.com.jjconsulting.mobile.dansales.database.RelatorioCarteiraPedidosDetailDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioCarteiraPedidoDetail;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioCarteiraPedidoDetailActivity extends AppCompatActivity implements AsyncTaskRelatorioCarteiraPedidosDetail.OnAsyncResponse {

    private static final String ARG_CODIGO_PEDIDO = "codigo_pedido";

    private AsyncTaskRelatorioCarteiraPedidosDetail asyncTaskRelatorioCarteiraPedidosDetail;
    private RelatorioCarteiraPedidoDetailAdapter mRelatorioCarteiraPedidoDetailAdapter;
    private RelatorioCarteiraPedidosDetailDao mRelatorioCarteiraPedidoDetailDao;
    private List<RelatorioCarteiraPedidoDetail> mRelatorioCarteiraPedidosDetail;

    private RecyclerView mCarteiraPedidoItemRecyclerView;
    private LinearLayout mListEmptyLinearLayout;

    private boolean isStartLoading;
    private int indexOffSet;
    private String idPedido;

    public static Intent newIntent(Context context, String codigoPedido) {
        Intent intent = new Intent(context, RelatorioCarteiraPedidoDetailActivity.class);
        intent.putExtra(ARG_CODIGO_PEDIDO, codigoPedido);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rel_cart_ped_detail);
        getSupportActionBar().setSubtitle(getString(R.string.title_relatorios));

        idPedido = getIntent().getStringExtra(ARG_CODIGO_PEDIDO);

        mCarteiraPedidoItemRecyclerView = findViewById(R.id.rel_cart_ped_detail_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);

        mRelatorioCarteiraPedidoDetailDao = new RelatorioCarteiraPedidosDetailDao(this);

        findItens();

        mCarteiraPedidoItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mCarteiraPedidoItemRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mCarteiraPedidoItemRecyclerView.addItemDecoration(divider);

        mCarteiraPedidoItemRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mRelatorioCarteiraPedidoDetailAdapter.isFinishPagination()) {
                    mCarteiraPedidoItemRecyclerView.post(() -> {
                        mRelatorioCarteiraPedidoDetailAdapter.setFinishPagination(true);
                        addIndexOffSet();
                        loadRelatorioCarteiraPedidoDetailPaginacao(false);
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void processFinish(List<RelatorioCarteiraPedidoDetail> sku) {
        mRelatorioCarteiraPedidosDetail = sku;
        if (isStartLoading) {
            mRelatorioCarteiraPedidoDetailAdapter = new RelatorioCarteiraPedidoDetailAdapter(this, mRelatorioCarteiraPedidosDetail);
            mCarteiraPedidoItemRecyclerView.setAdapter(mRelatorioCarteiraPedidoDetailAdapter);
        } else {
            mRelatorioCarteiraPedidoDetailAdapter.removeLoadingFooter();
            mRelatorioCarteiraPedidoDetailAdapter.updateData(mRelatorioCarteiraPedidosDetail);
        }

        if (!mRelatorioCarteiraPedidoDetailAdapter.isFinishPagination()) {
            mRelatorioCarteiraPedidoDetailAdapter.addLoadingFooter();
        }


        if (mRelatorioCarteiraPedidoDetailAdapter.getRelatorioCarteiraPedidoDetail().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mCarteiraPedidoItemRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mCarteiraPedidoItemRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void findItens() {
        findItens(null);
    }

    private void findItens(Intent data) {
        try {
            mRelatorioCarteiraPedidoDetailAdapter = new RelatorioCarteiraPedidoDetailAdapter(this, new ArrayList<>());
            mCarteiraPedidoItemRecyclerView.setAdapter(mRelatorioCarteiraPedidoDetailAdapter);

            reloadRelatorioCarteiraPedidoDetailPaginacao();
            mRelatorioCarteiraPedidoDetailAdapter.addLoadingFooter();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadRelatorioCarteiraPedidoDetailPaginacao() {
        resetIndexOffSet();
        mRelatorioCarteiraPedidoDetailAdapter.resetData();
        loadRelatorioCarteiraPedidoDetailPaginacao(true);
    }

    private void loadRelatorioCarteiraPedidoDetailPaginacao(boolean isStartLoading) {
        this.isStartLoading = isStartLoading;

        if (asyncTaskRelatorioCarteiraPedidosDetail != null) {
            asyncTaskRelatorioCarteiraPedidosDetail.cancel(true);
        }

        asyncTaskRelatorioCarteiraPedidosDetail = new AsyncTaskRelatorioCarteiraPedidosDetail(this, idPedido, indexOffSet, mRelatorioCarteiraPedidoDetailDao, this);
        asyncTaskRelatorioCarteiraPedidosDetail.execute();
    }

    public void resetIndexOffSet() {
        indexOffSet = 0;
    }

    public void addIndexOffSet() {
        this.indexOffSet += Config.SIZE_PAGE;
    }

}
