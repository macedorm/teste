package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskNotas;
import br.com.jjconsulting.mobile.dansales.adapter.RelatorioNotasAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.NotaFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioNotasDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioNotas;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioNotasActivity extends BaseActivity implements AsyncTaskNotas.OnAsyncResponse {

    private static final String KEY_NOTAS_STATE = "filter_notas_state";
    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskNotas asyncTaskNotas;
    private List<RelatorioNotas> mNotas;
    private RelatorioNotasDao mRelatorioNotasDao;
    private RelatorioNotasAdapter mRelatorioNotasAdapter;
    private NotaFilter mNotaFilter;

    private Menu mMenu;
    private RecyclerView mNotasRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;

    private boolean isStartLoading;
    private String mNome;
    private int indexOffSet;

    public RelatorioNotasActivity() {
    }

    public static RelatorioNotasActivity newInstance() {
        return new RelatorioNotasActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_notas);

        getSupportActionBar().setSubtitle(getString(R.string.title_relatorios));

        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mNotaFilter = (NotaFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mNotasRecyclerView = findViewById(R.id.relatorio_notas_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(view -> {
            openFilter();
        });

        mRelatorioNotasDao = new RelatorioNotasDao(this);

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
                        if (mRelatorioNotasAdapter.getNotas().get(position).getNumero() != null) {
                            Intent it = new Intent(this, RelatorioNotasDetailActivity.class);
                            it.putExtra(KEY_NOTAS_STATE, mRelatorioNotasAdapter.getNotas().get(position));
                            startActivity(it);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNotaFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mNotaFilter);
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
                if (mNotaFilter == null) {
                    mNotaFilter = new NotaFilter();
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
                mNotaFilter = new NotaFilter();
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
    public void processFinish(List<RelatorioNotas> notas) {
        mNotas = notas;
        if (isStartLoading) {
            mRelatorioNotasAdapter = new RelatorioNotasAdapter(this, mNotas);
            mNotasRecyclerView.setAdapter(mRelatorioNotasAdapter);
        } else {
            mRelatorioNotasAdapter.removeLoadingFooter();
            mRelatorioNotasAdapter.updateData(mNotas);
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
            mRelatorioNotasAdapter = new RelatorioNotasAdapter(this, new ArrayList<>());
            mNotasRecyclerView.setAdapter(mRelatorioNotasAdapter);

            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mNotaFilter = (NotaFilter) data.getSerializableExtra(
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

        if (asyncTaskNotas != null) {
            asyncTaskNotas.cancel(true);
        }

        asyncTaskNotas = new AsyncTaskNotas(this, indexOffSet, mNotaFilter, mNome, mRelatorioNotasDao, this);
        asyncTaskNotas.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mNotaFilter == null || mNotaFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mNotaFilter == null || mNotaFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }

    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, RelatorioNotasFilterActivity.class);
        if (mNotaFilter != null) {
            filterIntent.putExtra(RelatorioNotasFilterActivity.FILTER_RESULT_DATA_KEY,
                    mNotaFilter);
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
