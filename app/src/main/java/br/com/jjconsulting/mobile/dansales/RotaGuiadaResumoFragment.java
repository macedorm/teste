package br.com.jjconsulting.mobile.dansales;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.adapter.RotaGuiadaResumoAdapter;
import br.com.jjconsulting.mobile.dansales.asyncTask.AsyncTaskResumoRotaGuiada;
import br.com.jjconsulting.mobile.dansales.base.BaseFragment;
import br.com.jjconsulting.mobile.dansales.business.PedidoBusiness;
import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaAcessoDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaTarefaDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.JustTask;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.PedidoViewType;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.PerfilVenda;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.RotaOrigem;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTabType;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTaskType;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.TActionRotaGuiada;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.DialogsMultiValue;
import br.com.jjconsulting.mobile.dansales.util.JJSyncRotaGuiada;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.dansales.util.RotaGuiadaUtils;
import br.com.jjconsulting.mobile.dansales.util.TJustTask;
import br.com.jjconsulting.mobile.dansales.viewModel.RotaViewModel;
import br.com.jjconsulting.mobile.jjlib.base.ItemClickSupport;
import br.com.jjconsulting.mobile.jjlib.util.DialogsCustom;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class RotaGuiadaResumoFragment extends BaseFragment implements View.OnClickListener, AsyncTaskResumoRotaGuiada.OnAsyncResponse{

    private static final int NEW_PEDIDO_REQUEST_CODE = 1;
    private static final int NEW_PESQUISA_REQUEST_CODE = 2;

    private RotaViewModel mRotaViewModelProviders;

    private AsyncTaskResumoRotaGuiada mAsyncTaskRotaGuiadaResume;
    private List<RotaTarefas> mRotaResumo;
    private RotaGuiadaTarefaDao mRotaGuiadaTarefaDao;
    private RotaGuiadaResumoAdapter mRotaGuiadaResumoAdapter;

    private ScrollView mRecyclerScrollView;
    private RecyclerView mRotasRecyclerView;

    private LinearLayout mListEmptyLinearLayout;
    private LinearLayout mLinePedidoLinearLayout;

    private RelativeLayout mPedidoLinearLayout;

    private ViewPager viewPager;

    private Button mCheckoutButton;
    private Button mPauseButton;

    private TextView mResumeTitleTextView;
    private TextView mResumeStoreTextView;

    private FloatingActionButton mAddItemFloatingActionButton;

    private PesquisaAcessoDao acessoDao;

    private Perfil perfil;

    private boolean mIsStartLoading;
    private boolean isEdit;

    private String mTarefaIDSel;
    private String atividadSemJustificativa;

    private int indexJustTarefa;

    private  ArrayList<JustTask> listJustTasks;
    private ArrayList<MultiValues> justificativas;

   private Cliente cliente;


    public RotaGuiadaResumoFragment() {
    }

    public static RotaGuiadaResumoFragment newInstance(ViewPager viewPager, boolean isEdit) {
        RotaGuiadaResumoFragment rotaGuiadaResumoFragment = new RotaGuiadaResumoFragment();
        rotaGuiadaResumoFragment.setEdit(isEdit);
        rotaGuiadaResumoFragment.setViewPager(viewPager);
        return rotaGuiadaResumoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRotaViewModelProviders = ViewModelProviders.of(getActivity()).get(RotaViewModel.class);
        RotaGuiadaUtils.checkValidRota(getActivity(), mRotaViewModelProviders.getRotas().getValue(), isEdit);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_rota_guiada_resumo, container, false);

        mRecyclerScrollView = view.findViewById(R.id.rg_resume_scroll_view);
        mRotasRecyclerView = view.findViewById(R.id.rg_resume_recycler_view);
        mCheckoutButton = view.findViewById(R.id.checkout_button);
        mPauseButton = view.findViewById(R.id.pause_button);

        mPedidoLinearLayout = view.findViewById(R.id.rg_pedido_relative_layout);
        mLinePedidoLinearLayout = view.findViewById(R.id.rg_pedido_linear_layout);
        mResumeTitleTextView = view.findViewById(R.id.rg_resume_title_text_view);
        mResumeStoreTextView = view.findViewById(R.id.resume_store_tex_view);

        mListEmptyLinearLayout = view.findViewById(R.id.list_empty_text_view);
        mAddItemFloatingActionButton = view.findViewById(R.id.add_rg_item_floating_action_button);

        mRotaGuiadaTarefaDao = new RotaGuiadaTarefaDao(getActivity());
        acessoDao = new PesquisaAcessoDao(getContext());

        mRotasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        DividerItemDecoration divider = new DividerItemDecoration(mRotasRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getResources().getDrawable(R.drawable.custom_divider));
        mRotasRecyclerView.addItemDecoration(divider);

        ViewCompat.setNestedScrollingEnabled(mRotasRecyclerView, false);
        mRotasRecyclerView.setHasFixedSize(true);

        perfil = Current.getInstance(getContext()).getUsuario().getPerfil();

        findItens();
        addListener();

        return view;
    }

    private boolean checkClick(){
        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

        if(isEdit && (rotas.getStatus() != RotaGuiadaUtils.STATUS_RG_FINALIZADO
                && rotas.getStatus() != RotaGuiadaUtils.STATUS_RG_FORA_ROTA)) {
            return true;
        } else {
            return false;
        }
    }

    private void addListener(){
        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

        mPedidoLinearLayout.setOnClickListener(this);
        mAddItemFloatingActionButton.setOnClickListener(this);
        mCheckoutButton.setOnClickListener(this);
        mPauseButton.setOnClickListener(this);

        ItemClickSupport.addTo(mRotasRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            if (!RotaGuiadaUtils.checkValidRota(getActivity(), mRotaViewModelProviders.getRotas().getValue(), isEdit))
                return;

            if (checkClick()) {
                if (TextUtils.isNullOrEmpty(rotas.getCheckin())) {
                    errorCheckin();
                } else {
                    RotaTarefas rotaTarefas = mRotaGuiadaResumoAdapter.getRotasResumo().get(position);
                    if (rotaTarefas.getStatus() == RotaGuiadaTaskType.PEDIDO) {
                        if(isCheckTipoVenda()){
                            Pedido pedido = rotaTarefas.getPedido();
                            startActivityForResult(PedidoDetailActivity.newIntent(getContext(), pedido.getCodigo(), PedidoViewType.PEDIDO, false, true), NEW_PEDIDO_REQUEST_CODE);
                        }
                    } else {
                        if (isCheckAccessPesquisa(rotaTarefas.getPesquisa().getCodigo())) {

                            if (rotaTarefas.getPesquisa().getStatusResposta() != Pesquisa.OBRIGATORIAS_RESPONDIDAS) {
                                mTarefaIDSel = rotaTarefas.getId();
                            }

                            startActivityForResult(PesquisaPerguntasActivity.newIntent(getContext(), rotas.getCliente().getCodigo(),
                                    rotaTarefas.getPesquisa()), NEW_PESQUISA_REQUEST_CODE);
                        }
                    }
                }
            }
        });

    }

    private void showJustDialog(){
        JustTask justTask = listJustTasks.get(indexJustTarefa);

        String titleDialog = "";
        TMultiValuesType tMultiValuesType = null;

        if(justTask.getTipo() == TJustTask.PESQUISA){
            titleDialog =  getString(R.string.valid_pesquisa_checkout_success, justTask.getPesquisa().getNome());
        } else {
            titleDialog =  getString(R.string.valid_checkout_success);
            tMultiValuesType = TMultiValuesType.RG_TASK_JUST;
        }

        DialogsMultiValue dialogsMultiValue = new DialogsMultiValue(getActivity());

        if(justTask.getTipo() == TJustTask.PEDIDO){
            dialogsMultiValue.showDialogSpinner(tMultiValuesType, titleDialog, dialogsMultiValue.DIALOG_TYPE_WARNING, onClickDialogMessage());
        } else {
            dialogsMultiValue.showDialogSpinner(justTask.getPesquisa().getPesquisaJustificativa(), titleDialog, dialogsMultiValue.DIALOG_TYPE_WARNING, onClickDialogMessage());
        }
    }

    private DialogsMultiValue.OnClickDialogMessage onClickDialogMessage(){
       return (multiValues)-> {
           try {
               justificativas.add(multiValues);
               indexJustTarefa++;

               if (indexJustTarefa < listJustTasks.size()) {
                   showJustDialog();
               } else {
                   for (int ind = 0; ind < listJustTasks.size(); ind++) {
                       JustTask justTask = listJustTasks.get(ind);
                       Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

                       switch (listJustTasks.get(ind).getTipo()) {
                           case PEDIDO:
                               RotaGuiadaDao mRotaGuiadaDao = new RotaGuiadaDao(getContext());
                               mRotaGuiadaDao.setJustificaPedido(rotas, justificativas.get(ind));
                               rotas.setJustifPedido(justificativas.get(ind).getValCod());
                               break;
                           case PESQUISA:
                               RotaGuiadaTarefaDao rotaGuiadaTarefaDao = new RotaGuiadaTarefaDao(getContext());
                               rotaGuiadaTarefaDao.setJusticativaAtividade(justTask.getPesquisa().getCodigo(), justificativas.get(ind).getValCod(), rotas);
                               mRotaResumo.get(justTask.getIndexTarefa()).setAtividJust(justificativas.get(ind).getValCod());
                               break;
                       }

                       if (mRotaResumo != null && justTask.getIndexTarefa() < mRotaResumo.size()) {
                           mRotaGuiadaResumoAdapter.updateItem(mRotaResumo.get(justTask.getIndexTarefa()), justTask.getIndexTarefa());
                       }

                   }

                   if (!TextUtils.isNullOrEmpty(atividadSemJustificativa)) {
                       //dialogsDefault.showDialogMessage(getString(R.string.checklist_obrig_sem_justificativa), dialogsDefault.DIALOG_TYPE_WARNING, null);
                       //return;
                   }

                   checkout(mRotaViewModelProviders.getRotas().getValue());
               }
           }catch (Exception ex){
               LogUser.log(ex.getMessage());
           }
       };
    }

    private void visibleFields(){

        mListEmptyLinearLayout.setVisibility(View.GONE);

        if(!isEdit){
            mCheckoutButton.setVisibility(View.GONE);
            mPauseButton.setVisibility(View.GONE);
            mAddItemFloatingActionButton.hide();
            mPedidoLinearLayout.setVisibility(View.GONE);
            mLinePedidoLinearLayout.setVisibility(View.GONE);

            if (mRotaResumo == null || mRotaResumo.size() == 0) {
                mListEmptyLinearLayout.setVisibility(View.VISIBLE);
            }

        } else {

            if(mRotaResumo == null){
                return;
            }

            boolean isContainPedido = false;

            for(RotaTarefas rota:mRotaResumo){
                if(rota.getStatus() == RotaGuiadaTaskType.PEDIDO){
                    isContainPedido = true;
                    break;
                }
            }

            Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

            if(rotas.getStatus() == RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO)
                mPauseButton.setVisibility(View.VISIBLE);

            //Plano de campo == null - Clientes do planejamento de rota
            if(rotas.isRota() && rotas.getCliente().getPlanoCampo() != null){

                String planoCampo = PlanoCampoUtils.getPlanoCampoToday(rotas.getCliente().getPlanoCampo());

                if (!cliente.isInativo() && !isContainPedido &&
                        (!planoCampo.equals(PlanoCampoUtils.VISITA)&& !planoCampo.equals(PlanoCampoUtils.VISITAEDI))) {
                    showPedidoObrig(View.VISIBLE);

                    if(planoCampo.equals(PlanoCampoUtils.TIPO_E) || mRotaViewModelProviders.getIsVisitaPromotor().getValue()){
                        mResumeTitleTextView.setText(getString(R.string.rg_new_pedido).replace("*",""));
                    }

                } else {
                    showPedidoObrig(View.GONE);
                }
            } else {
                showPedidoObrig(View.GONE);
            }

            if(rotas.getStatus() != RotaGuiadaUtils.STATUS_RG_EM_ANDAMENTO){
                mCheckoutButton.setVisibility(View.GONE);
                mAddItemFloatingActionButton.hide();    

                if (mRotaResumo == null || mRotaResumo.size() == 0) {
                    mListEmptyLinearLayout.setVisibility(View.VISIBLE);
                }
            } else {
                mAddItemFloatingActionButton.show();
                mCheckoutButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showPedidoObrig(int visible){
        if(mRotaViewModelProviders.getIsVisitaPromotor().getValue()){
            mPedidoLinearLayout.setVisibility(View.GONE);
            mLinePedidoLinearLayout.setVisibility(View.GONE);
        } else {
            mPedidoLinearLayout.setVisibility(visible);
            mLinePedidoLinearLayout.setVisibility(visible);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_PEDIDO_REQUEST_CODE:
                break;
            case NEW_PESQUISA_REQUEST_CODE:
                if(resultCode == getActivity().RESULT_OK){
                    if (data != null && data.hasExtra(PesquisaFragment.KEY_CODIGO_PESQUISA)) {
                        String idPesquisa = data.getStringExtra(PesquisaFragment.KEY_CODIGO_PESQUISA);
                        createPesquisa(idPesquisa);
                    }
                }

                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

        findItens(data);

    }

    @Override
    public void processFinish(List<RotaTarefas> resume) {
        if(getActivity() != null && !getActivity().isFinishing()) {

            mRotaResumo = resume;

            mRotaGuiadaResumoAdapter = new RotaGuiadaResumoAdapter(mRotaResumo, getContext());
            mRotasRecyclerView.setAdapter(mRotaGuiadaResumoAdapter);

            int viewHeight = (mRotaGuiadaResumoAdapter.getSizeItem() * mRotaResumo.size()) + (mRotaResumo.size() * 5);
            mRotasRecyclerView.getLayoutParams().height = (viewHeight);

            Rotas rotas = mRotaViewModelProviders.getRotas().getValue();
            cliente = new ClienteDao(getContext()).get(rotas.getCodUnidNeg(), rotas.getCliente().getCodigo());

            visibleFields();
            mTarefaIDSel = null;
        }

    }

    @Override
    public void onClick(View v) {
        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();
        if (!RotaGuiadaUtils.checkValidRota(getActivity(), mRotaViewModelProviders.getRotas().getValue(), isEdit))
            return;

        //Bloquea todas as ações quando a rota estiver pausada.
        if(rotas.getStatus() == RotaGuiadaUtils.STATUS_RG_PAUSADO)
            return;

        switch (v.getId()){
            case R.id.rg_pedido_relative_layout:
                if (checkClick()) {
                    if (TextUtils.isNullOrEmpty(rotas.getCheckin())) {
                        errorCheckin();
                    } else {
                        if(isCheckTipoVenda()){
                            startActivityForResult(PedidoDetailActivity.newIntent(getContext(), createPedido(),
                                    PedidoViewType.PEDIDO, false, true), NEW_PEDIDO_REQUEST_CODE);
                        }
                    }
                }
                break;
            case R.id.add_rg_item_floating_action_button:
                 if (TextUtils.isNullOrEmpty(rotas.getCheckin())) {
                     errorCheckin();
                 } else {
                     createPopupMenu(v);
                 }
                break;
            case R.id.checkout_button:
                validCheckout(rotas);
                break;
            case R.id.pause_button:
                pauseRota(rotas);
                break;

        }
    }

    private void errorCheckin(){
        dialogsDefault.showDialogMessage(getString(R.string.checkin_not_done), dialogsDefault.DIALOG_TYPE_WARNING, new DialogsCustom.OnClickDialogMessage() {
            @Override
            public void onClick() {
                viewPager.setCurrentItem(RotaGuiadaTabType.CLIENTE_FRAGMENT.getValue());
            }
        });
    }

    private void validCheckout(Rotas rotas){
        if (!TextUtils.isNullOrEmpty(rotas.getCheckin())) {
            if(!RotaGuiadaUtils.checkoutValidDate(getActivity(), rotas))
                return;

            if(!rotas.isRota()){
                checkout(rotas);
                return;
            }

            String planoCampo = null;

            //Plano de campo == null - Clientes do planejamento de rota
            if(rotas.getCliente().getPlanoCampo() != null){
                planoCampo = PlanoCampoUtils.getPlanoCampoToday(rotas.getCliente().getPlanoCampo());
            }

            int count = 0;
            indexJustTarefa = 0;

            listJustTasks = new ArrayList<>();
            justificativas = new ArrayList<>();
            ArrayList<JustTask> listJustTasksPesquisa  = new ArrayList<>();

            PesquisaDao pesquisaDao = new PesquisaDao(getContext());

            atividadSemJustificativa = "";

            if(mRotaResumo != null && mRotaResumo.size() > 0){
                int index = 0;
                for(RotaTarefas item: mRotaResumo){

                    if(item.getStatus() == RotaGuiadaTaskType.PEDIDO){
                        if(cliente.isInativo()){
                            count++;
                        } else {
                            if(perfil.isRotaJutificativaPedidoNaoRealizado()){
                                if(RotaGuiadaUtils.isTaksFinish(item.getStatus().getValue(), item.getPedido().getStatus().getCodigo())){
                                    count++;
                                }
                            }
                        }
                    } else if(item.getStatus() == RotaGuiadaTaskType.PESQUISA) {
                        if(perfil.isRotaJutificativaAtividadeNaoRealizada() && item.getPesquisa().isAtividadeObrigatoria()) {

                            item.getPesquisa().setPesquisaJustificativa(
                                    pesquisaDao.getPesquisaJustificativa(String.valueOf(item.getPesquisa().getCodigo())));

                            if(item.getPesquisa().getPesquisaJustificativa().size() > 0 && item.getAtividJust() == 0)  {
                                if (!RotaGuiadaUtils.isTaksFinish(item.getStatus().getValue(), item.getPesquisa().getStatusResposta())) {
                                    JustTask justTask = new JustTask();
                                    justTask.setPesquisa(item.getPesquisa());
                                    justTask.setTipo(TJustTask.PESQUISA);
                                    justTask.setIndexTarefa(index);
                                    listJustTasksPesquisa.add(justTask);
                                }
                            } else {
                                if (!RotaGuiadaUtils.isTaksFinish(item.getStatus().getValue(), item.getPesquisa().getStatusResposta())) {
                                    if (TextUtils.isNullOrEmpty(atividadSemJustificativa)) {
                                        atividadSemJustificativa = item.getPesquisa().getNome();
                                    } else {
                                        atividadSemJustificativa += ", " + item.getPesquisa().getNome();
                                    }
                                }
                            }
                        }
                    }
                    index++;
                }
            }

            //Plano de campo == null - Clientes do planejamento de rota
            if(planoCampo == null || cliente.isInativo()  || (!perfil.isRotaJutificativaPedidoNaoRealizado()  ||
                    planoCampo.equals(PlanoCampoUtils.VISITAEDI) ||
                    planoCampo.equals(PlanoCampoUtils.TIPO_E) ||
                    planoCampo.equals(PlanoCampoUtils.VISITA))){
                count++;
            }

            if((count == 0 || listJustTasksPesquisa.size() > 0)) {

                if (count == 0 && rotas.getJustifPedido()  < 1 && !mRotaViewModelProviders.getIsVisitaPromotor().getValue()) {
                    JustTask justTask = new JustTask();
                    justTask.setTipo(TJustTask.PEDIDO);
                    listJustTasks.add(justTask);
                }

                if (listJustTasksPesquisa.size() > 0) {
                    listJustTasks.addAll(listJustTasksPesquisa);
                } else {
                    //Caso não tenha justificativa e tenha pesquisas obrigatorias em aberto não deixa continuar.
                    if(!TextUtils.isNullOrEmpty(atividadSemJustificativa)){
                        dialogsDefault.showDialogMessage(getString(R.string.checklist_obrig_sem_justificativa), dialogsDefault.DIALOG_TYPE_WARNING, null);
                        return;
                    }
                }

                if(listJustTasks.size() > 0){
                    showJustDialog();
                } else {
                    checkout(rotas);
                }
            } else {
                if(!TextUtils.isNullOrEmpty(atividadSemJustificativa)){
                    dialogsDefault.showDialogMessage(getString(R.string.checklist_obrig_sem_justificativa), dialogsDefault.DIALOG_TYPE_WARNING, null);
                    return;
                }
                checkout(rotas);
            }

        } else {
            errorCheckin();
        }
    }

    private void pauseRota(Rotas rotas){
        RotaOrigem rotaOrigem = mRotaViewModelProviders.getRotaOrigem().getValue();

        RotaGuiadaUtils.actionRota(getActivity(), rotaOrigem, rotas, TActionRotaGuiada.PAUSE, (rota)-> {
            RotaGuiadaDetailActivity.isUpdate = true;
            mRotaViewModelProviders.getRotas().setValue(rota);

            JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
            jjSyncRotaGuiada.syncRotaGuiada(getActivity(), ()-> {
                getActivity().finish();
            });
        });
    }

    private void checkout(Rotas rota){
        RotaOrigem rotaOrigem = mRotaViewModelProviders.getRotaOrigem().getValue();

        RotaGuiadaUtils.actionRota(getActivity(), rotaOrigem, rota, TActionRotaGuiada.CHECKOUT, (rotas)-> {
            RotaGuiadaDetailActivity.isUpdate = true;
            mRotaViewModelProviders.getRotas().setValue(rotas);

            JJSyncRotaGuiada jjSyncRotaGuiada = new JJSyncRotaGuiada();
            jjSyncRotaGuiada.syncRotaGuiada(getActivity(), ()-> {
                getActivity().finish();
            });
        });

        mCheckoutButton.setVisibility(View.GONE);
        mAddItemFloatingActionButton.hide();
    }

    public void findItens() {
        findItens(null);
    }

    private void findItens(Intent data) {
        mRotaGuiadaResumoAdapter = new RotaGuiadaResumoAdapter(new ArrayList<>(), getContext());
        mRotasRecyclerView.setAdapter(mRotaGuiadaResumoAdapter);

        if (data != null && data.hasExtra(ClienteFilterActivity.FILTER_RESULT_DATA_KEY)) {
            mRotaGuiadaResumoAdapter = (RotaGuiadaResumoAdapter) data.getSerializableExtra(
                    ClienteFilterActivity.FILTER_RESULT_DATA_KEY);

            mListEmptyLinearLayout.setVisibility(View.GONE);
            mRotasRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerScrollView.setVisibility(View.VISIBLE);
        }

        reloadItens();
    }

    private void reloadItens() {
        mRotaGuiadaResumoAdapter.resetData();
        loadRotaPaginacao(true);
    }

    private void loadRotaPaginacao(boolean isStartLoading) {
        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

        this.mIsStartLoading = isStartLoading;
        if (mAsyncTaskRotaGuiadaResume != null) {
            mAsyncTaskRotaGuiadaResume.cancel(true);
        }

        if(rotas == null){
            getActivity().finish();
        } else {
            mAsyncTaskRotaGuiadaResume = new AsyncTaskResumoRotaGuiada(getActivity(), rotas, mRotaGuiadaTarefaDao, new PedidoDao(getContext()), new PesquisaDao(getContext()), this);
            mAsyncTaskRotaGuiadaResume.setTarefaIDSel(mTarefaIDSel);

            mAsyncTaskRotaGuiadaResume.execute();
        }
    }

    private void createPopupMenu(View view) {
       PopupMenu popup = new PopupMenu(getContext(), view, Gravity.END);
       popup.getMenuInflater().inflate(R.menu.rg_resume_new_item, popup.getMenu());

       MenuItem menuItemPedido =  popup.getMenu().findItem(R.id.action_new_pedido);
       menuItemPedido.setVisible(mRotaViewModelProviders.getIsVisitaPromotor().getValue() ? false:true);

        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();

        popup.setOnMenuItemClickListener(menuItem -> {

            if(!RotaGuiadaUtils.checkValidRota(getActivity(), mRotaViewModelProviders.getRotas().getValue(), isEdit))
                return false;

            switch (menuItem.getItemId()) {
                case R.id.action_new_pedido:


                    if(cliente.isInativo()){
                        DialogsCustom dialogsDefault = new DialogsCustom(getContext());
                        dialogsDefault.showDialogMessage(getString(R.string.pedido_cliente_inativo), dialogsDefault.DIALOG_TYPE_WARNING, null);
                    }
                    else if(isCheckTipoVenda()) {
                        startActivityForResult(PedidoDetailActivity.newIntent(getContext(), createPedido(),
                                PedidoViewType.PEDIDO, false, true), NEW_PEDIDO_REQUEST_CODE);
                    }
                    break;
                case R.id.action_new_pesquisa:
                    startActivityForResult(PesquisaActivity.newIntent(getContext(), rotas.getCliente().getCodigo()), NEW_PESQUISA_REQUEST_CODE);
                    break;
            }
            return false;
        });
        popup.show();
    }

    private String createPedido(){
        PedidoDao pedidoDao = new PedidoDao(getContext());
        PedidoBusiness pedidoBusiness = new PedidoBusiness();
        Current current = Current.getInstance(getContext());

        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();
        Pedido pedido = pedidoBusiness.createNewPedido(pedidoDao, current.getUnidadeNegocio(),
                current.getUsuario(), rotas.getCliente());

        mRotaGuiadaTarefaDao.createNewTask(true, rotas, pedido.getCodigo(),  RotaGuiadaTaskType.PEDIDO);

        return pedido.getCodigo();
    }

    private void createPesquisa(String idPesquisa){
        Rotas rotas = mRotaViewModelProviders.getRotas().getValue();
        if(!mRotaGuiadaTarefaDao.hasTask(idPesquisa, rotas)){
            mRotaGuiadaTarefaDao.createNewTask(true, rotas, idPesquisa,  RotaGuiadaTaskType.PESQUISA);
        }
    }

    private boolean isCheckAccessPesquisa(int codigoPesquisa) {
        Usuario usuario = Current.getInstance(getContext()).getUsuario();
        String unNeg = Current.getInstance(getContext()).getUnidadeNegocio().getCodigo();
        boolean hasAccess = acessoDao.hasAccess(usuario, codigoPesquisa , unNeg);

        if(hasAccess){
            return true;
        } else {
            Toast.makeText(getContext(), getString(R.string.rg_message_error_task), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean isCheckTipoVenda(){
        List<PerfilVenda> perfilVenda = perfil.getPerfisVenda();

        if(perfilVenda != null && perfilVenda.size() > 0){
            return true;
        } else {
            Toast.makeText(getContext(), getString(R.string.rg_message_error_task), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public void setEditPause(boolean edit) {
        isEdit = edit;
        visibleFields();
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
    }
}
