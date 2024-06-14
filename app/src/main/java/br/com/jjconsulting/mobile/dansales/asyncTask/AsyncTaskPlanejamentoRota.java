package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.RotasFilter;
import br.com.jjconsulting.mobile.dansales.database.RotaGuiadaDao;
import br.com.jjconsulting.mobile.dansales.model.Perfil;
import br.com.jjconsulting.mobile.dansales.model.Rotas;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.util.FormatUtils;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class AsyncTaskPlanejamentoRota extends AsyncTask<Void, Void, List<Rotas>[]> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private RotasFilter mRotaGuiadaFilter;
    private String mNome;
    private RotaGuiadaDao mRotaGuiadaDao;
    private Date mDateRotaGuiada;
    private Date mDateOfDay;

    public AsyncTaskPlanejamentoRota(Context context, RotasFilter rotaGuiadaFilter, String mNome, Date date, RotaGuiadaDao rotaGuiadaDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mRotaGuiadaFilter = rotaGuiadaFilter;
        this.mNome = mNome;
        mDateRotaGuiada = date;
        this.mRotaGuiadaDao = rotaGuiadaDao;
        mDateOfDay = FormatUtils.toDateTimeFixed();
    }

    @Override
    protected List<Rotas>[] doInBackground(Void... params) {
        List<Rotas>[] arrayRotas = new List[2];
        List<Rotas> rotasDia = new ArrayList<>();

        Current current = Current.getInstance(context);

        if(mDateRotaGuiada.after(mDateOfDay)) {
            //Retorna todas as rotas futuras GA/Promotor
            rotasDia = mRotaGuiadaDao.simulateRoute(mDateRotaGuiada, mNome, mRotaGuiadaFilter);
        } else {

            rotasDia = mRotaGuiadaDao.getListRouteCreated(true, current.getUsuario().getCodigo(), current.getUnidadeNegocio().getCodigo(), mDateRotaGuiada, mNome, mRotaGuiadaFilter);

            boolean isContainRotas = (rotasDia != null && rotasDia.size() > 0) ;

            //Data da rota igual dia de hoje
            if(mDateOfDay.equals(mDateRotaGuiada)){

                //Cria a rota dia do usuário logado
                if(!isContainRotas && TextUtils.isNullOrEmpty(mNome) && mRotaGuiadaFilter.isEmpty()){
                    rotasDia = mRotaGuiadaDao.createListRoute(mDateRotaGuiada);
                }

                //Retorna tarefas não realizadas
                Perfil perfil = Current.getInstance(context).getUsuario().getPerfil();
                if (perfil.isRotaJutificativaVisitaNaoRealizada()) {
                    arrayRotas[0] = mRotaGuiadaDao.getUnrealizedRoute(current.getUsuario().getCodigo(), current.getUnidadeNegocio().getCodigo(), mDateRotaGuiada);
                } else {
                    arrayRotas[0] = new ArrayList<>();
                }
            }
        }

        arrayRotas[1] = rotasDia;

        /*
        RotaGuiadaDao rotaGuiadaDao = new RotaGuiadaDao(context);
        ClienteDao clienteDao = new ClienteDao(context);
        Cliente cliente = clienteDao.get(Current.getInstance(context).getUnidadeNegocio().getCodigo(), "0450067950");

        Rotas rota = rotaGuiadaDao.createNewRota(cliente, new Date(), true,  Current.getInstance(context).getUsuario().getCodigo(), Current.getInstance(context).getUnidadeNegocio().getCodigo());
        rotaGuiadaDao.insertRota(rota);*/



        return arrayRotas;
    }

    @Override
    protected void onPostExecute(List<Rotas>[] objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<Rotas>[] objects);
    }
}
