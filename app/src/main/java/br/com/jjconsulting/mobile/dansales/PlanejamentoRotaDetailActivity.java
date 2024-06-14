package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.com.jjconsulting.mobile.dansales.adapter.PlanejamentoRotaAdapter;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.database.PlanejamentoRotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.JJSyncRotaGuiada;
import br.com.jjconsulting.mobile.dansales.util.PlanejamentoRotaUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEvent;
import br.com.jjconsulting.mobile.jjlib.view.calendarEventView.JJCalendarEventView;

public class PlanejamentoRotaDetailActivity extends BaseActivity {

    private static final String KEY_CURRENT_DATE = "key_current_Date";
    private static final String KEY_CURRENT_USER = "key_current_user";

    private static final int DETAIL_RG_REQUEST_CODE = 3;
    private static final int COACHING_REQUEST_CODE = 4;

    private RotaGuiadaDao mRotaGuiadaDao;

    private ProgressDialog progressDialog;

    private RecyclerView mRotasRecyclerView;

    private JJCalendarEventView jjCalendarEventView;

    private PlanejamentoRotaAdapter planejamentoRotaAdapter;

    private List<JJCalendarEvent> calendarEventList;

    private  JJSyncRotaGuiada jjSyncRotaGuiada;

    private Date mDateRotaGuiada;

    private Usuario usuario;

    private Rotas rotaSelected;

    private JJCalendarEvent calendarEventSelected;

    private PlanejamentoRotaGuiadaDao mPlanejamentoRotaGuiadaDao;

    private boolean isForceUpdate;

    public PlanejamentoRotaDetailActivity() {
    }

    public static Intent newInstance(Context context, Date date, Usuario usuario) {
        Intent it = new Intent(context, PlanejamentoRotaDetailActivity.class);
        it.putExtra(KEY_CURRENT_DATE, date.getTime());

        if(usuario != null){
            Gson gson = new Gson();
            String json = gson.toJson(usuario);

            it.putExtra(KEY_CURRENT_USER, json);
        }

        return it;
    }

    @Override
    public void onResume(){
        super.onResume();

        if(isForceUpdate){
            isForceUpdate  = false;

            loadingEvents();

            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_planejamento_rotas_detail);

        mRotaGuiadaDao = new RotaGuiadaDao(this);
        jjSyncRotaGuiada = new JJSyncRotaGuiada();
        mPlanejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(PlanejamentoRotaDetailActivity.this);

        mDateRotaGuiada = new Date();
        mDateRotaGuiada.setTime(getIntent().getExtras().getLong(KEY_CURRENT_DATE, 0));
        mDateRotaGuiada = FormatUtils.resetTimeToMidnight(mDateRotaGuiada);

        String jsonUser = getIntent().getExtras().getString(KEY_CURRENT_USER);

        if(!TextUtils.isNullOrEmpty(jsonUser)){
            usuario = gson.fromJson(jsonUser, Usuario.class);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDateRotaGuiada);

        jjCalendarEventView = new JJCalendarEventView(this, mDateRotaGuiada);
        jjCalendarEventView.setEnableExpandable(true);
        jjCalendarEventView.setExpandable(true);
        jjCalendarEventView.setChangeColorDate(true);
        jjCalendarEventView.setOnItemClickListener((index, date)-> {
            mDateRotaGuiada = date;
            loadingEvents();
        });


        jjCalendarEventView.startCalendar();

        LinearLayout calendarLinearLayout = findViewById(R.id.calendar_linear_layout);
        calendarLinearLayout.addView(jjCalendarEventView);

        mRotasRecyclerView = findViewById(R.id.rg_recycler_view);

        DividerItemDecoration divider = new DividerItemDecoration(mRotasRecyclerView.getContext(), DividerItemDecoration.VERTICAL);

        mRotasRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        divider.setDrawable(this.getResources().getDrawable(R.drawable.custom_divider));
        mRotasRecyclerView.addItemDecoration(divider);

        ItemClickSupport.addTo(mRotasRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    calendarEventSelected = planejamentoRotaAdapter.getRotas().get(position);

                    if(UsuarioUtils.isPromotor(usuario.getCodigoFuncao())){
                        Rotas rota = mRotaGuiadaDao.getRota(calendarEventSelected.getCodCliente(), calendarEventSelected.getDate(),
                                usuario, Current.getInstance(this).getUnidadeNegocio().getCodigo(), true, false);
                        Intent it = RotaGuiadaDetailActivity.newIntent(this, rota, true);
                        startActivityForResult(it, DETAIL_RG_REQUEST_CODE);
                    } else {
                          switch (calendarEventSelected.getType()){
                              default:
                                  dialogsDefault.showDialogQuestionNote(getString(R.string.planejamneto_rota_note), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogNote() {
                                      @Override
                                      public void onClickPositive(String note) {
                                          mPlanejamentoRotaGuiadaDao.updatePlanejamentoRota(note, RotaGuiadaUtils.STATUS_RG_FINALIZADO, calendarEventSelected.getId());
                                          jjSyncRotaGuiada.syncRotaGuiada(PlanejamentoRotaDetailActivity.this, null);

                                          loadingEvents();

                                          Intent returnIntent = new Intent();
                                          setResult(Activity.RESULT_OK,returnIntent);
                                      }

                                      @Override
                                      public void onClickNegative() {

                                      }
                                  });
                                  break;
                              case 1:
                              case 2:
                                  Usuario usuario = new Usuario();
                                  usuario.setCodigo(calendarEventSelected.getUserID());

                                  rotaSelected = mRotaGuiadaDao.getRota(calendarEventSelected.getCodCliente(), calendarEventSelected.getDate(),
                                          usuario, Current.getInstance(this).getUnidadeNegocio().getCodigo(), false, true);

                                  jjSyncRotaGuiada.syncRotaGuiada(PlanejamentoRotaDetailActivity.this, null);
                                  if(rotaSelected.getCodRegFunc() != null){
                                      Intent it;
                                      if(calendarEventSelected.getType() == PlanejamentoRotaUtils.VISITAPROMOTOR.getValue()){
                                           it = RotaGuiadaDetailActivity.newIntent(this, rotaSelected, calendarEventSelected.getPromotor(), true, false);
                                      } else {
                                          it = RotaGuiadaDetailActivity.newIntent(this, rotaSelected, false);
                                      }

                                      startActivityForResult(it, DETAIL_RG_REQUEST_CODE);
                                  }
                                  break;
                              case 3:
                                  if(!mPlanejamentoRotaGuiadaDao.isEventEquals(calendarEventSelected.getDate()) && calendarEventSelected.getStatus() == RotaGuiadaUtils.STATUS_RG_NAO_REALIZADO) {
                                      Toast.makeText(this, getString(R.string.planejamento_coaching_promotor_date), Toast.LENGTH_LONG).show();
                                      return;
                                  }

                                  if (calendarEventSelected.getStatus() == RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO) {
                                      Toast.makeText(this, getString(R.string.planejamento_coaching_promotor_answer), Toast.LENGTH_LONG).show();
                                  } else {
                                      if (planejamentoRotaAdapter.isEnableCoaching(position)) {
                                          isForceUpdate = true;
                                          startActivityForResult(PesquisaCoachingActivity.newIntent(this, calendarEventSelected.getPromotor(), mDateRotaGuiada), COACHING_REQUEST_CODE);
                                      } else {
                                          Toast.makeText(this, getString(R.string.planejamento_coaching_promotor_error) + " " + calendarEventSelected.getPromotorName(), Toast.LENGTH_SHORT).show();
                                      }
                                  }
                                  break;
                          }
                    }
                }
        );

        loadingEvents();

    }

    private void loadingEvents(){
        createDialogProgress();
        progressDialog.show();

        ExecutorService executors = Executors.newFixedThreadPool(1);
        Runnable runnable = ()-> {

            PlanejamentoRotaGuiadaDao planejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(this);

            String unidadeNegocio = Current.getInstance(this).getUnidadeNegocio().getCodigo();
            String currentUser = Current.getInstance(this).getUsuario().getCodigo();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mDateRotaGuiada);

            if(!UsuarioUtils.isPromotor(usuario.getCodigoFuncao())){
                calendarEventList = planejamentoRotaGuiadaDao.getGAEvents(usuario.getCodigo(), unidadeNegocio, calendar);
            } else {
                calendarEventList = planejamentoRotaGuiadaDao.getPromotorEvents(usuario, unidadeNegocio , calendar);

                for(int ind = 0; ind < calendarEventList.size(); ind++){
                    JJCalendarEvent event = calendarEventList.get(ind);

                    boolean hasEvent;

                    try {
                        Date date = FormatUtils.resetTimeToMidnight(new Date());

                        if (FormatUtils.toDate(event.getDate()).after(date)) {
                            if (event.getType() == 1 && !currentUser.equals(event.getUserID())) {
                                hasEvent = !planejamentoRotaGuiadaDao.hasVisitaPromotor(event, currentUser, unidadeNegocio);
                                calendarEventList.get(ind).setAdd(hasEvent);
                            }
                        }
                    }catch (Exception ex){
                        LogUser.log(ex.toString());
                    }
                }
            }

            runOnUiThread(()->{
                planejamentoRotaAdapter = new PlanejamentoRotaAdapter(PlanejamentoRotaDetailActivity.this, calendarEventList, new PlanejamentoRotaAdapter.OnClickAddEvent() {
                    @Override
                    public void onClickAdd(JJCalendarEvent calendarEvent) {
                        String codUser = Current.getInstance(PlanejamentoRotaDetailActivity.this).getUsuario().getCodigo();
                        String unNeg = Current.getInstance(PlanejamentoRotaDetailActivity.this).getUnidadeNegocio().getCodigo();

                        calendarEvent.setPromotor(calendarEvent.getUserID());
                        calendarEvent.setUserID(codUser);
                        calendarEvent.setType(PlanejamentoRotaUtils.VISITAPROMOTOR.getValue());
                        calendarEvent.setUnNeg(unNeg);

                        UsuarioDao usuarioDao = new UsuarioDao(PlanejamentoRotaDetailActivity.this);
                        Usuario usuario =  usuarioDao.get(calendarEvent.getPromotor());
                        calendarEvent.setPromotorName(usuario.getNome());

                        planejamentoRotaGuiadaDao.insertPlanejamentoRota(calendarEvent);
                        planejamentoRotaGuiadaDao.insertRoute(calendarEvent);

                        jjSyncRotaGuiada.syncRotaGuiada(PlanejamentoRotaDetailActivity.this, null);
                        loadingEvents();
                    }

                    @Override
                    public void onClickRemove(JJCalendarEvent calendarEvent) {
                        dialogsDefault.showDialogQuestion(getString(R.string.planejamneto_rota_remove_question), dialogsDefault.DIALOG_TYPE_QUESTION, new DialogsCustom.OnClickDialogQuestion() {
                            @Override
                            public void onClickPositive() {
                                if(calendarEvent.getStatus() == RotaGuiadaUtils.STATUS_RG_NAO_INICIADO){

                                    mPlanejamentoRotaGuiadaDao.managerDeletePlanejamentoRota(calendarEvent);
                                    jjSyncRotaGuiada.syncRotaGuiada(PlanejamentoRotaDetailActivity.this, null);

                                    loadingEvents();

                                    Intent returnIntent = new Intent();
                                    setResult(Activity.RESULT_OK, returnIntent);
                                } else {
                                    Toast.makeText(PlanejamentoRotaDetailActivity.this,  getString(R.string.planejamneto_rota_remove_error), Toast.LENGTH_LONG).show();
                                }

                            }

                            @Override
                            public void onClickNegative() {

                            }
                        });
                    }
                });
                mRotasRecyclerView.setAdapter(planejamentoRotaAdapter);
            });

            progressDialog.dismiss();

        };

        executors.submit(runnable);

    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        String msg = getString(R.string.aguarde);
        progressDialog.setMessage(msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            default:
                super.onActivityResult(requestCode, resultCode, data);
                if(rotaSelected != null) {
                    Rotas rota = mRotaGuiadaDao.getRota(rotaSelected.getCodCliente(), rotaSelected.getDate(),
                            usuario, Current.getInstance(this).getUnidadeNegocio().getCodigo(), false, true);

                    if (rota.getStatus() != calendarEventSelected.getStatus()) {
                        mPlanejamentoRotaGuiadaDao.updatePlanejamentoRota(calendarEventSelected.getNote(),
                                rota.getStatus(), calendarEventSelected.getId());

                        jjSyncRotaGuiada.syncRotaGuiada(this, null);

                        loadingEvents();

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                    }

                    rotaSelected = null;
                }
                break;
        }
    }
}
