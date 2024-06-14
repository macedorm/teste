package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.RotaGuiadaAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskRotaGuiada;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.data.MessageFilter;
import br.com.jjconsulting.mobile.dansales.data.RotasFilter;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaActionDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaTarefaDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.DialogsMultiValue;
import br.com.jjconsulting.mobile.dansales.util.JJSyncRotaGuiada;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class RotaGuiadaFragment extends BaseFragment implements AsyncTaskRotaGuiada.OnAsyncResponse {

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";
    private static final int FILTER_REQUEST_CODE = 1;
    private static final int PICK_CLIENTE_REQUEST_CODE = 2;
    private static final int DETAIL_RG_REQUEST_CODE = 3;

    private static int LOCATION_REQ_CODE = 1;

    private AsyncTaskRotaGuiada mAsyncTaskRota;
    private List<Rotas> mRotas;
    private List<Rotas> mRotasUnrealized;

    private RotaGuiadaDao mRotaGuiadaDao;
    private RotaGuiadaTarefaDao mRotaGuiadaTarefaDao;

    private RotasFilter mRotaGuiadaFilter;
    private RotaGuiadaAdapter mRotaGuiadaAdapter;
    private RotaGuiadaAdapter mRotaGuiadaUnrealizedAdapter;

    private DatePickerDialog mDatePickerDialog;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ScrollView mRotaGuiadaScrollView;

    private TextView mDateTextView;
    private TextView mTitleTextView;
    private TextView mRotaGuiadaUnnrealizedTextView;
    private RecyclerView mRotasRecyclerView;
    private RecyclerView mRotasUnrealizedRecyclerView;

    private LinearLayout mListEmptyLinearLayout;
    private ImageButton mListEmptyImageButton;
    private LinearLayout mDataLinearLayout;
    private Menu mMenu;


    private FloatingActionButton mAddFloatingActionButton;

    private String mNome;

    private Date mDateRotaGuiada;

    private boolean isPermiteCheckinChekout;

    public RotaGuiadaFragment() {
    }

    public static RotaGuiadaFragment newInstance() {
        return new RotaGuiadaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requestPermissions(
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_REQ_CODE);

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_rotas, container, false);

        // if is there any data sent, get the filter
        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mRotaGuiadaFilter = (RotasFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        Current current = Current.getInstance(getContext());

        if(current != null){

            Perfil perfil = current.getUsuario().getPerfil();
            isPermiteCheckinChekout = perfil.isRotaPermiteCheckinChekout();

            mRotaGuiadaScrollView = view.findViewById(R.id.rg_scroll_view);
            mRotaGuiadaUnnrealizedTextView = view.findViewById(R.id.title_rg_unrealized_text_view);

            mAddFloatingActionButton = view.findViewById(R.id.add_rg_floating_action_button);
            mTitleTextView = view.findViewById(R.id.title_text_view);
            mDataLinearLayout = view.findViewById(R.id.data_linear_layout);
            mDateTextView = view.findViewById(R.id.rg_data_text_view);
            mDateSetListener = (DatePicker datePicker, int year, int month, int day) -> {

                try {
                    Date date = FormatUtils.toCreateDatePicker(year, month, day);
                    setDateRotaGuiada(date);

                } catch (Exception e) {
                    LogUser.log(Config.TAG, e.toString());
                }

            };

            createDataPicker(new Date());
            mDataLinearLayout.setOnClickListener((v) -> {
                mDatePickerDialog.show();
            });

            mRotasRecyclerView = view.findViewById(R.id.rg_recycler_view);
            mRotasUnrealizedRecyclerView = view.findViewById(R.id.rg_unrealized_recycler_view);

            mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
            mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
            mListEmptyImageButton.setOnClickListener(viewListEmpty -> {
                openFilter();
            });

            mRotaGuiadaDao = new RotaGuiadaDao(getActivity());
            mRotaGuiadaTarefaDao = new RotaGuiadaTarefaDao(getContext());

            DividerItemDecoration divider = new DividerItemDecoration(mRotasRecyclerView.getContext(), DividerItemDecoration.VERTICAL);

            mRotasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
            mRotasRecyclerView.addItemDecoration(divider);
            mRotasRecyclerView.setNestedScrollingEnabled(false);
            mRotasRecyclerView.setHasFixedSize(true);

            mRotasUnrealizedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
            mRotasUnrealizedRecyclerView.addItemDecoration(divider);
            mRotasUnrealizedRecyclerView.setNestedScrollingEnabled(false);
            mRotasRecyclerView.setHasFixedSize(true);

            ItemClickSupport.addTo(mRotasRecyclerView).setOnItemClickListener(
                    (recyclerView, position, v) -> {

                        if (mRotasUnrealized != null && mRotasUnrealized.size() > 0 && perfil.isRotaJutificativaVisitaNaoRealizada()) {
                            dialogsDefault.showDialogMessage(getString(R.string.message_error_visita_n_realizada), dialogsDefault.DIALOG_TYPE_WARNING, null);
                            return;
                        }

                        if (mRotas.get(position).getStatus() == RotaGuiadaUtils.STATUS_RG_NAO_INICIADO && isRotaAndamento()) {
                            dialogsDefault.showDialogMessage(getString(R.string.message_error_visita_andamento), dialogsDefault.DIALOG_TYPE_WARNING, null);
                            return;
                        }

                        if(ContextCompat.checkSelfPermission((getContext()), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                                PackageManager.PERMISSION_DENIED &&
                                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                        PackageManager.PERMISSION_DENIED) {

                            dialogsDefault.showDialogMessage(
                                    getString(R.string.not_permisssion_granted),
                                    dialogsDefault.DIALOG_TYPE_WARNING, null);
                        }else{
                            Rotas rota = mRotaGuiadaAdapter.getRotas().get(position);
                            Intent it = RotaGuiadaDetailActivity.newIntent(getContext(), rota);
                            getActivity().startActivityForResult(it, DETAIL_RG_REQUEST_CODE);
                        }

                    });

            ItemClickSupport.addTo(mRotasUnrealizedRecyclerView).setOnItemClickListener(
                    (recyclerView, position, v) -> {
                        DialogsMultiValue dialogsMultiValue = new DialogsMultiValue(getActivity());

                        if( mRotasUnrealized.size() == 0 || position >= mRotasUnrealized.size()){
                            return;
                        } else {
                            if(mRotasUnrealized.get(position).getStatus() == RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO){

                                RotaGuiadaActionDao rotaGuiadaActionDao = new RotaGuiadaActionDao(getContext());
                                Date dateCheckin = rotaGuiadaActionDao.getLastDateAction(mRotasUnrealized.get(position));
                                String checkin = "";

                                if(dateCheckin == null) {
                                    checkin = mRotasUnrealized.get(position).getCheckin();
                                } else {
                                    try{
                                        checkin = FormatUtils.toTextToCompareDateInSQlite(dateCheckin);
                                    }catch (Exception ex){
                                        LogUser.log(ex.toString());
                                    }
                                }

                                dialogsMultiValue.showDialogDateSpinner(TMultiValuesType.RG_JUST_INCOMPLETO, checkin, dialogsMultiValue.DIALOG_TYPE_WARNING, (multiValues, date) -> {
                                    if (perfil.isRotaJutificativaVisitaNaoRealizada()) {
                                        Rotas rota = mRotasUnrealized.get(position);
                                        rota.setCheckout(date);

                                        RotaGuiadaUtils.manualActionRotaIncomplete(getContext(), rota, multiValues);
                                        findRotaGuiada();
                                    }
                                });
                            } else if (mRotasUnrealized.get(position).getStatus() == RotaGuiadaUtils.STATUS_RG_PAUSADO) {
                                dialogsMultiValue.showDialogSpinner(TMultiValuesType.RG_JUST_INCOMPLETO,  getString(R.string.title_dialog_visita_incompleta), dialogsMultiValue.DIALOG_TYPE_WARNING, (multiValues) -> {
                                    if (perfil.isRotaJutificativaVisitaNaoRealizada()) {
                                        RotaGuiadaUtils.manualActionRotaPause(getContext(), mRotasUnrealized.get(position), multiValues);
                                        findRotaGuiada();
                                    }
                                });
                            } else {
                                dialogsMultiValue.showDialogSpinner(TMultiValuesType.RG_JUST_NAO_INICIADO,  getString(R.string.title_dialog_visita_n_realizada), dialogsMultiValue.DIALOG_TYPE_WARNING, (multiValues) -> {
                                    if (perfil.isRotaJutificativaVisitaNaoRealizada()) {
                                        mRotaGuiadaDao.setJustificaVisita(mRotasUnrealized.get(position), multiValues);
                                        findRotaGuiada();
                                    }
                                });
                            }

                            JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
                            jjSyncRotaGuiada.syncRotaGuiada(getActivity(), ()-> { });
                        }
                    });

            mAddFloatingActionButton.setOnClickListener((v) -> {
                dialogsDefault.showDialogQuestion(getString(R.string.message_cliente_fora_rota), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        Intent it = RotaGuiadaPickClienteActivity
                                .newIntent(getContext(), FormatUtils.toTextToCompareDateInSQlite(mDateRotaGuiada));
                        getActivity().startActivityForResult(it, PICK_CLIENTE_REQUEST_CODE);
                    }

                    @Override
                    public void onClickNegative() {

                    }
                });
            });

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            setDateRotaGuiada(FormatUtils.toCreateDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

            if (!isPermiteCheckinChekout) {
                mTitleTextView.setVisibility(View.GONE);
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRotaGuiadaFilter != null) {
            outState.putSerializable(KEY_FILTER_RESULT_STATE, mRotaGuiadaFilter);
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
                if (mRotaGuiadaFilter == null) {
                    mRotaGuiadaFilter = new RotasFilter();
                }
                findRotaGuiada();
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
                mRotaGuiadaFilter = new RotasFilter();
                findRotaGuiada();
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
                    findRotaGuiada(data);
                }
                break;
            case PICK_CLIENTE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    Cliente codCli = (Cliente) data.getSerializableExtra(RotaGuiadaPickClienteActivity.KEY_RESULT_CLIENTE);
                    mRotaGuiadaDao.newRoute(codCli.getCodigo(), mDateRotaGuiada, false,
                            Current.getInstance(getContext()).getUsuario().getCodigo(),
                            Current.getInstance(getContext()).getUnidadeNegocio().getCodigo());
                    findRotaGuiada();
                }
                break;
            case DETAIL_RG_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    findRotaGuiada();
                }
            default:
                if (RotaGuiadaDetailActivity.isUpdate) {
                    RotaGuiadaDetailActivity.isUpdate = false;
                    findRotaGuiada();
                }
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void processFinish(List<Rotas> rotas[]) {


        if (getActivity() == null) {
            return;
        }

        mRotas = rotas[1];
        mRotasUnrealized = rotas[0];

        mRotaGuiadaAdapter = new RotaGuiadaAdapter(getContext(), mRotas, mRotaGuiadaFilter, mDateRotaGuiada.before(FormatUtils.toDateTimeFixed()));
        mRotasRecyclerView.setAdapter(mRotaGuiadaAdapter);

        int viewHeight = 0;

        if (mRotas != null) {
            viewHeight = mRotaGuiadaAdapter.getSizeItem() * mRotas.size();
            mRotasRecyclerView.getLayoutParams().height = viewHeight + (int)(mRotaGuiadaAdapter.getSizeItem() / 1.5);
        }

        mRotaGuiadaUnrealizedAdapter = new RotaGuiadaAdapter(getContext(), mRotasUnrealized, null,true);
        mRotasUnrealizedRecyclerView.setAdapter(mRotaGuiadaUnrealizedAdapter);

        int viewHeight2 = 0;
        if (mRotasUnrealized != null) {
            viewHeight2 = mRotaGuiadaUnrealizedAdapter.getSizeItem() * mRotasUnrealized.size();
            mRotasUnrealizedRecyclerView.getLayoutParams().height = viewHeight2 + (int)(mRotaGuiadaAdapter.getSizeItem() / 1.5);
        }

        setShowListRotasUnrealized();
        showView();

        JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
        jjSyncRotaGuiada.syncRotaGuiada(getActivity(), null);

        List<Message> messages;
        MessageDao messageDao = new MessageDao(getActivity());

        Current current = Current.getInstance(getContext());

        //Verifica se existem mensagens não lidas de rota guiada
        MessageFilter messageFilter = new MessageFilter();
        messageFilter.settMessageType(TMessageType.ROTA_GUIADA);
        messages = messageDao.getMessages(current.getUsuario(), current.getUnidadeNegocio().getCodigo(), new Date(), messageFilter, true);

        if(messages != null && messages.size() > 0) {
            MessageDetailDialogFragment messageDetailDialogFragment = MessageDetailDialogFragment.newInstance(messages);
            messageDetailDialogFragment.show(getActivity().getSupportFragmentManager(), "");
        }
    }

    private void findRotaGuiada() {
        findRotaGuiada(null);
    }

    private void findRotaGuiada(Intent data) {
        try {
            mRotaGuiadaAdapter = new RotaGuiadaAdapter(getContext(), new ArrayList<>(), null, false);
            mRotasRecyclerView.setAdapter(mRotaGuiadaAdapter);

            if (data != null && data.hasExtra(RotaGuiadaFilterActivity.FILTER_RESULT_DATA_KEY)) {
                mRotaGuiadaFilter = (RotasFilter) data.getSerializableExtra(
                        RotaGuiadaFilterActivity.FILTER_RESULT_DATA_KEY);

                mListEmptyLinearLayout.setVisibility(View.GONE);
                mRotaGuiadaScrollView.setVisibility(View.VISIBLE);
            }

            loadRotaGuiadaPaginacao();
            setFilterMenuIcon();

        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void loadRotaGuiadaPaginacao() {
        mRotaGuiadaAdapter.resetData();
        if (mAsyncTaskRota != null) {
            mAsyncTaskRota.cancel(true);
        }

        if (mRotaGuiadaFilter != null && mRotaGuiadaFilter.getDate() != null) {
            mDateRotaGuiada = mRotaGuiadaFilter.getDate();
            mDateTextView.setText(FormatUtils.toDefaultDateFormat(getContext(), mDateRotaGuiada));
        }

        mAsyncTaskRota = new AsyncTaskRotaGuiada(getActivity(), mRotaGuiadaFilter, mNome, mDateRotaGuiada, mRotaGuiadaDao, this);
        mAsyncTaskRota.execute();
    }

    private void setFilterMenuIcon() {
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mRotaGuiadaFilter == null || mRotaGuiadaFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mRotaGuiadaFilter == null || mRotaGuiadaFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }
    }

    private void createDataPicker(Date date) {
        Calendar calendar = Calendar.getInstance();

        if (date != null) {
            try {
                calendar.setTime(date);
            } catch (Exception e) {
                LogUser.log(Config.TAG, e.toString());
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mDatePickerDialog = new DatePickerDialog(
                getActivity(), mDateSetListener, year, month, day);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                final Calendar minDate = Calendar.getInstance();
                minDate.setTime(new Date());
                minDate.set(Calendar.HOUR_OF_DAY, minDate.getMinimum(Calendar.HOUR_OF_DAY));
                minDate.set(Calendar.MINUTE, minDate.getMinimum(Calendar.MINUTE));
                minDate.set(Calendar.SECOND, minDate.getMinimum(Calendar.SECOND));
                minDate.set(Calendar.MILLISECOND, minDate.getMinimum(Calendar.MILLISECOND));
                minDate.add(Calendar.DAY_OF_MONTH, -30);

                final Calendar maxDate = Calendar.getInstance();
                maxDate.setTime(new Date());
                maxDate.set(Calendar.HOUR_OF_DAY, minDate.getMinimum(Calendar.HOUR_OF_DAY));
                maxDate.set(Calendar.MINUTE, minDate.getMinimum(Calendar.MINUTE));
                maxDate.set(Calendar.SECOND, minDate.getMinimum(Calendar.SECOND));
                maxDate.set(Calendar.MILLISECOND, minDate.getMinimum(Calendar.MILLISECOND));
                maxDate.add(Calendar.DAY_OF_MONTH, 30);

                mDatePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
                mDatePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

            } catch (Exception ex) {
                LogUser.log(Config.TAG, ex.toString());
            }
        } else {
            mDatePickerDialog.getDatePicker().setMinDate(FormatUtils.getDateTimeNow(0, -1, 0).getTime());
        }

        mDatePickerDialog.getDatePicker().setMaxDate(FormatUtils.getDateTimeNow(0, 1, 0).getTime());
    }

    private void openFilter() {
        Intent filterIntent = new Intent(getActivity(), RotaGuiadaFilterActivity.class);
        if (mRotaGuiadaFilter != null) {
            filterIntent.putExtra(RotaGuiadaFilterActivity.FILTER_RESULT_DATA_KEY,
                    mRotaGuiadaFilter);
        }

        getActivity().startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void showLegend() {
        Dialog mDialogSubtitles = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        mDialogSubtitles.setCancelable(true);
        mDialogSubtitles.setContentView(R.layout.dialog_legenda_rota);
        TextView tvOkSubTitles = mDialogSubtitles.findViewById(R.id.ok_button);
        tvOkSubTitles.setOnClickListener(v -> mDialogSubtitles.dismiss());
        mDialogSubtitles.show();
    }

    private void setDateRotaGuiada(Date date) {
        mRotaGuiadaFilter = new RotasFilter();
        mDateRotaGuiada = date;
        mDateTextView.setText(FormatUtils.toDefaultDateFormat(getContext(), date));

        if (FormatUtils.toDateTimeFixed().equals(mDateRotaGuiada) && isPermiteCheckinChekout) {
            mAddFloatingActionButton.show();
        } else {
            mAddFloatingActionButton.hide();
        }

        setFilterMenuIcon();
        findRotaGuiada();
    }

    private boolean isRotaAndamento() {
        for (Rotas rota : mRotas) {
            if (rota.getStatus() == RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO) {
                return true;
            }
        }
        return false;
    }

    private void setShowListRotasUnrealized() {
        if (mRotasUnrealized == null || mRotasUnrealized.size() == 0) {
            mRotasUnrealizedRecyclerView.setVisibility(View.GONE);
            mRotaGuiadaUnnrealizedTextView.setVisibility(View.GONE);
        } else {

            String dateUnrealized = "";

            try {
                dateUnrealized = FormatUtils.toDateTimeText(mRotasUnrealized.get(0).getDate());
            } catch (ParseException ex) {
                LogUser.log(Config.TAG, ex.toString());
            }

            mRotaGuiadaUnnrealizedTextView.setText(getString(R.string.title_visita_n_realizada, dateUnrealized));
            mRotasUnrealizedRecyclerView.setVisibility(View.VISIBLE);
            mRotaGuiadaUnnrealizedTextView.setVisibility(View.VISIBLE);
        }
    }

    private void showView() {
        if ((mRotaGuiadaAdapter.getRotas() == null || mRotaGuiadaAdapter.getRotas().size() == 0) &&
                (mRotaGuiadaUnrealizedAdapter.getRotas() == null || mRotaGuiadaUnrealizedAdapter.getRotas().size() == 0)) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mRotaGuiadaScrollView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mRotaGuiadaScrollView.setVisibility(View.VISIBLE);
        }
    }
}
