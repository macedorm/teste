package br.com.jjconsulting.mobile.dansales;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.jjconsulting.mobile.dansales.adapter.PerguntasPesquisaAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskPerguntas;
import br.com.jjconsulting.mobile.dansales.base.BaseActivity;
import br.com.jjconsulting.mobile.dansales.connectionController.BaseConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncPlanejamentoRotaGuiadaConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncRespostasPesquisaConnection;
import br.com.jjconsulting.mobile.dansales.connectionController.SyncRespostasPesquisaLayoutConnection;
import br.com.jjconsulting.mobile.dansales.data.SyncPesquisa;
import br.com.jjconsulting.mobile.dansales.database.LayoutDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaRespostaDao;
import br.com.jjconsulting.mobile.dansales.database.PlanejamentoRotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.model.CondicaoPerguntaPesquisa;
import br.com.jjconsulting.mobile.dansales.model.LayoutUserSync;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPergunta;
import br.com.jjconsulting.mobile.dansales.model.PesquisaPerguntaType;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.dansales.model.ResumeStore;
import br.com.jjconsulting.mobile.dansales.model.TFreq;
import br.com.jjconsulting.mobile.dansales.model.TPesquisaEdit;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.service.CurrentActionPesquisa;
import br.com.jjconsulting.mobile.dansales.util.ManagerSystemUpdate;
import br.com.jjconsulting.mobile.jjlib.Connection;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.dao.entity.TRegSync;
import br.com.jjconsulting.mobile.jjlib.model.ValidationLetter;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.ImageCameraGallery;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class PesquisaPerguntasActivity extends BaseActivity implements
        AsyncTaskPerguntas.OnAsyncResponse, PesquisaPerguntasDialog.OnPerguntasClickListener {

    private static final String ARG_CODIGO_CLIENTE = "codigo_cliente";
    private static final String ARG_PESQUISA = "objeto_pesquisa";
    private static final String ARG_BLOCK_RESUME = "objeto_block_resume";

    private AsyncTaskPerguntas asyncTaskPerguntas;
    private PesquisaRespostaDao mPesquisaRespostaDao;
    private List<PesquisaPergunta> mPesquisaPergunta;
    private Pesquisa mPesquisa;

    private PesquisaPerguntasDialog mPesquisaPerguntasDialog;
    private PesquisaResumoDialog mPesquisaResumoDialog;
    private RecyclerView mPerguntasRecyclerView;
    private PerguntasPesquisaAdapter mPerguntasAdapter;
    private LinearLayout mListEmptyLinearLayout;
    private ProgressDialog progressDialog;
    private ViewGroup mContainerViewGroup;

    private String codigoCliente;
    private int indexSelected;

    private LocationManager locationManager;

    private double latitude;
    private double longitude;

    private int numberCond;

    private boolean isBlockEdit;
    private boolean isBlockOpenResume;

    private String mFrequencia;

    Map<Integer, Integer> repostaDependecia = new HashMap<>();

    private SyncPesquisa syncPesquisa;

    public static Intent newIntent(Context context, String codigoCliente, Pesquisa pesquisa) {
        Intent intent = new Intent(context, PesquisaPerguntasActivity.class);
        intent.putExtra(ARG_CODIGO_CLIENTE, codigoCliente);
        intent.putExtra(ARG_PESQUISA, pesquisa);
        return intent;
    }

    public static Intent newIntent(Context context, String codigoCliente, boolean blockResume, Pesquisa pesquisa) {
        Intent intent = new Intent(context, PesquisaPerguntasActivity.class);
        intent.putExtra(ARG_CODIGO_CLIENTE, codigoCliente);
        intent.putExtra(ARG_PESQUISA, pesquisa);
        intent.putExtra(ARG_BLOCK_RESUME, blockResume);

        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesquisa_detail);

        mPesquisa = (Pesquisa) getIntent().getSerializableExtra(ARG_PESQUISA);
        codigoCliente = getIntent().getStringExtra(ARG_CODIGO_CLIENTE);
        isBlockOpenResume = getIntent().getBooleanExtra(ARG_BLOCK_RESUME, false);

        getSupportActionBar().setTitle(getString(R.string.title_pesquisa));
        getSupportActionBar().setSubtitle(mPesquisa.getNome());

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mPerguntasRecyclerView = findViewById(R.id.pesquisa_detail_recycler_view);
        mListEmptyLinearLayout = findViewById(R.id.list_empty_text_view);
        mContainerViewGroup = findViewById(R.id.pedido_detail_linear_layout);

        mPerguntasRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mPerguntasRecyclerView.addItemDecoration(new DividerItemDecoration(this, 0));

        ItemClickSupport.addTo(mPerguntasRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    try {
                        if (isBlockEdit) {
                            dialogsDefault.showDialogMessage(getString(R.string.pesquisa_sync_no_edit), dialogsDefault.DIALOG_TYPE_WARNING, null);
                        } else {

                            if (!mPesquisaPergunta.get(position).isDisable()) {
                                showDialogPergunta(mPesquisaPergunta.get(position), position);
                                indexSelected = position;
                            } else {
                                dialogsDefault.showDialogMessage(getString(R.string.pesquisa_sync_error_question_disable), dialogsDefault.DIALOG_TYPE_WARNING, null);
                            }
                        }
                    } catch (Exception ex) {
                        LogUser.log(Config.TAG, ex.toString());
                    }

                });

        createDialogProgress();
        init();
    }

    private void init() {
        if (codigoCliente == null || codigoCliente.length() == 0) {
            codigoCliente = "0000000000";
        }

        mFrequencia = TFreq.getFreq(mPesquisa.getFreq(),  mPesquisa.getCurrentDate());

        if(TextUtils.isNullOrEmpty(mFrequencia)){
            dialogsDefault.showDialogMessage(getString(R.string.pesquisa_frequencio_erro), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
                @Override
                public void onClick() {
                    finish();
                }
            });
            return;
        }

        loadingPerguntas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.pesquisa_menu, menu);

        MenuItem menuResume = menu.findItem(R.id.action_resume);
        if (mPesquisa.getTipo() == Pesquisa.PESQUISA || mPesquisa.getTipo() == Pesquisa.ATIVIDADE) {
            menuResume.setVisible(false);
        } else {
            menuResume.setVisible(!isBlockEdit);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                if (mPesquisa.getStatus() == Pesquisa.PUBLICADO) {
                    if (isBlockEdit) {
                        dialogsDefault.showDialogMessage(getString(R.string.pesquisa_sync_no_edit), dialogsDefault.DIALOG_TYPE_WARNING, null);
                    } else {
                        syncAnswers();
                    }
                } else {
                    dialogsDefault.showDialogMessage(getString(R.string.pesquisa_sync_error_status), dialogsDefault.DIALOG_TYPE_WARNING, null);
                }
                return true;
            case R.id.action_resume:
                loadingResume(true, codigoCliente, String.valueOf(mPesquisa.getCodigo()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageCameraGallery.OPEN_CAMERA:
                if(mPesquisaPerguntasDialog != null)
                    mPesquisaPerguntasDialog.onActivityResult(requestCode, resultCode, data);
                break;
            case PesquisaResumoDialog.SELECT_LAYOUT:
                if (resultCode == RESULT_OK) {
                    if (mPesquisaResumoDialog != null)
                        mPesquisaResumoDialog.onActivityResult(requestCode, resultCode, data);
                }
                break;

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void onPerguntaClickOK(PesquisaPergunta pesquisaPergunta) {
        if (pesquisaPergunta == null) {
            if (indexSelected > 0) {
                indexSelected--;
                showDialogPergunta(mPesquisaPergunta.get(indexSelected), indexSelected);
            }
        } else {

            if (mPesquisaPergunta == null || indexSelected >= mPesquisaPergunta.size()) {
                return;
            }

            saveAnswers(pesquisaPergunta);

            if (pesquisaPergunta.getPesquisaPerguntaParent() != null && pesquisaPergunta.getPesquisaPerguntaParent().size() > 0) {
                for (PesquisaPergunta parentPesquisaPergunta : pesquisaPergunta.getPesquisaPerguntaParent()) {
                    saveAnswers(parentPesquisaPergunta);
                }
            }

            //Remove respostas de uma pergunta que depente da resposta de outra para seu enunciado.
            if (repostaDependecia.containsKey(indexSelected)) {
                int positionDependencia = repostaDependecia.get(indexSelected);

                ArrayList<PesquisaResposta> pesquisaRespostas = mPesquisaPergunta.get(positionDependencia).getRespostaPesquisa();
                mPesquisaPergunta.get(positionDependencia).setRespostaPesquisa(new ArrayList<>());

                if (pesquisaRespostas != null && pesquisaRespostas.size() > 0) {
                    mPesquisaRespostaDao.deleteResposta(pesquisaRespostas.get(0));
                    mPerguntasAdapter.updateItem(mPesquisaPergunta.get(positionDependencia), positionDependencia);
                }
            }


            mPesquisaPergunta.set(indexSelected, pesquisaPergunta);

            //Determina qual a proxima pergunta a ser exibida
            List<CondicaoPerguntaPesquisa> condPerPesquisa = mPesquisaPergunta.get(indexSelected).getCondicaoPerguntaPesquisa();
            if (condPerPesquisa == null || condPerPesquisa.size() == 0) {
                mPerguntasAdapter.updateItem(pesquisaPergunta, indexSelected);
                indexSelected++;
            } else {
                int numberAnswersCondition = -1;
                int indexNextQuestion = -1;

                for (int ind = indexSelected; ind < mPesquisaPergunta.size(); ind++) {
                    PesquisaPergunta perguntaPesquisa = mPesquisaPergunta.get(ind);

                    perguntaPesquisa.setDisable(false);

                    if (ind < numberAnswersCondition) {
                        perguntaPesquisa.setDisable(true);
                    } else if (ind == numberAnswersCondition) {
                        indexNextQuestion = ind;
                    }

                    if (perguntaPesquisa.getRespostaPesquisa() != null &&
                            perguntaPesquisa.getRespostaPesquisa().size() > 0) {
                        for (PesquisaResposta item : perguntaPesquisa.getRespostaPesquisa()) {
                            if (ind == indexSelected) {
                                numberAnswersCondition = getIndex(GetNumberAnswersCondition(condPerPesquisa, item));
                            } else {
                                perguntaPesquisa.getRespostaPesquisa().remove(item);
                                mPesquisaRespostaDao.deleteResposta(item);
                            }
                        }

                        if (numberAnswersCondition == -1) {
                            if (ind + 1 < mPesquisaPergunta.size()) {
                                numberAnswersCondition = ind + 1;
                            }
                        }
                    }

                }

                mPerguntasAdapter.updateRange(mPesquisaPergunta, indexSelected);
                indexSelected = indexNextQuestion;
            }

            if ((indexSelected) < mPesquisaPergunta.size()) {
                try {
                    showDialogPergunta(mPesquisaPergunta.get(indexSelected), indexSelected);
                } catch (Exception ex) {
                    dialogsDefault.showDialogMessage(getString(R.string.pergunta_erro), dialogsDefault.DIALOG_TYPE_ERROR, null);
                }
            }
        }
    }

    private boolean isBlockEditPesquisa() {
        isBlockEdit = false;
        for (PesquisaPergunta pergunta : mPesquisaPergunta) {
            for (PesquisaResposta resposta : pergunta.getRespostaPesquisa()) {
                if (resposta.getRegSync() != TRegSync.INSERT) {
                    isBlockEdit = true;
                }
            }
            if (isBlockEdit) {
                break;
            }
        }

        return isBlockEdit;
    }

    private int getOrdem(int numPergunta) {
        int ordem = -1;

        for (PesquisaPergunta pesquisaPergunta : mPesquisaPergunta) {

            if (pesquisaPergunta.getNumPergunta() == numPergunta) {
                ordem = pesquisaPergunta.getOrdem();
                break;
            }
        }

        return ordem;

    }

    private int getIndex(int numPergunta) {
        int index = -1;

        for (int ind = 0; ind < mPesquisaPergunta.size(); ind++) {
            PesquisaPergunta pesquisaPergunta = mPesquisaPergunta.get(ind);

            if (pesquisaPergunta.getNumPergunta() == numPergunta) {
                index = ind;
                break;
            }
        }

        return index;

    }


    private int GetNumberAnswersCondition(List<CondicaoPerguntaPesquisa> condPerPesquisa, PesquisaResposta item) {
        int numberAnswersCondition = -1;

        try {
            String resp;
            if (item.getOpcao() == null || item.getOpcao().length() == 0) {
                resp = item.getResposta();
            } else {
                resp = item.getOpcao();
            }

            for (CondicaoPerguntaPesquisa condition : condPerPesquisa) {
                int result;
                switch (condition.getOperador()) {
                    case IGUAL:
                        if (resp.equals(condition.getCondicaoValor1())) {
                            numberAnswersCondition = condition.getCondicaoNumPergunta();
                        }
                        break;
                    case DIFERENTE:
                        if (!resp.equals(condition.getCondicaoValor1())) {
                            numberAnswersCondition = condition.getCondicaoNumPergunta();
                        }
                        break;
                    case MAIOR:
                        result = resp.compareTo(condition.getCondicaoValor1());
                        if (result > 0) {
                            numberAnswersCondition = condition.getCondicaoNumPergunta();
                        }
                        break;
                    case MAIOR_OU_IGUAL:
                        result = resp.compareTo(condition.getCondicaoValor1());
                        if (result >= 0) {
                            numberAnswersCondition = condition.getCondicaoNumPergunta();
                        }
                        break;
                    case MENOR:
                        result = resp.compareTo(condition.getCondicaoValor1());
                        if (result < 0) {
                            numberAnswersCondition = condition.getCondicaoNumPergunta();
                        }
                        break;
                    case MENOR_OU_IGUAL:
                        result = resp.compareTo(condition.getCondicaoValor1());
                        if (result <= 0) {
                            numberAnswersCondition = condition.getCondicaoNumPergunta();
                        }
                        break;
                    case ENTRE:
                        int res1 = resp.compareTo(condition.getCondicaoValor1());
                        int res2 = resp.compareTo(condition.getCondicaoValor2());
                        if ((res1 >= 0) && (res2 <= 0)) {
                            numberAnswersCondition = condition.getCondicaoNumPergunta();
                        }
                        break;
                    case CONTEM:
                        if (resp.contains(";")) {
                            String arrayRes[] = resp.split(";");

                            for (String res : arrayRes) {
                                if (res.trim().equals(condition.getCondicaoValor1().trim())) {
                                    numberAnswersCondition = condition.getCondicaoNumPergunta();
                                }
                            }

                        } else {
                            if (resp.contains(condition.getCondicaoValor1())) {
                                numberAnswersCondition = condition.getCondicaoNumPergunta();
                            }
                        }

                        break;
                    case NAO_CONTEM:

                        if (resp.contains(";")) {
                            String arrayRes[] = resp.split(";");

                            for (String res : arrayRes) {
                                if (!res.trim().equals(condition.getCondicaoValor1().trim())) {
                                    numberAnswersCondition = condition.getCondicaoNumPergunta();
                                } else {
                                    numberAnswersCondition = -1;
                                }
                            }

                        } else {
                            if (!resp.contains(condition.getCondicaoValor1())) {
                                numberAnswersCondition = condition.getCondicaoNumPergunta();
                            }
                        }

                        break;
                }
            }
        }catch (Exception ex){
            LogUser.log(ex.getMessage());
        }
        return numberAnswersCondition;
    }

    @Override
    public void processFinish(List<PesquisaPergunta> objects) {
        mPesquisaPergunta = (objects);
        mPerguntasAdapter = new PerguntasPesquisaAdapter(PesquisaPerguntasActivity.this, mPesquisaPergunta);
        mPerguntasRecyclerView.setAdapter(mPerguntasAdapter);

        for (int ind = 0; ind < mPesquisaPergunta.size(); ind++) {
            organizeAllQuestionCondition(ind);
            checkPerguntaDependencia(ind);
        }

        if (mPerguntasAdapter.getPerguntas().size() == 0) {
            mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            mPerguntasRecyclerView.setVisibility(View.GONE);
        } else {
            mListEmptyLinearLayout.setVisibility(View.GONE);
            mPerguntasRecyclerView.setVisibility(View.VISIBLE);
        }

        if (latitude == 0 || longitude == 0) {
            getLocation();
        }

        if (mPesquisa.getEdit() == TPesquisaEdit.VIEW_ONLY_ANSWER) {
            isBlockEditPesquisa();
        } else if (mPesquisa.getEdit() == TPesquisaEdit.NO_VIEW_AND_EDIT_ANSWER) {

            boolean blockEdit = false;
            for (PesquisaPergunta pesquisaPergunta : mPesquisaPergunta) {
                for (PesquisaResposta pesquisaResposta : pesquisaPergunta.getRespostaPesquisa()) {
                    if (pesquisaResposta.getRegSync() == TRegSync.SEND) {
                        blockEdit = true;
                        break;
                    }
                }
            }

            if (blockEdit) {
                dialogsDefault.showDialogMessage(getString(R.string.pesquisa_sync_no_edit), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
                mContainerViewGroup.setVisibility(View.GONE);

                return;
            }
        }

        if (!isBlockOpenResume && (mPesquisa.getTipo() == Pesquisa.CHECKLIST || mPesquisa.getTipo() == Pesquisa.CHECKLIST_RG)) {
            showDialogResume(true);
        }

        //Pesquisa tipo coaching abre a tela de resumo de notas
        if(!isBlockEdit && mPesquisa.getTipo() == Pesquisa.COACHING){
            loadingResume(false, codigoCliente, String.valueOf(mPesquisa.getCodigo()));
        }

        invalidateOptionsMenu();

    }

    private void loadingPerguntas() {
        try {
            mPerguntasAdapter = new PerguntasPesquisaAdapter(this, new ArrayList<>());
            mPerguntasRecyclerView.setAdapter(mPerguntasAdapter);
            mPerguntasAdapter.resetData();
            asyncLoadPerguntas();
            mPerguntasAdapter.addLoadingFooter();
        } catch (Exception ex) {
            LogUser.log(Config.TAG, "applyFilterOnData: " + ex.toString());
        }
    }

    private void asyncLoadPerguntas() {
        if (asyncTaskPerguntas != null) {
            asyncTaskPerguntas.cancel(true);
        }
        mPesquisaRespostaDao = new PesquisaRespostaDao(this, mPesquisa.getCurrentDate());

        //Carrega layout escolhido pelo usuário
        if (mPesquisa.getLayout() == null) {
            LayoutDao layoutDao = new LayoutDao(this);
            mPesquisa.setLayout(layoutDao.getLayoutUser(mPesquisa.getCodigo(),
                    Current.getInstance(this).getUsuario().getCodigo(), codigoCliente, mFrequencia));
        }

        asyncTaskPerguntas = new AsyncTaskPerguntas(this, mPesquisa.getCodigo(), codigoCliente,
                mPesquisa.getLayout(), mPesquisa.getFreq(), mPesquisa.getCurrentDate(), this);
        asyncTaskPerguntas.execute();
    }

    private void showDialogPergunta(PesquisaPergunta pesquisaPergunta, int position) {
        mPesquisaPerguntasDialog = new PesquisaPerguntasDialog(this, this,
                pesquisaPergunta, mPesquisa.getCurrentDate(), codigoCliente, mPesquisaPergunta.size(), position);

        boolean isOpen = true;

        String message = "";

       if (pesquisaPergunta.getTipo() == PesquisaPerguntaType.MULTI_SELECAO && pesquisaPergunta.getIdDependeciaResposta() != 0) {

            //Recupera as respostas  usada na dependecia dessa pergunta
            PesquisaPergunta pesquisaPerguntaDep = null;

            String perguntaDepDesc = "";

            for (PesquisaPergunta item : mPesquisaPergunta) {
                if (item.getNumPergunta() == pesquisaPergunta.getIdDependeciaResposta()) {
                    pesquisaPerguntaDep = item;
                    perguntaDepDesc = TextUtils.firstLetterUpperCase(item.getDescPergunta());

                    break;
                }
            }

            String codigoUsuario = Current.getInstance(this).getUsuario().getCodigo();
            ArrayList<PesquisaResposta> pesquisaRespostaDep = mPesquisaRespostaDao.getRespostaPesquisa(codigoUsuario, codigoCliente, mPesquisa.getFreq(), pesquisaPerguntaDep);

            //Verifica se a pergunta que contem as repostas usadas como opção
            if (pesquisaRespostaDep == null || pesquisaRespostaDep.size() == 0) {
                message = getString(R.string.pesquisa_pergunta_dependecia_resp_error, perguntaDepDesc);
                isOpen = false;
            } else {
                mPesquisaPerguntasDialog.setPesquisaRespostaDep(pesquisaRespostaDep);
            }
        }

        if (!isOpen) {
            dialogsDefault.showDialogMessage(message, dialogsDefault.DIALOG_TYPE_WARNING, null);
        } else {
            mPesquisaPerguntasDialog.show();
        }

    }

    private void showDialogResume(boolean isShowTitle) {
        String unidadeNegocio = Current.getInstance(this).getUnidadeNegocio().getCodigo();
        String codigoUsuario = Current.getInstance(this).getUsuario().getCodigo();

        try {
            mPesquisaResumoDialog = new PesquisaResumoDialog(this, isShowTitle, codigoCliente, mPesquisa, unidadeNegocio, codigoUsuario);
            mPesquisaResumoDialog.show();        }
        catch (WindowManager.BadTokenException e) {
            LogUser.log(e.getMessage());
        }
    }

    /**
     * Armazena indice de todas as perguntas que mostra suas opcoes com base na resposta de outra pergunta
     * Key do hash: contem a posicao da pergunta que fornece as opcoes
     * Value da hash: contem a posicao da pergunta que usa a resposta como opcao
     * Hash usada para apagar as respostas da pergunta dependentem, quando a pergunta que fornce as opcoes e alterada sua resposta
     *
     * @param indDep
     */
    private void checkPerguntaDependencia(int indDep) {
        if (mPesquisaPergunta.get(indDep).getTipo() == PesquisaPerguntaType.MULTI_SELECAO) {
            int dependeciaResposta = mPesquisaPergunta.get(indDep).getIdDependeciaResposta();
            if (dependeciaResposta != 0) {
                for (int ind = 0; ind < mPesquisaPergunta.size(); ind++) {
                    if (mPesquisaPergunta.get(ind).getNumPergunta() == dependeciaResposta) {
                        repostaDependecia.put(ind, indDep);
                    }

                }
            }
        }
    }


    private void saveAnswers(PesquisaPergunta pesquisaPergunta) {

        if (pesquisaPergunta.getRespostaPesquisa() == null || pesquisaPergunta.getRespostaPesquisa().size() == 0) {
            PesquisaResposta item = new PesquisaResposta();

            item.setCodigoUsuario(Current.getInstance(this).getUsuario().getCodigo());
            item.setCodigoCliente(codigoCliente);
            item.setCodigoPesquisa(pesquisaPergunta.getIdPequisa());
            item.setCodigoPergunta(pesquisaPergunta.getNumPergunta());
            item.setPosLatitute(latitude);
            item.setPosLongitude(longitude);
            item.setFreq(mFrequencia);
            mPesquisaRespostaDao.insertResposta(item);
        } else {
            for (PesquisaResposta item : pesquisaPergunta.getRespostaPesquisa()) {
                item.setCodigoUsuario(Current.getInstance(this).getUsuario().getCodigo());
                item.setCodigoCliente(codigoCliente);
                item.setCodigoPesquisa(pesquisaPergunta.getIdPequisa());
                item.setCodigoPergunta(pesquisaPergunta.getNumPergunta());
                item.setPosLatitute(latitude);
                item.setPosLongitude(longitude);
                item.setFreq(mFrequencia);
                mPesquisaRespostaDao.insertResposta(item);
            }
        }

        CurrentActionPesquisa.getInstance().setUpdateListPesquisa(true);
    }


    private void organizeAllQuestionCondition(int ind) {
        if (ind == 0)
            numberCond = -1;

        if (mPesquisaPergunta.get(ind).getTipo() == PesquisaPerguntaType.OUTROS) {
            try {
                if (mPesquisaPergunta.get(ind - 1).getRespostaPesquisa() == null || mPesquisaPergunta.get(ind - 1).getRespostaPesquisa().size() == 0) {
                    mPesquisaPergunta.get(ind).setDisable(true);
                } else if (ind < numberCond) {
                    mPesquisaPergunta.get(ind).setDisable(true);
                }
            } catch (Exception ex) {
                mPesquisaPergunta.get(ind).setDisable(true);
            }

        } else if (ind < numberCond) {
            mPesquisaPergunta.get(ind).setDisable(true);
        } else {
            numberCond = -1;
            if (numberCond == -1 || numberCond == ind) {
                numberCond = getIndex(getCond(mPesquisaPergunta.get(ind)));

            } else if (numberCond < ind) {
                mPesquisaPergunta.get(ind).setDisable(true);
            }
        }
    }

    private int getCond(PesquisaPergunta pesquisaPergunta) {
        int cond = -1;
        if (pesquisaPergunta.getRespostaPesquisa() != null && pesquisaPergunta.getRespostaPesquisa().size() > 0) {
            for (PesquisaResposta item : pesquisaPergunta.getRespostaPesquisa()) {

                if (latitude == 0 || longitude == 0) {
                    latitude = item.getPosLatitute();
                    longitude = item.getPosLongitude();
                }

                cond = GetNumberAnswersCondition(pesquisaPergunta.getCondicaoPerguntaPesquisa(), item);
            }
        } else {
            cond = -1;
        }
        return cond;
    }

    private void syncAnswers() {
        boolean isError = false;

        if (getWindow().getDecorView().isShown()) {
            progressDialog.show();
        }

        ArrayList<PesquisaResposta> respostasSync = new ArrayList<>();

        if(mPesquisaPergunta == null){
            return;
        }

        for (PesquisaPergunta item : mPesquisaPergunta) {
            if (((item.getRespostaPesquisa() == null || item.getRespostaPesquisa().size() == 0)
                    && item.isObrigatoria()) && !item.isDisable()) {
                isError = true;
                break;
            } else if (item.getRespostaPesquisa() != null) {
                for (PesquisaResposta resposta : item.getRespostaPesquisa()) {
                    PesquisaResposta resposta1 = resposta;
                    respostasSync.add(resposta1);
                }

                if (item.getPesquisaPerguntaParent() != null) {
                    for (PesquisaPergunta pesquisaPerguntaParent : item.getPesquisaPerguntaParent()) {
                        for (PesquisaResposta respostaParent : pesquisaPerguntaParent.getRespostaPesquisa()) {
                            respostasSync.add(respostaParent);
                        }
                    }
                }
            }
        }

        if (isError) {
            showErrorValidationSync(getString(R.string.message_error_sync_pesquisa_validation), dialogsDefault.DIALOG_TYPE_ERROR);
            return;
        }

        SyncRespostasPesquisaConnection syncRespostasPesquisaConnection = new SyncRespostasPesquisaConnection(
                getApplicationContext(), new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> responseList) {
                if (getWindow().getDecorView().isShown()) {
                    progressDialog.dismiss();
                }

                syncPesquisa = null;

                try {
                    syncPesquisa = gson.fromJson(response, SyncPesquisa.class);
                } catch (Exception ex) {
                    LogUser.log(ex.getMessage());
                }

                if (syncPesquisa != null && syncPesquisa.getListPilar() != null && syncPesquisa.isSuccess()) {

                    for (PesquisaResposta item : respostasSync) {
                        if (item.getRegSync() != TRegSync.SEND) {
                            mPesquisaRespostaDao.upadateRespostaRegSync(item);
                        }
                    }

                    if(mPesquisa.getTipo() == Pesquisa.COACHING){
                        String user = Current.getInstance(PesquisaPerguntasActivity.this).getUsuario().getCodigo();
                        String unNeg = Current.getInstance(PesquisaPerguntasActivity.this).getUnidadeNegocio().getCodigo();

                        PlanejamentoRotaGuiadaDao planejamentoRotaGuiadaDao = new PlanejamentoRotaGuiadaDao(PesquisaPerguntasActivity.this);
                        planejamentoRotaGuiadaDao.setStatusCoaching(PesquisaPerguntasActivity.this, user, unNeg, codigoCliente,
                                mPesquisa.getCurrentDate());
                    }

                    if (mPesquisa.getTipo() == Pesquisa.CHECKLIST || mPesquisa.getTipo() == Pesquisa.CHECKLIST_RG) {
                        finish();
                        startActivity(PesquisaNotasActivity.newIntent(PesquisaPerguntasActivity.this, codigoCliente, mPesquisa.getCodigo(),
                                mPesquisa.getCurrentDate(), syncPesquisa));
                    } else {
                        CurrentActionPesquisa currentActionPesquisa = CurrentActionPesquisa.getInstance();
                        currentActionPesquisa.setUpdateListPesquisa(true);
                        showErrorValidationSync(getString(R.string.pesquisa_sync_ok_connection),
                                dialogsDefault.DIALOG_TYPE_SUCESS);
                    }
                } else {
                    showErrorValidationSync(syncPesquisa == null ?
                            getString(R.string.sync_connection_error) : syncPesquisa.getMessage(),
                            dialogsDefault.DIALOG_TYPE_ERROR);
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                if (getWindow().getDecorView().isShown()) {
                    progressDialog.dismiss();
                }
                
                if(code == Connection.AUTH_FAILURE || code == Connection.SERVER_ERROR){
                    ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                    if(ManagerSystemUpdate.isRequiredUpadate(PesquisaPerguntasActivity.this, errorConnection.getMessage())){
                        return;
                    }

                    showMessageError(errorConnection.getMessage());
                    return;
                }

                showErrorValidationSync(getString(R.string.pesquisa_sync_error_connection), dialogsDefault.DIALOG_TYPE_ERROR);
            }
        });

        if (mPesquisa.getTipo() == Pesquisa.CHECKLIST || mPesquisa.getTipo() == Pesquisa.CHECKLIST_RG) {
            sendLayout(syncRespostasPesquisaConnection, respostasSync);
        } else {
            syncRespostasPesquisaConnection.syncRespostasPesquisa(respostasSync);
        }
    }

    /**
     * Envia o layout quando for checklist
     * @param syncRespostasPesquisaConnection
     */
    private void sendLayout(SyncRespostasPesquisaConnection syncRespostasPesquisaConnection, ArrayList<PesquisaResposta> respostasSync){
        LayoutUserSync layoutUserSync = null;

        LayoutDao layoutDao = new LayoutDao(this);
        if (mPesquisa.getLayout() == null) {
            String userId = Current.getInstance(this).getUsuario().getCodigo();
            mPesquisa.setLayout(layoutDao.getLayoutUser(mPesquisa.getCodigo(), userId, codigoCliente, mFrequencia));
        }

        layoutUserSync = new LayoutUserSync();
        layoutUserSync.setLay_txt_planogram_code(mPesquisa.getLayout().getCodigo());
        layoutUserSync.setLay_int_pesquisa_id(mPesquisa.getCodigo());
        layoutUserSync.setLay_txt_cliente(codigoCliente);
        layoutUserSync.setLay_txt_dtfreq(mFrequencia);
        layoutUserSync.setLay_int_cod_reg_func(Current.getInstance(this).getUsuario().getCodigo());
        layoutUserSync.setCod_reg_func(Integer.parseInt(Current.getInstance(this).getUsuario().getCodigo()));
        layoutUserSync.setDel_flag("0");
        layoutUserSync.setDt_ult_alt(FormatUtils.toTextToCompareDateInSQlite(mPesquisa.getCurrentDate()));

        if (!layoutDao.isExistsLayoutUser(layoutUserSync)) {
            layoutUserSync = null;
        }

        if (layoutUserSync != null) {
            progressDialog.show();

            SyncRespostasPesquisaLayoutConnection syncRespostasPesquisaLayoutConnection = new SyncRespostasPesquisaLayoutConnection(this, new BaseConnection.ConnectionListener() {
                @Override
                public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> list) {
                    try {

                        ArrayList<ValidationLetter> validationLetters = gson.fromJson(response,
                                new TypeToken<List<ValidationLetter>>() {}.getType());

                        if (validationLetters != null && validationLetters.size() > 0){
                            if(validationLetters.get(0).getStatus() == Connection.CREATED || validationLetters.get(0).getStatus() == Connection.SUCCESS) {
                                syncRespostasPesquisaConnection.syncRespostasPesquisa(respostasSync);
                            } else {
                                showErrorValidationSync(validationLetters.get(0).getMessage(), dialogsDefault.DIALOG_TYPE_ERROR);
                            }

                        } else {
                            showErrorValidationSync(getString(R.string.pesquisa_sync_error_connection), dialogsDefault.DIALOG_TYPE_ERROR);
                        }

                    } catch(Exception e){
                        showErrorValidationSync(getString(R.string.pesquisa_sync_error_connection), dialogsDefault.DIALOG_TYPE_ERROR);
                    }
                }

                @Override
                public void onError(VolleyError volleyError, int code,  int typeConnection, String response) {
                    progressDialog.dismiss();

                    if(code == Connection.AUTH_FAILURE ){
                        ValidationLetter errorConnection = gson.fromJson(response, ValidationLetter.class);
                        if(ManagerSystemUpdate.isRequiredUpadate(PesquisaPerguntasActivity.this, errorConnection.getMessage())){
                            return;
                        }

                        showMessageError(errorConnection.getMessage());
                        return;
                    }

                    showErrorValidationSync(getString(R.string.pesquisa_sync_error_connection), dialogsDefault.DIALOG_TYPE_ERROR);

                }
            });

            syncRespostasPesquisaLayoutConnection.syncRespostasPesquisaLayout(layoutUserSync);
        } else {
            syncRespostasPesquisaConnection.syncRespostasPesquisa(respostasSync);
        }
    }

    private void showErrorValidationSync(String message, int type) {
        if (getWindow().getDecorView().isShown()) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        runOnUiThread(()-> {
            dialogsDefault.showDialogMessage(
                    message,
                    type, null);
        });
    }

    private void createDialogProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.aguarde));
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (latitude == 0 || longitude == 0) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
            fetchCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        try {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    if (location != null) {
                        locationManager.removeUpdates(this);
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }

                @Override
                public void onProviderDisabled(String s) {
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }
            };

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500L, 30f, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void loadingResume(boolean isShowError, String promotor, String codPesquisa){
        dialogsDefault.showDialogProgress(this, getString(R.string.aguarde));
        SyncPlanejamentoRotaGuiadaConnection planejamentoRotaGuiadaConnection = new SyncPlanejamentoRotaGuiadaConnection(this, new BaseConnection.ConnectionListener() {
            @Override
            public void onSucess(String response, int typeConnection, InputStreamReader reader, ArrayList<String[]> list) {
                dialogsDefault.dissmissDialogProgress();

                try{
                    Gson gson = new Gson();
                    ResumeStore resumeStore = gson.fromJson(response, ResumeStore.class);

                    if(resumeStore.isSuccess()) {
                        PlanejamentoRotaResumeStoreDialogFragment planejamentoRotaResumeStoreDialogFragment = PlanejamentoRotaResumeStoreDialogFragment.newInstance(resumeStore);
                        planejamentoRotaResumeStoreDialogFragment.show(getSupportFragmentManager(), "");
                    } else {
                        if(isShowError){
                            showMessageError(getString(R.string.planejamneto_rota_resume_store_no_data));
                        }
                    }

                } catch (Exception ex){
                    if(isShowError){
                        showMessageError(getString(R.string.planejamneto_rota_resume_store_erro));
                    }
                }
            }

            @Override
            public void onError(VolleyError volleyError, int code, int typeConnection, String response) {
                dialogsDefault.dissmissDialogProgress();
                if(isShowError){
                    showMessageError(getString(R.string.planejamneto_rota_resume_store_erro));
                }
            }
        });

        planejamentoRotaGuiadaConnection.getResumeAll(promotor, codPesquisa, Current.getInstance(this).getUnidadeNegocio().getCodigo());
    }
}
