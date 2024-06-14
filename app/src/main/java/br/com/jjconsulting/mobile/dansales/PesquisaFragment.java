package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.PesquisaAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskPesquisa;
import br.com.jjconsulting.mobile.dansales.asyncTask.PostExecuteListener;
import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.TPesquisaEdit;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPesquisa;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PesquisaUtils;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class PesquisaFragment extends Fragment implements PostExecuteListener<List<Pesquisa>> {

    public static final String KEY_CODIGO_PESQUISA = "codigo_pesquisa";
    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";

    private DialogsCustom dialogsDefault;

    private AsyncTaskPesquisa mAsyncTaskPesquisa;
    private PesquisaDao mPesquisaDao;
    private List<Pesquisa> mPesquisa;

    private RecyclerView mPesquisaRecyclerView;
    private PesquisaAdapter mPesquisaAdapter;
    private LinearLayout mListEmptyLinearLayout;

    private String mNome;
    private Date currentDate;
    private int mIndexSelection;

    private String mCodCliente;

    private PesquisaDao.TTypePesquisa tTypePesquisa;

    public PesquisaFragment() {
    }

    public static PesquisaFragment newInstance(String codCliente, Date currentDate,  PesquisaDao.TTypePesquisa tTypePesquisa) {
        PesquisaFragment pesquisaFragment = new PesquisaFragment();
        pesquisaFragment.setCodCliente(codCliente);
        pesquisaFragment.settTypePesquisa(tTypePesquisa);
        pesquisaFragment.setCurrentDate(currentDate);
        return pesquisaFragment;
    }

    public static PesquisaFragment newInstance(String codCliente, PesquisaDao.TTypePesquisa tTypePesquisa) {
        PesquisaFragment pesquisaFragment = new PesquisaFragment();
        pesquisaFragment.setCodCliente(codCliente);
        pesquisaFragment.settTypePesquisa(tTypePesquisa);
        pesquisaFragment.setCurrentDate(new Date());
        return pesquisaFragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPesquisa != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, (Serializable) mPesquisa);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        setHasOptionsMenu(true);

        mPesquisaRecyclerView = view.findViewById(R.id.pesquisa_recycler_view);
        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);

        mPesquisaDao = new PesquisaDao(getActivity());
        dialogsDefault = new DialogsCustom(getActivity());

        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mPesquisa = (List<Pesquisa>) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
            createAdapter(mPesquisa);
        } else {
            findPesquisa();
        }

        mPesquisaRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPesquisaRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), 0));

        ItemClickSupport.addTo(mPesquisaRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        Pesquisa pesquisa = mPesquisaAdapter.getPesquisas().get(position);
                        mIndexSelection = position;

                        if (pesquisa.isSelecionaCliente() && !pesquisa.isResposta()) {
                            if(getCodCliente() == null){
                                String codPesquisa = String.valueOf(pesquisa.getCodigo());
                                startActivityForResult(PickClientePesquisaActivity.newIntent(
                                        getContext(), codPesquisa), Config.REQUEST_CLIENTE_PESQUISA);
                            } else {
                                selectedCliente(mCodCliente);
                            }
                        } else {
                            if(pesquisa.getEdit() == TPesquisaEdit.NO_VIEW_AND_EDIT_ANSWER && pesquisa.getStatusResposta() == Pesquisa.OBRIGATORIAS_RESPONDIDAS){
                                dialogsDefault.showDialogMessage(getString(R.string.pesquisa_sync_no_edit), dialogsDefault.DIALOG_TYPE_WARNING, null);
                            } else {
                                startActivity(PesquisaPerguntasActivity.newIntent(getContext(),
                                        "", mPesquisa.get(mIndexSelection)));
                            }
                        }

                        Intent intentPesquisa = new Intent();
                        intentPesquisa.putExtra(KEY_CODIGO_PESQUISA, String.valueOf(mPesquisa.get(mIndexSelection).getCodigo()));

                        getActivity().setResult(getActivity().RESULT_OK, intentPesquisa);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CurrentActionPesquisa.getInstance().isUpdateListPesquisa()) {
            CurrentActionPesquisa.getInstance().setUpdateListPesquisa(false);
            findPesquisa();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.REQUEST_CLIENTE_PESQUISA:
                if (resultCode == Activity.RESULT_OK) {
                    selectedCliente(data.getStringExtra(
                            PickClientePesquisaActivity.FILTER_RESULT_DATA_KEY));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onPostExecute(List<Pesquisa> object) {
        createAdapter(object);
    }

    private void selectedCliente(String codCliente){
        String codigoCliente = codCliente;
        startActivity(PesquisaPerguntasActivity.newIntent(getContext(), codigoCliente,
                mPesquisa.get(mIndexSelection)));
    }

    public void createAdapter(List<Pesquisa> object) {
        mPesquisa = object;

        //Caso a lista o tipo de pesquisa escolhida seja COACHING e resultado for 1 vai direto para as respostas
        if(PesquisaDao.TTypePesquisa.COACHING == tTypePesquisa && mPesquisa.size() == 1){
            selectedCliente(mCodCliente);
            getActivity().finish();
            return;
        }

        mPesquisaAdapter = new PesquisaAdapter(getActivity(), mPesquisa);
        mPesquisaRecyclerView.setAdapter(mPesquisaAdapter);

        if (mPesquisaAdapter.getPesquisas().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mPesquisaRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mPesquisaRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_legendas:
                showLegend();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
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
                findPesquisa();
                return true;
            }
        });
    }

    private void asyncLoadPesquisa() {
        if (mAsyncTaskPesquisa != null) {
            mAsyncTaskPesquisa.cancel(true);
        }

        mAsyncTaskPesquisa = new AsyncTaskPesquisa(getActivity(), mNome, mPesquisaDao, currentDate, tTypePesquisa, this);
        mAsyncTaskPesquisa.execute();
    }

    private void findPesquisa() {
        try {
            mPesquisaAdapter = new PesquisaAdapter(getActivity(), new ArrayList<>());
            mPesquisaRecyclerView.setAdapter(mPesquisaAdapter);
            mPesquisaAdapter.resetData();
            asyncLoadPesquisa();
            mPesquisaAdapter.addLoadingFooter();
        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void showLegend() {
        Dialog mDialogSubtitles = new Dialog(getContext(),
                android.R.style.Theme_Translucent_NoTitleBar);
        mDialogSubtitles.setCancelable(true);
        mDialogSubtitles.setContentView(R.layout.dialog_legenda_pesquisa);
        TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
        tvOkSubTitles.setOnClickListener(v -> mDialogSubtitles.dismiss());
        mDialogSubtitles.show();
    }

    public String getCodCliente() {
        return mCodCliente;
    }

    public void setCodCliente(String mCodCliente) {
        this.mCodCliente = mCodCliente;
    }

    public PesquisaDao.TTypePesquisa gettTypePesquisa() {
        return tTypePesquisa;
    }

    public void settTypePesquisa(PesquisaDao.TTypePesquisa tTypePesquisa) {
        this.tTypePesquisa = tTypePesquisa;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }
}
