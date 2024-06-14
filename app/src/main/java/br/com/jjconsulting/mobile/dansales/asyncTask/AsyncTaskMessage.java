package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.ClienteFilter;
import br.com.jjconsulting.mobile.dansales.data.MessageFilter;
import br.com.jjconsulting.mobile.dansales.database.ClienteDao;
import br.com.jjconsulting.mobile.dansales.database.MessageDao;
import br.com.jjconsulting.mobile.dansales.model.Cliente;
import br.com.jjconsulting.mobile.dansales.model.Message;
import br.com.jjconsulting.mobile.dansales.service.Current;
import br.com.jjconsulting.mobile.dansales.util.TMessageType;

public class AsyncTaskMessage extends AsyncTask<Void, Void, List<Message>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private MessageFilter mMessageFilter;
    private String mNome;
    private MessageDao mMessageDao;
    private int index;

    public AsyncTaskMessage(Context context, int index, MessageFilter messageFilter, String mNome, MessageDao mMessageDao, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mMessageFilter = messageFilter;
        this.mNome = mNome;
        this.mMessageDao = mMessageDao;
        this.index = index;
    }

    @Override
    protected List<Message> doInBackground(Void... params) {

        Current current = Current.getInstance(context);
        String unCod = current.getUnidadeNegocio().getCodigo();

        List<Message> messages;
        messages = mMessageDao.getMessagesVigenciaExt(current.getUsuario(), unCod, new Date(), mNome, mMessageFilter, index);

        return messages;
    }

    @Override
    protected void onPostExecute(List<Message> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<Message> objects);
    }
}
