package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import br.com.jjconsulting.mobile.dansales.data.TapFilter;
import br.com.jjconsulting.mobile.dansales.model.TapList;
import br.com.jjconsulting.mobile.dansales.service.Current;

public class AsyncTaskListETap extends AsyncTask<Void, Void, List<TapList>> {
    private Context context;
    private OnAsyncResponse onAsyncResponse;
    private TapFilter mTapFilter;
    private String mNome;
    private int index;


    public AsyncTaskListETap(Context context, int index, TapFilter mETap, String mNome, OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mTapFilter = mETap;
        this.mNome = mNome;
        this.index = index;
    }

    @Override
    protected List<TapList> doInBackground(Void... params) {
        List<TapList> mTapList;

        mTapList = new ArrayList<>();

        TapList tapListItem = new TapList();
        tapListItem.setId(1);
        tapListItem.setStatus(2);
        tapListItem.setCodigo("2000000012");
        tapListItem.setCliCod("0450045923");
        tapListItem.setCliNome("SUPERMERCADO MUNDIAL LTDA");
        tapListItem.setMasterContrato("TABLÓIDE");
        tapListItem.setAnoMesAcao("201806");
        tapListItem.setVlApur(2.000);
        tapListItem.setVlEst(1.000);

        mTapList.add(tapListItem);

        return mTapList;
    }

    @Override
    protected void onPostExecute(List<TapList> objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(List<TapList> objects);
    }
}
