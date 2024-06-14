package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.BandeiraDao;
import br.com.jjconsulting.mobile.dansales.database.OrganizacaoDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.Bandeira;
import br.com.jjconsulting.mobile.dansales.model.Organizacao;
import br.com.jjconsulting.mobile.dansales.model.UnidadeNegocio;
import br.com.jjconsulting.mobile.dansales.model.Usuario;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.ClienteUtils;
import br.com.jjconsulting.mobile.dansales.util.PlanoCampoUtils;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;
import br.com.jjconsulting.mobile.jjlib.base.Tree;

public class AsyncTaskFilter extends AsyncTask<String, Void, Object[]> {

    public static final int ORGANIZACAO = 0;
    public static final int BANDEIRA = 1;
    public static final int STATUS = 2;
    public static final int HIERARQUIA = 3;
    public static final int PLANOCAMPO = 4;

    private Context context;
    private int type;
    private boolean isStartLoading;
    private List<Organizacao> organizacoes;
    private OnAsyncResponse onAsyncResponse;
    private UsuarioDao usuarioDao;

    public AsyncTaskFilter(Context context, boolean isStartLoading, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.isStartLoading = isStartLoading;
        this.onAsyncResponse = onAsyncResponse;
    }

    @Override
    protected Object[] doInBackground(String... params) {
        try {

            type = Integer.parseInt(params[0]);
            Object[] objects = null;

            switch (type) {
                case ORGANIZACAO:
                    Current current = Current.getInstance(context);
                    String codigoUsuario = String.valueOf(current.getUsuario().getCodigo());
                    String codigoUnidadeNegocio = current.getUnidadeNegocio().getCodigo();

                    OrganizacaoDao organizacaoDao = new OrganizacaoDao(context);
                    organizacoes = organizacaoDao.getAll(codigoUsuario, codigoUnidadeNegocio);

                    objects = SpinnerArrayAdapter.makeObjectsWithHint(organizacoes.toArray(),
                            context.getString(R.string.select_organizacao));
                    break;
                case BANDEIRA:
                    BandeiraDao bandeiraDao = new BandeiraDao(context.getApplicationContext());
                    List<Bandeira> bandeiras = bandeiraDao.getAll(params[1]);
                    objects = SpinnerArrayAdapter.makeObjectsWithHint(bandeiras.toArray(),
                            context.getApplicationContext().getString(R.string.select_bandeira));
                    break;
                case STATUS:
                    int[] statuses = ClienteUtils.getStatuses();
                    objects = SpinnerArrayAdapter.makeObjectsWithHint(statuses,
                            context.getString(R.string.select_status_cliente));
                    break;
                case HIERARQUIA:
                    Usuario usuario = Current.getInstance(context).getUsuario();
                    UnidadeNegocio unidadeNegocio = Current.getInstance(context).getUnidadeNegocio();

                    usuarioDao = new UsuarioDao(context);
                    Tree<Usuario> hierarquiaComercial = usuarioDao.getHierarquiaComercial(usuario,
                            unidadeNegocio.getCodigo());
                    objects = new Object[1];
                    objects[0] = hierarquiaComercial;
                    break;
                case PLANOCAMPO:
                    int[] planoCampo = PlanoCampoUtils.filter;
                    objects = SpinnerArrayAdapter.makeObjectsWithHint(planoCampo,
                            context.getString(R.string.select_plano_campo));
                    break;
            }

            return objects;

        }catch (Exception ex){
            return null;
        }
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
