package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.TapListAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.TapConnection;
import br.com.jjconsulting.mobile.dansales.data.TapFilter;
import br.com.jjconsulting.mobile.dansales.database.StatusETapDao;
import br.com.jjconsulting.mobile.dansales.model.TapActionType;
import br.com.jjconsulting.mobile.dansales.model.TapConnectionType;
import br.com.jjconsulting.mobile.dansales.model.TapDetail;
import br.com.jjconsulting.mobile.dansales.model.TapList;
import br.com.jjconsulting.mobile.dansales.model.TapStatus;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionTap;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class TapListActivity extends BaseActivity {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;
    private static final String ARG_TYPE_ACTION = "type_action";

    private List<TapList> mTapList;
    private TapListAdapter mTapListAdapter;
    private TapFilter mTapFilter;

    private Menu mMenu;
    private RecyclerView mETapRecyclerView;
    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private LinearLayout mProgressLinearLayout;
    private RelativeLayout mBaseRelativeLayout;
    private TextView mMessageErrorTextView;
    private FloatingActionButton mAddPedidoFloatingActionButton;

    private String mNome;
    private String mTapCodSelected;

    private TapConnection tapConnection;

    private TapActionType tapActionType;

    private Toast toast;

    private MenuItem menuSendLote;
    private MenuItem menuSendAll;

    private boolean isSelectAll;


    /**
     * Use it to edit an item.
     */
    public static Intent newIntent(Context context, TapActionType tapActionType) {
        Intent intent = new Intent(context, TapListActivity.class);
        intent.putExtra(ARG_TYPE_ACTION, tapActionType.getValue());
        return intent;
    }

    public static TapListActivity newInstance() {
        return new TapListActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CurrentActionTap.getInstance().isUpdateListTap()) {
            CurrentActionTap.getInstance().setUpdateListTap(false);
            findETap();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_etap);

        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mTapFilter = (TapFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        tapActionType = TapActionType.getTapActionType(getIntent().getIntExtra(ARG_TYPE_ACTION, 1));

        mAddPedidoFloatingActionButton = findViewById(R.id.add_tap_floating_action_button);
        mProgressLinearLayout = findViewById(R.id.loading_linear_layout);
        mBaseRelativeLayout = findViewById(R.id.base_relative_layout);
        mMessageErrorTextView = findViewById(R.id.message_error_text_view);
        mETapRecyclerView = findViewById(R.id.list_etap_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mListEmptyImageButton = findViewById(R.id.list_empty_image_button);
        mListEmptyImageButton.setOnClickListener(view -> {
            openFilter();
        });

        findETap();

        mETapRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(mETapRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.custom_divider));
        mETapRecyclerView.addItemDecoration(divider);

        if(tapActionType != TapActionType.TAP_CONSULTA){
            ItemClickSupport.addTo(mETapRecyclerView).setOnItemLongClickListener((recyclerView, position, v) -> {
                if(mTapList.get(position).isAprov()){
                    mTapListAdapter.updateDataItem(mTapList.get(position), position);
                    boolean isEnableMenuSendLote = false;

                    for (TapList item: mTapListAdapter.getListETap()) {
                        if(item.isCheckdLote()){
                            isEnableMenuSendLote = true;
                        }
                    }

                    mTapList.set(position, mTapListAdapter.getListETap().get(position));

                    menuSendLote.setVisible(isEnableMenuSendLote);
                } else {

                    if(toast != null){
                        toast.cancel();
                    }

                    toast = Toast.makeText(this, "Tap não está disponivel para validação", Toast.LENGTH_SHORT);
                    toast.show();
                }

                return  true;
            });
        }

        ItemClickSupport.addTo(mETapRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        loadTap(position);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });


        mAddPedidoFloatingActionButton.setOnClickListener(v -> {
            startActivity(TapPickClienteActivity.newIntent(this));
        });

        if(tapActionType != TapActionType.TAP_LIST){
            mAddPedidoFloatingActionButton.hide();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTapFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mTapFilter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.search_menu, menu);
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        getMenuInflater().inflate(R.menu.cancel_filter_menu, menu);


        getMenuInflater().inflate(R.menu.cliente_label_menu, menu);
        getMenuInflater().inflate(R.menu.edit_tap_lote_menu, menu);

        if(tapActionType != TapActionType.TAP_CONSULTA){
            getMenuInflater().inflate(R.menu.select_all_menu, menu);
        }

        menuSendLote = menu.findItem(R.id.action_save_lote);
        menuSendAll = menu.findItem(R.id.action_select_all);

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
                if (mTapFilter == null) {
                    mTapFilter = new TapFilter();
                }
                findETap();
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
                mTapFilter = new TapFilter();
                findETap();
                setFilterMenuIcon();
                return true;
            case R.id.action_legendas:
                showLegendTap();
                return true;
            case R.id.action_save_lote:
                dialogsDefault.showDialogQuestion(getString(R.string.message_etap_send_lote_question), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        sendLoteTap();
                    }

                    @Override
                    public void onClickNegative() {

                    }
                });

                return true;
            case R.id.action_select_all:
                ArrayList<TapList> tapListTemp = new ArrayList<>();
                int count = 0;


                for (TapList itemTap : mTapList) {
                    if (itemTap.isAprov()) {
                        if(!isSelectAll){
                            itemTap.setCheckdLote(true);
                        } else {
                            itemTap.setCheckdLote(false);

                        }
                        count++;
                    }

                    tapListTemp.add(itemTap);
                }

                mTapList = tapListTemp;
                mTapListAdapter.updateData(mTapList);

                if (toast != null) {
                    toast.cancel();
                }

                String message = "";
                if(!isSelectAll){
                    menuSendAll.setTitle(getString(R.string.remove_all));
                    message = getString(R.string.tap_qtd_selected_tap) + ": " + count;
                    if(count > 0){
                        menuSendLote.setVisible(true);
                    }

                } else {
                    menuSendLote.setVisible(false);
                    menuSendAll.setTitle(getString(R.string.select_all));
                    message = getString(R.string.tap_qtd_remove_selected_tap) + ": " + count;
                }

                isSelectAll = !isSelectAll;
                toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
                toast.show();
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
                    findETap(data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void findETap() {
        findETap(null);
    }

    private void findETap(Intent data) {
        try {
            mTapListAdapter = new TapListAdapter(this, new ArrayList<>(), tapActionType);
            mETapRecyclerView.setAdapter(mTapListAdapter);

            // if is there any data sent, get the filter
            if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mTapFilter = (TapFilter) data.getSerializableExtra(
                        TapListFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mETapRecyclerView.setVisibility(View.VISIBLE);
            }

            mTapListAdapter.resetData();
            loadETap();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void loadETap() {
        tapConnection = new TapConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                showProgress(false);
                switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                    case TAP_LIST:
                        try {
                            mTapList = gson.fromJson(response, new TypeToken<List<TapList>>() {
                            }.getType());

                            mTapListAdapter = new TapListAdapter(getBaseContext(), mTapList, tapActionType);
                            mETapRecyclerView.setAdapter(mTapListAdapter);

                            if (mTapList.size() == 0) {
                                mListEmptyLinearLayout.setVisibility(View.VISIBLE);
                                mETapRecyclerView.setVisibility(View.GONE);
                            }
                        } catch (Exception ex){
                            dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                        }
                        break;
                    case TAP_DETAIL:
                        try{

                            TapDetail mTapDetail = gson.fromJson(response, TapDetail.class);

                            if (TextUtils.isNullOrEmpty(mTapDetail.getDescErr())) {
                                mTapDetail.setTapCod(mTapCodSelected);
                                startActivity(TapPedidoDetailActivity.newIntent(TapListActivity.this, mTapDetail, tapActionType));
                            } else {
                                dialogsDefault.showDialogMessage(mTapDetail.getDescErr(), dialogsDefault.DIALOG_TYPE_ERROR, null);
                            }
                        } catch (Exception ex){
                            dialogsDefault.showDialogMessage(getString(R.string.title_connection_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                        }
                        break;
                    case TAP_LOTE:
                        try {
                            dialogsDefault.showDialogMessage(getString(R.string.tap_apro_lib_msg_sucess), dialogsDefault.DIALOG_TYPE_SUCESS, () -> {
                                menuSendLote.setVisible(false);
                                findETap();
                            });
                        } catch (Exception ex) {
                            dialogsDefault.showDialogMessage(getString(R.string.message_etap_add_item_error), dialogsDefault.DIALOG_TYPE_ERROR, null);
                        }
                        break;
                }
            }

            @Override
            public void onError(VolleyError volleyError,  int code, int typeConnection, String response) {
                showProgress(false);

                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                    ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(TapListActivity.this, errorConnection.getMessage())){
                        finish();
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                switch (TapConnectionType.getTapConnectionType(typeConnection)) {
                    case TAP_LIST:
                        dialogsDefault.showDialogMessage(getString(R.string.message_etap_listl_erro), dialogsDefault.DIALOG_TYPE_ERROR, new DialogsCustom.OnClickDialogMessage() {
                            @Override
                            public void onClick() {
                                finish();
                            }
                        });
                        break;
                    case TAP_DETAIL:
                        showMessageError(getString(R.string.message_etap_detail_erro));
                        break;
                    case TAP_DELETE:
                        showMessageError(getString(R.string.message_etap_delete_erro));
                        break;
                    case TAP_LOTE:
                        showMessageError(getString(R.string.message_etap_lote_erro));
                        break;
                }
            }
        });

        showProgress(true);

        Current current = Current.getInstance(this);
        tapConnection.getListETap(current.getUnidadeNegocio().getCodigo(), mNome, tapActionType, mTapFilter);
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mTapFilter == null || mTapFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mTapFilter == null || mTapFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }

    }

    private void openFilter() {
        Intent filterIntent = new Intent(this, TapListFilterActivity.class);
        if (mTapFilter != null) {
            filterIntent.putExtra(TapListFilterActivity.FILTER_RESULT_DATA_KEY,
                    mTapFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void showLegendTap() {
        try {
            Dialog mDialogSubtitles = new Dialog(this,
                    android.R.style.Theme_Translucent_NoTitleBar);
            mDialogSubtitles.setCancelable(true);
            mDialogSubtitles.setContentView(R.layout.dialog_legenda_etap);

            StatusETapDao statusDao = new StatusETapDao(this);
            ArrayList<TapStatus> listStatus = statusDao.getAll();

            for (TapStatus st : listStatus) {
                String textViewID = "etap_status" + st.getCodigo();
                int resID = getResources().getIdentifier(textViewID, "id",
                        getPackageName());
                TextView lbl = mDialogSubtitles.findViewById(resID);
                lbl.setText(st.getNome());
            }

            TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
            tvOkSubTitles.setOnClickListener(view -> mDialogSubtitles.dismiss());
            mDialogSubtitles.show();
        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void loadTap(int position) {
        showProgress(true);
        mTapCodSelected = mTapListAdapter.getListETap().get(position).getCodigo();
        tapConnection.getETapDetail(mTapListAdapter.getListETap().get(position).getId() + "", tapActionType);
    }


    private void showProgress(boolean show) {
        mProgressLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        mMessageErrorTextView.setVisibility(show ? View.VISIBLE : View.GONE);
        mBaseRelativeLayout.setVisibility(show ? View.GONE : View.VISIBLE);

        if(tapActionType != TapActionType.TAP_LIST){
            mAddPedidoFloatingActionButton.setVisibility(View.GONE);
        } else {
            mAddPedidoFloatingActionButton.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        mListEmptyLinearLayout.setVisibility(View.GONE);
        mETapRecyclerView.setVisibility(View.VISIBLE);
    }


    private void sendLoteTap() {
        showProgress(true);

        ArrayList<Integer> arrayCodList = new ArrayList<>();


        for (TapList item:mTapList) {
            if(item.isCheckdLote()){
                arrayCodList.add(item.getId());
            }
        }


        tapConnection.sendLoteETap(arrayCodList);
    }
}
