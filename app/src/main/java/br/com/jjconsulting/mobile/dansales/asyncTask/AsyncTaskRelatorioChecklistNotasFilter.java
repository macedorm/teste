package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.BandeiraDao;
import br.com.jjconsulting.mobile.dansales.database.OrganizacaoDao;
import br.com.jjconsulting.mobile.dansales.database.RelatorioObjetivoDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Familia;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;

public class AsyncTaskRelatorioChecklistNotasFilter extends AsyncTask<String, Void, Object[]> {

    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private boolean isStartLoading;
    private UsuarioDao usuarioDao;

    public AsyncTaskRelatorioChecklistNotasFilter(Context context, boolean isStartLoading,
                                                  OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.isStartLoading = isStartLoading;
        this.onAsyncResponse = onAsyncResponse;
    }

    @Override
    protected Object[] doInBackground(String... params) {
        Object[] objects = null;

        Usuario usuario = Current.getInstance(context).getUsuario();
        UnidadeNegocio unidadeNegocio = Current.getInstance(context).getUnidadeNegocio();

        usuarioDao = new UsuarioDao(context);
        Tree<Usuario> hierarquiaComercial = usuarioDao.getHierarquiaComercial(usuario,
                unidadeNegocio.getCodigo());
        objects = new Object[1];
        objects[0] = hierarquiaComercial;

        return objects;
    }

    @Override
    protected void onPostExecute(Object[] objects) {
        onAsyncResponse.processFinish(1, isStartLoading, objects);
    }

    public interface OnAsyncResponse {
        void processFinish(int type, boolean isStartLoading, Object[] objects);
    }

    public void cancel() {
        this.cancel(true);
        if (usuarioDao != null) {
            usuarioDao.setCancel(true);
        }
    }
}
