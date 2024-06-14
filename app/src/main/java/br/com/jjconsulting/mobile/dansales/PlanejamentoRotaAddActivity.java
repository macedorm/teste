package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.reflect.TypeToken;
import com.unnamed.b.atv.model.TreeNode;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.data.ValidationDan;
import br.com.jjconsulting.mobile.dansales.database.PlanejamentoRotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Atividade;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.dansales.util.JJSyncRotaGuiada;
import br.com.jjconsulting.mobile.dansales.util.PlanejamentoRotaUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.util.TreeNodeUtils;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;
import br.com.jjconsulting.mobile.jjlib.util.ArrayUtils;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEvent;

public class PlanejamentoRotaAddActivity extends BaseActivity implements View.OnClickListener {

    //Picker Data start
    private TextView mDataStartTextView;
    private LinearLayout mDataStartLinearLayout;
    private DatePickerDialog.OnDateSetListener mDataStartDateSetListener;
    private DatePickerDialog mDataStartdatePickerDialog;

    //Picker Time start
    private TextView mTimeStartTextView;
    private LinearLayout mTimeStartLinearLayout;
    private TimePickerDialog.OnTimeSetListener mHoursStartDateSetListener;
    private TimePickerDialog mTimeStartDatePickerDialog;

    //Picker Time start
    private TextView mTimeEndTextView;
    private LinearLayout mTimeEndLinearLayout;
    private TimePickerDialog.OnTimeSetListener mHoursEndDateSetListener;
    private TimePickerDialog mTimeEndDatePickerDialog;

    //Cliente
    private LinearLayout mClienteLinearLayout;
    private TextView mClienteTextView;

    //Tipo de Atividade
    private SpinnerArrayAdapter<Atividade> mAtividadeSpinnerAdapter;
    private Spinner mAtividadeSpinner;

    //Hieraquia Comercial
    private LinearLayout mHierarquieLinearLayout;
    private SpinnerArrayAdapter<Usuario> mHierarquiaSpinnerAdapter;
    private Spinner mHierarquiaSpinner;

    private TextInputEditText obsEditText;

    private Button mOkButton;

    private int typeSelected;

    private Usuario promotorSelected = new Usuario();

    private List<Cliente> clientes;

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, PlanejamentoRotaAddActivity.class);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.activity_planejamento_rota_add);

        //Setup Data Picker Start
        mDataStartTextView = findViewById(R.id.data_start_text_view);
        mDataStartLinearLayout = findViewById(R.id.data_start_linear_layout);

        mTimeStartTextView = findViewById(R.id.time_start_text_view);
        mTimeStartLinearLayout = findViewById(R.id.time_start_linear_layout);

        mTimeEndTextView = findViewById(R.id.time_end_text_view);
        mTimeEndLinearLayout = findViewById(R.id.time_end_linear_layout);

        mClienteLinearLayout = findViewById(R.id.cliente_linear_layout);

        mClienteTextView = findViewById(R.id.cliente_text_view);

        mAtividadeSpinner = findViewById(R.id.type_spinner);

        mHierarquiaSpinner = findViewById(R.id.hierarquia_spinner);
        mHierarquieLinearLayout = findViewById(R.id.hierarquia_linear_layout);

        obsEditText = findViewById(R.id.obs_edit_text);

        mOkButton  = findViewById(R.id.add_button);

        clientes = new ArrayList<>();

        loadingData();

        setupDataPicker();
        setupTimeStartPicker();
        setupTimeEndPicker();

    }

    @Override
    public void onClick(View view) {
        ValidationDan validationDan = new ValidationDan();

        String user =  Current.getInstance(this).getUsuario().getCodigo();
        String unNeg = Current.getInstance(this).getUnidadeNegocio().getCodigo();

        Date date = FormatUtils.convertTextInDatePT(mDataStartTextView.getText().toString());
        JJCalendarEvent jjCalendarEvent = new JJCalendarEvent();
        jjCalendarEvent.setType(typeSelected);
        jjCalendarEvent.setUserID(user);
        jjCalendarEvent.setUnNeg(unNeg);
        jjCalendarEvent.setStatus(RotaGuiadaUtils.STATUS_RG_NAO_INICIADO);
        jjCalendarEvent.setPromotor(promotorSelected.getCodigo());
        jjCalendarEvent.setPromotorName(promotorSelected.getNome());
        jjCalendarEvent.setNote(obsEditText.getText().toString());

        if(date != null) {
            jjCalendarEvent.setDate(FormatUtils.toTextToCompareDateInSQlite(date));
        } else {
            validationDan.addError(getString(R.string.planejamneto_rota_add_task_date_error));
        }

        switch (view.getId()){
            case R.id.cliente_linear_layout:
                if(typeSelected == 1){
                    startActivityForResult(PickClientePlanejamentoRotaActivity.newIntent(this), Config.REQUEST_DETAIL_CLIENTE);
                } else {

                    if(TextUtils.isNullOrEmpty(jjCalendarEvent.getPromotor())){
                        validationDan.addError(getString(R.string.planejamneto_rota_add_task_promotor_error));
                    }

                    if(!validationDan.isValid()){
                        showDialogValidationError(validationDan);
                    } else {
                        startActivityForResult(PickClientePlanejamentoRotaActivity.newIntent(this, promotorSelected.getCodigo(), jjCalendarEvent.getDate()), Config.REQUEST_DETAIL_CLIENTE);
                    }
                }

                break;
            case R.id.add_button:
                switch (jjCalendarEvent.getType()){
                    case 0:
                        validationDan.addError(getString(R.string.planejamneto_rota_add_task_error));
                        break;
                    case 1:
                    case 2:
                        if(clientes.size() == 0){
                            validationDan.addError(getString(R.string.planejamneto_rota_add_task_client_error));
                        }

                        if(jjCalendarEvent.getType() == 2 && TextUtils.isNullOrEmpty(jjCalendarEvent.getPromotor())){
                            validationDan.addError(getString(R.string.planejamneto_rota_add_task_promotor_error));
                        }
                        break;
                }

                if(jjCalendarEvent.getType() != 2) {
                    boolean isNotEmpty = false;

                    if (!getString(R.string.hours_start_hint).equals(mTimeStartTextView.getText().toString())) {
                        jjCalendarEvent.setHoursStart(mTimeStartTextView.getText().toString());
                    } else {
                        validationDan.addError(getString(R.string.planejamneto_rota_add_task_hours_start_error));
                        isNotEmpty = true;
                    }

                    if (!getString(R.string.hours_end_hint).equals(mTimeEndTextView.getText().toString())) {
                        jjCalendarEvent.setHoursEnd(mTimeEndTextView.getText().toString());
                    } else {
                        validationDan.addError(getString(R.string.planejamneto_rota_add_task_hours_end_error));
                        isNotEmpty = true;
                    }

                    if(!isNotEmpty && !FormatUtils.compareHours(mTimeStartTextView.getText().toString(), mTimeEndTextView.getText().toString())){
                        validationDan.addError(getString(R.string.planejamneto_rota_add_task_hours_compare_error));
                    }
                }

                if(!validationDan.isValid()){
                    showDialogValidationError(validationDan);
                } else {
                    String warning = null;

                    PlanejamentoRotaGuiadaDao planejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(this);

                    if(jjCalendarEvent.getType() < PlanejamentoRotaUtils.COACHINGPORMOTOR.getValue()) {
                        for (Cliente cliente: clientes) {
                            jjCalendarEvent.setCodCliente(cliente.getCodigo());
                            if(!planejamentoRotaGuiadaDao.hasEvent(jjCalendarEvent)){
                                planejamentoRotaGuiadaDao.insertPlanejamentoRota(jjCalendarEvent);
                                planejamentoRotaGuiadaDao.insertRoute(jjCalendarEvent);
                            } else {
                                if(TextUtils.isNullOrEmpty(warning)){
                                    warning = "Os seguintes clientes não foram inseridos por já estarem incluídos no dia selecionado: " + cliente.getCodigo();
                                } else {
                                    warning += ", " + cliente.getCodigo();
                                }
                            }
                        }
                    } else {
                        planejamentoRotaGuiadaDao.insertPlanejamentoRota(JJCalendarEvent.copy(jjCalendarEvent));
                    }


                    JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
                    jjSyncRotaGuiada.syncRotaGuiada(this, null);

                    setResult(RESULT_OK);

                    if(TextUtils.isNullOrEmpty(warning)){
                        Toast.makeText(this, getString(R.string.planejamneto_rota_add_task_success), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        dialogsDefault.showDialogMessage(warning, dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
                            @Override
                            public void onClick() {
                                finish();
                            }
                        });
                    }

                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.REQUEST_DETAIL_CLIENTE && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra(PickClientePlanejamentoRotaActivity.FILTER_RESULT_DATA_KEY);

            Type listType = new TypeToken<List<Cliente>>(){}.getType();
            clientes = gson.fromJson(result, listType);

            String title = null;

            for(Cliente cliente: clientes){
                if(TextUtils.isNullOrEmpty(title)){
                    title = cliente.getCodigo();
                } else {
                    title += ", " + cliente.getCodigo();
                }
            }

            if(!TextUtils.isNullOrEmpty(title)) {
                mClienteTextView.setText(title);
            }
        }
    }

    private void loadingData(){
        showDialog();

        ExecutorService executors = Executors.newFixedThreadPool(1);
        Runnable runnable = ()-> {
            PlanejamentoRotaGuiadaDao planejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(this);

            UsuarioDao usuarioDao = new UsuarioDao(this);
            Usuario usuario = Current.getInstance(this).getUsuario();
            UnidadeNegocio unidadeNegocio = Current.getInstance(this).getUnidadeNegocio();
            Tree<Usuario> hierarquiaComercial = usuarioDao.getHierarquiaComercial(usuario,
                    unidadeNegocio.getCodigo());

            runOnUiThread(()-> {
                setupHierarquiaSpinner(usuarioDao.getListUserTree(hierarquiaComercial.getChildren()));
                setupTaskSpinner(planejamentoRotaGuiadaDao.getTask());
                addListener();

                dismissDialog();
            });

        };
        executors.submit(runnable);
    }

    private void visible(){
        mHierarquieLinearLayout.setVisibility(typeSelected == 2 ? View.VISIBLE:View.GONE);
        mClienteLinearLayout.setVisibility(typeSelected > 2 || typeSelected  == 0 ? View.GONE:View.VISIBLE);
        mTimeStartLinearLayout.setVisibility(typeSelected != 2 ? View.VISIBLE:View.GONE);
        mTimeEndLinearLayout.setVisibility(typeSelected != 2 ? View.VISIBLE:View.GONE);
    }

    private String getTime(int hours, int minutes){
        String time =(hours < 10 ? "0" : "") + hours + ":" + (minutes < 10 ? "0" : "") + minutes;
        return time;
    }

    private void showDialogValidationError(ValidationDan validation) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();

            PedidoValidationDialogFragment pedidoValidationDialogFragment =
                    PedidoValidationDialogFragment.newInstance(validation, false);
            pedidoValidationDialogFragment.show(fragmentManager, "");
            fragmentManager.executePendingTransactions();

            pedidoValidationDialogFragment.setOnDissmisDialog(new PedidoValidationDialogFragment.OnDissmisDialog() {
                @Override
                public void onFinish() {
                }
            });


        } catch (Exception ex) {
            LogUser.log(Config.TAG, ex.toString());
        }
    }

    private void setupDataPicker() {

        //Date Picker Start
        mDataStartLinearLayout.setOnClickListener(view -> {
            mDataStartdatePickerDialog.show();
        });

        mDataStartDateSetListener = (DatePicker datePicker, int year, int month, int day) -> {
            try {
                mClienteTextView.setText(getString(R.string.cliente_hint));
                clientes = new ArrayList<>();

                String date = FormatUtils.toDateCreateDatePicker(year, month, day);
                mDataStartTextView.setText(date);
                createStartDataPicker(false, year, month, day);

            } catch (ParseException e) {
                LogUser.log(Config.TAG, e.toString());
            }
        };

        createStartDataPicker( true, 0, 0, 2);

    }

    private void createStartDataPicker(boolean isDataNow, int year, int month, int day) {

        if (isDataNow) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        mDataStartdatePickerDialog = new DatePickerDialog(
                this, mDataStartDateSetListener, year, month, day);

        try {
            mDataStartdatePickerDialog.getDatePicker().setMinDate(FormatUtils.getDateTimeNow(0, 0, 0).getTime());
        }catch (Exception ex){
            LogUser.log(ex.toString());
        }
    }

    private void setupTimeStartPicker() {
        //Date Picker Start
        mTimeStartLinearLayout.setOnClickListener(view -> {
            mTimeStartDatePickerDialog.show();
        });

        createStartTimePicker(true, 0, 0);
    }

    private void setupTimeEndPicker() {
        //Date Picker Start
        mTimeEndLinearLayout.setOnClickListener(view -> {
            mTimeEndDatePickerDialog.show();
        });

        createEndTimePicker(true, 0, 0);
    }

    private void createStartTimePicker(boolean isTimeNow, int hours, int minutes) {

        if (isTimeNow) {
            final Calendar c = Calendar.getInstance();
            hours = c.get(Calendar.HOUR_OF_DAY);
            minutes = c.get(Calendar.MINUTE);
        }

        mHoursStartDateSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                mTimeStartTextView.setText(getTime(hours, minutes));
            }
        };

        mTimeStartDatePickerDialog= new TimePickerDialog(
                this, android.R.style.Theme_Holo_Light_Panel, mHoursStartDateSetListener, hours, minutes, true);
        mTimeStartDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mTimeStartDatePickerDialog.updateTime(hours, minutes);
    }

    private void createEndTimePicker(boolean isTimeNow, int hours, int minutes) {

        if (isTimeNow) {
            final Calendar c = Calendar.getInstance();
            hours = c.get(Calendar.HOUR_OF_DAY);
            minutes = c.get(Calendar.MINUTE);
        }

        mHoursEndDateSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                mTimeEndTextView.setText(getTime(hours, minutes));

            }
        };

        mTimeEndDatePickerDialog= new TimePickerDialog(
                this, android.R.style.Theme_Holo_Light_Panel, mHoursEndDateSetListener, hours, minutes, true);
        mTimeEndDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mTimeEndDatePickerDialog.updateTime(hours, minutes);

    }

    private void setupTaskSpinner(List<Atividade> task) {

        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(task.toArray(),
                getString(R.string.select_task));

        mAtividadeSpinnerAdapter = new SpinnerArrayAdapter<Atividade>(
                this, objects, true) {
            @Override
            public String getItemDescription(Atividade item) {
                return item.getNome();
            }
        };

        mAtividadeSpinner.setAdapter(mAtividadeSpinnerAdapter);

    }

    private void setupHierarquiaSpinner(List<Usuario> hierarquiaUsuario) {
        hierarquiaUsuario.remove(0);

        Object[] objects = SpinnerArrayAdapter.makeObjectsWithHint(hierarquiaUsuario.toArray(),
                getString(R.string.select_user));


        mHierarquiaSpinnerAdapter = new SpinnerArrayAdapter<Usuario>(
               this, objects, true) {
            @Override
            public String getItemDescription(Usuario item) {
                return item.getNomeReduzido();
            }
        };

        mHierarquiaSpinner.setAdapter(mHierarquiaSpinnerAdapter);

    }

    private void clearTime(){
        mTimeStartTextView.setText(R.string.hours_start_hint);
        mTimeEndTextView.setText(R.string.hours_end_hint);
    }


    private void addListener(){
        mHierarquiaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0){
                    Usuario usuario = (Usuario) mHierarquiaSpinner.getSelectedItem();
                    promotorSelected = usuario;
                } else {
                    promotorSelected = new Usuario() ;
                }

                mClienteTextView.setText(getString(R.string.cliente_hint));
                clientes = new ArrayList<>();

                visible();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mAtividadeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i > 0){
                    Atividade ati = (Atividade) mAtividadeSpinner.getSelectedItem();
                    typeSelected = Integer.parseInt((ati.getCod()));

                    if(typeSelected == 2){
                        clearTime();
                    }
                } else {
                    typeSelected = 0;
                }

                mClienteTextView.setText(getString(R.string.cliente_hint));
                clientes = new ArrayList<>();
                mHierarquiaSpinner.setSelection(0);

                visible();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mClienteLinearLayout.setOnClickListener(this);
        mOkButton.setOnClickListener(this);
    }
}

