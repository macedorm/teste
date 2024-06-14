package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import br.com.jjconsulting.mobile.dansales.R;
import br.com.jjconsulting.mobile.dansales.database.StatusETapDao;
import br.com.jjconsulting.mobile.dansales.database.UsuarioDao;
import br.com.jjconsulting.mobile.dansales.model.TapStatus;
import br.com.jjconsulting.mobile.jjlib.base.SpinnerArrayAdapter;

public class AsyncTaskListETapFilter extends AsyncTask<String, Void, Object[]> {

    public static final int STATUS = 0;
    public static final int HIERARQUIA = 1;

    private Context context;
    private int type;
    private boolean isStartLoading;
    private OnAsyncResponse onAsyncResponse;
    private UsuarioDao usuarioDao;

    public AsyncTaskListETapFilter(Context context, boolean isStartLoading, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.isStartLoading = isStartLoading;
        this.onAsyncResponse = onAsyncResponse;
    }

    @Override
    protected Object[] doInBackground(String... params) {
        type = Integer.parseInt(params[0]);
        Object[] objects = null;

        switch (type) {
            case STATUS:
                StatusETapDao statusETapDao = new StatusETapDao(context);
                ArrayList<TapStatus> tapStatuses = statusETapDao.getAll();
                objects = SpinnerArrayAdapter.makeObjectsWithHint(tapStatuses.toArray(),
                        context.getString(R.string.select_status_cliente));
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

}
