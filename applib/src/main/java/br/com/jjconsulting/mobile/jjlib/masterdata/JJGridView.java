package br.com.jjconsulting.mobile.jjlib.masterdata;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.R;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.connection.SyncFieldsConnection;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Action.*;
import br.com.jjconsulting.mobile.jjlib.dao.entity.DataItemValue;
import br.com.jjconsulting.mobile.jjlib.dao.entity.ElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.Factory;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElement;
import br.com.jjconsulting.mobile.jjlib.dao.entity.FormElementField;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TFilter;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TFormComponent;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TIcon;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TLoadingDataType;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TPageState;
import br.com.jjconsulting.mobile.jjlib.model.DataItem;
import br.com.jjconsulting.mobile.jjlib.model.DataTable;
import br.com.jjconsulting.mobile.jjlib.model.RetFields;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.JJSDK;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.Mask.CurrencyMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.Mask.NumberMaskUtil;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class JJGridView extends JJBaseFragment {

    public static final int KEY_FORM_VIEW = 99;

    private static final String keyName = "KEY_NAME";

    private AsyncTaskDataLoading asyncTaskDataLoading;

    private SyncFieldsConnection syncFieldsConnection;

    private JJOnUpdate jjOnUpdate;

    private Factory factory;

    private FormElement mFormElement;

    private FieldManager fieldManager;

    private JJGridAdapter mJJGridAdapter;

    private RecyclerView mGridViewRecyclerView;

    private JJTabContentView.OnFinish onFinish;

    private Menu mMenu;

    List<BasicAction> gridActions;
    List<BasicAction> toolBarActions;

    private ArrayList<DataTable> mDataTable;

    private ArrayList<DataItemValue> mLegendInfo;

    private JJPopMenuView jjPopMenuView;

    private LinearLayout mListEmptyLinearLayout;
    private LinearLayout mContainerLinearLayout;
    private LinearLayout mListProgressLinearLayout;

    private FloatingActionButton fab;

    private ImageView arrowAddImageView;

    private TextView mMessageTextView;

    private Hashtable mFilter;
    private Hashtable mFilterSearch;
    private Hashtable relationValues;

    private boolean showTitle;
    private boolean showMenuActionBar;
    private boolean mIsStartLoading;
    private boolean isAutoReloadFormFields;
    private boolean isTab;
    private boolean isDialogFilterShow;

    private int indexOffSet;

    private String messageEmpty;

    public static JJGridView renderFragment(Context context, String elementName) {
        Factory factory = new Factory(context);
        FormElement e = factory.getFormElement(elementName);

        return renderFragment(e);
    }


    public static JJGridView renderFragment(FormElement element) {
        JJGridView fragment = new JJGridView();
        fragment.showTitle = true;
        fragment.showMenuActionBar = true;
        fragment.setElement(element);

        try {
            if (!JJSDK.isInitialize()) {
                throw new Exception(fragment.getContext().getString(R.string.sdk_error), new Throwable(""));
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }

        return fragment;
    }


    public JJGridView() {
        indexOffSet = 1;
    }


    @Override
    public void onResume() {
        super.onResume();

        if (isAutoReloadFormFields && !isDialogFilterShow) {

            if (jjOnUpdate != null) {
                jjOnUpdate.onUpdate();
            }

            reloadGrid();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            fieldManager = new FieldManager();
            factory = new Factory(getActivity());

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                mFormElement = factory.getFormElement(bundle.getString(keyName));
            }


            createTitle();
        }catch (Exception ex){

        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        if (showMenuActionBar) {
            mMenu = menu;

            boolean isContainsLegend = false;
            boolean isContainsFilter = false;

            for (ElementField elementField : mFormElement.getFormFields()) {
                if (elementField.getElementFilter() != null) {
                    isContainsFilter = true;
                }

                if (elementField.getDataItem().showImageLegend) {
                    mLegendInfo = elementField.getDataItem().getItens();
                    isContainsLegend = true;
                }
            }

            if (isContainsFilter && getFilterAction().isVisible()) {
                inflater.inflate(R.menu.filter_menu, menu);
                inflater.inflate(R.menu.filter_menu_clear, menu);

                if (TLoadingDataType.fromInteger(getElement().getMode()) == TLoadingDataType.OFFLINE) {
                    inflater.inflate(R.menu.search_menu, menu);

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
                            mFilter = null;
                            if (newText.length() == 0) {
                                if (mFilterSearch != null) {
                                    reloadData();
                                    mFilterSearch = null;
                                }
                            } else {
                                mFilterSearch = new Hashtable();
                                for (FormElementField item : getElement().getFormfields()) {
                                    if (item.getElementFilter() != null && item.getElementFilter().getType() != TFilter.NONE) {
                                        mFilterSearch.put(item.getFieldname(), newText);
                                    }
                                }

                                fastSearch(mFilterSearch);
                            }

                            setFilterMenuIcon();

                            return true;
                        }
                    });
                }
            }

            if (isContainsLegend && getLegendAction().isVisible()) {
                inflater.inflate(R.menu.menu_legend, menu);
            }

            for (int ind = 0; ind < getToolBarActions().size(); ind++) {
                BasicAction action = getToolBarActions().get(ind);
                if (action instanceof SqlCommandAction ||
                        action instanceof InternalAction ||
                        action instanceof UrlRedirectAction ||
                        action instanceof InsertAction) {
                    if (action.isVisible()) {
                        menu.add(1, ind, ind, action.getToolTip());
                    }
                }
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.menu_filter) {
                showFilter();
                return false;
            } else if (item.getItemId() == R.id.action_filter_clear) {
                mFilter = null;
                setFilterMenuIcon();
                reloadData();
                return false;
            } else if (item.getItemId() == R.id.action_legend) {
                showLegend();
                return false;
            } else if (item.getItemId() == R.id.action_search) {
                return super.onOptionsItemSelected(item);
            } else {
                executeToolBarAction(getToolBarActions().get(item.getItemId()));
                return false;
            }

        } catch (Exception ex) {
            getActivity().finish();
            return false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);
        getActivity().invalidateOptionsMenu();

        RelativeLayout baseRelativeLayout = (RelativeLayout) inflater.inflate(R.layout.base_linear_layout, container, false);
        mContainerLinearLayout = baseRelativeLayout.findViewById(R.id.content_linear_layout);
        mListEmptyLinearLayout = baseRelativeLayout.findViewById(R.id.list_empty_text_view);
        mMessageTextView = mListEmptyLinearLayout.findViewById(R.id.message_empty_text_view);
        arrowAddImageView = mListEmptyLinearLayout.findViewById(R.id.arrow_add_image_view);


        if (mDataTable == null || mDataTable.size() == 0) {
            if (!TextUtils.isNullOrEmpty(messageEmpty)) {
                mMessageTextView.setText(messageEmpty + "\n" + getString(R.string.message_add_new_item));
            } else {
                mMessageTextView.setText(getString(R.string.message_add_new_item));
            }
        }

        mListProgressLinearLayout = baseRelativeLayout.findViewById(R.id.loading_linear_layout);
        mListProgressLinearLayout.setVisibility(View.VISIBLE);

        RelativeLayout containerRelativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams lpContainer = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        containerRelativeLayout.setLayoutParams(lpContainer);

        fab = new FloatingActionButton(getContext());
        fab.setColorFilter(getResources().getColor(R.color.fab_color_text));

        fab.setUseCompatPadding(true);
        RelativeLayout.LayoutParams fabParams = new RelativeLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fabParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        int margin = (int) getResources().getDimension(R.dimen.form_label_margin_vertical);

        fabParams.setMargins(0, 0, 0, margin);
        fab.setLayoutParams(fabParams);

        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));

        mGridViewRecyclerView = new RecyclerView(getActivity());
        final RecyclerView.LayoutParams lpRecyclerView = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        mGridViewRecyclerView.setLayoutParams(lpRecyclerView);
        mGridViewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGridViewRecyclerView.setNestedScrollingEnabled(false);

        DividerItemDecoration divider = new DividerItemDecoration(mGridViewRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        mGridViewRecyclerView.addItemDecoration(divider);

        mGridViewRecyclerView.setBackgroundColor(getResources().getColor(R.color.gridViewBackgroundColor));

        mJJGridAdapter = new JJGridAdapter(getActivity(), new ArrayList<>());
        loadData(true);

        mGridViewRecyclerView.setAdapter(mJJGridAdapter);

        mGridViewRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    InsertAction insertAction = getInsertAction();
                    if (insertAction != null && insertAction.isVisible()) {
                        if (fab.isShown()) {
                            fab.hide();
                        } else {
                            fab.show();
                        }
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = LinearLayoutManager.class.cast(
                        recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount() - 1;
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                boolean endHasBeenReached = lastVisible >= totalItemCount;

                if (totalItemCount > 0 && endHasBeenReached && !mJJGridAdapter.isFinishPagination()) {
                    mGridViewRecyclerView.post(() -> {
                        addIndexOffSet();
                        loadData(false);
                    });
                }

                InsertAction insertAction = getInsertAction();
                if (insertAction != null && insertAction.isVisible()) {
                    if (dy > 0 || dy < 0 && fab.isShown()) {
                        fab.hide();
                    } else {
                        fab.show();

                    }
                }

            }
        });

        ItemClickSupport.addTo(mGridViewRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        boolean isExistsDefault = false;
                        for (BasicAction action : getGridActions()) {
                            if (action.isDefaultOption()) {
                                isExistsDefault = true;
                                if (jjPopMenuView != null) {
                                    jjPopMenuView.dismiss();
                                }
                                executeGridAction(action, position);
                            }
                        }

                        if (!isExistsDefault) {
                            createPopupMenu(v, position);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });


        fab.setOnClickListener(view -> {
            executeToolBarAction(getInsertAction());
        });

        mContainerLinearLayout.addView(mGridViewRecyclerView);
        baseRelativeLayout.addView(fab);

        visible();

        return baseRelativeLayout;
    }

    private void visible() {
        InsertAction insertAction = getInsertAction();
        if (insertAction != null && insertAction.isVisible()) {
            fab.show();
        } else {
            fab.hide();
            arrowAddImageView.setVisibility(View.GONE);
        }
    }


    private void createPopupMenu(View view, int position) {

        List<JJPopMenuView.ListPopupItem> itens = new ArrayList<>();

        int index = 0;

        for (BasicAction item : getGridActions()) {
            if (item.isGroup()) {
                Hashtable values = new Hashtable();

                for (int ind = 0; ind < mJJGridAdapter.getDataTable().get(position).getDataItens().size(); ind++) {
                    DataItem dataTableItem = mJJGridAdapter.getDataTable().get(position).getDataItens().get(ind);
                    FormElementField form = mFormElement.getFormFields().get(ind);
                    if (dataTableItem.getValue() != null) {
                        values.put(form.getFieldname(), dataTableItem.getValue().toString());
                    }
                }

                mJJGridAdapter.mDataTable.get(position).getDataItens();
                if (fieldManager.expression.getBoolValue(item.getVisibleExpression(), item.getName(), TPageState.LIST, values, false)) {
                    itens.add(JJPopMenuView.getInstance(item.getToolTip(), item.getIcon().getValue(), index));
                }
            }

            index++;
        }

        jjPopMenuView = new JJPopMenuView(getContext(), (idItem) -> {
            jjPopMenuView.dismiss();
            executeGridAction(getGridActions().get(idItem), position);
        });

        jjPopMenuView.showListPopupWindow(view, itens);

    }

    private void executeToolBarAction(BasicAction action) {
        if (action == null)
            return;

        if (action instanceof InsertAction) {
            isAutoReloadFormFields = true;
            InsertAction insAction = (InsertAction) action;
            if (!TextUtils.isNullOrEmpty(insAction.getElementNameToSelect())) {
                startActivity(JJPickItemActivity.newIntent(getContext(), insAction.getElementNameToSelect(), getElement().getName()));
            } else {
                if (relationValues != null) {
                    startActivity(JJDataPainelActivity.newIntent(getContext(), getElement().getName(), TPageState.INSERT, relationValues, showTitle, insAction.isReopenForm(), onFinish));
                } else {
                    startActivity(JJDataPainelActivity.newIntent(getContext(), getElement().getName(), null, TPageState.INSERT, showTitle, insAction.isReopenForm(), onFinish));
                }
            }
        } else if (action instanceof SqlCommandAction) {
            //TODO: SqlCommandAction
        } else if (action instanceof UrlRedirectAction) {
            //TODO: UrlRedirectAction
        }

    }

    private void executeGridAction(final BasicAction gridAction, int position) {

        if (!TextUtils.isNullOrEmpty(gridAction.getConfirmationMessage())) {
            dialogCustom.showDialogQuestion(gridAction.getConfirmationMessage(), DialogsCustom.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                @Override
                public void onClickPositive() {
                    eventGridAction(gridAction, position);
                }

                @Override
                public void onClickNegative() {

                }
            });
        } else {
            eventGridAction(gridAction, position);
        }

    }

    private void eventGridAction(BasicAction gridAction, int position) {

        DataTable dataTable = mJJGridAdapter.getDataTable().get(position);

        if (gridAction.getEnableExpression() != null) {
            ExpressionManager expressionManager = new ExpressionManager();
            Factory factory = new Factory(getContext());
            if (!expressionManager.getBoolValue(gridAction.getEnableExpression(), gridAction.getName(), TPageState.LIST, factory.convertDataTableInHastable(dataTable, getElement()), true)) {
                Toast.makeText(getContext(), getString(R.string.action_enable), Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (gridAction instanceof CustomAction) {
            CustomAction customAction = (CustomAction) gridAction;
            customAction.getOnClick().onClick(customAction, mFormElement, dataTable);
        } else if (gridAction instanceof UrlRedirectAction) {
            UrlRedirectAction urlAction = (UrlRedirectAction) gridAction;
            if (urlAction.isUrlAsPopUp()) {
                startActivity(JJWebViewActivity.newIntent(getContext(), urlAction.getUrlRedirect(), mFormElement, mJJGridAdapter.getDataTable().get(position)));
            } else {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(urlAction.getUrlRedirect()));
                startActivity(i);
            }
         } else if (gridAction instanceof ViewAction) {
            if (getElement().getRelations().size() > 0) {
                startActivityForResult(JJTabActivity.newIntent(getActivity(), dataTable, getElement().getName(), TPageState.VIEW), KEY_FORM_VIEW);
            } else {
                startActivity(JJDataPainelActivity.newIntent(getContext(), getElement().getName(), mJJGridAdapter.getDataTable().get(position), TPageState.VIEW, showTitle, false, onFinish));
            }

        } else if (gridAction instanceof EditAction) {
            isAutoReloadFormFields = true;

            if (getElement().getRelations().size() > 0) {
                startActivityForResult(JJTabActivity.newIntent(getActivity(), dataTable, getElement().getName(), TPageState.UPDATE), KEY_FORM_VIEW);
            } else {
                startActivity(JJDataPainelActivity.newIntent(getContext(), getElement().getName(), mJJGridAdapter.getDataTable().get(position), TPageState.UPDATE, showTitle, false, onFinish));
            }

        } else if (gridAction instanceof InsertAction) {
            isAutoReloadFormFields = true;
            InsertAction insertAction = (InsertAction) gridAction;
            startActivity(JJDataPainelActivity.newIntent(getContext(), getElement().getName(), null, TPageState.INSERT, showTitle, insertAction.isReopenForm(), onFinish));
        } else if (gridAction instanceof DeleteAction) {
            delete(position);
        }
    }

    private void delete(int position) {
        try {
            SyncFieldsConnection syncFieldsConnection = new SyncFieldsConnection(getActivity(), new SyncFieldsConnection.ConnectionListener() {
                @Override
                public void onSucess(String response, int typeConnection, InputStreamReader reader) {
                    try {
                        if (TLoadingDataType.fromInteger(mFormElement.getMode()) == TLoadingDataType.OFFLINE) {
                            Factory factory = new Factory(getContext());
                            factory.delete(mFormElement, getSelectedRowFilters(getElement(), mJJGridAdapter.getDataTable().get(position)));
                            reloadGrid();
                        } else {
                            reloadGrid();
                        }
                    } catch (Exception ex) {
                        LogUser.log(Config.TAG, ex.toString());
                        dialogCustom.showDialogMessage(getString(R.string.sync_connection_error), DialogsCustom.DIALOG_TYPE_ERROR, null);
                    }
                }

                @Override
                public void onError(int code, VolleyError volleyError, int typeConnection, String response) {

                    if (Connection.NO_CONNECTION == code) {
                        dialogCustom.showDialogMessage(getString(R.string.title_connection_error), dialogCustom.DIALOG_TYPE_ERROR, null);
                    } else {
                        Gson gson = new Gson();
                        ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                        if (errorConnection != null && errorConnection.getMessage() != null) {
                            dialogCustom.showDialogMessage(errorConnection.getMessage(), dialogCustom.DIALOG_TYPE_ERROR, null);
                        }
                    }

                    dialogCustom.showDialogLoading(false);

                }
            });

            dialogCustom.showDialogLoading(true);
            syncFieldsConnection.deleteField(mFormElement.getName(), getSelectedRowFilters(getElement(), mJJGridAdapter.getDataTable().get(position)), mFormElement);

        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void createTitle() {
        try {
            if (!isTab) {
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

                if (actionBar != null) {
                    if (showTitle) {
                        getActivity().setTitle(mFormElement.getTitle());
                    } else {
                        getActivity().setTitle("");
                    }
                }
            }
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void loadData(boolean isStartLoading) {
        this.mIsStartLoading = isStartLoading;

        if (asyncTaskDataLoading != null) {
            asyncTaskDataLoading.cancel(true);
        }

        asyncTaskDataLoading = new AsyncTaskDataLoading(new OnAsyncResponse() {
            @Override
            public void processFinish(ArrayList<DataTable> objects) {
                mDataTable = objects;
                if (mIsStartLoading) {
                    mJJGridAdapter = new JJGridAdapter(getContext(), mDataTable);
                    mGridViewRecyclerView.setAdapter(mJJGridAdapter);
                } else {
                    mJJGridAdapter.updateData(mDataTable);
                }

                if (mDataTable.size() < Config.SIZE_PAGE) {
                    mJJGridAdapter.setFinishPagination(true);
                }

                setLayoutVisible();

                dialogCustom.showDialogLoading(false);

            }
        });
        asyncTaskDataLoading.execute();

    }

    private void fastSearch(Hashtable searchFilter) {
        resetIndexOffSet();
        mJJGridAdapter.resetData();

        mDataTable = factory.getDataTable(mFormElement, searchFilter == null ? new Hashtable() : searchFilter, getOrderBy(), true, Config.SIZE_PAGE, indexOffSet, 0);
        mJJGridAdapter = new JJGridAdapter(getContext(), mDataTable);
        mGridViewRecyclerView.setAdapter(mJJGridAdapter);
    }

    public void reloadData() {
        resetIndexOffSet();
        mJJGridAdapter.resetData();
        loadData(true);
    }

    private void reloadGrid() {
        reloadData();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon(mFilter == null ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);
        }
    }

    public FormElement getElement() {
        return mFormElement;
    }


    public void setElement(FormElement element) {
        this.mFormElement = element;
    }

    public HashMap<String, String> getSelectedRowFilters(FormElement e, DataTable dataTable) {
        HashMap<String, String> filters = new HashMap<String, String>();

        int index = 0;
        for (ElementField item : e.getFields()) {
            if (item.getIspk()) {
                filters.put(item.getFieldname(), dataTable.getDataItens().get(index).getValue().toString());
            }
            index++;
        }

        return filters;
    }

    public HashMap getSelectedRowId(FormElement e, int position) {
        HashMap values = new HashMap();

        int index = 0;
        for (ElementField item : e.getFields()) {
            if (item.getIspk()) {
                DataTable dataTable = mDataTable.get(position);

                values.put(index, dataTable.getDataItens().get(index).getValue());
            }
            index++;
        }

        return values;
    }


    public boolean isTab() {
        return isTab;
    }

    public void setTab(boolean tab) {
        isTab = tab;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }


    public Hashtable getRelationValues() {
        return relationValues;
    }

    public void setRelationValues(Hashtable relationValues) {
        this.relationValues = relationValues;
    }

    public boolean isShowMenuActionBar() {
        return showMenuActionBar;
    }

    public void setShowMenuActionBar(boolean showMenuActionBar) {
        this.showMenuActionBar = showMenuActionBar;
    }

    public String getMessageEmpty() {
        return messageEmpty;
    }

    public void setMessageEmpty(String messageEmpty) {
        this.messageEmpty = messageEmpty;
    }

    public void resetIndexOffSet() {
        this.indexOffSet = 1;
    }

    public void addIndexOffSet() {
        this.indexOffSet += 1;
    }

    public JJOnUpdate getJJOnUpdate() {
        return jjOnUpdate;
    }

    public void setJJOnUpdate(JJOnUpdate jjOnUpdate) {
        this.jjOnUpdate = jjOnUpdate;
    }

    public void showFilter() {

        android.app.FragmentManager fm = getActivity().getFragmentManager();
        JJFilterDialogFragment jjValidationDialogFragment =
                JJFilterDialogFragment.newInstance(mFormElement.getName(), mFilter);

        jjValidationDialogFragment.setOnFinishValidation(filter -> {
            isDialogFilterShow = false;
            mFilter = filter;
            setFilterMenuIcon();
            reloadData();
        });

        jjValidationDialogFragment.show(getActivity().getFragmentManager(), "");
        fm.executePendingTransactions();
        isDialogFilterShow = true;
    }

    public void showLegend() {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        JJLegendView jjFormLegendView =
                JJLegendView.newInstance(mLegendInfo);
        jjFormLegendView.show(getFragmentManager(), "");
        fm.executePendingTransactions();
    }


    private String getOrderBy() {
        String orderby = "";

        for (FormElementField formElementField : mFormElement.getFormFields()) {

            if (formElementField.getIspk()) {
                if (orderby.length() == 0) {
                    orderby = formElementField.getFieldname() + " ASC ";
                } else {
                    orderby += ", " + formElementField.getFieldname() + " ASC ";

                }
            }
        }
        return orderby;
    }

    public Hashtable currentFilter() {
        if (relationValues != null) {
            if (mFilter == null) {
                return relationValues;
            } else {
                Hashtable filterTemp = mFilter;

                for (Object key : relationValues.keySet()) {
                    if (relationValues.containsKey(key)) {
                        filterTemp.remove(key);
                    }
                    filterTemp.put(key, relationValues.get(key));
                }

                return filterTemp;
            }
        } else {
            return (mFilter == null || mFilter.isEmpty()) ? null : mFilter;
        }
    }

    public void setFilter(Hashtable filter){
        mFilter = new Hashtable();
        mFilter = filter;
    }


    private class JJGridAdapter extends RecyclerView.Adapter<JJGridAdapter.ViewHolder> {
        private static final int ITEM = 0;
        private static final int LOADING = 1;
        private Context mContext;
        private ArrayList<DataTable> mDataTable;
        private boolean isLoadingAdded;
        private boolean finishPagination;

        public JJGridAdapter(Context context, ArrayList<DataTable> dataTable) {
            mContext = context;
            mDataTable = dataTable;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = null;

            switch (viewType) {
                case ITEM:

                    List<BasicAction> actions = getGridActions();

                    if (actions.size() > 0) {
                        boolean isDefaultOptions = false;

                        for (BasicAction item : actions) {
                            if (item.isDefaultOption()) {
                                isDefaultOptions = true;
                            }
                        }

                        if (isDefaultOptions) {
                            view = inflater.inflate(R.layout.jj_item_grid_view_selectable_item, parent, false);
                        } else {
                            view = inflater.inflate(R.layout.jj_item_grid_view, parent, false);
                        }
                    } else {
                        view = inflater.inflate(R.layout.jj_item_grid_view, parent, false);
                    }


                    view.setId(viewType);
                    break;
                case LOADING:
                    view = inflater.inflate(R.layout.jj_item_progress, parent, false);
                    view.setId(viewType);
                    break;

            }

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            switch (getItemViewType(position)) {

                case ITEM:
                    DataTable dataTable = mDataTable.get(position);

                    if (dataTable != null && dataTable.getDataItens() != null) {

                        String content = "";
                        holder.mContainerItemLinearLayout.removeAllViews();

                        //Cria Layout para textos da Grid
                        LinearLayout textLinearLayout = new LinearLayout(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        params.setMargins((int) mContext.getResources().getDimension(R.dimen.margin_icon), 0, 0, 0);
                        textLinearLayout.setLayoutParams(params);
                        textLinearLayout.setOrientation(LinearLayout.VERTICAL);

                        for (int ind = 0; ind < dataTable.getDataItens().size(); ind++) {
                            DataItem dataTableItem = dataTable.getDataItens().get(ind);
                            FormElementField form = mFormElement.getFormFields().get(ind);

                            //Verifica se campo está visivel ou não (Apenas uma vez)
                            if (!dataTableItem.isSetVisibleEnable()) {
                                boolean visible = fieldManager.isVisible(mFormElement.getFormFields().get(ind), TPageState.LIST, null);

                                dataTableItem.setSetVisibleEnable(true);
                                dataTableItem.setVisible(visible);
                                dataTableItem.setEnable(false);

                                dataTable.getDataItens().set(ind, dataTableItem);
                            }

                            if (dataTable.getDataItens().get(ind).isVisible() || form.getDataItem().showImageLegend) {

                                if (dataTableItem.getValue() != null) {

                                    String value = "";

                                    if (form.getComponent() == TFormComponent.CURRENCY.getValue() && !TextUtils.isNullOrEmpty(dataTableItem.getValue().toString())) {
                                        value = CurrencyMaskUtil.convertSimpleText(dataTableItem.getValue().toString());
                                    } else if (form.getNumberOfDecimalPlaces() < 1) {
                                        value = dataTableItem.getValue().toString();
                                    } else {
                                        value = NumberMaskUtil.getNumberOfDecimalPlaces(dataTableItem.getValue().toString(), form.getNumberOfDecimalPlaces(), true);
                                    }

                                    TFormComponent tFormComponent = TFormComponent.fromInteger(form.getComponent());
                                    String label = "";
                                    switch (tFormComponent) {
                                        case COMBOBOX:
                                            try {
                                                for (DataItemValue dataItemValue : form.getDataItem().getItens()) {
                                                    if (dataItemValue.id.equals(value)) {
                                                        label = dataItemValue.description;
                                                    }
                                                }

                                                if (!TextUtils.isNullOrEmpty(value) && TextUtils.isNullOrEmpty(label)) {
                                                    label = value;
                                                }
                                            } catch (Exception ex) {
                                                LogUser.log(Config.TAG, ex.toString());
                                            }

                                            break;
                                        default:
                                            label = value;
                                            break;

                                    }

                                    content = "<b>" + form.getLabel() + ": " + "</b>" + label;
                                } else {
                                    content = "<b>" + form.getLabel() + ": ";
                                }

                                Activity activity = (Activity) mContext;
                                LayoutInflater inflater = activity.getLayoutInflater();

                                View view = inflater.inflate(R.layout.jj_item_text_view, null);
                                TextView mDescription = view.findViewById(R.id.description_text_view);

                                mDescription.setId(ind);
                                mDescription.setText(Html.fromHtml(content));

                                if (form.getDataItem().showImageLegend) {
                                    try {
                                        DataItemValue dataItemValue = DataItemValue.getContainsDataItemValue(form.getDataItem().getItens(), dataTableItem.getValue().toString());
                                        TIcon icon = (TIcon.values()[dataItemValue.icon]);

                                        JJIcon jjIcon = new JJIcon(mContext, icon, dataItemValue.imageColor);
                                        holder.mContainerItemLinearLayout.addView(jjIcon.renderView());
                                    } catch (Exception ex) {
                                        LogUser.log(Config.TAG, ex.toString());
                                    }

                                    if (form.getDataItem().replaceTextOnGrid) {
                                        mDescription.setVisibility(View.GONE);
                                    } else {
                                        mDescription.setVisibility(View.VISIBLE);
                                    }
                                }

                                textLinearLayout.addView(mDescription);
                            }

                        }

                        holder.mContainerItemLinearLayout.addView(textLinearLayout);
                        holder.mContainerMenuLinearLayout.removeAllViews();
                        holder.mContainerMenuIconLinearLayout.removeAllViews();

                        createActionMenu(holder.mContainerMenuIconLinearLayout, holder.mContainerMenuLinearLayout, position);

                    }
                    break;
                case LOADING:

                    break;
            }
        }

        private void createActionMenu(LinearLayout view, LinearLayout viewMenu, int position) {
            List<BasicAction> actions = new ArrayList<>();

            ExpressionManager expressionManager = new ExpressionManager();

            for (BasicAction item : getGridActions()) {
                if (!TextUtils.isNullOrEmpty(item.getEnableExpression())) {
                    if (expressionManager.getBoolValue(item.getVisibleExpression(), item.getName(), TPageState.LIST, factory.convertDataTableInHastable(mDataTable.get(position), getElement()), true)) {
                        actions.add(item);
                    }
                }
            }

            if (actions.size() > 0) {

                LinearLayout iconLinearLayout = new LinearLayout(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins((int) mContext.getResources().getDimension(R.dimen.margin_icon), 30, 0, 10);
                iconLinearLayout.setLayoutParams(params);
                iconLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                iconLinearLayout.setGravity(Gravity.END);
                iconLinearLayout.setVisibility(View.GONE);

                boolean isGroup = false;

                int index = 0;

                if (actions.size() == 1 && actions.get(0).isGroup()) {
                    BasicAction item = actions.get(0);

                    TIcon icon = (TIcon.values()[item.getIcon().getValue()]);

                    JJIcon jjIconMenu = new JJIcon(mContext, icon, String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_menu) & 0xffffff));
                    jjIconMenu.setId(0);

                    jjIconMenu.setOnClickListener((v) -> {
                        int id = v.getId();
                        executeGridAction(actions.get(id), position);
                    });

                    View viewJJIcon = jjIconMenu.renderView();
                    viewMenu.addView(viewJJIcon);
                    viewMenu.setVisibility(View.VISIBLE);
                } else {

                    for (BasicAction item : actions) {

                        if (item.isGroup()) {

                            TIcon icon = (TIcon.values()[200]);
                            JJIcon jjIconMenu = new JJIcon(mContext, icon, String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_menu) & 0xffffff));

                            viewMenu.setOnClickListener((v) -> {
                                createPopupMenu(v, position);
                            });

                            viewMenu.setVisibility(View.VISIBLE);
                            View viewJJIcon = jjIconMenu.renderView();

                            if (!isGroup) {
                                viewMenu.addView(viewJJIcon);
                            }

                            isGroup = true;
                        } else {
                            iconLinearLayout.setOnClickListener((v) -> {
                            });

                            String color = "";
                            boolean isEnable = expressionManager.getBoolValue(item.getEnableExpression(), item.getName(), TPageState.LIST, factory.convertDataTableInHastable(mDataTable.get(position), getElement()), true);

                            if (isEnable) {
                                color = String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_menu) & 0xffffff);
                            } else {
                                color = String.format("#%06x", mContext.getResources().getColor(R.color.action_icon_cancel_menu) & 0xffffff);
                            }

                            TIcon icon = (TIcon.values()[item.getIcon().getValue()]);
                            JJIcon jjIcon = new JJIcon(mContext, icon, color);
                            jjIcon.setId(index);
                            jjIcon.setLargeMargin(true);

                            if(isEnable) {
                                jjIcon.setOnClickListener((v) -> {
                                    int id = v.getId();
                                    executeGridAction(actions.get(id), position);
                                });
                            } else {
                                jjIcon.setOnClickListener((v) -> {
                                    Toast.makeText(getContext(), getString(R.string.action_enable), Toast.LENGTH_SHORT).show();
                                });
                            }

                            View viewJJIcon = jjIcon.renderView();

                            iconLinearLayout.addView(viewJJIcon);
                            iconLinearLayout.setVisibility(View.VISIBLE);

                        }
                        index++;
                    }

                    view.addView(iconLinearLayout);

                    if (!isGroup || actions.size() == 1) {
                        viewMenu.setVisibility(View.GONE);
                    }
                }
            } else {
                viewMenu.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return mDataTable.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout mContainerLinearLayout;
            private LinearLayout mContainerItemLinearLayout;
            private LinearLayout mContainerMenuLinearLayout;
            private LinearLayout mContainerMenuIconLinearLayout;


            public ViewHolder(View itemView) {
                super(itemView);

                switch (itemView.getId()) {
                    case ITEM:
                        mContainerLinearLayout = (LinearLayout) itemView;
                        mContainerItemLinearLayout = mContainerLinearLayout.findViewById(R.id.container_item_linear_layout);
                        mContainerMenuLinearLayout = mContainerLinearLayout.findViewById(R.id.container_menu_group_linear_layout);
                        mContainerMenuIconLinearLayout = mContainerLinearLayout.findViewById(R.id.container_menu_icon_linear_layout);
                        break;
                }
            }
        }

        public boolean isFinishPagination() {
            return finishPagination;
        }

        public void setFinishPagination(boolean finishPagination) {
            this.finishPagination = finishPagination;
        }

        public void resetData() {
            mDataTable.clear();
            finishPagination = true;
        }

        public ArrayList<DataTable> getDataTable() {
            return mDataTable;
        }

        public void add(DataTable dataTable) {
            mDataTable.add(dataTable);
            notifyItemInserted(mDataTable.size() - 1);
        }

        public void updateData(ArrayList<DataTable> listData) {
            if (listData.size() == 0 || listData.size() < Config.SIZE_PAGE) {
                finishPagination = true;
            } else {
                finishPagination = false;

            }
            mDataTable.addAll(listData);

            notifyDataSetChanged();
            LogUser.log(Config.TAG, "Pagination - listClientes size: " + mDataTable.size() + " - page size: " + Config.SIZE_PAGE + " - finishPagination: " + finishPagination);
        }

        public void addLoadingFooter() {
            isLoadingAdded = true;
            //add(new DataTable());
        }

        public void removeLoadingFooter() {
            isLoadingAdded = false;
            int position = mDataTable.size() - 1;
            DataTable item = getItem(position);
            if (item != null) {
                mDataTable.remove(position);
                notifyItemRemoved(position);
            }
        }

        public DataTable getItem(int position) {
            return mDataTable.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            return (position == mDataTable.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    public void setLayoutVisible() {
        mListProgressLinearLayout.setVisibility(View.GONE);
        if (mJJGridAdapter.getDataTable().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mContainerLinearLayout.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mContainerLinearLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Classe efetua o carregamento dos dados da lista (online/offline)
     */
    public class AsyncTaskDataLoading extends AsyncTask<Void, Void, ArrayList<DataTable>> {

        private OnAsyncResponse onAsyncResponse;
        private int mode;

        public AsyncTaskDataLoading(OnAsyncResponse onAsyncResponse) {
            this.onAsyncResponse = onAsyncResponse;
            mode = mFormElement.getMode();

        }

        @Override
        protected ArrayList<DataTable> doInBackground(Void... params) {
            ArrayList<DataTable> objects;
            // 1 - modo online / 0 - modo offline
            if (TLoadingDataType.fromInteger(mode) == TLoadingDataType.OFFLINE) {
                if (mIsStartLoading) {
                    objects = mDataTable = factory.getDataTable(mFormElement, currentFilter(), getOrderBy(), false, Config.SIZE_PAGE, indexOffSet, 0);
                } else {
                    objects = factory.getDataTable(mFormElement, currentFilter(), getOrderBy(), false, Config.SIZE_PAGE, indexOffSet, 0);
                }


            } else {
                objects = null;

                syncFieldsConnection = new SyncFieldsConnection(getActivity(), new SyncFieldsConnection.ConnectionListener() {
                    @Override
                    public void onSucess(String response, int typeConnection, InputStreamReader reader) {
                        try {

                            RetFields retFields = gson.fromJson(response, RetFields.class);
                            onAsyncResponse.processFinish(factory.convertResponseRetFields(retFields, mFormElement));
                        } catch (Exception ex) {
                            LogUser.log(Config.TAG, ex.toString());
                            dialogCustom.showDialogLoading(false);
                        }
                    }

                    @Override
                    public void onError(int code, VolleyError volleyError, int typeConnection, String response) {

                        if (indexOffSet == 1) {
                            setLayoutVisible();

                            if (Connection.NO_CONNECTION == code) {
                                mMessageTextView.setText(getString(R.string.sync_connection_error));
                                arrowAddImageView.setVisibility(View.GONE);
                            } else {
                                ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);

                                if (errorConnection != null && errorConnection.getMessage() != null && errorConnection.getStatus() != ValidationLetter.STATUS_EMPTY) {
                                    mMessageTextView.setText(errorConnection.getMessage());
                                    arrowAddImageView.setVisibility(View.GONE);
                                } else {
                                    InsertAction insertAction = getInsertAction();
                                    if (insertAction != null && insertAction.isVisible()) {
                                        if (!TextUtils.isNullOrEmpty(messageEmpty)) {
                                            mMessageTextView.setText(messageEmpty + "\n" + getString(R.string.message_add_new_item));
                                        } else {
                                            mMessageTextView.setText(getString(R.string.message_add_new_item));
                                        }
                                    } else {
                                        if (!TextUtils.isNullOrEmpty(messageEmpty)) {
                                            mMessageTextView.setText(messageEmpty);
                                        } else {
                                            mMessageTextView.setText(getString(R.string.none));
                                        }
                                    }
                                }
                            }

                            dialogCustom.showDialogLoading(false);

                        }
                    }
                });


                if (indexOffSet == 1) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogCustom.showDialogLoading(true);
                        }
                    });

                }

                syncFieldsConnection.syncField(mFormElement.getName(), currentFilter(), indexOffSet, Config.SIZE_PAGE, 0);


            }
            return objects;
        }

        @Override
        protected void onPostExecute(ArrayList<DataTable> objects) {
            //Listener chamado apenas pelo modo offiline, processFinish
            // é chamado no listener da conexao no modo offiline
            if (TLoadingDataType.fromInteger(mode) == TLoadingDataType.OFFLINE) {
                onAsyncResponse.processFinish(objects);
            }
        }
    }

    public interface OnAsyncResponse {
        void processFinish(ArrayList<DataTable> objects);
    }

    public List<BasicAction> getGridActions() {
        if (gridActions == null) {
            gridActions = new ArrayList<>();
        }
        return gridActions;
    }

    public void addGridAction(CustomAction action) {
        getGridActions().add(action);
    }

    public void addGridAction(SqlCommandAction action) {
        getGridActions().add(action);
    }

    public void addGridAction(UrlRedirectAction action) {
        getGridActions().add(action);
    }

    public void addGridAction(InternalAction action) {
        getGridActions().add(action);
    }

    public void removeGridAction(String name) {
        for (BasicAction basicAction : getGridActions() ) {
            if (basicAction.getName().equals(name)) {
                if (basicAction instanceof CustomAction ||
                    basicAction instanceof UrlRedirectAction ||
                    basicAction instanceof InternalAction) {

                    getGridActions().remove(basicAction);

                } else {
                    throw new IllegalArgumentException("This action can not be removed");
                }
                return;
            }
        }
    }

    public List<BasicAction> getToolBarActions() {
        if (toolBarActions == null) {
            toolBarActions = new ArrayList<>();
            toolBarActions.add(new LegendAction());
            toolBarActions.add(new RefreshAction());
            toolBarActions.add(new FilterAction());
        }

        return toolBarActions;
    }

    public void addToolBarAction(CustomAction action) {
        getToolBarActions().add(action);
    }

    public void addToolBarAction(SqlCommandAction action) {
        getToolBarActions().add(action);
    }

    public void addToolBarAction(UrlRedirectAction action) {
        getToolBarActions().add(action);
    }

    public void addToolBarAction(InternalAction action) {
        getToolBarActions().add(action);
    }

    public void removeToolBarAction(String name) {
        for (BasicAction basicAction : getToolBarActions() ) {
            if (basicAction.getName().equals(name)) {
                if (basicAction instanceof CustomAction ||
                        basicAction instanceof UrlRedirectAction ||
                        basicAction instanceof InternalAction) {

                    getToolBarActions().remove(basicAction);

                } else {
                    throw new IllegalArgumentException("This action can not be removed");
                }
                return;
            }
        }
    }

    private InsertAction getInsertAction() {
        for (BasicAction action : getToolBarActions()) {
            if (action instanceof InsertAction)
                return (InsertAction) action;
        }
        return null;
    }

    public LegendAction getLegendAction() {
        for (BasicAction action : getToolBarActions()) {
            if (action instanceof LegendAction)
                return (LegendAction) action;
        }
        return null;
    }

    public RefreshAction getRefreshAction() {
        for (BasicAction action : getToolBarActions()) {
            if (action instanceof RefreshAction)
                return (RefreshAction) action;
        }
        return null;
    }

    public FilterAction getFilterAction() {
        for (BasicAction action : getToolBarActions()) {
            if (action instanceof FilterAction)
                return (FilterAction) action;
        }
        return null;
    }

    public JJTabContentView.OnFinish getOnFinish() {
        return onFinish;
    }

    public void setOnFinish(JJTabContentView.OnFinish onFinish) {
        this.onFinish = onFinish;
    }
}

