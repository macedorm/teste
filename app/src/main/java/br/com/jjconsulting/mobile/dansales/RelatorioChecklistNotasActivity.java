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

import br.com.jjconsulting.mobile.dansales.adapter.RelatorioChecklistNotasAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskChecklistNotas;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.ChecklistNotaFilter;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisa;
import br.com.jjconsulting.mobile.dansales.database.RelatorioChecklistNotasDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioChecklistNotas;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioChecklistNotasActivity extends BaseActivity implements AsyncTaskChecklistNotas.OnAsyncResponse {

    private static final String KEY_NOTAS_STATE = "filter_checklist_notas_state";
    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskChecklistNotas asyncTaskChecklistNotas;
    private List<RelatorioChecklistNotas> mChecklistNotas;
    private RelatorioChecklistNotasDao mRelatorioChecklistNotasDao;
    private RelatorioChecklistNotasAdapter mRelatorioNotasAdapter;
    private ChecklistNotaFilter mChecklistNotaFilter;

    private Menu mMenu;
    private RecyclerView mNotasRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;

    private boolean isStartLoading;
    private String mNome;
    private int indexOffSet;

    public static RelatorioChecklistNotasActivity newInstance() {
        return new RelatorioChecklistNotasActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_checklist_notas);

        getSupportActionBar().setSubtitle(getString(R.string.title_relatorios));

        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mChecklistNotaFilter = (ChecklistNotaFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mNotasRecyclerView = findViewById(R.id.relatorio_checklist_notas_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(view -> {
            openFilter();
        });

        mRelatorioChecklistNotasDao = new RelatorioChecklistNotasDao(this);

        findClientes();

        mNotasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mNotasRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mNotasRecyclerView.addItemDecoration(divider);

        mNotasRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mRelatorioNotasAdapter.isFinishPagination()) {
                    mNotasRecyclerView.post(() -> {
                        mRelatorioNotasAdapter.setFinishPagination(true);
                        addIndexOffSet();
                        loadNotasPaginacao(false);
                    });
                }
            }
        });

        ItemClickSupport.addTo(mNotasRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {

                        RelatorioChecklistNotas relatorioChecklistNotas = mRelatorioNotasAdapter.getNotas().get(position);
                        SyncPesquisa syncPesquisa = mRelatorioChecklistNotasDao.getPilarNotas(relatorioChecklistNotas);
                        startActivity(RelatorioChecklistNotasDetailActivity.newIntent(RelatorioChecklistNotasActivity.this,
                                relatorioChecklistNotas.getCodigoCliente(), syncPesquisa));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mChecklistNotaFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mChecklistNotaFilter);
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
                if (mChecklistNotaFilter == null) {
                    mChecklistNotaFilter = new ChecklistNotaFilter();
                }
                findClientes();
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
                mChecklistNotaFilter = new ChecklistNotaFilter();
                findClientes();
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
                    findClientes(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<RelatorioChecklistNotas> notas) {
        mChecklistNotas = notas;
        if (isStartLoading) {
            mRelatorioNotasAdapter = new RelatorioChecklistNotasAdapter(this, mChecklistNotas);
            mNotasRecyclerView.setAdapter(mRelatorioNotasAdapter);
        } else {
            mRelatorioNotasAdapter.removeLoadingFooter();
            mRelatorioNotasAdapter.updateData(mChecklistNotas);
        }

        if (!mRelatorioNotasAdapter.isFinishPagination()) {
            mRelatorioNotasAdapter.addLoadingFooter();
        }

        if (mRelatorioNotasAdapter.getNotas().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mNotasRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mNotasRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void findClientes() {
        findClientes(null);
    }

    private void findClientes(Intent data) {
        try {
            mRelatorioNotasAdapter = new RelatorioChecklistNotasAdapter(this, new ArrayList<>());
            mNotasRecyclerView.setAdapter(mRelatorioNotasAdapter);

            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mChecklistNotaFilter = (ChecklistNotaFilter) data.getSerializableExtra(
                        RelatorioNotasFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mNotasRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadClientes();
            mRelatorioNotasAdapter.addLoadingFooter();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadClientes() {
        resetIndexOffSet();
        mRelatorioNotasAdapter.resetData();
        loadNotasPaginacao(true);
    }

    private void loadNotasPaginacao(boolean isStartLoading) {
        this.isStartLoading = isStartLoading;

        if (asyncTaskChecklistNotas != null) {
            asyncTaskChecklistNotas.cancel(true);
        }

        asyncTaskChecklistNotas = new AsyncTaskChecklistNotas(this, indexOffSet, mChecklistNotaFilter, mNome, mRelatorioChecklistNotasDao, this);
        asyncTaskChecklistNotas.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mChecklistNotaFilter == null || mChecklistNotaFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mChecklistNotaFilter == null || mChecklistNotaFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }

    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, RelatorioChecklistNotasFilterActivity.class);
        if (mChecklistNotaFilter != null) {
            filterIntent.putExtra(RelatorioChecklistNotasFilterActivity.FILTER_RESULT_DATA_KEY,
                    mChecklistNotaFilter);
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
