package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
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

import com.android.volley.VolleyError;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.ContratoAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.ContratoConnection;
import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.model.RetError;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class ContratoFragment extends BaseFragment {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;
    private ClienteFilter mClienteFilter;
    private ContratoAdapter mContratoAdapter;
    private ContratoConnection mContratoConnection;

    private OnClienteClickListener mOnClienteClickListener;

    private RecyclerView mContratoRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private Menu mMenu;

    private String mNome;
    private boolean mIsStartLoading;
    private int indexOffSet;
    private int totalPage;


    public ContratoFragment() {
    }

    public static ContratoFragment newInstance() {
        return new ContratoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_contrato, container, false);

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mClienteFilter = (ClienteFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }


        mContratoRecyclerView = view.findViewById(R.id.clientes_recycler_view);

        mContratoConnection = new ContratoConnection(getContext().getApplicationContext(), new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {

                List<String> mContratos = new ArrayList<>();

                if (mIsStartLoading) {
                    mContratoAdapter = new ContratoAdapter(mContratos);
                    mContratoRecyclerView.setAdapter(mContratoAdapter);
                } else {
                    mContratoAdapter.removeLoadingFooter();
                    mContratoAdapter.updateData(mContratos);
                }

                if (!mContratoAdapter.isFinishPagination()) {
                    mContratoAdapter.addLoadingFooter();
                }

                if (mContratoAdapter.getContratos().size() == 0) {
                    mListEmptyLinearLayout.setVisibility(View.VISIBLE);
                    mContratoRecyclerView.setVisibility(View.GONE);
                } else {
                    mListEmptyLinearLayout.setVisibility(View.GONE);
                    mContratoRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {

                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                    ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(getContext(), errorConnection.getMessage())){
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                if (indexOffSet == 0) {
                    dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                } else {
                    mContratoAdapter.removeLoadingFooter();
                }
            }
        });

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(viewListEmpty -> {
            openFilter();
        });

        findItens();

        mContratoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(mContratoRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mContratoRecyclerView.addItemDecoration(divider);

        mContratoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mContratoAdapter.isFinishPagination()) {
                    mContratoRecyclerView.post(() -> {
                        mContratoAdapter.setFinishPagination(true);
                        addIndexOffSet();
                        loadContratos();
                    });
                }
            }
        });

        ItemClickSupport.addTo(mContratoRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        /*TapCliente cliente = mContratoAdapter.getContratos().get(position);
                        if (cliente.getCodigo() != null) {
                            mOnClienteClickListener.onClienteClick(cliente);
                        }*/
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
                findItens();
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
                findItens();
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
                    findItens(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
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

    private void findItens() {
        findItens(null);
    }

    private void findItens(Intent data) {
        try {
            mContratoAdapter = new ContratoAdapter(new ArrayList<>());
            mContratoRecyclerView.setAdapter(mContratoAdapter);

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mClienteFilter = (ClienteFilter) data.getSerializableExtra(
                        ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mContratoRecyclerView.setVisibility(View.VISIBLE);
            }

            loadContratos();
            mContratoAdapter.addLoadingFooter();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void loadContratos() {
        resetIndexOffSet();
        mContratoAdapter.resetData();
        mIsStartLoading = true;
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


    public void resetIndexOffSet() {
        indexOffSet = 0;
    }

    public void addIndexOffSet() {
        this.indexOffSet += totalPage;
    }

    public interface OnClienteClickListener {
        void onClienteClick(Cliente cliente);
    }
}
