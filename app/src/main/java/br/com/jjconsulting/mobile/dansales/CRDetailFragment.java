package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.CRDetailAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskCRDetail;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.util.DetailImage;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.PicassoCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class CRDetailFragment extends BaseFragment implements View.OnClickListener, AsyncTaskCRDetail.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final String KEY_LAYOUT_RESULT_STATE = "layout_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskCRDetail mAsyncTaskCRDetail;
    private List<ItensListSortimento> mItensListSortimento;
    private LayoutFilter mLayoutFilter;

    private RecyclerView mCRDetailRecyclerView;
    private CRDetailAdapter mCRDetailAdapter;

    private ViewGroup buttonsViewGroup;
    private LinearLayout mListEmptyLinearLayout;
    private LinearLayout mCRDetailLinearLayout;
    private ImageView mProductImageView;
    private TextView mLayoutTextView;
    private Button mButtonInfo;
    private Button mButtonReport;

    private Layout layout;
    private Menu mMenu;

    private String mNome;

    private boolean isShowButton;

    private Gson gson;

    public CRDetailFragment() {
        gson = new Gson();
    }


    public static CRDetailFragment newInstance(Layout layout, boolean isShowButton) {
        CRDetailFragment crDetailFragment = new CRDetailFragment();
        crDetailFragment.setLayout(layout);
        crDetailFragment.setShowButton(isShowButton);
        return crDetailFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_cr_detail, container, false);

        // if is there any data sent, get the filter
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(KEY_FILTER_RESULT_STATE)) {
                mLayoutFilter = (LayoutFilter) savedInstanceState.getSerializable(KEY_FILTER_RESULT_STATE);
            }

            if (savedInstanceState.containsKey(KEY_LAYOUT_RESULT_STATE)) {
                layout = (Layout) savedInstanceState.getSerializable(KEY_LAYOUT_RESULT_STATE);
            }
        }

        if(layout != null){

            getActivity().setTitle(layout.getNome());

            mCRDetailRecyclerView = view.findViewById(R.id.clientes_recycler_view);
            mCRDetailLinearLayout = view.findViewById(R.id.cr_detail_linear_layout);
            mProductImageView = view.findViewById(R.id.product_image_view);
            mProductImageView.setOnClickListener(this);
            mLayoutTextView = view.findViewById(R.id.layout_text_view);

            mButtonInfo = view.findViewById(R.id.info_button);
            mButtonInfo.setOnClickListener(this);
            mButtonReport = view.findViewById(R.id.report_button);
            mButtonReport.setOnClickListener(this);
            buttonsViewGroup = view.findViewById(R.id.buttons_linear_layout);

            mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);

            mCRDetailRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            DividerItemDecoration divider = new DividerItemDecoration(mCRDetailRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
            mCRDetailRecyclerView.addItemDecoration(divider);

            mCRDetailRecyclerView.setNestedScrollingEnabled(false);
            mCRDetailRecyclerView.setHasFixedSize(false);

            init();

        } else {
            getActivity().finish();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mLayoutFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mLayoutFilter);
        }

        if(layout != null){
            outState.putSerializable(KEY_LAYOUT_RESULT_STATE, layout);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);

        try {
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
                    findItensSortimento();
                    return true;
                }
            });

        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }
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
                findItensSortimento();
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
                    findItensSortimento(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<ItensListSortimento> itensSortimento) {
        showItensLoading(true);
        mItensListSortimento = itensSortimento;

        mCRDetailAdapter = new CRDetailAdapter(mItensListSortimento, getContext());
        mCRDetailRecyclerView.setAdapter(mCRDetailAdapter);

        if (mCRDetailAdapter.getLayouts().size() == 0) {
            showListItens(false);
        } else {
            showListItens(true);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.info_button:
                startActivity(CRActivity.newIntent(getContext()));
                break;
            case R.id.report_button:
                startActivity(CRReportActivity.newIntent(getContext(), getLayout()));
                break;
            case R.id.product_image_view:
                startActivity(DetailImage.newIntent(getContext(), "/images/planograma/" + layout.getCodigo() +  ".png", "", "#FFFFFF", true));
                break;
        }
    }

    private void init(){
        showItensLoading(false);
        mLayoutTextView.setText(layout.getCodigo() + " - " + layout.getNome());
        PicassoCustom.setImage(getContext(), "/images/planograma/" + layout.getCodigo() +  ".png", mProductImageView);
        findItensSortimento();
    }

    private void findItensSortimento() {
        findItensSortimento(null);
    }

    private void findItensSortimento(Intent data) {
        try {
            mCRDetailAdapter= new CRDetailAdapter(new ArrayList<>(), getContext());
            mCRDetailRecyclerView.setAdapter(mCRDetailAdapter);

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mLayoutFilter = (LayoutFilter) data.getSerializableExtra(
                        ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

                showListItens(true);
            }

            reloadSortimento();
            mCRDetailAdapter.addLoadingFooter();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void reloadSortimento() {
        mCRDetailAdapter.resetData();
        loadSortimentoPaginacao();
    }

    private void loadSortimentoPaginacao() {
        if (mAsyncTaskCRDetail != null) {
            mAsyncTaskCRDetail.cancel(true);
        }

        mAsyncTaskCRDetail = new AsyncTaskCRDetail(getActivity(), layout.getCodigo(),mNome, this);
        mAsyncTaskCRDetail.execute();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mLayoutFilter == null || mLayoutFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);
        }
    }

    private void openFilter() {

        Intent filterIntent = new Intent(getActivity(), LayoutFilterActivity.class);
        if (mLayoutFilter != null) {
            filterIntent.putExtra(LayoutFilterActivity.FILTER_RESULT_DATA_KEY,
                    mLayoutFilter);
        }

        filterIntent.putExtra(LayoutFilterActivity.FILTER_REGIAO_DATA_KEY,
                "");
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void showListItens(boolean value){
        mListEmptyLinearLayout.setVisibility(value ? View.GONE:View.VISIBLE);
        mCRDetailLinearLayout.setVisibility(value ? View.VISIBLE:View.GONE);
    }

    private void showItensLoading(boolean value){
        mProductImageView.setVisibility(value ? View.VISIBLE:View.GONE);
        mLayoutTextView.setVisibility(value ? View.VISIBLE:View.GONE);

        if(value){
            buttonsViewGroup.setVisibility(isShowButton ? View.VISIBLE:View.GONE);
        }else {
            buttonsViewGroup.setVisibility(View.GONE);
        }
    }

    private void showLegend() {
        Dialog mDialogSubtitles = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialogSubtitles.setCancelable(true);
        mDialogSubtitles.setContentView(R.layout.dialog_legenda_cliente);
        TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
        tvOkSubTitles.setOnClickListener(v -> mDialogSubtitles.dismiss());
        mDialogSubtitles.show();
    }

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public boolean isShowButton() {
        return isShowButton;
    }

    public void setShowButton(boolean showButton) {
        isShowButton = showButton;
    }
}
