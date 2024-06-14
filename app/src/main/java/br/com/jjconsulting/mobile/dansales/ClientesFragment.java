package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.ClientesAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskCliente;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ClientesFragment extends BaseFragment implements AsyncTaskCliente.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskCliente mAsyncTaskCliente;
    private List<Cliente> mClientes;
    private ClienteDao mClienteDao;
    private ClienteFilter mClienteFilter;
    private ClientesAdapter mClientesAdapter;

    private OnClienteClickListener mOnClienteClickListener;

    private RecyclerView mClientesRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private Menu mMenu;

    private String mNome;
    private boolean mIsStartLoading;
    private int indexOffSet;
    private String codigoPesquisa;

    private boolean isRota;

    public ClientesFragment() {
    }

    public static ClientesFragment newInstance() {
        return new ClientesFragment();
    }

    public static ClientesFragment newInstance(String codigoPesquisa) {
        ClientesFragment c = new ClientesFragment();
        c.setCodigoPesquisa(codigoPesquisa);
        return c;
    }

    public static ClientesFragment newInstance(boolean isRota) {
        ClientesFragment c = new ClientesFragment();
        c.setIsRota(isRota);
        return c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mClienteFilter = (ClienteFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mClientesRecyclerView = view.findViewById(R.id.clientes_recycler_view);

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(viewListEmpty -> {
            openFilter();
        });

        mClienteDao = new ClienteDao(getActivity());
        findClientes();

        mClientesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(mClientesRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mClientesRecyclerView.addItemDecoration(divider);

        mClientesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mClientesAdapter.isFinishPagination()) {
                    mClientesRecyclerView.post(() -> {
                        mClientesAdapter.setFinishPagination(true);
                        addIndexOffSet();
                        loadClientesPaginacao(false);
                    });
                }
            }
        });

        ItemClickSupport.addTo(mClientesRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        Cliente cliente = mClientesAdapter.getClientes().get(position);
                        if (cliente.getCodigo() != null) {
                            mOnClienteClickListener.onClienteClick(cliente);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mClienteFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mClienteFilter);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.filter_menu, menu);
        inflater.inflate(R.menu.cancel_filter_menu, menu);
        inflater.inflate(R.menu.cliente_label_menu, menu);

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
                if (mClienteFilter == null) {
                    mClienteFilter = new ClienteFilter();
                }
                findClientes();
                return true;
            }
        });

        setFilterMenuIcon();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                openFilter();
                return true;
            case R.id.menu_cancel_filter:
                mClienteFilter = new ClienteFilter();
                findClientes();
                return true;
            case R.id.action_legendas:
                showLegend();
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
    public void processFinish(List<Cliente> clientes) {
        mClientes = clientes;
        if (mIsStartLoading) {
            mClientesAdapter = new ClientesAdapter(mClientes, false, getContext());
            mClientesRecyclerView.setAdapter(mClientesAdapter);
        } else {
            mClientesAdapter.removeLoadingFooter();
            mClientesAdapter.updateData(mClientes);
        }

        if (!mClientesAdapter.isFinishPagination()) {
            mClientesAdapter.addLoadingFooter();
        }


        if (mClientesAdapter.getClientes().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mClientesRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mClientesRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnClienteClickListener) {
            mOnClienteClickListener = (OnClienteClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClienteClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnClienteClickListener = null;
    }

    private void findClientes() {
        findClientes(null);
    }

    private void findClientes(Intent data) {
        try {
            mClientesAdapter = new ClientesAdapter(new ArrayList<>(), false, getContext());
            mClientesRecyclerView.setAdapter(mClientesAdapter);

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mClienteFilter = (ClienteFilter) data.getSerializableExtra(
                        ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mClientesRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadClientes();
            mClientesAdapter.addLoadingFooter();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadClientes() {
        resetIndexOffSet();
        mClientesAdapter.resetData();
        loadClientesPaginacao(true);
    }

    private void loadClientesPaginacao(boolean isStartLoading) {
        this.mIsStartLoading = isStartLoading;
        if (mAsyncTaskCliente != null) {
            mAsyncTaskCliente.cancel(true);
        }

        mAsyncTaskCliente = new AsyncTaskCliente(getActivity(), indexOffSet, mClienteFilter, mNome, mClienteDao, getCodigoPesquisa(),  this, isRota);
        mAsyncTaskCliente.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mClienteFilter == null || mClienteFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mClienteFilter == null || mClienteFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }
    }


    private void openFilter() {
        Intent filterIntent = new Intent(getActivity(), ClienteFilterActivity.class);
        if (mClienteFilter != null) {
            filterIntent.putExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY,
                    mClienteFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void showLegend() {
        Dialog mDialogSubtitles = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialogSubtitles.setCancelable(true);
        mDialogSubtitles.setContentView(R.layout.dialog_legenda_cliente);
        TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
        tvOkSubTitles.setOnClickListener(v -> mDialogSubtitles.dismiss());
        mDialogSubtitles.show();
    }

    public void resetIndexOffSet() {
        indexOffSet = 0;
    }

    public void addIndexOffSet() {
        this.indexOffSet += Config.SIZE_PAGE;
    }

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente);
    }

    public String getCodigoPesquisa() {
        return codigoPesquisa;
    }

    public void setCodigoPesquisa(String codigoPesquisa) {
        this.codigoPesquisa = codigoPesquisa;
    }

    public void setIsRota(boolean isRota) {
        this.isRota = isRota;
    }
}
