package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.BandeiraDao;
import br.com.jjconsulting.mobile.dansales.database.MultiValuesDao;
import br.com.jjconsulting.mobile.dansales.database.OrganizacaoDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.MultiValues;
import br.com.jjconsulting.mobile.dansales.model.TMultiValuesType;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;

public class AsyncConsultaGenericaFilter extends AsyncTask<String, Void, Object[]> {

    public static final int TIPO_CADASTRO = 0;
    public static final int STATUS = 1;
    public static final int HIERARQUIA = 2;

    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private boolean isStartLoading;
    private UsuarioDao usuarioDao;
    private int type;

    public AsyncConsultaGenericaFilter(Context context, boolean isStartLoading,
                                       OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.isStartLoading = isStartLoading;
        this.onAsyncResponse = onAsyncResponse;
    }

    @Override
    protected Object[] doInBackground(String... params) {
        type = Integer.parseInt(params[0]);
        Object[] objects = new Object[1];

        MultiValuesDao multiValuesDao = new MultiValuesDao(context);
        ArrayList<MultiValues> multi;

        switch (type) {
            case TIPO_CADASTRO:
                multi = multiValuesDao.getList(TMultiValuesType.CG_TIPO_CADASTRO);
                objects = SpinnerArrayAdapter.makeObjectsWithHint(multi.toArray(),
                        context.getApplicationContext().getString(R.string.select_tipo_cadastro));
                break;
            case STATUS:
                multi = multiValuesDao.getList(TMultiValuesType.CG_STATUS);
                objects = SpinnerArrayAdapter.makeObjectsWithHint(multi.toArray(),
                        context.getApplicationContext().getString(R.string.select_status));
                break;
            case HIERARQUIA:
                Usuario usuario = Current.getInstance(context).getUsuario();
                UnidadeNegocio unidadeNegocio = Current.getInstance(context).getUnidadeNegocio();

                usuarioDao = new UsuarioDao(context);
                Tree<Usuario> hierarquiaComercial = usuarioDao.getHierarquiaComercial(usuario,
                        unidadeNegocio.getCodigo());

                objects[0] = hierarquiaComercial;
                break;
        }

        return objects;

    }

    @Override
    protected void onPostExecute(Object[] objects) {
        onAsyncResponse.processFinish(type, isStartLoading, objects);
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
