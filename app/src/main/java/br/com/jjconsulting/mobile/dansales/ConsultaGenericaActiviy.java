package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.ConsultaGenericaAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskConsultaGenerica;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.ConsultaGenericaFilter;
import br.com.jjconsulting.mobile.dansales.database.ConsultaGenericaDao;
import br.com.jjconsulting.mobile.dansales.model.ConsultaGenerica;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ConsultaGenericaActiviy extends BaseActivity implements AsyncTaskConsultaGenerica.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskConsultaGenerica asyncTaskRequisicao;
    private List<ConsultaGenerica> mConsultaGenerica;
    private ConsultaGenericaDao mConsultaGenericaDao;
    private ConsultaGenericaAdapter mConsultaGenericaAdapter;
    private ConsultaGenericaFilter mConsultaGenericaFilter;

    private Menu mMenu;
    private RecyclerView mRequisicaoRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;

    private boolean isStartLoading;
    private String mNome;
    private int indexOffSet;

    public static ConsultaGenerica newInstance() {
        return new ConsultaGenerica();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicao);

        getSupportActionBar().setTitle(getString(R.string.title_consulta_generica));

        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mConsultaGenericaFilter = (ConsultaGenericaFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mRequisicaoRecyclerView = findViewById(R.id.requisicao_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(view -> {
            openFilter();
        });

        mConsultaGenericaDao = new ConsultaGenericaDao(this);

        find();

        mRequisicaoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mRequisicaoRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mRequisicaoRecyclerView.addItemDecoration(divider);

        mRequisicaoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mConsultaGenericaAdapter.isFinishPagination()) {
                    mRequisicaoRecyclerView.post(() -> {
                        mConsultaGenericaAdapter.setFinishPagination(true);
                        addIndexOffSet();
                        loadPaginacao(false);
                    });
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mConsultaGenericaFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mConsultaGenericaFilter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        getMenuInflater().inflate(R.menu.cancel_filter_menu, menu);

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
                mNome = newText;
                if (mConsultaGenericaFilter == null) {
                    mConsultaGenericaFilter = new ConsultaGenericaFilter();
                }
                find();
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
                mConsultaGenericaFilter = new ConsultaGenericaFilter();
                find();
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
                    find(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<ConsultaGenerica> requisicaos) {
        mConsultaGenerica = requisicaos;
        if (isStartLoading) {
            mConsultaGenericaAdapter = new ConsultaGenericaAdapter(this, mConsultaGenerica);
            mRequisicaoRecyclerView.setAdapter(mConsultaGenericaAdapter);
        } else {
            mConsultaGenericaAdapter.removeLoadingFooter();
            mConsultaGenericaAdapter.updateData(mConsultaGenerica);
        }

        if (!mConsultaGenericaAdapter.isFinishPagination()) {
            mConsultaGenericaAdapter.addLoadingFooter();
        }

        if (mConsultaGenericaAdapter.getRequisao().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mRequisicaoRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mRequisicaoRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void find() {
        find(null);
    }

    private void find(Intent data) {
        try {
            mConsultaGenericaAdapter = new ConsultaGenericaAdapter(this, new ArrayList<>());
            mRequisicaoRecyclerView.setAdapter(mConsultaGenericaAdapter);

            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mConsultaGenericaFilter = (ConsultaGenericaFilter) data.getSerializableExtra(
                        RelatorioNotasFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mRequisicaoRecyclerView.setVisibility(View.VISIBLE);
            }

            reload();
            mConsultaGenericaAdapter.addLoadingFooter();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reload() {
        resetIndexOffSet();
        mConsultaGenericaAdapter.resetData();
        loadPaginacao(true);
    }

    private void loadPaginacao(boolean isStartLoading) {
        this.isStartLoading = isStartLoading;

        if (asyncTaskRequisicao != null) {
            asyncTaskRequisicao.cancel(true);
        }

        asyncTaskRequisicao = new AsyncTaskConsultaGenerica(this, indexOffSet, mConsultaGenericaFilter, mNome, mConsultaGenericaDao, this);
        asyncTaskRequisicao.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mConsultaGenericaFilter == null || mConsultaGenericaFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mConsultaGenericaFilter == null || mConsultaGenericaFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }

    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, ConsultaGenericaFilterActivity.class);
        if (mConsultaGenericaFilter != null) {
            filterIntent.putExtra(ConsultaGenericaFilterActivity.FILTER_RESULT_DATA_KEY,
                    mConsultaGenericaFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    public void resetIndexOffSet() {
        indexOffSet = 0;
    }

    public void addIndexOffSet() {
        this.indexOffSet += Config.SIZE_PAGE;
    }

}
