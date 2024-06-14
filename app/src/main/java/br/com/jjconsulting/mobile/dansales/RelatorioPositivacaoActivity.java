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

import br.com.jjconsulting.mobile.dansales.adapter.RelatorioPositivacaoAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRelatorioPositivacao;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.ObjetivoFilter;
import br.com.jjconsulting.mobile.dansales.database.RelatorioPositivacaoDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioPositivacao;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RelatorioPositivacaoActivity extends BaseActivity implements AsyncTaskRelatorioPositivacao.OnAsyncResponse {

    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskRelatorioPositivacao asyncTaskRelatorioPositivacao;
    private ObjetivoFilter mObjetivoFilter;
    private RelatorioPositivacaoDao mRelatorioPositivacaoDao;

    private RelatorioPositivacaoAdapter mRelatorioPositivacaoAdapter;

    private Menu mMenu;
    private RecyclerView mPositivacaoRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;


    public static RelatorioPositivacaoActivity newInstance() {
        return new RelatorioPositivacaoActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_objetivo);

        getSupportActionBar().setSubtitle(getString(R.string.title_relatorios));

        mRelatorioPositivacaoDao = new RelatorioPositivacaoDao(this);

        mPositivacaoRecyclerView = findViewById(R.id.relatorio_objetivo_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setVisibility(View.GONE);

        findPositivacao();

        mPositivacaoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mPositivacaoRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mPositivacaoRecyclerView.addItemDecoration(divider);

        ItemClickSupport.addTo(mPositivacaoRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {

                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu);

      //  getMenuInflater().inflate(R.menu.search_menu, menu);
      //  getMenuInflater().inflate(R.menu.filter_menu, menu);
      //  getMenuInflater().inflate(R.menu.cancel_filter_menu, menu);

        /*MenuItem searchItem = menu.findItem(R.id.action_search);
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
        });*/

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
                findPositivacao();
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
                    findPositivacao(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<RelatorioPositivacao> relatorioPositivacao) {
        mRelatorioPositivacaoAdapter = new RelatorioPositivacaoAdapter(this, relatorioPositivacao);
        mPositivacaoRecyclerView.setAdapter(mRelatorioPositivacaoAdapter);

        if (relatorioPositivacao.size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mPositivacaoRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mPositivacaoRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void findPositivacao() {
        findPositivacao(null);
    }

    private void findPositivacao(Intent data) {
        try {
            mRelatorioPositivacaoAdapter = new RelatorioPositivacaoAdapter(this, new ArrayList<>());
            mPositivacaoRecyclerView.setAdapter(mRelatorioPositivacaoAdapter);

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mObjetivoFilter = (ObjetivoFilter) data.getSerializableExtra(
                        ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mPositivacaoRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadObjetivo();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadObjetivo() {
        mRelatorioPositivacaoAdapter.resetData();
        loadPositivacaoPaginacao();
    }

    private void loadPositivacaoPaginacao() {
        if (asyncTaskRelatorioPositivacao != null) {
            asyncTaskRelatorioPositivacao.cancel(true);
        }

        asyncTaskRelatorioPositivacao = new AsyncTaskRelatorioPositivacao(this, mRelatorioPositivacaoDao, this);
        asyncTaskRelatorioPositivacao.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
       /* if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mObjetivoFilter == null || mObjetivoFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mObjetivoFilter == null || mObjetivoFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }*/

    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, RelatorioObjetivoFilterActivity.class);
        if (mObjetivoFilter != null) {
            filterIntent.putExtra(RelatorioObjetivoFilterActivity.FILTER_RESULT_DATA_KEY,
                    mObjetivoFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }
}
