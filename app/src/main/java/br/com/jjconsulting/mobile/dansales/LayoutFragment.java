package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.LayoutAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskLayout;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class LayoutFragment extends BaseFragment implements AsyncTaskLayout.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskLayout mAsyncTaskLayout;
    private List<Layout> mLayout;
    private SortimentoDao mSortimentoDao;
    private LayoutFilter mLayoutFilter;
    private LayoutAdapter mLayoutAdapter;

    private OnLayoutClickListener mOnLayoutClickListener;

    private RecyclerView mLayoutRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private Menu mMenu;

    private String mNome;
    private boolean mIsStartLoading;

    private String[] groupList;

    public LayoutFragment() {
    }


    public static LayoutFragment newInstance() {
        return new LayoutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        View view = inflater.inflate(R.layout.fragment_clientes, container, false);

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mLayoutFilter = (LayoutFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mLayoutRecyclerView = view.findViewById(R.id.clientes_recycler_view);

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(viewListEmpty -> {
            openFilter();
        });

        mSortimentoDao = new SortimentoDao(getActivity());
        findLayouts();

        mLayoutRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(mLayoutRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mLayoutRecyclerView.addItemDecoration(divider);

        ItemClickSupport.addTo(mLayoutRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        Layout layout = mLayoutAdapter.getLayouts().get(position);
                        if (layout.getCodigo() != null) {
                            mOnLayoutClickListener.onLayoutClick(layout);
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
        if (mLayoutFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mLayoutFilter);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.search_menu, menu);
        inflater.inflate(R.menu.filter_menu, menu);
        inflater.inflate(R.menu.cancel_filter_menu, menu);

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
                if (mLayoutFilter == null) {
                    mLayoutFilter = new LayoutFilter();
                }
                findLayouts();
                return true;
            }
        });

        setFilterMenuIcon();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.menu_filter:
                openFilter();
                return true;
            case R.id.menu_cancel_filter:
                mLayoutFilter = new LayoutFilter();
                findLayouts();
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
                    findLayouts(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(Object[] objects) {
        mLayout = (List<Layout>) objects[0];

        if(groupList == null) {
            List<String> list = new ArrayList<>();

            String group = "";
            for (Layout layout : mLayout) {
                if (!layout.getGrupo().trim().equals(group.trim())) {
                    group = layout.getGrupo();
                    list.add(group);
                }
            }

            groupList = list.toArray(new String[list.size()]);

        }

        mLayoutAdapter = new LayoutAdapter(mLayout, getContext());
        mLayoutRecyclerView.setAdapter(mLayoutAdapter);

        if (mLayoutAdapter.getLayouts().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mLayoutRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mLayoutRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLayoutClickListener) {
            mOnLayoutClickListener = (OnLayoutClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnClienteClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnLayoutClickListener = null;
    }

    private void findLayouts() {
        findLayouts(null);
    }

    private void findLayouts(Intent data) {
        try {
            mLayoutAdapter = new LayoutAdapter(new ArrayList<>(), getContext());
            mLayoutRecyclerView.setAdapter(mLayoutAdapter);

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mLayoutFilter = (LayoutFilter) data.getSerializableExtra(
                        ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mLayoutRecyclerView.setVisibility(View.VISIBLE);
            }

            reloadLayout();
            mLayoutAdapter.addLoadingFooter();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadLayout() {
        mLayoutAdapter.resetData();
        loadLayoutPaginacao(true);
    }

    private void loadLayoutPaginacao(boolean isStartLoading) {
        this.mIsStartLoading = isStartLoading;
        if (mAsyncTaskLayout != null) {
            mAsyncTaskLayout.cancel(true);
        }
        mAsyncTaskLayout = new AsyncTaskLayout(getActivity(), mLayoutFilter, mNome, false, this);
        mAsyncTaskLayout.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mLayoutFilter == null || mLayoutFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mLayoutFilter == null || mLayoutFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }
    }


    private void openFilter() {

        if(groupList == null)
            return;


        Intent filterIntent = new Intent(getActivity(), LayoutFilterActivity.class);
        if (mLayoutFilter != null) {
            filterIntent.putExtra(LayoutFilterActivity.FILTER_RESULT_DATA_KEY,
                    mLayoutFilter);
        }

        filterIntent.putExtra(LayoutFilterActivity.FILTER_REGIAO_DATA_KEY,
                groupList);
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

    public interface OnLayoutClickListener {
        void onLayoutClick(Layout layout);
    }

}
