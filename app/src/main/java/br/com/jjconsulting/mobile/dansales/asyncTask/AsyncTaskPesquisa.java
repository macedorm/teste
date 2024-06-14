package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.database.PesquisaDao;
import br.com.jjconsulting.mobile.dansales.model.Pesquisa;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskPesquisa extends AsyncTask<Void, Void, List<Pesquisa>> {

    private Context context;
    private PostExecuteListener<List<Pesquisa>> postExecuteListener;
    private String nome;
    private PesquisaDao pesquisaDao;
    private PesquisaDao.TTypePesquisa tTypePesquisa;
    private Date currentDate;

    public AsyncTaskPesquisa(Context context, String nome, PesquisaDao pesquisaDao, Date currentDate, PesquisaDao.TTypePesquisa tTypePesquisa,
                             PostExecuteListener<List<Pesquisa>> postExecuteListener) {
        this.context = context;
        this.postExecuteListener = postExecuteListener;
        this.nome = nome;
        this.pesquisaDao = pesquisaDao;
        this.tTypePesquisa = tTypePesquisa;
        this.currentDate = currentDate;
    }

    @Override
    protected List<Pesquisa> doInBackground(Void... params) {
        Current current = Current.getInstance(context);
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        ArrayList<Pesquisa> pesquisas = new ArrayList<>();

        if(tTypePesquisa != null){
            pesquisas = pesquisaDao.getAll(codigoUnidadeNegocio, current.getUsuario(), null, nome, currentDate,   tTypePesquisa);
        }

        return pesquisas;
    }

    @Override
    protected void onPostExecute(List<Pesquisa> objects) {
        postExecuteListener.onPostExecute(objects);
    }
}
