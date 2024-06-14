package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import br.com.jjconsulting.mobile.dansales.data.GetSortimentoAsyncTaskParameters;
import br.com.jjconsulting.mobile.dansales.data.ResumoSortimento;
import br.com.jjconsulting.mobile.dansales.database.SortimentoDao;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class GetSortimentoAsyncTask
        extends AsyncTask<GetSortimentoAsyncTaskParameters, Void, ResumoSortimento> {

    private OnPostExecuteListener onPostExecuteListener;
    private Context context;

    public GetSortimentoAsyncTask(Context context, OnPostExecuteListener onPostExecuteListener) {
        this.onPostExecuteListener = onPostExecuteListener;
        this.context = context;
    }

    @Override
    protected ResumoSortimento doInBackground(
            GetSortimentoAsyncTaskParameters... parameters) {
        if (parameters == null || parameters.length != 1)
            return null;

        GetSortimentoAsyncTaskParameters p = parameters[0];
        SortimentoDao dao = p.getSortimentoDao();

        Usuario usuario = Current.getInstance(context).getUsuario();

        return dao.getResumoSortimento(usuario, p.getPedido(), p.getPlanogramCode(), p.getDate());
    }

    @Override
    protected void onPostExecute(ResumoSortimento resumoSortimento) {
        onPostExecuteListener.onPostExecute(resumoSortimento);
    }

    public interface OnPostExecuteListener {

        void onPostExecute(ResumoSortimento resumoSortimento);
    }
}
