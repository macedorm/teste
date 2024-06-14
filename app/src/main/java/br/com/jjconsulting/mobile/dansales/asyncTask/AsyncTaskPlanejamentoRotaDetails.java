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

public class AsyncTaskPlanejamentoRotaDetails extends AsyncTask<Void, Void, List<Rotas>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private RotaGuiadaDao mRotaGuiadaDao;
    private Date mDateRotaGuiada;

    public AsyncTaskPlanejamentoRotaDetails(Context context, Date date, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mDateRotaGuiada = date;
        this.mRotaGuiadaDao = new RotaGuiadaDao(context);
    }

    @Override
    protected List<Rotas> doInBackground(Void... params) {
        List<Rotas> rotasDia;

        Current current = Current.getInstance(context);

        rotasDia = mRotaGuiadaDao.getListRouteCreated(true, current.getUsuario().getCodigo(),
                current.getUnidadeNegocio().getCodigo(), mDateRotaGuiada, null, null);

         if(rotasDia.size() == 0) {
             rotasDia = mRotaGuiadaDao.simulateRoute(mDateRotaGuiada, null, null);
         }

        return rotasDia;
    }

    @Override
    protected void onPostExecute(List<Rotas> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<Rotas> objects);
    }
}
