package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.data.PlanejamentoRotaFilter;
import br.com.jjconsulting.mobile.dansales.database.PlanejamentoRotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.JJSyncRotaGuiada;
import br.com.jjconsulting.mobile.dansales.util.PlanejamentoRotaUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEvent;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEventGridAdapter;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEventView;

public class PlanejamentoRotaFragment extends BaseFragment implements View.OnClickListener {

    private static final int FILTER_REQUEST_CODE = 1;
    private static final int DETAIL_REQUEST_CODE = 2;
    private static final int ADD_DETAIL_REQUEST_CODE =3;

    private static final String KEY_FILTER_RESULT_STATE = "filter_result_state";

    private SpinnerArrayAdapter<Usuario> mHierarquiaSpinnerAdapter;
    private Spinner mHierarquiaSpinner;

    private Menu mMenu;

    private  ProgressDialog progressDialog;

    private ImageButton mListEmptyImageButton;

    private JJCalendarEventView event;

    private List<JJCalendarEvent> calendarEventList;

    private PlanejamentoRotaFilter mPlanejamentoRotaFilter;

    private FloatingActionButton fab;

    private int currentHierarquia;
    private int currentMonthAdd;

    public PlanejamentoRotaFragment() {
        // Required empty public constructor
    }

    public static PlanejamentoRotaFragment newInstance() {
        return new PlanejamentoRotaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view =  inflater.inflate(R.layout.activity_planejamento_rota, container, false);

        JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
        jjSyncRotaGuiada.syncRotaGuiada(getActivity(), new JJSyncRotaGuiada.OnFinish() {
            @Override
            public void onFinish() {

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(
                KEY_FILTER_RESULT_STATE)) {
            mPlanejamentoRotaFilter = (PlanejamentoRotaFilter) savedInstanceState.getSerializable(
                    KEY_FILTER_RESULT_STATE);
        }

        LinearLayout relativeLayout = view.findViewById(R.id.calendar_linear_layout);
        fab = view.findViewById(R.id.add_floating_action_button);
        fab.setOnClickListener(this);

        mListEmptyImageButton = view.findViewById(R.id.list_empty_image_button);
        mHierarquiaSpinner = view.findViewById(R.id.hierarquia_spinner);

        calendarEventList = new ArrayList<>();

        event =  new JJCalendarEventView(getContext(), calendarEventList, new Date());
        event.startCalendar();

        event.setOnItemClickListener((index, date)-> {
            startActivityForResult(PlanejamentoRotaDetailActivity.newInstance(getContext(),
                    date, getHierarquiaUsuario().get(currentHierarquia)), DETAIL_REQUEST_CODE);
        });

        event.setOnLongClickListener((calendarEvent) -> {
            if(calendarEvent.getType() != PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue()){
                dialogsDefault.showDialogQuestion(getString(R.string.planejamneto_rota_remove_question), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                    @Override
                    public void onClickPositive() {
                        PlanejamentoRotaGuiadaDao mPlanejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(getContext());


                        if(calendarEvent.getStatus() == RotaGuiadaUtils.STATUS_RG_NAO_INICIADO){
                            mPlanejamentoRotaGuiadaDao.managerDeletePlanejamentoRota(calendarEvent);
                            jjSyncRotaGuiada.syncRotaGuiada(getActivity(), null);

                            loadingEvents(currentMonthAdd);
                        } else {
                            Toast.makeText(getContext(),   getString(R.string.planejamneto_rota_remove_error), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onClickNegative() {

                    }
                });
            }
        });

        event.getCalendarGridView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (fab.isShown()) {
                        fab.hide();
                    } else {
                        fab.show();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        event.setLayoutParams(layoutParams);
        relativeLayout.addView(event);

        setupHierarquiaSpinner();
        createDialogProgress();
        visible();

        loadingEvents(currentMonthAdd);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        inflater.inflate(R.menu.menu_calendario, menu);
        inflater.inflate(R.menu.cancel_filter_menu, menu);
        inflater.inflate(R.menu.filter_menu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_calendar:
                showDialogCalendar();
                return true;
            case R.id.menu_cancel_filter:
                clearFilter();
                return true;
            case R.id.menu_filter:
                openFilter();
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

                    if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
                        mPlanejamentoRotaFilter = (PlanejamentoRotaFilter) data.getSerializableExtra(
                                PlanejamentoRotaFilterActivity.FILTER_RESULT_DATA_KEY);
                        setFilterMenuIcon();
                        setupHierarquiaSpinner();
                    }
                }
                break;
            case DETAIL_REQUEST_CODE:
            case ADD_DETAIL_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    loadingEvents(currentMonthAdd);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void visible(){
        if(UsuarioUtils.isPromotor(Current.getInstance(getContext()).getUsuario().getCodigoFuncao())){
            fab.hide();
        }
    }

    public void showDialogCalendar() {

        if(getActivity().isFinishing()){
            return;
        }

        Dialog calendarDialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        calendarDialog.setCancelable(true);
        calendarDialog.setContentView(R.layout.dialog_calendar);

        TextView beforeCalendarTextView  = calendarDialog.findViewById(R.id.before_month_text_view);
        TextView currentCalendarTextView  = calendarDialog.findViewById(R.id.current_month_text_view);
        TextView afterCalendarTextView  = calendarDialog.findViewById(R.id.after_month_text_view);

        currentCalendarTextView.setText(event.getMonthLabel(0).toUpperCase());
        beforeCalendarTextView.setText(event.getMonthLabel(-1).toUpperCase());
        afterCalendarTextView.setText(event.getMonthLabel(1).toUpperCase());

        currentCalendarTextView.setOnClickListener((view)-> {
            loadingEvents(0);
            calendarDialog.dismiss();
        });

        beforeCalendarTextView.setOnClickListener((view)-> {
            loadingEvents(-1);
            calendarDialog.dismiss();
        });

        afterCalendarTextView.setOnClickListener((view)-> {
            loadingEvents(1);
            calendarDialog.dismiss();
        });

        calendarDialog.show();

    }

    private void reloadCalendar(){
        getActivity().runOnUiThread(()->{
                    event.reloadCalendar();
                }
        );
    }

    private void setupHierarquiaSpinner() {

        mHierarquiaSpinnerAdapter = new SpinnerArrayAdapter<Usuario>(
                getContext(), getHierarquiaUsuario().toArray(), false) {
            @Override
            public String getItemDescription(Usuario item) {
                return item.getNomeReduzido();
            }
        };

        mHierarquiaSpinner.setAdapter(mHierarquiaSpinnerAdapter);
        mHierarquiaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != currentHierarquia){
                    currentHierarquia = i;
                    loadingEvents(currentMonthAdd);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void openFilter() {
        Intent filterIntent = new Intent(getContext(), PlanejamentoRotaFilterActivity.class);
        if (mPlanejamentoRotaFilter != null) {
            filterIntent.putExtra(PlanejamentoRotaFilterActivity.FILTER_RESULT_DATA_KEY,
                    mPlanejamentoRotaFilter);
        }
        startActivityForResult(filterIntent, FILTER_REQUEST_CODE);
    }

    private void clearFilter(){
        mPlanejamentoRotaFilter = new PlanejamentoRotaFilter();
        setFilterMenuIcon();
        currentMonthAdd = 0;
        reloadCalendar();
        setupHierarquiaSpinner();
    }

    private void setFilterMenuIcon() {
        // setup filter's icon
        if (mMenu != null) {
            MenuItem filterItem = mMenu.findItem(R.id.menu_filter);
            filterItem.setIcon((mPlanejamentoRotaFilter == null || mPlanejamentoRotaFilter.isEmpty()) ?
                    R.drawable.ic_filter : R.drawable.ic_filter_applied);

            mListEmptyImageButton.setColorFilter(
                    (mPlanejamentoRotaFilter == null || mPlanejamentoRotaFilter.isEmpty()) ?
                            getResources().getColor(R.color.statusNoFilter) :
                            getResources().getColor(R.color.statusFilter));
        }

    }

    private List<Usuario> getHierarquiaUsuario(){
        List<Usuario> list = new ArrayList<>();

        Usuario usuario = Current.getInstance(getContext()).getUsuario();
        usuario.setNome("Minha Agenda");

        if(mPlanejamentoRotaFilter == null || mPlanejamentoRotaFilter.isEmpty()){
            list.add(usuario);
        } else {
            list = mPlanejamentoRotaFilter.getHierarquiaComercial();

            boolean notUser = false;

            for(int ind = 0; ind < list.size(); ind++){
                if(list.get(ind).getCodigo().equals(Current.getInstance(getContext()).getUsuario().getCodigo())){
                    list.set(ind, usuario);
                    notUser = true;
                }
            }

            if(!notUser){
                List<Usuario> listUser = new ArrayList<>();
                listUser.add(usuario);
                listUser.addAll(list);

                list = listUser;
            }
        }

        return list;
    }

    private void loadingEvents(int addMonth){

        createDialogProgress();
        progressDialog.show();

        ExecutorService executors = Executors.newFixedThreadPool(1);
        Runnable runnable = ()-> {
            try {
                event.setMonth(addMonth);
                currentMonthAdd = addMonth;

                PlanejamentoRotaGuiadaDao planejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(getContext());

                Usuario usuario = getHierarquiaUsuario().get(currentHierarquia);
                String unidadeNegocio = Current.getInstance(getContext()).getUnidadeNegocio().getCodigo();

                if(UsuarioUtils.isPromotor(usuario.getCodigoFuncao())){
                    calendarEventList = planejamentoRotaGuiadaDao.getPromotorAllEvents(usuario, unidadeNegocio , event.getCalendar());
                } else {
                    calendarEventList = planejamentoRotaGuiadaDao.getGAAllEvents(usuario.getCodigo(), unidadeNegocio, event.getCalendar());
                }

                event.setEventsList(calendarEventList);
                reloadCalendar();

                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        executors.submit(runnable);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_floating_action_button:
                if(!UsuarioUtils.isPromotor(Current.getInstance(getContext()).getUsuario().getCodigoFuncao())){
                    startActivityForResult(PlanejamentoRotaAddActivity.newIntent(getContext()), ADD_DETAIL_REQUEST_CODE);
                } else {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.rg_message_error_task), Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }


}
