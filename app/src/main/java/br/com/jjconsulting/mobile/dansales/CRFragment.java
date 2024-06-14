package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.CRAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskLayout;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.data.LayoutFilter;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.ComplementoSortimento;
import br.com.jjconsulting.mobile.dansales.model.Layout;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.CustomAPI;
import br.com.jjconsulting.mobile.dansales.util.ManagerAttachment;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class CRFragment extends BaseFragment implements AsyncTaskLayout.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;

    private AsyncTaskLayout mAsyncTaskLayout;
    private List<Layout> mLayout;
    private List<ComplementoSortimento> mSortimentoComplemento;

    private LayoutFilter mLayoutFilter;
    private CRAdapter mLayoutAdapter;

    private ExpandableListView mCRExpandableListView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private Menu mMenu;

    private String mNome;
    private String[] grupoList;

    public CRFragment() {
    }


    public static CRFragment newInstance() {
        return new CRFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        View view = inflater.inflate(R.layout.fragment_cr, container, false);

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mLayoutFilter = (LayoutFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        mCRExpandableListView = view.findViewById(R.id.cr_expandable_list_view);

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(viewListEmpty -> {
            openFilter();
        });


        mCRExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Layout layout =  (Layout) mLayoutAdapter.getChild(groupPosition, childPosition);
                startActivity(CRDetailActivity.newIntent(getContext(), layout, false));
                return false;
            }
        });

        findLayouts();

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
        mSortimentoComplemento = (List<ComplementoSortimento>) objects[1];

        if(grupoList == null) {
            List<String> list = new ArrayList<>();

            String grupo = "";
            for (Layout layout : mLayout) {
                if (!layout.getGrupo().trim().equals(grupo.trim())) {
                    grupo = layout.getGrupo();
                    list.add(grupo);
                }
            }

            grupoList = list.toArray(new String[list.size()]);

            addFooter();

        }


        mLayoutAdapter = new CRAdapter(getContext(), mLayout);
        mCRExpandableListView.setAdapter(mLayoutAdapter);

        mCRExpandableListView.post(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isNullOrEmpty(mNome)){
                    for(int ind = 0; ind < grupoList.length; ind++){
                        try {
                           if(!mCRExpandableListView.isGroupExpanded(ind)){
                               mCRExpandableListView.expandGroup(ind);
                           }
                        }catch (Exception ex){

                        }
                    }
                }

                mLayoutAdapter.notifyDataSetChanged();

            }
        });


        mListEmptyLinearLayout.setVisibility(View.GONE);
        mCRExpandableListView.setVisibility(View.VISIBLE);
    }

    private void findLayouts() {
        findLayouts(null);
    }

    private void findLayouts(Intent data) {
        try {

            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mLayoutFilter = (LayoutFilter) data.getSerializableExtra(
                        ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mCRExpandableListView.setVisibility(View.VISIBLE);
            }

            loadLayout();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void loadLayout() {
        if (mAsyncTaskLayout != null) {
            mAsyncTaskLayout.cancel(true);
        }
        mAsyncTaskLayout = new AsyncTaskLayout(getActivity(), mLayoutFilter, mNome, true, this);
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

    private void addFooter(){
        if(mCRExpandableListView != null && getContext() != null){
            LayoutInflater inflaterBase = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewBase = inflaterBase.inflate(R.layout.item_base_complemento, null, false);

            if(mSortimentoComplemento != null && mSortimentoComplemento.size() > 0){
                int index = 0;
               for(ComplementoSortimento item: mSortimentoComplemento){

                   ViewGroup base = viewBase.findViewById(R.id.base_complemento_linear_layout);

                   LayoutInflater inflater = (LayoutInflater) getContext()
                           .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   View view = inflater.inflate(R.layout.item_complemento, null, false);
                   view.setTag(item.getId());
                   view.setId(index);

                   LinearLayout line = view.findViewById(R.id.line_top_complemento_linear_layout);
                   line.setVisibility(index == 0 ? View.VISIBLE:View.GONE);

                   TextView textView = view.findViewById(R.id.complemento_title_text_view);
                   textView.setText(item.getTitulo());

                   TextView subtextView = view.findViewById(R.id.complemento_subtitle_text_view);
                   subtextView.setText(item.getSubtitulo());

                   view.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           int id = v.getId();

                           String URL = BuildConfig.URL_SITE  +  "/" + CustomAPI.API_ATTACH_CR + "?id=" + v.getTag().toString() +
                                   "&filename=" + mSortimentoComplemento.get(id).getArquivo();

                           ManagerAttachment managerAttachment = new ManagerAttachment(getContext(), true);
                           managerAttachment.setTitleDetail(mSortimentoComplemento.get(id).getTitulo());
                           managerAttachment.checkAndOpenFile(URL,  mSortimentoComplemento.get(id)
                               .getArquivo(), String.valueOf(id), getContext());

                       }
                   });

                   base.addView(view);

                   index++;

               }


                mCRExpandableListView.addFooterView(viewBase);

            }

        }
    }


    private void openFilter() {
        if(grupoList == null)
            return;

        Intent filterIntent = new Intent(getActivity(), LayoutFilterActivity.class);
        if (mLayoutFilter != null) {
            filterIntent.putExtra(LayoutFilterActivity.FILTER_RESULT_DATA_KEY,
                    mLayoutFilter);
        }

        filterIntent.putExtra(LayoutFilterActivity.FILTER_REGIAO_DATA_KEY, grupoList);
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

}
