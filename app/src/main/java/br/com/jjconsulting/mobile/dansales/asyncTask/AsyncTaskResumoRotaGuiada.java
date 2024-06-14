package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.database.PedidoDao;
import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaTarefaDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Pedido;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.model.RotaGuiadaTaskType;
import br.com.jjconsulting.mobile.dansales.model.RotaTarefas;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.PesquisaUtils;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.dansales.util.UsuarioUtils;
import br.com.jjconsulting.mobile.jjlib.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class AsyncTaskResumoRotaGuiada extends AsyncTask<Void, Void, List<RotaTarefas>> {

    private OnAsyncResponse onAsyncResponse;

    private RotaGuiadaTarefaDao mRotaGuiadaTarefaDao;

    private PedidoDao mPedidoDao;

    private PesquisaDao mPesquisaDao;

    private Rotas mRota;

    private String mTarefaIDSel;

    private Context context;

    public AsyncTaskResumoRotaGuiada(Context context, Rotas rota, RotaGuiadaTarefaDao rotaGuiadaTarefaDao, PedidoDao pedidoDao, PesquisaDao pesquisaDao, OnAsyncResponse onAsyncResponse) {
        this.onAsyncResponse = onAsyncResponse;
        this.mRotaGuiadaTarefaDao = rotaGuiadaTarefaDao;
        this.mPedidoDao = pedidoDao;
        this.mPesquisaDao = pesquisaDao;
        this.mRota = rota;
        this.context = context;
    }

    @Override
    protected List<RotaTarefas> doInBackground(Void... params) {
        List<RotaTarefas> rotasResumo;

        if(context == null){
            return new ArrayList<>();
        }

        if(mRota.getCliente() == null && !TextUtils.isNullOrEmpty(mRota.getCodCliente())){
            ClienteDao clienteDao = new ClienteDao(context);
            mRota.setCliente(clienteDao.get(Current.getInstance(context).getUnidadeNegocio().getCodigo(), mRota.getCodCliente()));

            if(mRota.getCodCliente() == null){
                return new ArrayList<>();
            }
        }

        rotasResumo = mRotaGuiadaTarefaDao.getAllTask(mRota, mRota.getCliente().getCodigo());
        Date dateRota = null;

        try{
            dateRota = FormatUtils.toDate(mRota.getDate());
        }catch (Exception ex){
            LogUser.log(Config.TAG, ex.toString());
        }

        if(FormatUtils.toDateTimeFixed().equals(dateRota) || FormatUtils.toDateTimeFixed().before(dateRota)) {

            boolean isInsert = FormatUtils.toDateTimeFixed().equals(dateRota);
            //&& UsuarioUtils.isPromotor(Current.getInstance(context).getUsuario().getCodigoFuncao());

            ArrayList<Pedido> pedidoSUG = mPedidoDao.getPedidoSugerido(mRota.getCodRegFunc(), mRota.getCodUnidNeg(), mRota.getCliente().getCodigo());

            if(pedidoSUG != null){
                //adicionando
                for(Pedido pedido: pedidoSUG) {
                    boolean isAtividade = false;

                    for (RotaTarefas rotaTarefa : rotasResumo) {
                        if (rotaTarefa.getStatus() == RotaGuiadaTaskType.PEDIDO && rotaTarefa.getIdItem().equals(pedido.getCodigo())) {
                            isAtividade = true;
                            break;
                        }
                    }

                    if (!isAtividade) {
                        RotaTarefas t = mRotaGuiadaTarefaDao.createNewTask(isInsert, mRota, pedido.getCodigo(), RotaGuiadaTaskType.PEDIDO);
                        t.setPedido(pedido );
                        rotasResumo.add(t);
                    }
                }
            }

            UsuarioDao usuarioDao = new UsuarioDao(context);
            Usuario usuario = usuarioDao.get(mRota.getCodRegFunc());

            ArrayList<Pesquisa> pesquisas = mPesquisaDao.getAll(mRota.getCodUnidNeg(),
                    usuario, mRota.getCliente().getCodigo(),"", dateRota, PesquisaDao.TTypePesquisa.ISROTA);

            //adicionando
            for(Pesquisa pesquisa: pesquisas){
                boolean isAtividade = false;

                for (RotaTarefas rotaTarefa : rotasResumo) {
                    if (rotaTarefa.getIdItem().equals(String.valueOf(pesquisa.getCodigo()))) {
                        isAtividade = true;
                        break;
                    }
                }

                if (!isAtividade) {
                    RotaTarefas atividade = mRotaGuiadaTarefaDao.createNewTask(isInsert, mRota, String.valueOf(pesquisa.getCodigo()), RotaGuiadaTaskType.PESQUISA);
                    atividade.setPesquisa(pesquisa);
                    rotasResumo.add(atividade);
                }
            }

            Date proxVisita = null;
            Date date = null;

            if(mRota.isRota()) {
                try {
                    date = FormatUtils.toDate(mRota.getDate());
                } catch (Exception ex) {
                    LogUser.log(ex.toString());
                }

                //Plano de campo == null - Clientes do planejamento de rota
                if(mRota.getCliente().getPlanoCampo() != null){
                    proxVisita = PlanoCampoUtils.getProxVisita(context, mRota.getCliente().getPlanoCampo(), date);
                }
            }

            //removendo e verificando obrigatoriedade
            List<RotaTarefas> listRotaRemove = new ArrayList<>();
            for(int ind = 0; ind < rotasResumo.size(); ind++){
                RotaTarefas rotaTarefa = rotasResumo.get(ind);

                if (rotaTarefa.getStatus() == RotaGuiadaTaskType.PESQUISA) {
                    boolean isDel = true;

                    //Remove tarefa caso a pesquisa não esteja mais disponivel
                    for (Pesquisa pesquisa : pesquisas) {
                        if (String.valueOf(pesquisa.getCodigo()).equals(rotaTarefa.getIdItem())) {
                            isDel = false;
                            break;
                        }
                    }

                    if (isDel) {
                        //Teste pesquisas que adicionadas pelo usuário, antes de remover
                         ArrayList<Pesquisa> pesquisa = mPesquisaDao.hasPesquisaAtiva(mRota.getCodUnidNeg(),
                            usuario, mRota.getCliente().getCodigo(),null, rotaTarefa.getIdItem(), dateRota);

                         if(pesquisa.size() > 0){
                             int tipo = pesquisa.get(0).getTipo();

                             if(tipo == Pesquisa.CHECKLIST || tipo == Pesquisa.CHECKLIST_RG){
                                 listRotaRemove.add(rotaTarefa);
                             }

                         } else {
                             listRotaRemove.add(rotaTarefa);
                         }
                    } else {
                        if(mRota.isRota()){

                            Pesquisa pesquisa = rotaTarefa.getPesquisa();

                            if(pesquisa != null) {

                                if(proxVisita != null) {
                                    pesquisa.setAtividadeObrigatoria(PesquisaUtils.
                                            isAtividadeObrigatoriaRota(pesquisa, date, proxVisita));
                                }

                                //Esconde da lista tarefas que não sejam permitidas a visualização após respostas
                                if (!pesquisa.isVisualizaAtividade() && UsuarioUtils.isPromotor(Current.getInstance(context).getUsuario().getCodigoFuncao())) {

                                    if (pesquisa.getStatusResposta() == Pesquisa.OBRIGATORIAS_RESPONDIDAS) {
                                            rotaTarefa.setInvisible(true);
                                            listRotaRemove.add(rotaTarefa);
                                    }
                                }
                            } else {
                                listRotaRemove.add(rotaTarefa);
                            }
                        }
                    }
                }
            }

            for (RotaTarefas t : listRotaRemove) {
                if(!t.isInvisible()){
                    mRotaGuiadaTarefaDao.deleteTask(t);
                }
                rotasResumo.remove(t);
            }
        }
        return rotasResumo;
    }

    @Override
    protected void onPostExecute(List<RotaTarefas> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public void setTarefaIDSel(String tarefaIDSel){
        mTarefaIDSel = tarefaIDSel;
    }

    public interface OnAsyncResponse {
        void processFinish(List<RotaTarefas> objects);
    }
}
