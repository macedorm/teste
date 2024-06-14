package br.com.jjconsulting.mobile.dansales.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.jjconsulting.mobile.dansales.database.FamiliaDao;
import br.com.jjconsulting.mobile.dansales.model.ItensListSortimento;
import br.com.jjconsulting.mobile.dansales.model.PesquisaResposta;
import br.com.jjconsulting.mobile.dansales.model.Produto;
import br.com.jjconsulting.mobile.jjlib.util.TextUtils;

public class AsyncTaskItensPesquisaSortimento extends AsyncTask<Void, Void, HashMap<String, ArrayList<ItensListSortimento>>>  {
    private Context context;
    private OnAsyncResponse onAsyncResponse;


    private String mResposta;

    private PesquisaResposta mPesquisaResposta;

    private ArrayList<ItensListSortimento> mItensListSortimento;

    private boolean isPreco;

    public AsyncTaskItensPesquisaSortimento(Context context,
                                            String resposta, PesquisaResposta pesquisaResposta, ArrayList<ItensListSortimento> itensListSortimento, boolean isPreco,
                                            OnAsyncResponse onAsyncResponse) {
        this.context = context;
        this.onAsyncResponse = onAsyncResponse;
        this.mResposta = resposta;
        this.mPesquisaResposta = pesquisaResposta;
        this.mItensListSortimento = itensListSortimento;
        this.isPreco = isPreco;

    }

    @Override
    protected HashMap<String, ArrayList<ItensListSortimento>> doInBackground(Void... params) {
        FamiliaDao familiaDao = new FamiliaDao(context);

        HashMap<String, ArrayList<ItensListSortimento>> expandableListDetail = new HashMap<>();

        ArrayList<ItensListSortimento> itensListSortimentoMarca;

        String arraySKUSelected[] = null;

        if(!TextUtils.isNullOrEmpty(mResposta)){
            arraySKUSelected = mResposta.split("\\|");
        }

        boolean isContainResposta = false;

        if(mPesquisaResposta != null && !
                TextUtils.isNullOrEmpty(mPesquisaResposta.getResposta())){
            isContainResposta = true;
        }

        if(mItensListSortimento != null) {
            for (ItensListSortimento item : mItensListSortimento) {

                boolean isNotIgnore = true;

                if(isContainResposta){
                    String ids[] = mPesquisaResposta.getResposta().split("\\|");

                    if(isPreco){
                        isNotIgnore = false;

                        for(int ind = 0; ind < ids.length;ind++){
                            if(ids[ind].equals(item.getSKU())) {
                                isNotIgnore = true;
                            }
                        }
                    }
                }

                if(isNotIgnore) {

                    if (arraySKUSelected != null) {
                        for (String SKU : arraySKUSelected) {
                            if(!isPreco){
                                if (item.getSKU().equals(SKU)) {
                                    item.setSelected(true);
                                }
                            } else{
                                if(SKU.contains(item.getSKU())){
                                    String info [] = SKU.split("#");
                                    item.setSelected(info[4].equals("1"));
                                    item.setPrecoUser(info[3]);
                                }
                            }
                        }
                    } else {
                        item.setSelected(false);
                    }

                    itensListSortimentoMarca = new ArrayList<>();
                    itensListSortimentoMarca.add(item);

                    String currentMarca = familiaDao.getFamilia(item.getMarca());

                    if(expandableListDetail.size() == 0){
                        expandableListDetail.put(currentMarca, itensListSortimentoMarca);
                    } else  {
                        if(expandableListDetail.containsKey(currentMarca)){
                            itensListSortimentoMarca = expandableListDetail.get(currentMarca);
                            itensListSortimentoMarca.add(item);
                        }
                        expandableListDetail.put(currentMarca, itensListSortimentoMarca);
                    }
                }
            }
        }

        return expandableListDetail;
    }

    @Override
    protected void onPostExecute(HashMap<String, ArrayList<ItensListSortimento>>  objects) {
        onAsyncResponse.processFinish(objects);
    }

    public interface OnAsyncResponse {
        void processFinish(HashMap<String, ArrayList<ItensListSortimento>>  objects);
    }
}
