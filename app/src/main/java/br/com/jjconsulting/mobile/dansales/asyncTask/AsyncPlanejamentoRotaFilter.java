package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.MultiValuesDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;

public class AsyncPlanejamentoRotaFilter extends AsyncTask<String, Void, Object[]> {

    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private UsuarioDao usuarioDao;

    public AsyncPlanejamentoRotaFilter(Context context, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
    }

    @Override
    protected Object[] doInBackground(String... params) {
        Object[] objects = new Object[1];

        usuarioDao = new UsuarioDao(context);
        Usuario usuario = Current.getInstance(context).getUsuario();
        UnidadeNegocio unidadeNegocio = Current.getInstance(context).getUnidadeNegocio();

        usuarioDao = new UsuarioDao(context);
        Tree<Usuario> hierarquiaComercial = usuarioDao.getHierarquiaComercial(usuario,
                unidadeNegocio.getCodigo());

        objects[0] = hierarquiaComercial;

        return objects;

    }

    @Override
    protected void onPostExecute(Object[] objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(Object[] objects);
    }

    public void cancel() {
        this.cancel(true);
        if (usuarioDao != null) {
            usuarioDao.setCancel(true);
        }
    }
}
