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

import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioObjetivo;
import br.com.jjconsulting.mobile.dansales.adapter.RelatorioObjetivoAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.ObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioObjetivoDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioObjetivo;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioObjetivoActivity extends BaseActivity implements AsyncTaskRelatorioObjetivo.OnAsyncResponse {

    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskRelatorioObjetivo asyncTaskRelatorioObjetivo;
    private ObjetivoFilter mObjetivoFilter;
    private List<RelatorioObjetivo> mRelatorioObjetivo;
    private RelatorioObjetivoDao mObjetivosDao;
    private RelatorioObjetivoAdapter mRelatorioObjetivoAdapter;

    private Menu mMenu;
    private RecyclerView mObjetivoRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;

    private boolean isStartLoading;
    private int indexOffSet;
    private String mNome;

    public RelatorioObjetivoActivity() {
    }

    public static RelatorioObjetivoActivity newInstance() {
        return new RelatorioObjetivoActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_objetivo);

        getSupportActionBar().setSubtitle(getString(R.string.title_relatorios));

        mObjetivoRecyclerView = findViewById(R.id.relatorio_objetivo_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(view -> {
            openFilter();
        });

        mObjetivosDao = new RelatorioObjetivoDao(this);

        findObjetivo();

        mObjetivoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mObjetivoRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mObjetivoRecyclerView.addItemDecoration(divider);

        mObjetivoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mRelatorioObjetivoAdapter.isFinishPagination()) {
                    mObjetivoRecyclerView.post(() -> {
                        mRelatorioObjetivoAdapter.setFinishPagination(true);
                        addIndexOffSet();
                        loadObjetivoPaginacao(false);
                    });
                }
            }
        });

        ItemClickSupport.addTo(mObjetivoRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {

                });
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
                findObjetivo();
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
                mObjetivoFilter = new ObjetivoFilter();
                findObjetivo();
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
                    findObjetivo(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<RelatorioObjetivo> relatorioObjetivos) {
        mRelatorioObjetivo = relatorioObjetivos;
        if (isStartLoading) {
            mRelatorioObjetivoAdapter = new RelatorioObjetivoAdapter(this, mRelatorioObjetivo);
            mObjetivoRecyclerView.setAdapter(mRelatorioObjetivoAdapter);
        } else {
            mRelatorioObjetivoAdapter.removeLoadingFooter();
            mRelatorioObjetivoAdapter.updateData(mRelatorioObjetivo);
        }

        if (!mRelatorioObjetivoAdapter.isFinishPagination()) {
            mRelatorioObjetivoAdapter.addLoadingFooter();
        }


        if (mRelatorioObjetivoAdapter.getOjetivos().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mObjetivoRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mObjetivoRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void findObjetivo() {
        findObjetivo(null);
    }

    private void findObjetivo(Intent data) {
        try {
            mRelatorioObjetivoAdapter = new RelatorioObjetivoAdapter(this, new ArrayList<>());
            mObjetivoRecyclerView.setAdapter(mRelatorioObjetivoAdapter);

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mObjetivoFilter = (ObjetivoFilter) data.getSerializableExtra(
                        ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mObjetivoRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadObjetivo();
            mRelatorioObjetivoAdapter.addLoadingFooter();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadObjetivo() {
        resetIndexOffSet();
        mRelatorioObjetivoAdapter.resetData();
        loadObjetivoPaginacao(true);
    }

    private void loadObjetivoPaginacao(boolean isStartLoading) {
        this.isStartLoading = isStartLoading;

        if (asyncTaskRelatorioObjetivo != null) {
            asyncTaskRelatorioObjetivo.cancel(true);
        }

        asyncTaskRelatorioObjetivo = new AsyncTaskRelatorioObjetivo(this, indexOffSet, mNome, mObjetivoFilter, mObjetivosDao, this);
        asyncTaskRelatorioObjetivo.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mObjetivoFilter == null || mObjetivoFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mObjetivoFilter == null || mObjetivoFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }

    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, RelatorioObjetivoFilterActivity.class);
        if (mObjetivoFilter != null) {
            filterIntent.putExtra(RelatorioObjetivoFilterActivity.FILTER_RESULT_DATA_KEY,
                    mObjetivoFilter);
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
