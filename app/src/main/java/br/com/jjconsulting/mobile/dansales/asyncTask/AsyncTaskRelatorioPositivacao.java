package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;


import br.com.jjconsulting.mobile.dansales.database.RelatorioPositivacaoDao;
import br.com.jjconsulting.mobile.dansales.model.RelatorioPositivacao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.Config;
import br.com.jjconsulting.mobile.jjlib.util.LogUser;

public class AsyncTaskRelatorioPositivacao extends AsyncTask<Void, Void, List<RelatorioPositivacao>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private RelatorioPositivacaoDao mRelatorioPositivacaoDao;

    public AsyncTaskRelatorioPositivacao(Context context, RelatorioPositivacaoDao relatorioPositivacaoDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mRelatorioPositivacaoDao = new RelatorioPositivacaoDao(context);
    }

    @Override
    protected List<RelatorioPositivacao> doInBackground(Void... params) {
        List<RelatorioPositivacao> mRelatorioPositivacao = new ArrayList<>();

        Current current = Current.getInstance(context);
        String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
        String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

        RelatorioPositivacao resumoRelatorioPositivacao = mRelatorioPositivacaoDao.getRelPositivacao(codigoUsuario, codigoUnidadeNegocio);
        mRelatorioPositivacao.add(resumoRelatorioPositivacao);

        ArrayList<Usuario> codUserSub = mRelatorioPositivacaoDao.getSubordinados(codigoUsuario, codigoUnidadeNegocio);

        if(codUserSub != null){
            for (Usuario usuario:codUserSub){
                RelatorioPositivacao positivacao = mRelatorioPositivacaoDao.getRelPositivacao(usuario.getCodigo(), codigoUnidadeNegocio);
                positivacao.setNome(usuario.getNomeReduzido());
                positivacao.setCodigo(usuario.getCodigo());
                mRelatorioPositivacao.add(positivacao);
            }
        }

        LogUser.log(Config.TAG, "relatorio 1: " + mRelatorioPositivacao.size());

        return mRelatorioPositivacao;
    }

    @Override
    protected void onPostExecute(List<RelatorioPositivacao> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<RelatorioPositivacao> objects);
    }
}
